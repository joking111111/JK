package com.joking.jk.activity;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.utils.ToastUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends BaseActivity {

    private PackageManager packageManager;
    private List<CacheInfo> cacheInfos;

    private ListView list_view;
    private CacheAdapter cacheAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private class CacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.clean_cache_item, null);

                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) view.findViewById(R.id.iv_icon);
                viewHolder.appName = (TextView) view.findViewById(R.id.tv_appname);
                viewHolder.cacheSize = (TextView) view.findViewById(R.id.tv_cachesize);

                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.icon.setImageDrawable(cacheInfos.get(position).icon);
            viewHolder.appName.setText(cacheInfos.get(position).appName);
            viewHolder.cacheSize.setText("缓存大小：" + Formatter.formatFileSize(
                    CleanCacheActivity.this, cacheInfos.get(position).cacheSize));

            return view;
        }
    }

    static class ViewHolder {
        public ImageView icon;
        public TextView appName;
        public TextView cacheSize;
    }

    private void initViews() {
        setContentView(R.layout.activity_clean_cache);

        list_view = (ListView) findViewById(R.id.list_view);

        cacheInfos = new ArrayList<CacheInfo>();

        packageManager = getPackageManager();
        /**
         * 接收2个参数
         * 第一个参数接收一个包名
         * 第二个参数接收aidl的对象
         */
//		  * @hide
//		     */
//		    public abstract void getPackageSizeInfo(String packageName,
//		            IPackageStatsObserver observer);
//		packageManager.getPackageSizeInfo();

        cacheInfos.clear();
        cacheAdapter = new CacheAdapter();

        //安装到手机上面所有的应用程序
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : installedPackages) {
            getCacheSize(packageInfo);
        }
        list_view.setAdapter(cacheAdapter);
    }

    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //通过反射获取到当前的方法
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            /**
             * 第一个参数表示这方法在哪个类里调用
             * 后面的参数是方法的参数
             * 第二个参数是包名
             */
//            System.out.println("packageInfo.packageName:" + packageInfo.packageName);
//            System.out.println("packageInfo.applicationInfo.packageName:" + packageInfo.applicationInfo.packageName);
            method.invoke(packageManager, packageInfo.packageName,
                    new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注意是继承.Stub!!
     */
    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        private PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0) {
//                System.out.println("当前应用的名字：" + packageInfo.applicationInfo.loadLabel(packageManager)
//                        + "缓存大小：" + cacheSize);

                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.icon = packageInfo.applicationInfo.loadIcon(packageManager);
                cacheInfo.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.cacheSize = cacheSize;

                cacheInfos.add(cacheInfo);
                /**
                 * 加了这句就不报错了。。。
                 */
                cacheAdapter.notifyDataSetChanged();
            }
        }
    }

    static class CacheInfo {
        public Drawable icon;
        public String appName;
        public long cacheSize;
    }

    public void cleanAllCache(View view) {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE,
                            new IPackageDataObserver.Stub() {
                                @Override
                                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ToastUtils.showShortToast(CleanCacheActivity.this, "清理完毕");
    }
}
