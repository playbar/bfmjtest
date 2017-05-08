package com.baofeng.mj.bean;

import java.io.Serializable;

/** 和U3D交互的层级关系
 * Created by muyu on 2016/6/2.
 */
public class HierarchyBean implements Serializable{

    private String subActivityName; //自己写的。测试用
    private String detailUrl;//自己写的。测试用
    //服务器返回
    private String type;
    private String subType;
    private LandscapeUrlBean landscapeUrlBean;

    private String currentNavId; //二级层级id号
    private HeadData headData;

    private Local local;
    private String currentVideoIndex;//影视播放 当前剧集数
    private String online_resource_from;//在线资源来自哪里
    private String pageType;//横屏播放报数用，记录点击播放的页面类型

    public static class HeadData implements Serializable{
        private String headUrl; //首页入口
        private String currentNavId; //一级层级id号

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getCurrentNavId() {
            return currentNavId;
        }

        public void setCurrentNavId(String currentNavId) {
            this.currentNavId = currentNavId;
        }

        @Override
        public String toString() {
            return "{" +
                    "headUrl='" + headUrl + '\'' +
                    ", currentNavId='" + currentNavId + '\'' +
                    '}';
        }
    }

    public static class Local implements Serializable{
        private String name;
        private String localPath;
        private String download_url;//下载url（全景漫游用）
        private String local_resource_from;//本地资源来自：1已下载，2本地
        private String video_type;//视频类型
        private String is4k; //视频是否为4k类型

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocalPath() {
            return localPath;
        }

        public void setLocalPath(String localPath) {
            this.localPath = localPath;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public String getLocal_resource_from() {
            return local_resource_from;
        }

        public void setLocal_resource_from(String local_resource_from) {
            this.local_resource_from = local_resource_from;
        }

        public String getVideo_type() {
            return video_type;
        }

        public void setVideo_type(String video_type) {
            this.video_type = video_type;
        }

        public String getIs4k() {
            return is4k;
        }

        public void setIs4k(String is4k) {
            this.is4k = is4k;
        }

        @Override
        public String toString() {
            return "Local{" +
                    "name='" + name + '\'' +
                    ", localPath='" + localPath + '\'' +
                    ", download_url='" + download_url + '\'' +
                    ", local_resource_from='" + local_resource_from + '\'' +
                    ", video_type='" + video_type + '\'' +
                    ", is4k='" + is4k + '\'' +
                    '}';
        }
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public String getSubActivityName() {
        return subActivityName;
    }

    public void setSubActivityName(String subActivityName) {
        this.subActivityName = subActivityName;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public LandscapeUrlBean getLandscapeUrlBean() {
        return landscapeUrlBean;
    }

    public void setLandscapeUrlBean(LandscapeUrlBean landscapeUrlBean) {
        this.landscapeUrlBean = landscapeUrlBean;
    }

    public String getCurrentNavId() {
        return currentNavId;
    }

    public void setCurrentNavId(String currentNavId) {
        this.currentNavId = currentNavId;
    }

    public HeadData getHeadData() {
        return headData;
    }

    public void setHeadData(HeadData headData) {
        this.headData = headData;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getCurrentVideoIndex() {
        return currentVideoIndex;
    }

    public void setCurrentVideoIndex(String currentVideoIndex) {
        this.currentVideoIndex = currentVideoIndex;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getOnline_resource_from() {
        return online_resource_from;
    }

    public void setOnline_resource_from(String online_resource_from) {
        this.online_resource_from = online_resource_from;
    }

    @Override
    public String toString() {
        return "HierarchyBean{" +
                "subActivityName='" + subActivityName + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", landscapeUrlBean=" + landscapeUrlBean +
                ", currentNavId='" + currentNavId + '\'' +
                ", headData=" + headData +
                ", local=" + local +
                ", currentVideoIndex='" + currentVideoIndex + '\'' +
                ", pageType='" + pageType + '\'' +
                '}';
    }
}
