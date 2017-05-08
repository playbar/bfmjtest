package com.baofeng.mj.business.downloadbusiness;

import com.baofeng.mj.business.publicbusiness.FileSearchBusiness;
import com.baofeng.mj.util.fileutil.FileCommonUtil;

import java.io.Serializable;

/**
 * @author liuchuanchi
 * @description: 下载信息检索业务
 */
public class DownloadResInfoSearchBusiness extends FileSearchBusiness {
    private static DownloadResInfoSearchBusiness instance;

    private DownloadResInfoSearchBusiness(){
    }

    public static DownloadResInfoSearchBusiness getInstance(){
        if(instance == null){
            instance = new DownloadResInfoSearchBusiness();
        }
        return instance;
    }

    @Override
    public Serializable readFileSearchMap() {
        return FileCommonUtil.readFileSerializable(DownloadResInfoBusiness.getDownloadSearchInfo());
    }

    @Override
    public void writeFileSearchMap() {
        FileCommonUtil.writeFileSerializable(getFileSearchMap(), DownloadResInfoBusiness.getDownloadSearchInfo());
    }
}
