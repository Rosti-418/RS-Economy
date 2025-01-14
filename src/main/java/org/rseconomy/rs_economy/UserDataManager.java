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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserDataManager {
    private static final File USERDATA_FILE = new File("rs-economy_userdata.json");
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataManager.class);
    private static final Gson GSON = new Gson().newBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public void saveUserData(Map<UUID, Map<String, Double>> balances, Map<UUID, LocalDate> dailyRewards) {
        try (FileWriter writer = new FileWriter(USERDATA_FILE)) {
            Map<String, Object> data = new HashMap<>();
            data.put("balances", balances);
            data.put("dailyRewards", dailyRewards);
            GSON.toJson(data, writer);
            LOGGER.info(Localization.get("userdata.save.success"));
        } catch (IOException e) {
            LOGGER.error(Localization.get("userdata.save.error"), e.getMessage());
        }
    }

    public void loadUserData(BalanceManager balanceManager, RewardManager rewardManager) {
        if (!USERDATA_FILE.exists()) {
            LOGGER.info(Localization.get("userdata.load.missing"));
            return;
        }
        try (FileReader reader = new FileReader(USERDATA_FILE)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> data = GSON.fromJson(reader, type);

            Type balancesType = new TypeToken<Map<UUID, Map<String, Double>>>() {
            }.getType();
            balanceManager.loadBalances(GSON.fromJson(GSON.toJson(data.get("balances")), balancesType));

            Type rewardsType = new TypeToken<Map<UUID, LocalDate>>() {
            }.getType();
            rewardManager.loadDailyRewards(GSON.fromJson(GSON.toJson(data.get("dailyRewards")), rewardsType));

            LOGGER.info(Localization.get("userdata.load.success"));
        } catch (IOException e) {
            LOGGER.error(Localization.get("userdata.load.error"), e.getMessage());
        }
    }
}