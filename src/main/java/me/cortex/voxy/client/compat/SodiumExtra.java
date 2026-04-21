package me.cortex.voxy.client.compat;

import net.neoforged.fml.ModList;

public class SodiumExtra {
    public static final boolean HAS_SODIUM_EXTRA = ModList.get().isLoaded("sodium-extra");
    public static boolean useSodiumExtraCulling() {
        return HAS_SODIUM_EXTRA;
    }
}
