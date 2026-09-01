package com.aceboost;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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

            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(GRAYSCALE);

            XposedHelpers.findAndHookMethod("android.view.ViewRootImpl", lp.classLoader,
                "setView", View.class, int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        try {
                            View root = (View) param.args[0];
                            if (root != null) {
                                root.setLayerType(View.LAYER_TYPE_HARDWARE, filter);
                            }
                        } catch (Throwable ignored) {}
                    }
                });

            LogUtil.log("PaperEyeHook hooked ViewRootImpl for " + lp.packageName);
        } catch (Throwable t) {
            LogUtil.error("PaperEyeHook failed", t);
        }
    }
}
