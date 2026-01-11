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
import java.util.UUID;

/**
 * Version-specific implementation for Minecraft 1.20.6
 * Uses older APIs compatible with this version.
 */
public class EconomyData extends SavedData {
    private final Map<UUID, Double> balances = new HashMap<>();
    private final Map<UUID, LocalDate> dailyRewards = new HashMap<>();

    public static EconomyData create() {
        return new EconomyData();
    }

    public static EconomyData load(CompoundTag tag, HolderLookup.Provider provider) {
        EconomyData data = new EconomyData();

        // Load balances - 1.20.6 compatible
        if (tag.contains("balances")) {
            CompoundTag balancesTag = tag.getCompound("balances");
            for (String key : balancesTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double amount = balancesTag.getDouble(key);
                    data.balances.put(uuid, amount);
                } catch (IllegalArgumentException e) {
                    // Skip invalid UUID entries
                }
            }
        }

        // Load daily rewards - 1.20.6 compatible
        if (tag.contains("dailyRewards")) {
            CompoundTag rewardsTag = tag.getCompound("dailyRewards");
            for (String key : rewardsTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String dateStr = rewardsTag.getString(key);
                    if (dateStr != null && !dateStr.isEmpty()) {
                        LocalDate date = LocalDate.parse(dateStr);
                        data.dailyRewards.put(uuid, date);
                    }
                } catch (IllegalArgumentException e) {
                    // Skip invalid UUID entries
                }
            }
        }

        return data;
    }

    @Override
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
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(EconomyData::create, EconomyData::load),
                "rs_economy"
        );
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