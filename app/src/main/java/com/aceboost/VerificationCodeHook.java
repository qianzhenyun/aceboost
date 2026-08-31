package com.aceboost;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VerificationCodeHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lp) {
        try {
            LogUtil.log("VerificationCodeHook init package=" + lp.packageName);
            try {
                Class<?> smsParser = XposedHelpers.findClass("com.android.mms.transaction.SmsReceiverService", lp.classLoader);
                LogUtil.log("VerificationCode found SmsReceiverService: " + smsParser);
            } catch (Throwable t1) {
                LogUtil.log("VerificationCode AOSP SMS class absent: " + t1);
            }
        } catch (Throwable t) {
            LogUtil.error("VerificationCodeHook failed", t);
        }
    }
}
