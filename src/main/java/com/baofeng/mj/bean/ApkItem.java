package com.baofeng.mj.bean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author wanghongfang
 * 插件管理 用的实体类
 */
public class ApkItem {
    private Drawable icon;
    private int versionCode;
    private String apkfile;  //路径
    private PackageInfo packageInfo;
    private String plugin_id;  // 插件ID  跟cms后台配置的id相对应
    private String download_url;  //插件下载地址
    private String upgrade_type;  // 升级类型，1：正式升级；2：测试升级
    private String version_name;  //该插件支持的最小魔镜版本
    private String plugin_version_name; //插件版本
    private String apk_name;     //插件包名
    private String plugin_name;  //插件名称

     private boolean installing = false;

    public ApkItem(){

    }
    public ApkItem(Context context, PackageInfo info, String path) {
        PackageManager pm = context.getPackageManager();
        Resources resources = null;
        try {
            resources = getResources(context, path);
        } catch (Exception e) {
        }
        try {
            if (resources != null) {
                icon = resources.getDrawable(info.applicationInfo.icon);
            }
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        try {
            if (resources != null) {
                plugin_name = resources.getString(info.applicationInfo.labelRes);
            }
        } catch (Exception e) {
            plugin_name = info.packageName;
        }

        plugin_version_name = info.versionName;
        versionCode = info.versionCode;
        apkfile = path;
        packageInfo = info;
    }

    public ApkItem(PackageManager pm, PackageInfo info, String path) {
        try {
            icon = pm.getApplicationIcon(info.applicationInfo);
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        plugin_name = pm.getApplicationLabel(info.applicationInfo).toString();
        plugin_version_name = info.versionName;
        versionCode = info.versionCode;
        apkfile = path;
        packageInfo = info;
    }

    public static Resources getResources(Context context, String apkPath) throws Exception {
        String PATH_AssetManager = "android.content.res.AssetManager";
        Class assetMagCls = Class.forName(PATH_AssetManager);
        Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
        Object assetMag = assetMagCt.newInstance((Object[]) null);
        Class[] typeArgs = new Class[1];
        typeArgs[0] = String.class;
        Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                typeArgs);
        Object[] valueArgs = new Object[1];
        valueArgs[0] = apkPath;
        assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
        Resources res = context.getResources();
        typeArgs = new Class[3];
        typeArgs[0] = assetMag.getClass();
        typeArgs[1] = res.getDisplayMetrics().getClass();
        typeArgs[2] = res.getConfiguration().getClass();
        Constructor resCt = Resources.class.getConstructor(typeArgs);
        valueArgs = new Object[3];
        valueArgs[0] = assetMag;
        valueArgs[1] = res.getDisplayMetrics();
        valueArgs[2] = res.getConfiguration();
        res = (Resources) resCt.newInstance(valueArgs);
        return res;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkfile() {
        return apkfile;
    }

    public void setApkfile(String apkfile) {
        this.apkfile = apkfile;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public boolean isInstalling() {
        return installing;
    }

    public void setInstalling(boolean installing) {
        this.installing = installing;
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
}