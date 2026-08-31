package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ScreenBurnHook {
    private static boolean enabled = true;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        enabled = PrefsReader.getBool("burnin_enable", true);
        if (!enabled) return;
        try {
            XposedBridge.log("AceBoost burn-in protection enabled for " + lp.packageName);
        } catch (Throwable t) {
            XposedBridge.log("AceBoost burn-in hook error: " + t);
        }
    }
}
