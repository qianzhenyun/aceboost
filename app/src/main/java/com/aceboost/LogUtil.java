package com.aceboost;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import de.robv.android.xposed.XposedBridge;

public class LogUtil {
    private static final String TAG = "AceBoost";
    private static final String LOG_DIR = "/data/data/com.aceboost/files/logs";

    public static void log(String msg) {
        Log.d(TAG, msg);
        XposedBridge.log(TAG + ": " + msg);
        writeFile(msg);
    }

    public static void error(String msg, Throwable t) {
        String s = msg + " | " + (t == null ? "null" : t.toString());
        Log.e(TAG, s);
        XposedBridge.log(TAG + "[E]: " + s);
        writeFile("[E] " + s);
    }

    public static void clearLocal() {
        try {
            File d = new File(LOG_DIR);
            if (d.exists() && d.isDirectory()) {
                File[] files = d.listFiles();
                if (files != null) {
                    for (File f : files) f.delete();
                }
            }
        } catch (Throwable ignored) {}
    }

    private static void writeFile(String msg) {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) dir.mkdirs();
            File f = new File(dir, "aceboost_debug.log");
            FileWriter fw = new FileWriter(f, true);
            String t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
            fw.write("[" + t + "] " + msg + "\n");
            fw.flush();
            fw.close();
        } catch (Throwable ignored) {}
    }
}
