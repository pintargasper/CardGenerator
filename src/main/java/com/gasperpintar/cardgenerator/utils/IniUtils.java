package com.gasperpintar.cardgenerator.utils;

import java.io.*;
import java.util.*;

public class IniUtils {

    public static Map<String, Map<String, String>> readIni(File file) throws IOException {
        Map<String, Map<String, String>> result = new HashMap<>();
        if (!file.exists()) {
            return result;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line, section = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) continue;
                if (line.startsWith("[") && line.endsWith("]")) section = line.substring(1, line.length() - 1);
                else if (section != null && line.contains("=")) {
                    int index = line.indexOf("=");
                    result.computeIfAbsent(section, k -> new HashMap<>()).put(line.substring(0, index).trim(), line.substring(index + 1).trim());
                }
            }
        }
        return result;
    }

    public static void writeIni(File file, Map<String, Map<String, String>> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (var sectionEntry : data.entrySet()) {
                writer.write("[" + sectionEntry.getKey() + "]\n");
                for (var entry : sectionEntry.getValue().entrySet()) writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
                writer.write("\n");
            }
        }
    }

    public static String getIniValue(File file, String section, String key, String defaultValue) {
        try {
            return readIni(file).getOrDefault(section, Map.of()).getOrDefault(key, defaultValue);
        }
        catch (IOException ignored) {
            return defaultValue;
        }
    }

    public static void setIniValue(File file, String section, String key, String value) throws IOException {
        var iniData = readIni(file);
        iniData.computeIfAbsent(section, string -> new HashMap<>()).put(key, value);
        writeIni(file, iniData);
    }
}
