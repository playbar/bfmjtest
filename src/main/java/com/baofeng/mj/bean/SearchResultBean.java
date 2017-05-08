package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunshine on 16/9/20.
 * 搜索结果列表实体累
 */
public class SearchResultBean implements Serializable {
    private String status;
    private String status_msg;
    private String version;
    private String channel;
    private String date;
    private String language;
    private String data_type;
    private MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> data;

    public MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> getData() {
        return data;
    }

    public void setData(MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
