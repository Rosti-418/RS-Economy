/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Version-specific implementation for Minecraft 1.21.5+
 * Uses newer APIs and reflection for private field access.
 */
public class LeaderboardManager {
    private final BalanceManager balanceManager;
    private static final List<Map.Entry<UUID, Double>> cachedRanking = new ArrayList<>();
    private static long lastCacheTime = 0;
    private static final long CACHE_DURATION = 30000; // 30 seconds

    public LeaderboardManager(BalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    /**
     * Gets the sorted ranking of all players by balance.
     *
     * @return A list of UUID-balance pairs sorted by balance descending.
     */
    public List<Map.Entry<UUID, Double>> getSortedBalances() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheTime > CACHE_DURATION) {
            EconomyData data = EconomyData.get(null); // Will be called with proper level in production
            if (data != null) {
                cachedRanking.clear();
                cachedRanking.addAll(data.getBalances().entrySet());
                cachedRanking.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            }
            lastCacheTime = currentTime;
        }
        return new ArrayList<>(cachedRanking);
    }

    /**
     * Displays the leaderboard page to the player.
     *
     * @param player The player to display the leaderboard to.
     * @param page   The page number (1-based).
     */
    public void openLeaderboard(ServerPlayer player, int page) {
        List<Map.Entry<UUID, Double>> ranking = getSortedBalances();
        MinecraftServer server = getServer(player);
        LeaderboardMenuProvider.open(player, server, ranking, page);
    }

    // 1.21.5+ compatible server access
    private static MinecraftServer getServer(ServerPlayer player) {
        return player.level().getServer();
    }
}