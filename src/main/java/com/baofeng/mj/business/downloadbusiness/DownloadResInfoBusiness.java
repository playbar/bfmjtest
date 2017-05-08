package com.baofeng.mj.business.downloadbusiness;

import android.text.TextUtils;

import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.mojing.dl.domain.DownloadItem;

import java.io.File;

/**
 * Created by liuchuanchi on 2016/5/5.
 * 下载资源信息业务
 */
public class DownloadResInfoBusiness {
    private static String videoInfoPath;//保存路径（视频文件的资源信息）
    private static String gameInfoPath;//保存路径（游戏文件的资源信息）
    private static String appInfoPath;//保存路径（应用文件的资源信息）
    private static String imageInfoPath;//保存路径（图片文件的资源信息）
    private static String roamingInfoPath;//保存路径（漫游文件的资源信息）
    private static String downloadingInfoPath;//保存路径（下载中的资源信息）
    private static String movieInfoPath;//保存路径（视频文件的资源信息）
    /**
     * 获取下载资源信息的文件夹
     */
    public static String getDownloadResInfoFolder(int resType){
        switch (resType){
            case ResTypeUtil.res_type_video:
                if(TextUtils.isEmpty(videoInfoPath)){
                    videoInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/videoInfo";
                }
                FileStorageUtil.mkdir(videoInfoPath);
                return videoInfoPath;
            case ResTypeUtil.res_type_movie:
                if(TextUtils.isEmpty(movieInfoPath)){
                    movieInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/movieInfo";
                }
                FileStorageUtil.mkdir(movieInfoPath);
                return movieInfoPath;
            case ResTypeUtil.res_type_game:
                if(TextUtils.isEmpty(gameInfoPath)){
                    gameInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/gameInfo";
                }
                FileStorageUtil.mkdir(gameInfoPath);
                return gameInfoPath;
            case ResTypeUtil.res_type_apply:
                if(TextUtils.isEmpty(appInfoPath)){
                    appInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/appInfo";
                }
                FileStorageUtil.mkdir(appInfoPath);
                return appInfoPath;
            case ResTypeUtil.res_type_image:
                if(TextUtils.isEmpty(imageInfoPath)){
                    imageInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/imageInfo";
                }
                FileStorageUtil.mkdir(imageInfoPath);
                return imageInfoPath;
            case ResTypeUtil.res_type_roaming:
                if(TextUtils.isEmpty(roamingInfoPath)){
                    roamingInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/roamingInfo";
                }
                FileStorageUtil.mkdir(roamingInfoPath);
                return roamingInfoPath;
            case ResTypeUtil.res_type_downloading:
                if(TextUtils.isEmpty(downloadingInfoPath)) {
                    downloadingInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo/downloadingInfo";
                }
                FileStorageUtil.mkdir(downloadingInfoPath);
                return downloadingInfoPath;
            default:
                return "";
        }
    }

    /**
     * 获取下载资源信息的文件路径
     * 注：820一体机之后，文件名改成资源id，为了兼容老版本，title和resId都得要
     */
    public static String getDownloadResInfoFilePath(int type, String title, String resId){
        String filePath = getDownloadResInfoFolder(type) + "/" + resId + ".info";
        File file = new File(filePath);
        if(file.exists()){
            return filePath;
        }
        return getDownloadResInfoFolder(type) + "/" + title + ".info";
    }

    /**
     * 获取下载资源信息的文件路径
     */
    /*public static String getDownloadResInfoFilePath(int type, String resId){
        return getDownloadResInfoFolder(type) + "/" + resId + ".info";
    }*/

    /**
     * 获取下载资源信息
     */
    public static String getDownloadResInfo(){
        String downloadInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo";
        FileStorageUtil.mkdir(downloadInfoPath);
        return downloadInfoPath + "/downloadInfo.info";
    }

    /**
     * 获取下载检索信息
     * @return
     */
    public static String getDownloadSearchInfo(){
        String downloadInfoPath = FileStorageUtil.getMojingDir() + "downloadInfo";
        FileStorageUtil.mkdir(downloadInfoPath);
        return downloadInfoPath + "/downloadSearchInfo.info";
    }

    /**
     * 删除资源信息文件
     * @param downloadItem 下载实体类
     */
    public static void deleteDownloadResInfoFile(DownloadItem downloadItem){
        //删除下载中的资源信息文件
        File fileDownloading = new File(getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading, downloadItem.getTitle(), downloadItem.getAid()));
        if(fileDownloading.exists()){
            fileDownloading.delete();
        }
        //删除已下载的资源信息文件
        File fileDownloaded = new File(getDownloadResInfoFilePath(downloadItem.getDownloadType(), downloadItem.getTitle(), downloadItem.getAid()));
        if(fileDownloaded.exists()){
            fileDownloaded.delete();
        }
    }
}
