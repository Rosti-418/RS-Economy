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
public class BalanceManager {
    public static String CURRENCY;
    private static final Map<UUID, Double> playerBalances = new HashMap<>();
    private final EconomyData economyData;
    public BalanceManager(EconomyData economyData) {
        this.economyData = economyData;
    }
    /**

     Loads the current currency name.
     */
    public static void loadBalance() {
        CURRENCY = ModConfigs.CURRENCY.get();
    }

    public double getBalance(UUID playerId) {
        return playerBalances.getOrDefault(playerId, 0.0);
    }
    public void setBalance(UUID playerId, double amount) {
        playerBalances.put(playerId, amount);
        economyData.setBalance(playerId, amount);
        economyData.setDirty();
    }
    public void addBalance(UUID playerId, double amount) {
        double currentBalance = getBalance(playerId);
        double newAmount = currentBalance + amount;
        playerBalances.put(playerId, newAmount);
        economyData.setBalance(playerId, newAmount);
        economyData.setDirty();
    }
    public boolean subtractBalance(UUID playerId, double amount) {
        double currentBalance = getBalance(playerId);
        if (currentBalance < amount) {
// Insufficient funds; balance cannot be subtracted.
            return false;
        }
        double newAmount = currentBalance - amount;
        playerBalances.put(playerId, newAmount);
        economyData.setBalance(playerId, newAmount);
        economyData.setDirty();
        return true;
    }
    public Map<UUID, Double> getBalances() {
        return new HashMap<>(playerBalances);
    }
    public void loadBalances(Map<UUID, Double> balances) {
        playerBalances.putAll(balances);
    }
}