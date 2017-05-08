package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanyang on 2016/6/12.
 * 筛选栏目信息：电视剧、电影、综艺等,
 * 筛选类型：年代、地区等可复用此实体类
 */
public class SelectListBean<T> extends ContentBaseBean implements Serializable {
    private String bg;
    private int res_id;
    private String keyname;
    private String title;
    private List<T> list;
    private String type;
    private String subtype;
    private int selectPos = 0;//选中位置
    private String list_url;

    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }

    public List<T> getList() {
        return list;
    }

    public int getRes_id() {
        return res_id;
    }

    public void setRes_id(int res_id) {
        this.res_id = res_id;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getList_url() {
        return list_url;
    }

    public void setList_url(String list_url) {
        this.list_url = list_url;
    }

    @Override
    public String toString() {
        return "SelectListBean{" +
                "bg='" + bg + '\'' +
                ", res_id=" + res_id +
                ", keyname='" + keyname + '\'' +
                ", title='" + title + '\'' +
                ", list=" + list +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", selectPos=" + selectPos +
                ", list_url='" + list_url + '\'' +
                '}';
    }
}
