package com.joking.jk.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.db.dao.AddressDao;


/**
 * 归属地查询页面
 *
 * @author Kevin
 */
public class AddressActivity extends BaseActivity {


    private TextView query_number;
    private TextView query_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address);
        query_number = (TextView) findViewById(R.id.query_number);
        query_result = (TextView) findViewById(R.id.query_result);
    }

    public void number(View view) {

        String query = query_number.getText().toString();

        switch (view.getId()) {
            case R.id.btn0:
                query += "0";
                break;
            case R.id.btn1:
                query += "1";
                break;
            case R.id.btn2:
                query += "2";
                break;
            case R.id.btn3:
                query += "3";
                break;
            case R.id.btn4:
                query += "4";
                break;
            case R.id.btn5:
                query += "5";
                break;
            case R.id.btn6:
                query += "6";
                break;
            case R.id.btn7:
                query += "7";
                break;
            case R.id.btn8:
                query += "8";
                break;
            case R.id.btn9:
                query += "9";
                break;
            case R.id.btn_cancel:
                query = "";
                break;
            case R.id.btn_ok:
                break;
        }

        query(query);

        query_number.setText(query);
    }

    /**
     * 开始查询
     */
    public void query(String number) {
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            query_result.setText(address);
        } else {
            query_result.setText("查询结果");

            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

            query_number.startAnimation(shake);
            vibrate();
        }
    }

    /**
     * 手机震动, 需要权限 android.permission.VIBRATE
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // vibrator.vibrate(2000);震动两秒
        vibrator.vibrate(new long[]{1000, 2000, 1000, 3000}, -1);// 先等待1秒,再震动2秒,再等待1秒,再震动3秒,
        // 参2等于-1表示只执行一次,不循环,
        // 参2等于0表示从头循环,
        // 参2表示从第几个位置开始循环
        // 取消震动vibrator.cancel()
    }
}
