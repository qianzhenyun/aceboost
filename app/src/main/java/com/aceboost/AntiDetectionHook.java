package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AntiDetectionHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            boolean enabled = PrefsReader.getBool("hide_enable", true);
            LogUtil.log("AntiDetectionHook init package=" + lp.packageName + " enabled=" + enabled);
            if (!enabled) return;

            final Class<?> throwable = XposedHelpers.findClass("java.lang.Throwable", lp.classLoader);
            XposedHelpers.findAndHookMethod(throwable, "getStackTrace", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        StackTraceElement[] stack = (StackTraceElement[]) param.getResult();
                        if (stack == null) return;
                        int count = 0;
                        for (StackTraceElement e : stack) {
                            String cn = e.getClassName();
                            if (cn == null) { count++; continue; }
                            if (cn.contains("xposed") || cn.contains("Xposed") ||
                                cn.contains("lsposed") || cn.contains("LSPosed") ||
                                cn.contains("edxposed") || cn.contains("EdXposed") ||
                                cn.contains("de.robv")) {
                                continue;
                            }
                            count++;
                        }
                        StackTraceElement[] filtered = new StackTraceElement[count];
                        int idx = 0;
                        for (StackTraceElement e : stack) {
                            String cn = e.getClassName();
                            if (cn == null) { filtered[idx++] = e; continue; }
                            if (cn.contains("xposed") || cn.contains("Xposed") ||
                                cn.contains("lsposed") || cn.contains("LSPosed") ||
                                cn.contains("edxposed") || cn.contains("EdXposed") ||
                                cn.contains("de.robv")) {
                                continue;
                            }
                            filtered[idx++] = e;
                        }
                        param.setResult(filtered);
                    } catch (Throwable ignored) {}
                }
            });
            LogUtil.log("AntiDetectionHook StackTrace filter installed");

            try {
                final Class<?> file = XposedHelpers.findClass("java.io.File", lp.classLoader);
                XposedHelpers.findAndHookMethod(file, "exists", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        try {
                            String path = param.thisObject.toString();
                            if (path.contains("su") && (path.contains("/system/") || path.contains("/sbin/") || path.contains("/magisk/"))) {
                                param.setResult(false);
                            }
                        } catch (Throwable ignored) {}
                    }
                });
                LogUtil.log("AntiDetectionHook su file filter installed");
            } catch (Throwable t) {
                LogUtil.log("AntiDetectionHook su filter skip: " + t);
            }
        } catch (Throwable t) {
            LogUtil.error("AntiDetectionHook failed", t);
        }
    }
}
