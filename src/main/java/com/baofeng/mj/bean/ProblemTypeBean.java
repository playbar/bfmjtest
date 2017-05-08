package com.baofeng.mj.bean;

/**
 * 意见反馈页面问题类型实体类
 * Created by yushaochen on 2016/12/28.
 */

public class ProblemTypeBean {
    private String name;//问题类型名称
    private boolean isSelected;//是否选中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
