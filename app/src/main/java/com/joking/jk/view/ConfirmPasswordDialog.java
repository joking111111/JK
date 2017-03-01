package com.joking.jk.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joking.jk.R;
import com.joking.jk.utils.MD5Utils;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;

/**
 * Created by Administrator on 2016/10/14.
 */

public class ConfirmPasswordDialog {

    private DialogListener mListener;

    public interface DialogListener {
        void confirmed();

        void quitReset();
    }

    public void setDialogListener(DialogListener listener) {
        mListener = listener;
    }

    /**
     * 显示密码弹窗
     */
    public void show(Activity activity, String password_type) {
        // 判断是否设置密码
        String savedPassword = SharedPreferencesUtils.getString(activity, password_type, "");
        if (!TextUtils.isEmpty(savedPassword)) {
            // 输入密码弹窗
            showPasswordInputDialog(activity, password_type);
        } else {
            // 如果没有设置过, 弹出设置密码的弹窗
            showPasswordSetDailog(activity, password_type);
        }
    }

    /**
     * 输入密码弹窗
     */
    private void showPasswordInputDialog(final Activity activity, final String password_type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(activity, R.layout.dialog_input_password, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText et_password = (EditText) view.findViewById(R.id.et_password);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = SharedPreferencesUtils.getString(activity, password_type, "");

                    if (MD5Utils.encode(password).equals(savedPassword)) {
                        dialog.dismiss();

                        if (mListener != null) {
                            mListener.confirmed();
                        }
                    } else {
                        ToastUtils.showShortToast(activity, "密码错误!");
                    }
                } else {
                    ToastUtils.showShortToast(activity, "输入框内容不能为空!");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();
    }

    /**
     * 设置密码的弹窗
     */
    private void showPasswordSetDailog(final Activity activity, final String password_type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(activity, R.layout.dialog_set_password, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        final EditText et_passwordConfirm = (EditText) view.findViewById(R.id.et_password_confirm);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString();
                String passwordConfirm = et_passwordConfirm.getText().toString();
                // password!=null && !password.equals("")
                if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
                    if (password.equals(passwordConfirm)) {
                        // 将密码保存起来
                        SharedPreferencesUtils.setString(activity, password_type,
                                MD5Utils.encode(password));

                        dialog.dismiss();

                        if (mListener != null) {
                            mListener.confirmed();
                        }
                    } else {
                        ToastUtils.showShortToast(activity, "两次密码不一致!");
                    }
                } else {
                    ToastUtils.showShortToast(activity, "输入框内容不能为空!");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
                if (mListener != null) {
                    mListener.quitReset();
                }
            }
        });

        dialog.show();
    }
}
