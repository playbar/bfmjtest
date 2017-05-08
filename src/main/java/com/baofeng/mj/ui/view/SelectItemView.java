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
public class SelectItemView extends LinearLayout implements View.OnClickListener {
    public Context mContext;
    private SelectChange selectChange;
    private String key;
    private SelectListBean<SelectDetailBean> listBean;
    private RadioGroup radioGroup;

    public SelectItemView(Context context, SelectListBean<SelectDetailBean> selectListBean, SelectChange selectChange) {
        super(context);
        this.mContext = context;
        this.listBean = selectListBean;
        this.key = listBean.getKeyname();
        this.selectChange = selectChange;
        init(selectListBean.getList(), selectListBean.getSelectPos());
    }

    public SelectItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void init(List<SelectDetailBean> beans, int selectPos) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_item_container, null);
        addView(view);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.select_container);
        this.radioGroup = radioGroup;
        RadioButton radioButton;
        for (int i = 0; i < beans.size(); i++) {
            radioButton = getRadioButton(beans.get(i).getTitle());
            radioButton.setTag(beans.get(i));
            radioGroup.addView(radioButton);
            radioButton.setId(i);
            radioButton.setOnClickListener(this);
        }
        ((RadioButton) radioGroup.getChildAt(selectPos)).setChecked(true);
    }

    public RadioButton getRadioButton(String text) {
        RadioButton button = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.select_item, null);
        //去掉左侧默认的圆点
        button.setButtonDrawable(android.R.color.transparent);
        button.setGravity(Gravity.CENTER);
        button.setText(text);
        return button;
    }

    @Override
    public void onClick(View v) {
        if (selectChange != null) {
            RadioButton radioButton = (RadioButton) v;
            listBean.setSelectPos(radioButton.getId());
            selectChange.select(radioGroup, radioButton.getId());
        }
    }

    public interface SelectChange {
        public void select(RadioGroup group, int checkedId);
    }

    public String getKey() {
        return key;
    }

    public void setCheck(int checkPos) {
        ((RadioButton) radioGroup.getChildAt(checkPos)).setChecked(true);
    }

    public SelectListBean getListBean() {
        return listBean;
    }
}
