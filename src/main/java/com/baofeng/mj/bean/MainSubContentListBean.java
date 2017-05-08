package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * 二级Tab内容 里面list字段 bean
 * Created by muyu on 2016/5/12.
 */
public class MainSubContentListBean<T> extends ContentBaseBean implements Serializable {
    private LandscapeUrlBean landscape_url;
    private int total;
    private int has_more;
    private String category_url;
    private String title;
    private String url;
    private String res_id;
    private int object_type;
    private int lightshow;
    private String layout_type;
    private String bg_url = "";
    private T list;

    @Override
    public String getLayout_type() {
        return layout_type;
    }

    @Override
    public void setLayout_type(String layout_type) {
        this.layout_type = layout_type;
    }

    public int getObject_type() {
        return object_type;
    }

    public void setObject_type(int object_type) {
        this.object_type = object_type;
    }

    public int getLightshow() {
        return lightshow;
    }

    public void setLightshow(int lightshow) {
        this.lightshow = lightshow;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory_url() {
        return category_url;
    }

    public void setCategory_url(String category_url) {
        this.category_url = category_url;
    }

    public LandscapeUrlBean getLandscape_url() {
        return landscape_url;
    }

    public void setLandscape_url(LandscapeUrlBean landscape_url) {
        this.landscape_url = landscape_url;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getHas_more() {
        return has_more;
    }

    public void setHas_more(int has_more) {
        this.has_more = has_more;
    }


    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }

    public String getBg_url() {
        return bg_url;
    }

    public void setBg_url(String bg_url) {
        this.bg_url = bg_url;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MainSubContentListBean{" +
                "landscape_url=" + landscape_url +
                ", total=" + total +
                ", has_more=" + has_more +
                ", category_url='" + category_url + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", res_id='" + res_id + '\'' +
                ", object_type=" + object_type +
                ", lightshow=" + lightshow +
                ", layout_type='" + layout_type + '\'' +
                ", bg_url='" + bg_url + '\'' +
                ", list=" + list +
                '}';
    }
}
