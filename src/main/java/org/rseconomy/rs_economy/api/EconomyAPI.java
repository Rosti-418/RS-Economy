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
public class EconomyAPI {
    public static double getBalance(UUID playerId) {
        return RSEconomy.getInstance().getBalanceManager().getBalance(playerId);
    }
    public static void addBalance(UUID playerId, double amount) {
        RSEconomy.getInstance().getBalanceManager().addBalance(playerId, amount);
    }
    public static boolean subtractBalance(UUID playerId, double amount) {
        return RSEconomy.getInstance().getBalanceManager().subtractBalance(playerId, amount);
    }
}