package com.aceboost;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        if (lp.packageName.equals("android")) {
            try { AudioHook.hook(lp); } catch (Throwable t) { log("Audio", t); }
            try { VibrationHook.hook(lp); } catch (Throwable t) { log("Vibrate", t); }
            try { AntiDetectionHook.hook(lp); } catch (Throwable t) { log("Anti", t); }
        }
        if (lp.packageName.equals("com.android.systemui")) {
            try { StatusBarColorHook.hook(lp); } catch (Throwable t) { log("StatusBar", t); }
        }
        if (lp.packageName.equals("com.android.mms") || lp.packageName.equals("com.google.android.apps.messaging")) {
            try { VerificationCodeHook.hook(lp); } catch (Throwable t) { log("SMS", t); }
        }
        try { ScreenBurnHook.hook(lp); } catch (Throwable t) { log("BurnIn", t); }
    }

    private void log(String tag, Throwable t) {
        XposedBridge.log("AceBoost " + tag + " error: " + t);
    }
}
