package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * 应用主Tab Bean
 * Created by muyu on 2016/5/12.
 */
public class MainTabBean<T> implements Serializable{
    private int res_id;
    private String title;
    private int type;
    private int subType;
    private T pages;

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

    public T getPages() {
        return pages;
    }

    public void setPages(T pages) {
        this.pages = pages;
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

    @Override
    public String toString() {
        return "MainTabBean{" +
                "res_id=" + res_id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", subType=" + subType +
                ", pages=" + pages +
                '}';
    }
}
