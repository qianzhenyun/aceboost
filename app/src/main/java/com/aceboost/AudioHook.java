package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AudioHook {
    private static boolean enabled = true;
    private static int volSteps = 60;
    private static int volMax = 201;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        enabled = PrefsReader.getBool("vol_enable", true);
        volSteps = PrefsReader.getInt("vol_steps", 60);
        volMax = PrefsReader.getInt("vol_max", 201);
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
}
