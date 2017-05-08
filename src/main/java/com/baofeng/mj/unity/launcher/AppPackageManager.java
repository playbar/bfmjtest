package com.baofeng.mj.unity.launcher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.UserHandle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;

import com.baofeng.mj.business.downloadbusiness.DownloadResInfoSaveBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.threadutil.LocalDownloadProxy;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.google.gson.Gson;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;


/**
 * 获取应用列表
 * Created by dupengwei on 2016/12/20.
 */
public class AppPackageManager  extends AndroidManager {

    private Context mContext;
    private static AppPackageManager mAppPackageManager;
    private PackageManager mPackageManager;
    private ActivityManager mActivitymanager;
    private IconCache mIconCache;
    private AppChangeReceiver mReceiver;
    private Gson mGson = new Gson();
    private PackageChangeCallback callback;
    private List<PackageItem> mInstalledAppList = new ArrayList<>();
    private AppListThread mThread;
    private UninstallThread mUninstallThread;
    private final String mojingApp = "com.baofeng.mj";
    private final String SETTINGPACKAGE = "com.baofeng.aone.settings";
    private final String FILEMANAGERPACKAGE = "com.baofeng.aone.filemanager";
    public static String TAG = "AppPackageManager";
    //应用程序的图标文件夹路径
    private String APPIconPath;
    private boolean mIsPlayerApp;
    private String mIconPath;
    AppPackageManager(Context mContext) {
        this.mContext = mContext;
        this.mPackageManager = mContext.getPackageManager();
        this.mActivitymanager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);

