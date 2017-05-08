package com.baofeng.mj.business.downloadbusiness;

import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/5/5.
 * 下载资源业务
 */
public class DownloadResBusiness {
    private static String videoPath;//保存路径（视频文件）
    private static String gamePath;//保存路径（游戏文件）
    private static String appPath;//保存路径（应用文件）
    private static String imagePath;//保存路径（图片文件）
    private static String roamingPath;//保存路径（漫游文件）
    private static String firewarePath;//保存路径（固件文件）
    private static String moviePath;//保存路径（视频文件）
    private static String pluginPath; //保存路径（插件文件）
    /**
     * 重置路径
     */
    public static void resetPath(){
        videoPath = null;
        gamePath = null;
        appPath = null;
        imagePath = null;
        roamingPath = null;
        firewarePath = null;
        moviePath = null;
    }

    /**
     * 获取下载资源的文件夹
     */
    public static String getDownloadResFolder(int resType) {
        switch (resType){
            case ResTypeUtil.res_type_movie:
                if(TextUtils.isEmpty(moviePath)){
                    moviePath = FileStorageUtil.getDownloadDir() + "movie";
                }
                FileStorageUtil.mkdir(moviePath);
                return moviePath;
            case ResTypeUtil.res_type_video:
                if(TextUtils.isEmpty(videoPath)){
                    videoPath = FileStorageUtil.getDownloadDir() + "video";
                }
                FileStorageUtil.mkdir(videoPath);
                return videoPath;
            case ResTypeUtil.res_type_game:
                if(TextUtils.isEmpty(gamePath)){
                    gamePath = FileStorageUtil.getDownloadDir() + "game";
                }
                FileStorageUtil.mkdir(gamePath);
                return gamePath;
            case ResTypeUtil.res_type_apply:
                if(TextUtils.isEmpty(appPath)){
                    appPath = FileStorageUtil.getDownloadDir() + "app";
                }
                FileStorageUtil.mkdir(appPath);
                return appPath;
            case ResTypeUtil.res_type_image:
                if(TextUtils.isEmpty(imagePath)){
                    imagePath = FileStorageUtil.getDownloadDir() + "image";
                }
                FileStorageUtil.mkdir(imagePath);
                return imagePath;
            case ResTypeUtil.res_type_roaming:
                if(TextUtils.isEmpty(roamingPath)){
                    roamingPath = FileStorageUtil.getDownloadDir() + "roam";
                }
                FileStorageUtil.mkdir(roamingPath);
                return roamingPath;
            case ResTypeUtil.res_type_fireware:
                if(TextUtils.isEmpty(firewarePath)){
                    firewarePath = FileStorageUtil.getDownloadDir()+"fireware";
                }
                FileStorageUtil.mkdir(firewarePath);
                return firewarePath;
            case ResTypeUtil.res_type_plugin:
                if(TextUtils.isEmpty(pluginPath)){
                    pluginPath = FileStorageUtil.getDownloadDir()+"plugin";
                }
                FileStorageUtil.mkdir(pluginPath);
                return pluginPath;
            default:
                return "";
        }
    }

    /**
     * 获取下载资源的文件
     */
    public static File getDownloadResFile(PanoramaVideoBean panoramaVideoBean){
        int resType = panoramaVideoBean.getType();
        String resId = panoramaVideoBean.getRes_id();
        String resTitle = panoramaVideoBean.getTitle();
        String downloadUrl = panoramaVideoBean.getDownload_url();
        if(ResTypeUtil.res_type_video == resType){//全景视频
            List<PanoramaVideoAttrs> videoAttrs = panoramaVideoBean.getVideo_attrs();//视频属性集合
            for(PanoramaVideoAttrs panoramaVideoAttrs : videoAttrs){//遍历视频属性集合
                downloadUrl = panoramaVideoAttrs.getDownload_url();//下载地址
                File file = getDownloadResFile(resType, resId, resTitle, downloadUrl);
                if(file.exists()){
                    return file;
                }
            }
        }
        return getDownloadResFile(resType, resId, resTitle, downloadUrl);
    }

    /**
     * 获取下载资源的文件
     */
    public static File getDownloadResFile(VideoDetailBean videoBean){
        int resType = videoBean.getType();
        String resId = videoBean.getId()+"";
        String resTitle = videoBean.getTitle();
        String downloadUrl = "";
        if(ResTypeUtil.res_type_movie == resType){//全景视频
            List<VideoDetailBean.AlbumsBean> albumsBeans = videoBean.getAlbums();//视频属性集合
            for(VideoDetailBean.AlbumsBean bean : albumsBeans){//遍历视频属性集合
                downloadUrl = bean.getVideos().get(0).getDownload_url();//下载地址
                File file = getDownloadResFile(resType, resId, resTitle, downloadUrl);
                if(file.exists()){
                    return file;
                }
            }
        }
        return getDownloadResFile(resType, resId, resTitle, downloadUrl);
    }

    /**
     * 获取下载资源的文件
     */
    public static File getDownloadResFile(GameDetailBean gameDetailBean){
        int resType = gameDetailBean.getType();
        String resId = gameDetailBean.getRes_id();
        String resTitle = gameDetailBean.getTitle();
        String downloadUrl = gameDetailBean.getDownload_url();
        return getDownloadResFile(resType, resId, resTitle, downloadUrl);
    }

    /**
     * 获取下载资源的文件
     */
    public static File getDownloadResFile(DownloadItem downloadItem){
        int resType = downloadItem.getDownloadType();
        String resId = downloadItem.getAid();
        String resTitle = downloadItem.getTitle();
        String downloadUrl = downloadItem.getHttpUrl();
        return getDownloadResFile(resType, resId, resTitle, downloadUrl);
    }

