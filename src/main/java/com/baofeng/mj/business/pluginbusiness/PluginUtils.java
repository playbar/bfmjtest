package com.baofeng.mj.business.pluginbusiness;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.RemoteException;

import com.baofeng.mj.bean.ApkItem;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.morgoo.droidplugin.pm.PluginManager;
import com.sina.weibo.sdk.api.share.Base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghongfang on 2017/3/1.
 */
public class PluginUtils {

    public static  ArrayList<ApkItem> getInstalledApk(){
        ArrayList<ApkItem> list = new ArrayList<ApkItem>();
        try {
            final List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
            final PackageManager pm = BaseApplication.getInstance().getPackageManager();
            for (final PackageInfo info : infos) {
                ApkItem item = new ApkItem(pm, info, info.applicationInfo.publicSourceDir);
                list.add(item);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void searchPluginApk(final IPluginSearchListener callback){
        ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                String filePath = DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_plugin);
                File file = new File(filePath);
                List<File> apks = new ArrayList<File>(10);
                File[] files = file.listFiles();
                if (files != null) {
                    for (File apk : files) {
                        if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                            apks.add(apk);
                        }
                    }
                }

//                file = new File(Environment.getExternalStorageDirectory(), "360Download");
//                if (file.exists() && file.isDirectory()) {
//                    File[] files1 = file.listFiles();
//                    if (files1 != null) {
//                        for (File apk : files1) {
//                            if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
//                                apks.add(apk);
//                            }
//                        }
//                    }
//
//                }
                ArrayList<ApkItem> list = new ArrayList<ApkItem>();
                PackageManager pm = BaseApplication.getInstance().getPackageManager();
                for (final File apk : apks) {
                    try {
                        if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                            final PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
                            if (info != null) {
                                try {
                                    ApkItem item = new ApkItem(BaseApplication.getInstance(), info, apk.getPath());
                                    list.add(item);
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                if(callback!=null){
                    callback.onSearchResult(list);
                }
            }
        });
    }

    public interface IPluginSearchListener{
        void onSearchResult(ArrayList<ApkItem> apkItems);
    }

    public interface IPluginInstallListener{
        void onInstallResult(int result);
    }
}
