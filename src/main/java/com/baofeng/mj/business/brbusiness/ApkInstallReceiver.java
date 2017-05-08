package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by liuchuanchi on 2016/6/23.
 * apk安装，卸载等广播
 */
public class ApkInstallReceiver extends BroadcastReceiver {
    public static ArrayList<ApkInstallNotify> apkInstallNotifyList = new ArrayList<ApkInstallNotify>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getAction() == null){
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            if(intent.getData() == null){
                return;
            }
            String packageName = intent.getData().getSchemeSpecificPart();
            for(int i = apkInstallNotifyList.size() - 1; i >= 0; i--){
                ApkInstallNotify apkInstallNotify = apkInstallNotifyList.get(i);
                if(apkInstallNotify == null){
                    apkInstallNotifyList.remove(i);
                }else{
                    apkInstallNotify.installNotify(packageName);
                }
            }
        }else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
        }
    }

    public static void addApkInstallNotify(ApkInstallNotify apkInstallNotify){
        if(apkInstallNotify == null || apkInstallNotifyList.contains(apkInstallNotify)){
            return;
        }
        apkInstallNotifyList.add(apkInstallNotify);
    }

    public static void removeApkInstallNotify(ApkInstallNotify apkInstallNotify){
        if(apkInstallNotify == null){
            return;
        }
        apkInstallNotifyList.remove(apkInstallNotify);
    }

    public interface ApkInstallNotify {
        void installNotify(String packageName);
    }
}
