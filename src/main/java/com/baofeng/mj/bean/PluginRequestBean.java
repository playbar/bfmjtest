package com.baofeng.mj.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghongfang on 2017/3/7.
 * 网络请求获取直播插件的返回数据
 */
public class PluginRequestBean {
    private boolean status;
    private List<ApkItem> data = new ArrayList<ApkItem>();
    private String code;
    private String message;
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<ApkItem> getData() {
        return data;
    }

    public void setData(List<ApkItem> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
