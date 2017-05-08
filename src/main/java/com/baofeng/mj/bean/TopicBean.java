package com.baofeng.mj.bean;

import java.util.List;

/**
 * Created by muyu on 2016/5/21.
 */
public class TopicBean {
    private int res_id;
    private String title;
    private String subtitle;
    private List<String> thumb_pic_url;
    private int type;
    private String desc;
    private String source;
    private String topic_recent_name;
    private LandscapeUrlBean landscape_url;
    private String list_url;

    public int getRes_id() {
        return res_id;
    }

    public void setRes_id(int res_id) {
        this.res_id = res_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getThumb_pic_url() {
        return thumb_pic_url;
    }

    public void setThumb_pic_url(List<String> thumb_pic_url) {
        this.thumb_pic_url = thumb_pic_url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTopic_recent_name() {
        return topic_recent_name;
    }

    public void setTopic_recent_name(String topic_recent_name) {
        this.topic_recent_name = topic_recent_name;
    }

    public String getList_url() {
        return list_url;
    }

    public void setList_url(String list_url) {
        this.list_url = list_url;
    }

    public LandscapeUrlBean getLandscape_url() {
        return landscape_url;
    }

    public void setLandscape_url(LandscapeUrlBean landscape_url) {
        this.landscape_url = landscape_url;
    }

    @Override
    public String toString() {
        return "TopicBean{" +
                "res_id=" + res_id +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", thumb_pic_url=" + thumb_pic_url +
                ", type=" + type +
                ", desc='" + desc + '\'' +
                ", source='" + source + '\'' +
                ", topic_recent_name='" + topic_recent_name + '\'' +
                ", landscape_url=" + landscape_url +
                ", list_url='" + list_url + '\'' +
                '}';
    }
}
