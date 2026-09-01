package com.aceboost;

final class HostApp {

    static final HostApp WECHAT = new HostApp(
            "com.tencent.mm",
            "com.tencent.mm.ui.LauncherUI",
            new String[]{"com.tencent.mm.ui.LauncherUIBottomTabView"},
            new String[]{"setTo"},
            "getCurIdx",
            new String[0],
            new String[]{"TabIconView"},
            false,
            "com.tencent.mm.ui.");

    static final HostApp QQ = new HostApp(
            "com.tencent.mobileqq",
            "com.tencent.mobileqq.activity.SplashActivity",
            new String[]{
                    "com.tencent.mobileqq.widget.QQTabWidget",
                    "com.tencent.mobileqq.widget.QQTabLayout",
            },
            new String[]{"setCurrentTab"},
            "getCurrentTab",
            new String[]{"com.tencent.qui.quiblurview.QQBlurViewWrapper"},
            new String[]{"TabDragAnimationView"},
            true,
            "com.tencent.mobileqq.");

    static final HostApp DOUYIN = new HostApp(
            "com.ss.android.ugc.aweme",
            "",
            new String[0],
            new String[0],
            "",
            new String[0],
            new String[0],
            false,
            "com.ss.android.ugc.aweme.");

    static final HostApp DOUYIN_LITE = new HostApp(
            "com.ss.android.ugc.aweme.lite",
            "",
            new String[0],
            new String[0],
            "",
            new String[0],
            new String[0],
            false,
            "com.ss.android.ugc.aweme.lite.");

    static final HostApp KUAISHOU = new HostApp(
            "com.smile.gifmaker",
            "",
            new String[0],
            new String[0],
            "",
            new String[0],
            new String[0],
            false,
            "com.smile.gifmaker.");

    static final HostApp KUAISHOU_NEBULA = new HostApp(
            "com.kuaishou.nebula",
            "",
            new String[0],
            new String[0],
            "",
            new String[0],
            new String[0],
            false,
            "com.kuaishou.nebula.");

    private static final HostApp[] ALL = {
            WECHAT, QQ, DOUYIN, DOUYIN_LITE, KUAISHOU, KUAISHOU_NEBULA
    };

    final String pkg;
    final String launcherActivity;
    final String[] tabViewClasses;
    final String[] tabSwitchMethods;
    final String currentIndexMethod;
    final String[] hiddenSiblings;
    final String[] iconClassSuffixes;
    final boolean preferTextColorProbe;
    final String uiPrefix;

    private HostApp(String pkg, String launcherActivity, String[] tabViewClasses,
                    String[] tabSwitchMethods, String currentIndexMethod,
                    String[] hiddenSiblings, String[] iconClassSuffixes,
                    boolean preferTextColorProbe, String uiPrefix) {
        this.pkg = pkg;
        this.launcherActivity = launcherActivity;
        this.tabViewClasses = tabViewClasses;
        this.tabSwitchMethods = tabSwitchMethods;
        this.currentIndexMethod = currentIndexMethod;
        this.hiddenSiblings = hiddenSiblings;
        this.iconClassSuffixes = iconClassSuffixes;
        this.preferTextColorProbe = preferTextColorProbe;
        this.uiPrefix = uiPrefix;
    }

    static HostApp forProcess(String processName) {
        for (HostApp app : ALL) {
            if (app.pkg.equals(processName)) {
                return app;
            }
        }
        return null;
    }

    static HostApp forPackage(String packageName) {
        for (HostApp app : ALL) {
            if (app.pkg.equals(packageName)) {
                return app;
            }
        }
        return null;
    }

    boolean isTabViewClass(String className) {
        for (String c : tabViewClasses) {
            if (c.equals(className)) {
                return true;
            }
        }
        return false;
    }

    boolean isHiddenSibling(String className) {
        for (String c : hiddenSiblings) {
            if (c.equals(className)) {
                return true;
            }
        }
        return false;
    }

    boolean isTabIconClass(String className) {
        for (String suffix : iconClassSuffixes) {
            if (className.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return pkg;
    }
}
