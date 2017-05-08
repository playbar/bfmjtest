package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanyang on 2016/6/12.
 * 筛选实体类
 */
public class SelectBean<T> implements Serializable {
    private List<T> list;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
