package main.old;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.type.ItemStack;

public class SectorizedLoadout {
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

        if (wave >= 5) loadout.add(new ItemStack(Items.plastanium, Math.min(0, (20 * (wave - 5)))));
        if (wave >= 10) loadout.add(new ItemStack(Items.phaseFabric, Math.min(0, (15 * (wave - 10)))));
        if (wave >= 10) loadout.add(new ItemStack(Items.surgeAlloy, Math.min(0, (15 * (wave - 10)))));

        return loadout;
    }

    public static Schematic getStartingBase(int wave) {
        if (wave > 30) {
            return Schematics.readBase64("bXNjaAF4nC2TbW7bMAyGKX/bkpNcxD97kp5Ac9TCgyMZstMgA3bxDWhG8l0L5LEV8qFEMXSiU0FV9LdAl/cwHykvv8L1ffOP+Eb2GvY5L9uxpEh0ofO2+v3wcbnfpodfV7r4fEs5XKc5xa/wTJnsnlafp83HsFK9+/Ur0bClR8hTTNdADeu2NVD1cd8DnRdOywcL9sTI1P7wB/NJ3T2uyV95yeWw+YWVaYkHuZnrTfE+r+G+05iPz+kzxJA975xOtxCv05bTTz0JtfNzXlMMZEXw4WXxSUS/Sf9K+TBAAZRARaVh1PI1UQO0yOoQ0gMDYAEHjKzTFKbh/0JQUFFqNSNLqjZQG1FLSIfIHm8DGYEFnAgN1AXUaq3koROTVoK4gLgQcc0le4RbwCFdVbrfhtTS8lsl2ythKWEpxSKntoBDozS9QisqeTD8UQMN0Ep/KuRVyKuQV6NprUT2/NaTkaKDNKBGZC2R2iHZkh5PUADcRinTd7zz15/XNy7w9VdP1sDQwNDC0MLQwtDiIlq5R8EAWMABozg7HLeT9Eoa0JVc8MVlv7lsY+TaVNJB0kHSQdJB0qNPMjSl0wcz8ofFsrZkwHANcm8nHahSZqCWIwxycAHvvmfIZhiaZ3GbVvJkWmqgATjhzDvpDWEgLIbA8prmquL/QDgoHBQOCicK+c4ixCFB80b8IkYdPV1SlICO0gjZCNmo7WdYhDiABf8A/4hn2w==");
        } else if (wave > 20) {
            return Schematics.readBase64("bXNjaAF4nEWSYY7bIBBGx2BjA/ZuehD/6032BNSmu5YciIizUXr0Vtp0hq9SYykPxMxjBqCRXhS1KZwjnd7icuSy/Yrr2yXc03fya7wuZbscW05EJxqPD16/ned72Hf6dmxHSDJdcvqMj1zIX/MeynwJKe7U/wjHEcuD2o+w8fS61DkZNl72SN017J+Zhlvac1h54RTKOZe4/veNC8/ndFv2eLuSu+R7LHPKa6SpHO/ze0yxBK6ZzDkmUfTLY9lziuTDVuafQRp6ENFO9afkr+Gvjis00FIj7KhODdAja0CIBRzggRGYBDJSqvq1QJEWuybFdt5By0qHAgzQy5YN76AMh3spsGHZv1JZVqvsSQaao9jCdShYFCwKdSrkaeTVthzHGalfo3gtIVIJSbdSVIUCOMUz7KAgef5uNM+dNNNKv4LabwtPB08HTwdPJx6BlX47SRd4YAQm2cHIJlIgH5Fn6EE/v5jq+fX8U80GEgOJgcRAYiCR1tXA0KRHBvc5MbycQc+RSkIm3CJHTvUy1YuIScnBdwBva0k8LJOPWx9gGWAZYLE4Xov7tki3ki7g9Fcu1jaEi7GsqKgKC4WDwkHhoHBQOFHIWs1zyHPI83gVHg9Y3ooWaKA+Lw+Zh8zXh8PwCBkBlv0FmvFa6A==");
        } else if (wave > 10) {
            return Schematics.readBase64("bXNjaAF4nEVRSXKDMBAciU0SPiQP4ZR35OQXKKCUqcISJbBdztezKLNk4UCrZrp7Nuih11BHfw7wcAzjnvL8Fqbj6m/xCfopbGOe131OEeAAh/2E+ct5uPllgWbzyzXB4z7vPlJ0TPEa7ilD9+L3PeQ79FtafB5WH8MC5hKX5KeQoT75eYHDmHIY4mVcwmUDt6ZbyENMU4BuG9kAej/n4dVTW3d48PmMium/DsAz8FfRT0GlELSEank1Aq0EOyAGGAEr4AiU2Ch61PirQWsEliuSU7CDisAIWCqrUP5bVDEXNZoy6CAuWly0uGhyaRHYRaMLy93fGB3SG3KpqM8WgfusiEKOWEdzNwxY0SAYU5fPUqAu7+WrfEBTiq4wbmUeljYibURKXTJgHwRWwEFlZVsGQRsaRaFp4f6Z2wq3JW6D7SLFYcbSCpUiIe/WSGlDBjQt78JQ3V68qTQfxMq9rDCtMC0xCZBJuY624+SMTu7kZN9OdE50TnSOdJT70RkBKwI0+AaoHVLm");
        } else if (wave > 5) {
            return Schematics.readBase64("bXNjaAF4nD1QW27EIAw0j2wIaaXuQfLT/55iT0ATpEZiISLJRtur90FtrC4fDLJnxmagh16Cju7q4eXixy3l+dNPl8Ud8RX6ya9jnpdtThHAwvM2by7O+3U4XAjQrC7cEpwf1THFm7+nDO272zaf79CvKbg8LC76AGaPIbnJZ9Afbg7wNKbsh7iPwe8r2CUdPg8xTR7adawGAPAG9Si6BCiBILmkQdGr4cKJiy0QAwxDx2AJBNsIemi8NEiJUOWC5FRsQREYho7GCpT/DxWVixpJHXRgF8kukl0kuZwQqotElyq3j2+0SG/IRdGeJ4SO7BVRyBHnyLpNBZxoEIxR5acU0OWr/JZvqbBWZZplDcsaltGGouNQDII0tLFAbcFey/sbjshwIoYItHT9kqkWvLuuQQj6Se113LOcuOVILUdjmWmZaYn5B4S+RN0=");
        }

        return Schematics.readBase64("bXNjaAF4nCWPbW7DIBBEh/UXtvujF0mkniUnoBgplihY2GmUXr2q3N0FCT1gHmjAjJnQJvcV8H4L/shl/QnLbXPP9IF5Cbsv63asOQETrM/pO7xywezztoVyeboYMe85unLZXAoR7d2tEW8+l3BJDx/DY4d9pJjdEgqmLT/5WspLwPDpjiOUF/qdbX/HsHs9AXDliQ46esAwBjQCWzFWTGgkVdOISQw2BbZCTcMmSaomVZPEbBhqEpvskZgte50UaDgzDWMEGQZnPVpYS+ffeaI9f2ngvaYtp0ZLoBkYRqr3ICsXjfbS/oMkHYOkmYX+xsqhNpGF5cdI0MnDY+081mys2VizST4uMBVUoeZUzUnMf20mOFA=");
    }
}
