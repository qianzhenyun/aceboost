package com.aceboost;

import android.app.Activity;
import android.app.Instrumentation;
import android.util.Log;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class LiquidGlassModule {
    static final String TAG = "LiquidGlass";
    private static volatile HostApp sApp;
    private static volatile int sResumeHits;

    static HostApp app() { return sApp; }
    static void setApp(HostApp app) { sApp = app; }

    interface InterceptCallback { Object intercept(Chain chain) throws Throwable; }
    interface AfterCallback { void after(Chain chain) throws Throwable; }

    static class Chain {
        private final XC_MethodHook.MethodHookParam param;
        Chain(XC_MethodHook.MethodHookParam param) { this.param = param; }
        Object proceed() throws Throwable {
            return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
        }
        Object getThisObject() { return param.thisObject; }
        Object getArg(int i) { return param.args[i]; }
    }

    static void hookAfter(Member member, AfterCallback fn) {
        try {
            XposedBridge.hookMethod(member, new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try { fn.after(new Chain(param)); } catch (Throwable t) { logErr("after-hook failed", t); }
                }
            });
        } catch (Throwable t) { logErr("hookAfter failed", t); }
    }

    static void hookIntercept(Member member, InterceptCallback fn) {
        try {
            XposedBridge.hookMethod(member, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object result = fn.intercept(new Chain(param));
                        param.setResult(result);
                    } catch (Throwable t) { logErr("intercept hook failed", t); }
                }
            });
        } catch (Throwable t) { logErr("hookIntercept failed", t); }
    }

    static void install(ClassLoader cl) {
        HostApp app = sApp;
        if (app == null) return;
        try {
            Method callOnResume = Instrumentation.class.getMethod("callActivityOnResume", Activity.class);
            XposedBridge.hookMethod(callOnResume, new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object arg0 = param.args[0];
                    if (arg0 instanceof Activity) {
                        Activity activity = (Activity) arg0;
                        if (app.launcherActivity.equals(activity.getClass().getName())) {
                            GlassConfig.load(activity);
                            sResumeHits++;
                            if (sResumeHits <= 3 || sResumeHits % 20 == 0) {
                                log(Log.INFO, "home activity onResume #" + sResumeHits);
                            }
                            LiquidGlassInstaller.scheduleInstall(activity);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            logErr("install resume hook failed", t);
        }
        TabBarBridge.install(app, cl);
    }

    static void log(int prio, String msg) { Log.println(prio, TAG, msg); }
    static void logErr(String msg, Throwable t) { Log.e(TAG, msg, t); }
}
