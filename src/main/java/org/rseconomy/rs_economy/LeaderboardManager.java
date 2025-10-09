/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.stream.Collectors;

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
        return balanceManager.getBalances().entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Displays the leaderboard page to the player.
     *
     * @param player The player to send the leaderboard to.
     * @param page   The page number (1-based).
     */
    public void displayLeaderboard(ServerPlayer player, int page) {
        List<Map.Entry<UUID, Double>> sorted = getSortedBalances();
        if (sorted.isEmpty()) {
            player.sendSystemMessage(Component.literal(Localization.get("leaderboard.noplayers")));
            return;
        }

        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) sorted.size() / itemsPerPage);
        page = Math.max(1, Math.min(page, totalPages));

        // Title
        player.sendSystemMessage(Component.literal(Localization.get("leaderboard.title", BalanceManager.CURRENCY)));

        // Calculate start/end index for the page
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, sorted.size());

        MinecraftServer server = player.getServer();
        UUID playerUUID = player.getUUID();
        int playerPosition = -1;
        double playerBalance = balanceManager.getBalance(playerUUID);

        // Find player's global position
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(playerUUID)) {
                playerPosition = i + 1;
                break;
            }
        }

        // Display entries for the page
        for (int i = start; i < end; i++) {
            Map.Entry<UUID, Double> entry = sorted.get(i);
            String name = getPlayerName(server, entry.getKey());
            String entryText = Localization.get("leaderboard.entry", (i + 1), name, entry.getValue(), BalanceManager.CURRENCY);
            player.sendSystemMessage(Component.literal(entryText));
        }

        // On page 1, always show the player's own position (if not already in top 10)
        if (page == 1 && playerPosition > 10) {  // If not in top 10
            String ownName = player.getName().getString();
            String ownText = Localization.get("leaderboard.yourposition", playerPosition, playerBalance, BalanceManager.CURRENCY);
            player.sendSystemMessage(Component.literal(ownText));
        }

        // Pagination controls
        Component pageInfo = Component.literal(Localization.get("leaderboard.page", page, totalPages));
        player.sendSystemMessage(pageInfo);

        Component pagination = Component.empty();
        if (page > 1) {
            Component prev = createClickableComponent(Localization.get("leaderboard.prev"), "/"+Localization.get("command.top")+" "+(page-1));
            pagination = pagination.copy().append(prev).append(" ");
        }
        if (page < totalPages) {
            Component next = createClickableComponent(Localization.get("leaderboard.next"), "/"+Localization.get("command.top")+" "+(page+1));
            pagination = pagination.copy().append(next);
        }
        if (!pagination.getString().isEmpty()) {
            player.sendSystemMessage(pagination);
        }
    }

    /**
     * Creates a clickable text component that runs a command on click.
     *
     * @param text    The display text.
     * @param command The command to run (e.g., "/top 2").
     * @return The clickable component.
     */
    private Component createClickableComponent(String text, String command) {
        Style style = Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to execute")));
        return Component.literal(text).setStyle(style);
    }

    /**
     * Gets the player name from UUID using the server's profile cache.
     *
     * @param server The Minecraft server.
     * @param uuid   The player's UUID.
     * @return The name, or UUID string if not found.
     */
    private String getPlayerName(MinecraftServer server, UUID uuid) {
        Optional<GameProfile> profile = server.getProfileCache().get(uuid);
        return profile.map(GameProfile::getName).orElse(uuid.toString());
    }
}