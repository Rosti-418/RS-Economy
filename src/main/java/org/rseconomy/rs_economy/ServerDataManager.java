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
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDataManager.class);
    private static final Gson GSON = new Gson();

    private static Map<String, Object> data = new HashMap<>();

    static {
        loadServerData();
    }

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
            return;
        }

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

    private static void initializeDefaultSettings() {
        data.put("currency", "Coins");
        data.put("locale", "en");
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
        String localeString = (String) data.getOrDefault("locale", "en");
        String[] parts = localeString.split("_");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        } else if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new Locale(parts[0], parts[1], parts[2]);
        }
        LOGGER.warn(Localization.get("serverdata.locale.format.invalid"), localeString);
        return Locale.ENGLISH;
    }

    public static void setLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException(Localization.get("serverdata.locale.format.null"));
        }
        data.put("locale", locale.toString());
        saveServerData();
    }
}