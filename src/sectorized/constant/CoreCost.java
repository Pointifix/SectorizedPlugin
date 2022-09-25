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

    public static final ItemSeq[] requirementsSerpulo = new ItemSeq[size];
    public static final ItemSeq[] requirementsErekir = new ItemSeq[size];

    static {
        for (int i = 0; i < size; i++) {
            ItemSeq itemSeq = new ItemSeq();

            itemSeq.add(Items.copper, 200 + i * 100);
            itemSeq.add(Items.lead, 100 + i * 70);
            if (i >= 1) itemSeq.add(Items.graphite, 50 + (i - 1) * 20);
            if (i >= 2) itemSeq.add(Items.silicon, 70 + (i - 2) * 50);
            if (i >= 3) itemSeq.add(Items.metaglass, 50 + (i - 3) * 30);
            if (i >= 5) itemSeq.add(Items.titanium, 200 + (i - 5) * 40);
            if (i >= 6) itemSeq.add(Items.thorium, 100 + (i - 6) * 40);
            if (i >= 8) itemSeq.add(Items.plastanium, 50 + (i - 8) * 30);
            if (i >= 11) itemSeq.add(Items.phaseFabric, 20 + (i - 11) * 20);
            if (i >= 15) itemSeq.add(Items.surgeAlloy, 30 + (i - 15) * 30);

            requirementsSerpulo[i] = itemSeq;
        }

        for (int i = 0; i < size; i++) {
            ItemSeq itemSeq = new ItemSeq();

            itemSeq.add(Items.beryllium, 50 + i * 50);
            if (i >= 1) itemSeq.add(Items.graphite, 20 + (i - 1) * 20);
            if (i >= 3) itemSeq.add(Items.silicon, 50 + (i - 3) * 30);
            if (i >= 5) itemSeq.add(Items.oxide, 10 + (i - 5) * 20);
            if (i >= 7) itemSeq.add(Items.carbide, 10 + (i - 7) * 10);

            requirementsErekir[i] = itemSeq;
        }
    }

    public static boolean checkAndConsumeFunds(Team team) {
        int core = team.cores().size - 1;

        ItemSeq requirement = State.planet.equals(Planets.serpulo.name) ? requirementsSerpulo[Math.max(Math.min(core, size - 1), 0)] : requirementsErekir[Math.max(Math.min(core, size - 1), 0)];

        if (team.core().items().has(requirement)) {
            team.core().items().remove(requirement);
            return true;
        }

        return false;
    }
}
