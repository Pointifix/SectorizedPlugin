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
                    Items.copper, Config.c.getInt("loadout.serpulo.copper.base") + (Config.c.getInt("loadout.serpulo.copper.perWave") * wave),
                    Items.lead, Config.c.getInt("loadout.serpulo.lead.base") + (Config.c.getInt("loadout.serpulo.lead.perWave") * wave),
                    Items.graphite, Config.c.getInt("loadout.serpulo.graphite.base") + (Config.c.getInt("loadout.serpulo.graphite.perWave") * wave),
                    Items.silicon, Config.c.getInt("loadout.serpulo.silicon.base") + (Config.c.getInt("loadout.serpulo.silicon.perWave") * wave),
                    Items.metaglass, Config.c.getInt("loadout.serpulo.metaglass.base") + (Config.c.getInt("loadout.serpulo.metaglass.perWave") * wave),
                    Items.titanium, Config.c.getInt("loadout.serpulo.titanium.base") + (Config.c.getInt("loadout.serpulo.titanium.perWave") * wave),
                    Items.thorium, Config.c.getInt("loadout.serpulo.thorium.base") + (Config.c.getInt("loadout.serpulo.thorium.perWave") * wave));

            if (wave >= Config.c.getInt("loadout.serpulo.plastanium.waveRequired")) loadout.add(new ItemStack(Items.plastanium, Config.c.getInt("loadout.serpulo.plastanium.perWave") * (wave - Config.c.getInt("loadout.serpulo.plastanium.waveOffset"))));
            if (wave >= Config.c.getInt("loadout.serpulo.phaseFabric.waveRequired")) loadout.add(new ItemStack(Items.phaseFabric, Config.c.getInt("loadout.serpulo.phaseFabric.perWave") * (wave - Config.c.getInt("loadout.serpulo.phaseFabric.waveOffset"))));
            if (wave >= Config.c.getInt("loadout.serpulo.surgeAlloy.waveRequired")) loadout.add(new ItemStack(Items.surgeAlloy, Config.c.getInt("loadout.serpulo.surgeAlloy.perWave") * (wave - Config.c.getInt("loadout.serpulo.surgeAlloy.waveOffset"))));

            return loadout;
        } else if (State.planet.equals(Planets.erekir.name)) {
            Seq<ItemStack> loadout = ItemStack.list(
                    Items.beryllium, Config.c.getInt("loadout.erekir.beryllium.base") + (Config.c.getInt("loadout.erekir.beryllium.perWave") * wave),
                    Items.graphite, Config.c.getInt("loadout.erekir.graphite.base") + (Config.c.getInt("loadout.erekir.graphite.perWave") * wave),
                    Items.silicon, Config.c.getInt("loadout.erekir.silicon.base") + (Config.c.getInt("loadout.erekir.silicon.perWave") * wave),
                    Items.thorium, Config.c.getInt("loadout.erekir.thorium.base") + (Config.c.getInt("loadout.erekir.thorium.perWave") * wave));

            if (wave >= Config.c.getInt("loadout.erekir.tungsten.waveRequired")) loadout.add(new ItemStack(Items.tungsten, Config.c.getInt("loadout.erekir.tungsten.perWave") * (wave - Config.c.getInt("loadout.erekir.tungsten.waveOffset"))));
            if (wave >= Config.c.getInt("loadout.erekir.oxide.waveRequired")) loadout.add(new ItemStack(Items.oxide, Config.c.getInt("loadout.erekir.oxide.perWave") * (wave - Config.c.getInt("loadout.erekir.oxide.waveOffset"))));
            if (wave >= Config.c.getInt("loadout.erekir.carbide.waveRequired")) loadout.add(new ItemStack(Items.carbide, Config.c.getInt("loadout.erekir.carbide.perWave") * (wave - Config.c.getInt("loadout.erekir.carbide.waveOffset"))));
            if (wave >= Config.c.getInt("loadout.erekir.surgeAlloy.waveRequired")) loadout.add(new ItemStack(Items.surgeAlloy, Config.c.getInt("loadout.erekir.surgeAlloy.perWave") * (wave - Config.c.getInt("loadout.erekir.surgeAlloy.waveOffset"))));
            if (wave >= Config.c.getInt("loadout.erekir.phaseFabric.waveRequired")) loadout.add(new ItemStack(Items.phaseFabric, Config.c.getInt("loadout.erekir.phaseFabric.perWave") * (wave - Config.c.getInt("loadout.erekir.phaseFabric.waveOffset"))));

            return loadout;
        }

        return new Seq<>();
    }
}
