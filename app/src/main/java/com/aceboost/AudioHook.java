package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

public class AudioHook {
    private static boolean enabled = true;
    private static int volSteps = 60;
    private static int volMax = 201;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        loadPrefs();
        if (!enabled) return;
        try {
            Class<?> audioService = XposedHelpers.findClass("com.android.server.audio.AudioService", lp.classLoader);
            XposedHelpers.findAndHookMethod(audioService, "adjustStreamVolume", int.class, int.class, int.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) {
                    XposedBridge.log("AceBoost volSteps=" + volSteps + " volMax=" + volMax);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost AudioService error: " + t);
        }
    }

    private static void loadPrefs() {
        try {
            File f = new File("/data/data/com.aceboost/shared_prefs/aceboost_prefs.xml");
            if (!f.exists()) return;
            String xml = new String(java.nio.file.Files.readAllBytes(f.toPath()));
            if (xml.contains("\"vol_enable\" value=\"false\"")) enabled = false;
            volSteps = readInt(xml, "vol_steps", 60);
            volMax = readInt(xml, "vol_max", 201);
        } catch (Throwable ignored) {}
    }

    private static int readInt(String xml, String key, int def) {
        int i = xml.indexOf("\"" + key + "\"");
        if (i > 0) {
            int j = xml.indexOf('>', i);
            int k = xml.indexOf('<', j);
            if (j > 0 && k > j) {
                try { return Integer.parseInt(xml.substring(j + 1, k).trim()); } catch (Exception ignored) {}
            }
        }
        return def;
    }
}
