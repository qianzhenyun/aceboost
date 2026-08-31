package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AntiDetectionHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            LogUtil.log("AntiDetectionHook init package=" + lp.packageName);
            LogUtil.log("AntiDetection basic hide enabled");
        } catch (Throwable t) {
            LogUtil.error("AntiDetectionHook failed", t);
        }
    }
}
