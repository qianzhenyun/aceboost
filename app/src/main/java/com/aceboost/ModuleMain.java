package com.aceboost;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        try {
            if (lp.packageName.equals("android")) {
                AudioHook.hook(lp);
                VibrationHook.hook(lp);
                AntiDetectionHook.hook(lp);
            } else if (lp.packageName.equals("com.android.mms") || lp.packageName.equals("com.google.android.apps.messaging")) {
                VerificationCodeHook.hook(lp);
            }
            ScreenBurnHook.hook(lp);
            VideoCapabilityHook.hook(lp);
        } catch (Throwable t) {
            XposedBridge.log("AceBoost ModuleMain error: " + t);
        }
    }
}
