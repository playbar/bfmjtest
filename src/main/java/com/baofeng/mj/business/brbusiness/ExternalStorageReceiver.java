package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.fileutil.FileStorageUtil;

/**
 * Created by liuchuanchi on 2016/6/23.
 * 外部存储广播（sdcard存储，usb存储）
 */
public class ExternalStorageReceiver extends BroadcastReceiver {
    private ExternalStorageNotify externalStorageNotify;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || TextUtils.isEmpty(intent.getAction())){
            return;
        }
        if(intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){//外存储插入
            notify(true);//通知
        }else if(intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL) ||
                intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)){//外存储移除
            notify(false);//通知
            changeDownloadDir();//改变下载存储路径
        }
    }

    /**
     * 改变下载存储路径
     */
    public static void changeDownloadDir(){
        if(FileStorageUtil.getStorageMode() > 0){//当前下载存储模式是外部存储
            FileStorageUtil.setStorageMode(0);//设置下载存储模式为内部存储
            String dirPath = FileStorageUtil.getExternalMojingDir() + "download/";
            FileStorageUtil.mkdir(dirPath);
            FileStorageUtil.setStorageDir(dirPath);//设置下载存储路径
            FileStorageUtil.resetDownloadDir();//重置路径
            DownloadResBusiness.resetPath();//重置路径
        }
    }

    /**
     * 通知
     */
    private void notify(boolean status){
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null) {//通知Unity
                iAndroidCallback.sendUsbDeviceState(status);
            }
        }
        if(externalStorageNotify != null){
            externalStorageNotify.externalStorageState(status);
        }
    }

    public void addExternalStorageNotify(ExternalStorageNotify externalStorageNotify){
        this.externalStorageNotify = externalStorageNotify;
    }

    public void removeExternalStorageNotify(){
        this.externalStorageNotify = null;
    }

    public interface ExternalStorageNotify{
        void externalStorageState(boolean status);//true：插入，false：移除
    }
}
