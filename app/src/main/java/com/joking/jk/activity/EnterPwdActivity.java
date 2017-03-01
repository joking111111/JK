package com.joking.jk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.utils.MD5Utils;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;

public class EnterPwdActivity extends BaseActivity {

    private String packageName;
    private TextView applock_password;
    private String lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {

        setContentView(R.layout.activity_enter_pwd);

        Intent intent = getIntent();

        if (intent != null) {
            packageName = intent.getStringExtra("packageName");
        }
//		// 隐藏当前的键盘
//		et_pwd.setInputType(InputType.TYPE_NULL);
//		et_pwd.setText(str.substring(0, str.length() - 1));

        applock_password = (TextView) findViewById(R.id.applock_password);
        lock = SharedPreferencesUtils.getString(this, "lock", "");
    }

    public void number(View view) {

        String password = applock_password.getText().toString();

        switch (view.getId()) {
            case R.id.btn0:
                password += "0";
                break;
            case R.id.btn1:
                password += "1";
                break;
            case R.id.btn2:
                password += "2";
                break;
            case R.id.btn3:
                password += "3";
                break;
            case R.id.btn4:
                password += "4";
                break;
            case R.id.btn5:
                password += "5";
                break;
            case R.id.btn6:
                password += "6";
                break;
            case R.id.btn7:
                password += "7";
                break;
            case R.id.btn8:
                password += "8";
                break;
            case R.id.btn9:
                password += "9";
                break;
            case R.id.btn_cancel:
                password = "";
                break;
            case R.id.btn_ok:
                if (MD5Utils.encode(password).equals(lock)) {
                    // 如果密码正确。说明是自己人
                    /**
                     * 是自己家人。不要拦截他
                     */
                    System.out.println("密码输入正确");

                    Intent intent = new Intent();
                    // 发送广播。停止保护
                    intent.setAction("com.joking.jk.stopprotect");
                    // 跟狗说。现在停止保护短信
                    intent.putExtra("packageName", packageName);
                    // 发送后如果再次打开同一个应用，不会有密码输入
                    // 若打开另一个加锁的app，就会有密码输入
                    // 锁屏一下就可以再次弹出输入密码

                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtils.showShortToast(EnterPwdActivity.this, "密码错误");
                }
                break;
        }

        applock_password.setText(password);
    }

    // 监听当前页面的后退健
    // <intent-filter>
    // <action android:name="android.intent.action.MAIN" />
    // <category android:name="android.intent.category.HOME" />
    // <category android:name="android.intent.category.DEFAULT" />
    // <category android:name="android.intent.category.MONKEY"/>
    // </intent-filter>
    @Override
    public void onBackPressed() {
        // 当用户输入后退健 的时候。我们进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);

        finish();
    }
}
