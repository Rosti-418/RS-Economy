/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy.api;

import org.rseconomy.rs_economy.RSEconomy;

import java.util.UUID;

/**
 * Provides an API for interacting with the RSEconomy mod's economy system.
 * This class allows external mods to manage player balances.
 * <p>
 * Note: This API should only be used after the server has started.
 * Using it before initialization will result in a NullPointerException.
 */
public class EconomyAPI {

    /**
     * Retrieves the balance of a player.
     *
     * @param playerId The UUID of the player.
     * @return The player's current balance, or 0.0 if the balance manager is not initialized.
     * @throws NullPointerException if the balance manager is not initialized (server not started).
     */
    public static double getBalance(UUID playerId) {
        var instance = RSEconomy.getInstance();
        if (instance == null) return 0.0;
        var balanceManager = instance.getBalanceManager();
        return balanceManager != null ? balanceManager.getBalance(playerId) : 0.0;
    }

    /**
     * Adds an amount to a player's balance.
     *
     * @param playerId The UUID of the player.
     * @param amount   The amount to add.
     * @throws NullPointerException if the balance manager is not initialized (server not started).
     */
    public static void addBalance(UUID playerId, double amount) {
        var balanceManager = RSEconomy.getInstance().getBalanceManager();
        if (balanceManager != null) {
            balanceManager.addBalance(playerId, amount);
        }
    }

    /**
     * Subtracts an amount from a player's balance if sufficient funds are available.
     *
     * @param playerId The UUID of the player.
     * @param amount   The amount to subtract.
     * @return True if the subtraction was successful, false if insufficient funds or balance manager not initialized.
     */
    public static boolean subtractBalance(UUID playerId, double amount) {
        var instance = RSEconomy.getInstance();
        if (instance == null) return false;
        var balanceManager = instance.getBalanceManager();
        return balanceManager != null && balanceManager.subtractBalance(playerId, amount);
    }
}