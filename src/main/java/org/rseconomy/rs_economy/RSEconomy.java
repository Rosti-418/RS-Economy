/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Main class for the RSEconomy mod, initializing core components and event handlers.
 */
@Mod(RSEconomy.MOD_ID)
public class RSEconomy {
    public static final String MOD_ID = "rs_economy";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static RSEconomy instance;
    private BalanceManager balanceManager;
    private RewardManager rewardManager;
    private CommandManager commandManager;

    /**
     * Constructs the RSEconomy mod instance and registers event listeners.
     *
     * @param modEventBus The mod event bus.
     * @param modContainer The mod container for configuration registration.
     */
    public RSEconomy(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_CONFIG);
    }

    /**
     * Gets the singleton instance of the RSEconomy mod.
     *
     * @return The RSEconomy instance.
     */
    public static RSEconomy getInstance() {
        return instance;
    }

    /**
     * Gets the balance manager instance.
     *
     * @return The BalanceManager instance.
     */
    public BalanceManager getBalanceManager() {
        return balanceManager;
    }

    /**
     * Performs common setup tasks during mod initialization.
     *
     * @param event The common setup event.
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Initialization logic can be added here if needed
    }

    /**
     * Initializes managers and registers commands when the server starts.
     *
     * @param event The server starting event.
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        ServerLevel level = event.getServer().overworld();
        EconomyData econ = EconomyData.get(level);
        balanceManager = new BalanceManager(econ);
        rewardManager = new RewardManager(balanceManager, econ);
        commandManager = new CommandManager(balanceManager, rewardManager);
        balanceManager.loadBalances(econ.getBalances());
        rewardManager.loadDailyRewards(econ.getDailyRewards());
        balanceManager.loadBalance();
        commandManager.registerCommands(dispatcher);
    }

    /**
     * Saves configuration when the server stops.
     *
     * @param event The server stopping event.
     */
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ModConfigs.COMMON_CONFIG.save();
    }

    /**
     * Reloads mod configuration and localization data.
     */
    public void reload() {
        Localization.init();
        balanceManager.loadBalance();
    }
}