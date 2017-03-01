package com.joking.jk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseService;
import com.joking.jk.db.dao.AddressDao;
import com.joking.jk.utils.SharedPreferencesUtils;

/**
 * 来电提醒的服务
 *
 * @author Kevin
 */
public class AddressService extends BaseService {

    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWM;
    private View view;
    private int winWidth;
    private int winHeight;
    private WindowManager.LayoutParams params;
    private int startX;
    private int startY;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();

        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 监听来电的状态

        receiver = new OutCallReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);// 动态注册广播
    }

    class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:// 电话铃声响了
                    System.out.println("电话铃响...");
                    String address = AddressDao.getAddress(incomingNumber);// 根据来电号码查询归属地
                    showToast(address);// 浮窗
                    break;

                case TelephonyManager.CALL_STATE_IDLE:// 电话闲置状态
                    if (mWM != null && view != null) {
                        mWM.removeView(view);// 从window中移除浮窗
                        view = null;
                    }
                    break;
                default:
                    break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }

    }

    /**
     * 监听去电的广播接受者 需要权限: android.permission.PROCESS_OUTGOING_CALLS
     *
     * @author Kevin
     */
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();// 获取去电电话号码

            String address = AddressDao.getAddress(number);
            // Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            showToast(address);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);// 停止来电监听

        unregisterReceiver(receiver);// 注销广播
        receiver = null;
    }

    /**
     * 自定义归属地浮窗
     */
    private void showToast(String text) {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        //使用父布局的layoutparams
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。

        params.gravity = Gravity.START + Gravity.TOP;// 将重心位置设置为左上方,
        // 也就是(0,0)从左上方开始,而不是默认的重心位置

        int lastX = SharedPreferencesUtils.getInt(this, "lastX", 0);
        int lastY = SharedPreferencesUtils.getInt(this, "lastY", 0);

        // 设置浮窗的位置, 基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;

        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.mipmap.call_locate_white,
                R.mipmap.call_locate_orange, R.mipmap.call_locate_blue,
                R.mipmap.call_locate_gray, R.mipmap.call_locate_green};

        int style = SharedPreferencesUtils.getInt(this, "address_style", 2);

        view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView textView = (TextView) view.findViewById(R.id.tv_number);
        textView.setText(text);

        mWM.addView(view, params);// 将view添加在屏幕上(Window)

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
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
                        if (params.x > winWidth - view.getWidth()) {
                            params.x = winWidth - view.getWidth();
                        }

                        if (params.y > winHeight - view.getHeight()) {
                            params.y = winHeight - view.getHeight();
                        }

                        // System.out.println("x:" + params.x + ";y:" + params.y);

                        mWM.updateViewLayout(view, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 记录坐标点
                        SharedPreferencesUtils.setInt(AddressService.this, "lastX", params.x);
                        SharedPreferencesUtils.setInt(AddressService.this, "lastY", params.y);
                        break;
                }
                return true;//拦截事件
            }
        });
    }

}
