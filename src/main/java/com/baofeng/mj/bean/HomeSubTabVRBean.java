package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Home SubTab VR 分类Bean
 * Created by muyu on 2017/5/12.
 */
public class HomeSubTabVRBean implements Serializable{
    private String bg;
    private String res_id;
    private String keyname;
    private String title;
    private List list;
    private int type;
    private int subType;
    private String list_url;

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
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

    public String getList_url() {
        return list_url;
    }

    public void setList_url(String list_url) {
        this.list_url = list_url;
    }

    @Override
    public String toString() {
        return "HomeSubTabVRBean{" +
                "bg='" + bg + '\'' +
                ", res_id='" + res_id + '\'' +
                ", keyname='" + keyname + '\'' +
                ", title='" + title + '\'' +
                ", list=" + list +
                ", type=" + type +
                ", subType=" + subType +
                ", list_url='" + list_url + '\'' +
                '}';
    }
}
