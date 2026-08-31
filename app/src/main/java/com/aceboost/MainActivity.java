package com.aceboost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import java.util.List;

public class MainActivity extends Activity {
    private SharedPreferences sp;
    private FrameLayout contentArea;
    private LinearLayout navBar;
    private TextView tabOverview, tabUserApps, tabSystemApps, tabSettings;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sp = getSharedPreferences("aceboost_prefs", MODE_PRIVATE);

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setBackground(getGlassBackground());

            contentArea = new FrameLayout(this);
            contentArea.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
            root.addView(contentArea);

            navBar = new LinearLayout(this);
            navBar.setOrientation(LinearLayout.HORIZONTAL);
            navBar.setPadding(12, 12, 12, 20);
            navBar.setBackground(getNavBackground());
            root.addView(navBar);

            tabOverview = createTab("概览", 0);
            tabUserApps = createTab("手机软件", 1);
            tabSystemApps = createTab("本机应用", 2);
            tabSettings = createTab("设置", 3);
            navBar.addView(tabOverview);
            navBar.addView(tabUserApps);
            navBar.addView(tabSystemApps);
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
        tv.setTextSize(14);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, 72, 1f));
        tv.setOnClickListener(v -> switchTab(index));
        return tv;
    }

    private void switchTab(int index) {
        currentTab = index;
        contentArea.removeAllViews();
        if (index == 0) buildOverviewPage();
        else if (index == 1) buildUserAppsPage();
        else if (index == 2) buildSystemAppsPage();
        else buildSettingsPage();
        updateTabColors();
    }

    private void updateTabColors() {
        tabOverview.setTextColor(currentTab == 0 ? Color.WHITE : Color.parseColor("#8899AA"));
        tabUserApps.setTextColor(currentTab == 1 ? Color.WHITE : Color.parseColor("#8899AA"));
        tabSystemApps.setTextColor(currentTab == 2 ? Color.WHITE : Color.parseColor("#8899AA"));
        tabSettings.setTextColor(currentTab == 3 ? Color.WHITE : Color.parseColor("#8899AA"));
    }

    private GradientDrawable getGlassBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.argb(120, 20, 24, 34));
        gd.setCornerRadius(28);
        return gd;
    }

    private GradientDrawable getNavBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.argb(160, 12, 16, 24));
        gd.setCornerRadius(24);
        return gd;
    }

    private void buildOverviewPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(16, 24, 16, 16);

        TextView title = new TextView(this);
        title.setText("⚡ AceBoost");
        title.setTextSize(30);
        title.setTextColor(getRainbowColor());
        page.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("一加 Ace3V 增强模块 · LSPosed");
        subtitle.setTextSize(14);
        subtitle.setTextColor(Color.parseColor("#99AABB"));
        subtitle.setPadding(0, 4, 0, 20);
        page.addView(subtitle);

        addCard(page, "状态栏彩虹色", "时间 / 图标 / 信号 / 电量\n默认彩虹渐变，可选呼吸模式", true);
        addCard(page, "液态玻璃", "应用界面磨砂玻璃背景", true);
        addCard(page, "音量 / 马达 / 音频", "系统级增强选项", false);
        addCard(page, "隐私与便捷", "验证码 / 隐藏 Root", false);

        contentArea.addView(page);
    }

    private void buildUserAppsPage() {
        contentArea.addView(buildAppList(false));
    }

    private void buildSystemAppsPage() {
        contentArea.addView(buildAppList(true));
    }

    private LinearLayout buildAppList(boolean system) {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(16, 20, 16, 16);

        TextView title = new TextView(this);
        title.setText(system ? "本机应用" : "手机软件");
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);
        page.addView(title);

        ScrollView scroll = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : apps) {
            boolean isSys = (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSys != system) continue;
            String name = info.loadLabel(pm).toString();
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(12, 12, 12, 12);
            GradientDrawable rowBg = new GradientDrawable();
            rowBg.setColor(Color.argb(90, 255, 255, 255));
            rowBg.setCornerRadius(16);
            row.setBackground(rowBg);
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rlp.setMargins(0, 0, 0, 10);
            row.setLayoutParams(rlp);
            TextView appName = new TextView(this);
            appName.setText(name);
            appName.setTextSize(15);
            appName.setTextColor(Color.WHITE);
            row.addView(appName);
            TextView pkg = new TextView(this);
            pkg.setText(info.packageName);
            pkg.setTextSize(11);
            pkg.setTextColor(Color.parseColor("#8899AA"));
            pkg.setGravity(Gravity.END);
            pkg.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(pkg);
            list.addView(row);
        }
        scroll.addView(list);
        page.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        return page;
    }

    private void buildSettingsPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(16, 20, 16, 16);
        TextView title = new TextView(this);
        title.setText("设置");
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);
        page.addView(title);

        addToggleRow(page, "呼吸模式", "彩虹颜色缓慢流动", "rainbow_breath", false);
        addToggleRow(page, "状态栏彩虹色", "系统状态栏时间与图标", "rainbow_enable", true);
        addToggleRow(page, "液态玻璃", "App 磨砂玻璃背景", "glass_enable", true);
        contentArea.addView(page);
    }

    private void addToggleRow(LinearLayout parent, String label, String desc, String key, boolean def) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(14, 12, 14, 12);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(90, 255, 255, 255));
        bg.setCornerRadius(16);
        row.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 8, 0, 0);
        row.setLayoutParams(lp);
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
        d.setTextColor(Color.parseColor("#8899AA"));
        textBox.addView(t);
        textBox.addView(d);
        Switch sw = new Switch(this);
        sw.setChecked(sp.getBoolean(key, def));
        sw.setOnCheckedChangeListener((v, c) -> sp.edit().putBoolean(key, c).apply());
        row.addView(textBox);
        row.addView(sw);
        parent.addView(row);
    }

    private void addCard(LinearLayout parent, String title, String desc, boolean enabled) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(16, 14, 16, 14);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(100, 255, 255, 255));
        bg.setCornerRadius(20);
        card.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(lp);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(16);
        t.setTextColor(Color.WHITE);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12);
        d.setTextColor(Color.parseColor("#8899AA"));
        box.addView(t);
        box.addView(d);
        card.addView(box);
        TextView badge = new TextView(this);
        badge.setText(enabled ? "ON" : "OFF");
        badge.setTextSize(12);
        badge.setTextColor(enabled ? Color.WHITE : Color.parseColor("#667788"));
        card.addView(badge);
        parent.addView(card);
    }

    private int getRainbowColor() {
        float hue = (System.currentTimeMillis() / 16) % 360;
        return Color.HSVToColor(255, new float[]{hue, 0.7f, 1.0f});
    }
}
