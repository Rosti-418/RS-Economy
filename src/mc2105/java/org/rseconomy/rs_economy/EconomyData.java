/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Version-specific implementation for Minecraft 1.21.5+
 * Uses newer APIs with Optional return values.
 */
public class EconomyData extends SavedData {
    private final Map<UUID, Double> balances = new HashMap<>();
    private final Map<UUID, LocalDate> dailyRewards = new HashMap<>();

    public static EconomyData create() {
        return new EconomyData();
    }

    public static EconomyData load(CompoundTag tag, HolderLookup.Provider provider) {
        EconomyData data = new EconomyData();

        // For 1.21.5+ we use a simplified approach
        // This would need to be enhanced based on actual API availability
        return data;
    }

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag balancesTag = new CompoundTag();
        balances.forEach((uuid, amount) -> balancesTag.putDouble(uuid.toString(), amount));
        tag.put("balances", balancesTag);

        CompoundTag rewardsTag = new CompoundTag();
        dailyRewards.forEach((uuid, date) -> rewardsTag.putString(uuid.toString(), date.toString()));
        tag.put("dailyRewards", rewardsTag);

        return tag;
    }

    public static EconomyData get(ServerLevel level) {
        // For 1.21.8+ we use a different approach
        // This is a placeholder - would need actual implementation
        return new EconomyData();
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        setDirty();
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    public void setDailyReward(UUID uuid, LocalDate date) {
        dailyRewards.put(uuid, date);
        setDirty();
    }

    public LocalDate getDailyReward(UUID uuid) {
        return dailyRewards.get(uuid);
    }

    public Map<UUID, Double> getBalances() {
        return new HashMap<>(balances);
    }

    public Map<UUID, LocalDate> getDailyRewards() {
        return new HashMap<>(dailyRewards);
    }
}