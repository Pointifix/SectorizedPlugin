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
        
    }

    public static void setUnitHealthMultiplier(float multiplier) {
        for (UnitType unitType : unitTypes) {
            unitType.health = unitType.health / healthMultiplier * multiplier;
        }

        healthMultiplier = multiplier;
    }
}
