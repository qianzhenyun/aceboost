package com.aceboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PrefsReader {
    public static String read() {
        try {
            File f = new File("/data/data/com.aceboost/shared_prefs/aceboost_prefs.xml");
            if (!f.exists()) return "";
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            br.close();
            return sb.toString();
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static boolean getBool(String key, boolean def) {
        String xml = read();
        if (xml.contains("\"" + key + "\" value=\"false\"")) return false;
        if (xml.contains("\"" + key + "\" value=\"true\"")) return true;
        return def;
    }

    public static int getInt(String key, int def) {
        String xml = read();
        int i = xml.indexOf("\"" + key + "\"");
        if (i < 0) return def;
        int j = xml.indexOf('>', i);
        int k = xml.indexOf('<', j);
        if (j < 0 || k <= j) return def;
        try { return Integer.parseInt(xml.substring(j + 1, k).trim()); } catch (Throwable ignored) { return def; }
    }

    public static String getString(String key, String def) {
        String xml = read();
        int i = xml.indexOf("\"" + key + "\"");
        if (i < 0) return def;
        int j = xml.indexOf('>', i);
        int k = xml.indexOf('<', j);
        if (j < 0 || k <= j) return def;
        return xml.substring(j + 1, k).trim();
    }
}
