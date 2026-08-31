package com.aceboost;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class StatusBarColorHook {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static String colorHex = "#FFD866";
    private static boolean enabled = true;

    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        enabled = PrefsReader.getBool("color_enable", true);
        colorHex = PrefsReader.getString("status_color", "#FFD866");
        try {
            final Class<?> clock = XposedHelpers.findClass("com.android.systemui.statusbar.policy.Clock", lp.classLoader);
            XposedHelpers.findAndHookMethod(clock, "onAttachedToWindow", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) {
                    applyColor(param.thisObject);
                }
            });
            XposedHelpers.findAndHookMethod(clock, "updateClock", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) {
                    if (enabled) HANDLER.postDelayed(() -> applyColor(param.thisObject), 500);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost StatusBar hook error: " + t);
        }
    }

    private static void applyColor(Object obj) {
        try {
            if (!enabled || obj == null) return;
            android.widget.TextView tv = (android.widget.TextView) obj;
            tv.setTextColor(Color.parseColor(colorHex));
            if (tv.getPaint() != null) tv.getPaint().setShader(null);
            tv.invalidate();
        } catch (Throwable t) {
            XposedBridge.log("AceBoost applyColor error: " + t);
        }
    }
}
