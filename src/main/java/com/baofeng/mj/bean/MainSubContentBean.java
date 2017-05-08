package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * 二级Tab内容
 * Created by muyu on 2016/5/12.
 */
public class MainSubContentBean<T> implements Serializable {
    private LandscapeUrlBean landscape_url;
    private T list;

    public LandscapeUrlBean getLandscape_url() {
        return landscape_url;
    }

    public void setLandscape_url(LandscapeUrlBean landscape_url) {
        this.landscape_url = landscape_url;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MainSubContentBean{" +
                "landscape_url=" + landscape_url +
                ", list=" + list +
                '}';
    }
}
