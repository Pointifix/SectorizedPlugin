package sectorized.constant;

import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.type.ItemSeq;

import java.util.HashMap;

public class CoreCost {
    private static final int size = Config.c.game.coreTierSize;
    private static final int maxTeamSize = Config.c.game.maxTeamSize;

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

    public static final ItemSeq[][] requirementsSerpulo = new ItemSeq[size][maxTeamSize];
    public static final ItemSeq[][] requirementsErekir = new ItemSeq[size][maxTeamSize];

    static {
        double factor = 0;

        for (int teamSize = 0; teamSize < maxTeamSize; teamSize++) {
            factor += 1d / Math.pow(2, (Math.max(teamSize - 1, 0)));

            for (int i = 0; i < size; i++) {
                ItemSeq itemSeq = new ItemSeq();

                itemSeq.add(Items.copper, (int) ((Config.c.coreCost.serpulo.copper.base + i * Config.c.coreCost.serpulo.copper.perTier) * factor));
                itemSeq.add(Items.lead, (int) ((Config.c.coreCost.serpulo.lead.base + i * Config.c.coreCost.serpulo.lead.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.graphite.minTier) itemSeq.add(Items.graphite, (int) ((Config.c.coreCost.serpulo.graphite.base + (i - 1) * Config.c.coreCost.serpulo.graphite.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.silicon.minTier) itemSeq.add(Items.silicon, (int) ((Config.c.coreCost.serpulo.silicon.base + (i - 2) * Config.c.coreCost.serpulo.silicon.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.metaglass.minTier) itemSeq.add(Items.metaglass, (int) ((Config.c.coreCost.serpulo.metaglass.base + (i - 3) * Config.c.coreCost.serpulo.metaglass.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.titanium.minTier) itemSeq.add(Items.titanium, (int) ((Config.c.coreCost.serpulo.titanium.base + (i - 5) * Config.c.coreCost.serpulo.titanium.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.thorium.minTier) itemSeq.add(Items.thorium, (int) ((Config.c.coreCost.serpulo.thorium.base + (i - 6) * Config.c.coreCost.serpulo.thorium.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.plastanium.minTier) itemSeq.add(Items.plastanium, (int) ((Config.c.coreCost.serpulo.plastanium.base + (i - 8) * Config.c.coreCost.serpulo.plastanium.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.phaseFabric.minTier) itemSeq.add(Items.phaseFabric, (int) ((Config.c.coreCost.serpulo.phaseFabric.base + (i - 11) * Config.c.coreCost.serpulo.phaseFabric.perTier) * factor));
                if (i >= Config.c.coreCost.serpulo.surgeAlloy.minTier) itemSeq.add(Items.surgeAlloy, (int) ((Config.c.coreCost.serpulo.surgeAlloy.base + (i - 15) * Config.c.coreCost.serpulo.surgeAlloy.perTier) * factor));

                requirementsSerpulo[i][teamSize] = itemSeq;
            }

            for (int i = 0; i < size; i++) {
                ItemSeq itemSeq = new ItemSeq();

                itemSeq.add(Items.beryllium, (int) ((Config.c.coreCost.erekir.beryllium.base + i * Config.c.coreCost.erekir.beryllium.perTier) * factor));
                if (i >= Config.c.coreCost.erekir.graphite.minTier) itemSeq.add(Items.graphite, (int) ((Config.c.coreCost.erekir.graphite.base + (i - 1) * Config.c.coreCost.erekir.graphite.perTier) * factor));
                if (i >= Config.c.coreCost.erekir.silicon.minTier) itemSeq.add(Items.silicon, (int) ((Config.c.coreCost.erekir.silicon.base + (i - 3) * Config.c.coreCost.erekir.silicon.perTier) * factor));
                if (i >= Config.c.coreCost.erekir.oxide.minTier) itemSeq.add(Items.oxide, (int) ((Config.c.coreCost.erekir.oxide.base + (i - 5) * Config.c.coreCost.erekir.oxide.perTier) * factor));
                if (i >= Config.c.coreCost.erekir.carbide.minTier) itemSeq.add(Items.carbide, (int) ((Config.c.coreCost.erekir.carbide.base + (i - 7) * Config.c.coreCost.erekir.carbide.perTier) * factor));

                requirementsErekir[i][teamSize] = itemSeq;
            }
        }
    }

    public static boolean checkAndConsumeFunds(Team team) {
        int core = Math.max(Math.min(team.cores().size - 1, size - 1), 0);
        int size = Math.max(Math.min(team.data().players.size - 1, maxTeamSize - 1), 0);

        ItemSeq requirement = State.planet.equals(Planets.serpulo.name) ? requirementsSerpulo[core][size] : requirementsErekir[core][size];

        if (team.core().items.has(requirement)) {
            team.core().items.remove(requirement);
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
