package com.joking.jk.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Administrator on 2016/10/11.
 */

public class SystemUtils {
    /**
     * 获取sim序列号
     *
     * @param context
     * @return
     */
    public static String getSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();// 获取sim卡序列号
    }

    /**
     * 检测服务是否正在运行
     *
     * @return
     */
    public static boolean isServiceRunning(Context ctx, String serviceName) {

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);// 获取系统所有正在运行的服务,最多返回100个

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();// 获取服务的名称
            // System.out.println(className);
            if (className.equals(serviceName)) {// 服务存在
                return true;
            }
        }

        return false;
    }

    /**
     * 返回进程的总个数
     *
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        // 得到进程管理者
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        // 获取到当前手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager
                .getRunningAppProcesses();

        // 获取手机上面一共有多少个进程
        return runningAppProcesses.size();
    }

    /**
     * 返回可用运行内存
     *
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        // 得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        // 获取到内存的基本信息
        activityManager.getMemoryInfo(memoryInfo);
        // 获取到剩余内存
        return memoryInfo.availMem;
    }

    /**
     * 返回总运行内存
     *
     * @param context
     * @return
     */
    public static long getTotalMem(Context context) {
        // 获取到总内存
        /*
         * 这个地方不能直接跑到低版本的手机上面 MemTotal: 344740 kB "/proc/meminfo"
		 */
        try {
            // /proc/meminfo 配置文件的路径
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String readLine = reader.readLine();

            StringBuilder sb = new StringBuilder();

            for (char c : readLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
