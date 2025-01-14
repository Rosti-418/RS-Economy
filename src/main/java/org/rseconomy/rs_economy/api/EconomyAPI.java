/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */

package org.rseconomy.rs_economy.api;

import org.rseconomy.rs_economy.BalanceManager;

import java.util.UUID;

public class EconomyAPI {
    private static final BalanceManager balanceManager = new BalanceManager();

    public static double getBalance(UUID playerId) {
        return balanceManager.getBalance(playerId);
    }

    public static void addBalance(UUID playerId, double amount) {
        balanceManager.addBalance(playerId, amount);
    }

    public static boolean subtractBalance(UUID playerId, double amount) {
        return balanceManager.subtractBalance(playerId, amount);
    }
}