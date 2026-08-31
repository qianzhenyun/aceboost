package com.aceboost;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CameraEnhance {
    private static final String EIS_PATH = "/odm/etc/camera/oplus_eis_camera.vcfg";
    private static final String BACKUP_PATH = "/storage/emulated/0/Documents/camera_eis_backup.vcfg";

    public interface Callback {
        void onResult(String msg);
    }

    public static void applyEisBoost(final Callback cb) {
        new Thread(() -> {
            String result;
            try {
                if (!new File(BACKUP_PATH).exists()) {
                    exec("cp " + EIS_PATH + " " + BACKUP_PATH);
                }
                exec("mount -o remount,rw /odm");

                File f = new File("/storage/emulated/0/Documents/camera_eis_tmp.vcfg");
                exec("cp " + EIS_PATH + " " + f.getAbsolutePath());
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                br.close();
                String content = sb.toString();
                content = content.replace("\"strength\": 0.85", "\"strength\": 0.95");
                content = content.replace("\"strengthDeferMin\": 0.85", "\"strengthDeferMin\": 0.95");

                FileWriter fw = new FileWriter(f);
                fw.write(content);
                fw.close();

                exec("cp " + f.getAbsolutePath() + " " + EIS_PATH);
                exec("chmod 644 " + EIS_PATH);
                exec("mount -o remount,ro /odm");
                exec("pkill -f com.oplus.camera");
                result = "相机增强已应用，请重启相机";
            } catch (Throwable t) {
                result = "相机增强失败：" + t.getMessage();
            }
            final String finalResult = result;
            if (cb != null) {
                new Handler(Looper.getMainLooper()).post(() -> cb.onResult(finalResult));
            }
        }).start();
    }

    public static void restoreEis(final Callback cb) {
        new Thread(() -> {
            String result;
            try {
                if (new File(BACKUP_PATH).exists()) {
                    exec("mount -o remount,rw /odm");
                    exec("cp " + BACKUP_PATH + " " + EIS_PATH);
                    exec("chmod 644 " + EIS_PATH);
                    exec("mount -o remount,ro /odm");
                    exec("pkill -f com.oplus.camera");
                    result = "已恢复相机配置";
                } else {
                    result = "未找到备份，无法恢复";
                }
            } catch (Throwable t) {
                result = "恢复失败：" + t.getMessage();
            }
            final String finalResult = result;
            if (cb != null) {
                new Handler(Looper.getMainLooper()).post(() -> cb.onResult(finalResult));
            }
        }).start();
    }

    private static void exec(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
        p.waitFor();
    }
}
