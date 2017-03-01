package com.joking.jk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.service.AddressService;
import com.joking.jk.service.CallSmsSafeService;
import com.joking.jk.service.FloatService;
import com.joking.jk.service.WatchDogService;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.SystemUtils;
import com.joking.jk.view.SettingClickView;
import com.joking.jk.view.SettingItemView;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

    private SettingItemView siv_update;
    private SettingItemView siv_address;
    private SettingClickView scv_addressstyle;
    private SettingClickView scv_addresslocation;
    private SettingItemView siv_floatwindow;
    private SettingItemView siv_callsafe;
    private SettingItemView siv_applock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initData();
    }

    private void initViews() {
        setContentView(R.layout.activity_setting);

        initUpdateView();
        initAddressView();
        initAddressStyle();
        initAddressLocation();
        initRocketView();
        initBlackNumberView();
        initAppLock();
    }

    private void initData() {

    }

    private void initUpdateView() {
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
        boolean auto_update = SharedPreferencesUtils.getBoolean(this, "auto_update", true);
        if (auto_update) {
            siv_update.setCheckedAndDesc(true);
        } else {
            siv_update.setCheckedAndDesc(false);
        }

        siv_update.setOnClickListener(this);
    }

    private void initAddressView() {
        siv_address = (SettingItemView) findViewById(R.id.siv_address);
        // 根据归属地服务是否运行来更新checkbox
        boolean serviceRunning = SystemUtils.isServiceRunning(this,
                "com.joking.jk.service.AddressService");
        if (serviceRunning) {
            siv_address.setCheckedAndDesc(true);
        } else {
            siv_address.setCheckedAndDesc(false);
        }

        siv_address.setOnClickListener(this);
    }

    private void initAddressStyle() {
        scv_addressstyle = (SettingClickView) findViewById(R.id.scv_addressstyle);
        int style = SharedPreferencesUtils.getInt(this, "address_style", 2);
        scv_addressstyle.setDesc(items[style]);

        scv_addressstyle.setOnClickListener(this);
    }

    private void initAddressLocation() {
        scv_addresslocation = (SettingClickView) findViewById(R.id.scv_addresslocation);

        scv_addresslocation.setOnClickListener(this);
    }

    private void initRocketView() {
        siv_floatwindow = (SettingItemView) findViewById(R.id.siv_floatwindow);

        // 根据悬浮窗服务是否运行来更新checkbox
        boolean serviceRunning = SystemUtils.isServiceRunning(this,
                "com.joking.jk.service.FloatService");
        if (serviceRunning) {
            siv_floatwindow.setCheckedAndDesc(true);
        } else {
            siv_floatwindow.setCheckedAndDesc(false);
        }

        siv_floatwindow.setOnClickListener(this);
    }

    private void initBlackNumberView() {
        siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);
        // 根据拦截黑名单服务是否运行来更新checkbox
        boolean serviceRunning = SystemUtils.isServiceRunning(this,
                "com.joking.jk.service.CallSmsSafeService");
        if (serviceRunning) {
            siv_callsafe.setCheckedAndDesc(true);
        } else {
            siv_callsafe.setCheckedAndDesc(false);
        }

        siv_callsafe.setOnClickListener(this);
    }

    private void initAppLock() {
        siv_applock = (SettingItemView) findViewById(R.id.siv_applock);

        // 根据程序锁服务是否运行来更新checkbox
        boolean serviceRunning = SystemUtils.isServiceRunning(this,
                "com.joking.jk.service.WatchDogService");
        if (serviceRunning) {
            siv_applock.setCheckedAndDesc(true);
        } else {
            siv_applock.setCheckedAndDesc(false);
        }

        siv_applock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.siv_update:
                if (siv_update.isChecked()) {
                    siv_update.setCheckedAndDesc(false);
                    SharedPreferencesUtils.setBoolean(this, "auto_update", false);
                } else {
                    siv_update.setCheckedAndDesc(true);
                    SharedPreferencesUtils.setBoolean(this, "auto_update", true);
                }
                break;

            case R.id.siv_address:
                if (siv_address.isChecked()) {
                    siv_address.setCheckedAndDesc(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));// 停止归属地服务
                } else {
                    siv_address.setCheckedAndDesc(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));// 开启归属地服务
                }
                break;

            case R.id.scv_addressstyle:
                showSingleChooseDailog();
                break;

            case R.id.scv_addresslocation:
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
                break;

            case R.id.siv_floatwindow:
                if (siv_floatwindow.isChecked()) {
                    siv_floatwindow.setCheckedAndDesc(false);
                    stopService(new Intent(SettingActivity.this, FloatService.class));// 停止悬浮窗服务
                } else {
                    siv_floatwindow.setCheckedAndDesc(true);
                    startService(new Intent(SettingActivity.this, FloatService.class));// 开启悬浮窗服务
                }
                break;

            case R.id.siv_callsafe:
                if (siv_callsafe.isChecked()) {
                    siv_callsafe.setCheckedAndDesc(false);
                    stopService(new Intent(SettingActivity.this, CallSmsSafeService.class));// 停止黑名单拦截服务
                } else {
                    siv_callsafe.setCheckedAndDesc(true);
                    startService(new Intent(SettingActivity.this, CallSmsSafeService.class));// 开启黑名单拦截服务
                }
                break;

            case R.id.siv_applock:
                if (siv_applock.isChecked()) {
                    siv_applock.setCheckedAndDesc(false);
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));// 停止程序锁服务
                } else {
                    siv_applock.setCheckedAndDesc(true);
                    startService(new Intent(SettingActivity.this, WatchDogService.class));// 开启程序锁服务
                }
                break;
        }
    }

    /**
     * 弹出样式选择框
     */
    private void showSingleChooseDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");

        int style = SharedPreferencesUtils.getInt(SettingActivity.this, "address_style", 2);

        builder.setSingleChoiceItems(items, style,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferencesUtils.setInt(SettingActivity.this, "address_style", which);
                        // 保存选择的风格
                        dialog.dismiss();// 让dialog消失

                        scv_addressstyle.setDesc(items[which]);// 更新组合控件的描述信息
                    }
                });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
