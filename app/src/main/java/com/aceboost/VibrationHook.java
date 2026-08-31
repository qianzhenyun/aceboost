package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VibrationHook {
    private static boolean enabled = true;
    private static float level = 1.6f;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        enabled = PrefsReader.getBool("vibrate_enable", true);
        level = PrefsReader.getInt("vibrate_level", 160) / 100f;
        if (!enabled) return;
        try {
            Class<?> vib = XposedHelpers.findClass("android.os.Vibrator", lp.classLoader);
            XposedHelpers.findAndHookMethod(vib, "vibrate", long.class, android.media.AudioAttributes.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) {
                    try {
                        long ms = (long) p.args[0];
                        p.args[0] = (long) (ms * level);
                    } catch (Throwable ignored) {}
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost Vibration hook error: " + t);
        }
    }
}
