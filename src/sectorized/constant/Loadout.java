package sectorized.constant;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.ItemStack;

public class Loadout {
    public static Seq<ItemStack> getLoadout(int wave) {
        wave--;

        if (State.planet.equals(Planets.serpulo.name)) {
            Seq<ItemStack> loadout = ItemStack.list(
                    Items.copper, 800 + (150 * wave),
                    Items.lead, 500 + (100 * wave),
                    Items.graphite, 150 + (20 * wave),
                    Items.silicon, 150 + (30 * wave),
                    Items.metaglass, 100 + (10 * wave),
                    Items.titanium, 50 + (20 * wave),
                    Items.thorium, 10 + (15 * wave));

            if (wave >= 5) loadout.add(new ItemStack(Items.plastanium, 20 * (wave - 4)));
            if (wave >= 10) loadout.add(new ItemStack(Items.phaseFabric, 15 * (wave - 9)));
            if (wave >= 10) loadout.add(new ItemStack(Items.surgeAlloy, 15 * (wave - 9)));

            return loadout;
        } else if (State.planet.equals(Planets.erekir.name)) {
            Seq<ItemStack> loadout = ItemStack.list(
                    Items.beryllium, 300 + (100 * wave),
                    Items.graphite, 100 + (50 * wave),
                    Items.silicon, 50 + (30 * wave),
                    Items.thorium, 150 + (20 * wave));

            if (wave >= 1) loadout.add(new ItemStack(Items.tungsten, 30 * (wave)));
            if (wave >= 3) loadout.add(new ItemStack(Items.oxide, 20 * (wave - 2)));
            if (wave >= 5) loadout.add(new ItemStack(Items.carbide, 10 * (wave - 4)));
            if (wave >= 7) loadout.add(new ItemStack(Items.surgeAlloy, 20 * (wave - 7)));
            if (wave >= 9) loadout.add(new ItemStack(Items.phaseFabric, 10 * (wave - 4)));

            return loadout;
        }

        return new Seq<>();
    }
}
