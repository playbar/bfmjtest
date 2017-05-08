package com.baofeng.mj.bean;

/**
 * Created by liuchuanchi on 2016/6/3.
 * 已支付资源信息
 */
public class PayInfo {
    private int res_type;//资源类型
    private String res_id;//资源id

    public int getRes_type() {
        return res_type;
    }

    public void setRes_type(int res_type) {
        this.res_type = res_type;
    }

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }
}
