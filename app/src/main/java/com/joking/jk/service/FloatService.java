package com.joking.jk.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.joking.jk.R;
import com.joking.jk.base.BaseService;
import com.joking.jk.utils.SharedPreferencesUtils;

public class FloatService extends BaseService {

    private WindowManager.LayoutParams params;
    private int winWidth;
    private int winHeight;
    private WindowManager mWM;

    private int startX;
    private int startY;
    private ImageView iv_float;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        params.gravity = Gravity.START + Gravity.TOP;// 将重心位置设置为左上方,
        // 也就是(0,0)从左上方开始,而不是默认的重心位置

        int floatX = SharedPreferencesUtils.getInt(this, "floatX", 0);
        int floatY = SharedPreferencesUtils.getInt(this, "floatY", 0);
        params.x = floatX;
        params.y = floatY;

        iv_float = new ImageView(this);
        iv_float.setImageResource(R.mipmap.snowman);

        mWM.addView(iv_float, params);

        iv_float.setOnTouchListener(new OnTouchListener() {

            private boolean flag = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        //初始化标记
                        flag = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        flag = false;

                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        // 计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新浮窗位置
                        params.x += dx;
                        params.y += dy;

                        // 防止坐标偏离屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        // 防止坐标偏离屏幕
                        if (params.x > winWidth - iv_float.getWidth()) {
                            params.x = winWidth - iv_float.getWidth();
                        }

                        if (params.y > winHeight - iv_float.getHeight()) {
                            params.y = winHeight - iv_float.getHeight();
                        }

                        // System.out.println("x:" + params.x + ";y:" + params.y);

                        mWM.updateViewLayout(iv_float, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (flag) {
                            //清理内存
                            Intent intent = new Intent();
                            //发送一个隐式意图
                            intent.setAction("com.joking.jk.KillAllProcess");
                            sendBroadcast(intent);
                            //动画
                            showAnim();
                        } else {
                            //动画
                            if (params.x < winWidth / 2) {
                                params.x = 0;
                            } else {
                                params.x = winWidth - iv_float.getWidth();
                            }

                            mWM.updateViewLayout(iv_float, params);
                            SharedPreferencesUtils.setInt(FloatService.this, "floatX", params.x);
                            SharedPreferencesUtils.setInt(FloatService.this, "floatY", params.y);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void showAnim() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWM != null && iv_float != null) {
            mWM.removeView(iv_float);
            iv_float = null;
        }
    }
}
