/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Menu for displaying the leaderboard of player balances.
 */
public class LeaderboardMenu extends AbstractContainerMenu {

    private static final String PREV_BASE64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";
    private static final String NEXT_BASE64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";

    private final Container container;
    private final List<Map.Entry<UUID, Double>> ranking;
    private final MinecraftServer server;
    private int page;
    private final int maxPage;
    private final boolean largeInventory;
    private final int rows;

    /**
     * Displays the leaderboard page to the player.
     *
     * @param id      The window ID.
     * @param playerInv The player's inventory.
     * @param ranking The sorted ranking list.
     * @param server  The Minecraft server instance.
     * @param page   The page number (1-based).
     */
    public LeaderboardMenu(int id, Inventory playerInv, List<Map.Entry<UUID, Double>> ranking, MinecraftServer server, int page) {
        super(ranking.size() > 18 ? MenuType.GENERIC_9x6 : MenuType.GENERIC_9x3, id);

        this.ranking = ranking != null ? ranking : List.of();
        this.server = server;

        this.largeInventory = this.ranking.size() > 18;
        this.rows = largeInventory ? 6 : 3;

        this.container = new SimpleContainer(rows * 9);

        int pageSize = largeInventory ? 45 : rows * 9;
        this.maxPage = Math.max(1, (int) Math.ceil(this.ranking.size() / (double) pageSize));
        this.page = Math.max(1, Math.min(page, maxPage));

        fillInventory();

        try {
            if (playerInv.player instanceof ServerPlayer sp) addSelfHead(sp);
        } catch (Throwable ignored) {}

        layoutSlots(playerInv);
    }

    /**
     * Fills the inventory with player heads representing the leaderboard entries and navigation items.
     */
    private void fillInventory() {
        int pageSize = largeInventory ? 45 : rows * 9;
        int start = (page - 1) * pageSize;

        for (int slot = 0; slot < pageSize; slot++) {
            int index = start + slot;
            if (index >= ranking.size()) {
                container.setItem(slot, ItemStack.EMPTY);
                continue;
            }
            var entry = ranking.get(index);
            container.setItem(slot, createPlayerSkull(entry.getKey(), entry.getValue(), index + 1));
        }

        // Navigation nur bei großen Inventaren UND mehreren Seiten
        if (largeInventory && maxPage > 1) {
            container.setItem(45, page > 1 ? createDecoHead(PREV_BASE64, Localization.get("leaderboard.btn.prev")) : ItemStack.EMPTY);
            container.setItem(49, createPageInfoItem());
            container.setItem(53, page < maxPage ? createDecoHead(NEXT_BASE64, Localization.get("leaderboard.btn.next")) : ItemStack.EMPTY);
        }
    }

    /**
     * Creates a decorative player head with a custom texture.
     *
     * @param base64      The base64-encoded texture data for the head.
     * @param displayName The display name for the head item.
     * @return ItemStack representing the decorative head.
     */
    private ItemStack createDecoHead(String base64, String displayName) {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        GameProfile profile = new GameProfile(UUID.randomUUID(), displayName != null ? displayName : "DecoHead");
        if (base64 != null && !base64.isEmpty()) {
            profile.getProperties().put("textures", new Property("textures", base64));
        }
        skull.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        if (displayName != null) {
            skull.set(DataComponents.CUSTOM_NAME, Component.literal(displayName));
        }
        return skull;
    }

    /**
     * Creates an item displaying the current page information.
     *
     * @return ItemStack representing the page info.
     */
    private ItemStack createPageInfoItem() {
        ItemStack item = new ItemStack(Items.PAPER);
        item.set(DataComponents.CUSTOM_NAME,
                Component.literal(Localization.get("leaderboard.page", page, maxPage)));
        return item;
    }

