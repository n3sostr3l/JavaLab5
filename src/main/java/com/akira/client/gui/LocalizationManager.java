package com.akira.client.gui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Менеджер локализации. Поддерживает переключение языков без перезапуска приложения.
 */
public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;

    private static final Locale[] SUPPORTED_LOCALES = {
        new Locale("en", "IN"),
        new Locale("ru", "RU"),
        new Locale("be", "BY"),
        new Locale("sq", "AL")
    };

    private static final String[] LOCALE_NAMES = {
        "English (India)",
        "Русский",
        "Беларуская",
        "Shqip"
    };

    private LocalizationManager() {
        setLocale(SUPPORTED_LOCALES[0]);
    }

    public static synchronized LocalizationManager getInstance() {
        if (instance == null) instance = new LocalizationManager();
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        try {
            bundle = ResourceBundle.getBundle("i18n.strings", locale);
        } catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle("i18n.strings", SUPPORTED_LOCALES[0]);
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public String format(String key, Object... args) {
        return MessageFormat.format(getString(key), args);
    }

    public static Locale[] getSupportedLocales() {
        return SUPPORTED_LOCALES;
    }

    public static String[] getLocaleNames() {
        return LOCALE_NAMES;
    }

    public int getLocaleIndex() {
        for (int i = 0; i < SUPPORTED_LOCALES.length; i++) {
            if (SUPPORTED_LOCALES[i].equals(currentLocale)) return i;
        }
        return 0;
    }
}