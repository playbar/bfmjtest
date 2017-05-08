package com.baofeng.mj.util.threadutil;

/**
 * Created by liuchuanchi on 2016/7/19.
 * 单线程代理（本地下载用）
 */
public class LocalDownloadProxy extends SingleThreadProxy {
    private static LocalDownloadProxy instance;//单例

    private LocalDownloadProxy(){
    }

    public static LocalDownloadProxy getInstance(){
        if(instance == null){
            instance = new LocalDownloadProxy();
        }
        return instance;
    }
}
