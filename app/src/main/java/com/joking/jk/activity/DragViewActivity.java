package com.joking.jk.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joking.jk.R;
import com.joking.jk.base.BaseActivity;
import com.joking.jk.utils.SharedPreferencesUtils;
import com.joking.jk.utils.WindowUtils;


/**
 * 修改归属地显示位置
 *
 * @author Kevin
 */
public class DragViewActivity extends BaseActivity {

    private TextView tv_top;
    private TextView tv_bottom;

    private ImageView iv_drag;

    private int startX;
    private int startY;

    private long[] mHits = new long[2];// 数组长度表示要点击的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowUtils.WindowFullScreen(this);
        setContentView(R.layout.activity_drag_view);

        tv_top = (TextView) findViewById(R.id.tv_top);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        iv_drag = (ImageView) findViewById(R.id.iv_drag);

        int lastX = SharedPreferencesUtils.getInt(this, "lastX", 0);
        int lastY = SharedPreferencesUtils.getInt(this, "lastY", 0);

        // onMeasure(测量view), onLayout(安放位置), onDraw(绘制)
        // iv_drag.layout(lastX, lastY, lastX + iv_drag.getWidth(),
        // lastY + iv_drag.getHeight());//不能用这个方法,因为还没有测量完成,就不能安放位置

        // 获取屏幕宽高
        final int winWidth = WindowUtils.getWindowWidth(this);
        final int winHeight = WindowUtils.getWindowHeight(this);

        if (lastY > winHeight / 2) {// 上边显示,下边隐藏
            tv_top.setVisibility(View.VISIBLE);
            tv_bottom.setVisibility(View.INVISIBLE);
        } else {
            tv_top.setVisibility(View.INVISIBLE);
            tv_bottom.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_drag
                .getLayoutParams();// 获取布局对象
        layoutParams.leftMargin = lastX;// 设置左边距
        layoutParams.topMargin = lastY;// 设置top边距

        iv_drag.setLayoutParams(layoutParams);// 重新设置位置

        iv_drag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//循环左移
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // 把图片居中
                    iv_drag.layout(winWidth / 2 - iv_drag.getWidth() / 2,
                            iv_drag.getTop(),
                            winWidth / 2 + iv_drag.getWidth() / 2,
                            iv_drag.getBottom());
                }
            }
        });

        // 设置触摸监听
        iv_drag.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        // 计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新左上右下距离
                        int l = iv_drag.getLeft() + dx;
                        int r = iv_drag.getRight() + dx;

                        int t = iv_drag.getTop() + dy;
                        int b = iv_drag.getBottom() + dy;

                        // 判断是否超出屏幕边界, 注意状态栏的高度
                        if (l < 0 || r > winWidth || t < 0 || b > winHeight) {
                            break;
                        }

                        // 根据图片位置,决定提示框显示和隐藏
                        if (t > winHeight / 2) {// 上边显示,下边隐藏
                            tv_top.setVisibility(View.VISIBLE);
                            tv_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            tv_top.setVisibility(View.INVISIBLE);
                            tv_bottom.setVisibility(View.VISIBLE);
                        }

                        // 更新界面
                        iv_drag.layout(l, t, r, b);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 记录坐标点
                        SharedPreferencesUtils.setInt(DragViewActivity.this, "lastX", iv_drag.getLeft());
                        SharedPreferencesUtils.setInt(DragViewActivity.this, "lastY", iv_drag.getTop());
                        break;
                }

                return false;//事件要向下传递,让onclick(双击事件)可以响应
            }
        });
    }
}
