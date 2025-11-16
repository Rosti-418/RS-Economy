/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.List;

/**
 * Manages the leaderboard for player balances, including sorting and pagination.
 */
public class LeaderboardManager {
    private final BalanceManager balanceManager;

    /**
     * Constructs a LeaderboardManager with the specified balance manager.
     *
     * @param balanceManager The balance manager instance.
     */
    public LeaderboardManager(BalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    /**
     * Gets a sorted list of player balances (descending by amount).
     *
     * @return List of entries sorted by balance descending.
     */
    private List<Map.Entry<UUID, Double>> getSortedBalances() {
        return balanceManager.getBalances().entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
    }

    /**
     * Displays the leaderboard page to the player.
     *
     * @param player The player to display the leaderboard to.
     * @param page   The page number (1-based).
     */
    public void openLeaderboard(ServerPlayer player, int page) {
        List<Map.Entry<UUID, Double>> ranking = getSortedBalances();
        LeaderboardMenuProvider.open(player, player.server, ranking, page);
    }
}