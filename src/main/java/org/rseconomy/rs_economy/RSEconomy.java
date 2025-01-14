/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */

package org.rseconomy.rs_economy;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(RSEconomy.MOD_ID)
public class RSEconomy {

    public static final String MOD_ID = "rs_economy";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static RSEconomy instance;

    private final UserDataManager userDataManager = new UserDataManager();
    private final BalanceManager balanceManager = new BalanceManager();
    private final RewardManager rewardManager = new RewardManager(balanceManager);
    private final CommandManager commandManager = new CommandManager(userDataManager, balanceManager, rewardManager);

    public RSEconomy(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    public static RSEconomy getInstance() {
        return instance;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        userDataManager.loadUserData(balanceManager, rewardManager);
        ServerDataManager.loadServerData();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        commandManager.registerCommands(dispatcher);
        balanceManager.loadBalance();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        userDataManager.saveUserData(balanceManager.getBalances(), rewardManager.getLastClaimedRewards());
        ServerDataManager.saveServerData();
    }

    public void reload() {
        userDataManager.saveUserData(balanceManager.getBalances(), rewardManager.getLastClaimedRewards());
        ServerDataManager.saveServerData();
        userDataManager.loadUserData(balanceManager, rewardManager);
        ServerDataManager.loadServerData();
        balanceManager.loadBalance();
    }
}