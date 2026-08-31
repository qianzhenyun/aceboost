package com.aceboost;

import android.graphics.Color;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Method;

public class StatusBarColorHook {
    private static boolean rainbowEnabled = true;
    private static boolean breathEnabled = false;
    private static boolean goldEnabled = false;

    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        try {
            rainbowEnabled = PrefsReader.getBool("rainbow_enable", true);
            breathEnabled = PrefsReader.getBool("rainbow_breath", false);
            goldEnabled = PrefsReader.getBool("gold_enable", false);
            LogUtil.log("RainbowHook init rainbow=" + rainbowEnabled + " breath=" + breathEnabled + " gold=" + goldEnabled);
            if (!rainbowEnabled && !goldEnabled) return;
            hookTextClock(lp);
            hookOplusIcons(lp);
            hookBatteryIcon(lp);
            hookWifiSignal(lp);
            hookStatusBarIconContainer(lp);
            hookMoreIcons(lp);
        } catch (Throwable t) {
            LogUtil.error("RainbowHook failed", t);
        }
    }

    private static int resolveColor() {
        if (goldEnabled && !rainbowEnabled) return Color.parseColor("#FFD866");
        long step = breathEnabled ? 120L : 40L;
        float hue = (System.currentTimeMillis() % (360 * step)) / (float) step;
        return Color.HSVToColor(255, new float[]{hue, 0.75f, 1.0f});
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
                    if (n.contains("update") || n.contains("set") || n.contains("refresh")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyTextColor(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("RainbowHook clock hooked: " + name);
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
                    if (n.contains("Update") || n.contains("set") || n.contains("Draw") || n.contains("Icon")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyIconTint(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("RainbowHook icons hooked: " + name);
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
                LogUtil.log("RainbowHook battery hooked: " + name);
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
                LogUtil.log("RainbowHook wifi/signal hooked: " + name);
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
                LogUtil.log("RainbowHook container hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }

    private static void hookMoreIcons(final XC_LoadPackage.LoadPackageParam lp) {
        String[] names = {
            "com.android.systemui.statusbar.policy.BluetoothControllerImpl",
            "com.android.systemui.qs.tiles.BluetoothTile",
            "com.android.systemui.statusbar.policy.AirplaneModeControllerImpl",
            "com.android.systemui.statusbar.policy.HotspotControllerImpl",
            "com.android.systemui.statusbar.policy.DataSaverControllerImpl",
            "com.android.systemui.statusbar.policy.NfcController",
            "com.android.systemui.statusbar.policy.RotationLockControllerImpl",
            "com.android.systemui.statusbar.policy.DndControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusDndControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusBluetoothControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusAirplaneControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusHotspotControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusNfcControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusDataSaverControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusVpnControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusVolteControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusLocationControllerImpl",
            "com.oplus.systemui.statusbar.policy.OplusHeadsetControllerImpl"
        };
        for (String name : names) {
            try {
                final Class<?> clazz = XposedHelpers.findClass(name, lp.classLoader);
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("Icon") || n.contains("Color") || n.contains("Tint") || n.contains("Update")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                applyIconTint(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("RainbowHook more hooked: " + name);
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
            Method setColorFilter = o.getClass().getMethod("setColorFilter", int.class);
            setColorFilter.invoke(o, resolveColor());
        } catch (Throwable ignored) {}
    }
}
