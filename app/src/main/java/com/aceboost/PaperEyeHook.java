package com.aceboost;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

public class PaperEyeHook {
    private static final ColorMatrix GRAYSCALE = new ColorMatrix();
    static {
        GRAYSCALE.setSaturation(0f);
    }

    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            boolean enabled = PrefsReader.getBool("paper_eye_enable", false);
            LogUtil.log("PaperEyeHook init package=" + lp.packageName + " enabled=" + enabled);
            if (!enabled) return;

            final Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(GRAYSCALE));

            Class<?> vri = XposedHelpers.findClass("android.view.ViewRootImpl", lp.classLoader);
            int hooked = 0;
            for (Method m : vri.getDeclaredMethods()) {
                if (!"setView".equals(m.getName())) {
                    continue;
                }
                Class<?>[] ps = m.getParameterTypes();
                if (ps.length == 0 || ps[0] != View.class) {
                    continue;
                }
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        try {
                            if (param.args.length > 0 && param.args[0] instanceof View) {
                                View root = (View) param.args[0];
                                if (root != null) {
                                    root.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
                                }
                            }
                        } catch (Throwable ignored) {}
                    }
                });
                hooked++;
            }

            LogUtil.log("PaperEyeHook hooked setView x" + hooked + " for " + lp.packageName);
        } catch (Throwable t) {
            LogUtil.error("PaperEyeHook failed", t);
        }
    }
}
