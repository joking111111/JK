package com.joking.jk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.view.ConfirmPasswordDialog;

public class HomeActivity extends BaseActivity {

    private GridView gv_home;
    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private int[] mPics = new int[]{R.mipmap.home_safe,
            R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
            R.mipmap.home_taskmanager, R.mipmap.home_netmanager,
            R.mipmap.home_trojan, R.mipmap.home_sysoptimize,
            R.mipmap.home_tools, R.mipmap.home_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initData();
    }

    private void initViews() {
        setContentView(R.layout.activity_home);

        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(mListener);
    }

    private void initData() {

    }

    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    //手机防盗
                    ConfirmPasswordDialog confirmPasswordDialog = new ConfirmPasswordDialog();
                    confirmPasswordDialog.setDialogListener(new ConfirmPasswordDialog.DialogListener() {
                        @Override
                        public void confirmed() {
                            startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                        }

                        @Override
                        public void quitReset() {

                        }
                    });
                    confirmPasswordDialog.show(HomeActivity.this, "password");
                    break;

                case 1:
                    //通信卫士
                    startActivity(new Intent(HomeActivity.this, CallSmsSafeActivity.class));
                    break;

                case 2:
                    //软件管理
                    startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                    break;

                case 3:
                    //进程管理
                    startActivity(new Intent(HomeActivity.this, TaskManagerActivity.class));
                    break;

                case 6:
                    //缓存清理
                    startActivity(new Intent(HomeActivity.this, CleanCacheActivity.class));
                    break;

                case 5:
                    //手机杀毒
                    startActivity(new Intent(HomeActivity.this, AntivirusActivity.class));
                    break;

                case 7:
                    //设置中心
                    startActivity(new Intent(HomeActivity.this, AdvancedToolsActivity.class));
                    break;

                case 8:
                    //设置中心
                    startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                    break;
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
            ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

            tv_item.setText(mItems[position]);
            iv_item.setImageResource(mPics[position]);
            return view;
        }
    }
}
