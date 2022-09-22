package sectorized.constant;

import mindustry.type.UnitType;

import static mindustry.content.UnitTypes.*;

public class Units {
    public static final UnitType[] unitTypes = new UnitType[]{
            mace,
            dagger,
            crawler,
            fortress,
            scepter,
            reign,
            nova,
            pulsar,
            quasar,
            vela,
            corvus,
            atrax,
            spiroct,
            arkyid,
            toxopid,
            flare,
            eclipse,
            horizon,
            zenith,
            antumbra,
            mono,
            poly,
            mega,
            quad,
            oct,
            risso,
            minke,
            bryde,
            sei,
            omura
    };

    public static float healthMultiplier = 1.0f;

    static {
        mono.health *= 10;
        horizon.health *= 0.8f;
        zenith.health *= 0.6f;
        quad.health *= 0.4f;
        corvus.health *= 0.5f;
        antumbra.health *= 2.0f;
        eclipse.health *= 2.2f;

        scepter.speed *= 1.2f;
        vela.speed *= 1.2f;
        arkyid.speed *= 1.2f;

        corvus.speed *= 1.3f;
        reign.speed *= 1.3f;
        toxopid.speed *= 1.1f;
        oct.health *= 0.25f;
        oct.speed *= 0.9f;
        oct.payloadCapacity = 400f;
    }

    public static void setUnitHealthMultiplier(float multiplier) {
        for (UnitType unitType : unitTypes) {
            unitType.health = unitType.health / healthMultiplier * multiplier;
        }

        healthMultiplier = multiplier;
    }
}
