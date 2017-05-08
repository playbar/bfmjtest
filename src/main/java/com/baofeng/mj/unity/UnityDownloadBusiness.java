package com.baofeng.mj.unity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoSaveBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.activity.BasePlayerActivity;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.threadutil.LocalDownloadProxy;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author liuchuanchi
 * @description: Unity下载业务类
 */
public class UnityDownloadBusiness {
    public static final int REASON = 0x1000;
    /**
     * 开始下载
     */
    public static void startDownload(String json){
        LogHelper.e("infos","startDownload.json=="+json);
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(downloadItem.getAid());//获取正在下载的DownloadItem
        if(downloadingItem != null){
            downloadItem.setCreateTime(downloadingItem.getCreateTime());
        }
        if(downloadingItem == null) {//没有下载过（是开始下载，不是继续下载）
            downloadItem.setCreateTime(System.currentTimeMillis());
            String baseInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading, downloadItem.getTitle(),downloadItem.getAid());
            FileCommonUtil.writeFileString(json, baseInfoPath);//资源信息保存到正在下载文件夹
        }
        LogHelper.e("infosss","createtime====="+downloadItem.getCreateTime());
        DemoUtils.startDownload(BaseApplication.INSTANCE, downloadItem);//开始下载

    }

    /**
     * 暂停下载
     */
    public static void pauseDownload(String json){
        LogHelper.e("infos","pause.json=="+json);
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
        DemoUtils.unityPauseDownload(BaseApplication.INSTANCE, downloadItem);//暂停下载

    }

    /**
     * 删除下载
     */
    public static void deleteDownload(String json){
        LogHelper.e("infossss","==========deleteDownload============");
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
        DownLoadBusiness.deleteDownload(downloadItem, null);
        if(null != UnityActivity.INSTANCE){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                JSONObject downloadJSONObject = DownloadItemUtil.createDeletedJSONObject(downloadItem);
                iAndroidCallback.sendDownloadDeleted(downloadJSONObject.toString());
            }
        }
    }

    /**
     * 获取已下载资源信息数据
     */
    public static void getDownloadInfoData(){
        List<DownloadItem> list = new ArrayList<>();
        list.addAll(DownloadUtils.getInstance().getAllDownLoadsByState(BaseApplication.INSTANCE,MjDownloadStatus.COMPLETE,true));
        if(null != UnityActivity.INSTANCE){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                JSONArray downloadJSONArray = DownloadItemUtil.createDownloadedJSONArray(list);
                if (downloadJSONArray.length() == 0) {
                    iAndroidCallback.sendDownloadedJSONArray("");
                } else {
                    iAndroidCallback.sendDownloadedJSONArray(downloadJSONArray.toString());
                    LogHelper.e("infos","downloadInfoData==="+downloadJSONArray.toString());
                }
            }
        }
    }

    /**
     * 更新已下载
     */
    public static void updateDownloaded(DownloadItem downloadItem){
        LogHelper.e("infosss","=======updateDownloaded==111111111111111========"+downloadItem.getDownloadType());
//        if(downloadItem.getDownloadType()== 7){//如果是游戏，静默安装
//            UnityGameBusiness.installSilent(downloadItem.getPackageName(),downloadItem.getAid(),downloadItem.getDownloadType(),downloadItem.getTitle());
//        }
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                JSONObject downloadJSONObject = DownloadItemUtil.createDownloadCompletedJSONObject(downloadItem);
                iAndroidCallback.sendDownloadCompleted(downloadJSONObject.toString());
                LogHelper.e("infosss","=======updateDownloaded==22222222222========");
            }
        }

    }

    /**
     * 更新正在下载
     */
    public static void updateDownloading(int downloadingSize,List<DownloadItem> downloadingList){
        if(null != UnityActivity.INSTANCE){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                if(downloadingSize == 0){
                    iAndroidCallback.sendDownloadingJSONArray("");
                }else{
                    JSONArray downloadJSONArray = DownloadItemUtil.createDownloadingJSONArray(downloadingList);
                    if(downloadJSONArray.length() == 0){
                        iAndroidCallback.sendDownloadingJSONArray("");
                    }else{
                        iAndroidCallback.sendDownloadingJSONArray(downloadJSONArray.toString());
                        LogHelper.e("infosssss","sendDownloadingJSONArray====="+downloadJSONArray.toString());
                    }
                }
            }
        }

    }

   public static void stopAll(){
        LogHelper.e("px","==========stopAll============");
        MjDownloadSDK.stopAll(BaseApplication.INSTANCE);
        DownloadUtils.getInstance().mIsInit = false;

    }


    public static void startAll(){
        LogHelper.e("px","------------startAll--------------");
        DownloadUtils.getInstance().mIsInit = true;
        BaseApplication.INSTANCE.initDownloadInfo();
        DownloadUtils.getInstance().startAllDownload(BaseApplication.INSTANCE);

    }


    public static void getAllDownload(){
        List<DownloadItem> list = DownloadUtils.getInstance().getAllDownLoadsByState(BaseApplication.INSTANCE,MjDownloadStatus.COMPLETE,false);
        updateDownloading(list.size(),list);

    }

    /**
     * 获取正在下载集合
     */
    public static String getDownloadingList(){
        List<DownloadItem> downloadingList = BaseApplication.INSTANCE.getDownloadingList();
        JSONArray downloadJSONArray = DownloadItemUtil.createDownloadingJSONArray(downloadingList);
        if(downloadJSONArray.length() == 0){
            return "";
        }else{
            return downloadJSONArray.toString();
        }
    }


    public static void startAllInstall(){
        List<DownloadItem> list = DownloadUtils.getInstance().getAllDownLoadsByState(BaseApplication.INSTANCE,MjDownloadStatus.COMPLETE,true);
        DownloadItem downloadItem ;
        for(int i = 0;i < list.size();i++){
            downloadItem = list.get(i);
            if(downloadItem.getDownloadType() == 7 && !BaseApplication.INSTANCE.isAppInstalled(downloadItem.getPackageName())){
                UnityGameBusiness.installSilent(downloadItem.getPackageName(),downloadItem.getAid(),downloadItem.getDownloadType(),downloadItem.getTitle());
            }
        }
    }
    /**
     * 获取下载状态
     * @param resType 资源类型
     * @param resId 资源id
     * @param resTitle 资源标题
     * @param downloadUrl 下载url
     * @return 0未下载，2，正在下载，4已下载
     */
    public static int getDownloadState(int resType, String resId, String resTitle, String downloadUrl){
        LogHelper.e("infos","resType==="+resType+"==resId="+resId+"==resTitle=="+resTitle);
        DownloadItem downloadItem = BaseApplication.INSTANCE.getDownloadItem(resId);//获取正在下载的DownloadItem
        if(downloadItem != null){
            LogHelper.e("infossss","===111111111111=============="+resId+"==resTitle=="+resTitle);
            return downloadItem.getDownloadState();//正在下载
        }
        File file = DownloadResBusiness.getDownloadResFile(resType, resId, resTitle, downloadUrl);
        if(file.exists()){//资源存在
            if(ResTypeUtil.res_type_roaming == resType){//全景漫游
                if(file.getName().endsWith(".zip")){//当前文件是zip包（全景漫游下载完成，但是还没解压）
                    LogHelper.e("infossss","===2222222222=============="+resId+"==resTitle=="+resTitle);
                    return MjDownloadStatus.DOWNLOADING;//正在下载
                }
            }
            LogHelper.e("infossss","===333333333=============="+resId+"==resTitle=="+resTitle);
            return MjDownloadStatus.COMPLETE;//已下载
        }else{//资源不存在
            file = new File(DownloadResInfoBusiness.getDownloadResInfoFilePath(resType, resTitle, resId));
            if(file.exists()){
                file.delete();//删除资源信息
            }
            LogHelper.e("infossss","===44444444444=============="+resId+"==resTitle=="+resTitle);
            return MjDownloadStatus.DEFAULT;//未下载
        }
    }

    /**
     * 获取下载状态（游戏用）
     * @param resType 资源类型
     * @param resTitle 资源标题
     * @param downloadUrl 下载url
     * @param packageName apk包名
     * @param versionCode apk版本号
     *
     *
     *
     *
     */
    public static int getGameDownloadState(int resType, String resId, String resTitle, String downloadUrl, String packageName, int versionCode) {
        DownloadItem downloadItem = BaseApplication.INSTANCE.getDownloadItemForGame(packageName);//获取正在下载的DownloadItem
        LogHelper.e("checkApk","======getGameDownloadState=========="+resTitle+"==item=="+downloadItem);
        if(downloadItem != null ){
            return downloadItem.getDownloadState();//正在下载
        }
        File file = DownloadResBusiness.getDownloadResFile(resType, resId, resTitle, downloadUrl);
        return ApkUtil.checkApk(file, packageName, versionCode);
    }

    /**
     * 获取下载文件的路径
     * @param resType 资源类型
     * @param resId 资源id
     * @param resTitle 资源标题
     * @param downloadUrl 下载url
     */
    public static String getDownloadFilePath(int resType, String resId, String resTitle, String downloadUrl){
        File file = DownloadResBusiness.getDownloadResFile(resType, resId, resTitle, downloadUrl);
        if(file.exists()){
            return file.getAbsolutePath();
        }else{
            return "";
        }
    }

    /**
     * 获取下载文件夹的路径
     * @param resType 资源类型
     */
    public static String getDownloadFolderPath(int resType){
        return DownloadResBusiness.getDownloadResFolder(resType);
    }


}
