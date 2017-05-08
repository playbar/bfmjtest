package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunshine on 16/9/20.
 * 热搜词接口
 */
public class KeyWordBean implements Serializable {


    private int status;
    private String status_msg;
    private String version;
    private String channel;
    private String date;
    private String language;
    private String data_type;

    private KeyWordListBean data;

    public KeyWordListBean getData() {
        return data;
    }

    public void setData(KeyWordListBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }
}
