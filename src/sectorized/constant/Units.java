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

        zenith.speed *= (float) Config.c.getDouble("unit.speedMultiplier.zenith");
        zenith.health *= (float) Config.c.getDouble("unit.healthMultiplier.zenith");

        mega.speed *= (float) Config.c.getDouble("unit.speedMultiplier.mega");
        mega.health *= (float) Config.c.getDouble("unit.healthMultiplier.mega");

        antumbra.speed *= (float) Config.c.getDouble("unit.speedMultiplier.antumbra");
        antumbra.health *= (float) Config.c.getDouble("unit.healthMultiplier.antumbra");

        quad.speed *= (float) Config.c.getDouble("unit.speedMultiplier.quad");

        eclipse.speed *= (float) Config.c.getDouble("unit.speedMultiplier.eclipse");
        eclipse.health *= (float) Config.c.getDouble("unit.healthMultiplier.eclipse");

        crawler.speed *= (float) Config.c.getDouble("unit.speedMultiplier.crawler");
        dagger.speed *= (float) Config.c.getDouble("unit.speedMultiplier.dagger");
        nova.speed *= (float) Config.c.getDouble("unit.speedMultiplier.nova");

        atrax.speed *= (float) Config.c.getDouble("unit.speedMultiplier.atrax");
        mace.speed *= (float) Config.c.getDouble("unit.speedMultiplier.mace");
        pulsar.speed *= (float) Config.c.getDouble("unit.speedMultiplier.pulsar");

        spiroct.speed *= (float) Config.c.getDouble("unit.speedMultiplier.spiroct");
        fortress.speed *= (float) Config.c.getDouble("unit.speedMultiplier.fortress");
        quad.speed *= (float) Config.c.getDouble("unit.speedMultiplier.quad.extra");

        arkyid.speed *= (float) Config.c.getDouble("unit.speedMultiplier.arkyid");
        scepter.speed *= (float) Config.c.getDouble("unit.speedMultiplier.scepter");
        vela.speed *= (float) Config.c.getDouble("unit.speedMultiplier.vela");

        toxopid.speed *= (float) Config.c.getDouble("unit.speedMultiplier.toxopid");
        reign.speed *= (float) Config.c.getDouble("unit.speedMultiplier.reign");
        corvus.speed *= (float) Config.c.getDouble("unit.speedMultiplier.corvus");

        emanate.speed *= (float) Config.c.getDouble("unit.speedMultiplier.emanate");
    }

    public static void setUnitHealthMultiplier(float multiplier) {
        for (UnitType unitType : unitTypes) {
            unitType.health = unitType.health / healthMultiplier * multiplier;
        }

        healthMultiplier = multiplier;
    }
}
