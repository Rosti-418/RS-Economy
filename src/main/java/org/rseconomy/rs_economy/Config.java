package org.rseconomy.rs_economy;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Handles configuration loading for the RSEconomy mod.
 * Currently, no specific actions are taken on config load.
 */
@EventBusSubscriber(modid = RSEconomy.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    /**
     * Called when the mod's configuration is loaded or reloaded.
     *
     * @param event The mod configuration event.
     */
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // Placeholder for future configuration loading logic
    }
}