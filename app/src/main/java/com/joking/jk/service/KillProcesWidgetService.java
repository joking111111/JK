package com.joking.jk.service;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.joking.jk.R;
import com.joking.jk.base.BaseService;
import com.joking.jk.receiver.MyAppWidget;
import com.joking.jk.utils.SystemUtils;

import java.util.Timer;
import java.util.TimerTask;


public class KillProcesWidgetService extends BaseService {

    private AppWidgetManager widgetManager;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //桌面小控件的管理者
        widgetManager = AppWidgetManager.getInstance(this);
        //每隔5秒钟更新一次桌面
        //初始化定时器
        timer = new Timer();
        //初始化一个定时任务
        timerTask = new TimerTask() {

            @Override
            public void run() {

                System.out.println("KillProcesWidgetService");
                //这个是把当前的布局文件添加进行
                /**
                 * 初始化一个远程的view
                 * Remote 远程
                 */
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                /**
                 * 需要注意。这个里面findingviewyid这个方法
                 * 设置当前文本里面一共有多少个进程
                 */
                //设置文本
                int processCount = SystemUtils.getProcessCount(getApplicationContext());
                views.setTextViewText(R.id.process_count, "正在运行的软件:" + String.valueOf(processCount));
                //获取到当前手机上面的可用内存
                long availMem = SystemUtils.getAvailMem(getApplicationContext());
                views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(), availMem));

                Intent intent = new Intent();
                //发送一个隐式意图
                intent.setAction("com.joking.jk.KillAllProcess");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                //设置点击事件
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                //第一个参数表示上下文
                //第二个参数表示当前有哪一个广播进行去处理当前的桌面小控件
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidget.class);

                //更新桌面
                widgetManager.updateAppWidget(provider, views);
            }
        };
        //从0开始。每隔5秒钟更新一次
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //优化代码
        if (timer != null || timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

}
