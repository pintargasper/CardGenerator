package com.gasperpintar.cardgenerator.service.generator;

import com.gasperpintar.cardgenerator.model.Settings;

public class CardFormatUtil {
    private CardFormatUtil() {}

    public static Settings setupFormat(String format) {
        Settings settings = new Settings();
        settings.cmToInch = 2.54;

        if ("13x18".equals(format)) {
            settings.dpi = 300;
            settings.numCols = 2;
            settings.numRows = 2;
            settings.scaleFactor = 3.0;
            settings.cardWidthCm = 13.0;
            settings.cardHeightCm = 18.0;
        } else {
            return null;
        }
        return settings;
    }
}

