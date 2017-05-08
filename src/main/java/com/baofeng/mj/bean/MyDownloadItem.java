package com.baofeng.mj.bean;

import com.mojing.dl.domain.DownloadItem;

/**
 * Created by liuchuanchi on 2016/11/18.
 * 自定义下载实体类（扩展字段）
 */
public class MyDownloadItem extends DownloadItem {
    private int is4k;//下载4k视频为1，其他为0
    private int is_panorama;//全景控制
    private int video_dimension;//全景视频播放模式
    private int pov_heading;//初始角度
    private int operation_type;//播放类型
    private String source;//资源来源
    private String play_mode;//游戏的控制方式
    private String url;//资源详情的访问接口

    public MyDownloadItem(int itemType) {
        super(itemType);
    }

    public int getIs4k() {
        return is4k;
    }

    public void setIs4k(int is4k) {
        this.is4k = is4k;
    }

    public int getIs_panorama() {
        return is_panorama;
    }

    public void setIs_panorama(int is_panorama) {
        this.is_panorama = is_panorama;
    }

    public int getVideo_dimension() {
        return video_dimension;
    }

    public void setVideo_dimension(int video_dimension) {
        this.video_dimension = video_dimension;
    }

    public int getPov_heading() {
        return pov_heading;
    }

    public void setPov_heading(int pov_heading) {
        this.pov_heading = pov_heading;
    }

    public int getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(int operation_type) {
        this.operation_type = operation_type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(String play_mode) {
        this.play_mode = play_mode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
