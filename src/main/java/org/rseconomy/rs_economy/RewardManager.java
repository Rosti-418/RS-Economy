/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Manages daily rewards for players, ensuring one claim per day.
 */
public class RewardManager {
    private final BalanceManager balanceManager;
    private final Map<UUID, LocalDate> lastClaimedRewards = new HashMap<>();
    private final Random random = new Random();
    private final EconomyData economyData;

    /**
     * Constructs a RewardManager with the specified balance manager and economy data.
     *
     * @param balanceManager The balance manager instance.
     * @param economyData    The economy data instance for persistent storage.
     */
    public RewardManager(BalanceManager balanceManager, EconomyData economyData) {
        this.balanceManager = balanceManager;
        this.economyData = economyData;
    }

    /**
     * Allows a player to claim their daily reward.
     * Rewards are only claimable once per day, and the amount is randomly selected
     * within the configured range.
     *
     * @param player The player claiming the reward.
     * @return 1 if the reward is successfully claimed, 0 if already claimed today.
     */
    public int claimDailyReward(ServerPlayer player) {
        UUID playerId = player.getUUID();
        LocalDate today = LocalDate.now();
        if (lastClaimedRewards.getOrDefault(playerId, LocalDate.MIN).equals(today)) {
            player.sendSystemMessage(Component.literal(
                    Localization.get("reward.daily.alreadyclaimed")));
            return 0;
        }
        lastClaimedRewards.put(playerId, today);
        economyData.setDailyReward(playerId, today);
        int min = ModConfigs.DAILY_REWARD_MIN.get();
        int max = ModConfigs.DAILY_REWARD_MAX.get();
        double rewardAmount = random.nextInt(max - min + 1) + min;
        balanceManager.addBalance(playerId, rewardAmount);
        player.sendSystemMessage(Component.literal(
                Localization.get("reward.daily.redeem", rewardAmount, BalanceManager.CURRENCY)));
        return 1;
    }

    /**
     * Gets a copy of the last claimed rewards map.
     *
     * @return A copy of the last claimed rewards map.
     */
    public Map<UUID, LocalDate> getLastClaimedRewards() {
        return new HashMap<>(lastClaimedRewards);
    }

    /**
     * Loads daily reward data from a provided map into memory.
     *
     * @param rewards The map of UUIDs to reward claim dates to load.
     */
    public void loadDailyRewards(Map<UUID, LocalDate> rewards) {
        lastClaimedRewards.putAll(rewards);
    }
}