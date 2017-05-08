package com.baofeng.mj.unity.launcher;

import android.content.pm.ApplicationInfo;

public class PackageItem {

    private String mPackageName;
    private String mAppName;
    private String mActivityname;
    private String mAppIcon;
    private int mAppFlag;
    private boolean isSystemApp;
    // private IconData mData;
    // 安装时间
    private long installTime = 1482501147000L;
    private boolean mIsPlayerApp;

    public PackageItem(String mPackageName, String mAppName, int mAppFlag) {
        this.mPackageName = mPackageName;
        this.mAppName = mAppName;
        this.mAppFlag = mAppFlag;
    }

    public PackageItem(String mPackageName, String mAppName,
                       String activityName, /* IconData data, */int mAppFlag,boolean isPlayerApp) {
        this.mPackageName = mPackageName;
        this.mAppName = mAppName;
        this.mActivityname = activityName;
        // this.mData = data;
        this.mAppFlag = mAppFlag;
        mIsPlayerApp = isPlayerApp;
        setSystemApp((mAppFlag & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public PackageItem(String mPackageName, String mAppName,
                       String activityName, String mAppIcon, /* IconData data, */int mAppFlag,boolean isPlayerApp) {
        this.mPackageName = mPackageName;
        this.mAppName = mAppName;
        this.mActivityname = activityName;
        this.mAppIcon = mAppIcon;
        // this.mData = data;
        this.mAppFlag = mAppFlag;
        mIsPlayerApp = isPlayerApp;
        setSystemApp((mAppFlag & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public int getAppFlag() {
        return mAppFlag;
    }

    public void setAppFlag(int mAppFlag) {
        this.mAppFlag = mAppFlag;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public String getActivityname() {
        return mActivityname;
    }

    public void setActivityname(String mActivityname) {
        this.mActivityname = mActivityname;
    }

    public String getmAppIcon() {
        return mAppIcon;
    }

    public void setmAppIcon(String mAppIcon) {
        this.mAppIcon = mAppIcon;
    }

    // public IconData getData() {
    // return mData;
    // }
    //
    // public void setData(IconData mData) {
    // this.mData = mData;
    // }

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }

    public boolean ismIsPlayerApp() {
        return mIsPlayerApp;
    }

    public void setmIsPlayerApp(boolean mIsPlayerApp) {
        this.mIsPlayerApp = mIsPlayerApp;
    }
}
