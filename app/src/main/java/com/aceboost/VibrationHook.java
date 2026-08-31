package com.aceboost;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VibrationHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            XposedHelpers.findAndHookMethod("android.os.Vibrator", lp.classLoader, "vibrate", long.class, new Object[]{null});
        } catch (Throwable t) {
            XposedBridge.log("AceBoost vibration hook pending: " + t);
        }
    }
}
