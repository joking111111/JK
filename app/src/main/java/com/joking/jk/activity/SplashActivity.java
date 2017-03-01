package com.joking.jk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.bean.UpdateInfo;
import com.joking.jk.global.global;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.ToastUtils;
import com.joking.jk.utils.WindowUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends BaseActivity {

    private static final int ENTER_HOME = 0;

    private TextView tv_version;
    private ProgressBar pb_progress;
    private UpdateInfo updateInfo;

    private int version_code;
    private String version_name;
    private long startTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENTER_HOME:
                    enterHome();
                    break;
            }

        }
    };
    private TextView tv_progress;
    private RelativeLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTime = System.currentTimeMillis();
        initViews();
        iniData();

        boolean auto_update = SharedPreferencesUtils.getBoolean(this, "auto_update", true);
        if (auto_update) {
            checkVersion();
        } else {
            long period = System.currentTimeMillis() - startTime;
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 2000 - period);
        }

        // 渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        mRoot.startAnimation(anim);
    }

    private void iniData() {
        //拷贝归属地数据库
        copyDB("address.db");
        copyDB("antivirus.db");
        createShortcut();
    }

    /**
     * 拷贝数据库
     *
     * @param dbName
     */
    private void copyDB(String dbName) {
        // File filesDir = getFilesDir();
        // System.out.println("路径:" + filesDir.getAbsolutePath());
        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址

        if (destFile.exists()) {
            System.out.println("数据库" + dbName + "已存在!");
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(destFile);

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 快捷方式
     */
    private void createShortcut() {
        Intent intent = new Intent();

        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        //如果设置为true表示可以创建重复的快捷方式
        intent.putExtra("duplicate", false);

        /**
         * 1 干什么事情
         * 2 你叫什么名字
         * 3你长成什么样子
         */
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "极客卫士");
        //干什么事情
        /**
         * 这个地方不能使用显示意图
         * 必须使用隐式意图
         */
        Intent shortcut_intent = new Intent();
        shortcut_intent.setAction("com.joking.jk");
        shortcut_intent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);

        sendBroadcast(intent);
    }

    private void initViews() {
        WindowUtils.WindowFullScreen(this);
        setContentView(R.layout.activity_splash);

        version_code = getVersionCode();
        version_name = getVersionName();

        mRoot = (RelativeLayout) findViewById(R.id.activity_splash);

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号：" + version_name);

        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);

        tv_progress = (TextView) findViewById(R.id.tv_progress);
    }

    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        HttpUtils httpUtils = new HttpUtils();
        String url = global.SERVER_URL + "update.json";
        httpUtils.send(HttpRequest.HttpMethod.GET, url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        ToastUtils.showShortToast(SplashActivity.this, responseInfo.result);
                        Gson gson = new Gson();
                        updateInfo = gson.fromJson(responseInfo.result, UpdateInfo.class);
//                        ToastUtils.showShortToast(SplashActivity.this, updateInfo.toString());

                        if (updateInfo.versioncode > version_code) {//需要更新
                            showUpdateDailog();
                        } else {
                            long period = System.currentTimeMillis() - startTime;
                            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 2000 - period);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ToastUtils.showShortToast(SplashActivity.this, "当前网络不可用");

                        long period = System.currentTimeMillis() - startTime;
                        mHandler.sendEmptyMessageDelayed(ENTER_HOME, 2000 - period);
                    }
                });
    }

    /**
     * 升级对话框
     */
    private void showUpdateDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//不要用getApplicationContext()
        builder.setTitle("最新版本:" + updateInfo.versionname);
        builder.setMessage(updateInfo.description);
        // builder.setCancelable(false);//不让用户取消对话框, 用户体验太差,尽量不要用
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();
            }
        });

        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        // 设置取消的监听, 用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });

        builder.show();
    }

    /**
     * 进入主页面
     */
    private void enterHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    /**
     * 下载新的安装包，未完善
     * handler.cancel();取消下载
     */
    private void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String target = Environment.getExternalStorageDirectory() + "/update.apk";

            tv_progress.setVisibility(View.VISIBLE);

            HttpUtils http = new HttpUtils();
            //第一个参数数下载地址，第二个参数是下载到哪里
            HttpHandler handler = http.download(updateInfo.downloadUri, target,
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            tv_progress.setText("下载进度" + current * 100 / total + "%");
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            tv_progress.setText("下载完成！");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setDataAndType(Uri.fromFile(responseInfo.result),
                                    "application/vnd.android.package-archive");
                            startActivityForResult(intent, 0);// 如果用户取消安装的话,
                            // 会返回结果,回调方法onActivityResult
                        }


                        @Override
                        public void onFailure(HttpException error, String msg) {
                            tv_progress.setText("下载失败");
                            ToastUtils.showShortToast(SplashActivity.this, "下载失败");
                        }
                    });
        } else {
            ToastUtils.showShortToast(SplashActivity.this, "SD卡不可用");
        }
    }

    // 如果用户取消安装的话,回调此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
    }
}
