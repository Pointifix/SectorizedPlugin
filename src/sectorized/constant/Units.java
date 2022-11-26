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
        zenith.speed *= 0.6f;
        zenith.health *= 0.5f;

        antumbra.speed *= 0.8f;
        antumbra.health *= 0.8f;

        crawler.speed *= 1.25f;
        dagger.speed *= 1.25f;
        nova.speed *= 1.25f;

        atrax.speed *= 1.25f;
        mace.speed *= 1.25f;
        pulsar.speed *= 1.25f;

        spiroct.speed *= 1.25f;
        fortress.speed *= 1.25f;
        quad.speed *= 1.25f;

        arkyid.speed *= 1.25f;
        scepter.speed *= 1.25f;
        vela.speed *= 1.25f;

        toxopid.speed *= 1.25f;
        reign.speed *= 1.25f;
        corvus.speed *= 1.25f;
    }

    public static void setUnitHealthMultiplier(float multiplier) {
        for (UnitType unitType : unitTypes) {
            unitType.health = unitType.health / healthMultiplier * multiplier;
        }

        healthMultiplier = multiplier;
    }
}
