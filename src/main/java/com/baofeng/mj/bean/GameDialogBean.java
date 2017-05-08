package com.baofeng.mj.bean;

/**
 * Created by muyu on 2017/4/10.
 */
public class GameDialogBean {
    private String image_info;
    private String url;
    private String status; //非零，接口认证失败

    public String getImage_info() {
        return image_info;
    }

    public void setImage_info(String image_info) {
        this.image_info = image_info;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
