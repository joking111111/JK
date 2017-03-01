package com.joking.jk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.joking.jk.R;
import com.joking.jk.base.BaseSetupActivity;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;

public class Setup3Activity extends BaseSetupActivity {

    private EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        et_phone = (EditText) findViewById(R.id.et_phone);
        String phone = SharedPreferencesUtils.getString(this, "safe_phone", "");
        et_phone.setText(phone);

        /**
         * 方法一
         * int i = Integer.parseInt("t")；
         * 检查输入的每个字符是否是数字
         * 注意要try catch
         *
         * 方法二
         * 正则表达式
         */
//        et_phone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    public void showNextPage() {
        String phone = et_phone.getText().toString().trim();// 注意过滤空格

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShortToast(this, "安全号码不能为空!");
            return;
        }

        SharedPreferencesUtils.setString(this, "safe_phone", phone);// 保存安全号码

        startActivity(new Intent(this, Setup4Activity.class));
        finish();
        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);// 进入动画和退出动画
    }

    public void selectContact(View view) {
        startActivityForResult(new Intent(this, ContactAvtivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//用户什么也不点，直接返回
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");// 替换-和空格

            et_phone.setText(phone);// 把电话号码设置给输入框
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
