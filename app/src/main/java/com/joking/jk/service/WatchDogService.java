package com.joking.jk.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.joking.jk.activity.EnterPwdActivity;
import com.joking.jk.base.BaseService;
import com.joking.jk.db.dao.AppLockDao;

import java.util.List;

public class WatchDogService extends BaseService {

    private ActivityManager activityManager;
    private List<String> appLockInfos;
    private WatchDogReceiver receiver;

    //标记当前的看萌狗是否停下来
    private boolean flag = false;
    private AppLockDao dao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //临时停止保护的包名
    private String tempStopProtectPackageName;

    private class WatchDogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.joking.jk.changed")) {
                // 更新加锁的应用列表
                appLockInfos = dao.findAll();
            } else if (intent.getAction().equals("com.joking.jk.stopprotect")) {
                // 获取到停止保护的对象
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                tempStopProtectPackageName = null;
                // 让狗休息
                flag = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //让狗继续干活
                if (!flag) {
                    startWatDog();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new AppLockDao(this);
        appLockInfos = dao.findAll();

        /**
         * 更新加锁应用列表，另一个解决方案是自定义contentProvider
         * 停止保护
         * 当屏幕锁住的时候。狗就休息
         * 屏幕解锁的时候。让狗活过来
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.joking.jk.changed");
        filter.addAction("com.joking.jk.stopprotect");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        //注册广播接受者
        receiver = new WatchDogReceiver();
        registerReceiver(receiver, filter);

        //获取到进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //1 首先需要获取到当前的任务栈
        //2取任务栈最上面的任务
        startWatDog();
    }

    private void startWatDog() {

        new Thread() {
            public void run() {
                flag = true;
                while (flag) {
                    //由于这个狗一直在后台运行。为了避免程序阻塞。
                    //获取到当前正在运行的任务栈
                    List<RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
                    //获取到最上面的进程
                    RunningTaskInfo taskInfo = tasks.get(0);
                    //获取到最顶端应用程序的包名
                    String packageName = taskInfo.topActivity.getPackageName();

                    //直接从数据库里面查找当前的数据
                    //这个可以优化。改成从内存当中寻找
                    if (appLockInfos.contains(packageName)) {
                        //说明需要临时取消保护
                        //是因为用户输入了正确的密码
                        if (packageName.equals(tempStopProtectPackageName)) {

                        } else {
                            // 输入密码
                            Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                            /**
                             * 需要注意：如果是在服务里面往activity界面跳的话。需要设置flag
                             */
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);

                            startActivity(intent);
                        }
                    }

                    SystemClock.sleep(100);//让狗休息，不要过多占用cpu
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
    }
}
