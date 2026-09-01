package com.aceboost;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        try {
            String pkg = lp.packageName;
            if (pkg.equals("com.tencent.mm") || pkg.equals("com.tencent.mobileqq")) {
                if (PrefsReader.getBool("liquid_glass_enable", false)) {
                    HostApp app = HostApp.forPackage(pkg);
                    if (app != null) {
                        LiquidGlassModule.setApp(app);
                        LiquidGlassModule.install(lp.classLoader);
                    }
                }
            }

            if (pkg.equals("android")) {
                AudioHook.hook(lp);
                VibrationHook.hook(lp);
                AntiDetectionHook.hook(lp);
            } else if (pkg.equals("com.android.mms") || pkg.equals("com.google.android.apps.messaging")) {
                VerificationCodeHook.hook(lp);
            }
            ScreenBurnHook.hook(lp);
            VideoCapabilityHook.hook(lp);
        } catch (Throwable t) {
            XposedBridge.log("AceBoost ModuleMain error: " + t);
        }
    }
}
