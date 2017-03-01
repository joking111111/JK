package com.joking.jk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;
import com.joking.jk.view.ConfirmPasswordDialog;

public class LostFindActivity extends BaseActivity {

    private TextView tv_safephone;
    private TextView tv_protect;
    private ImageView iv_protect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean configed = SharedPreferencesUtils.getBoolean(this, "configed", false);// 判断是否进入过设置向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);

            tv_safephone = (TextView) findViewById(R.id.tv_safephone);
            tv_protect = (TextView) findViewById(R.id.tv_protect);
            iv_protect = (ImageView) findViewById(R.id.iv_protect);

            // 根据sp更新安全号码
            String phone = SharedPreferencesUtils.getString(this, "safe_phone", "");
            tv_safephone.setText(phone);

            // 根据sp更新保护锁
            boolean protect = SharedPreferencesUtils.getBoolean(this, "protect", false);
            if (protect) {
                tv_protect.setText("防盗保护已开启");
                iv_protect.setImageResource(R.mipmap.lock);
            } else {
                tv_protect.setText("防盗保护未开启");
                iv_protect.setImageResource(R.mipmap.unlock);
            }
        } else {
            // 跳转设置向导页
            startActivity(new Intent(this, Setup1Activity.class));
            finish();
        }
    }

    public void resetPassword(View view) {
        final String password = SharedPreferencesUtils.getString(this, "password", "");
        SharedPreferencesUtils.setString(this, "password", "");
        ConfirmPasswordDialog confirmPasswordDialog = new ConfirmPasswordDialog();
        confirmPasswordDialog.setDialogListener(new ConfirmPasswordDialog.DialogListener() {
            @Override
            public void confirmed() {
                ToastUtils.showShortToast(LostFindActivity.this, "修改成功！");
            }

            @Override
            public void quitReset() {
                ToastUtils.showShortToast(LostFindActivity.this, "密码不变！");
                SharedPreferencesUtils.setString(LostFindActivity.this, "password", password);
            }
        });
        confirmPasswordDialog.show(LostFindActivity.this, "password");
    }

    /**
     * 重新进入设置向导
     *
     * @param view
     */
    public void reEnter(View view) {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }
}
