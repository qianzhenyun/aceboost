package com.aceboost;

import android.media.AudioManager;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

public class AudioHook {
    private static boolean enabled = true;
    private static int volSteps = 60;
    private static int volMax = 201;

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            enabled = PrefsReader.getBool("vol_enable", true);
            volSteps = PrefsReader.getInt("vol_steps", 60);
            volMax = PrefsReader.getInt("vol_max", 201);
            LogUtil.log("AudioHook init enabled=" + enabled + " steps=" + volSteps + " max=" + volMax);
            if (!enabled) return;

            final Class<?> audioService = XposedHelpers.findClass("com.android.server.audio.AudioService", lp.classLoader);
            
            for (Method m : audioService.getDeclaredMethods()) {
                final String name = m.getName();
                if (name.equals("setStreamVolumeInt") || name.equals("setStreamVolume")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                if (param.args.length >= 2 && param.args[1] instanceof Integer) {
                                    int index = (Integer) param.args[1];
                                    int systemMax = getSystemMax(param.args[0]);
                                    int scaled = (int) Math.round((double) index * systemMax / volMax);
                                    param.args[1] = Math.max(0, Math.min(systemMax, scaled));
                                }
                            } catch (Throwable t) {
                                LogUtil.log("AudioHook scale failed: " + t);
                            }
                        }
                    });
                    LogUtil.log("AudioHook hooked: " + name);
                } else if (name.equals("getStreamMaxVolume")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            param.setResult(volMax);
                        }
                    });
                    LogUtil.log("AudioHook hooked max: " + name);
                }
            }
        } catch (Throwable t) {
            LogUtil.error("AudioHook hook failed", t);
        }
    }

    private static int getSystemMax(Object streamTypeObj) {
        try {
            int streamType = (Integer) streamTypeObj;
            switch (streamType) {
                case AudioManager.STREAM_MUSIC: return 15;
                case AudioManager.STREAM_RING: return 7;
                case AudioManager.STREAM_ALARM: return 7;
                case AudioManager.STREAM_VOICE_CALL: return 5;
                case AudioManager.STREAM_SYSTEM: return 7;
                case AudioManager.STREAM_NOTIFICATION: return 7;
                default: return 15;
            }
        } catch (Throwable t) {
            return 15;
        }
    }
}
