package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;

import java.util.List;

/**
 * Created by hanyang on 2016/5/6.
 * 筛选类型View
 */
public class HomeSelectItemView extends SelectItemView {

    public HomeSelectItemView(Context context, SelectListBean<SelectDetailBean> selectListBean, SelectChange selectChange) {
        super(context, selectListBean, selectChange);
    }

    public HomeSelectItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioButton getRadioButton(String text) {
        RadioButton button = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.home_select_item, null);
        //去掉左侧默认的圆点
        button.setButtonDrawable(android.R.color.transparent);
        button.setGravity(Gravity.CENTER);
        button.setText(text);
        return button;
    }
}
