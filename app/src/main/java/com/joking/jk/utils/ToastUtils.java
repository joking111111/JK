package com.joking.jk.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 单例模式
 * 可在子线程中使用
 */

public class ToastUtils {
    public static Toast mToast;

    public static void showShortToast(final Activity activity, final String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);

        if ("main".equals(Thread.currentThread().getName())) {
            mToast.show();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mToast.show();
                }
            });
        }
    }
}
