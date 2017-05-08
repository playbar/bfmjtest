package com.baofeng.mj.util.systemutil;

import android.app.PackageInstallObserver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.callback.PackageChangeCallback;
import com.storm.smart.common.utils.LogHelper;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by pc on 2016/5/19.
 */
public class FileManager {

    private static Context mContext;
    private static FileManager mManager;
    private UninstallThread mUninstallThread;
    //方法所用的时间，做测试用

    public static FileManager getAndroidManager() {
        return getInstance();
    }

    private synchronized static FileManager getInstance() {
        if (mManager == null) {
            mManager = new FileManager();
        }
        mContext = BaseApplication.INSTANCE;
        return mManager;
    }

    /**
     * 安装apk
     * @param filePath apk的路径
     * @param callback 安装的回调
     */
    public void installPackage(final String filePath, final PackageChangeCallback callback) {
        if (TextUtils.isEmpty(filePath) || null == callback) {
            //参数不能为空
            callback.onPackageInstalled(0);
            LogHelper.e("infos","=====参数不能为空========");
        }
        final PackageManager mPackageManager = mContext.getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppPackageInstallObserver appPackageChangeObserver = new AppPackageInstallObserver(callback);
                File file = new File(filePath);
                if (!file.exists()) {
                    //路径不存在
                    callback.onPackageInstalled(-1);
                    LogHelper.e("infos","=====路径不存在========");
                } else {
                    PackageInfo info = mPackageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                    if (info != null) {
                        ApplicationInfo mAppInfo = info.applicationInfo;
                        mAppInfo.sourceDir = filePath;
                        mAppInfo.publicSourceDir = filePath;
                        int installFlags = 0;
                        try {
                            //noinspection WrongConstant
                            PackageInfo packageInfo = mPackageManager.getPackageInfo(mAppInfo.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                            if (packageInfo != null) {
                                //apk已经安装，所以替换安装
                                installFlags |= 2;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            //忽略
                            e.printStackTrace();
                            LogHelper.e("infos","=====忽略========");
                        }
                        try {
                            invokeInstallPackageMethod(mPackageManager, Uri.fromFile(file),
                                    appPackageChangeObserver,
                                    installFlags, mAppInfo.packageName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //安装失败
                            callback.onPackageInstalled(-2);
                            LogHelper.e("infos","=====安装失败========");
                        }
                    } else {
                        //解析出现问题
                        callback.onPackageInstalled(-3);
                        LogHelper.e("infos","=====解析出现问题========");
                    }
                }
            }
        }).run();
    }

    private void invokeInstallPackageMethod(PackageManager pm, Uri packageURI,
                                            PackageInstallObserver observer,
                                            int flags, String installerPackageName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends PackageManager> aClass = pm.getClass();
        //反射调用installPackage方法
        Method installPackage = aClass.getMethod("installPackage",
                new Class[]{Uri.class, PackageInstallObserver.class, int.class, String.class});
        installPackage.invoke(pm, packageURI, observer, flags, installerPackageName);
    }

    /**
     * 用于回调
     */
    private class AppPackageInstallObserver extends PackageInstallObserver {
        private PackageChangeCallback mListener;

        public AppPackageInstallObserver(PackageChangeCallback callback) {
            this.mListener = callback;
        }

        public void onPackageInstalled(String basePackageName, int returnCode,
                                       String msg, Bundle extras) {
            if (mListener != null) {
                mListener.onPackageInstalled(returnCode);
            }
        }
    }


    /**
     * 卸载指定的apk
     * @param packageName 指定zpk的packagename
     */
    public void uninstallPackage(String packageName) {
        mUninstallThread = new UninstallThread();
        mUninstallThread.setPackageName(packageName);
        mUninstallThread.start();
    }

    private class UninstallThread extends Thread {
        public String packageName = null;

        public void setPackageName(String string) {
            this.packageName = string;
        }
        @Override
        public void run() {
            if (TextUtils.isEmpty(packageName)) {
                return;
            }
            try {
                Object packageManagerService = getPackageManagerService();
                Class<?> pmService = packageManagerService.getClass();
                Class<?>[] paramTypes1 = getParamTypes(pmService, "deletePackage");
                Method deletePackage = pmService.getMethod("deletePackage", paramTypes1);
                //deletePackage方法的userid参数
                Class<?> userHandle = UserHandle.class;
                Class<?>[] myUserIds = getParamTypes(userHandle, "myUserId");
                Method myUserId = userHandle.getMethod("myUserId", myUserIds);
                int userId = (int) myUserId.invoke(null);

                deletePackage.invoke(packageManagerService, packageName, null, userId, 0);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * apk安装、卸载时需要使用的对象
     * @return 安装、卸载具体实现方法的对象
     */
    public static Object getPackageManagerService(){
        Object packageManagerService = null;
        try {
            //通过ActivityThread获取PackageManagerService对象
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            //getPackageManager的参数
            Class<?>[] paramTypes = getParamTypes(activityThread,"getPackageManager");
            //获取getPackageManager的对象
            Method getPackageManager = activityThread.getMethod("getPackageManager", paramTypes);
            packageManagerService = getPackageManager.invoke(activityThread);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packageManagerService;
    }

    /**
     * 获取某个方法的参数
     * @param cls
     * @param mName 方法名
     * @return 参数类型的数组
     */
    public static Class<?>[] getParamTypes(Class<?> cls, String mName) {
        Class<?> cs[] = null;

        Method[] mtd = cls.getMethods();

        for (int i = 0; i < mtd.length; i++) {
            if (!mtd[i].getName().equals(mName)) {
                continue;
            }
            cs = mtd[i].getParameterTypes();
        }
        return cs;
    }

}
