/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy.api;

import org.rseconomy.rs_economy.BalanceManager;
import org.rseconomy.rs_economy.RSEconomy;

import java.util.UUID;

/**
 * Provides an API for interacting with the RSEconomy mod's economy system.
 * This class allows external mods to manage player balances.
 */
public class EconomyAPI {

    /**
     * Retrieves the balance of a player.
     *
     * @param playerId The UUID of the player.
     * @return The player's current balance.
     */
    public static double getBalance(UUID playerId) {
        return RSEconomy.getInstance().getBalanceManager().getBalance(playerId);
    }

    /**
     * Adds an amount to a player's balance.
     *
     * @param playerId The UUID of the player.
     * @param amount   The amount to add.
     */
    public static void addBalance(UUID playerId, double amount) {
        RSEconomy.getInstance().getBalanceManager().addBalance(playerId, amount);
    }

    /**
     * Subtracts an amount from a player's balance if sufficient funds are available.
     *
     * @param playerId The UUID of the player.
     * @param amount   The amount to subtract.
     * @return True if the subtraction was successful, false if insufficient funds.
     */
    public static boolean subtractBalance(UUID playerId, double amount) {
        return RSEconomy.getInstance().getBalanceManager().subtractBalance(playerId, amount);
    }
}