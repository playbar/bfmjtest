package com.baofeng.mj.unity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.utils.PackageUtils;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;

/**
 * @author liuchuanchi
 * @description: Unity游戏业务类
 */
public class UnityGameBusiness {
    /**
     * apk是否安装
     * @param packageName 包名
     * @return true已安装，false未安装
     */
    public static boolean apkHasInstalled(String packageName){
        return ApkUtil.apkHasInstalled(packageName);
    }

    /**
     * 安装apk
     * @param apkPath apk路径
     */
    public static void installApk(String apkPath) {
        ApkUtil.installApk(UnityActivity.INSTANCE, apkPath);
    }

    /**
     * 打开apk
     * @param packageName 包名
     */
    public static void startPlayApk(String packageName){
        ApkUtil.startPlayApk(UnityActivity.INSTANCE, packageName);
    }

    /**
     * 静默安装
     * @param resId 资源id
     * @param resType 类型
     * @param resTitle 应用名称
     */
    public static void installSilent(final String packageName,final String resId,final int resType,final String resTitle){
        LogHelper.e("installapp","======installSilent==========");
        ApkUtil.installSilentApk(packageName, resType, resId, resTitle);
    }

    /**
     * 静默卸载
     * @param packageName 包名
     */
    public static void uninstallSilent(final String packageName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = PackageUtils.uninstall(UnityActivity.INSTANCE, packageName);
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//卸载完成通知Unity
                        iAndroidCallback.sendApkUninstallResult(packageName, status);
                    }
                }
            }
        }).run();

    }

    /**
     * 删除apk文件
     * @param resType 资源类型
     * @param resId 资源id
     * @param resTitle 资源标题
     */
    public static void deleteApkFile(int resType, String resId, String resTitle){
       /* File file = DownloadResBusiness.getApkFile(resType, resId, resTitle);
        if(file.exists()){
            file.delete();
        }*/

        deleteAllFile(resType, resId, resTitle);
    }

    public static void deleteAllFile(int resType, String resId, String resTitle){
        String folder = DownloadResBusiness.getDownloadResFolder(resType);
        File file = new File(folder, resId + ".zip");
        if(file.exists()){
           file.delete();
        }
        file = new File(folder, resTitle + ".zip");
        if(file.exists()){
           file.delete();
        }
        file = new File(folder, resId + ".apk");
        if(file.exists()){
            file.delete();
        }

        file = new File(folder,resId);
        if(file.exists() && file.isDirectory()){
//            deleteAllFilesOfDir(file);
            LogHelper.e("infossss","====file.isDirectory=====");
        }
    }

    public static void deleteZipFile(int resType, String resId, String resTitle){
        String folder = DownloadResBusiness.getDownloadResFolder(resType);
        File file = new File(folder, resId + ".apk");
        if(file.exists()){
            file.delete();
        }

        file = new File(folder,resId);
        if(file.exists() && file.isDirectory()){
            deleteAllFilesOfDir(file);
        }
    }


    private static void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }

    /**
     * 打开小花秀直播
     */
    public static void openXiaoHuaXiu(String roomId){
        Intent intent = new Intent("rgbvr.intent.action.OPEN");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("roomId",roomId);
        UnityActivity.INSTANCE.startActivity(intent);
    }

    /**
     * 根据gameid启动游戏
     * @param gameId
     */
    public static void startGameForId(String gameId){
        final Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
        LogHelper.e("infossss","=====gameId==="+gameId+"===currentActivity==="+currrentActivity);
        if(null != currrentActivity){
            LogHelper.e("infossss","==currrentActivity==="+currrentActivity);
            currrentActivity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("xiaoji://"+gameId)));
        }

    }

    public static String getGameMethodImagePath(String packageName){
        return ApkUtil.getGameMethodImagePath(packageName);
    }

    public static String getRectIconImagePath(String packageName){
        LogHelper.e("RectIcon","path==="+packageName);
        return ApkUtil.getRectIconImagePath(packageName);
    }
}
