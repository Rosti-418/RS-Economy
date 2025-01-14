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
        // Initialisiere das ResourceBundle mit dem Standardwert
        init();
    }

    /**
     * Initialisiert das ResourceBundle mit der aktuellen Sprache des ServerDataManager.
     * Wird beim Start der Anwendung mit eine Fallback-Sprache aufgerufen. Und erneut sobald die Serverdaten geladen wurden.
     */
    public static void init() {
        messages = getResourceBundle(ServerDataManager.getLocale());
        ServerDataManager.LOGGER.debug("Set current locale to: " + messages.getLocale());
    }

    public static String setLocale(Locale locale) {
        try {
            ServerDataManager.LOGGER.error("Locale: " + locale.toString());
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
            return null; // Fallback für fehlende Schlüssel
        }
    }

    private static ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle rb = ResourceBundle.getBundle("locale", locale);
        // Falls die Sprache nicht unterstützt wird, wird auf die Standard-Sprache zurückgegriffen
        if(!rb.getLocale().getDisplayLanguage().equals(locale.getDisplayLanguage())){
            rb = ResourceBundle.getBundle("locale", Locale.of("en", "US"));
        }
        return rb;
    }
}