    /**
     * 获取下载资源的文件
     */
    public static File getDownloadResFile(JSONObject jo){
        try {
            int resType = jo.getInt("type");
            String resId = jo.getString("res_id");
            String resTitle = jo.getString("title");
            String downloadUrl = jo.getString("download_url");
            return getDownloadResFile(resType, resId, resTitle, downloadUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取下载资源的文件（视频，图片，漫游都可以用）
     * 注：820一体机之后，资源名称改成resId，不是resTitle
     */
    public static File getDownloadResFile(int resType, String resId, String resTitle, String downloadUrl){
        LogHelper.e("tests","resType==="+resType+"==title=="+resTitle);
        String folder = getDownloadResFolder(resType);
        if(ResTypeUtil.res_type_roaming == resType) {//全景漫游
            File file = new File(folder, resId + ".zip");
            if(file.exists()){
                return file;
            }
            file = new File(folder, resTitle + ".zip");
            if(file.exists()){
                return file;
            }
            file = new File(folder, resId);
            return file;
        }else if(ResTypeUtil.isGameOrApp(resType)){//游戏或者应用
            File file = new File(folder, resId + ".zip");
            if(file.exists()){
                return file;
            }
            file = new File(folder, resTitle + ".zip");
            if(file.exists()){
                return file;
            }
            file = new File(folder, resId + ".apk");
            if(file.exists()){
                return file;
            }
            file = new File(folder, resTitle + ".apk");
            return file;
        }/*else if(ResTypeUtil.res_type_roaming == resType){

        }*/
        else{//其他
            String fileSuffix = FileCommonUtil.getFileSuffix(downloadUrl);//文件后缀
            File file = new File(folder, resId + fileSuffix);
            if(file.exists()){
                return file;
            }

            LogHelper.e("infos","downloadUrl=="+downloadUrl+"==suffix=="+fileSuffix+"==file.path=="+new File(folder, resTitle + fileSuffix));
            return new File(folder, resTitle + fileSuffix);
        }
    }

    /**
     * 获取apk文件
     * @param resType 资源类型
     * @param resTitle 资源标题
     */
    public static File getApkFile(int resType, String resId, String resTitle){
        String folder = getDownloadResFolder(resType);
        File file = new File(folder, resId + ".zip");
        if(file.exists()){
            return file;
        }
        file = new File(folder, resTitle + ".zip");
        if(file.exists()){
            return file;
        }
        file = new File(folder, resId + ".apk");
        if(file.exists()){
            return file;
        }
        file = new File(folder, resTitle + ".apk");
        return file;
    }

    /**
     * 获取apk文件
     * @param
     * @param
     */
   /* public static File getApkFile(int resType, String resTitle){
        String folder = getDownloadResFolder(resType);
        File file = new File(folder, resTitle + ".apk");//apk
        if(file.exists()){
            return file;
        }
        return new File(folder, resTitle + ".zip");//obb未解压
    }*/

    public static File getDownloadResFileNoEx(int resType, String resTitle, String resId){
        String folder = getDownloadResFolder(resType);

        File file = new File(folder, resId+ BaseApplication.TEMP);
		if(file.exists()){
            return file;
        }

        file = new File(folder, resTitle+ BaseApplication.TEMP);
        if(file.exists()){
            return file;
        }
        return null;
    }

    public static File getDownloadResFileHasEx(int resType, String resTitle,String resId, String downloadUrl){
        LogHelper.e("infosss","downloadUrl=="+downloadUrl);
        String folder = getDownloadResFolder(resType);
        LogHelper.e("infosss","folder=="+folder);
        File file = new File(folder, resId+ FileCommonUtil.getFileSuffix(downloadUrl));
        if(file.exists()){
            return file;
        }

        file = new File(folder, resTitle+ FileCommonUtil.getFileSuffix(downloadUrl));
        if(file.exists()){
            return file;
        }
        return new File(folder, resId + FileCommonUtil.getFileSuffix(downloadUrl));
    }

    /**
     * 删除资源文件
     * @param downloadItem 下载实体类
     */
    public static void deleteDownloadResFile(DownloadItem downloadItem){
        File file = getDownloadResFile(downloadItem);
        if(file.exists()){
            FileCommonUtil.deleteFile(file);//可能删除的是文件夹，所以调用这个方法
        }
        String fileDir = DownloadResBusiness.getDownloadResFolder(downloadItem.getDownloadType());
        File fileDownload = new File(fileDir, downloadItem.getAid() + ".download");
        if (fileDownload.exists()) {
            fileDownload.delete();
        }
        fileDownload = new File(fileDir, downloadItem.getTitle() + ".download");
        if (fileDownload.exists()) {
            fileDownload.delete();
        }
        File fileRanges = new File(fileDir, downloadItem.getAid() + ".ranges");
        if (fileRanges.exists()) {
            fileRanges.delete();
        }

        fileRanges = new File(fileDir, downloadItem.getTitle() + ".ranges");
        if (fileRanges.exists()) {
            fileRanges.delete();
        }

        File fileTemp = new File(fileDir, downloadItem.getAid() + BaseApplication.TEMP);
        if (fileTemp.exists()) {
            fileTemp.delete();
        }

        fileTemp = new File(fileDir, downloadItem.getTitle() + BaseApplication.TEMP);
        if (fileTemp.exists()) {
            fileTemp.delete();
        }

    }
}
