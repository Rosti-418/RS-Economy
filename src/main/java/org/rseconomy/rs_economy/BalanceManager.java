/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player balances for the RSEconomy mod.
 * Synchronizes in-memory balances with persistent storage in EconomyData.
 */
public class BalanceManager {
    /** The name of the currency used in the economy system. */
    public static String CURRENCY;

    private final Map<UUID, Double> playerBalances = new HashMap<>();
    private final EconomyData economyData;

    /**
     * Constructs a BalanceManager with the specified EconomyData instance.
     *
     * @param economyData The EconomyData instance for persistent storage.
     */
    public BalanceManager(EconomyData economyData) {
        this.economyData = economyData;
    }

    /**
     * Loads the current currency name from the configuration.
     */
    public static void loadBalance() {
        CURRENCY = ModConfigs.CURRENCY.get();
    }

    /**
     * Retrieves a player's balance.
     *
     * @param playerId The UUID of the player.
     * @return The player's balance, or 0.0 if not found.
     */
    public double getBalance(UUID playerId) {
        return playerBalances.getOrDefault(playerId, 0.0);
    }

    /**
     * Sets a player's balance to a specific amount.
     *
     * @param playerId The UUID of the player.
     * @param amount   The new balance amount.
     */
    public void setBalance(UUID playerId, double amount) {
        playerBalances.put(playerId, amount);
        economyData.setBalance(playerId, amount);
    }

    /**
     * Adds an amount to a player's balance.
     *
     * @param playerId The UUID of the player.
     * @param amount   The amount to add.
     */
    public void addBalance(UUID playerId, double amount) {
        double newAmount = getBalance(playerId) + amount;
        setBalance(playerId, newAmount);
    }

    /**
     * Subtracts an amount from a player's balance if sufficient funds are available.
     *
     * @param playerId The UUID of the player.
     * @param amount   The amount to subtract.
     * @return True if the subtraction was successful, false if insufficient funds.
     */
    public boolean subtractBalance(UUID playerId, double amount) {
        double currentBalance = getBalance(playerId);
        if (currentBalance < amount) {
            return false;
        }
        setBalance(playerId, currentBalance - amount);
        return true;
    }

    /**
     * Gets a copy of all player balances.
     *
     * @return A copy of the balances map.
     */
    public Map<UUID, Double> getBalances() {
        return new HashMap<>(playerBalances);
    }

    /**
     * Loads balances from a provided map into memory.
     *
     * @param balances The map of UUIDs to balances to load.
     */
    public void loadBalances(Map<UUID, Double> balances) {
        playerBalances.putAll(balances);
    }
}