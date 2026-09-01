package com.aceboost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    private SharedPreferences sp;
    private FrameLayout contentArea;
    private float d;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        try {
            sp = getSharedPreferences("aceboost_prefs", MODE_PRIVATE);
            d = getResources().getDisplayMetrics().density;
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setBackgroundColor(Color.parseColor("#0F141A"));
            contentArea = new FrameLayout(this);
            contentArea.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1f));
            root.addView(contentArea);
            LiquidGlassNavBar nav = new LiquidGlassNavBar(this, this::switchTab);
            nav.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(72)));
            root.addView(nav);
            setContentView(root);
            switchTab(0);
        } catch (Throwable t) {
            TextView e = new TextView(this);
            e.setText("加载失败：" + t);
            e.setTextColor(Color.WHITE);
            e.setTextSize(14);
            e.setPadding(dp(24), dp(24), dp(24), dp(24));
            setContentView(e);
        }
    }

    private void switchTab(int i) {
        contentArea.removeAllViews();
        if (i == 0) system(); else if (i == 1) app(); else self();
    }

    private void system() {
        contentArea.addView(page("手机系统功能", "音频、显示与系统增强", p -> {
            toggle(p, "采样率优化", "目标采样率 192000 Hz", "audio_enable", true);
            toggle(p, "振动增强", "提升振动反馈强度", "vibrate_enable", true);
            slider(p, "振动强度", "100% - 200%", "vibrate_level", 160, 100, 200);
            toggle(p, "防烧屏", "保护 OLED 屏幕", "burnin_enable", true);
            toggle(p, "4K 视频解码", "解锁 4K 分辨率播放", "video_4k_enable", false);
        }));
    }

    private void app() {
        contentArea.addView(page("手机应用功能", "验证码与自动化", p -> {
            toggle(p, "验证码自动复制", "复制验证码到剪贴板", "sms_copy", true);
            toggle(p, "验证码自动填充", "自动填入输入框", "sms_fill", false);
        }));
    }

    private void self() {
        contentArea.addView(page("本APP设置", "模块与系统控制", p -> {
            toggle(p, "隐藏 Xposed/Root", "基础检测隐藏", "hide_enable", true);
            action(p, "软重启 Zygote", "快速重新加载模块", () -> exec("stop; start"));
            action(p, "重启 SystemUI", "重新加载状态栏", () -> exec("killall com.android.systemui"));
        }));
    }

    interface B { void b(LinearLayout p); }

    private View page(String t1, String t2, B builder) {
        ScrollView s = new ScrollView(this);
        s.setFillViewport(true);
        LinearLayout p = new LinearLayout(this);
        p.setOrientation(LinearLayout.VERTICAL);
        p.setPadding(dp(20), dp(24), dp(20), dp(20));
        TextView t = new TextView(this);
        t.setText(t1);
        t.setTextSize(24);
        t.setTextColor(Color.WHITE);
        t.setTypeface(null, Typeface.BOLD);
        p.addView(t);
        TextView st = new TextView(this);
        st.setText(t2);
        st.setTextSize(13);
        st.setTextColor(Color.parseColor("#8A97AA"));
        st.setPadding(0, dp(4), 0, dp(20));
        p.addView(st);
        builder.b(p);
        s.addView(p);
        return s;
    }

    private LinearLayout row() {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.setPadding(dp(16), dp(14), dp(16), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(40, 255, 255, 255));
        bg.setCornerRadius(dp(18));
        bg.setStroke(1, Color.argb(30, 255, 255, 255));
        r.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, dp(12));
        r.setLayoutParams(lp);
        return r;
    }

    private LinearLayout box(String a, String b) {
        LinearLayout x = new LinearLayout(this);
        x.setOrientation(LinearLayout.VERTICAL);
        x.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        TextView t = new TextView(this);
        t.setText(a);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        t.setTypeface(null, Typeface.BOLD);
        TextView d = new TextView(this);
        d.setText(b);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#758398"));
        d.setPadding(0, dp(3), 0, 0);
        x.addView(t);
        x.addView(d);
        return x;
    }

    private void toggle(LinearLayout p, String a, String b, String k, boolean def) {
        LinearLayout r = row();
        Switch sw = new Switch(this);
        sw.setChecked(sp.getBoolean(k, def));
        sw.setOnCheckedChangeListener((v, c) -> sp.edit().putBoolean(k, c).apply());
        r.addView(box(a, b));
        r.addView(sw);
        p.addView(r);
    }

    private void slider(LinearLayout p, String a, String b, String k, int def, int min, int max) {
        LinearLayout r = row();
        r.setOrientation(LinearLayout.VERTICAL);
        LinearLayout h = new LinearLayout(this);
        h.setOrientation(LinearLayout.HORIZONTAL);
        h.setGravity(Gravity.CENTER_VERTICAL);
        int cur = sp.getInt(k, def);
        TextView v = new TextView(this);
        v.setText(cur + "%");
        v.setTextSize(15);
        v.setTextColor(Color.parseColor("#6E8BFF"));
        v.setTypeface(null, Typeface.BOLD);
        h.addView(box(a, b));
        h.addView(v);
        r.addView(h);
        SeekBar sb = new SeekBar(this);
        sb.setMax(max - min);
        sb.setProgress(cur - min);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(10), 0, 0);
        sb.setLayoutParams(lp);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int x, boolean fromUser) {
                int val = x + min;
                v.setText(val + "%");
                if (fromUser) sp.edit().putInt(k, val).apply();
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        r.addView(sb);
        p.addView(r);
    }

    private void action(LinearLayout p, String a, String b, Runnable run) {
        LinearLayout r = row();
        TextView ar = new TextView(this);
        ar.setText(">");
        ar.setTextSize(26);
        ar.setTextColor(Color.parseColor("#5C6B80"));
        r.addView(box(a, b));
        r.addView(ar);
        r.setOnClickListener(v -> run.run());
        p.addView(r);
    }

    private void exec(String cmd) {
        new Thread(() -> {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
                int code = p.waitFor();
                runOnUiThread(() -> Toast.makeText(this, code == 0 ? "执行完成" : "执行失败，请检查 Root 权限", Toast.LENGTH_SHORT).show());
            } catch (Throwable t) {
                runOnUiThread(() -> Toast.makeText(this, "执行失败：" + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private int dp(int v) { return Math.round(v * d); }
}
