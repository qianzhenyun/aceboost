package com.aceboost;

import android.content.ClipboardManager;
import android.content.Context;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

public class VerificationCodeHook {
    public static void hook(final XC_LoadPackage.LoadPackageParam lp) {
        try {
            boolean copyEnabled = PrefsReader.getBool("sms_copy", true);
            boolean fillEnabled = PrefsReader.getBool("sms_fill", false);
            LogUtil.log("VerificationCodeHook init package=" + lp.packageName + " copy=" + copyEnabled + " fill=" + fillEnabled);
            if (!copyEnabled && !fillEnabled) return;

            try {
                final Class<?> smsService = XposedHelpers.findClass("com.android.mms.transaction.SmsReceiverService", lp.classLoader);
                for (Method m : smsService.getDeclaredMethods()) {
                    String n = m.getName();
                    if (n.contains("store") || n.contains("handle") || n.contains("Message")) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                extractAndCopy(param.args, lp);
                            }
                        });
                    }
                }
                LogUtil.log("VerificationCodeHook sms service hooked");
            } catch (Throwable t1) {
                LogUtil.log("VerificationCodeHook sms service skip: " + t1);
            }

            try {
                final Class<?> smsProvider = XposedHelpers.findClass("com.android.providers.telephony.SmsProvider", lp.classLoader);
                XposedHelpers.findAndHookMethod(smsProvider, "insert", android.net.Uri.class, android.content.ContentValues.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (param.args.length >= 2 && param.args[1] instanceof android.content.ContentValues) {
                            android.content.ContentValues cv = (android.content.ContentValues) param.args[1];
                            String body = cv.getAsString("body");
                            copyCode(body, lp);
                        }
                    }
                });
                LogUtil.log("VerificationCodeHook sms provider hooked");
            } catch (Throwable t2) {
                LogUtil.log("VerificationCodeHook sms provider skip: " + t2);
            }
        } catch (Throwable t) {
            LogUtil.error("VerificationCodeHook failed", t);
        }
    }

    private static void extractAndCopy(Object[] args, XC_LoadPackage.LoadPackageParam lp) {
        if (args == null) return;
        for (Object arg : args) {
            if (arg == null) continue;
            if (arg instanceof String) {
                copyCode((String) arg, lp);
            } else if (arg instanceof byte[]) {
                try {
                    String decoded = new String((byte[]) arg, "UTF-8");
                    copyCode(decoded, lp);
                } catch (Throwable ignored) {}
            }
        }
    }

    private static void copyCode(String text, XC_LoadPackage.LoadPackageParam lp) {
        if (text == null || text.isEmpty()) return;
        try {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?<![0-9])([0-9]{4,8})(?![0-9])");
            java.util.regex.Matcher m = p.matcher(text);
            while (m.find()) {
                String code = m.group(1);
                if (isLikelyCode(text, code)) {
                    copyToClipboard(lp, code);
                    return;
                }
            }
        } catch (Throwable ignored) {}
    }

    private static boolean isLikelyCode(String text, String code) {
        String upper = text.toUpperCase();
        return upper.contains("验证码") || upper.contains("码") || upper.contains("CODE") ||
               upper.contains("OTP") || upper.contains("PIN") || upper.contains("动态");
    }

    private static void copyToClipboard(XC_LoadPackage.LoadPackageParam lp, String code) {
        try {
            Context ctx = (Context) XposedHelpers.callStaticMethod(
                XposedHelpers.findClass("android.app.ActivityThread", lp.classLoader),
                "currentApplication");
            ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(code);
            LogUtil.log("VerificationCodeHook copied: " + code);
        } catch (Throwable t) {
            LogUtil.log("VerificationCodeHook clipboard failed: " + t);
        }
    }
}
