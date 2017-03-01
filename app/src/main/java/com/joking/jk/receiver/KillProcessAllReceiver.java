package com.joking.jk.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.joking.jk.base.BaseBroadcastReceiver;

import java.util.List;

public class KillProcessAllReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //得到手机上面正在运行的进程
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
            //杀死所有的进程
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
        Toast.makeText(context, "清理完毕", Toast.LENGTH_SHORT).show();
    }
}
