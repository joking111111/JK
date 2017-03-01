package com.joking.jk.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.fragment.LockFragment;
import com.joking.jk.fragment.UnLockFragment;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;
import com.joking.jk.view.ConfirmPasswordDialog;

public class AppLockActivity extends FragmentActivity implements OnClickListener {

    private TextView tv_unlock;
    private TextView tv_lock;
    private FragmentManager fragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment LockFragment;
    private Button btn_resetpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_app_lock);

        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        btn_resetpassword = (Button) findViewById(R.id.btn_resetpassword);

        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);
        btn_resetpassword.setOnClickListener(this);
        //获取到fragment的管理者

        fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();

        unLockFragment = new UnLockFragment();
        LockFragment = new LockFragment();
        /**
         * 替换界面
         * 1 需要替换的界面的id
         * 2具体指某一个fragment的对象
         */
        mTransaction.replace(R.id.fl_content, unLockFragment).commit();
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_unlock:
                if (!LockFragment.isAnim()) {//没有动画
                    //没有加锁
                    tv_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
                    tv_lock.setBackgroundResource(R.mipmap.tab_right_default);

                    ft.replace(R.id.fl_content, unLockFragment);
//                System.out.println("切换到lockFragment");
                }
                break;

            case R.id.tv_lock:
                if (!unLockFragment.isAnim()) {//没有动画
                    //没有加锁
                    tv_unlock.setBackgroundResource(R.mipmap.tab_left_default);
                    tv_lock.setBackgroundResource(R.mipmap.tab_right_pressed);

                    ft.replace(R.id.fl_content, LockFragment);
//                System.out.println("切换到unlockFragment");
                }
                break;

            case R.id.btn_resetpassword:
                final String lock = SharedPreferencesUtils.getString(this, "lock", "");
                SharedPreferencesUtils.setString(this, "lock", "");
                ConfirmPasswordDialog confirmPasswordDialog = new ConfirmPasswordDialog();
                confirmPasswordDialog.setDialogListener(new ConfirmPasswordDialog.DialogListener() {
                    @Override
                    public void confirmed() {
                        ToastUtils.showShortToast(AppLockActivity.this, "修改成功！");
                    }

                    @Override
                    public void quitReset() {
                        ToastUtils.showShortToast(AppLockActivity.this, "密码不变！");
                        SharedPreferencesUtils.setString(AppLockActivity.this, "lock", lock);
                    }
                });
                confirmPasswordDialog.show(AppLockActivity.this, "lock");
                break;
        }
        ft.commit();
    }
}
