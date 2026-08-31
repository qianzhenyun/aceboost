package com.aceboost;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VerificationCodeHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            Class<?> smsParser = XposedHelpers.findClass("com.android.mms.transaction.SmsReceiverService", lp.classLoader);
            XposedHelpers.findAndHookMethod(smsParser, "storeMessage", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log("AceBoost: SMS message intercepted");
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AceBoost SMS hook missing: " + t);
        }
    }
}
