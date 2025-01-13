package org.rseconomy.rs_economy;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private static ResourceBundle messages;

    static {
        try {
            // Initialisiere das ResourceBundle mit dem Standardwert
            messages = ResourceBundle.getBundle("locale", ServerDataManager.getLocale());
        } catch (Exception e) {
            messages = ResourceBundle.getBundle("locale", Locale.ENGLISH); // Fallback auf Englisch
        }
    }

    public static String setLocale(Locale locale) {
        try {
            messages = ResourceBundle.getBundle("locale", locale);
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
}