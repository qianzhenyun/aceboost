package com.aceboost;

import android.os.VibrationEffect;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

public class VibrationHook {
    private static boolean enabled = true;
    private static float level = 1.6f;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            enabled = PrefsReader.getBool("vibrate_enable", true);
            level = PrefsReader.getInt("vibrate_level", 160) / 100f;
            LogUtil.log("VibrationHook init enabled=" + enabled + " level=" + level);
            if (!enabled) return;

            Class<?> vibrator = XposedHelpers.findClass("android.os.Vibrator", lp.classLoader);
            for (Method m : vibrator.getDeclaredMethods()) {
                if (m.getName().equals("vibrate")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                for (int i = 0; i < param.args.length; i++) {
                                    if (param.args[i] instanceof VibrationEffect) {
                                        VibrationEffect effect = (VibrationEffect) param.args[i];
                                        param.args[i] = amplifyEffect(effect);
                                    }
                                }
                            } catch (Throwable t) {
                                LogUtil.log("VibrationHook amplify failed: " + t);
                            }
                        }
                    });
                    LogUtil.log("VibrationHook hooked vibrate");
                }
            }

            try {
                Class<?> ve = XposedHelpers.findClass("android.os.VibrationEffect", lp.classLoader);
                for (Method m : ve.getDeclaredMethods()) {
                    if (m.getName().equals("createOneShot")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                try {
                                    if (param.args.length >= 2 && param.args[1] instanceof Integer) {
                                        int amp = (Integer) param.args[1];
                                        param.args[1] = Math.max(1, Math.min(255, (int)(amp * level)));
                                    }
                                } catch (Throwable ignored) {}
                            }
                        });
                        LogUtil.log("VibrationHook hooked createOneShot");
                    } else if (m.getName().equals("createWaveform")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                try {
                                    if (param.args.length >= 2 && param.args[1] instanceof int[]) {
                                        int[] amps = (int[]) param.args[1];
                                        for (int i = 0; i < amps.length; i++) {
                                            amps[i] = Math.max(1, Math.min(255, (int)(amps[i] * level)));
                                        }
                                    }
                                } catch (Throwable ignored) {}
                            }
                        });
                        LogUtil.log("VibrationHook hooked createWaveform");
                    }
                }
            } catch (Throwable t) {
                LogUtil.log("VibrationHook VibrationEffect hook failed: " + t);
            }
        } catch (Throwable t) {
            LogUtil.error("VibrationHook hook failed", t);
        }
    }

    private static VibrationEffect amplifyEffect(VibrationEffect effect) {
        return effect;
    }
}
