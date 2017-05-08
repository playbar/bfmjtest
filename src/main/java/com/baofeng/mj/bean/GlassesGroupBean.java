package com.baofeng.mj.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghongfang on 2017/2/24.
 */
public class GlassesGroupBean {
    private List<GlassesBean.DataBean> data = new ArrayList<>();
    private String glass_brand_id; //品牌id （根据id分组）
    private boolean isSelected = false;
    public List<GlassesBean.DataBean> getData() {
        return data;
    }

    public void setData(List<GlassesBean.DataBean> data) {
        this.data = data;
    }

    public String getGlass_brand_id() {
        return glass_brand_id;
    }

    public void setGlass_brand_id(String glass_brand_id) {
        this.glass_brand_id = glass_brand_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
