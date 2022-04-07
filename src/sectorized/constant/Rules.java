package sectorized.constant;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.SpawnGroup;
import mindustry.game.Team;

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

        rules.spawns = Seq.with(
                // T1
                new SpawnGroup(dagger) {{
                    begin = 0;
                    end = 10;
                    unitAmount = 1;
                    unitScaling = 3;
                }},
                new SpawnGroup(nova) {{
                    begin = 5;
                    end = 10;
                    unitAmount = 1;
                    unitScaling = 3;
                }},
                new SpawnGroup(crawler) {{
                    begin = 3;
                    end = 10;
                    unitAmount = 1;
                    unitScaling = 3;
                }},
                new SpawnGroup(flare) {{
                    begin = 5;
                    end = 10;
                    unitAmount = 1;
                    unitScaling = 3;
                }},

                // T2
                new SpawnGroup(mace) {{
                    begin = 10;
                    end = 20;
                    unitAmount = 1;
                    unitScaling = 3;
                }},
                new SpawnGroup(pulsar) {{
                    begin = 15;
                    end = 20;
                    unitAmount = 1;
                    unitScaling = 3;
                }},
                new SpawnGroup(atrax) {{
                    begin = 13;
                    end = 20;
                    unitAmount = 1;
                    unitScaling = 3;
                }},
                new SpawnGroup(horizon) {{
                    begin = 15;
                    end = 20;
                    unitAmount = 1;
                    unitScaling = 3;
                }},

                // T3
                new SpawnGroup(fortress) {{
                    begin = 20;
                    end = 30;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(quasar) {{
                    begin = 25;
                    end = 30;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(spiroct) {{
                    begin = 23;
                    end = 30;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(zenith) {{
                    begin = 25;
                    end = 30;
                    unitAmount = 2;
                    unitScaling = 2;
                }},

                // T4
                new SpawnGroup(scepter) {{
                    begin = 30;
                    end = 40;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(vela) {{
                    begin = 35;
                    end = 40;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(arkyid) {{
                    begin = 33;
                    end = 40;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(antumbra) {{
                    begin = 35;
                    end = 40;
                    unitAmount = 2;
                    unitScaling = 2;
                }},
                new SpawnGroup(quad) {{
                    begin = 37;
                    end = 40;
                    unitAmount = 2;
                    unitScaling = 2;
                }},

                // T5
                new SpawnGroup(reign) {{
                    begin = 40;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 2;
                }},
                new SpawnGroup(corvus) {{
                    begin = 45;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 2;
                }},
                new SpawnGroup(toxopid) {{
                    begin = 50;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 2;
                }},
                new SpawnGroup(eclipse) {{
                    begin = 55;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 2;
                }},
                new SpawnGroup(oct) {{
                    begin = 60;
                    end = never;
                    unitAmount = 1;
                    unitScaling = 2;
                }}
        );
    }
}
