package com.aceboost;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;
import java.util.Map;

public class StatusBarColorHook {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static String colorHex = "#FFD866";
    private static boolean enabled = true;

    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        loadPrefs();
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
            if (!enabled) return;
            android.widget.TextView tv = (android.widget.TextView) obj;
            int c = Color.parseColor(colorHex);
            tv.setTextColor(c);
            if (tv.getPaint() != null) tv.getPaint().setShader(null);
            tv.invalidate();
        } catch (Throwable t) {
            XposedBridge.log("AceBoost applyColor error: " + t);
        }
    }

    private static void loadPrefs() {
        try {
            File f = new File("/data/data/com.aceboost/shared_prefs/aceboost_prefs.xml");
            if (!f.exists()) return;
            String xml = new String(java.nio.file.Files.readAllBytes(f.toPath()));
            if (xml.contains("\"color_enable\" value=\"false\"")) enabled = false;
            int i = xml.indexOf("\"status_color\"");
            if (i > 0) {
                int j = xml.indexOf('>', i);
                int k = xml.indexOf('<', j);
                if (j > 0 && k > j) colorHex = xml.substring(j + 1, k).trim();
            }
        } catch (Throwable ignored) {}
    }
}
