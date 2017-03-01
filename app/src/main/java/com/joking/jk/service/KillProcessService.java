package com.joking.jk.service;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.joking.jk.base.BaseService;
import com.joking.jk.receiver.KillProcessAllReceiver;

public class KillProcessService extends BaseService {

    private KillProcessAllReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new KillProcessAllReceiver();
        //锁屏的过滤器
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //注册一个锁屏的广播
        registerReceiver(receiver, filter);

//        Timer timer = new Timer();
//
//        TimerTask task = new TimerTask() {
//
//            @Override
//            public void run() {
//                // 写我们的业务逻辑
//                System.out.println("我被调用了");
//            }
//        };
//        //进行定时调度
//        /**
//         * 第一个参数  表示用那个类进行调度
//         *
//         * 第二个参数表示时间
//         */
//        timer.schedule(task, 0, 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //当应用程序推出的时候。需要把广播反注册掉
        unregisterReceiver(receiver);
        //手动回收
        receiver = null;
    }

}
