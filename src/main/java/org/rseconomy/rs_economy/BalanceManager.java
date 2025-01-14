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
    private static final Map<UUID, Map<String, Double>> playerBalances = new HashMap<>();

    /**
     * Loads and updates player balances based on the current currency.
     * Ensures balances are migrated if the currency changes.
     */
    public static void loadBalance() {

        String newCurrency = ServerDataManager.getCurrency();

        if (CURRENCY != null && !CURRENCY.equals(newCurrency)) {
            for (Map.Entry<UUID, Map<String, Double>> entry : playerBalances.entrySet()) {
                UUID playerId = entry.getKey();
                Map<String, Double> balances = entry.getValue();

                double oldAmount = balances.getOrDefault(CURRENCY, 0.0);
                double newAmount = balances.getOrDefault(newCurrency, 0.0);

                balances.put(newCurrency, oldAmount + newAmount);
                balances.remove(CURRENCY);
            }
        }
        CURRENCY = newCurrency;
    }

    public double getBalance(UUID playerId) {
        return playerBalances.getOrDefault(playerId, new HashMap<>()).getOrDefault(CURRENCY, 0.0);
    }

    public void setBalance(UUID playerId, double amount) {
        mergeOldCurrencyBalance(playerId);
        playerBalances.computeIfAbsent(playerId, k -> new HashMap<>()).put(CURRENCY, amount);
    }

    public void addBalance(UUID playerId, double amount) {
        mergeOldCurrencyBalance(playerId);
        Map<String, Double> balances = playerBalances.computeIfAbsent(playerId, k -> new HashMap<>());
        balances.put(CURRENCY, balances.getOrDefault(CURRENCY, 0.0) + amount);
    }

    public boolean subtractBalance(UUID playerId, double amount) {
        mergeOldCurrencyBalance(playerId);
        Map<String, Double> balances = playerBalances.computeIfAbsent(playerId, k -> new HashMap<>());
        double currentBalance = balances.getOrDefault(CURRENCY, 0.0);
        if (currentBalance < amount) {
            // Insufficient funds; balance cannot be subtracted.
            return false;
        }
        balances.put(CURRENCY, currentBalance - amount);
        return true;
    }

    public Map<UUID, Map<String, Double>> getBalances() {
        return new HashMap<>(playerBalances);
    }

    public void loadBalances(Map<UUID, Map<String, Double>> balances) {
        playerBalances.putAll(balances);
    }

    private void mergeOldCurrencyBalance(UUID playerId) {
        Map<String, Double> balances = playerBalances.computeIfAbsent(playerId, k -> new HashMap<>());

        if (!balances.containsKey(CURRENCY)) {
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                String currency = entry.getKey();
                double amount = entry.getValue();

                balances.put(CURRENCY, balances.getOrDefault(CURRENCY, 0.0) + amount);
                balances.remove(currency);
                break;
            }
        }
    }
}