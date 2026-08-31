package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class StatusBarColorHook {
    private static String colorHex = "#FFD866";
    private static boolean enabled = true;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            enabled = PrefsReader.getBool("color_enable", true);
            colorHex = PrefsReader.getString("status_color", "#FFD866");
            LogUtil.log("StatusBarColorHook init enabled=" + enabled + " color=" + colorHex);
            if (!enabled) return;

            try {
                Class<?> clock = XposedHelpers.findClass("com.android.systemui.statusbar.policy.Clock", lp.classLoader);
                LogUtil.log("StatusBar found AOSP Clock class: " + clock);
            } catch (Throwable t1) {
                LogUtil.log("StatusBar AOSP Clock absent, searching ColorOS classes...");
                java.util.Enumeration<java.net.URL> urls = lp.classLoader.getResources("");
                LogUtil.log("StatusBar classloader resources done");
            }

            Class<?>[] all = null;
            try {
                java.lang.reflect.Field f = lp.classLoader.getClass().getDeclaredField("classes");
                f.setAccessible(true);
                Object v = f.get(lp.classLoader);
                LogUtil.log("StatusBar classloader internal: " + v);
            } catch (Throwable t2) {
                LogUtil.log("StatusBar cannot list classes: " + t2);
            }
        } catch (Throwable t) {
            LogUtil.error("StatusBarColorHook failed", t);
        }
    }
}
