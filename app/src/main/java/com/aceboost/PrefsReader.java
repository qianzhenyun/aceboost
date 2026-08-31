package com.aceboost;

import de.robv.android.xposed.XSharedPreferences;

public class PrefsReader {
    private static XSharedPreferences prefs;

    private static synchronized XSharedPreferences get() {
        if (prefs == null) {
            prefs = new XSharedPreferences("com.aceboost", "aceboost_prefs");
        }
        prefs.reload();
        return prefs;
    }

    public static boolean getBool(String key, boolean def) {
        try { return get().getBoolean(key, def); } catch (Throwable t) { return def; }
    }

    public static int getInt(String key, int def) {
        try { return get().getInt(key, def); } catch (Throwable t) { return def; }
    }

    public static String getString(String key, String def) {
        try { return get().getString(key, def); } catch (Throwable t) { return def; }
    }
}
