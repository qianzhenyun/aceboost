package com.aceboost;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
            LogUtil.log("StatusBarColorHook v2 init enabled=" + enabled + " color=" + colorHex + " alpha=" + alpha);
            if (!enabled) return;

            hookTextClock(lp);
            hookOplusIcons(lp);
            hookBatteryIcon(lp);
            hookWifiSignal(lp);
            hookStatusBarIconContainer(lp);
        } catch (Throwable t) {
            LogUtil.error("StatusBarColorHook v2 failed", t);
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

    private static void hookTextClock(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.android.systemui.statusbar.policy.Clock",
            "com.android.systemui.statusbar.Clock",
            "com.oplus.systemui.statusbar.widget.StatClock"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyTextColor(param.thisObject);
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("update") || n.contains("Set") || n.contains("set") || n.contains("Refresh") || n.contains("refresh")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyTextColor(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("ColorHook clock hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookOplusIcons(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.oplus.systemui.statusbar.phone.OplusStatusBarIcon",
            "com.oplus.systemui.statusbar.widget.StatIconView",
            "com.android.systemui.statusbar.StatusBarIconView"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyIconTint(param.thisObject);
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("Update") || n.contains("OnIcon") || n.contains("set") || n.contains("Set") || n.contains("Draw")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyIconTint(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("ColorHook icon hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookBatteryIcon(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.oplus.systemui.statusbar.util.StatusBarHelper",
            "com.oplus.systemui.statusbar.util.StatusBarViewUtil",
            "com.android.systemui.battery.BatteryMeterView"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("Icon") || n.contains("Battery") || n.contains("Tint") || n.contains("Update")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyIconTint(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("ColorHook battery hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookWifiSignal(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.oplus.systemui.qs.utils.QsWifiIcons",
            "com.android.systemui.statusbar.connectivity.WifiIconsEx",
            "com.oplus.systemui.statusbar.policy.MobileIconSets",
            "com.oplus.systemui.statusbar.policy.TelephonyIcons"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("Icon") || n.contains("Color") || n.contains("Tint")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyIconTint(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("ColorHook wifi/signal hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookStatusBarIconContainer(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.oplus.systemui.privacy.OplusPrivacyIconContainer",
            "com.android.systemui.statusbar.phone.StatusIconContainer",
            "com.android.systemui.statusbar.StatusIconContainer"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) {
                        applyIconTint(param.thisObject);
                    }
                });
                LogUtil.log("ColorHook container hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void applyTextColor(Object o) {
        try {
            if (o instanceof TextView) {
                ((TextView) o).setTextColor(resolveColor());
            }
        } catch (Throwable ignored) {}
    }

    private static void applyIconTint(Object o) {
        try {
            if (o instanceof TextView) {
                ((TextView) o).setTextColor(resolveColor());
            }
            Method getSelected = o.getClass().getMethod("getSelectedIconData");
            Object iconData = null;
            try { iconData = getSelected.invoke(o); } catch (Throwable ignored) {}
            Method setTint = o.getClass().getMethod("setColorFilter", int.class);
            if (setTint != null) {
                setTint.invoke(o, resolveColor());
            }
        } catch (Throwable ignored) {}
    }
}
