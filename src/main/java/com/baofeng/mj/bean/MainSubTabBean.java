package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * 二级层级Tab Bean
 * Created by muyu on 2016/5/12.
 */
public class MainSubTabBean implements Serializable{
    private int res_id;
    private String title;
    private int type;
    private int subType;
    private String url;
    private String category_url;
    private String icon;

    public int getRes_id() {
        return res_id;
    }

    public void setRes_id(int res_id) {
        this.res_id = res_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public String getCategory_url() {
        return category_url;
    }

    public void setCategory_url(String category_url) {
        this.category_url = category_url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "MainSubTabBean{" +
                "res_id=" + res_id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", subType=" + subType +
                ", url='" + url + '\'' +
                ", category_url='" + category_url + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
