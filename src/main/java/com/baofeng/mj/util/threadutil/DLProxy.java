package com.baofeng.mj.util.threadutil;

/**
 * Created by liuchuanchi on 2016/7/19.
 * 单线程代理（下载刷新专用）
 */
public class DLProxy extends SingleThreadProxy {
    private static DLProxy instance;//单例

    private DLProxy(){
    }

    public static DLProxy getInstance(){
        if(instance == null){
            instance = new DLProxy();
        }
        return instance;
    }


}
