package sectorized.update;

import arc.Events;
import arc.util.Timer;
import arc.util.*;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.BlockUnitUnit;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.Packets;
import mindustry.type.ItemStack;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.CoreCost;
import sectorized.constant.Loadout;
import sectorized.constant.MessageUtils;
import sectorized.constant.State;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.netServer;
import static mindustry.Vars.state;

public class UpdateManager implements Manager {
    private final Interval interval = new Interval(3);

    private final HashSet<String> hideHud = new HashSet<>();

    private int infoMessageIndex = 0;

    @Override
    public void init() {
        Events.run(EventType.Trigger.update, () -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            State.time += Time.delta;

            if (interval.get(0, 60 * 5)) {
                double blockDamageMultiplier = Math.round(state.rules.blockDamageMultiplier * 10.0) / 10.0;
                double unitDamageMultiplier = Math.round(state.rules.unitDamageMultiplier * 10.0) / 10.0;

                Groups.player.each(player -> !hideHud.contains(player.uuid()), player -> {
                    int cores = player.team().cores().size - 1;

                    StringBuilder infoPopupText = new StringBuilder("Costs for next \uF869\n");

                    for (ItemStack itemStack : CoreCost.requirements[Math.max(Math.min(cores, CoreCost.requirements.length - 1), 0)]) {
                        int availableItems = player.team().items().get(itemStack.item);

                        infoPopupText
                                .append(CoreCost.itemUnicodes.get(itemStack.item))
                                .append(availableItems >= itemStack.amount ? itemStack.amount + "[green]\uE800[white]" : "[red]" + availableItems + "[white]/" + itemStack.amount)
                                .append("\n");
                    }

                    infoPopupText.append("\nDamage Multipliers: ")
                            .append("\n")
                            .append("[white]\uF856 [magenta]")
                            .append(blockDamageMultiplier)
                            .append(" [white]\uF7F4 [magenta]")
                            .append(unitDamageMultiplier)
                            .append("[white]\n");

                    if (State.gameState == State.GameState.LOCKED) {
                        infoPopupText.append("\n(Re)spawning [purple]locked[white]\n");
                    }

                    infoPopupText.append("\nToggle with [teal]/hud [white]");

                    Call.infoPopup(player.con, infoPopupText.toString(), 5.01f, Align.topLeft, 90, 5, 0, 0);
                });
            }

            if (interval.get(1, 60 * 60 * 8)) {
                switch (infoMessageIndex) {
                    case 0:
                        MessageUtils.sendMessage("Type [teal]/info" + MessageUtils.defaultColor + " for information about how to play.\n" +
                                "You can request to join another team using [teal]/join" + MessageUtils.defaultColor + "\n" +
                                "Join the discord and be part of the community! \n" +
                                "[blue]\uE80D" + MessageUtils.defaultColor + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                    case 1:
                        MessageUtils.sendMessage("Type [teal]/score" + MessageUtils.defaultColor + " or [teal]/leaderboard" + MessageUtils.defaultColor + " to display your rank!\n" +
                                "[blue]\uE80D" + MessageUtils.defaultColor + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                    case 2:
                        MessageUtils.sendMessage("If you are dominating the game end it as soon as possible, others want to play as well!\n" +
                                "Also, stalling does not increase your score! \n" +
                                "[blue]\uE80D" + MessageUtils.defaultColor + " https://discord.gg/AmdMXKkS9Q", MessageUtils.MessageLevel.INFO);
                        break;
                }

                infoMessageIndex = (infoMessageIndex + 1) % 3;
            }

            if (interval.get(2, 60 * 60)) {
                if (Groups.player.size() < 2) return;

                Team dominatingTeam = null;
                boolean lock = false;

                Team[] teams = Team.all.clone();
                Arrays.sort(teams, Comparator.comparingInt(t -> -t.cores().size));
                if (teams[0].cores().size >= teams[1].cores().size + 4 + (state.wave * 0.1)) {
                    dominatingTeam = teams[0];
                    lock = true;
                }

                if (!lock) {
                    HashMap<Team, Float> healthPerTeam = new HashMap<>();

                    Groups.unit.each(u -> u.team != Team.crux &&
                                    u.type != UnitTypes.mono &&
                                    u.type != UnitTypes.poly
                            , u -> {
                                if (u.team != Team.crux)
                                    healthPerTeam.put(u.team, healthPerTeam.getOrDefault(u.team, 0.0f) + u.health());
                            });

                    List<Map.Entry<Team, Float>> healthPerTeamEntries = new ArrayList<>(healthPerTeam.entrySet());
                    healthPerTeamEntries.sort((Map.Entry<Team, Float> a, Map.Entry<Team, Float> b) -> (int) (b.getValue() - a.getValue()));

                    if (healthPerTeamEntries.size() > 1 && healthPerTeamEntries.get(0).getValue() >= healthPerTeamEntries.get(1).getValue() + 10000 + 1000 * state.wave) {
                        dominatingTeam = healthPerTeamEntries.get(0).getKey();
                        lock = true;
                    }
                }

                if (State.gameState == State.GameState.ACTIVE && lock) {
                    State.gameState = State.GameState.LOCKED;
                    Events.fire(new SectorizedEvents.TeamDominatingEvent(dominatingTeam));
                } else if (State.gameState == State.GameState.LOCKED && !lock) {
                    State.gameState = State.GameState.ACTIVE;
                    Events.fire(new SectorizedEvents.NoTeamDominatingEvent());
                }
            }
        });

        Events.on(SectorizedEvents.GamemodeStartEvent.class, event -> {
            setServerDescription();
        });

        Events.on(EventType.WaveEvent.class, event -> {
            state.rules.loadout = Loadout.getLoadout(state.wave);

            state.rules.blockDamageMultiplier = (float) (2 / (Math.pow(state.wave * 0.05, 2) + 1));
            state.rules.unitDamageMultiplier = (float) (2.5 - (2 / (Math.pow(state.wave * 0.05, 2) + 1)));

            setServerDescription();
        });

        Events.on(SectorizedEvents.RestartEvent.class, event -> {
            Log.info("Restarting: " + event.reason);
            MessageUtils.sendMessage(event.reason + "\nServer is restarting in [gold]15" + MessageUtils.defaultColor + " seconds", MessageUtils.MessageLevel.INFO);

            final int seconds = 10;
            AtomicInteger countdown = new AtomicInteger(seconds);
            Timer.schedule(() -> {
                if (countdown.get() == 0) {
                    Log.info("Restarting server ...");
                    netServer.kickAll(Packets.KickReason.serverRestarting);
                    Events.fire(new SectorizedEvents.ShutdownEvent());
                    System.exit(1);
                }

                MessageUtils.sendMessage("Server is restarting in [gold]" + (countdown.getAndDecrement()) + MessageUtils.defaultColor + " seconds.", MessageUtils.MessageLevel.INFO);
            }, 5, 1, seconds);
        });

        netServer.admins.addActionFilter((action) -> {
            if (State.gameState == State.GameState.INACTIVE) return true;

            switch (action.type) {
                case command:
                    return false;
                case control:
                    if (!(action.unit instanceof BlockUnitUnit)) return false;
                    break;
            }

            return true;
        });
    }

    @Override
    public void reset() {
        State.time = 0;

        interval.clear();
        hideHud.clear();
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {

    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("hud", "Toggles the visibility of the hud.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            String uuid = player.uuid();
            if (hideHud.contains(uuid)) {
                hideHud.remove(uuid);
                MessageUtils.sendMessage(player, "Hud will be shown again shortly!", MessageUtils.MessageLevel.INFO);
            } else {
                hideHud.add(uuid);
                MessageUtils.sendMessage(player, "Hud will be hidden shortly!", MessageUtils.MessageLevel.INFO);
            }
        });

        handler.<Player>register("time", "Display elapsed time.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            int hour = (int) (State.time / 60 / 60 / 60);
            int min = (int) (State.time / 60 / 60 % 60);
            int sec = (int) (State.time / 60 % 60);

            MessageUtils.sendMessage(player, "Elapsed time: " +
                    (hour > 0 ? hour + "h " : "") +
                    (min > 0 ? min + "m " : "") +
                    sec + "s", MessageUtils.MessageLevel.INFO);
        });

        handler.<Player>register("info", "Display information about the gamemode.", (args, player) -> {
            if (State.gameState == State.GameState.INACTIVE) return;

            MessageUtils.sendWelcomeMessage(player);
        });
    }

    private void setServerDescription() {
        if (state.wave < 2) {
            Administration.Config.desc.set("[white]Wave [red]" + state.wave + "[gray] |[white] Time elapsed: [green]Just started!");
        } else {
            int hour = (int) (State.time / 60 / 60 / 60);
            int min = (int) (State.time / 60 / 60 % 60);

            Administration.Config.desc.set("[white]Wave [red]" + state.wave + "[gray] |[white] Time elapsed: [goldenrod]" + (hour > 0 ? hour + "h " : "") +
                    min + "m" + (State.gameState == State.GameState.LOCKED ? "[gray] | [purple]LOCKED" : ""));
        }
    }
}
