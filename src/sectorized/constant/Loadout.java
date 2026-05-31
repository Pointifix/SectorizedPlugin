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
                    Items.copper, Config.c.loadout.serpulo.copper.base + (Config.c.loadout.serpulo.copper.perWave * wave),
                    Items.lead, Config.c.loadout.serpulo.lead.base + (Config.c.loadout.serpulo.lead.perWave * wave),
                    Items.graphite, Config.c.loadout.serpulo.graphite.base + (Config.c.loadout.serpulo.graphite.perWave * wave),
                    Items.silicon, Config.c.loadout.serpulo.silicon.base + (Config.c.loadout.serpulo.silicon.perWave * wave),
                    Items.metaglass, Config.c.loadout.serpulo.metaglass.base + (Config.c.loadout.serpulo.metaglass.perWave * wave),
                    Items.titanium, Config.c.loadout.serpulo.titanium.base + (Config.c.loadout.serpulo.titanium.perWave * wave),
                    Items.thorium, Config.c.loadout.serpulo.thorium.base + (Config.c.loadout.serpulo.thorium.perWave * wave));

            if (wave >= Config.c.loadout.serpulo.plastanium.waveRequired) loadout.add(new ItemStack(Items.plastanium, Config.c.loadout.serpulo.plastanium.perWave * (wave - Config.c.loadout.serpulo.plastanium.waveOffset)));
            if (wave >= Config.c.loadout.serpulo.phaseFabric.waveRequired) loadout.add(new ItemStack(Items.phaseFabric, Config.c.loadout.serpulo.phaseFabric.perWave * (wave - Config.c.loadout.serpulo.phaseFabric.waveOffset)));
            if (wave >= Config.c.loadout.serpulo.surgeAlloy.waveRequired) loadout.add(new ItemStack(Items.surgeAlloy, Config.c.loadout.serpulo.surgeAlloy.perWave * (wave - Config.c.loadout.serpulo.surgeAlloy.waveOffset)));

            return loadout;
        } else if (State.planet.equals(Planets.erekir.name)) {
            Seq<ItemStack> loadout = ItemStack.list(
                    Items.beryllium, Config.c.loadout.erekir.beryllium.base + (Config.c.loadout.erekir.beryllium.perWave * wave),
                    Items.graphite, Config.c.loadout.erekir.graphite.base + (Config.c.loadout.erekir.graphite.perWave * wave),
                    Items.silicon, Config.c.loadout.erekir.silicon.base + (Config.c.loadout.erekir.silicon.perWave * wave),
                    Items.thorium, Config.c.loadout.erekir.thorium.base + (Config.c.loadout.erekir.thorium.perWave * wave));

            if (wave >= Config.c.loadout.erekir.tungsten.waveRequired) loadout.add(new ItemStack(Items.tungsten, Config.c.loadout.erekir.tungsten.perWave * (wave - Config.c.loadout.erekir.tungsten.waveOffset)));
            if (wave >= Config.c.loadout.erekir.oxide.waveRequired) loadout.add(new ItemStack(Items.oxide, Config.c.loadout.erekir.oxide.perWave * (wave - Config.c.loadout.erekir.oxide.waveOffset)));
            if (wave >= Config.c.loadout.erekir.carbide.waveRequired) loadout.add(new ItemStack(Items.carbide, Config.c.loadout.erekir.carbide.perWave * (wave - Config.c.loadout.erekir.carbide.waveOffset)));
            if (wave >= Config.c.loadout.erekir.surgeAlloy.waveRequired) loadout.add(new ItemStack(Items.surgeAlloy, Config.c.loadout.erekir.surgeAlloy.perWave * (wave - Config.c.loadout.erekir.surgeAlloy.waveOffset)));
            if (wave >= Config.c.loadout.erekir.phaseFabric.waveRequired) loadout.add(new ItemStack(Items.phaseFabric, Config.c.loadout.erekir.phaseFabric.perWave * (wave - Config.c.loadout.erekir.phaseFabric.waveOffset)));

            return loadout;
        }

        return new Seq<>();
    }
}
