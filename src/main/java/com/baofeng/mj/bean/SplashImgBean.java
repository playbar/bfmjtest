package com.baofeng.mj.bean;

/**
 * Created by liuchuanchi on 2016/7/4.
 * splash图片实体类
 */
public class SplashImgBean {
    String image_url;//图片url
    String start_time;//开始时间
    String end_time;//结束时间
    String jump_url;//跳转url

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }
}
