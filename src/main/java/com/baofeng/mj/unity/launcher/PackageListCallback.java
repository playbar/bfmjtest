package com.baofeng.mj.unity.launcher;

/*
 * getAppPackageList
 */
public interface PackageListCallback extends AndroidCallback {
    /**
     * 返回应用程序列表数据
     * @param msg json 数据
     */
    public void onPackageListResult(String msg);
}
