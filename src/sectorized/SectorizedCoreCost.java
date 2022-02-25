package sectorized;

import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;

import java.util.HashMap;

public class SectorizedCoreCost {
    private static final HashMap<Item, String> itemUnicodes = new HashMap<Item, String>() {{
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
    }};

    private static final int size = 50;

    private static final ItemSeq[] requirements = new ItemSeq[size];

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
            if (i >= 10) itemSeq.add(Items.phaseFabric, 50 + (i - 10) * 50);
            if (i >= 10) itemSeq.add(Items.surgeAlloy, 50 + (i - 10) * 50);

            requirements[i] = itemSeq;
        }
    }

    public static boolean consumeIfHas(Team team) {
        int core = SectorizedTeamManager.getTeam(team).cores - 1;

        ItemSeq requirement = requirements[Math.max(Math.min(core, size - 1), 0)];

        if (team.core().items().has(requirement)) {
            team.core().items().remove(requirement);
            return true;
        }

        return false;
    }

    public static String getRequirementsText(Team team) {
        int cores = SectorizedTeamManager.getTeam(team).cores - 1;

        String requirementsText = "Requirements for next Core: \n";

        for (ItemStack itemStack : requirements[cores]) {
            int availableItems = team.items().get(itemStack.item);

            requirementsText += itemUnicodes.get(itemStack.item) + (availableItems >= itemStack.amount ? itemStack.amount + "[green]\uE800[white]" : "[red]" + availableItems + "[white]/" + itemStack.amount) + "\n";
        }

        return requirementsText;
    }
}
