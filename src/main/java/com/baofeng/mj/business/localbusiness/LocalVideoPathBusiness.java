package com.baofeng.mj.business.localbusiness;

import android.text.TextUtils;

import com.baofeng.mj.util.fileutil.FileStorageUtil;

/**
 * Created by liuchuanchi on 2016/5/5.
 * 本地视频路径业务
 */
public class LocalVideoPathBusiness {
    private static String localVideoFolderPath;//本地视频文件夹路径

    /**
     * 获取本地视频文件夹路径
     */
    public static String getLocalVideoFolderPath(){
        if(TextUtils.isEmpty(localVideoFolderPath)){
            localVideoFolderPath = FileStorageUtil.getMojingDir() + "localVideoFolder";
        }
        FileStorageUtil.mkdir(localVideoFolderPath);
        return localVideoFolderPath;
    }

    /**
     * 获取本地视频缩略图
     */
    public static String getLocalVideoImg(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            int lastIndex = filePath.lastIndexOf(".");
            if(lastIndex >= 0){
                filePath = filePath.substring(0, lastIndex);
            }
        }
        return getLocalVideoFolderPath() + "/" + filePath.hashCode() + ".png";
    }
}
