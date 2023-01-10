package sectorized.world;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.struct.StringMap;
import arc.util.CommandHandler;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.maps.Map;
import sectorized.Manager;
import sectorized.SectorizedEvents;
import sectorized.constant.Constants;
import sectorized.constant.DiscordBot;
import sectorized.constant.Loadout;
import sectorized.constant.State;
import sectorized.world.map.Biomes;
import sectorized.world.map.MapGenerator;

import static mindustry.Vars.*;

public class WorldManager implements Manager {
    @Override
    public void init() {
        Events.on(SectorizedEvents.GamemodeStartEvent.class, event -> {
            String biomeVoteString = (String) Core.settings.get("biomeVote", "");
            Biomes.Biome biomeVote = Biomes.all.stream().filter(biome -> biome.toString().equals(biomeVoteString)).findFirst().orElse(null);
            Core.settings.put("biomeVote", "");

            final String planet = biomeVote != null ? biomeVote.getPlanet() : Mathf.chance(0.7) ? Planets.serpulo.name : Planets.erekir.name;
            State.planet = planet;
            state.rules.loadout = Loadout.getLoadout(1);

            if (maps.customMaps().isEmpty()) {
                System.out.println("No Map exists, generating ...");

                MapGenerator mapGenerator = new MapGenerator(biomeVote);
                world.loadGenerator(Constants.mapWidth, Constants.mapHeight, mapGenerator);
                state.map = new Map(StringMap.of("name", Core.settings.get("mostFrequentBiomes", "")));
                Events.fire(new SectorizedEvents.BiomesGeneratedEvent());
            } else {
                System.out.println("Map exists, loading ...");
                Map map = maps.customMaps().peek();

                world.loadMap(map);
                world.tile(2, 2).setBlock(Blocks.air);
                Events.fire(new SectorizedEvents.BiomesGeneratedEvent());
                state.map = new Map(StringMap.of("name", Core.settings.get("mostFrequentBiomes", "")));
                //map.file.delete();
            }

            DiscordBot.sendMessage("**Server started!** Current map: " + Core.settings.get("mostFrequentBiomes", ""));

            if (State.planet.equals(Planets.serpulo.name)) {
                state.rules.env = Planets.serpulo.defaultEnv;
                state.rules.hiddenBuildItems.clear();
                state.rules.hiddenBuildItems.addAll(Planets.serpulo.hiddenItems);
            } else if (State.planet.equals(Planets.erekir.name)) {
                state.rules.env = Planets.erekir.defaultEnv;
                state.rules.hiddenBuildItems.clear();
                state.rules.hiddenBuildItems.addAll(Planets.erekir.hiddenItems);
            }
        });

        Events.on(SectorizedEvents.BiomeVoteFinishedEvent.class, event -> {
            System.out.println("Pregenerating map ...");

            MapGenerator mapGenerator = new MapGenerator(null);
            world.loadGenerator(Constants.mapWidth, Constants.mapHeight, mapGenerator);
            world.tile(2, 2).setBlock(Blocks.coreShard, Team.sharded);
            state.map = new Map(StringMap.of("name", "sectorized"));
            maps.saveMap(state.map.tags);
        });
    }

    @Override
    public void reset() {

    }

    @Override
    public void registerServerCommands(CommandHandler handler) {

    }

    @Override
    public void registerClientCommands(CommandHandler handler) {

    }
}
