package sectorized.constant;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.SpawnGroup;
import mindustry.game.Team;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.units.Reconstructor;

import static mindustry.content.UnitTypes.*;

public class Rules {
    public static final mindustry.game.Rules rules = new mindustry.game.Rules();

    static {
        rules.tags.put("sectorized", "true");
        rules.enemyCoreBuildRadius = 0.0f;
        rules.canGameOver = false;
        rules.defaultTeam = Team.derelict;
        rules.waves = true;
        rules.waveSpacing = 60 * 60 * 2;
        rules.waitEnemies = false;
        rules.buildSpeedMultiplier = 1.5f;
        rules.blockDamageMultiplier = 2.0f;
        rules.unitDamageMultiplier = 0.5f;
        rules.buildCostMultiplier = 0.75f;
        rules.dropZoneRadius = 100f;
        rules.logicUnitBuild = false;
        rules.loadout = Loadout.getLoadout(1);
        rules.bannedBlocks = ObjectSet.with(
                Blocks.shockMine,
                Blocks.switchBlock,
                Blocks.hyperProcessor,
                Blocks.logicProcessor,
                Blocks.microProcessor,
                Blocks.memoryCell,
                Blocks.memoryBank,
                Blocks.logicDisplay,
                Blocks.largeLogicDisplay);

        rules.unitCap = 0;
        Blocks.coreShard.unitCapModifier = 2;
        Blocks.coreFoundation.unitCapModifier = 5;
        Blocks.coreNucleus.unitCapModifier = 8;

        ((ItemTurret) (Blocks.foreshadow)).ammoTypes.forEach(ammoType -> {
            ammoType.value.damage *= 0.5;
        });
        ((Reconstructor) (Blocks.exponentialReconstructor)).constructTime = 60f * 60f * 1;
        ((Reconstructor) (Blocks.tetrativeReconstructor)).constructTime = 60f * 60f * 2;

        rules.spawns = Seq.with(
                // T1
                new SpawnGroup(dagger) {{
                    begin = 0;
                    end = 9;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(nova) {{
                    begin = 5;
                    end = 9;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(crawler) {{
                    begin = 3;
                    end = 9;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(flare) {{
                    begin = 5;
                    end = 9;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(risso) {{
                    begin = 5;
                    end = 9;
                    spacing = 2;
                    unitAmount = 1;
                    unitScaling = 0.5f;
                }},

                // T2
                new SpawnGroup(mace) {{
                    begin = 10;
                    end = 19;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(pulsar) {{
                    begin = 15;
                    end = 19;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(atrax) {{
                    begin = 13;
                    end = 19;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(horizon) {{
                    begin = 15;
                    end = 19;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(minke) {{
                    begin = 15;
                    end = 19;
                    spacing = 2;
                    unitAmount = 1;
                    unitScaling = 1;
                }},

                // T3
                new SpawnGroup(fortress) {{
                    begin = 20;
                    end = 29;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(quasar) {{
                    begin = 25;
                    end = 29;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(spiroct) {{
                    begin = 23;
                    end = 29;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(zenith) {{
                    begin = 25;
                    end = 29;
                    unitAmount = 1;
                    unitScaling = 1;
                }},
                new SpawnGroup(bryde) {{
                    begin = 25;
                    end = 29;
                    spacing = 2;
                    unitAmount = 1;
                    unitScaling = 0.5f;
                }},

                // T4
                new SpawnGroup(scepter) {{
                    begin = 30;
                    end = 39;
                    unitAmount = 1;
                    unitScaling = 0.5f;
                }},
                new SpawnGroup(vela) {{
                    begin = 35;
                    end = 39;
                    unitAmount = 1;
                    unitScaling = 0.5f;
                }},
                new SpawnGroup(arkyid) {{
                    begin = 33;
                    end = 39;
                    unitAmount = 1;
                    unitScaling = 0.5f;
                }},
                new SpawnGroup(antumbra) {{
                    begin = 35;
                    end = 39;
                    unitAmount = 1;
                    unitScaling = 0.5f;
                }},
                new SpawnGroup(quad) {{
                    begin = 37;
                    end = 39;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},
                new SpawnGroup(sei) {{
                    begin = 35;
                    end = 39;
                    spacing = 2;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},

                // T5
                new SpawnGroup(reign) {{
                    begin = 40;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},
                new SpawnGroup(corvus) {{
                    begin = 45;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},
                new SpawnGroup(toxopid) {{
                    begin = 50;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},
                new SpawnGroup(eclipse) {{
                    begin = 55;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},
                new SpawnGroup(oct) {{
                    begin = 60;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }},
                new SpawnGroup(omura) {{
                    begin = 55;
                    end = never;
                    spacing = 2;
                    unitAmount = 1;
                    unitScaling = 0.25f;
                }}
        );
    }
}
