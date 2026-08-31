package com.aceboost;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class StatusBarColorHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            Class<?> clock = XposedHelpers.findClass("com.android.systemui.statusbar.policy.Clock", lp.classLoader);
            XposedHelpers.findAndHookMethod(clock, "updateClock", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log("AceBoost: clock update intercepted");
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost Clock hook missing: " + t);
        }
    }
}
