package com.aceboost;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

public class StatusBarColorHook {
    private static String colorHex = "#FFD866";
    private static int alpha = 255;
    private static boolean enabled = true;
    private static boolean applyAlpha = true;

    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        try {
            enabled = PrefsReader.getBool("color_enable", true);
            colorHex = PrefsReader.getString("status_color", "#FFD866");
            alpha = PrefsReader.getInt("status_alpha", 255);
            applyAlpha = PrefsReader.getBool("status_alpha_enable", true);
            LogUtil.log("StatusBarColorHook init enabled=" + enabled + " color=" + colorHex + " alpha=" + alpha);
            if (!enabled) return;

            hookClock(lp);
            hookSignalCluster(lp);
            hookBattery(lp);
            hookStatusIcons(lp);
        } catch (Throwable t) {
            LogUtil.error("StatusBarColorHook failed", t);
        }
    }

    private static int resolveColor() {
        try {
            int base = Color.parseColor(colorHex);
            if (!applyAlpha) return base;
            int a = Math.max(0, Math.min(255, alpha));
            return Color.argb(a, Color.red(base), Color.green(base), Color.blue(base));
        } catch (Throwable t) {
            return Color.parseColor("#FFD866");
        }
    }

    private static void applyTextColor(View view) {
        if (view == null) return;
        try {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(resolveColor());
            }
        } catch (Throwable ignored) {}
    }

    private static void hookClock(final XC_LoadPackage.LoadPackageParam lp) {
        String[] clockClasses = {
            "com.android.systemui.statusbar.policy.Clock",
            "com.android.systemui.statusbar.Clock",
            "android.widget.TextClock"
        };
        for (String name : clockClasses) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        try { applyTextColor((View) param.thisObject); } catch (Throwable ignored) {}
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getName().toLowerCase().contains("time") ||
                        m.getName().toLowerCase().contains("clock") ||
                        m.getName().toLowerCase().contains("update")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                try { applyTextColor((View) param.thisObject); } catch (Throwable ignored) {}
                            }
                        });
                    }
                }
                XposedHelpers.findAndHookMethod(clazz, "onAttachedToWindow", new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        try { applyTextColor((View) param.thisObject); } catch (Throwable ignored) {}
                    }
                });
                LogUtil.log("StatusBarColorHook clock hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookSignalCluster(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.android.systemui.statusbar.SignalClusterView",
            "com.android.systemui.statusbar.phone.SignalClusterView",
            "com.android.keyguard.CarrierText"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyTintRecursive((View) param.thisObject);
                    }
                });
                XposedHelpers.findAndHookMethod(clazz, "onAttachedToWindow", new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyTintRecursive((View) param.thisObject);
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("apply") || n.contains("set") || n.contains("update") || n.contains("refresh")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyTintRecursive((View) param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("StatusBarColorHook signal hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookBattery(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.android.systemui.battery.BatteryMeterView",
            "com.android.systemui.BatteryMeterView"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyTintRecursive((View) param.thisObject);
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("set") || n.contains("update") || n.contains("refresh") || n.contains("onBattery")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyTintRecursive((View) param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("StatusBarColorHook battery hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookStatusIcons(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.android.systemui.statusbar.phone.StatusIconContainer",
            "com.android.systemui.statusbar.StatusIconContainer"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyTintRecursive((View) param.thisObject);
                    }
                });
                LogUtil.log("StatusBarColorHook icons hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void applyTintRecursive(View view) {
        if (view == null) return;
        try {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(resolveColor());
            }
            try {
                if (view instanceof android.view.ViewGroup) {
                    android.view.ViewGroup vg = (android.view.ViewGroup) view;
                    int childCount = vg.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        applyTintRecursive(vg.getChildAt(i));
                    }
                }
            } catch (Throwable ignored) {}
            try {
                Method setColorFilter = view.getClass().getMethod("setColorFilter", int.class);
                setColorFilter.invoke(view, resolveColor());
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
    }
}
