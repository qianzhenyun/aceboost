package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ScreenBurnHook {
    private static boolean enabled = true;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            enabled = PrefsReader.getBool("burnin_enable", true);
            LogUtil.log("ScreenBurnHook init package=" + lp.packageName + " enabled=" + enabled);
            if (!enabled) return;
            LogUtil.log("ScreenBurn protection active for " + lp.packageName);
        } catch (Throwable t) {
            LogUtil.error("ScreenBurnHook failed", t);
        }
    }
}
