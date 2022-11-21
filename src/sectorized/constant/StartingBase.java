package sectorized.constant;

import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class StartingBase {
    public static void spawnStartingBase(int coreX, int coreY, Team team) {
        Schematic start = getStartingBase(state.wave);

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

    private static Schematic getStartingBase(int wave) {
        if (State.planet.equals(Planets.serpulo.name)) {
            if (wave >= 20) {
                return Schematics.readBase64("bXNjaAF4nE2Uy27cRhBFa5rv90OJk5+YZb7EyyALitNyCFDkgORIVj422yC/kAia1K1rwLFgHg7Zfaq6utjyIA+BhMvw7KX97Mdj3aY//OXzdXhdfpHi4vdxm67HtC4iEs/Do593cb/+9rPk19+H3Z9fh3mWdtie181fzuO6vPi3dZOH6zzsx7BMt+fvD7t9nYftfB0WP5/17ouXcvPXYdJn67QcUvxvgATDNkq0D/PLKrEmcZ29hE+33UvzuE2XL/67OL0t8zpc/CbJ43AcfnuTTF8ew7Tos2bSYduh6e2rYtPU11e/nZf14qVA9KcBC3+TctRVnJfbOPvbLp+Gy2U6phd/3rza9mO7YZgk+2hBpBm3t/Vpvk2X8/P0FdHHt3FeF83wddABZ//12EyN0i2jjhD5W+yfw+Wkf3ZvCIiQiIhYTmAi9jPlxIyOnEMKoiQqoiYaoiU6ogdw5wLVOQmASAKkkzCrlFllTEcDIZ2CKImKqImG0ECaMQOdNNC3lbqTwtSOagd1osgliBTmdHQ6Oh2dDk7AnI7OgM6AzoDOgM5AZUGqeTfidFE6zyb0rK3gIdZkcKglboJcL3Fyv3/8qf//0lKEdId0h3BjTJ7Ac7/f34NCfxaRhjzB0NHfc+8sTMQwSF5zinQvNeeI3ojeCF680xJXCi1xrajUkJ8wtaOvZy/olBgtgaLF1MbUxtTG1MbQNoo8TRDs/n7/V7O2q21CeH83QaFXLXqrs5uTMLGYQWMEtV2TE3AiHBEQIRERmlmnkW1JCfYUsD1N6EzgtO5CW6dwAo4IiJCIiBiWFHsKVBJgidYLKWUZZRllGWUZZRllGWUZv6YMMqAiaqIhWqIjerRajrr3Wivt2wfrAtco4lR1WketJa7KD6uws08nZ5ScUXJGyRklZ5ScUXJGKRCltB2xjdHpQEXUREO0REfY9BLTa4VmV1hL6dSS5SvZFSWqg+83JwqsQ9+m4f2f+wdWgF7TteCzqTSkNm+psbTnSo1l7dbzjFFdiwHWMxq2Yli0sA6qGK9ivIrxKsQrLSn1VvAC5q3oreH9QRHCUqMSWHliXwMemLamtqa2hjaWmtqa2pramtoG2h8FTe5wqtj0htMbTA/0XYV5Dc+chmdOwz5raGn5Ubf8+locOJ+wEfwViZ1Upm6pbqEOFbppPykwRS89T2LBu44yJGswS0dLR0vH9XU4zjCv5wluZ2Bvh5/CEQEREhEREwmRomV6RugZgZ2tKDmkImqiIVqiIzT+f1btup4=");
            } else if (wave >= 15) {
                return Schematics.readBase64("bXNjaAF4nE2UYY6bMBSEX2yMDYZADpJ/vcmewCXeKhILEUl2lZ6wZ6q2Td94tFITKR/BzIzfs40c5GCkWtJblvElT7d1O//Mp5dL+li+STzl67SdL7fzuoiMMlzmdL2l5Xx/O36keZYxbW/rlk/HaV3e82PdpJ7TMuVNui1f0nk7XtbzchP/Pd1ueXuITdsk8brOSYfSkmepXu/XLIf/bh316kcWd03z+yrtZf3I23FZT1nCfZnXdFL7+rpuaigRGa8J835IN+lcjst9mvP9Kn56TPO6ZBH5JeVj8LPTb7kusERFONmBtZS/nggUN3ykJSLRET2xJwZiBHBlDEORZcUgqyKcGIzUnJUnQpkH8mpFS0SiI3piTwwMGr9KNLXCYsygMKAUZhhkGGQQpPNAEKBBXlUdyjdqXVwGeo5slV7aUoWOWUQFNahQg6W1pbWFdaNooLI0szSzNKtgVpW+6xjyS1dqwhOlExXlFeUV5Y5yR7mj3FHuKHeQt4omuFKSOj7/PD+fn7B5fpqoY63DMnd6FbGKtfS7skXgOTBo5H9BB9HtAkNYoiJ0NnsNDdgdNQxr/RaXmi6eLp4uni6eLp4uHi5ogC44Cilr6ykPlAfKA+WB8kB54EoHlVugI3piTwzEiBVtWCQ2967XdtlSv2n1jgtGe4WlMQbbsVg2tGxo2dCyoWVDy5YVl4MSywqUbVzkLeUt5S3lLeUt5ZHycsAqwSLpjzbUwRk3yupG1A80RCsGEVHn/Pv5F/M28CjnJfK8RJ6XyJ5GbqSOaeUclz7h+PbcSB0zOmZ0yMAjHbZch6kDAyQdzXqa9TyPPeU95Wiu7tgeOqsYsGd76vY8GHh9GJzqQDREixfIHqcAT458w5RVG7j9YbWry0wKKsIRNeFhNtB6oPXAKQ1YGqDDzFC9QcLId5h8vcp2gCEsURGOqAmPDTgyaGTQiCCMRTRrRBDQE3tiIDTxH8yBaxc=");
            } else if (wave >= 10) {
                return Schematics.readBase64("bXNjaAF4nE2S/27aMBSFr38RO3bC+iD8tUfpE3jBXSOlCUpCEXv0dRq716eVBkIfhvMd7IvpSEdNds5vhb49l2Ff1vFXOT9f8m3+TvFctmEdL/u4zERHetrHPc/j9e00LPN7uS8rpf2VFf7klqeJTF4Hitsy5fV0yXOZ6Om/xYnf/SzUbEPe97JS86PyTmktlzxyahnnnfx1npZ85oB9zeNEUb56ybI3jg7LWk7zdZjKdaP2stzKepqXcyG35el9Ifty3QoR3flFjurjADREiuFJCQLQAhFIQAf0kldoUWhR0qIZnsgwApHldcSHnQQUeyRrBxyARrajxTswalIjaZA0SBpJNowaMYhYRCwiViIHhpfTWApOfiDwu1YO5Cgpwk4sdFeXDAVowACWVOTGRsqcVMiR+6/BKYECNGAA9hKj9e7x8fgrT9KP32QfH/z6UyfWf85dhAYtDVoatDTSImh5zYhAAjqgJ1MHLsPxMreOoWULvk6fUXUP3UP30D30AD2I7mVWDhsLUAPUADVADVBbqK2o/dcOZcwcbungNR/W1uPL9OV8DA8E0kpuhxREKRA42XrEfxkhRAgRQoKQICQICUKCkCAkCB2ETm6MQAMGsAD/4XLxakuHls9BdmjpccPl2iuBBgxgAQfInWDUlh4tvbT8Az+/YvE=");
            } else if (wave >= 5) {
                return Schematics.readBase64("bXNjaAF4nD2RfY6bMBDFn23AYBOpe5D806vkBC5xJSTWRoZslJ591XQ+pCKhX2LeezOewQUXi66kz4wft7ycta1/8v22p2f5iXjPx9LW/VxrAWZ8nOuZyvr4vC61fOVXbbj8P3qmbUN/pO2rIh51S+26p5I3zC3vaaV/dS0nXGoLwl6fuV1LvWeMj7LVdM8N81JbvpbHsuXHAf8rnWduL/hjkV+InPI7cZMvADd60UOeATAED8MYFZMiKKJiZqFRn2GfJXg4R5iAjhDgekKENYSZlVYNlg2OMCkCLCNylFWlU6Vj5UDwI833/f3+S2lUBiN99lTNUYZlBIVkOM4YySdlOpV0KulU0mmZXtoH9ymwCrrFRJjgGHKRXi/Sq29Q36C+QX3cp4B8jKCIihmOJyQD9dwkw8INPDa6HXnf3+gMj18CvAZ4DfAaMEJWMXKAJzg+nCCLCeR+02O4BPcklSxvQkYZuDuZlIaIJKgk8hHPxvAOo0qiSqJKZt01b94wrMIpOgVPkiC+mX3/ABd1TCk=");
            }

            return Schematics.readBase64("bXNjaAF4nCWQUW7DIBBExwu2id2PXiQflXqTnIBipFpywcJOo/ToVdR0B1tCD+F9uwMYMQps8l8Rr5cY9lzmnzhdVn9LbxinuIUyr/ucEzDChZy+4z0XjCGvayznm18W9B9+32O5Y9zy4st59SkuGNZ804qUpwj76ecFLyGXeE7XsMTrBndNS/ZTLHgpcfWzenlOO/ot1HYwvgQA77pgUb8WaBQdDNGjIdyB04GBFc0hNBREoYJR9AChAlGFRgWwyB5oOUvQOV3PP9jnQ02h2SocG4ia6gnNTo9bJjFwSmNa3dXfhr+NNnOcbfXQMNVAdDrK6ACWGYVAajiI46VgeoXwssK0en5iaMZ2DMCm1snzofl+IcI3YDxFx1YnKgNHQhiHN1K0B2oJb9009bUq5IA5YHWjqMJA4R8Izz5a");
        } else if (State.planet.equals(Planets.erekir.name)) {
            if (wave >= 25) {
                return Schematics.readBase64("bXNjaAF4nE2QQW6DMBBFxzYkQCCLLnoLNt30BD1BllUXjpm0Vl2IDChST9/5nk2FrDfj/+fbmM40OKpm/8P0fOGwLTn+8nS5+8f8lvk75lc6TbyGHO9bXGYiOiR/5bSSff9oqJr2sNGw7fPnuvE8PnxK1Aefr3Fi7Q7XzD580XGKt9u+Mp33eeJ8S8tjLNPnsGQefcjLfUlxpaf/42Py+ZPl2BdZ1BAZQUvOCjoywEnRA1AtYLSThdpBMFQhwyDFidIhxcic1AiwVdlGpU4LJ7RicZitBaIB5S5ONKvB5kD4RKsQTYKTogfqcrTAKCzCar1ZowONDrRqadXSwnIUdJjvoDj11dLV+NsOXot3oPIcZbPXrtduQAdYvMuATCOosD3A8gdiTTjq");
            } else if (wave >= 20) {
                return Schematics.readBase64("bXNjaAF4nCVPS27FIBAzn5c232Vv0GW2vUFP8JZVF4TQFj0KEUkUqafvDCAxHmOPNWDCoKCj+XV4uTt7pOz/3HrfzBXfs3v4/IZ+dbvNfjt8igCaYBYXdsiPzwZ6Pe2B8Tjj9364OF8mBAzW5MWvrrJmyc7YH0xnXF3+Cumay9BkU3azsTltKfidkl/p4gkQBM9QDC2UJOggGHqWBFsUZNVEfZR1ThKToqiCQUFoKkVTVdOs3dDgBuo1p3DTVegZWpQdWiqCDSWsZS7IB9SoOqU5s6N1i1YsPcrpWZMYKhsqG7lnKB8Za/TIe4KALP/HFy5k");
            } else if (wave >= 15) {
                return Schematics.readBase64("bXNjaAF4nCWOS27EIBBEC/B4xt9lbpCll3OEnGCWURYMJokVAha2ZSmnTzdtZB7dVV2AEb1BFe2vx8vDuz3l5c/Pj9We8S37nyXf0c1+c3lZ9yVFAHWwTx826PePC6r5cDuG/Yhf2+7jdNoQUD+zt+4b4xFnnz9DOqdiG13KfrIupzWFZaOsV/pxBRThBsNoBK2gg2aVLJpW0ZQ0tcxprhQMHRTDQPFWNCNaxVqFGhdQq+IUEFpBx2hQ3tDQRuONhDVcK/IBcg2PtxzHzZtoxdKhfJ1ovVS9VANK9sDRDC0wfN/Aln+r5Cmn");
            } else if (wave >= 10) {
                return Schematics.readBase64("bXNjaAF4nCWOTW6EMAyFX0IGyu+yN+iSVc/QE8yy6iJAaKPJJCiAUHv62gkS+Ww/288Y0BVQXj8NXu9mPkK0f2a5b/ryH9E8bHxHu5h9jnY7bPAASqcn43bIz68KajnnA8Nk4q9z9nyOl3YO/XH67/0wPqflFI2efzCcfjFxdeEa89gcohn1HMMWnN1RLXZdz92QyRv9qABBeEHBqFFIQpPRQrJKLQVFSRO5KPOcpIzAqmRQoOipcjFpirUbStxAseItHDQZLaNGuqGmR3ADuQhCzdWGbyyTqeApxUYNnZu01NIifS1rEl3Oupz1HDME7+zz6p5Mya/nln/qZjJh");
            } else if (wave >= 5) {
                return Schematics.readBase64("bXNjaAF4nFWOQU7EMAxFf9PQmbZiNkhIHKIb7sAJZolYpKkZIjJJ5aaq4PQ4yQop0rP/97eDEWMLHcyd8HwlmyK7X1quqznCG9O341eMC22W3ZpcDAA6b2byG9T7xwl62W3CZSb+8d7t9+kw3uMx7eG2JQq17WYmY79w2cNC/OnjMdWYjUyTsRzX6N2Gp3+xyRu+kRx8Qb4KNIIT2owzWiXoKwao7MpIK1XxmiqqmlPSCdpcNmVIaUFXxeLp7D2IpCFP5y256CuGjLOMNJ1IyIFe+rK43B7kZwUi/gEXKDYp");
            }

            return Schematics.readBase64("bXNjaAF4nFWOPU7FMBCEx47Jy4/0KkTBHVJwCE7wSkThOAtY+MXRJlEEp2c3rpCLzzuz4zF69BXc7O+EpxuFLXP8pem2+GN+ZfqO/IJ+ojVwXLaYZwB18iOlFfbtvYab9rDhOhL/pBT3+3D4lFCPTD584brPE/FHysdQ9kJmGnzgvOQUVzz+zw3J8ydJxTO0BzCCC4yiKWgLOrWMrlg5DSojU6cpW3JWJ4NKrwoLUwnqIp6eU8+J5CCS01cgaAs6RYPzD9IN8yDOObYqa5stuBSI+AccXTEa");
        }

        return null;
    }
}
