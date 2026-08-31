package com.aceboost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.*;
import java.util.ArrayList;
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
            LinearLayout.LayoutParams contentLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
            contentArea.setLayoutParams(contentLp);
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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 72, 1f);
        tv.setLayoutParams(lp);
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
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#1A1F2B"), Color.parseColor("#10131C")});
        gd.setCornerRadius(24f);
        return gd;
    }

    private GradientDrawable getNavBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.argb(90, 30, 35, 50));
        gd.setCornerRadius(22f);
        gd.setStroke(1, Color.argb(60, 255, 255, 255));
        return gd;
    }

    private LinearLayout makeGlassCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(22, 20, 22, 20);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(80, 40, 45, 60));
        bg.setCornerRadius(20f);
        bg.setStroke(1, Color.argb(55, 255, 255, 255));
        card.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(14, 10, 14, 10);
        card.setLayoutParams(lp);
        return card;
    }

    private TextView makeTitle(String text, int size, String color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(Color.parseColor(color));
        tv.setPadding(2, 4, 2, 4);
        return tv;
    }

    private void buildOverviewPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(18, 26, 18, 18);

        TextView title = makeTitle("⚡ AceBoost", 28, "#FFFFFF");
        page.addView(title);
        TextView sub = makeTitle("一加 Ace3V 增强模块 · LSPosed", 13, "#8899AA");
        sub.setPadding(2, 4, 2, 18);
        page.addView(sub);

        LinearLayout card = makeGlassCard();
        card.addView(makeTitle("📊 模块状态", 16, "#CCFFFFFF"));
        card.addView(makeTitle("LSPosed 激活状态：请在 LSPosed 中检查", 13, "#99AABB"));
        card.addView(makeTitle("当前版本：7.0", 14, "#7FD4FF"));
        page.addView(card);

        LinearLayout card2 = makeGlassCard();
        card2.addView(makeTitle("🎨 状态栏颜色", 16, "#CCFFFFFF"));
        Switch sw = new Switch(this);
        sw.setText("启用彩虹渐变");
        sw.setChecked(sp.getBoolean("color_enable", true));
        sw.setOnCheckedChangeListener((v, checked) -> sp.edit().putBoolean("color_enable", checked).apply());
        card2.addView(sw);
        page.addView(card2);

        contentArea.addView(page);
    }

    private void buildUserAppsPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(18, 26, 18, 18);
        page.addView(makeTitle("📱 手机软件", 22, "#FFFFFF"));
        page.addView(makeTitle("第三方应用优化", 13, "#8899AA"));
        for (String app : getInstalledApps(false)) {
            LinearLayout c = makeGlassCard();
            c.addView(makeTitle(app, 15, "#DDFFFFFF"));
            page.addView(c);
        }
        contentArea.addView(page);
    }

    private void buildSystemAppsPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(18, 26, 18, 18);
        page.addView(makeTitle("⚙️ 本机应用", 22, "#FFFFFF"));
        page.addView(makeTitle("系统应用优化", 13, "#8899AA"));
        for (String app : getInstalledApps(true)) {
            LinearLayout c = makeGlassCard();
            c.addView(makeTitle(app, 15, "#DDFFFFFF"));
            page.addView(c);
        }
        contentArea.addView(page);
    }

    private void buildSettingsPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(18, 26, 18, 18);
        page.addView(makeTitle("🛠 设置", 22, "#FFFFFF"));

        LinearLayout card = makeGlassCard();
        card.addView(makeTitle("软重启", 16, "#CCFFFFFF"));
        Button btnZygote = new Button(this);
        btnZygote.setText("软重启 Zygote");
        btnZygote.setAllCaps(false);
        btnZygote.setOnClickListener(v -> execRoot("setprop ctl.restart zygote"));
        card.addView(btnZygote);
        Button btnSysui = new Button(this);
        btnSysui.setText("重启 SystemUI");
        btnSysui.setAllCaps(false);
        btnSysui.setOnClickListener(v -> execRoot("pkill -f com.android.systemui"));
        card.addView(btnSysui);
        page.addView(card);

        LinearLayout card2 = makeGlassCard();
        card2.addView(makeTitle("彩虹呼吸模式", 16, "#CCFFFFFF"));
        Switch breathe = new Switch(this);
        breathe.setText("启用呼吸效果");
        breathe.setChecked(sp.getBoolean("breath_enable", true));
        breathe.setOnCheckedChangeListener((v, checked) -> sp.edit().putBoolean("breath_enable", checked).apply());
        card2.addView(breathe);
        page.addView(card2);

        contentArea.addView(page);
    }

    private List<String> getInstalledApps(boolean system) {
        List<String> names = new ArrayList<>();
        PackageManager pm = getPackageManager();
        for (ApplicationInfo ai : pm.getInstalledApplications(0)) {
            boolean isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSystem == system) {
                String label = String.valueOf(pm.getApplicationLabel(ai));
                if (!names.contains(label)) names.add(label);
            }
        }
        return names;
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
