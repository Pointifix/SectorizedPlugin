package sectorized.constant;

import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.type.ItemSeq;

import java.util.HashMap;

public class CoreCost {
    public static final HashMap<Item, String> itemUnicodes = new HashMap() {{
        put(Items.copper, "\uF838");
        put(Items.lead, "\uF837");
        put(Items.graphite, "\uF835");
        put(Items.silicon, "\uF82F");
        put(Items.metaglass, "\uF836");
        put(Items.titanium, "\uF832");
        put(Items.thorium, "\uF831");
        put(Items.plastanium, "\uF82E");
        put(Items.phaseFabric, "\uF82D");
        put(Items.surgeAlloy, "\uF82C");
        put(Items.beryllium, "\uF748");
        put(Items.tungsten, "\uF739");
        put(Items.oxide, "\uF721");
        put(Items.carbide, "\uF736");
    }};

    private static final int size = 25;
    private static final int maxTeamSize = 4;

    public static final ItemSeq[][] requirementsSerpulo = new ItemSeq[size][maxTeamSize];
    public static final ItemSeq[][] requirementsErekir = new ItemSeq[size][maxTeamSize];

    static {
        double factor = 0;

        for (int teamSize = 0; teamSize < maxTeamSize; teamSize++) {
            factor += 1d / Math.pow(2, (Math.max(teamSize - 1, 0)));

            for (int i = 0; i < size; i++) {
                ItemSeq itemSeq = new ItemSeq();

                itemSeq.add(Items.copper, (int) ((200 + i * 100) * factor));
                itemSeq.add(Items.lead, (int) ((100 + i * 70) * factor));
                if (i >= 1) itemSeq.add(Items.graphite, (int) ((50 + (i - 1) * 20) * factor));
                if (i >= 2) itemSeq.add(Items.silicon, (int) ((70 + (i - 2) * 50) * factor));
                if (i >= 3) itemSeq.add(Items.metaglass, (int) ((50 + (i - 3) * 30) * factor));
                if (i >= 5) itemSeq.add(Items.titanium, (int) ((200 + (i - 5) * 40) * factor));
                if (i >= 6) itemSeq.add(Items.thorium, (int) ((100 + (i - 6) * 40) * factor));
                if (i >= 8) itemSeq.add(Items.plastanium, (int) ((50 + (i - 8) * 30) * factor));
                if (i >= 11) itemSeq.add(Items.phaseFabric, (int) ((20 + (i - 11) * 20) * factor));
                if (i >= 15) itemSeq.add(Items.surgeAlloy, (int) ((30 + (i - 15) * 30) * factor));

                requirementsSerpulo[i][teamSize] = itemSeq;
            }

            for (int i = 0; i < size; i++) {
                ItemSeq itemSeq = new ItemSeq();

                itemSeq.add(Items.beryllium, (int) ((50 + i * 50) * factor));
                if (i >= 1) itemSeq.add(Items.graphite, (int) ((20 + (i - 1) * 20) * factor));
                if (i >= 3) itemSeq.add(Items.silicon, (int) ((50 + (i - 3) * 30) * factor));
                if (i >= 5) itemSeq.add(Items.oxide, (int) ((10 + (i - 5) * 20) * factor));
                if (i >= 7) itemSeq.add(Items.carbide, (int) ((10 + (i - 7) * 10) * factor));

                requirementsErekir[i][teamSize] = itemSeq;
            }
        }
    }

    public static boolean checkAndConsumeFunds(Team team) {
        int core = Math.max(Math.min(team.cores().size - 1, size - 1), 0);
        int size = Math.max(Math.min(team.data().players.size - 1, maxTeamSize - 1), 0);

        ItemSeq requirement = State.planet.equals(Planets.serpulo.name) ? requirementsSerpulo[core][size] : requirementsErekir[core][size];

        if (team.core().items().has(requirement)) {
            team.core().items().remove(requirement);
            return true;
        }

        return false;
    }

    public static ItemSeq getRequirements(Team team) {
        int core = Math.max(Math.min(team.cores().size - 1, size - 1), 0);
        int size = Math.max(Math.min(team.data().players.size - 1, maxTeamSize - 1), 0);

        return State.planet.equals(Planets.serpulo.name) ? requirementsSerpulo[core][size] : requirementsErekir[core][size];
    }
}
