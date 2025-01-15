/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */

package org.rseconomy.rs_economy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ServerDataManager {
    private static final File SERVERDATA_FILE = new File("rs-economy_serverdata.json");
    public static final Logger LOGGER = LoggerFactory.getLogger(ServerDataManager.class);
    private static final Gson GSON = new Gson();

    private static Map<String, Object> data = new HashMap<>();

    public static void saveServerData() {
        try (FileWriter writer = new FileWriter(SERVERDATA_FILE)) {
            GSON.toJson(data, writer);
            LOGGER.info(Localization.get("serverdata.save.success"));
        } catch (IOException e) {
            LOGGER.error(Localization.get("serverdata.save.error"), e.getMessage());
        }
    }

    public static void loadServerData() {
        if (!SERVERDATA_FILE.exists()) {
            LOGGER.warn(Localization.get("serverdata.load.missing"));
            initializeDefaultSettings();
        }
        else{
            try (FileReader reader = new FileReader(SERVERDATA_FILE)) {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                data = GSON.fromJson(reader, type);
                LOGGER.info(Localization.get("serverdata.load.success"));
            } catch (IOException e) {
                LOGGER.error(Localization.get("serverdata.load.error"), e.getMessage());
                initializeDefaultSettings();
            } catch (Exception e) {
                LOGGER.error(Localization.get("serverdata.load.error"), e.getMessage());
                initializeDefaultSettings();
            }
        }
        Localization.init();
    }

    private static void initializeDefaultSettings() {
        data.put("currency", "Coins");
        data.put("locale", "en_US");
        data.put("dailyReward", "100-500");
        saveServerData();
    }

    public static String getCurrency() {
        return (String) data.getOrDefault("currency", "Coins");
    }

    public static void setCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException(Localization.get("serverdata.currency.invalid"));
        }
        data.put("currency", currency);
        saveServerData();
    }

    public static Locale getLocale() {
        String localeString = (String) data.getOrDefault("locale", "en_US");
        if(localeString == null || localeString.trim().isEmpty()) {
            localeString = "en_US";
        }
        String[] parts = localeString.split("_");
        if (parts.length == 1) {
            return Locale.of(parts[0]);
        } else if (parts.length == 2) {
            return Locale.of(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return Locale.of(parts[0], parts[1], parts[2]);
        }
        LOGGER.warn(Localization.get("serverdata.locale.format.invalid"), localeString);
        return Locale.ENGLISH;
    }

    public static void setLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException(Localization.get("serverdata.locale.format.null"));
        }
        data.put("locale", locale.toString());
        //LOGGER.debug("locale" + locale.toString());
        saveServerData();
    }

    public static int[] getDailyReward() {
        String rewardString = (String) data.getOrDefault("dailyReward", "100_500");

        String[] parts = rewardString.split("-");

        int minReward = Integer.parseInt(parts[0]);
        int maxReward = Integer.parseInt(parts[1]);

        return new int[]{minReward, maxReward};
    }

    public static void setDailyReward(int minReward, int maxReward) {
        if (minReward < 0 || maxReward < 0 || minReward > maxReward) {
            throw new IllegalArgumentException(Localization.get("serverdata.dailyreward.invalid"));
        }
        data.put("dailyReward", minReward + "-" + maxReward);
        saveServerData();
    }
}