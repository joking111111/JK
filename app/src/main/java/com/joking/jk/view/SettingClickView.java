package com.joking.jk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joking.jk.R;

/**
 * Created by Administrator on 2016/10/10.
 */

public class SettingClickView extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_desc;
    private String mTitle;
    private String mDesc;

    public SettingClickView(Context context) {
        this(context, null, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(getContext(), R.layout.view_setting_click, this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_desc = (TextView) findViewById(R.id.tv_desc);

//        int attributeCount = attrs.getAttributeCount();
//        for (int i = 0; i < attributeCount; i++) {
//            String attributeName = attrs.getAttributeName(i);
//            String attributeValue = attrs.getAttributeValue(i);
//
//            System.out.println(attributeName + "=" + attributeValue);
//        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingClickView);
        mTitle = typedArray.getString(R.styleable.SettingClickView_scv_title);
//        mDesc_on = attrs.getAttributeValue(R.styleable.SettingItemView_desc_on);//不行
//        mDesc_on = attrs.getAttributeValue(R.styleable.SettingItemView_desc_off);//不行
        mDesc = typedArray.getString(R.styleable.SettingClickView_scv_desc);

        tv_title.setText(mTitle);
        tv_desc.setText(mDesc);

//        System.out.println(mTitle);
//        System.out.println(mDesc_on + "  " + mDesc_off);
    }

    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }
}
