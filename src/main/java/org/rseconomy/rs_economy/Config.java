/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */

package org.rseconomy.rs_economy;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

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