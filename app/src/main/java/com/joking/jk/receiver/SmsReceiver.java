package com.joking.jk.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.joking.jk.R;
import com.joking.jk.base.BaseBroadcastReceiver;
import com.joking.jk.service.LocationService;
import com.joking.jk.utils.SharedPreferencesUtils;

/**
 * 拦截短信
 *
 * @author Kevin
 */
public class SmsReceiver extends BaseBroadcastReceiver {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    private String password;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean protect = SharedPreferencesUtils.getBoolean(context, "protect", true);

        if (protect) {
            /**
             * String在jdk1.7一下是不能用switch的,只能用equals
             */
            mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
            mDeviceAdmin = new ComponentName(context, AdminReceiver.class);// 设备管理组件

            password = SharedPreferencesUtils.getString(context, "password", "");

            switch (intent.getAction()) {
                case "android.provider.Telephony.SMS_RECEIVED":

                    Object[] objects = (Object[]) intent.getExtras().get("pdus");

                    for (Object object : objects) {// 短信最多140字节,
                        // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
                        SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                        String originatingAddress = message.getOriginatingAddress();// 短信来源号码
                        String messageBody = message.getMessageBody();// 短信内容

                        System.out.println(originatingAddress + ":" + messageBody);

                        if ("#*alarm*#".equals(messageBody)) {
                            System.out.println("播放报警音乐");
                            // 播放报警音乐, 即使手机调为静音,也能播放音乐, 因为使用的是媒体声音的通道,和铃声无关
                            MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                            player.setVolume(1f, 1f);
                            player.setLooping(true);
                            player.start();

                            abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
                        } else if ("#*location*#".equals(messageBody)) {
                            System.out.println("获取经纬度坐标");
                            //获取的坐标没有进行火星坐标修正
                            context.startService(new Intent(context, LocationService.class));// 开启定位服务

                            abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
                        } else if ("#*wipedata*#".equals(messageBody)) {
                            System.out.println("远程清除数据");

                            wipeData();

                            abortBroadcast();
                        } else if ("#*lockscreen*#".equals(messageBody)) {
                            System.out.println("远程锁屏");

                            lockScreen();

                            abortBroadcast();
                        }
                    }

                    break;
                case "getLocation":

                    //已经获取到坐标位置
//            String location = intent.getStringExtra("location");
                    String location = SharedPreferencesUtils.getString(context, "location", "getting location...");
                    String safephone = SharedPreferencesUtils.getString(context, "safe_phone", "");

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(safephone, null, location, null, null);

                    System.out.println("location:" + location);
                    abortBroadcast();

                    break;
            }

        }
    }

    // 一键锁屏
    private void lockScreen() {
        if (mDPM.isAdminActive(mDeviceAdmin)) {// 判断设备管理器是否已经激活
            mDPM.lockNow();// 立即锁屏

            mDPM.resetPassword(password, 0);
        }
    }

    // 清除数据
    private void wipeData() {
        if (mDPM.isAdminActive(mDeviceAdmin)) {// 判断设备管理器是否已经激活
            mDPM.wipeData(0);// 清除数据,恢复出厂设置
        }
    }
}
