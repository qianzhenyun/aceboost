package com.aceboost;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import android.graphics.Color;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#1A1A2E"));
        root.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("AceBoost");
        title.setTextColor(Color.parseColor("#FFD700"));
        title.setTextSize(28);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("一加 Ace3V 增强模块");
        subtitle.setTextColor(Color.parseColor("#AAAAAA"));
        subtitle.setTextSize(14);
        root.addView(subtitle);

        addSection(root, "音量控制", "音量步进 60 步 · 最大音量 201 档");
        addSection(root, "音频优化", "采样率 192000Hz");
        addSection(root, "马达增强", "振动强度提升 160%");
        addSection(root, "状态栏优化", "时钟防烧屏 · 时间/电量/信号颜色渐变");
        addSection(root, "隐私保护", "隐藏 Xposed/Root 痕迹");
        addSection(root, "验证码助手", "自动复制 · 自动填入输入框");

        TextView note = new TextView(this);
        note.setText("\n请在本模块的 LSPosed 作用域中勾选：\n系统框架、SystemUI、电话、短信\n然后重启设备生效");
        note.setTextColor(Color.parseColor("#FF6B6B"));
        note.setTextSize(13);
        root.addView(note);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);
    }

    private void addSection(LinearLayout parent, String title, String desc) {
        TextView t = new TextView(this);
        t.setText("\n" + title);
        t.setTextColor(Color.parseColor("#FFFFFF"));
        t.setTextSize(18);
        parent.addView(t);

        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextColor(Color.parseColor("#BBBBBB"));
        d.setTextSize(13);
        parent.addView(d);
    }
}
