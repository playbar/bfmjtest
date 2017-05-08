package com.baofeng.mj.util.entityutil;

import android.text.TextUtils;

import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.OTABean;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.downloadutil.DownLoadItemUtils;
import com.baofeng.mj.business.firewarebusiness.FirewareBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.mojing.dl.domain.DownloadItem;
import com.mojing.dl.utils.DownloadConstant;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liuchuanchi on 2016/5/21.
 * 创建下载(实体类)工具类
 */
public class DownloadItemUtil {
    //暴风魔镜下载
    public static final int DOWNLOAD_TYPE_MJ = -1;//下载资源类型
    public static final String DOWNLOAD_ID_MJ = "-1";//下载资源id
    public static final String DOWNLOAD_TITLE_MJ = "暴风魔镜";//下载资源title
    //暴风魔镜OTA下载
    public static final int DOWNLOAD_TYPE_OTA = -2; //下载资源类型
    public static final String DOWNLOAD_ID_OTA = "-2";//下载资源id
    public static final String DOWNLOAD_TITLE_OTA = "暴风魔镜OTA";//下载资源title
    // 固件MCU下载
    public static final int DOWNLOAD_TYPE_FRIEWARE_MCU = -3;
    public static final String DOWNLOAD_ID_FRIEWARE_MCU = "-3";
    public static final String DOWNLOAD_TITLE_FRIEWARE_MCU = "Fireware_MCU.bin";
    // 固件BLE下载
    public static final int DOWNLOAD_TYPE_FRIEWARE_BLE = -4;
    public static final String DOWNLOAD_ID_FRIEWARE_BLE = "-4";
    public static final String DOWNLOAD_TITLE_FRIEWARE_BLE = "Fireware_BLE.bin";

