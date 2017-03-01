package com.joking.jk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.bean.AppInfo;
import com.joking.jk.db.dao.AppLockDao;
import com.joking.jk.utils.AppInfos;

import java.util.ArrayList;
import java.util.List;


public class UnLockFragment extends Fragment {

    private TextView tv_unlock;
    private ListView list_view;
    private AppLockDao dao;
    private List<AppInfo> unLockLists;
    private UnLockAdapter adapter;

    private boolean isAnim = false;

    /*
     * 类似activity里面的setContentView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_unlock_fragment, null);

        list_view = (ListView) view.findViewById(R.id.list_view);
        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<AppInfo> appInfos = AppInfos.getAppInfos(getActivity());

        // 获取到程序锁的dao
        dao = new AppLockDao(getActivity());
        // 初始化一个没有加锁的集合
        unLockLists = new ArrayList<AppInfo>();

        for (AppInfo appInfo : appInfos) {
            // 判断当前的应用是否在程序所的数据里面
            if (dao.find(appInfo.getApkPackageName())) {

            } else {
                // 如果查询不到说明没有在程序锁的数据库里面
                unLockLists.add(appInfo);
            }
        }

        adapter = new UnLockAdapter();
        list_view.setAdapter(adapter);
    }

    public boolean isAnim() {
        return isAnim;
    }

    public class UnLockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            tv_unlock.setText("未加锁(" + unLockLists.size() + ")个");
            return unLockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            final View view;
            final AppInfo appInfo;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_unlock, null);

                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            // 获取到当前的对象
            appInfo = unLockLists.get(position);

            holder.iv_icon.setImageDrawable(unLockLists.get(position).getIcon());
            holder.tv_name.setText(unLockLists.get(position).getApkName());
            // 把程序添加到程序锁数据库里面
            holder.iv_unlock.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!isAnim) {
                        // 初始化一个位移动画
                        TranslateAnimation translateAnimation = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        // 设置动画时间
                        translateAnimation.setDuration(1000);
                        // 开始动画
                        view.startAnimation(translateAnimation);

                        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isAnim = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dao.add(appInfo.getApkPackageName());
                                unLockLists.remove(position);
                                adapter.notifyDataSetChanged();

                                isAnim = false;

                                Intent intent = new Intent();
                                // 发送广播。停止保护
                                intent.setAction("com.joking.jk.changed");
                                getActivity().sendBroadcast(intent);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
