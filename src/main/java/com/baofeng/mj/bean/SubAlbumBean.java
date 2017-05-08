package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanyang on 2016/6/21.
 * 用户订阅专辑列表实体类
 */
public class SubAlbumBean implements Serializable {

    private int status;
    private String msg;
    private List<SubBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<SubBean> getData() {
        return data;
    }

    public void setData(List<SubBean> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
