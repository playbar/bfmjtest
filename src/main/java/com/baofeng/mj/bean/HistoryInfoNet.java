package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by liuchuanchi on 2016/5/18.
 * 历史信息
 */
public class HistoryInfoNet implements Serializable{
    private static final long serialVersionUID = 3553185462425337213L;

    private String object_id;//资源id
    private String user_id;//用户id
    private String seq;//当前集数
    private String length;//片长
    private String play_time;//当前播放点
    private String dimension;//视频维度(1：2D，2:3D上下，3:3D左右)
    private String hd_type;//当前选择清晰度
    private String create_time;//记录时间
    private String title;//资源名称
    private String object_type;//资源类型(1:影视，4：视频)
    private String h_thumb;//缩略图（270*360）
    private String w_thumb;//缩略图（400*300）
    private String url;//资源详情地址

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPlay_time() {
        return play_time;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getHd_type() {
        return hd_type;
    }

    public void setHd_type(String hd_type) {
        this.hd_type = hd_type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public String getH_thumb() {
        return h_thumb;
    }

    public void setH_thumb(String h_thumb) {
        this.h_thumb = h_thumb;
    }

    public String getW_thumb() {
        return w_thumb;
    }

    public void setW_thumb(String w_thumb) {
        this.w_thumb = w_thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