        mIconCache = new IconCache(mPackageManager);
        if (mReceiver == null) {
            mReceiver = new AppChangeReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mReceiver, filter);
    }

    private synchronized static AppPackageManager getAppPackageManager(
            Context context) {
        if (mAppPackageManager == null) {
            mAppPackageManager = new AppPackageManager(context);
        }
        return mAppPackageManager;
    }

    private class AppChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            final String packageName = intent.getData().getSchemeSpecificPart();
            final String action = intent.getAction();
            if (action == null)
                return;
            LogHelper.e("infos","packageName=="+packageName+"==action==="+action);
            LocalDownloadProxy.getInstance().addProxyRunnable(new LocalDownloadProxy.ProxyRunnable() {
                @Override
                public void run() {
                    DownloadResInfoSaveBusiness.getDownloadInfoData(new DownloadResInfoSaveBusiness.DownloadInfoCallback() {
                        @Override
                        public void downloadInfoCallback(TreeMap<Long, String> downloadInfoMap) {
                            boolean isPlayerApp = false;
                            LogHelper.e("infos","downloadInfoMap.size==="+downloadInfoMap.size());
                            if(!downloadInfoMap.isEmpty()){
                                Iterator it = downloadInfoMap.keySet().iterator();
                                while (it.hasNext()) {
                                    String string = downloadInfoMap.get(it.next());
                                    LogHelper.e("infos","string=="+string);
                                    try{
                                        JSONObject item = new JSONObject(string);
                                        LogHelper.e("infos","item.getpackageName=="+item.getString("package_name")+"==packageName=="+packageName);
                                        if(item.getString("package_name").equals(packageName)){
                                            isPlayerApp = true;
                                            break;
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }


                                }
                            }

                            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                                LogHelper.e("infos","isPlayerApp===="+isPlayerApp);
                                receiver_install(packageName,isPlayerApp);
                            } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                                receiver_uninstall(packageName);
                                DownloadUtils.getInstance().deleteUninstallItem(packageName);
                            } else if (action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
                                receiver_change(packageName,isPlayerApp);
                            }
                        }
                    });
                }
            });

        }

    }

    private void receiver_install(String packageName,boolean isPlayerApp) {
        PackageItem item = Utils.getPackageItemFromPackageName(
                packageName, mPackageManager,isPlayerApp);
        if (item == null)
            return;
        //        Drawable iconDrawable = Utils
        //                .getIconDrawableFromPackageName(packageName,
        //                        mPackageManager);

        File f1 = new File(APPIconPath, packageName + ".png");
        if (f1.exists()) {
            f1.delete();
        }
        File f2 = new File(APPIconPath, packageName + "bj.png");
        if (f2.exists()) {
            f2.delete();
        }
        //得到应用的图标
        Drawable icon = Utils.getIconDrawableFromPackageName(packageName, mPackageManager);
        //将图标保存到本地,返回路径
        String iconPath = saveIcon(icon,packageName);
        //处理图片，放大，模糊，裁剪,并且保存在本地
//        if(!isPlayerApp){
            dealtIcon(icon,packageName);
//        }

        if(isBreakNew(mIconPath)){
            dealtIcon(icon,packageName);
            LogHelper.e("infos","=====install====损坏重新生成========");
        }
        isBreakNew(mIconPath);//再次出现，直接删除
        //设置路径
        item.setmAppIcon(iconPath);
        //添加到集合中
        mInstalledAppList.add(item);
        if (callback != null) {
            String msgString = mGson.toJson(item);
            callback.onInstallPackageInfo(msgString);
            LogHelper.e("infos","receiver_install_msgString=="+msgString);
        }
    }


    private void receiver_uninstall(String packageName) {
        PackageItem item = findPackageItemFromList(packageName);
        if (item == null)
            return;
        File f1 = new File(APPIconPath, packageName + ".png");
        if (f1.exists()) {
            f1.delete();
        }
        File f2 = new File(APPIconPath, packageName + "bj.png");
        if (f2.exists()) {
            f2.delete();
        }
        mInstalledAppList.remove(item);
        mIconCache.remove(packageName);
        if (callback != null) {
            String msgString = mGson.toJson(item);
            callback.onUninstallPackageInfo(msgString);
            LogHelper.e("infos","receiver_uninstall_msgString=="+msgString);
        }
    }

    private void receiver_change(String packageName,boolean isPlayerApp) {
        PackageItem item = findPackageItemFromList(packageName);
        if (item == null)
            return;

        LogHelper.e("infos","title=="+item.getAppName()+"===time===="+item.getInstallTime());
        mInstalledAppList.remove(item);
        mIconCache.remove(packageName);
        PackageItem updateItem = Utils
                .getPackageItemFromPackageName(packageName,
                        mPackageManager,isPlayerApp);
        if(updateItem == null)
            return;

        LogHelper.e("infos","title----"+item.getAppName()+"----time===="+item.getInstallTime());
        File f1 = new File(APPIconPath, packageName + ".png");
        if (f1.exists()) {
            f1.delete();
        }
        File f2 = new File(APPIconPath, packageName + "bj.png");
        if (f2.exists()) {
            f2.delete();
        }
        //得到应用的图标
        Drawable icon = Utils.getIconDrawableFromPackageName(packageName, mPackageManager);
        //将图标保存到本地,返回路径
        String iconPath = saveIcon(icon,packageName);
        //处理图片，放大，模糊，裁剪,并且保存在本地
//        if(!isPlayerApp){
            dealtIcon(icon,packageName);
//        }
        if(isBreakNew(mIconPath)){
            dealtIcon(icon,packageName);
            LogHelper.e("infos","=====change====损坏重新生成========");
        }
        isBreakNew(mIconPath);//再次出现，直接删除
        //设置路径
        item.setmAppIcon(iconPath);
        //添加到集合中
        mInstalledAppList.add(updateItem);
        if (callback != null) {
            String msgString = mGson.toJson(updateItem);
            callback.onPackageUpdateInfo(msgString);
            LogHelper.e("infos","receiver_change_msgString=="+msgString);
        }
    }

    private PackageItem findPackageItemFromList(String packageName) {
        if (mInstalledAppList.size() == 0)
            return null;
        for (PackageItem item : mInstalledAppList) {
            if (item.getPackageName().equals(packageName))
                return item;
        }
        return null;
    }

    private class AppListThread extends Thread {
        public PackageListCallback callback = null;

        public void run() {
           /* if (mInstalledAppList.size() > 0) {
                String msg = parseListToJson(mInstalledAppList);
                if (callback != null) {
                    callback.onPackageListResult(msg);
                }
                return;
            }*/
            // 如果需要时间排序 可改为getAppListSortByTime
            getAppListSortByTime(getFileterInfos(),callback);
 //           getAppList(getFileterInfos(), callback);
        }
    }

    private List<ResolveInfo> getFileterInfos() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = mPackageManager
                .queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(
                mPackageManager));
        return resolveInfos;
    }

   /* private void getAppList(final List<ResolveInfo> resolveInfos,
                            PackageListCallback listCallback) {
        // setting app 放到第一位
        PackageItem settingItem = null;
        PackageItem fileManagerItem = null;
        PackageItem baofengItem = null;
        String selfPackageName = BaseApplication.INSTANCE.getBaseContext().getPackageName();
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String packageName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(mPackageManager); // 获得应用程序的Label

            //得到应用的图标
            Drawable icon = Utils.getIconDrawableFromPackageName(packageName, mPackageManager);
            //将图标保存到本地
            String iconPath = saveIcon(icon,packageName);
            //处理图片，放大，模糊，裁剪,并且保存在本地
            dealtIcon(icon,packageName);

            int flag = reInfo.activityInfo.applicationInfo.flags;
//            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //系统程序
            LogHelper.i("infos","---packageName-"+packageName);
            if (SETTINGPACKAGE.equals(packageName)) {
                settingItem = new PackageItem(packageName, appLabel,
                            activityName,iconPath, flag,mIsPlayerApp);
                continue;
            } else if (packageName.equals(selfPackageName)) {
                continue;
            }else if(FILEMANAGERPACKAGE.equals(packageName)){
                fileManagerItem = new PackageItem(packageName, appLabel,
                        activityName,iconPath, flag,mIsPlayerApp);
                continue;
            }

            PackageItem item = new PackageItem(packageName, appLabel,
                    activityName,iconPath, flag,mIsPlayerApp);
            if (mojingApp.equals(packageName)) {
                baofengItem = item;
            } else {
                mInstalledAppList.add(item);
            }
        }
        //排序
        Collections.sort(mInstalledAppList, new Comparator<PackageItem>() {

            @Override
            public int compare(PackageItem lhs, PackageItem rhs) {
                //已安装在前
                if (lhs.isSystemApp() != rhs.isSystemApp()) {
                    return lhs.isSystemApp() ? 1 : -1;
                }
                return (int) ((rhs.getInstallTime() - lhs.getInstallTime()) / 1000);
            }
        });
        if (settingItem != null) {
            mInstalledAppList.add(0, settingItem);
        }
        if (fileManagerItem != null&& mInstalledAppList.size() >= 2) {
            mInstalledAppList.add(1, fileManagerItem);
        }
        if (baofengItem != null && mInstalledAppList.size() >= 3) {
            mInstalledAppList.add(2, baofengItem);
        } else if (baofengItem != null) {
            mInstalledAppList.add(baofengItem);
        }
        if (Utils.isLoadAllAppOnlyString && listCallback != null) {
            String msg = parseListToJson(mInstalledAppList);
            listCallback.onPackageListResult(msg);
        }
    }*/
    /**
     * 根据需求 按时间排序 暂时提供方法
     * @param
     * @param
     */
    /*private void getAppListSortByTimes(final List<ResolveInfo> resolveInfos,
                                      PackageListCallback listCallback) {
        PackageManager packageManager=mContext.getPackageManager();
        // setting app 放到第一位
        PackageItem settingItem = null;
        PackageItem baofengItem = null;
        String selfPackageName = BaseApplication.INSTANCE.getBaseContext().getPackageName();
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String packageName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(mPackageManager); // 获得应用程序的Label

            //得到应用的图标
            Drawable icon = Utils.getIconDrawableFromPackageName(packageName, mPackageManager);
            //将图标保存到本地
            String iconPath = saveIcon(icon,packageName);
            //处理图片，放大，模糊，裁剪,并且保存在本地
            dealtIcon(icon,packageName);
            if(isBreakNew(mIconPath)){
                dealtIcon(icon,packageName);
                LogHelper.e("infos","=====time====损坏重新生成========");
            }
           isBreakNew(mIconPath);//再次出现，直接删除
            int flag = reInfo.activityInfo.applicationInfo.flags;
            //            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
            //系统程序

            if (SETTINGPACKAGE.equals(packageName)) {
                settingItem = new PackageItem(packageName, appLabel,
                        activityName,iconPath, flag,mIsPlayerApp);
                //                }
                continue;
            } else if (packageName.equals(selfPackageName)) {
                continue;
            }

            PackageItem item = new PackageItem(packageName, appLabel,
                    activityName,iconPath, flag,mIsPlayerApp);
            if(item.isSystemApp()){
                continue;
            }
  //          setInstallTime(packageManager,item);
//            if (mojingApp.equals(packageName)) {
//                baofengItem = item;
//            } else {
            mInstalledAppList.add(item);
//            }
        }

//        if (settingItem != null) {
//            mInstalledAppList.add(0, settingItem);
//        }
//        if (baofengItem != null && mInstalledAppList.size() >= 2) {
//            mInstalledAppList.add(1, baofengItem);
//        } else if (baofengItem != null) {
//            mInstalledAppList.add(baofengItem);
//        }
        Collections.sort(mInstalledAppList, new Comparator<PackageItem>() {
            @Override
            public int compare(PackageItem lhs, PackageItem rhs) {
//                    //已安装在前
//                    if (lhs.isSystemApp() != rhs.isSystemApp()) {
//                        return lhs.isSystemApp() ? 1 : -1;
//                    }
                return (int) ((rhs.getInstallTime() - lhs.getInstallTime()) / 1000);
            }
        });
        if (Utils.isLoadAllAppOnlyString && listCallback != null) {
            String msg = parseListToJson(mInstalledAppList);
            listCallback.onPackageListResult(msg);
        }
    }*/
    private boolean isBreakNew(String path){
            //表示图片已损毁
            File file = new File(path);
        if(file.exists() && file.length() < 1024){
            file.delete();
            return true;
        }
        return false;
    }
   /* private boolean isBreak(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options); //filePath代表图片路径
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            //表示图片已损毁
            File file = new File(path);
           if(file.exists()){
               file.delete();
           }
            return true;
        }

        return false;
    }*/
    /**
     * 根据需求 按时间排序 暂时提供方法
     * @param resolveInfos
     * @param listCallback
     */
    private void getAppListSortByTime(final List<ResolveInfo> resolveInfos,
                            PackageListCallback listCallback) {
        mInstalledAppList.clear();
        PackageManager packageManager=mContext.getPackageManager();
        // setting app 放到第一位
        PackageItem settingItem = null;
        PackageItem baofengItem = null;
        PackageItem fileManagerItem = null;
        String selfPackageName = BaseApplication.INSTANCE.getBaseContext().getPackageName();
        for (ResolveInfo reInfo : resolveInfos) {
            mIsPlayerApp = false;
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            final String packageName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(mPackageManager); // 获得应用程序的Label
            int flag = reInfo.activityInfo.applicationInfo.flags;
            //filter myself
            if (packageName.equals(BaseApplication.INSTANCE.getPackageName())) {
                continue;
            }

            //filter system apps
           /* if (filterApp(flag)&& (!packageName.equals("com.mediatek.factorymode")
                                        && !packageName.equals("com.android.chrome"))
                                        && !packageName.equals("appcom.mediatek.factorymode")) {//排除这些应用
                Log.e("infos","packageName========"+packageName);
                continue;
            }*/
            if (filterApp(flag)&& packageName.equals("com.android.settings") ) {//排除这些应用
                LogHelper.e("infos","packageName========"+packageName);
                continue;
            }
           String havePath = APPIconPath + packageName + ".png";
            File localFile = new File(havePath);
            if(!localFile.exists()){
                LogHelper.e("infosss","localFile==="+localFile.getAbsolutePath());


            //得到应用的图标
            Drawable icon = Utils.getIconDrawableFromPackageName(packageName, mPackageManager);
            //将图标保存到本地,返回路径
            String iconPath = saveIcon(icon,packageName);



            //处理图片，放大，模糊，裁剪,并且保存在本地
//            if(!mIsPlayerApp){
                dealtIcon(icon,packageName);
//            }

            if(isBreakNew(mIconPath)){
                dealtIcon(icon,packageName);
                LogHelper.e("infos","=====time====损坏重新生成========");
            }
            isBreakNew(mIconPath);//再次出现，直接删除

            }
            DownloadResInfoSaveBusiness.getDownloadInfoData(new DownloadResInfoSaveBusiness.DownloadInfoCallback() {
                @Override
                public void downloadInfoCallback(TreeMap<Long, String> downloadInfoMap) {
                    if(downloadInfoMap.isEmpty()){
                        LogHelper.e("infos","infoMap.size===="+downloadInfoMap.size());
                        return;
                    }
                    Iterator it = downloadInfoMap.keySet().iterator();
                    while (it.hasNext()) {
                        String string = downloadInfoMap.get(it.next());
                        LogHelper.e("infos","====string==="+string);
//                        DownloadItem item = mGson.fromJson(string,DownloadItem.class);
                        try{
                            JSONObject item = new JSONObject(string);
                            LogHelper.e("infos","item.packageName=="+item.getString("package_name")+"==packageName=="+packageName);
                            if(item.getString("package_name").equals(packageName)){
                                mIsPlayerApp = true;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }

                }
            });


            LogHelper.e("infos","===sorTime===isPlayerApp===="+mIsPlayerApp);
            //            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
            //系统程序
            LogHelper.i("infos","--packageName-"+packageName);

            String path = APPIconPath + packageName + ".png";

            if (SETTINGPACKAGE.equals(packageName)) {
                settingItem = new PackageItem(packageName, appLabel,
                        activityName,path, flag,mIsPlayerApp);
                continue;
            } else if (packageName.equals(selfPackageName)) {
                continue;
            } else if (FILEMANAGERPACKAGE.equals(packageName)) {
                fileManagerItem = new PackageItem(packageName, appLabel,
                        activityName,path, flag,mIsPlayerApp);
                continue;
            }

            PackageItem item = new PackageItem(packageName, appLabel,
                    activityName,path, flag,mIsPlayerApp);
            //设置安装时间
            setInstallTime(packageManager,item);
            if (mojingApp.equals(packageName)) {
                baofengItem = item;
            } else {
                mInstalledAppList.add(item);
            }
        }
        LogHelper.i("infos","==============msg======0000000000000000========");
        Collections.sort(mInstalledAppList, new Comparator<PackageItem>() {
            @Override
            public int compare(PackageItem lhs, PackageItem rhs) {
//                    //已安装在前
//                    if (lhs.isSystemApp() != rhs.isSystemApp()) {
//                        return lhs.isSystemApp() ? 1 : -1;
//                    }
                return (int) ((lhs.getInstallTime() - rhs.getInstallTime()) / 1000);
            }
        });


        if (settingItem != null) {
            mInstalledAppList.add(0, settingItem);
        }
        if (fileManagerItem != null && mInstalledAppList.size() >= 2) {
            mInstalledAppList.add(1, fileManagerItem);
        }
        if (baofengItem != null && mInstalledAppList.size() >= 3) {
            mInstalledAppList.add(2, baofengItem);
        } else if (baofengItem != null) {
            mInstalledAppList.add(baofengItem);
        }
        LogHelper.i("infos","==============msg======11111111111111========");
        if (Utils.isLoadAllAppOnlyString && listCallback != null) {
            LogHelper.i("infos","==============msg======22222222222========");
            String msg = parseListToJson(mInstalledAppList);
            LogHelper.i("infos","==============msg======33333333333========");
            LogHelper.i("infos","----msg"+msg);
            //返回应用程序列表数据的回调
            listCallback.onPackageListResult(msg);
        }
    }

    /**
     * 过滤出系统安装的应用
     * @param flags
     * @return
     */
    private static boolean filterApp(int flags) {
        if ((flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return true;
        }
        return false;
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
                Object packageManagerService = Utils.getPackageManagerService();
                Class<?> pmService = packageManagerService.getClass();
                Class<?>[] paramTypes1 = Utils.getParamTypes(pmService, "deletePackage");
                Method deletePackage = pmService.getMethod("deletePackage", paramTypes1);
                //deletePackage方法的userid参数
                Class<?> userHandle = UserHandle.class;
                Class<?>[] myUserIds = Utils.getParamTypes(userHandle, "myUserId");
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

    /**==========对外接口================================*/
    /**
     * 获取AppPackageManager对象
     *
     * @return 返回一个AppPackageManager实例
     */
    public static AppPackageManager getAndroidManager() {
        return getAppPackageManager(BaseApplication.INSTANCE);
    }

    private String parseListToJson(List<PackageItem> mInstalledAppList) {
        AppListJsonBean bean = new AppListJsonBean();
        if (mInstalledAppList != null) {
            bean.appCount = mInstalledAppList.size();
            bean.appList = mInstalledAppList;
        }
        return mGson.toJson(bean);
    }

    /**
     * 获取应用列表
     * 因为异步获取所以有回调
     *
     * @param callback 回调接口
     */
    public void getInstalledAppList(final PackageListCallback callback) {
//        if (mInstalledAppList.size() > 0) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String msg = parseListToJson(mInstalledAppList);
//                    if (callback != null) {
//                        callback.onPackageListResult(msg);
//                    }
//                }
//            }).start();
//            return;
//        }
//        if (mThread == null) {
//        }
        LogHelper.e("infos","=========getInstalledAppList========");
        //创建应用图标文件夹
        createFolder();
        //获取应用列表
        mThread = new AppListThread();
        mThread.callback = callback;
        mThread.start();

    }

    /**
     * 创建文件夹
     */
    private void createFolder(){

        String rootPath = getRootPath();

        if(!"".equals(rootPath)) {
            APPIconPath = rootPath + "/APPIcon/";
            File BannerFile = new File(APPIconPath);
            if(!BannerFile.exists()) {
                BannerFile.mkdirs();
                LogHelper.i(TAG, "-------createFolder创建文件夹---->" + APPIconPath );
            }
        }

    }

    /**
     * 获取根目录
     * @return 绝对路径 没有或不可用为""
     */
    public String getRootPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "";
        }
    }

    /**
     * 删除文件夹里的内容
     */
    private void DeleteFile(String path) {
        if(TextUtils.isEmpty(path)) {
            LogHelper.i(TAG, "-----------> 文件夹不存在");
            return;
        }
        File file = new File(path);
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i++) {
            File file1 = files[i];
            if(file1.isFile()) {
                file1.delete();
            }
            LogHelper.i(TAG, "-----------> 删除文件成功");
        }
    }

    private String saveIcon(Drawable drawable,String packageName){

        Bitmap toBitmap = drawableToBitmap(drawable);

        //给图片设置圆角
        Bitmap bitmap = toRoundCorner(toBitmap, 22);

        //将图片保存到本地
        String path = saveImg(bitmap, packageName,".png");
        LogHelper.i("infos","path==="+path+"===bitmap==="+bitmap);
        return path;
    }

    /**
     *
     * @param bitmap
     * @param packageName
     * @param str 路径后缀
     * @return
     */
    private String saveImg(Bitmap bitmap,String packageName,String str){
        if(TextUtils.isEmpty(APPIconPath)){
            createFolder();
        }
        File f = new File(APPIconPath, packageName + str);
        String iconPath = APPIconPath + packageName + str;

        if (f.exists()) {
//            f.delete();
            LogHelper.i(TAG,"---------------------->saveIcon--文件已存在" + iconPath);
            return iconPath;
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            LogHelper.i(TAG, "---------------------->saveIcon--保存成功" + iconPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return iconPath;
    }
    //处理图片
    private String dealtIcon(Drawable drawable,String packageName){

        //把Drawable 转换成Bitmap
        Bitmap bitmap = drawableToBitmap(drawable);
        //放大图片
        Bitmap big = big(bitmap,4.1f,4.1f);
        //裁剪图片
        int height = big.getHeight();
        int width = big.getWidth();
        Bitmap big1 = null;
        if(width < 336) {
            float m = 336*2/width;
            big1 = big(big, m, m);
            height = big1.getHeight();
            width = big1.getWidth();
            big = big1;
        }
        int x = (int)(width - 336)/2;
        int y = (int)(height - 189)/2;
        LogHelper.i(TAG,"---------------------->dealtIcon" + "x=" + x +"y="+ y+"==height=="+height+"===width==="+width);
        Bitmap bitmap1 = Bitmap.createBitmap(big, x, y, 336, 189, null, true);
        LogHelper.e("infosss","111111111111111111111111");
        //图片模糊
//        Bitmap bitmap2 = renderBlurBitmap(bitmap1);
        LogHelper.e("infosss","22222222222222222222");
        //给图片设置遮罩
        Bitmap blackImage = getBlackImage(bitmap1);
        LogHelper.e("infosss","33333333333333333333333");
        Bitmap testBmp = Bitmap.createBitmap(blackImage, 0, 0, 332, 185, null, true);
        LogHelper.e("infosss","4444444444444444");
        Bitmap tempBmp = Bitmap.createBitmap(blackImage,0,0,blackImage.getWidth(),blackImage.getHeight(),null,true);
        LogHelper.e("infosss","5555555555555555");
        Paint vPaint = new Paint();
        vPaint.setAntiAlias(true);
        vPaint .setAlpha(113);
        Canvas testC= new Canvas();
        testC.drawBitmap ( tempBmp , tempBmp.getWidth(), tempBmp.getHeight(), vPaint );  //有透明
        LogHelper.e("infosss","66666666666666666666");

        RectF dRect = new RectF(2, 2, tempBmp.getWidth() - 2, tempBmp.getHeight() - 2);
        RectF sRect = new RectF(0, 0, testBmp.getWidth(), testBmp.getHeight());
        LogHelper.e("infosss","77777777777777777777");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(tempBmp);
        canvas.drawBitmap(testBmp,null,dRect,paint);

        LogHelper.e("infosss","888888888888888");
        //将图片保存到本地
        String imgpath = saveImg(tempBmp, packageName, "bj.png");
        LogHelper.e("infosss","9999999999999999999");
        mIconPath = imgpath;
        LogHelper.i("infos","imgPath=="+imgpath);
        return imgpath;
    }


    public Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

    //设置亮度
    public Bitmap Brightness(Bitmap map,int b){//b的值正数变亮，负数变暗

        int imgHeight = map.getHeight();
        int imgWidth = map.getWidth();
        Bitmap bmp = Bitmap.createBitmap(imgWidth, imgHeight,
                Bitmap.Config.ARGB_8888);
        int brightness = b;
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(map, 0, 0, paint);
        return bmp ;
    }


    /**
     * 设置透明度
     * @param sourceImg
     * @param number 0 标示完全透明  0到100
     * @return
     */
    public static Bitmap getAlplaBitmap(Bitmap sourceImg, int number) {


        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;

    }

    //RenderScript 实现图片模糊
    public Bitmap renderBlurBitmap(Bitmap bitmap){
        LogHelper.e("infosss","=======renderBlurBitmap1111111111111111==========");
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        LogHelper.e("infosss","=======renderBlurBitmap222222222222222222==========");

        RenderScript rs = RenderScript.create(mContext);
        LogHelper.e("infosss","=======renderBlurBitmap33333333333==========");

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        LogHelper.e("infosss","=======renderBlurBitmap4444444444==========");

        Allocation allIn = Allocation.createFromBitmap(rs,bitmap);
        LogHelper.e("infosss","=======renderBlurBitmap555555555555==========");

        Allocation allOut = Allocation.createFromBitmap(rs,outBitmap);
        LogHelper.e("infosss","=======renderBlurBitmap6666666666666666==========");

        blurScript.setRadius(25.f);
        LogHelper.e("infosss","=======renderBlurBitmap7777777777777777==========");

        blurScript.setInput(allIn);
        LogHelper.e("infosss","=======renderBlurBitmap888888888888888==========");

        blurScript.forEach(allOut);
        LogHelper.e("infosss","=======renderBlurBitmap99999999999999999==========");

        allOut.copyTo(outBitmap);
        LogHelper.e("infosss","=======renderBlurBitmap00000000000000000000000000==========");

        rs.destroy();
        LogHelper.e("infosss","=======renderBlurBitmap102020202020222020202020202==========");

        return outBitmap;
    }

    //放大图片
    private  Bitmap big(Bitmap bitmap,float x,float y) {
        Matrix matrix = new Matrix();
        matrix.postScale(x,y); //长和宽放大缩小的比例+-
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    //设置遮罩颜色
    public  Bitmap getBlackImage(Bitmap bitmap){
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bmp);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,0,0,paint);
        canvas.drawColor(Color.parseColor("#D2000000"));
        return bmp;
    }

    /**
     * 给图片设置圆角
     * @param bitmap
     * @param pixels 圆角值
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
//        if (bitmap != null && !bitmap.isRecycled()) {
//            bitmap.recycle();
//        }
        return output;
    }

    //把Drawable 转换成Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable){
        //把Drawable 转换成Bitmap
        Bitmap bitmap;
        byte[] size = null;
        //将drawable转换成bitmap
        if (drawable instanceof BitmapDrawable){
            bitmap =((BitmapDrawable) drawable).getBitmap();
        } else {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();

            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            bitmap = Bitmap.createBitmap(w, h, config);
            Canvas canvas = new Canvas(bitmap);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
        }

        return bitmap;
    }

    /**
     * 注册一个App安装、更新、卸载的回调事件
     *
     * @param callback 状态回调
     */
    public void registerAppChangeCallback(PackageChangeCallback callback) {
        this.callback = callback;
    }

    /**
     * 取消App状态监听
     */
    public void unregisterAppChangeCallback() {
        callback = null;
    }

    /**
     * 获取应用图标
     *
     * @param name 包名
     */
    public IconData getAppIconFromPackageName(String name) {
        return mIconCache.getIconData(name);
    }



    public void unInstallPackage(String packageName
                                ) {
//        AppPackageChangeObserver appPackageChangeObserver = new AppPackageChangeObserver(
//                callback);
//        IPackageDeleteObserver iPackageDeleteObserver=new IPackageDeleteObserver();
//        this.mPackageManager
//                .deletePackage(
//                        packageName,
//                        (IPackageDeleteObserver) appPackageChangeObserver.deleteObserver,
//                        0);
//         observer=new PackageDeleteObserver();
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

    /**
     * 设置安装时间
     * @param pm
     * @param pkg
     * @return
     */
    private  void setInstallTime(PackageManager pm, PackageItem pkg){
        try {
            PackageInfo applicationInfo= pm.getPackageInfo(pkg.getPackageName(),0);
            if (applicationInfo!=null)
                pkg.setInstallTime(applicationInfo.firstInstallTime);

        }
        catch (PackageManager.NameNotFoundException e) {
            pkg.setInstallTime(1482501147000L);
            e.printStackTrace();
        }


    }
    public String getCurrentTime(){
        SharedPreferences sharedPre = mContext.getSharedPreferences("sharedTime",
                Context.MODE_PRIVATE);
        String sharedTime = sharedPre.getString("time", "");
        return sharedTime;
    }
    /**
     * 启动其它应用
     *
     * @param packageName 包名
     * @param className   类名
     */
    public void startActivity(String packageName, String className) {
        if (packageName != null && className != null) {
            Intent launchIntent = new Intent();
            launchIntent
                    .setComponent(new ComponentName(packageName, className));
            if (packageName.equals(mojingApp)) {
                launchIntent.putExtra(GlobIntentkey.MOJING_USER, UserManager.getInstance()
                        .getCurrentUser());
            }
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            try {
//                addAnim(launchIntent);

            }
            catch (RuntimeException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * 通过packagename启动应用
     * @param
     * @param packagename
     * */
    public  void startAppFromPackageName(String packagename){
        Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
        Intent intent = isexit(currrentActivity,packagename);
        if(intent != null){
            if (packagename.equals(mojingApp)) {
                intent.putExtra(GlobIntentkey.MOJING_USER, UserManager.getInstance()
                        .getCurrentUser());
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//            currrentActivity.startActivity(intent);
            try {
                addAnim(intent,currrentActivity);
            }catch (RuntimeException e) {
                e.printStackTrace();

            }
        }

    }

    /**
     * 通过packagename判断应用是否安装
     * @param context
     * @param pk_name
     *
     * @return 跳转的应用主activity Intent
     * */

    private  Intent isexit(Context context,String pk_name){
        if (pk_name.equals("com.BaoFeng.Elysium")
                || pk_name.equals("com.bfmj.argamedyzmhandle")
                || pk_name.equals("com.bfmj.argamedyzmhead")) {
            return new Intent(pk_name + ".LandScape");
        }
        return BaseApplication.INSTANCE.getPackageManager().getLaunchIntentForPackage(pk_name);
    }


    /**
     * 添加过场动画
     */
    private void addAnim(final Intent launchIntent,final Activity activity ) {
//        final Activity activity = UnityPlayer.currentActivity;
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.startActivity(launchIntent);
                    activity.overridePendingTransition(GetResourceUtil.getAnimId
                            (mContext, "fade_in_fast"), GetResourceUtil.getAnimId(mContext, "fade_out_fast"));
//                    activity.overridePendingTransition(GetResourceUtil.getAnimId
//                            (mContext, ""), GetResourceUtil.getAnimId(mContext, ""));
                }
            });

        }
    }

    public void startChrome(){
        Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
        Intent intent = new Intent();
        intent.setClassName("com.android.chrome",
                "com.google.android.apps.chrome.Main");
        intent.setData(Uri.parse("http://dl.mojing.cn/matrix/index.html"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        try {
            addAnim(intent,currrentActivity);
        }catch (RuntimeException e) {
            e.printStackTrace();

        }
    }

    public String getAllInstall(){
        List<PackageItem> installApp = new ArrayList<>();
        List<DownloadItem> list = DownloadUtils.getInstance().getAllDownLoadsByState(BaseApplication.INSTANCE, MjDownloadStatus.COMPLETE,true);
        if(!list.isEmpty()){
           for(DownloadItem in : list){
               if(null != in && in.getDownloadType() == ResTypeUtil.res_type_game
                       && in.getDownloadState() == MjDownloadStatus.COMPLETE
                       && isAppInstalled(in.getPackageName())){
                   //得到应用的图标
                   Drawable icon = Utils.getIconDrawableFromPackageName(in.getPackageName(), mPackageManager);
                   //将图标保存到本地,返回路径
                   String iconPath = saveAppIcon(icon,in.getPackageName());
                   PackageItem item = Utils.getPackageItemFromPackageName(
                           in.getPackageName(), mPackageManager,true);
                   if(null != item){
                       item.setmAppIcon(iconPath);
                   }
                   installApp.add(item);
               }
           }

        }
        return parseListToJson(installApp);
    }

    private boolean isAppInstalled(String packagename)
    {
        PackageInfo packageInfo;
        try {
            packageInfo = BaseApplication.INSTANCE.getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo ==null){
            return false;
        }else{
            return true;
        }
    }

    private String saveAppIcon(Drawable drawable,String packageName){

        Bitmap toBitmap = drawableToBitmap(drawable);

        //将图片保存到本地
        String path = saveImg(toBitmap, packageName,".png");
        LogHelper.i("infos","path==="+path+"===bitmap==="+toBitmap);
        return path;
    }
}
