package com.joking.jk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.bean.AppInfo;
import com.joking.jk.utils.AppInfos;
import com.joking.jk.utils.WindowUtils;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView;
    private TextView tv_rom;
    private TextView tv_sd;
    private TextView tv_app;

    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;

    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;
    private UninstallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter adapter = new AppManagerAdapter();
            listView.setAdapter(adapter);
        }
    };

    private void initViews() {
        setContentView(R.layout.activity_app_manager);

        listView = (ListView) findViewById(R.id.list_view);
        tv_rom = (TextView) findViewById(R.id.tv_rom);
        tv_sd = (TextView) findViewById(R.id.tv_sd);
        tv_app = (TextView) findViewById(R.id.tv_app);

        //获取到rom内存的运行的剩余空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取到SD卡的剩余空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        //格式化大小
        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));
        tv_sd.setText("SD卡可用" + Formatter.formatFileSize(this, sd_freeSpace));

        receiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);

        //设置listview的滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                popupWindowDismiss();

                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > (userAppInfos.size() + 1)) {
                        //系统应用程序
                        tv_app.setText("系统程序(" + systemAppInfos.size() + ")个");
                    } else {
                        //用户应用程序
                        tv_app.setText("用户程序(" + userAppInfos.size() + ")个");
                    }
                }
            }
        });

        final int winWidth = WindowUtils.getWindowWidth(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到当前点击的item对象
                Object obj = listView.getItemAtPosition(position);

                if (obj != null && obj instanceof AppInfo) {

                    clickAppInfo = (AppInfo) obj;

                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);

                    ImageView run = (ImageView) contentView.findViewById(R.id.run);
                    ImageView uninstall = (ImageView) contentView.findViewById(R.id.uninstall);
                    ImageView details = (ImageView) contentView.findViewById(R.id.details);
                    run.setOnClickListener(AppManagerActivity.this);
                    uninstall.setOnClickListener(AppManagerActivity.this);
                    details.setOnClickListener(AppManagerActivity.this);

                    //将之前的popupWindow去除
                    popupWindowDismiss();

                    // -2表示包裹内容
                    popupWindow = new PopupWindow(contentView, -2, -2);
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);

                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, winWidth / 2, location[1]);

                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                    sa.setDuration(750);

                    contentView.startAnimation(sa);
                }
            }
        });
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取到所有安装到手机上面的应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //appInfos拆成 用户程序的集合 + 系统程序的集合

                //用户程序的集合
                userAppInfos = new ArrayList<AppInfo>();
                //系统程序的集合
                systemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo : appInfos) {
                    //用户程序
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(0);

            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //运行
            case R.id.run:
                Intent start_localIntent = this.getPackageManager().
                        getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                startActivity(start_localIntent);

                popupWindowDismiss();
                break;

            //卸载
            case R.id.uninstall:
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(uninstall_localIntent);

                popupWindowDismiss();
                break;

            //详情
            case R.id.details:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(detail_intent);

                popupWindowDismiss();
                break;
        }

    }

    /**
     * 方法必须全部实现
     * onItemClickListener需要
     */
    private class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);

            } else {

                int location = userAppInfos.size() + 2;

                appInfo = systemAppInfos.get(position - location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果当前的position等于0 表示应用程序
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(20);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userAppInfos.size() + ")");

                return textView;
                //表示系统程序
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(20);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");

                return textView;
            }

            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = userAppInfos.size() + 2;

                appInfo = systemAppInfos.get(position - location);
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.app_manager_item, null);

                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_apk_size = (TextView) view.findViewById(R.id.tv_apk_size);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);

                view.setTag(holder);
            }

            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_apk_size.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            holder.tv_name.setText(appInfo.getApkName());

            if (appInfo.isRom()) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("外部存储");
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_apk_size;
        TextView tv_location;
        TextView tv_name;
    }

    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收到卸载的广播");
        }
    }

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }


    @Override
    protected void onDestroy() {
        popupWindowDismiss();

        unregisterReceiver(receiver);
        receiver = null;

        super.onDestroy();
    }
}
