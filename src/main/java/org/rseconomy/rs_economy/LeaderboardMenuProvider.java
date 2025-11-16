/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides the leaderboard menu for displaying player rankings.
 */
public class LeaderboardMenuProvider implements net.minecraft.world.MenuProvider {

    private final List<Map.Entry<UUID, Double>> ranking;
    private final MinecraftServer server;
    private final int page;

    /**
     * Constructs a LeaderboardMenuProvider with the specified ranking, server, and page.
     *
     * @param ranking The list of player UUIDs and their balances.
     * @param server  The Minecraft server instance.
     * @param page    The current page number for pagination.
     */
    public LeaderboardMenuProvider(List<Map.Entry<UUID, Double>> ranking, MinecraftServer server, int page) {
        this.ranking = ranking;
        this.server = server;
        this.page = page;
    }

    /**
     * Gets the display name for the leaderboard menu.
     *
     * @return The display name component.
     */
    @Override
    public Component getDisplayName() {
        return Component.literal(Localization.get("leaderboard.title", BalanceManager.CURRENCY));
    }

    /**
     * Creates the leaderboard menu for the player.
     *
     * @param id    The menu ID.
     * @param inv   The player's inventory.
     * @param player The player entity.
     * @return The created AbstractContainerMenu instance.
     */
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new LeaderboardMenu(id, inv, ranking, server, page);
    }

    /**
     * Opens the leaderboard menu for the specified player.
     *
     * @param player  The player to open the menu for.
     * @param server  The Minecraft server instance.
     * @param ranking The list of player UUIDs and their balances.
     * @param page    The current page number for pagination.
     */
    public static void open(ServerPlayer player, MinecraftServer server, List<Map.Entry<UUID, Double>> ranking, int page) {
        player.openMenu(new LeaderboardMenuProvider(ranking, server, page));
    }
}
