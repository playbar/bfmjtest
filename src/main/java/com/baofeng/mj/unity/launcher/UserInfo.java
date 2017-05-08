package com.baofeng.mj.unity.launcher;

import java.io.Serializable;

public class UserInfo  implements Serializable {


    private static final long serialVersionUID = -5747329406283154256L;
    public UserInfo(){

    }
    private String user_tel;
    private String serailNumber;
    private String user_name;
    private String password;
    private String token;
    //uid
    private String user_no;
//头像
    private String user_head_url;
    public UserInfo(String phoneNumber, String serailNumber) {
        this.user_tel = phoneNumber;
        this.serailNumber = serailNumber;
    }

    public String getPhoneNumber() {
        return user_tel;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.user_tel = phoneNumber;
    }

    public String getSerailNumber() {
        return serailNumber;
    }

    public void setSerailNumber(String serailNumber) {
        this.serailNumber = serailNumber;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return user_no;
    }

    public void setUid(String uid) {
        this.user_no = uid;
    }

    public String getUser_head_url() {
        return user_head_url;
    }

    public void setUser_head_url(String user_head_url) {
        this.user_head_url = user_head_url;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "user_tel='" + user_tel + '\'' +
                ", serailNumber='" + serailNumber + '\'' +
                ", user_name='" + user_name + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", user_no='" + user_no + '\'' +
                ", user_head_url='" + user_head_url + '\'' +
                '}';
    }
}
