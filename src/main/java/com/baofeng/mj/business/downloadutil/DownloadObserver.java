package com.baofeng.mj.business.downloadutil;

import android.content.Context;

import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.downloadlistener.DownLoaderListener;
import com.baofeng.mj.business.downloadlistener.DownloadListenerUtils;
import com.baofeng.mj.util.threadutil.DLProxy;
import com.mojing.dl.domain.DownloadItem;

import java.util.ArrayList;

/**
 * Created by zhangxiong on 2016/9/1.
 */
public class DownloadObserver implements DownloadListenerUtils {
    protected final ArrayList<DownLoaderListener> mDownLoadListeners = new ArrayList<>();
    protected static DownloadObserver mInstance;
    protected DownloadUtils downloadBusiness;
    private DownloadObserver() {
        if(downloadBusiness == null){
            downloadBusiness= DownloadUtils.getInstance();
        }
    }
    public static DownloadObserver getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadObserver();
        }
        return mInstance;
    }

    @Override
    public void registerListener(Context mContext, DownLoaderListener downLoaderListener) {
        if (downLoaderListener == null) {
            return;
        }
        synchronized (mDownLoadListeners) {
            /**
             * 添加回调
             */
            if(downloadBusiness!=null){
                downloadBusiness.onResume(mContext);
            }

            if (mDownLoadListeners.contains(downLoaderListener)) {
                return;
            }else{
                mDownLoadListeners.clear();
                mDownLoadListeners.add(downLoaderListener);
            }

        }
    }

    @Override
    public void unregisterListener(DownLoaderListener downLoaderListener) {
        if (downLoaderListener == null) {
            return;
        }
        synchronized (mDownLoadListeners) {
            int index = mDownLoadListeners.indexOf(downLoaderListener);
            if (index == -1) {
                return;
            }
            mDownLoadListeners.remove(index);
            /**
             * 取消回调
             */
            if(downloadBusiness!=null){
                downloadBusiness.onPause();
            }
        }
    }

    /**
     * 取消注册所有的信息
     */
    @Override
    public void unregisterAll() {
        synchronized (mDownLoadListeners) {
            mDownLoadListeners.clear();
        }
    }

    @Override
    public void notifyDownLoadCallBack(final DownloadItem downloadItems) {
        DLProxy.getInstance().addProxyRunnable(new DLProxy.ProxyRunnable(){
            @Override
            public void run() {
                for (DownLoaderListener downLoaderListener : mDownLoadListeners)
                {
                    downLoaderListener.updateDownLoadStatus(downloadItems);
                }
            }
        });
    }
}
