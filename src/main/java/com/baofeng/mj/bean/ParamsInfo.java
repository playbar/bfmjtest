package com.baofeng.mj.bean;

/**
 * Created by zhaominglei on 2016/5/16.
 * 用户中心请求参数生成json数据定义的mode
 */
public class ParamsInfo {
    public String key = "";
    public String value = "";

    public ParamsInfo(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