    /**
     * 创建下载中的JSONArray
     */
    public static JSONArray createDownloadingJSONArray(List<DownloadItem> downloadItemList) {
        JSONArray downloadJSONArray = new JSONArray();
        List<DownloadItem> list = new ArrayList<>();
        list.addAll(downloadItemList);
        DownloadUtils.getInstance().sortCreateTimeByDownLoadItems(list);
        for(DownloadItem downloadItem : list){
            if(downloadItem == null){
                continue;
            }
            LogHelper.e("infosssss","title=="+downloadItem.getTitle()+"===createTime==="+downloadItem.getCreateTime());
            JSONObject downloadJSONObject = new JSONObject();
            try {
//                downloadJSONObject.put("id",downloadItem.getId());//下载id
                downloadJSONObject.put("type", downloadItem.getDownloadType());//资源类型
                downloadJSONObject.put("res_id", downloadItem.getAid());//资源id
                downloadJSONObject.put("title", downloadItem.getTitle());//资源标题
                downloadJSONObject.put("icon_url", downloadItem.getImageUrl());//图标地址
                downloadJSONObject.put("size", FileSizeUtil.formatFileSize(downloadItem.getTotalLen()));//资源总大小
                downloadJSONObject.put("current_size", FileSizeUtil.formatFileSize(downloadItem.getTotalLen() *  downloadItem.getProgress() / 100));//资源当前下载大小
                downloadJSONObject.put("download_progress", DownLoadBusiness.getDownloadProgress(downloadItem));//当前下载进度
                downloadJSONObject.put("download_status", downloadItem.getDownloadState());//下载状态
                downloadJSONObject.put("download_url", downloadItem.getHttpUrl());//资源下载地址
                downloadJSONObject.put("package_name", downloadItem.getPackageName());//apk包名
//                downloadJSONObject.put("download_error_count",downloadItem.getDownloadErrorCount());
//                LogHelper.e("infosss","download_error_count===="+downloadItem.getDownloadErrorCount());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            downloadJSONArray.put(downloadJSONObject);
        }
        return downloadJSONArray;
    }
    /**
     * 创建已完成的JSONArray
     */
    public static JSONArray createDownloadedJSONArray(List<DownloadItem> downloadItemList) {
        JSONArray downloadJSONArray = new JSONArray();
        List<DownloadItem> list = new ArrayList<>();
        list.addAll(downloadItemList);
        if(list.size() >= 2){
            DownloadUtils.getInstance().sortFinishTimeByDownLoadItems(list);
        }

        for(DownloadItem downloadItem : list){
            if(downloadItem == null){
                continue;
            }
            LogHelper.e("tests","title=="+downloadItem.getTitle()+"==finishTime=="+downloadItem.getFinishTime());
            JSONObject downloadJSONObject = new JSONObject();
            try {
//                downloadJSONObject.put("id",downloadItem.getId());//下载id
                downloadJSONObject.put("type", downloadItem.getDownloadType());//资源类型
                downloadJSONObject.put("res_id", downloadItem.getAid());//资源id
                downloadJSONObject.put("title", downloadItem.getTitle());//资源标题
                downloadJSONObject.put("icon_url", downloadItem.getImageUrl());//图标地址
                downloadJSONObject.put("size", FileSizeUtil.formatFileSize(downloadItem.getTotalLen()));//资源总大小
//                downloadJSONObject.put("current_size", FileSizeUtil.formatFileSize(downloadItem.getTotalLen() *  downloadItem.getProgress() / 100));//资源当前下载大小
//                downloadJSONObject.put("download_progress", DownLoadBusiness.getDownloadProgress(downloadItem));//当前下载进度
//                downloadJSONObject.put("download_status", downloadItem.getDownloadState());//下载状态
                downloadJSONObject.put("download_url", downloadItem.getHttpUrl());//资源下载地址
                downloadJSONObject.put("package_name", downloadItem.getPackageName());//apk包名
                downloadJSONObject.put("file_path", DownloadResBusiness.getDownloadResFile(downloadItem));//文件保存地址
                downloadJSONObject.put("duration", downloadItem.getDuration());
                downloadJSONObject.put("is_panorama", downloadItem.getIs_panorama());
                downloadJSONObject.put("is4k", downloadItem.getIs4k());
                downloadJSONObject.put("versioncode", downloadItem.getApkVersionCode());
                downloadJSONObject.put("video_dimension", downloadItem.getVideo_dimension());
                downloadJSONObject.put("pov_heading", downloadItem.getPov_heading());
                LogHelper.e("infossss","filePath=="+DownloadResBusiness.getDownloadResFile(downloadItem));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            downloadJSONArray.put(downloadJSONObject);
        }
        return downloadJSONArray;
    }




    /**
     * 创建下载完成的JSONObject
     */
    public static JSONObject createDownloadCompletedJSONObject(DownloadItem downloadItem) {
        JSONObject downloadJSONObject = new JSONObject();
        try {
            downloadJSONObject.put("id",downloadItem.getId());//下载id
            downloadJSONObject.put("type", downloadItem.getDownloadType());//资源类型
            downloadJSONObject.put("res_id", downloadItem.getAid());//资源id
            downloadJSONObject.put("title", downloadItem.getTitle());//资源标题
            downloadJSONObject.put("icon_url", downloadItem.getImageUrl());//图标地址
            downloadJSONObject.put("size", FileSizeUtil.formatFileSize(downloadItem.getTotalLen()));//资源总大小
            downloadJSONObject.put("current_size", FileSizeUtil.formatFileSize(downloadItem.getTotalLen() *  downloadItem.getProgress() / 100));//资源当前下载大小
            downloadJSONObject.put("download_progress", DownLoadBusiness.getDownloadProgress(downloadItem));//当前下载进度
            downloadJSONObject.put("download_status", downloadItem.getDownloadState());//下载状态
            downloadJSONObject.put("download_url", downloadItem.getHttpUrl());//资源下载地址
            downloadJSONObject.put("package_name", downloadItem.getPackageName());//apk包名
            downloadJSONObject.put("file_path", DownloadResBusiness.getDownloadResFile(downloadItem));//文件保存地址
            downloadJSONObject.put("duration", downloadItem.getDuration());
            downloadJSONObject.put("is_panorama", downloadItem.getIs_panorama());
            downloadJSONObject.put("is4k", downloadItem.getIs4k());
            downloadJSONObject.put("versioncode", downloadItem.getApkVersionCode());
            downloadJSONObject.put("video_dimension", downloadItem.getVideo_dimension());
            LogHelper.e("infossss","filePath=="+DownloadResBusiness.getDownloadResFile(downloadItem));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return downloadJSONObject;
    }

    /**
     * 创建删除的JSONObject
     */
    public static JSONObject createDeletedJSONObject(DownloadItem downloadItem) {
        JSONObject downloadJSONObject = new JSONObject();
        try {
            downloadJSONObject.put("res_id", downloadItem.getAid());//资源id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return downloadJSONObject;
    }


    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItem(String json) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        try {
            JSONObject jo = new JSONObject(json);
            downloadItem.setDownloadType(jo.getInt("type"));//资源类型
            downloadItem.setAid(jo.getString("res_id"));//资源id
            downloadItem.setTitle(jo.getString("title"));//资源标题
            downloadItem.setHttpUrl(jo.getString("download_url"));//资源下载地址
            downloadItem.setSite(jo.getString("size"));//资源大小
            downloadItem.setImageUrl(jo.getString("icon_url"));//图标地址
            if(!jo.isNull("id")){
                downloadItem.setId(jo.getLong("id"));
            }
            if(!jo.isNull("download_status")){
                downloadItem.setDownloadStatus(jo.getInt("download_status"));
            }
            if (!jo.isNull("duration")) {//视频总时长
                downloadItem.setDuration(jo.getInt("duration"));
            }
            if (!jo.isNull("package_name")) {//apk包名
                downloadItem.setPackageName(jo.getString("package_name"));
            }
            if (!jo.isNull("versioncode")) {//apk版本号
                downloadItem.setApkVersionCode(jo.getString("versioncode"));
            }
            if (!jo.isNull("video_dimension")) {//全景视频播放模式
                downloadItem.setVideo_dimension(jo.getInt("video_dimension"));
            }
            if (!jo.isNull("is_panorama")) {//全景控制
                 downloadItem.setIs_panorama(jo.getInt("is_panorama"));
            }
            if (!jo.isNull("is4k")) {//是否为4k视频
               downloadItem.setIs4k(jo.getInt("is4k"));
            }
            if (!jo.isNull("pov_heading")) {//初始角度
               downloadItem.setPov_heading(jo.getInt("pov_heading"));
            }
            if (!jo.isNull("operation_type")) {//播放类型
               downloadItem.setOperation_type(jo.getInt("operation_type"));
            }
            if (!jo.isNull("source")) {//资源来源
               downloadItem.setSource(jo.getString("source"));
            }
            if (!jo.isNull("play_mode")) {//游戏的控制方式
                downloadItem.setPlay_mode(jo.getString("play_mode"));
            }
            if (!jo.isNull("url")) {//资源详情的访问接口
               downloadItem.setUrl(jo.getString("url"));
            }
//            if(UnityActivity.mIsYTJ){
                downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
//            }else {
//                downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
//            }

            downloadItem.setFileDir(DownloadResBusiness.getDownloadResFolder(jo.getInt("type")));//资源本地保存路径
//            if(downloadItem.getCreateTime() == 0){
//                downloadItem.setCreateTime(System.currentTimeMillis());
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return downloadItem;
    }

    /**
     * 创建下载JSONObject
     */
    public static JSONObject createJSONObject(DownloadItem downloadItem) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", downloadItem.getDownloadType());//资源类型
            jo.put("res_id", downloadItem.getAid());//资源id
            jo.put("title", downloadItem.getTitle());//资源标题
            jo.put("download_url", downloadItem.getHttpUrl());//资源下载地址
            jo.put("size", downloadItem.getSite());//资源大小
            jo.put("icon_url", downloadItem.getImageUrl());//图标地址
            int duration = downloadItem.getDuration();
            if (duration > 0) {//视频总时长
                jo.put("duration", duration);
            }
            String packageName = downloadItem.getPackageName();
            if (!TextUtils.isEmpty(packageName)) {//apk包名
                jo.put("package_name", packageName);
            }
            String versioncode = downloadItem.getApkVersionCode();
            if (!TextUtils.isEmpty(versioncode)) {//apk版本号
                jo.put("versioncode", versioncode);
            }
            int video_dimension =  downloadItem.getVideo_dimension();
            if (video_dimension > 0) {//全景视频播放模式
                jo.put("video_dimension", video_dimension);
            }
            int is_panorama = downloadItem.getIs_panorama();
            if (is_panorama > 0) {//全景控制
                jo.put("is_panorama", is_panorama);
            }
            int is4k =  downloadItem.getIs4k();
            if(is4k > 0){
                jo.put("is4k", is4k);
            }
            jo.put("pov_heading",  downloadItem.getPov_heading());//初始角度
            jo.put("operation_type", downloadItem.getOperation_type());//播放类型
            jo.put("source",  downloadItem.getSource());//资源来源
            jo.put("play_mode", downloadItem.getPlay_mode());//游戏的控制方式
            jo.put("url", downloadItem.getUrl());//资源详情的访问接口
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItem(PanoramaVideoBean panoramaVideoBean, String downloadUrl, String size, int is4k, String detailUrl) {
        DownloadItem downloadItem = createDownloadItem(panoramaVideoBean, detailUrl);
        downloadItem.setHttpUrl(downloadUrl);//资源下载地址
        downloadItem.setSite(size);//资源大小
        downloadItem.setIs4k(is4k);
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        return downloadItem;
    }

    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItem(PanoramaVideoBean panoramaVideoBean, String detailUrl) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        downloadItem.setDownloadType(panoramaVideoBean.getType());//资源类型
        downloadItem.setAid(String.valueOf(panoramaVideoBean.getRes_id()));//资源id
        downloadItem.setTitle(panoramaVideoBean.getTitle());//资源标题
        downloadItem.setHttpUrl(panoramaVideoBean.getDownload_url());//资源下载地址
        downloadItem.setSite(panoramaVideoBean.getSize());//资源大小
        downloadItem.setImageUrl(panoramaVideoBean.getThumb_pic_url().get(0));//图标地址
        downloadItem.setDuration(Integer.valueOf(panoramaVideoBean.getDuration()));//视频总时长
        downloadItem.setVideo_dimension(panoramaVideoBean.getVideo_dimension());//全景视频播放模式
        downloadItem.setIs_panorama(panoramaVideoBean.getIs_panorama());//全景控制
        downloadItem.setPov_heading(panoramaVideoBean.getPov_heading());//初始角度
        downloadItem.setOperation_type(panoramaVideoBean.getOperation_type());//播放类型
        downloadItem.setSource(panoramaVideoBean.getSource());//资源来源
        downloadItem.setUrl(detailUrl);//资源详情的访问接口
        downloadItem.setFileDir(DownloadResBusiness.getDownloadResFolder(panoramaVideoBean.getType()));//资源本地保存路径
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        downloadItem.setCreateTime(System.currentTimeMillis());
        return downloadItem;
    }

    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItem(VideoDetailBean videoDetailBean, String downloadUrl, String size, int is4k, String detailUrl) {
        DownloadItem downloadItem = createDownloadItem(videoDetailBean, detailUrl);
        downloadItem.setHttpUrl(downloadUrl);//资源下载地址
        downloadItem.setSite(size);//资源大小
        downloadItem.setIs4k(is4k);
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        return downloadItem;
    }
    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItem(VideoDetailBean videoDetailBean, String detailUrl) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        downloadItem.setDownloadType(videoDetailBean.getType());//资源类型
        downloadItem.setAid(String.valueOf(videoDetailBean.getId()));//资源id
        downloadItem.setTitle(videoDetailBean.getTitle());//资源标题
        downloadItem.setDuration(Integer.valueOf(videoDetailBean.getDuration()));//视频总时长
        downloadItem.setSource(videoDetailBean.getSource());//资源来源
        downloadItem.setUrl(detailUrl);//资源详情的访问接口
        downloadItem.setFileDir(DownloadResBusiness.getDownloadResFolder(videoDetailBean.getType()));//资源本地保存路径
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(), downloadItem.getAid(), "") + BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(), downloadItem.getTitle(), "") + BaseApplication.TEMP);
        downloadItem.setCreateTime(System.currentTimeMillis());
        downloadItem.setImageUrl(videoDetailBean.getHpic());//图标地址
        return downloadItem;
    }

    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItem(GameDetailBean gameDetailBean, String detailUrl) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        downloadItem.setDownloadType(gameDetailBean.getType());//资源类型
        downloadItem.setAid(gameDetailBean.getRes_id());//资源id
        downloadItem.setTitle(gameDetailBean.getTitle());//资源标题
        downloadItem.setHttpUrl(gameDetailBean.getDownload_url());//资源下载地址
        downloadItem.setSite(gameDetailBean.getSize());//资源大小
        downloadItem.setImageUrl(gameDetailBean.getIcon_url());//图标地址
        downloadItem.setPackageName(gameDetailBean.getPackage_name());//apk包名
        downloadItem.setApkVersionCode(gameDetailBean.getVersioncode());//apk版本号
        downloadItem.setSource(gameDetailBean.getSource());
        downloadItem.setPlay_mode(gameDetailBean.getPlay_mode().toString());
