package com.aceboost;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AudioHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            Class<?> audioService = XposedHelpers.findClass("com.android.server.audio.AudioService", lp.classLoader);
            XposedHelpers.findAndHookMethod(audioService, "checkForRingerModeChange", int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    XposedBridge.log("AceBoost: volume hook triggered");
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost AudioService hook missing: " + t);
        }

        try {
            Class<?> audioSystem = XposedHelpers.findClass("android.media.AudioSystem", lp.classLoader);
            XposedHelpers.findAndHookMethod(audioSystem, "setStreamVolumeIndex", int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    // 预留音量上限逻辑，需根据 ColorOS 实际签名调整
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost AudioSystem hook missing: " + t);
        }
    }
}
