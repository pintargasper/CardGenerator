package com.gasperpintar.cardgenerator.utils;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Locale;

public class FallbackResourceBundle extends ResourceBundle {

    private final ResourceBundle primary;
    private final ResourceBundle fallback;
    private final Locale locale;

    @Override
    public Enumeration<String> getKeys() {
        Set<String> keys = new HashSet<>(Collections.list(primary.getKeys()));
        if (fallback != null) {
            keys.addAll(Collections.list(fallback.getKeys()));
        }
        return Collections.enumeration(keys);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    protected Object handleGetObject(String key) {
        if (primary.containsKey(key)) {
            return primary.getObject(key);
        } else if (fallback != null && fallback.containsKey(key)) {
            return fallback.getObject(key);
        }
        return null;
    }

    public FallbackResourceBundle(ResourceBundle primary, ResourceBundle fallback, Locale locale) {
        this.primary = primary;
        this.fallback = fallback;
        this.locale = locale;
    }

    @SuppressWarnings("unused")
    public FallbackResourceBundle(ResourceBundle primary, ResourceBundle fallback) {
        this(primary, fallback, primary.getLocale());
    }
}
