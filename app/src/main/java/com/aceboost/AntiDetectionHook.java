package com.aceboost;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AntiDetectionHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            XposedHelpers.setStaticBooleanField(XposedHelpers.findClass("de.robv.android.xposed.XposedBridge", lp.classLoader), "disableHooks", true);
        } catch (Throwable t) {
            XposedBridge.log("AceBoost anti-detect skip: " + t);
        }
    }
}
