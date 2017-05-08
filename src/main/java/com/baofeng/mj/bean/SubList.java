package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by muyu on 2017/3/2.
 */
public class SubList<T> implements Serializable {
    private List<T> list;
    private String url;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SubList{" +
                "list=" + list +
                ", url='" + url + '\'' +
                '}';
    }
}


