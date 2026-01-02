/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Handles migration from legacy JSON file storage to the new NBT-based EconomyData system.
 * Migrates both user data (balances, daily rewards) and server configuration.
 */
public class LegacyJsonMigrator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String USERDATA_FILENAME = "rs-economy_userdata.json";
    private static final String SERVERDATA_FILENAME = "rs-economy_serverdata.json";
    
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();

    /**
     * Attempts to migrate legacy JSON data to the new system.
     *
     * @param server The Minecraft server instance.
     * @param economyData The EconomyData instance to migrate to.
     * @return true if migration was attempted (regardless of success), false if no legacy files were found.
     */
    public static boolean migrateIfNeeded(MinecraftServer server, EconomyData economyData) {
        File serverDirectory = server.getServerDirectory().toFile();
        File userDataFile = new File(serverDirectory, USERDATA_FILENAME);
        File serverDataFile = new File(serverDirectory, SERVERDATA_FILENAME);

        boolean userDataExists = userDataFile.exists() && userDataFile.isFile();
        boolean serverDataExists = serverDataFile.exists() && serverDataFile.isFile();

        if (!userDataExists && !serverDataExists) {
            return false; // No legacy files found
        }

        LOGGER.info("Legacy JSON files detected. Starting migration...");

        boolean migrationSuccessful = true;

        // Migrate server configuration first
        if (serverDataExists) {
            try {
                migrateServerData(serverDataFile);
                LOGGER.info("Successfully migrated server configuration from {}", SERVERDATA_FILENAME);
            } catch (Exception e) {
                LOGGER.error("Failed to migrate server configuration: {}", e.getMessage(), e);
                migrationSuccessful = false;
            }
        }

        // Migrate user data
        if (userDataExists) {
            try {
                migrateUserData(userDataFile, economyData);
                LOGGER.info("Successfully migrated user data from {}", USERDATA_FILENAME);
            } catch (Exception e) {
                LOGGER.error("Failed to migrate user data: {}", e.getMessage(), e);
                migrationSuccessful = false;
            }
        }

        // Delete legacy files only if migration was completely successful
        if (migrationSuccessful) {
            if (userDataExists) {
                if (userDataFile.delete()) {
                    LOGGER.info("Deleted legacy file: {}", USERDATA_FILENAME);
                } else {
                    LOGGER.warn("Failed to delete legacy file: {}", USERDATA_FILENAME);
                }
            }
            if (serverDataExists) {
                if (serverDataFile.delete()) {
                    LOGGER.info("Deleted legacy file: {}", SERVERDATA_FILENAME);
                } else {
                    LOGGER.warn("Failed to delete legacy file: {}", SERVERDATA_FILENAME);
                }
            }
            LOGGER.info("Migration completed successfully!");
        } else {
            LOGGER.warn("Migration completed with errors. Legacy files have been kept for manual review.");
        }

        return true;
    }

    /**
     * Migrates server configuration from JSON to ModConfigs.
     *
     * @param serverDataFile The server data JSON file.
     */
    private static void migrateServerData(File serverDataFile) throws IOException, JsonParseException {
        try (FileReader reader = new FileReader(serverDataFile)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            
            // Migrate currency
            if (json.has("currency") && json.get("currency").isJsonPrimitive()) {
                String currency = json.get("currency").getAsString();
                if (currency != null && !currency.trim().isEmpty()) {
                    ModConfigs.CURRENCY.set(currency);
                    LOGGER.info("Migrated currency: {}", currency);
                }
            }

            // Migrate locale
            if (json.has("locale") && json.get("locale").isJsonPrimitive()) {
                String localeString = json.get("locale").getAsString();
                if (localeString != null && !localeString.trim().isEmpty()) {
                    try {
                        String[] parts = localeString.split("_");
                        Locale locale;
                        if (parts.length == 1) {
                            locale = Locale.of(parts[0]);
                        } else if (parts.length == 2) {
                            locale = Locale.of(parts[0], parts[1]);
                        } else if (parts.length == 3) {
                            locale = Locale.of(parts[0], parts[1], parts[2]);
                        } else {
                            locale = Locale.of("en", "US");
                        }
                        ModConfigs.LOCALE.set(locale.toString());
                        LOGGER.info("Migrated locale: {}", localeString);
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse locale '{}', using default", localeString);
                    }
                }
            }

            // Migrate daily reward range
            if (json.has("dailyReward") && json.get("dailyReward").isJsonPrimitive()) {
                String rewardString = json.get("dailyReward").getAsString();
                if (rewardString != null && rewardString.contains("-")) {
                    try {
                        String[] parts = rewardString.split("-");
                        if (parts.length == 2) {
                            int min = Integer.parseInt(parts[0].trim());
                            int max = Integer.parseInt(parts[1].trim());
                            if (min >= 0 && max >= min) {
                                ModConfigs.DAILY_REWARD_MIN.set(min);
                                ModConfigs.DAILY_REWARD_MAX.set(max);
                                LOGGER.info("Migrated daily reward range: {}-{}", min, max);
                            }
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warn("Failed to parse daily reward range '{}'", rewardString);
                    }
                }
            }
        }
    }

    /**
     * Migrates user data (balances and daily rewards) from JSON to EconomyData.
     *
     * @param userDataFile The user data JSON file.
     * @param economyData The EconomyData instance to migrate to.
     */
    private static void migrateUserData(File userDataFile, EconomyData economyData) throws IOException, JsonParseException {
        try (FileReader reader = new FileReader(userDataFile)) {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = GSON.fromJson(reader, mapType);

            // Migrate balances
            if (data.containsKey("balances") && data.get("balances") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> balancesMap = (Map<String, Object>) data.get("balances");
                
                int migratedCount = 0;
                for (Map.Entry<String, Object> entry : balancesMap.entrySet()) {
                    try {
                        UUID uuid = UUID.fromString(entry.getKey());
                        Object balanceValue = entry.getValue();
                        
                        double totalBalance = 0.0;
                        
                        if (balanceValue instanceof Map) {
                            // Old format: Map<String, Double> (multiple currencies)
                            @SuppressWarnings("unchecked")
                            Map<String, Double> currencyMap = (Map<String, Double>) balanceValue;
                            
                            // Sum all currencies (migrate all balances to current currency)
                            for (Double amount : currencyMap.values()) {
                                if (amount != null) {
                                    totalBalance += amount;
                                }
                            }
                        } else if (balanceValue instanceof Number) {
                            // Fallback: direct number value
                            totalBalance = ((Number) balanceValue).doubleValue();
                        }
                        
                        if (totalBalance > 0) {
                            economyData.setBalance(uuid, totalBalance);
                            migratedCount++;
                        }
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Skipping invalid UUID in balances: {}", entry.getKey());
                    }
                }
                LOGGER.info("Migrated {} player balances", migratedCount);
            }

            // Migrate daily rewards
            if (data.containsKey("dailyRewards") && data.get("dailyRewards") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> rewardsMap = (Map<String, Object>) data.get("dailyRewards");
                
                int migratedCount = 0;
                for (Map.Entry<String, Object> entry : rewardsMap.entrySet()) {
                    try {
                        UUID uuid = UUID.fromString(entry.getKey());
                        Object rewardValue = entry.getValue();
                        
                        LocalDate date = null;
                        if (rewardValue instanceof String) {
                            date = LocalDate.parse((String) rewardValue);
                        } else if (rewardValue instanceof JsonElement) {
                            date = GSON.fromJson((JsonElement) rewardValue, LocalDate.class);
                        }
                        
                        if (date != null) {
                            economyData.setDailyReward(uuid, date);
                            migratedCount++;
                        }
                    } catch (IllegalArgumentException | DateTimeParseException e) {
                        LOGGER.warn("Skipping invalid UUID or date in daily rewards: {} = {}", entry.getKey(), entry.getValue());
                    }
                }
                LOGGER.info("Migrated {} daily reward entries", migratedCount);
            }
        }
    }

    /**
     * Simple type adapter for LocalDate serialization/deserialization.
     */
    private static class LocalDateTypeAdapter implements com.google.gson.JsonSerializer<LocalDate>, com.google.gson.JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    }
}

