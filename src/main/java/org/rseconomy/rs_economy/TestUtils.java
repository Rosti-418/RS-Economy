package org.rseconomy.rs_economy;

import net.minecraft.server.level.ServerPlayer;

import java.util.Random;
import java.util.UUID;

/**
 * Utility-Klasse für Testspieler und Testguthaben.
 */
public class TestUtils {

    private static final Random random = new Random();

    /**
     * Generiert eine Anzahl Fake-Spieler für Testzwecke.
     * Jeder Spieler bekommt ein zufälliges Guthaben zwischen minAmount und maxAmount.
     *
     * @param balanceManager Dein BalanceManager
     * @param amount         Anzahl Fake-Spieler
     * @param minAmount      Minimales Geld
     * @param maxAmount      Maximales Geld
     */
    public static void generateFakePlayers(BalanceManager balanceManager, int amount, double minAmount, double maxAmount) {
        for (int i = 0; i < amount; i++) {
            // Zufällige UUID für den Fake-Spieler
            UUID fakeId = UUID.randomUUID();
            // Name als "TestPlayer_X"
            String name = "TestPlayer_" + (i + 1);
            // Zufälliges Geld
            double money = minAmount + (maxAmount - minAmount) * random.nextDouble();

            // Spieler im BalanceManager registrieren
            balanceManager.addBalance(fakeId, money);

            // Optional: Debug-Ausgabe
            System.out.println("Generated " + name + " with " + money + " coins.");
        }
    }
}