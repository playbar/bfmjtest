package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by hanyang on 2016/6/12.
 * 筛选选项详细信息
 */
public class SelectDetailBean implements Serializable {
    private String id;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
