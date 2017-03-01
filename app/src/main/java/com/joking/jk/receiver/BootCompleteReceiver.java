package com.joking.jk.receiver;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.joking.jk.base.BaseBroadcastReceiver;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.SystemUtils;

/**
 * 监听手机开机启动的广播
 *
 * @author Kevin
 */
public class BootCompleteReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean protect = SharedPreferencesUtils.getBoolean(context, "protect", false);
        // 只有在防盗保护开启的前提下才进行sim卡判断
        if (protect) {
            String sim = SharedPreferencesUtils.getString(context, "sim", "");// 获取绑定的sim卡

            if (!TextUtils.isEmpty(sim)) {
                // 获取当前手机的sim卡
                String currentSim = SystemUtils.getSim(context);

                if (sim.equals(currentSim)) {
                    System.out.println("手机安全");
                } else {
                    System.out.println("sim卡已经变化, 发送报警短信!!!");
                    String phone = SharedPreferencesUtils.getString(context, "safe_phone", "");// 读取安全号码

                    // 发送短信给安全号码
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "sim card changed!", null, null);
                }
            }
        }
    }

}