//        List<String> playModeList = gameDetailBean.getPlay_mode();
//        if(playModeList != null && playModeList.size() > 0){
//            downloadItem.setPlay_mode(playModeList.get(0));
//        }
        downloadItem.setUrl(detailUrl);//资源详情的访问接口
        downloadItem.setFileDir(DownloadResBusiness.getDownloadResFolder(gameDetailBean.getType()));//资源本地保存路径
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        downloadItem.setCreateTime(System.currentTimeMillis());
        return downloadItem;
    }

    /**
     * 创建下载DownloadItem（解压obb用）
     */
    public static DownloadItem createDownloadItem(int resType, String resId, String resTitle, String downloadUrl) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        downloadItem.setDownloadType(resType);//资源类型
        downloadItem.setAid(resId);//资源id
        downloadItem.setTitle(resTitle);//资源标题
        downloadItem.setHttpUrl(downloadUrl);//资源下载地址
        downloadItem.setSite("");//资源大小
        downloadItem.setImageUrl("");//图标地址
        downloadItem.setPackageName("");//apk包名
        downloadItem.setApkVersionCode("");//apk版本号
        downloadItem.setFileDir("");//资源本地保存路径
        downloadItem.setCreateTime(System.currentTimeMillis());
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        return downloadItem;
    }

    /**
     * 创建下载DownloadItem
     */
    public static DownloadItem createDownloadItemForMojing(String downloadUrl) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        downloadItem.setDownloadType(ResTypeUtil.res_type_apply);//资源类型
        downloadItem.setAid(DOWNLOAD_ID_MJ);//资源id
        downloadItem.setTitle(DOWNLOAD_TITLE_MJ);//资源标题
        downloadItem.setPackageName(BaseApplication.INSTANCE.getPackageName());
        downloadItem.setHttpUrl(downloadUrl);//资源下载地址
        downloadItem.setFileDir(DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_apply));//本地保存路径
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
        //downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        downloadItem.setCreateTime(System.currentTimeMillis());
        return downloadItem;
    }

    /**
     * 创建OTA资源下载
     */
    public static DownloadItem createDownloadItemForOTA(OTABean.DataBean listBean) {
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        downloadItem.setDownloadType(DOWNLOAD_TYPE_OTA);//资源类型
        downloadItem.setAid(listBean.getUpgrade_md5());//资源MD5
        downloadItem.setTitle(listBean.getUpgrade_name());//资源标题
        downloadItem.setHttpUrl(listBean.getUpgrade_download());//资源下载地址
        downloadItem.setApkVersionCode(listBean.getUpgrade_version()); //升级包版本号
        downloadItem.setApkVersionName(listBean.getUpgrade_desc()); //版本描述
        downloadItem.setFileDir(FileStorageUtil.getExternalMojingFileDir());//本地保存路径
        downloadItem.setApkDownloadType(listBean.getUp_way());//升级方式，0静默，1非静默，2动态，3强更
        downloadItem.setDefinition(listBean.getUpgrade_provision()); //版本要求
        downloadItem.setCreateTime(System.currentTimeMillis());
        return downloadItem;
    }
    // 创建Fireware下载
    public static DownloadItem createDownloadItemForFireware(int type, String downloadURL){
        DownloadItem downloadItem = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
        int resType = 0;
        String resID = "";
        String resTitle = "";
        switch (type) {
            case FirewareBusiness.FIREWARE_TYPE_MCU:
                resType = DOWNLOAD_TYPE_FRIEWARE_MCU;
                resID = DOWNLOAD_ID_FRIEWARE_MCU;
                resTitle = DOWNLOAD_TITLE_FRIEWARE_MCU;
                break;
            case FirewareBusiness.FIREWARE_TYPE_BLE:
                resType = DOWNLOAD_TYPE_FRIEWARE_BLE;
                resID = DOWNLOAD_ID_FRIEWARE_BLE;
                resTitle = DOWNLOAD_TITLE_FRIEWARE_BLE;
                break;
            default:break;
        }
        downloadItem.setDownloadType(resType);//资源类型
        downloadItem.setAid(resID);//资源id
        downloadItem.setTitle(resTitle);//资源标题
        downloadItem.setHttpUrl(downloadURL);
        downloadItem.setFileDir(FileStorageUtil.getExternalMojingFileDir());
        downloadItem.setCreateTime(System.currentTimeMillis());
        downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getAid(), "")+BaseApplication.TEMP);
       // downloadItem.setFilePathName(DownLoadItemUtils.getDowloadDicFromType(downloadItem.getDownloadType(),downloadItem.getTitle(), "")+BaseApplication.TEMP);
        return downloadItem;
    }
}


