package sectorized.constant;

import mindustry.type.UnitType;

import static mindustry.content.UnitTypes.*;

public class Units {
    public static final UnitType[] unitTypes = new UnitType[]{
            dagger,
            mace,
            fortress,
            scepter,
            reign,

            nova,
            pulsar,
            quasar,
            vela,
            corvus,

            crawler,
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
            omura,

            retusa,
            oxynoe,
            cyerce,
            aegires,
            navanax,

            alpha,
            beta,
            gamma,

            stell,
            locus,
            precept,
            vanquish,
            conquer,

            merui,
            cleroi,
            anthicus,
            tecta,
            collaris,

            elude,
            avert,
            obviate,
            quell,
            disrupt,

            evoke,
            incite,
            emanate
    };

    public static float healthMultiplier = 1.0f;

    static {
        for (UnitType unitType : unitTypes) {
            unitType.payloadCapacity = 0;
        }

        zenith.speed *= 0.6f;
        zenith.health *= 0.5f;

        mega.speed *= 0.5f;
        mega.health *= 0.8f;

        antumbra.speed *= 0.8f;
        antumbra.health *= 0.8f;

        quad.speed *= 0.8f;

        eclipse.speed *= 0.7f;
        eclipse.health *= 0.7f;

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

        emanate.speed *= 0.6f;
    }

    public static void setUnitHealthMultiplier(float multiplier) {
        for (UnitType unitType : unitTypes) {
            unitType.health = unitType.health / healthMultiplier * multiplier;
        }

        healthMultiplier = multiplier;
    }
}
