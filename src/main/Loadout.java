package main;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class Loadout {
    public static Seq<ItemStack> getLoadout(int wave) {
        wave--;

        Seq<ItemStack> loadout = ItemStack.list(
                Items.copper, 800 + (150 * wave),
                Items.lead, 500 + (100 * wave),
                Items.graphite, 150 + (20 * wave),
                Items.silicon, 150 + (30 * wave),
                Items.metaglass, 100 + (10 * wave),
                Items.titanium, 50 + (20 * wave),
                Items.thorium, 10 + (15 * wave));

        if (wave >= 5) loadout.add(new ItemStack(Items.plastanium, 20 * (wave - 4)));
        if (wave >= 10) loadout.add(new ItemStack(Items.phaseFabric, 15 * (wave - 9)));
        if (wave >= 10) loadout.add(new ItemStack(Items.surgeAlloy, 15 * (wave - 9)));

        return loadout;
    }

    private static Schematic getStartingBase(int wave) {
        if (wave >= 40) {
            return Schematics.readBase64("bXNjaAF4nE2Ua3KcRhSFL9283yDHWcX8zEq8Asy0HKoQTAEjZbLY/E1lC4lK5Nw+rrKlEh+C7u/049LyJE9GwmV4cdJ+ceOxbtOf7vrlNrwtv0lxdfu4TbdjWheRXyW//T7s7vI2zLO0w/aybu56Gdfl1T3WTZ5u87AfwzLdX3487PZ1HrbLbVjcfMHdNyfl5m7DhGfrtBxS/NRA7LCNEu3D/LpKjODb7CR8vu9Omq/bdP3mfojT+zKvw9VtknwdjsNtD8nw8himBc+aCc22A8PbV2DD0Nc3t12W9eqk0PTnQSf7kHLELC7LfZzdfZfPw/U6HdOru2wOtv3Y7tpMkn30IdKM22N9nu/T9fIy/aHp42Oc18VJPA/LiAci/4j/MXoJ8OvvPSwREhERS6BMxP+bsmNGR84mBVESFVETDdESHdEr9M5Y6IxYRSRWh5NwVClHlXE4CNLhFERJVERNNASCMGIGBQj6PlMTAF5tqDaqToBcbAR4p6HT0GnoNOpUeKeh09Jp6bR0WjotZDbFuBsxmBT6+Q4911b0oc7Jw+ha6o3NcYmT8/z4C39/YylCukO6Q3VrmzxRz3me77bAv0WEyEANHf09987HRIzRwWNMEfYSY47ojeiN1KvvsMQVgCWugQqGPNCuHX09awFdYi0JXbSY2pjamNqY2li1DZCniYTnuw9Eu/P9/A+j91e/GQWeYtFb9G4Cke/tOqb1wl2TQBEQhrBESEQERtYh2U8p0T1V+D1N6EzU6atLyzpVp8IQlgiJiIjVkuqeKiqxOkVfCyllGWUZZRllGWUZZRllGb+mTGWKiqiJhmiJjui11HJd9x5rhbp98lVgGiDG2gZYW6yvXsEPic8Pv7rGfz45k3Im5UzKmZQzKWdSzqRCk0q/K35z0F1RETXREC3REb57qd1rACMsfFmha8klLFkZpa6QfsM5Uehc8DYNz38x/tB/IhHmo59OhUgUcIks1F2JLF9yPc8Z6Fpt4OsGsRVjtYzRqGJexbyKeZXmlX5Q8FbqVXhvRW+t3k9AqJZaV0JnnvgvQh94bU1tTW2t2lhqamtqa2prahvV/iJa6EZPFt+9YfdGu1u8q7Rfw3On4bnTsNYaWlp+2C2/wFYPHYUV+wkIxZ9WXt1S3ao6BLBpnwHtgkvP01j0XUdZR1mnMj3svKWjpeP8Oj3StF/PU9yfg70/AAFDWCIkIiImEiLVhJ4JPRNY3UDJJhVREw3REh2B/P8B/9+zLw==");
        } else if (wave >= 30) {
            return Schematics.readBase64("bXNjaAF4nE2UYY6bMBSEX2yMDYZADpJ/vcmewCXeKhILEUl2lZ6wZ6q2Td94tFITKR/BzIzfs40c5GCkWtJblvElT7d1O//Mp5dL+li+STzl67SdL7fzuoiMMlzmdL2l5Xx/O36keZYxbW/rlk/HaV3e82PdpJ7TMuVNui1f0nk7XtbzchP/Pd1ueXuITdsk8brOSYfSkmepXu/XLIf/bh316kcWd03z+yrtZf3I23FZT1nCfZnXdFL7+rpuaigRGa8J835IN+lcjst9mvP9Kn56TPO6ZBH5JeVj8LPTb7kusERFONmBtZS/nggUN3ykJSLRET2xJwZiBHBlDEORZcUgqyKcGIzUnJUnQpkH8mpFS0SiI3piTwwMGr9KNLXCYsygMKAUZhhkGGQQpPNAEKBBXlUdyjdqXVwGeo5slV7aUoWOWUQFNahQg6W1pbWFdaNooLI0szSzNKtgVpW+6xjyS1dqwhOlExXlFeUV5Y5yR7mj3FHuKHeQt4omuFKSOj7/PD+fn7B5fpqoY63DMnd6FbGKtfS7skXgOTBo5H9BB9HtAkNYoiJ0NnsNDdgdNQxr/RaXmi6eLp4uni6eLp4uHi5ogC44Cilr6ykPlAfKA+WB8kB54EoHlVugI3piTwzEiBVtWCQ2967XdtlSv2n1jgtGe4WlMQbbsVg2tGxo2dCyoWVDy5YVl4MSywqUbVzkLeUt5S3lLeUt5ZHycsAqwSLpjzbUwRk3yupG1A80RCsGEVHn/Pv5F/M28CjnJfK8RJ6XyJ5GbqSOaeUclz7h+PbcSB0zOmZ0yMAjHbZch6kDAyQdzXqa9TyPPeU95Wiu7tgeOqsYsGd76vY8GHh9GJzqQDREixfIHqcAT458w5RVG7j9YbWry0wKKsIRNeFhNtB6oPXAKQ1YGqDDzFC9QcLId5h8vcp2gCEsURGOqAmPDTgyaGTQiCCMRTRrRBDQE3tiIDTxH8yBaxc=");
        } else if (wave >= 20) {
            return Schematics.readBase64("bXNjaAF4nE2S/27aMBSFr38RO3bC+iD8tUfpE3jBXSOlCUpCEXv0dRq716eVBkIfhvMd7IvpSEdNds5vhb49l2Ff1vFXOT9f8m3+TvFctmEdL/u4zERHetrHPc/j9e00LPN7uS8rpf2VFf7klqeJTF4Hitsy5fV0yXOZ6Om/xYnf/SzUbEPe97JS86PyTmktlzxyahnnnfx1npZ85oB9zeNEUb56ybI3jg7LWk7zdZjKdaP2stzKepqXcyG35el9Ifty3QoR3flFjurjADREiuFJCQLQAhFIQAf0kldoUWhR0qIZnsgwApHldcSHnQQUeyRrBxyARrajxTswalIjaZA0SBpJNowaMYhYRCwiViIHhpfTWApOfiDwu1YO5Cgpwk4sdFeXDAVowACWVOTGRsqcVMiR+6/BKYECNGAA9hKj9e7x8fgrT9KP32QfH/z6UyfWf85dhAYtDVoatDTSImh5zYhAAjqgJ1MHLsPxMreOoWULvk6fUXUP3UP30D30AD2I7mVWDhsLUAPUADVADVBbqK2o/dcOZcwcbungNR/W1uPL9OV8DA8E0kpuhxREKRA42XrEfxkhRAgRQoKQICQICUKCkCAkCB2ETm6MQAMGsAD/4XLxakuHls9BdmjpccPl2iuBBgxgAQfInWDUlh4tvbT8Az+/YvE=");
        } else if (wave >= 10) {
            return Schematics.readBase64("bXNjaAF4nD2RfY6bMBDFn23AYBOpe5D806vkBC5xJSTWRoZslJ591XQ+pCKhX2LeezOewQUXi66kz4wft7ycta1/8v22p2f5iXjPx9LW/VxrAWZ8nOuZyvr4vC61fOVXbbj8P3qmbUN/pO2rIh51S+26p5I3zC3vaaV/dS0nXGoLwl6fuV1LvWeMj7LVdM8N81JbvpbHsuXHAf8rnWduL/hjkV+InPI7cZMvADd60UOeATAED8MYFZMiKKJiZqFRn2GfJXg4R5iAjhDgekKENYSZlVYNlg2OMCkCLCNylFWlU6Vj5UDwI833/f3+S2lUBiN99lTNUYZlBIVkOM4YySdlOpV0KulU0mmZXtoH9ymwCrrFRJjgGHKRXi/Sq29Q36C+QX3cp4B8jKCIihmOJyQD9dwkw8INPDa6HXnf3+gMj18CvAZ4DfAaMEJWMXKAJzg+nCCLCeR+02O4BPcklSxvQkYZuDuZlIaIJKgk8hHPxvAOo0qiSqJKZt01b94wrMIpOgVPkiC+mX3/ABd1TCk=");
        }

        return Schematics.readBase64("bXNjaAF4nCWQXW6DMBCExz+AgTz0Inmo1JvkBK6xVCRqI0MapWevlM7aSPBZ65mdXTBj1rDJf0e83WI4c1l/43Lb/SO9Y17iEcq6n2tOwAwXcvqJz1wwh7zvsVwfftswfPrzjOWJ+cibL9fdp7hh2vODipSXCPvl1w2XkEu8pnvY4v2Au6ct+yUWXErc/UpfXtOJ4Qi1HYwvAcAHX1jUpwMU0cMIBiiBaxgbJlGoZlBi0AQNhhgAAQ2CalA0QES2oZMsjd6x8vrjydCpxdkRThpoOunT4uxZ7mQSA0ca0/FUr41cGzZzkm1ZNDLVJOgZZRgiMktoJ10V8yy/L1MnZVU2hBkILZtrGZ31UTaQHZxMI5CRia6BDWU7KieJFcnYJGOTjE0imytV/1iFbjANlgeiGiYx/AOoYDxl");
    }

    public static void spawnStartingBase(int coreX, int coreY, Team team) {
        Schematic start = Loadout.getStartingBase(state.wave);

        Schematic.Stile coreTile = start.tiles.find(s -> s.block instanceof CoreBlock);
        if (coreTile == null) throw new IllegalArgumentException("Schematic has no core tile.");
        int ox = coreX - coreTile.x, oy = coreY - coreTile.y;
        start.tiles.each(st -> {
            Tile tile = world.tile(st.x + ox, st.y + oy);
            if (tile == null) return;

            if (tile.block() != Blocks.air) {
                tile.removeNet();
            }

            tile.setNet(st.block, team, st.rotation);

            if (st.config != null) {
                tile.build.configureAny(st.config);
            }
            if (tile.block() instanceof CoreBlock) {
                for (ItemStack stack : state.rules.loadout) {
                    Call.setItem(tile.build, stack.item, stack.amount);
                }
            }
        });
    }
}
