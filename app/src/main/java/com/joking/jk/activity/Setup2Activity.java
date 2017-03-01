package com.joking.jk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.joking.jk.base.BaseSetupActivity;
import com.joking.jk.R;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.SystemUtils;
import com.joking.jk.utils.ToastUtils;
import com.joking.jk.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_sim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        siv_sim = (SettingItemView) findViewById(R.id.siv_sim);

        String sim = SharedPreferencesUtils.getString(Setup2Activity.this, "sim", "");
        if (!TextUtils.isEmpty(sim)) {
            siv_sim.setCheckedAndDesc(true);
        } else {
            siv_sim.setCheckedAndDesc(false);
        }

        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_sim.isChecked()) {
                    siv_sim.setCheckedAndDesc(false);
                } else {
                    siv_sim.setCheckedAndDesc(true);
                    //保存SIM卡
                    SharedPreferencesUtils.setString(Setup2Activity.this, "sim",
                            SystemUtils.getSim(Setup2Activity.this));
                    // 将sim卡序列号保存在sp中
                }
            }
        });
    }

    @Override
    public void showNextPage() {
        // 如果sim卡没有绑定,就不允许进入下一个页面
        String sim = SharedPreferencesUtils.getString(this, "sim", "");
        if (TextUtils.isEmpty(sim)) {
            ToastUtils.showShortToast(this, "必须绑定sim卡!");
            return;
        }

        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);// 进入动画和退出动画
    }
}
