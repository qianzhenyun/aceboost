package com.aceboost;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StatusBarColorHook {
    private static boolean rainbowEnabled = true;
    private static boolean flowEnabled = true;
    private static boolean goldEnabled = false;
    private static final List<WeakReference<Object>> views = new ArrayList<>();
    private static final List<Integer> phases = new ArrayList<>();
    private static Handler handler;
    private static Runnable ticker;
    private static long startTime = 0;
    private static int phaseCounter = 0;

    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        try {
            rainbowEnabled = PrefsReader.getBool("rainbow_enable", true);
            flowEnabled = PrefsReader.getBool("rainbow_breath", false);
            goldEnabled = PrefsReader.getBool("gold_enable", false);
            LogUtil.log("MarqueeHook init rainbow=" + rainbowEnabled + " flow=" + flowEnabled + " gold=" + goldEnabled);
            if (!rainbowEnabled && !goldEnabled) return;
            startTime = System.currentTimeMillis();
            hookTextClock(lp);
            hookOplusIcons(lp);
            hookBatteryIcon(lp);
            hookWifiSignal(lp);
            hookStatusBarIconContainer(lp);
            hookMoreIcons(lp);
            startTicker();
        } catch (Throwable t) {
            LogUtil.error("MarqueeHook failed", t);
        }
    }

    private static int resolveColor(int phase) {
        if (goldEnabled && !rainbowEnabled) return Color.parseColor("#FFD866");
        long elapsed = System.currentTimeMillis() - startTime;
        float speed = flowEnabled ? 0.35f : 0.18f;
        float hue = ((elapsed * speed) + phase) % 360f;
        return Color.HSVToColor(255, new float[]{hue, 0.85f, 1.0f});
    }

    private static void startTicker() {
        if (handler != null) return;
        handler = new Handler(Looper.getMainLooper());
        ticker = new Runnable() {
            @Override public void run() {
                synchronized (views) {
                    for (int i = views.size() - 1; i >= 0; i--) {
                        Object o = views.get(i).get();
                        if (o == null) {
                            views.remove(i);
                            phases.remove(i);
                            continue;
                        }
                        int phase = i < phases.size() ? phases.get(i) : 0;
                        applyColorTo(o, resolveColor(phase));
                    }
                }
                handler.postDelayed(this, 50);
            }
        };
        handler.postDelayed(ticker, 50);
    }

    private static void addView(Object o) {
        if (o == null) return;
        synchronized (views) {
            views.add(new WeakReference<>(o));
            phases.add((phaseCounter++ * 45) % 360);
        }
    }

    private static void applyColorTo(Object o, int color) {
        try {
            if (o instanceof TextView) {
                ((TextView) o).setTextColor(color);
            }
            Method setColorFilter = o.getClass().getMethod("setColorFilter", int.class);
            setColorFilter.invoke(o, color);
        } catch (Throwable ignored) {}
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
                        addView(param.thisObject);
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("update") || n.contains("set") || n.contains("refresh")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                addView(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("MarqueeHook clock hooked: " + name);
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
                        addView(param.thisObject);
                    }
                });
                for (Method m : clazz.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("update") || n.contains("set") || n.contains("draw") || n.contains("Icon")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override protected void afterHookedMethod(MethodHookParam param) {
                                addView(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("MarqueeHook icons hooked: " + name);
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
                                addView(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("MarqueeHook battery hooked: " + name);
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
                                addView(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("MarqueeHook wifi/signal hooked: " + name);
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
                        addView(param.thisObject);
                    }
                });
                LogUtil.log("MarqueeHook container hooked: " + name);
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
                                addView(param.thisObject);
                            }
                        });
                    }
                }
                LogUtil.log("MarqueeHook more hooked: " + name);
            } catch (Throwable ignored) {}
        }
    }
}
