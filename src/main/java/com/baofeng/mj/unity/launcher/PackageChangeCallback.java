package com.baofeng.mj.unity.launcher;

/*
 * register on applist page
 */
public interface PackageChangeCallback extends AndroidCallback {
    /**
     * 安装app 事件的回调方法
     * @param msg 返回参数 格式如下：
     *
     */
    public void onInstallPackageInfo(String msg);

    /**
     * 卸载app 事件的回调方法
     * @param msg 返回参数 格式如下：
     */
    public void onUninstallPackageInfo(String msg);

    /**
     * 软件更新 事件回调
     * @param msg
     */
    public void onPackageUpdateInfo(String msg);
}