    /**
     * Adds the player's own head to the inventory, showing their balance and rank.
     *
     * @param player The player whose head to add.
     */
    private void addSelfHead(ServerPlayer player) {
        if (player == null) return;

        double bal = ranking.stream()
                .filter(e -> e.getKey().equals(player.getUUID()))
                .map(Map.Entry::getValue)
                .findFirst().orElse(0.0);

        ItemStack skull = createPlayerSkull(player.getUUID(), bal, getRankOf(player.getUUID()));
        skull.set(DataComponents.CUSTOM_NAME, Component.literal(Localization.get("leaderboard.self")));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal(Localization.get("leaderboard.balance", bal, BalanceManager.CURRENCY)));
        int rank = getRankOf(player.getUUID());
        lore.add(Component.literal(rank > 0 ? Localization.get("leaderboard.self.position", rank) : Localization.get("leaderboard.self.position.missing")));
        skull.set(DataComponents.LORE, new ItemLore(lore));

        int slot;
        if (largeInventory && maxPage > 1) {
            slot = 47; // 3. von links unterste Reihe
        } else {
            int bottomRowStart = (rows - 1) * 9;
            slot = bottomRowStart + 4; // mittig
        }
        container.setItem(slot, skull);
    }

    /**
     * Gets the rank of a player by their UUID.
     *
     * @param uuid The player's UUID.
     * @return The rank (1-based) or -1 if not found.
     */
    private int getRankOf(UUID uuid) {
        for (int i = 0; i < ranking.size(); i++) if (ranking.get(i).getKey().equals(uuid)) return i + 1;
        return -1;
    }

    /**
     * Creates a player skull item representing a player's balance and rank.
     *
     * @param uuid    The player's UUID.
     * @param balance The player's balance.
     * @param rank    The player's rank.
     * @return ItemStack representing the player's skull.
     */
    private ItemStack createPlayerSkull(UUID uuid, double balance, int rank) {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        GameProfile profile = server.getProfileCache().get(uuid).orElse(null);
        if (profile != null) {
            skull.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        } else {
            skull.set(DataComponents.PROFILE, new ResolvableProfile(new GameProfile(uuid, "Player")));
        }

        String display = (rank > 0 ? rank + ". " : "") + (profile != null ? Localization.get("leaderboard.name", profile.getName()) : "Player");
        skull.set(DataComponents.CUSTOM_NAME, Component.literal(display));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal(Localization.get("leaderboard.balance", balance, BalanceManager.CURRENCY)));
        skull.set(DataComponents.LORE, new ItemLore(lore));
        return skull;
    }

    /**
     * Lays out the slots for the container and the player's inventory.
     *
     * @param playerInv The player's inventory.
     */
    private void layoutSlots(Inventory playerInv) {
        int index = 0;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new LockedSlot(container, index++, 8 + col * 18, 18 + row * 18));

        int invY = rows == 6 ? 140 : 70;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new LockedSlot(playerInv, col + row * 9 + 9, 8 + col * 18, invY + row * 18));

        invY += 58;
        for (int col = 0; col < 9; col++)
            this.addSlot(new LockedSlot(playerInv, col, 8 + col * 18, invY));
    }

    /**
     * Refreshes the inventory to display a new page.
     *
     * @param newPage The new page number to display.
     */
    private void refreshPage(int newPage) {
        newPage = Math.max(1, Math.min(newPage, maxPage));
        if (newPage == page) return;
        page = newPage;
        fillInventory();
        broadcastChanges();
    }

    /**
     * A slot that does not allow item placement or pickup.
     */
    public static class LockedSlot extends Slot {
        public LockedSlot(Container container, int index, int x, int y) { super(container, index, x, y); }
        @Override public boolean mayPickup(Player player) { return false; }
        @Override public boolean mayPlace(ItemStack stack) { return false; }
    }

    /**
     * Handles click events for navigation buttons.
     */
    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {
        if (largeInventory && maxPage > 1) {
            if (slot == 45 && page > 1) { refreshPage(page - 1); return; }
            if (slot == 53 && page < maxPage) { refreshPage(page + 1); return; }
        }
    }

    /**
     * Always valid since this is a custom menu.
     */
    @Override
    public boolean stillValid(Player player) { return true; }

    /**
     * Disables quick moving of items.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    /**
     * No special action needed on removal.
     */
    @Override
    public void removed(Player player) { }
}
