/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */

package org.rseconomy.rs_economy;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private static ResourceBundle messages;

    static {
        // Initialize the ResourceBundle with the default value
        init();
    }

    /**
     * Initializes the ResourceBundle with the current language of the ServerDataManager.
     * Is called with a fallback language when the application is started. And again as soon as the server data has been loaded.
     */
    public static void init() {
        messages = getResourceBundle(ServerDataManager.getLocale());
        //ServerDataManager.LOGGER.debug("Set current locale to: " + messages.getLocale());
    }

    public static String setLocale(Locale locale) {
        try {
            //ServerDataManager.LOGGER.error("Locale: " + locale.toString());
            messages = getResourceBundle(locale);
            ServerDataManager.setLocale(locale);
            return messages.getLocale().getDisplayLanguage();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves a localized message based on the given key and parameters.
     * Falls back to '???key???' if the key is not found.
     *
     * @param key    The localization key.
     * @param params Parameters for formatting the message.
     * @return Localized and formatted string.
     */
    public static String get(String key, Object... params) {
        try {
            String message = messages.getString(key);
            return MessageFormat.format(message, params);
        } catch (Exception e) {
            return null; //Fallback for missing keys
        }
    }

    private static ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle rb = ResourceBundle.getBundle("locale", locale);
        // If the language is not supported, the default language is used
        if(!rb.getLocale().getDisplayLanguage().equals(locale.getDisplayLanguage())){
            rb = ResourceBundle.getBundle("locale", Locale.of("en", "US"));
        }
        return rb;
    }
}