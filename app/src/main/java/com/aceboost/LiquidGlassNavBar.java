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
    private final int itemWidth;
    private float density;

    public LiquidGlassNavBar(Context context, Listener listener) {
        super(context);
        density = getResources().getDisplayMetrics().density;
        int totalW = getResources().getDisplayMetrics().widthPixels;
        int padH = dp(16);
        itemWidth = (totalW - padH * 2 - dp(12)) / 3;

        setPadding(padH, dp(8), padH, dp(8));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.argb(90, 255, 255, 255), Color.argb(30, 255, 255, 255)});
        bg.setCornerRadius(dp(28));
        bg.setStroke(1, Color.argb(130, 255, 255, 255));
        setBackground(bg);
        setElevation(dp(18));

        indicator = new View(context);
        GradientDrawable id = new GradientDrawable();
        id.setColor(Color.argb(160, 255, 255, 255));
        id.setCornerRadius(dp(22));
        indicator.setBackground(id);
        FrameLayout.LayoutParams il = new FrameLayout.LayoutParams(itemWidth, FrameLayout.LayoutParams.MATCH_PARENT);
        il.setMargins(dp(4), dp(6), 0, dp(6));
        indicator.setLayoutParams(il);
        addView(indicator);

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams rl = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rl.setMargins(dp(4), dp(6), dp(4), dp(6));
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
                indicator.animate().translationX(itemWidth * idx).setDuration(280).start();
                for (int j = 0; j < 3; j++) {
                    tabs[j].setTextColor(j == idx ? Color.parseColor("#0B84FF") : Color.parseColor("#7A879B"));
                }
                listener.onSelect(idx);
            });
            tabs[i] = t;
            row.addView(t);
        }
        addView(row);
        tabs[0].setTextColor(Color.parseColor("#0B84FF"));
    }

    private int dp(int v) {
        return Math.round(v * density);
    }
}
