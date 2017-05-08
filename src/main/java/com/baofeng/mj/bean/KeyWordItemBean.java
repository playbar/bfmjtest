package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by sunshine on 16/9/20.
 * 热搜词Item实体类
 */
public class KeyWordItemBean implements Serializable {
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
