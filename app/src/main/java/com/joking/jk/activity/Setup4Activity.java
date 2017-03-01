package com.joking.jk.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.joking.jk.R;
import com.joking.jk.base.BaseSetupActivity;
import com.joking.jk.receiver.AdminReceiver;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;

public class Setup4Activity extends BaseSetupActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;

    private CheckBox cb_protect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
        mDeviceAdmin = new ComponentName(this, AdminReceiver.class);// 设备管理组件

        cb_protect = (CheckBox) findViewById(R.id.cb_protect);

        boolean protect = SharedPreferencesUtils.getBoolean(this, "protect", false);
        // 根据sp保存的状态,更新checkbox
        if (protect) {
            cb_protect.setText("防盗保护已经开启");
            cb_protect.setChecked(true);
        } else {
            cb_protect.setText("防盗保护没有开启");
            cb_protect.setChecked(false);
        }

        // 当checkbox发生变化时,回调此方法
        cb_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开启设备管理器
                    activeAdmin();

                    cb_protect.setText("防盗保护已经开启");
                } else {
                    //关闭设备管理器
                    removeAdmin();

                    System.out.println("333333333333333");
                    cb_protect.setText("防盗保护没有开启");
                    SharedPreferencesUtils.setBoolean(Setup4Activity.this, "protect", false);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mDPM.isAdminActive(mDeviceAdmin)) {// 判断设备管理器是否已经激活
            System.out.println("11111111111111");

            cb_protect.setText("防盗保护已经开启");
            cb_protect.setChecked(true);
            SharedPreferencesUtils.setBoolean(Setup4Activity.this, "protect", true);
        } else {
            System.out.println("222222222222");
            ToastUtils.showShortToast(Setup4Activity.this, "必须先激活设备管理器!");

            cb_protect.setText("防盗保护没有开启");
            cb_protect.setChecked(false);
            SharedPreferencesUtils.setBoolean(Setup4Activity.this, "protect", false);
        }
    }

    // 激活设备管理器, 也可以在设置->安全->设备管理器中手动激活
    private void activeAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "哈哈哈, 我们有了超级设备管理器, 好NB!");
        startActivityForResult(intent, 0);
    }

    private void removeAdmin() {
        mDPM.removeActiveAdmin(mDeviceAdmin);// 取消激活
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, LostFindActivity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画

        SharedPreferencesUtils.setBoolean(this, "configed", true);// 更新sp,表示已经展示过设置向导了,下次进来就不展示啦
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);// 进入动画和退出动画
    }
}
