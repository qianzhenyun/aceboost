package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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

            Class<?> audioService = XposedHelpers.findClass("com.android.server.audio.AudioService", lp.classLoader);
            LogUtil.log("AudioHook found AudioService: " + audioService);

            for (java.lang.reflect.Method m : audioService.getDeclaredMethods()) {
                String n = m.getName();
                Class<?>[] types = m.getParameterTypes();
                if ((n.contains("StreamVolume") || n.contains("streamVolume") || n.contains("adjustStream")) && types.length >= 2) {
                    LogUtil.log("AudioHook candidate method: " + n + " params=" + java.util.Arrays.toString(types));
                }
            }
        } catch (Throwable t) {
            LogUtil.error("AudioHook hook failed", t);
        }
    }
}
