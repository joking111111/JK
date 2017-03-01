package com.joking.jk.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.view.ConfirmPasswordDialog;
import com.joking.jk.utils.SmsUtils;
import com.joking.jk.utils.ToastUtils;

/**
 * 高级工具
 */
public class AdvancedToolsActivity extends BaseActivity {

    private ProgressDialog pd;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //失败
                    ToastUtils.showShortToast(AdvancedToolsActivity.this, "备份失败");
                    break;
                case 1:
                    //成功
                    ToastUtils.showShortToast(AdvancedToolsActivity.this, "备份成功");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_tools);
    }

    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    public void backupSms(View view) {
        //初始化一个进度条的对话框
        pd = new ProgressDialog(AdvancedToolsActivity.this);
        pd.setTitle("提示");
        pd.setMessage("稍安勿躁。正在备份。。。");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        new Thread() {
            public void run() {
                boolean result = SmsUtils.backUp(AdvancedToolsActivity.this, new SmsUtils.BackUpCallBackSms() {

                    @Override
                    public void onBackUpSms(int process) {
                        pd.setProgress(process);
                    }

                    @Override
                    public void onPre(int count) {
                        pd.setMax(count);
                    }
                });
                if (result) {
                    //安全弹吐司的方法
                    mHandler.sendEmptyMessage(1);
                } else {
                    mHandler.sendEmptyMessage(0);
                }
                pd.dismiss();
            }
        }.start();
    }

    public void appLock(View view) {
        ConfirmPasswordDialog confirmPasswordDialog = new ConfirmPasswordDialog();
        confirmPasswordDialog.setDialogListener(new ConfirmPasswordDialog.DialogListener() {
            @Override
            public void confirmed() {
                startActivity(new Intent(AdvancedToolsActivity.this, AppLockActivity.class));
            }

            @Override
            public void quitReset() {

            }
        });
        confirmPasswordDialog.show(AdvancedToolsActivity.this, "lock");
    }
}
