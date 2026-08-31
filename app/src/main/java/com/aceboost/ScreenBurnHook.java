package com.aceboost;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Field;

public class ScreenBurnHook {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        try {
            final Class<?> viewRoot = XposedHelpers.findClass("android.view.ViewRootImpl", lp.classLoader);
            XposedBridge.hookAllConstructors(viewRoot, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        Field f = XposedHelpers.findField(viewRoot, "mView");
                        final View view = (View) f.get(param.thisObject);
                        if (view != null) scheduleShift(view);
                    } catch (Throwable t) {
                        XposedBridge.log("AceBoost burnin view error: " + t);
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost burnin hook error: " + t);
        }
    }

    private static void scheduleShift(final View view) {
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    float dx = (float) ((Math.random() * 4.0) - 2.0);
                    float dy = (float) ((Math.random() * 4.0) - 2.0);
                    view.setTranslationX(dx);
                    view.setTranslationY(dy);
                } catch (Throwable ignored) {}
                HANDLER.postDelayed(this, 3000);
            }
        }, 3000);
    }
}
