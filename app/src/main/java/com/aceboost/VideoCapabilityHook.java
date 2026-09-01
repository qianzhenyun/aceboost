package com.aceboost;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VideoCapabilityHook {
    private static final String PKG = "com.aceboost";
    private static final String PREFS = "aceboost_prefs";
    private static final String KEY = "video_4k_enable";

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            Class<?> c = XposedHelpers.findClass("android.media.MediaCodecInfo$VideoCapabilities", lp.classLoader);
            XposedHelpers.findAndHookMethod(c, "isSizeSupported", int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    try {
                        XSharedPreferences sp = new XSharedPreferences(PKG, PREFS);
                        sp.reload();
                        if (!sp.getBoolean(KEY, false)) return;
                        int w = (int) param.args[0];
                        int h = (int) param.args[1];
                        if (w > 0 && h > 0 && w <= 3840 && h <= 2160) {
                            param.setResult(true);
                        }
                    } catch (Throwable ignore) {}
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost 4K hook error: " + t);
        }
    }
}
