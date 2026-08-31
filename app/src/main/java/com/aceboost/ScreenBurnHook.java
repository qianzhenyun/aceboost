package com.aceboost;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ScreenBurnHook {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static float offset = 0f;
    private static int direction = 1;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            boolean enabled = PrefsReader.getBool("burnin_enable", true);
            LogUtil.log("ScreenBurnHook init package=" + lp.packageName + " enabled=" + enabled);
            if (!enabled) return;

            String[] viewClasses = {
                "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                "com.android.systemui.statusbar.phone.StatusBarWindowView",
                "com.android.systemui.statusbar.phone.NavigationBarView",
                "com.android.systemui.navigationbar.NavigationBarView"
            };
            boolean hooked = false;
            for (String name : viewClasses) {
                try {
                    final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                    XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            startBurnProtection((View) param.thisObject);
                        }
                    });
                    XposedHelpers.findAndHookMethod(clazz, "onAttachedToWindow", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            startBurnProtection((View) param.thisObject);
                        }
                    });
                    LogUtil.log("ScreenBurnHook hooked: " + name);
                    hooked = true;
                } catch (Throwable ignored) {}
            }
            if (!hooked) {
                LogUtil.log("ScreenBurnHook no status bar class found, trying generic hook");
            }
        } catch (Throwable t) {
            LogUtil.error("ScreenBurnHook failed", t);
        }
    }

    private static void startBurnProtection(final View view) {
        if (view == null) return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (view.getParent() == null) return;
                    offset += direction * 0.5f;
                    if (offset >= 2f || offset <= -2f) direction *= -1;
                    view.setTranslationX(offset);
                    view.setTranslationY(offset * 0.5f);
                    handler.postDelayed(this, 60000);
                } catch (Throwable ignored) {}
            }
        }, 60000);
    }
}
