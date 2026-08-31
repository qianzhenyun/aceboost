package com.aceboost;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android")) {
            try {
                AudioHook.hook(lpparam);
                AntiDetectionHook.hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("AceBoost AudioHook error: " + t);
            }
        }
        if (lpparam.packageName.equals("com.android.systemui")) {
            try {
                StatusBarColorHook.hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("AceBoost StatusBar error: " + t);
            }
        }
        if (lpparam.packageName.equals("com.android.mms") || lpparam.packageName.equals("com.google.android.apps.messaging")) {
            try {
                VerificationCodeHook.hook(lpparam);
            } catch (Throwable t) {
                XposedBridge.log("AceBoost SMS error: " + t);
            }
        }
    }
}
