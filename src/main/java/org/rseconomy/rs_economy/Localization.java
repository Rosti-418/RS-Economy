/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Handles localization for the RSEconomy mod, providing translated messages.
 */
public class Localization {
    private static ResourceBundle messages;

    static {
        init();
    }

    /**
     * Initializes the ResourceBundle with the configured locale.
     */
    public static void init() {
        messages = getResourceBundle(Locale.of(ModConfigs.LOCALE.get()));
    }

    /**
     * Sets the locale for localization and updates the configuration.
     *
     * @param locale The new locale to set.
     * @return The display name of the new locale, or null if the locale is invalid.
     */
    public static String setLocale(Locale locale) {
        if (locale == null) {
            return null;
        }
        try {
            messages = getResourceBundle(locale);
            ModConfigs.LOCALE.set(locale.toString());
            return messages.getLocale().getDisplayLanguage();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves a localized message for the given key, formatted with parameters.
     *
     * @param key    The localization key.
     * @param params Parameters for formatting the message.
     * @return The localized and formatted string, or the key itself if not found.
     */
    public static String get(String key, Object... params) {
        try {
            if (messages == null) {
                init();
            }
            String message = messages.getString(key);
            return MessageFormat.format(message, params);
        } catch (Exception e) {
            return key; // Return the key instead of null for better debugging
        }
    }

    /**
     * Loads a ResourceBundle for the specified locale, falling back to en_US if unavailable.
     *
     * @param locale The desired locale.
     * @return The loaded ResourceBundle.
     */
    private static ResourceBundle getResourceBundle(Locale locale) {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("locale", locale);
            if (!rb.getLocale().getLanguage().equals(locale.getLanguage())) {
                rb = ResourceBundle.getBundle("locale", Locale.of("en", "US"));
            }
            return rb;
        } catch (MissingResourceException e) {
            return ResourceBundle.getBundle("locale", Locale.of("en", "US"));
        }
    }
}