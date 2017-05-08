package com.baofeng.mj.business.dl.domain;


import com.baofeng.mj.business.downloadutil.Common;

/**
 * Created by zhangxiong on 2016/8/24.
 */
public class DownloadConfig {
    /**
     * @param lMemTotalSize 内存缓存总量最大值
     * @param lFileTotalSize 文件缓存总量最大值
     * @param lJobMaxMemSize 单个任务内存大小最大值
     * @param lJobMaxFileSize 单个任务文件大小最大值
     * @param nJobMaxNum 同时进行的任务最大个数
     * @param cachePath 缓存路径，数据库
     */
    private long memoryMaxSize = 20;
    private long fileMaxSize = 1024 * 500;
    private long singleMemoryMaxSize = 20;
    private long singleFileMaxSize = 1024 * 5;
    private int maxTask = 3;
    private String cachePath ;

    public long getMemoryMaxSize() {
        return memoryMaxSize;
    }

    public long getFileMaxSize() {
        return fileMaxSize;
    }

    public long getSingleMemoryMaxSize() {
        return singleMemoryMaxSize;
    }

    public long getSingleFileMaxSize() {
        return singleFileMaxSize;
    }

    public int getMaxTask() {
        return maxTask;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String mCachePath){
        cachePath = mCachePath;
    }


}
