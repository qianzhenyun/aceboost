package com.aceboost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private SharedPreferences sp;
    private FrameLayout contentArea;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sp = getSharedPreferences("aceboost_prefs", MODE_PRIVATE);
            density = getResources().getDisplayMetrics().density;

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setBackgroundColor(Color.parseColor("#070A0F"));

            contentArea = new FrameLayout(this);
            contentArea.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
            root.addView(contentArea);

            LiquidGlassNavBar nav = new LiquidGlassNavBar(this, this::switchTab);
            nav.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(72)));
            root.addView(nav);

            setContentView(root);
            switchTab(0);
        } catch (Throwable t) {
            TextView err = new TextView(this);
            err.setText("页面加载失败：" + t);
            err.setTextColor(Color.WHITE);
            err.setTextSize(14);
            err.setPadding(dp(24), dp(24), dp(24), dp(24));
            setContentView(err);
        }
    }

    private void switchTab(int index) {
        contentArea.removeAllViews();
        if (index == 0) buildSystemPage();
        else if (index == 1) buildAppPage();
        else buildSelfPage();
    }

    private void buildSystemPage() {
        LinearLayout page = basePage("手机系统功能", "音频与显示增强");
        addCard(page, "🎵", "采样率优化", "目标采样率 192000 Hz", "audio_enable", true);
        addCard(page, "📳", "振动增强", "提升振动反馈强度", "vibrate_enable", true);
        addSliderCard(page, "振动强度", "100% ~ 200%", "vibrate_level", 160, 100, 200);
        addCard(page, "🖥️", "防烧屏", "保护 OLED 屏幕", "burnin_enable", true);
        contentArea.addView(page);
    }

    private void buildAppPage() {
        LinearLayout page = basePage("手机应用功能", "验证码与自动化");
        addCard(page, "📋", "验证码自动复制", "自动复制到剪贴板", "sms_copy", true);
        addCard(page, "✍️", "验证码自动填充", "自动填入输入框", "sms_fill", false);
        contentArea.addView(page);
    }

    private void buildSelfPage() {
        LinearLayout page = basePage("本APP设置", "模块与系统控制");
        addCard(page, "🛡️", "隐藏 Xposed/Root", "基础检测隐藏", "hide_enable", true);
        addActionCard(page, "🔁", "软重启 Zygote", "快速重新加载模块", () -> execRoot("setprop ctl.restart zygote"));
        addActionCard(page, "🔄", "重启 SystemUI", "重新加载状态栏", () -> execRoot("pkill -f com.android.systemui"));
        contentArea.addView(page);
    }

    private LinearLayout basePage(String titleText, String subtitleText) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(22), dp(18), dp(18));

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(24);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, dp(4));
        page.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(subtitleText);
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.parseColor("#93A0B4"));
        subtitle.setPadding(0, 0, 0, dp(18));
        page.addView(subtitle);

        scroll.addView(page, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return wrapper;
    }

    private void addCard(LinearLayout parent, String icon, String label, String desc, String key, boolean def) {
        LinearLayout row = glassCard();

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(22);
        iconView.setGravity(Gravity.CENTER);
        iconView.setPadding(0, 0, dp(12), 0);
        row.addView(iconView);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        t.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#8A97AA"));
        d.setPadding(0, dp(3), 0, 0);
        box.addView(t);
        box.addView(d);
        row.addView(box);

        Switch sw = new Switch(this);
        sw.setChecked(sp.getBoolean(key, def));
        sw.setOnCheckedChangeListener((v, c) -> sp.edit().putBoolean(key, c).apply());
        row.addView(sw);
        parent.addView(row);
    }

    private void addSliderCard(LinearLayout parent, String label, String rangeText, String key, int def, int min, int max) {
        LinearLayout row = glassCard();
        row.setOrientation(LinearLayout.VERTICAL);

        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        TextView iconView = new TextView(this);
        iconView.setText("🎚️");
        iconView.setTextSize(22);
        iconView.setPadding(0, 0, dp(12), 0);
        head.addView(iconView);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        t.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView d = new TextView(this);
        d.setText(rangeText);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#8A97AA"));
        d.setPadding(0, dp(3), 0, 0);
        box.addView(t);
        box.addView(d);
        head.addView(box);

        int current = sp.getInt(key, def);
        TextView val = new TextView(this);
        val.setText(current + "%");
        val.setTextSize(16);
        val.setTextColor(Color.parseColor("#4DA3FF"));
        val.setTypeface(null, android.graphics.Typeface.BOLD);
        head.addView(val);
        row.addView(head);

        SeekBar bar = new SeekBar(this);
        bar.setMax(max - min);
        bar.setProgress(current - min);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        blp.setMargins(0, dp(8), 0, 0);
        bar.setLayoutParams(blp);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                int v = p + min;
                val.setText(v + "%");
                if (fromUser) sp.edit().putInt(key, v).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        row.addView(bar);
        parent.addView(row);
    }

    private void addActionCard(LinearLayout parent, String icon, String label, String desc, Runnable action) {
        LinearLayout row = glassCard();

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(22);
        iconView.setGravity(Gravity.CENTER);
        iconView.setPadding(0, 0, dp(12), 0);
        row.addView(iconView);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        t.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#8A97AA"));
        d.setPadding(0, dp(3), 0, 0);
        box.addView(t);
        box.addView(d);
        row.addView(box);

        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(26);
        arrow.setTextColor(Color.parseColor("#8A97AA"));
        row.addView(arrow);
        row.setOnClickListener(v -> { if (action != null) action.run(); });
        parent.addView(row);
    }

    private LinearLayout glassCard() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(42, 255, 255, 255));
        bg.setCornerRadius(dp(20));
        bg.setStroke(1, Color.argb(30, 255, 255, 255));
        row.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(12));
        row.setLayoutParams(lp);
        return row;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void execRoot(String cmd) {
        new Thread(() -> {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
                p.waitFor();
                runOnUiThread(() -> toast("执行完成"));
            } catch (Throwable t) {
                runOnUiThread(() -> toast("执行失败：" + t.getMessage()));
            }
        }).start();
    }

    private int dp(int v) {
        return Math.round(v * density);
    }
}
