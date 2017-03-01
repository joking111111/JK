package com.joking.jk.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 关于屏幕的工具
 */

public class WindowUtils {
    /**
     * 获取状态栏高度
     * 感觉有问题！！！！！！！
     *
     * @param v
     * @return
     */
    public static int getStatusBarHeight(View v) {
        if (v == null) {
            return 0;
        }
        Rect frame = new Rect();
        v.getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取屏幕宽度
     * @param activity
     * @return
     */
    public static int getWindowWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     * @param activity
     * @return
     */
    public static int getWindowHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 去除标题栏
     *
     * @param activity
     */
    public static void WindowNoTitle(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 不能实现
     */
//    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
//    <item name="windowFullscreen">true</item>
//    <item name="windowContentOverlay">@null</item>
//    </style>

    /**
     * 去除状态栏
     *
     * @param activity
     */
    public static void WindowFullScreen(Activity activity) {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setFlags(flag, flag);
    }
}
