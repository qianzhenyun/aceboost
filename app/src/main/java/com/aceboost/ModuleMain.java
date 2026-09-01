package com.aceboost;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        try {
            String pkg = lp.packageName;

            // 全局灰阶墨水屏需要在所有目标进程生效，包括微信/QQ 子进程，
            // 所以必须在子进程 early return 之前注册。
            PaperEyeHook.hook(lp);

            HostApp targetApp = HostApp.forPackage(pkg);
            boolean isSupported = targetApp != null;

            // 已支持应用只在主进程注入液态玻璃；子进程直接跳过，避免重复 Hook。
            if (isSupported && !lp.processName.equals(pkg)) {
                return;
            }

            if (isSupported) {
                boolean enabled = PrefsReader.getBool("liquid_glass_enable", true);
                XposedBridge.log("AceBoost LG branch pkg=" + pkg + " enabled=" + enabled);
                if (enabled) {
                    LiquidGlassModule.setApp(targetApp);
                    LiquidGlassModule.install(lp.classLoader);
                    XposedBridge.log("AceBoost LG install called for " + pkg);
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
