package com.joking.jk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joking.jk.R;

/**
 * Created by Administrator on 2016/10/10.
 */

public class SettingItemView extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_desc;
    private CheckBox cb_status;
    private String mTitle;
    private String mDesc_on;
    private String mDesc_off;

    public SettingItemView(Context context) {
        this(context, null, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(getContext(), R.layout.view_setting_item, this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        cb_status = (CheckBox) findViewById(R.id.cb_status);

//        int attributeCount = attrs.getAttributeCount();
//        for (int i = 0; i < attributeCount; i++) {
//            String attributeName = attrs.getAttributeName(i);
//            String attributeValue = attrs.getAttributeValue(i);
//
//            System.out.println(attributeName + "=" + attributeValue);
//        }


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        mTitle = typedArray.getString(R.styleable.SettingItemView_siv_title);
//        mDesc_on = attrs.getAttributeValue(R.styleable.SettingItemView_desc_on);//不行
//        mDesc_on = attrs.getAttributeValue(R.styleable.SettingItemView_desc_off);//不行
        mDesc_on = typedArray.getString(R.styleable.SettingItemView_siv_desc_on);
        mDesc_off = typedArray.getString(R.styleable.SettingItemView_siv_desc_off);
        tv_title.setText(mTitle);

//        System.out.println(mTitle);
//        System.out.println(mDesc_on + "  " + mDesc_off);
    }

    public boolean isChecked() {
        return cb_status.isChecked();
    }

    public void setCheckedAndDesc(boolean checked) {
        cb_status.setChecked(checked);
        if (checked) {
            tv_desc.setText(mDesc_on);
        } else {
            tv_desc.setText(mDesc_off);
        }
    }
}
