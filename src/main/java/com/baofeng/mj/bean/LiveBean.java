package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**直播详情bean
 * Created by muyu on 2016/5/22.
 */
public class LiveBean implements Serializable{


    /**
     * res_id : 200095
     * title : 暴风影音上市
     * subtitle : 上市敲钟仪式
     * thumb_pic_url : ["http://img.static.mojing.cn/picture/150323/1427098490.jpg"]
     * type : 5
     * video_dimension : 1
     * general_playurl : http://livetest.mojing.cn:8090/hls/livetest.m3u8
     * live_type : 1
     * live_start_time : 2015-03-24 08:50:00
     * live_end_time : 2015-03-24 09:30:00
     * expire_time : 2015-03-24 09:40:00
     * desc : 暴风影音上市敲钟仪式！
     * payment_type : 0
     * payment_count : 0
     * onlineview_count : 54
     * is_pay : 0
     * score : 2.3
     * score_count : 6
     * Source : 暴风魔镜
     * review_id : 0
     * screenshot : ["http://img.static.mojing.cn/picture/150323/1427088920.jpg"]
     */

    private LandscapeUrlBean landscape_url;

    private int res_id;
    private String title;
    private String subtitle;
    private int type;
    private int subType;
    private int video_dimension;
    private String general_playurl;
    private int live_type;
    private long live_start_time;
    private long live_end_time;
    private String expire_time;
    private String desc;
    private String payment_type;
    private String payment_count;
    private int onlineview_count;
    private int is_pay;
    private float score;
    private String score_count;
    private String Source;
    private int review_id;
    private List<String> thumb_pic_url;
    private List<String> screenshot;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getVideo_dimension() {
        return video_dimension;
    }

    public void setVideo_dimension(int video_dimension) {
        this.video_dimension = video_dimension;
    }

    public String getGeneral_playurl() {
        return general_playurl;
    }

    public void setGeneral_playurl(String general_playurl) {
        this.general_playurl = general_playurl;
    }

    public int getLive_type() {
        return live_type;
    }

    public void setLive_type(int live_type) {
        this.live_type = live_type;
    }

    public long getLive_start_time() {
        return live_start_time;
    }

    public void setLive_start_time(long live_start_time) {
        this.live_start_time = live_start_time;
    }

    public long getLive_end_time() {
        return live_end_time;
    }

    public void setLive_end_time(long live_end_time) {
        this.live_end_time = live_end_time;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPayment_count() {
        return payment_count;
    }

    public void setPayment_count(String payment_count) {
        this.payment_count = payment_count;
    }

    public int getOnlineview_count() {
        return onlineview_count;
    }

    public void setOnlineview_count(int onlineview_count) {
        this.onlineview_count = onlineview_count;
    }

    public int getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(int is_pay) {
        this.is_pay = is_pay;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getScore_count() {
        return score_count;
    }

    public void setScore_count(String score_count) {
        this.score_count = score_count;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public int getReview_id() {
        return review_id;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public List<String> getThumb_pic_url() {
        return thumb_pic_url;
    }

    public void setThumb_pic_url(List<String> thumb_pic_url) {
        this.thumb_pic_url = thumb_pic_url;
    }

    public List<String> getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(List<String> screenshot) {
        this.screenshot = screenshot;
    }

    public LandscapeUrlBean getLandscape_url() {
        return landscape_url;
    }

    public void setLandscape_url(LandscapeUrlBean landscape_url) {
        this.landscape_url = landscape_url;
    }
}
