package com.baofeng.mj.bean;

/**
 * Created by liuchuanchi on 2016/4/28.
 * 用户登录信息
 */
public class UserInfo {
    private String uid;
    private String username;
    private String nikename;
    private String mobile;
    private String userPhone;
    //魔币
    private String recharge_modou;
    //魔豆
    private String gift_modou;
    private String bfcsid;
    private String st;
    private int ssottl;
    private String ssostatus;
    private String logoUrl;
    private String email;

//    public String getUserPhone() {
//        return userPhone;
//    }
//
//    public void setUserPhone(String userPhone) {
//        this.userPhone = userPhone;
//    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRecharge_modou() {
        return recharge_modou;
    }

    public void setRecharge_modou(String recharge_modou) {
        this.recharge_modou = recharge_modou;
    }

    public String getGift_modou() {
        return gift_modou;
    }

    public void setGift_modou(String gift_modou) {
        this.gift_modou = gift_modou;
    }

    public String getBfcsid() {
        return bfcsid;
    }

    public void setBfcsid(String bfcsid) {
        this.bfcsid = bfcsid;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public int getSsottl() {
        return ssottl;
    }

    public void setSsottl(int ssottl) {
        this.ssottl = ssottl;
    }

    public String getSsostatus() {
        return ssostatus;
    }

    public void setSsostatus(String ssostatus) {
        this.ssostatus = ssostatus;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
