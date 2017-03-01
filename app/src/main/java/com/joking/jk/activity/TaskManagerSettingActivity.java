package com.joking.jk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.service.KillProcessService;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.SystemUtils;

public class TaskManagerSettingActivity extends BaseActivity {

    private SharedPreferences sp;
    private CheckBox cb_status_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_task_manager_setting);
        CheckBox cb_status = (CheckBox) findViewById(R.id.cb_status);

        //设置是否选中
        cb_status.setChecked(SharedPreferencesUtils.getBoolean(
                TaskManagerSettingActivity.this, "is_show_system", false));

        cb_status.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setBoolean(TaskManagerSettingActivity.this, "is_show_system", isChecked);
            }
        });

        //定时清理进程

        cb_status_kill_process = (CheckBox) findViewById(R.id.cb_status_kill_process);

        if (SystemUtils.isServiceRunning(TaskManagerSettingActivity.this,
                "com.joking.jk.service.KillProcessService")) {
            cb_status_kill_process.setChecked(true);
        } else {
            cb_status_kill_process.setChecked(false);
        }

        final Intent intent = new Intent(this, KillProcessService.class);

        cb_status_kill_process.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }
}
