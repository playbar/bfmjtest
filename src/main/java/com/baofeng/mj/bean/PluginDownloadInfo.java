package com.baofeng.mj.bean;

import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;

/**
 * Created by wanghongfang
 */
public class PluginDownloadInfo {
    public static final int Installing = 100;
    public static final int Installed = 101;
    public static final int InstallFailed = 102;
    private long id;
    // download status :0 default,1 downloading,2 paused,3 waiting,4 complete,5 error   //100 正在安装  101安装成功 102安装失败
    private int status = MjDownloadStatus.DEFAULT;
    private double progress;
    private String path;
    private long errorCode;
    private int plugin_upgrade = 0;  //0 不升级 1：升级
    /**
     * 使用的缓存类型：1三级缓存（内存、本地、网络），2二级缓存（本地、网络），3一级缓存（网络）
     */
    private int cacheType = 2;
   //定义cms后台数据，存数据库时使用//
    private String plugin_id;  // 插件ID  跟cms后台配置的id相对应
    private String download_url;  //插件下载地址
    private String upgrade_type;  // 升级类型，1：正式升级；2：测试升级
    private String version_name;  //该插件支持的最小魔镜版本
    private String plugin_version_name; //插件版本
    private String apk_name;     //插件包名
    private String plugin_name;  //插件名称

    public int getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCacheType() {
        return cacheType;
    }

    public void setCacheType(int cacheType) {
        this.cacheType = cacheType;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public String getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(String plugin_id) {
        this.plugin_id = plugin_id;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getUpgrade_type() {
        return upgrade_type;
    }

    public void setUpgrade_type(String upgrade_type) {
        this.upgrade_type = upgrade_type;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getPlugin_version_name() {
        return plugin_version_name;
    }

    public void setPlugin_version_name(String plugin_version_name) {
        this.plugin_version_name = plugin_version_name;
    }

    public String getApk_name() {
        return apk_name;
    }

    public void setApk_name(String apk_name) {
        this.apk_name = apk_name;
    }

    public String getPlugin_name() {
        return plugin_name;
    }

    public void setPlugin_name(String plugin_name) {
        this.plugin_name = plugin_name;
    }

    public int getPlugin_upgrade() {
        return plugin_upgrade;
    }

    public void setPlugin_upgrade(int plugin_upgrade) {
        this.plugin_upgrade = plugin_upgrade;
    }

    @Override
    public String toString() {
        return "PluginDownloadInfo{" +
                "id=" + id +
                ", status=" + status +
                ", progress=" + progress +
                ", path='" + path + '\'' +
                ", errorCode=" + errorCode +
                ", cacheType=" + cacheType +
                ", plugin_id='" + plugin_id + '\'' +
                ", download_url='" + download_url + '\'' +
                ", upgrade_type='" + upgrade_type + '\'' +
                ", version_name='" + version_name + '\'' +
                ", plugin_version_name='" + plugin_version_name + '\'' +
                ", apk_name='" + apk_name + '\'' +
                ", plugin_name='" + plugin_name + '\'' +
                '}';
    }

    public PluginDownloadInfo clone(PluginDownloadInfo info){
        PluginDownloadInfo info1 = new PluginDownloadInfo();
        info1.setApk_name(info.getApk_name());
        info1.setVersion_name(info.getVersion_name());
        info1.setPlugin_version_name(info.getPlugin_version_name());
        info1.setStatus(info.getStatus());
        info1.setPlugin_name(info.getPlugin_name());
        info1.setDownload_url(info.getDownload_url());
        info1.setId(info.getId());
        info1.setPlugin_id(info.getPlugin_id());
        info1.setUpgrade_type(info.getUpgrade_type());
        info1.setPath(info.getPath());
        info1.setProgress(info.getProgress());
        info1.setErrorCode(info.getErrorCode());
        info1.setCacheType(info.getCacheType());
        info1.setPlugin_upgrade(info.getPlugin_upgrade());
        return info1;
    }
}
