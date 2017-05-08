package com.baofeng.mj.business.downloadlistener;

import android.content.Context;

import com.mojing.dl.domain.DownloadItem;


/**
 * Created by zhangxiong on 2016/9/2.
 */
public interface DownloadListenerUtils {

     void registerListener(Context mContext, DownLoaderListener downLoaderListener);

     void unregisterListener(DownLoaderListener downLoaderListener);

     void unregisterAll();

     void notifyDownLoadCallBack(DownloadItem downloadItem);

}
