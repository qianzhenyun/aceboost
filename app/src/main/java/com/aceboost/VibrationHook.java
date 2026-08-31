package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VibrationHook {
    private static boolean enabled = true;
    private static float level = 1.6f;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            enabled = PrefsReader.getBool("vibrate_enable", true);
            level = PrefsReader.getInt("vibrate_level", 160) / 100f;
            LogUtil.log("VibrationHook init enabled=" + enabled + " level=" + level);
            if (!enabled) return;

            Class<?> vib = XposedHelpers.findClass("android.os.Vibrator", lp.classLoader);
            LogUtil.log("VibrationHook found Vibrator: " + vib);
            for (java.lang.reflect.Method m : vib.getDeclaredMethods()) {
                if (m.getName().equals("vibrate")) {
                    LogUtil.log("VibrationHook candidate: " + m.getName() + " params=" + java.util.Arrays.toString(m.getParameterTypes()));
                }
            }
        } catch (Throwable t) {
            LogUtil.error("VibrationHook failed", t);
        }
    }
}
