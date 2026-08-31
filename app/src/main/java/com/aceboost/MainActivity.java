package com.aceboost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends Activity {
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sp = getSharedPreferences("aceboost_prefs", MODE_PRIVATE);

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(24, 48, 24, 32);
            GradientDrawable rootBg = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Color.parseColor("#0B0F14"), Color.parseColor("#11161D")});
            root.setBackground(rootBg);

            TextView title = new TextView(this);
            title.setText("⚡ AceBoost");
            title.setTextColor(Color.parseColor("#FFD866"));
            title.setTextSize(30);
            title.setPadding(0, 0, 0, 6);
            root.addView(title);

            TextView subtitle = new TextView(this);
            subtitle.setText("一加 Ace3V 增强模块 · LSPosed");
            subtitle.setTextColor(Color.parseColor("#8B949E"));
            subtitle.setTextSize(13);
            subtitle.setPadding(0, 0, 0, 24);
            root.addView(subtitle);

            addSectionHeader(root, "系统增强");
            addToggle(root, "🔊 音量增强", "音量键步进 + 最大音量上限", "vol_enable", true);
            addSeek(root, "音量步数", 30, 100, "vol_steps", 60);
            addSeek(root, "最大音量上限", 150, 255, "vol_max", 201);
            addToggle(root, "🎵 音频采样率优化", "目标采样率 192000Hz", "audio_enable", true);
            addToggle(root, "📳 马达增强", "振动强度提升", "vibrate_enable", true);
            addSeek(root, "振动强度 (%)", 100, 200, "vibrate_level", 160);

            addSectionHeader(root, "显示优化");
            addToggle(root, "🖥️ 全屏防烧屏", "像素级微移，保护 OLED", "burnin_enable", true);
            addToggle(root, "🎨 状态栏时钟颜色", "时间 / 电量 / 信号颜色", "color_enable", true);
            addColorPicker(root);

            addSectionHeader(root, "隐私与便捷");
            addToggle(root, "✉️ 验证码自动复制", "复制验证码到剪贴板", "sms_copy", true);
            addToggle(root, "⌨️ 验证码自动填入", "自动填入输入框", "sms_fill", false);
            addToggle(root, "🛡️ 隐藏 Xposed/Root", "基础防检测", "hide_enable", true);

            addSectionHeader(root, "快捷操作");
            addSoftRebootCard(root);

            TextView note = new TextView(this);
            note.setText("\n请在 LSPosed 作用域中勾选：\n系统框架、SystemUI、电话、短信\n设置保存后重启设备生效");
            note.setTextColor(Color.parseColor("#FF7B72"));
            note.setTextSize(12);
            note.setPadding(0, 16, 0, 0);
            root.addView(note);

            ScrollView scroll = new ScrollView(this);
            scroll.addView(root);
            setContentView(scroll);
        } catch (Throwable t) {
            TextView err = new TextView(this);
            err.setText("界面加载失败：" + t);
            err.setTextColor(Color.WHITE);
            err.setTextSize(14);
            err.setPadding(32, 32, 32, 32);
            setContentView(err);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.clearLocal();
    }

    private void addSectionHeader(LinearLayout parent, String text) {
        TextView h = new TextView(this);
        h.setText(text);
        h.setTextColor(Color.parseColor("#56CCF2"));
        h.setTextSize(15);
        h.setPadding(4, 10, 0, 10);
        parent.addView(h);
    }

    private LinearLayout makeCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 16, 20, 16);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#161B22"));
        bg.setCornerRadius(20);
        card.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(lp);
        return card;
    }

    private void addToggle(LinearLayout parent, String titleText, String descText, String key, boolean def) {
        LinearLayout card = makeCard();
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        card.addView(title);

        TextView desc = new TextView(this);
        desc.setText(descText);
        desc.setTextColor(Color.parseColor("#8B949E"));
        desc.setTextSize(12);
        card.addView(desc);

        Switch sw = new Switch(this);
        sw.setChecked(sp.getBoolean(key, def));
        sw.setOnCheckedChangeListener((v, checked) -> sp.edit().putBoolean(key, checked).apply());
        card.addView(sw);
        parent.addView(card);
    }

    private void addSeek(LinearLayout parent, String label, int min, int max, String key, int def) {
        LinearLayout card = makeCard();
        TextView title = new TextView(this);
        title.setText(label + "：" + sp.getInt(key, def));
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);
        card.addView(title);

        SeekBar bar = new SeekBar(this);
        bar.setMax(max - min);
        bar.setProgress(sp.getInt(key, def) - min);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                title.setText(label + "：" + (p + min));
                if (fromUser) sp.edit().putInt(key, p + min).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        card.addView(bar);
        parent.addView(card);
    }

    private void addColorPicker(LinearLayout parent) {
        LinearLayout card = makeCard();
        TextView title = new TextView(this);
        title.setText("渐变配色");
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);
        card.addView(title);

        String[] colors = {"金色", "玫瑰金", "冰蓝", "青绿", "紫色"};
        String[] hex = {"#FFD866", "#F7B7C5", "#7FD4FF", "#6FE3C1", "#C9A6FF"};

        HorizontalScrollView hsv = new HorizontalScrollView(this);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < colors.length; i++) {
            final int fi = i;
            Button b = new Button(this);
            b.setText(colors[i]);
            b.setAllCaps(false);
            b.setTextSize(12);
            b.setTextColor(Color.BLACK);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor(hex[i]));
            bg.setCornerRadius(16);
            b.setBackground(bg);
            LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            blp.setMargins(0, 0, 10, 0);
            b.setLayoutParams(blp);
            b.setOnClickListener(v -> {
                sp.edit().putString("status_color", hex[fi]).apply();
                Toast.makeText(this, "已选择：" + colors[fi], Toast.LENGTH_SHORT).show();
            });
            row.addView(b);
        }
        hsv.addView(row);
        card.addView(hsv);
        parent.addView(card);
    }

    private void addSoftRebootCard(LinearLayout parent) {
        LinearLayout card = makeCard();
        TextView title = new TextView(this);
        title.setText("🔄 软重启");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        card.addView(title);

        TextView desc = new TextView(this);
        desc.setText("无需整机重启，快速重新加载模块");
        desc.setTextColor(Color.parseColor("#8B949E"));
        desc.setTextSize(12);
        card.addView(desc);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        Button btnZygote = new Button(this);
        btnZygote.setText("软重启 Zygote");
        btnZygote.setAllCaps(false);
        btnZygote.setTextColor(Color.BLACK);
        GradientDrawable bg1 = new GradientDrawable();
        bg1.setColor(Color.parseColor("#FFD866"));
        bg1.setCornerRadius(16);
        btnZygote.setBackground(bg1);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp1.setMargins(0, 0, 10, 0);
        btnZygote.setLayoutParams(lp1);
        btnZygote.setOnClickListener(v -> execRoot("setprop ctl.restart zygote"));

        Button btnSysui = new Button(this);
        btnSysui.setText("重启 SystemUI");
        btnSysui.setAllCaps(false);
        btnSysui.setTextColor(Color.BLACK);
        GradientDrawable bg2 = new GradientDrawable();
        bg2.setColor(Color.parseColor("#7FD4FF"));
        bg2.setCornerRadius(16);
        btnSysui.setBackground(bg2);
        btnSysui.setOnClickListener(v -> execRoot("pkill -f com.android.systemui"));

        row.addView(btnZygote);
        row.addView(btnSysui);
        card.addView(row);
        parent.addView(card);
    }

    private void execRoot(String cmd) {
        new Thread(() -> {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
                p.waitFor();
                runOnUiThread(() -> Toast.makeText(this, "执行完成", Toast.LENGTH_SHORT).show());
            } catch (Throwable t) {
                runOnUiThread(() -> Toast.makeText(this, "执行失败：" + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
