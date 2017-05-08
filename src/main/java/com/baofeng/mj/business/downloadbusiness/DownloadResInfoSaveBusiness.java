package com.baofeng.mj.business.downloadbusiness;

import android.util.Log;

import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ComparatorLong;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author liuchuanchi
 * @description: 下载资源信息保存业务
 */
public class DownloadResInfoSaveBusiness {

    private DownloadResInfoSaveBusiness() {
    }

    /**
     * 获取已下载资源信息数据
     */
    public static void getDownloadInfoData(final DownloadInfoCallback downloadInfoCallback) {
        HashMap<Long, String> downloadInfoMap = null;//从本地读取下载资源信息
        Serializable downloadInfoSer = FileCommonUtil.readFileSerializable(DownloadResInfoBusiness.getDownloadResInfo());
        if (downloadInfoSer == null) {
            downloadInfoMap = new HashMap<Long, String>();
        }else{
            downloadInfoMap = (HashMap<Long, String>) downloadInfoSer;
        }
        HashMap<Long, String> searchMap = new HashMap<Long, String>();//下载资源信息集合
        //searchDownloadInfo(ResTypeUtil.res_type_image,searchMap);//检索图片资源信息
        searchDownloadInfo(ResTypeUtil.res_type_roaming,searchMap);//检索漫游资源信息
        searchDownloadInfo(ResTypeUtil.res_type_video,searchMap);//检索视频资源信息
        searchDownloadInfo(ResTypeUtil.res_type_game, searchMap);//检索游戏资源信息
        searchDownloadInfo(ResTypeUtil.res_type_apply, searchMap);//检索应用资源信息
        searchDownloadInfo(ResTypeUtil.res_type_movie, searchMap);//检索3d视频资源信息
        if (searchMap.size() > 0) {
            Iterator iter = searchMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                try {
                    Long lastModify = (Long) entry.getKey();
                    String json = (String) entry.getValue();
                    if(downloadInfoMap.containsValue(json)){
                        removeOldJson(json, downloadInfoMap);
                    }
                    downloadInfoMap.put(lastModify, json);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        LogHelper.e("infos","===getDownloadInfoData==11111====="+downloadInfoMap.size());
        deleteFileNotExist(downloadInfoMap);//删除不存在的资源信息
        LogHelper.e("infos","===getDownloadInfoData==2222====="+downloadInfoMap.size());
        FileCommonUtil.writeFileSerializable(downloadInfoMap, DownloadResInfoBusiness.getDownloadResInfo());
        DownloadResInfoSearchBusiness.getInstance().resetFileSearchMap();//重置fileSearchMap
        TreeMap treeMap = sort(downloadInfoMap);//排序
        if(downloadInfoCallback != null){
            downloadInfoCallback.downloadInfoCallback(treeMap);
        }
    }

    /**
     * 检索下载信息
     */
    private static void searchDownloadInfo(int resType, HashMap<Long, String> searchMap){
        String dirPath = DownloadResInfoBusiness.getDownloadResInfoFolder(resType);//下载信息目录
        File dirFile = new File(dirPath);
        if(!dirFile.exists() || !dirFile.isDirectory()){
            return;
        }
        File[] fileList = dirFile.listFiles();//文件夹下所有文件
        if (fileList != null && fileList.length > 0) {
            for (File f : fileList) {
                if(f.isFile() && DownloadResInfoSearchBusiness.getInstance().needSearch(f)) {//是文件，需要检索
                    String json = FileCommonUtil.readFileString(f);
                    searchMap.put(f.lastModified(), json);
                }
            }
        }
    }

    /**
     * 排序
     * @return
     */
    private static TreeMap<Long, String> sort(HashMap<Long, String> downloadInfoMap){
        TreeMap<Long, String> treeMap = new TreeMap<Long, String>(new ComparatorLong());
        Iterator iter = downloadInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            try {
                Long lastModify = (Long) entry.getKey();
                String json = (String) entry.getValue();
                treeMap.put(lastModify, json);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return treeMap;
    }

    /**
     * 删除不存在的资源信息
     */
    private static void deleteFileNotExist(HashMap<Long,String> downloadInfoMap){
        List<Long> deleteList = new ArrayList<Long>();
        Iterator iter = downloadInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            try {
                String json = (String) entry.getValue();//资源信息实体类
                JSONObject joDownloadInfo = new JSONObject(json);
                int resType = joDownloadInfo.getInt("type");
                String resId = joDownloadInfo.getString("res_id");
                String resTitle = joDownloadInfo.getString("title");
                String downloadUrl = joDownloadInfo.getString("download_url");

                File downloadInfoFile = new File(DownloadResInfoBusiness.getDownloadResInfoFilePath(resType, resTitle, resId));//资源信息文件
                if(!downloadInfoFile.exists()){//资源信息文件不存在
                    deleteList.add((Long) entry.getKey());
                    LogHelper.e("infos","======资源信息文件不存在========="+downloadInfoFile.getAbsolutePath());
                }else {//资源信息文件存在
                    File downloadFile = DownloadResBusiness.getDownloadResFile(resType, resId, resTitle, downloadUrl);//资源文件
                    if(!downloadFile.exists()){//资源文件不存在
                        if(ResTypeUtil.isGameOrApp(resType)){//游戏或者应用
                            String packageName = joDownloadInfo.getString("package_name");//包名
                            if(!ApkUtil.apkHasInstalled(packageName)){//未安装
                                deleteList.add((Long) entry.getKey());
                                FileCommonUtil.deleteFile(downloadInfoFile);//删除资源信息文件
                            }
                        }else{
                            deleteList.add((Long) entry.getKey());
                            FileCommonUtil.deleteFile(downloadInfoFile);//删除资源信息文件
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Long lastModify : deleteList){
            downloadInfoMap.remove(lastModify);
        }
    }

    /**
     * 删除资源信息
     */
    public static void deleteFile(DownloadItem downloadItem){
        Serializable downloadInfoSer = FileCommonUtil.readFileSerializable(DownloadResInfoBusiness.getDownloadResInfo());
        if (downloadInfoSer == null) {
            return;
        }
        HashMap<Long, String> downloadInfoMap = (HashMap<Long, String>) downloadInfoSer;
        Iterator iter = downloadInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            try {
                String json = (String) entry.getValue();//资源信息实体类
                JSONObject joDownloadInfo = new JSONObject(json);
                int resType = joDownloadInfo.getInt("type");
                String resId = joDownloadInfo.getString("res_id");
                if(resType == downloadItem.getDownloadType() && resId.equals(downloadItem.getAid())){
                    downloadInfoMap.remove((Long) entry.getKey());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileCommonUtil.writeFileSerializable(downloadInfoMap, DownloadResInfoBusiness.getDownloadResInfo());
    }

    /**
     * 移除旧的资源信息
     * @param newJson 新的资源信息
     * @param downloadInfoMap 资源信息map
     */
    private static void removeOldJson(String newJson, HashMap<Long, String> downloadInfoMap){
        Iterator iter = downloadInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            try {
                String oldJson = (String) entry.getValue();//旧的资源信息
                if(oldJson.equals(newJson)){
                    downloadInfoMap.remove(entry.getKey());
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public interface DownloadInfoCallback{
        void downloadInfoCallback(TreeMap<Long, String> downloadInfoMap);
    }
}
