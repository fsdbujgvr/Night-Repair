package com.nightrepaire.nightrepairemod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = NightRepair.MODID, dist = Dist.CLIENT)
public class NightRepairClient {
    public NightRepairClient(ModContainer container) {
        // Client-side initialization for Night Repair mod
        // NightRepair.LOGGER.info("Night Repair client initialized - Mixin injection active");
    }
}
