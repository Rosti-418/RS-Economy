/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages persistent economy data, including player balances and daily reward timestamps.
 * This class extends SavedData to handle serialization and deserialization to NBT.
 */
public class EconomyData extends SavedData {
    private final Map<UUID, Double> balances = new HashMap<>();
    private final Map<UUID, LocalDate> dailyRewards = new HashMap<>();

    /**
     * Creates a new instance of EconomyData.
     *
     * @return A new EconomyData instance.
     */
    public static EconomyData create() {
        return new EconomyData();
    }

    /**
     * Loads economy data from NBT.
     *
     * @param tag      The NBT compound tag containing the data.
     * @param provider The HolderLookup provider for deserialization.
     * @return The loaded EconomyData instance.
     */
    public static EconomyData load(CompoundTag tag, HolderLookup.Provider provider) {
        EconomyData data = new EconomyData();
        if (tag.contains("balances")) {
            CompoundTag balancesTag = tag.getCompound("balances");
            for (String uuidStr : balancesTag.getAllKeys()) {
                UUID uuid = UUID.fromString(uuidStr);
                Tag playerTag = balancesTag.get(uuidStr);
                double amount;
                if (playerTag instanceof CompoundTag compoundTag) {
                    // Legacy format: sum all currencies
                    amount = compoundTag.getAllKeys().stream()
                            .mapToDouble(compoundTag::getDouble)
                            .sum();
                } else {
                    amount = balancesTag.getDouble(uuidStr);
                }
                data.balances.put(uuid, amount);
            }
        }
        if (tag.contains("dailyRewards")) {
            CompoundTag rewardsTag = tag.getCompound("dailyRewards");
            for (String uuidStr : rewardsTag.getAllKeys()) {
                data.dailyRewards.put(
                        UUID.fromString(uuidStr),
                        LocalDate.parse(rewardsTag.getString(uuidStr))
                );
            }
        }
        return data;
    }

    /**
     * Saves economy data to NBT.
     *
     * @param tag      The NBT compound tag to save to.
     * @param provider The HolderLookup provider for serialization.
     * @return The modified NBT compound tag.
     */
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

    /**
     * Retrieves the EconomyData instance for the given server level.
     *
     * @param level The server level to retrieve data for.
     * @return The EconomyData instance.
     */
    public static EconomyData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(EconomyData::create, EconomyData::load),
                "rs_economy"
        );
    }

    /**
     * Sets a player's balance.
     *
     * @param uuid   The player's UUID.
     * @param amount The new balance amount.
     */
    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        setDirty();
    }

    /**
     * Gets a player's balance.
     *
     * @param uuid The player's UUID.
     * @return The player's balance, or 0.0 if not found.
     */
    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    /**
     * Sets the date of a player's last daily reward claim.
     *
     * @param uuid The player's UUID.
     * @param date The date of the last claim.
     */
    public void setDailyReward(UUID uuid, LocalDate date) {
        dailyRewards.put(uuid, date);
        setDirty();
    }

    /**
     * Gets the date of a player's last daily reward claim.
     *
     * @param uuid The player's UUID.
     * @return The date of the last claim, or null if not found.
     */
    public LocalDate getDailyReward(UUID uuid) {
        return dailyRewards.get(uuid);
    }

    /**
     * Gets the map of all player balances.
     *
     * @return A copy of the balances map.
     */
    public Map<UUID, Double> getBalances() {
        return new HashMap<>(balances);
    }

    /**
     * Gets the map of all daily reward claim dates.
     *
     * @return A copy of the daily rewards map.
     */
    public Map<UUID, LocalDate> getDailyRewards() {
        return new HashMap<>(dailyRewards);
    }
}