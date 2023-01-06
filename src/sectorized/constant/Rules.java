package sectorized.constant;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.game.SpawnGroup;
import mindustry.game.Team;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;
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
        rules.pvp = true;
        Vars.state.gameOver = true;
        rules.waitEnemies = false;
        rules.buildSpeedMultiplier = 2.0f;
        rules.blockDamageMultiplier = 2.0f;
        rules.unitDamageMultiplier = 0.5f;
        rules.buildCostMultiplier = 1f;
        rules.dropZoneRadius = 100f;
        rules.logicUnitBuild = false;
        rules.coreIncinerates = true;
        rules.possessionAllowed = true;
        rules.showSpawns = true;
        rules.bannedBlocks = ObjectSet.with(
                Blocks.shockMine,
                Blocks.switchBlock,
                Blocks.hyperProcessor,
                Blocks.logicProcessor,
                Blocks.microProcessor,
                Blocks.memoryCell,
                Blocks.memoryBank,
                Blocks.logicDisplay,
                Blocks.largeLogicDisplay,
                Blocks.canvas);

        ((ItemTurret) (Blocks.foreshadow)).ammoTypes.forEach(ammoType -> {
            ammoType.value.damage *= 0.75;
        });
        ((ItemTurret) (Blocks.spectre)).ammoTypes.forEach(ammoType -> {
            ammoType.value.damage *= 2.0;
        });
        ((LaserTurret) (Blocks.meltdown)).shootType.damage *= 1.5;
        ((Reconstructor) (Blocks.exponentialReconstructor)).constructTime *= 0.75;
        ((Reconstructor) (Blocks.tetrativeReconstructor)).constructTime *= 0.75;

        rules.unitCap = 0;

        Blocks.coreShard.unitCapModifier = 4;
        Blocks.coreFoundation.unitCapModifier = 6;
        Blocks.coreNucleus.unitCapModifier = 8;

        Blocks.coreBastion.unitCapModifier = 3;
        Blocks.coreCitadel.unitCapModifier = 5;
        Blocks.coreAcropolis.unitCapModifier = 7;
    }

    public static void setSpawnGroups(mindustry.game.Rules rules) {
        if (State.planet.equals(Planets.serpulo.name)) {
            rules.waveSpacing = 60 * 60 * 2.5f;

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
                        unitScaling = 2f;
                    }},

                    // T4
                    new SpawnGroup(scepter) {{
                        begin = 30;
                        end = 39;
                        unitAmount = 1;
                        unitScaling = 2f;
                    }},
                    new SpawnGroup(vela) {{
                        begin = 35;
                        end = 39;
                        unitAmount = 1;
                        unitScaling = 2f;
                    }},
                    new SpawnGroup(arkyid) {{
                        begin = 33;
                        end = 39;
                        unitAmount = 1;
                        unitScaling = 2f;
                    }},
                    new SpawnGroup(antumbra) {{
                        begin = 35;
                        end = 39;
                        unitAmount = 1;
                        unitScaling = 2f;
                    }},
                    new SpawnGroup(quad) {{
                        begin = 37;
                        end = 39;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},
                    new SpawnGroup(sei) {{
                        begin = 35;
                        end = 39;
                        spacing = 2;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},

                    // T5
                    new SpawnGroup(reign) {{
                        begin = 40;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},
                    new SpawnGroup(corvus) {{
                        begin = 45;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},
                    new SpawnGroup(toxopid) {{
                        begin = 50;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},
                    new SpawnGroup(eclipse) {{
                        begin = 55;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},
                    new SpawnGroup(oct) {{
                        begin = 60;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }},
                    new SpawnGroup(omura) {{
                        begin = 55;
                        end = never;
                        spacing = 2;
                        unitAmount = 1;
                        unitScaling = 4f;
                    }}
            );
        } else if (State.planet.equals(Planets.erekir.name)) {
            rules.waveSpacing = 60 * 60 * 3f;

            rules.spawns = Seq.with(
                    // T1
                    new SpawnGroup(stell) {{
                        begin = 0;
                        end = 14;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(merui) {{
                        begin = 5;
                        end = 7;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(elude) {{
                        begin = 11;
                        end = 14;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    // T2
                    new SpawnGroup(locus) {{
                        begin = 15;
                        end = 29;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(anthicus) {{
                        begin = 22;
                        end = 29;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(avert) {{
                        begin = 26;
                        end = 29;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    // T3
                    new SpawnGroup(precept) {{
                        begin = 30;
                        end = 44;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(tecta) {{
                        begin = 37;
                        end = 44;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(obviate) {{
                        begin = 41;
                        end = 44;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    // T4
                    new SpawnGroup(vanquish) {{
                        begin = 45;
                        end = 59;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(collaris) {{
                        begin = 52;
                        end = 59;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(quell) {{
                        begin = 56;
                        end = 59;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    // T5
                    new SpawnGroup(conquer) {{
                        begin = 60;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }},
                    new SpawnGroup(disrupt) {{
                        begin = 72;
                        end = never;
                        unitAmount = 1;
                        unitScaling = 3f;
                    }}
            );
        }
    }
}
