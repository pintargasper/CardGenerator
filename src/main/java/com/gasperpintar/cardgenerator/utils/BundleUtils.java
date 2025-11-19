package com.gasperpintar.cardgenerator.utils;

import com.gasperpintar.cardgenerator.CardGenerator;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class BundleUtils {

    public static ResourceBundle getBundle() {
        return CardGenerator.getResourceBundle();
    }

    public static String getString(String key, Object... params) {
        String pattern = getBundle().getString(key);
        return params.length > 0 ? MessageFormat.format(pattern, params) : pattern;
    }
}
