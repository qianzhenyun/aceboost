package com.aceboost;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiquidGlassNavBar extends FrameLayout {
    public interface Listener { void onSelect(int index); }
    private final TextView[] tabs = new TextView[3];
    private final View indicator;
    private int selected = 0;
    private final int itemWidth;

    public LiquidGlassNavBar(Context context, Listener listener) {
        super(context);
        float d = getResources().getDisplayMetrics().density;
        int totalW = getResources().getDisplayMetrics().widthPixels;
        itemWidth = (totalW - dp(44, d)) / 3;
        setPadding(dp(16, d), dp(6, d), dp(16, d), dp(14, d));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.argb(107, 255, 255, 255), Color.argb(41, 255, 255, 255)});
        bg.setCornerRadius(dp(34, d));
        bg.setStroke(dp(1, d), Color.argb(166, 255, 255, 255));
        setBackground(bg);
        setElevation(dp(24, d));

        indicator = new View(context);
        GradientDrawable id = new GradientDrawable();
        id.setColor(Color.argb(184, 255, 255, 255));
        id.setCornerRadius(dp(26, d));
        indicator.setBackground(id);
        FrameLayout.LayoutParams il = new FrameLayout.LayoutParams(itemWidth, FrameLayout.LayoutParams.MATCH_PARENT);
        il.setMargins(dp(6, d), dp(8, d), 0, dp(8, d));
        indicator.setLayoutParams(il);
        addView(indicator);

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams rl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rl.setMargins(dp(6, d), dp(8, d), dp(6, d), dp(8, d));
        row.setLayoutParams(rl);
        String[] labels = {"手机系统功能", "手机应用功能", "本APP设置"};
        for (int i = 0; i < 3; i++) {
            TextView t = new TextView(context);
            t.setText(labels[i]);
            t.setTextSize(13);
            t.setGravity(Gravity.CENTER);
            t.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            final int idx = i;
            t.setOnClickListener(v -> {
                selected = idx;
                indicator.animate().translationX(itemWidth * idx).setDuration(320).start();
                for (int j = 0; j < 3; j++) tabs[j].setTextColor(j == idx ? Color.parseColor("#1A73E8") : Color.parseColor("#5F6368"));
                listener.onSelect(idx);
            });
            tabs[i] = t;
            row.addView(t);
        }
        addView(row);
        tabs[0].setTextColor(Color.parseColor("#1A73E8"));
    }

    private int dp(int v, float d) { return Math.round(v * d); }
}
