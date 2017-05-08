package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/5/13.
 * 游戏实体类
 */
public class GameDetailBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 4683339411088532142L;

    private LandscapeUrlBean landscape_url;
    private String app_id;//评论所需id
    private String title;//资源名称
    private String subtitle;//副标题
    private String icon_url;//游戏icon
    private String desc;//游戏描述
    private String payment_type;//付费方式
    private String payment_count;//付费金额
    private String is_pay;//是否需要付费
    private String download_count;//下载次数
    private String score;//评分
    private String score_count;//评分人数
    private String source;//游戏来源
    private String typename;//资源所属类型
    private String size;//资源大小
    private String download_url;//资源下载地址
    private String package_name;//资源包名
    private String version;//资源版本
    private String versioncode;//资源版本号
    private String background_url;//资源背景图片地址
    private String cat_two_name;//资源所属第二类型
    private String play_feature;//特色玩法
    private List<String> thumb_pic_url;//未知
    private List<String> play_mode;//操作方式
    private List<String> bigimages;//未知
    private String brief;//硬件适配
    private String activity_pic;//活动图片地址
    private String activity_url;//活动地址

    public String getActivity_pic() {
        return activity_pic;
    }

    public void setActivity_pic(String activity_pic) {
        this.activity_pic = activity_pic;
    }

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
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

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
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

    public String getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(String is_pay) {
        this.is_pay = is_pay;
    }

    public String getDownload_count() {
        return download_count;
    }

    public void setDownload_count(String download_count) {
        this.download_count = download_count;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore_count() {
        return score_count;
    }

    public void setScore_count(String score_count) {
        this.score_count = score_count;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getBackground_url() {
        return background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public String getCat_two_name() {
        return cat_two_name;
    }

    public void setCat_two_name(String cat_two_name) {
        this.cat_two_name = cat_two_name;
    }

    public String getPlay_feature() {
        return play_feature;
    }

    public void setPlay_feature(String play_feature) {
        this.play_feature = play_feature;
    }

    public List<String> getThumb_pic_url() {
        return thumb_pic_url;
    }

    public void setThumb_pic_url(List<String> thumb_pic_url) {
        this.thumb_pic_url = thumb_pic_url;
    }

    public List<String> getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(List<String> play_mode) {
        this.play_mode = play_mode;
    }

    public List<String> getBigimages() {
        return bigimages;
    }

    public void setBigimages(List<String> bigimages) {
        this.bigimages = bigimages;
    }

    public LandscapeUrlBean getLandscape_url() {
        return landscape_url;
    }

    public void setLandscape_url(LandscapeUrlBean landscape_url) {
        this.landscape_url = landscape_url;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }
}
