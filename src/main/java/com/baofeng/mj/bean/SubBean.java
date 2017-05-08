package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by hanyang on 2016/6/21.
 * 单个订阅专辑实体类
 */
public class SubBean implements Serializable {
    private String album_id;
    private String album_name;
    private String album_thumbnail;
    private int album_modify_time;
    private String album_description;

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_thumbnail() {
        return album_thumbnail;
    }

    public void setAlbum_thumbnail(String album_thumbnail) {
        this.album_thumbnail = album_thumbnail;
    }

    public String getAlbum_description() {
        return album_description;
    }

    public void setAlbum_description(String album_description) {
        this.album_description = album_description;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public int getAlbum_modify_time() {
        return album_modify_time;
    }

    public void setAlbum_modify_time(int album_modify_time) {
        this.album_modify_time = album_modify_time;
    }
}
