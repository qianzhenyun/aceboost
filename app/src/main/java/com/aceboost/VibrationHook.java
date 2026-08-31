package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

public class VibrationHook {
    private static boolean enabled = true;
    private static float level = 1.6f;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        loadPrefs();
        if (!enabled) return;
        try {
            Class<?> vib = XposedHelpers.findClass("android.os.Vibrator", lp.classLoader);
            XposedHelpers.findAndHookMethod(vib, "vibrate", long.class, android.media.AudioAttributes.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) {
                    long ms = (long) p.args[0];
                    p.args[0] = (long) (ms * level);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost Vibration hook error: " + t);
        }
    }

    private static void loadPrefs() {
        try {
            File f = new File("/data/data/com.aceboost/shared_prefs/aceboost_prefs.xml");
            if (!f.exists()) return;
            String xml = new String(java.nio.file.Files.readAllBytes(f.toPath()));
            if (xml.contains("\"vibrate_enable\" value=\"false\"")) enabled = false;
            int i = xml.indexOf("\"vibrate_level\"");
            if (i > 0) {
                int j = xml.indexOf('>', i);
                int k = xml.indexOf('<', j);
                if (j > 0 && k > j) {
                    try { level = Integer.parseInt(xml.substring(j + 1, k).trim()) / 100f; } catch (Exception ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }
}
