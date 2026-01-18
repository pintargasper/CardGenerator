package com.gasperpintar.cardgenerator.service.builder;

public class Styles {

    public static String extractStyleValue(String style, String property) {
        if (style == null) {
            return null;
        }

        String[] parts = style.split(";");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2 && keyValue[0].trim().equals(property)) {
                return keyValue[1].trim();
            }
        }
        return null;
    }

    public static String setStyleValue(String style, String property, String value) {
        if (style == null) {
            style = "";
        }

        String[] parts = style.split(";");

        StringBuilder styles = new StringBuilder();
        boolean found = false;

        for (String part : parts) {
            if (part.trim().isEmpty()) {
                continue;
            }

            String[] keyValue = part.split(":");
            if (keyValue.length == 2 && keyValue[0].trim().equals(property)) {
                if (value != null && !value.isEmpty()) {
                    styles.append(property).append(":").append(value).append(";");
                }
                found = true;
            } else {
                styles.append(part).append(";");
            }
        }

        if (!found && value != null && !value.isEmpty()) {
            styles.append(property).append(":").append(value).append(";");
        }
        return styles.toString();
    }
}
