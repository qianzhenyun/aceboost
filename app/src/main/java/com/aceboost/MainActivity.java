package com.aceboost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    private SharedPreferences sp;
    private FrameLayout contentArea;
    private LinearLayout navBar;
    private TextView tabStatus, tabAudio, tabDisplay, tabPrivacy, tabSettings;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sp = getSharedPreferences("aceboost_prefs", MODE_PRIVATE);

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setBackgroundColor(Color.parseColor("#0A0D12"));

            contentArea = new FrameLayout(this);
            contentArea.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
            root.addView(contentArea);

            navBar = new LinearLayout(this);
            navBar.setOrientation(LinearLayout.HORIZONTAL);
            navBar.setPadding(8, 10, 8, 16);
            GradientDrawable navBg = new GradientDrawable();
            navBg.setColor(Color.argb(150, 28, 36, 48));
            navBg.setCornerRadius(22);
            navBg.setStroke(1, Color.argb(50, 255, 255, 255));
            navBar.setBackground(navBg);
            root.addView(navBar);

            tabStatus = createTab("状态栏", 0);
            tabAudio = createTab("音频", 1);
            tabDisplay = createTab("显示", 2);
            tabPrivacy = createTab("隐私", 3);
            tabSettings = createTab("设置", 4);
            navBar.addView(tabStatus);
            navBar.addView(tabAudio);
            navBar.addView(tabDisplay);
            navBar.addView(tabPrivacy);
            navBar.addView(tabSettings);

            switchTab(0);
            setContentView(root);
        } catch (Throwable t) {
            TextView err = new TextView(this);
            err.setText("界面加载失败：" + t);
            err.setTextColor(Color.WHITE);
            err.setTextSize(14);
            err.setPadding(32, 32, 32, 32);
            setContentView(err);
        }
    }

    private TextView createTab(String label, final int index) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(13);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, 64, 1f));
        tv.setOnClickListener(v -> switchTab(index));
        return tv;
    }

    private void switchTab(int index) {
        currentTab = index;
        contentArea.removeAllViews();
        switch (index) {
            case 0: buildStatusPage(); break;
            case 1: buildAudioPage(); break;
            case 2: buildDisplayPage(); break;
            case 3: buildPrivacyPage(); break;
            default: buildSettingsPage(); break;
        }
        updateTabColors();
    }

    private void updateTabColors() {
        tabStatus.setTextColor(currentTab == 0 ? Color.WHITE : Color.parseColor("#93A0B4"));
        tabAudio.setTextColor(currentTab == 1 ? Color.WHITE : Color.parseColor("#93A0B4"));
        tabDisplay.setTextColor(currentTab == 2 ? Color.WHITE : Color.parseColor("#93A0B4"));
        tabPrivacy.setTextColor(currentTab == 3 ? Color.WHITE : Color.parseColor("#93A0B4"));
        tabSettings.setTextColor(currentTab == 4 ? Color.WHITE : Color.parseColor("#93A0B4"));
    }

    private void buildStatusPage() {
        LinearLayout page = basePage("状态栏");
        addToggleRow(page, "彩虹渐变", "状态栏时间与图标使用彩虹色", "rainbow_enable", true);
        addToggleRow(page, "呼吸模式", "彩虹颜色缓慢流动", "rainbow_breath", false);
        addToggleRow(page, "金色默认", "使用默认金色，关闭彩虹", "gold_enable", false);
        contentArea.addView(page);
    }

    private void buildAudioPage() {
        LinearLayout page = basePage("音频");
        addToggleRow(page, "音量增强", "音量键步进 + 最大音量上限", "vol_enable", true);
        addSeekRow(page, "音量步数", 30, 100, "vol_steps", 60);
        addSeekRow(page, "最大音量上限", 150, 255, "vol_max", 201);
        addToggleRow(page, "采样率优化", "目标采样率 192000Hz", "audio_enable", true);
        addToggleRow(page, "马达增强", "振动强度提升", "vibrate_enable", true);
        addSeekRow(page, "振动强度 (%)", 100, 200, "vibrate_level", 160);
        contentArea.addView(page);
    }

    private void buildDisplayPage() {
        LinearLayout page = basePage("显示");
        addToggleRow(page, "导航栏液态玻璃", "底部导航栏磨砂玻璃效果", "glass_enable", true);
        addToggleRow(page, "全屏防烧屏", "像素级微移，保护 OLED", "burnin_enable", true);
        addActionRow(page, "手机软件", "对第三方应用启用液态玻璃", () -> toast("手机软件页面待扩展"));
        addActionRow(page, "本机应用", "对系统应用启用液态玻璃", () -> toast("本机应用页面待扩展"));
        contentArea.addView(page);
    }

    private void buildPrivacyPage() {
        LinearLayout page = basePage("隐私");
        addToggleRow(page, "验证码自动复制", "复制验证码到剪贴板", "sms_copy", true);
        addToggleRow(page, "验证码自动填入", "自动填入输入框", "sms_fill", false);
        addToggleRow(page, "隐藏 Xposed/Root", "基础防检测", "hide_enable", true);
        contentArea.addView(page);
    }

    private void buildSettingsPage() {
        LinearLayout page = basePage("设置");
        addActionRow(page, "软重启 Zygote", "快速重新加载模块", () -> execRoot("setprop ctl.restart zygote"));
        addActionRow(page, "重启 SystemUI", "重新加载状态栏", () -> execRoot("pkill -f com.android.systemui"));
        addActionRow(page, "应用相机增强", "实验性：提升 EIS 防抖强度", () -> CameraEnhance.applyEisBoost(msg -> toast(msg)));
        addActionRow(page, "恢复相机配置", "恢复原始 EIS 配置", () -> CameraEnhance.restoreEis(msg -> toast(msg)));
        contentArea.addView(page);
    }

    private LinearLayout basePage(String titleText) {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(16, 20, 16, 16);
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(22);
        title.setTextColor(Color.WHITE);
        title.setPadding(0, 0, 0, 16);
        page.addView(title);
        return page;
    }

    private void addToggleRow(LinearLayout parent, String label, String desc, String key, boolean def) {
        LinearLayout row = glassRow();
        LinearLayout textBox = new LinearLayout(this);
        textBox.setOrientation(LinearLayout.VERTICAL);
        textBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#93A0B4"));
        textBox.addView(t);
        textBox.addView(d);
        Switch sw = new Switch(this);
        sw.setChecked(sp.getBoolean(key, def));
        sw.setOnCheckedChangeListener((v, c) -> sp.edit().putBoolean(key, c).apply());
        row.addView(textBox);
        row.addView(sw);
        parent.addView(row);
    }

    private void addSeekRow(LinearLayout parent, String label, int min, int max, String key, int def) {
        LinearLayout row = glassRow();
        row.setOrientation(LinearLayout.VERTICAL);
        TextView t = new TextView(this);
        t.setText(label + "：" + sp.getInt(key, def));
        t.setTextSize(15);
        t.setTextColor(Color.WHITE);
        row.addView(t);
        SeekBar bar = new SeekBar(this);
        bar.setMax(max - min);
        bar.setProgress(sp.getInt(key, def) - min);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                t.setText(label + "：" + (p + min));
                if (fromUser) sp.edit().putInt(key, p + min).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        row.addView(bar);
        parent.addView(row);
    }

    private void addActionRow(LinearLayout parent, String label, String desc, Runnable action) {
        LinearLayout row = glassRow();
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#93A0B4"));
        box.addView(t);
        box.addView(d);
        row.addView(box);
        TextView arrow = new TextView(this);
        arrow.setText(">");
        arrow.setTextSize(16);
        arrow.setTextColor(Color.parseColor("#93A0B4"));
        row.addView(arrow);
        row.setOnClickListener(v -> {
            if (action != null) action.run();
        });
        parent.addView(row);
    }

    private LinearLayout glassRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(14, 12, 14, 12);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(55, 255, 255, 255));
        bg.setCornerRadius(18);
        bg.setStroke(1, Color.argb(36, 255, 255, 255));
        row.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
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
}
