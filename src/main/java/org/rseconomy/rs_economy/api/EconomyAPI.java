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