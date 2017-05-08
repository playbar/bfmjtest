package com.baofeng.mj.business.downloadlistener;


import com.mojing.dl.domain.DownloadItem;

/**
 * Created by zhangxiong on 2016/9/2.
 */
public interface DownLoaderListener {
    /**
     * 更新下载进度
     * @param downloadItem
     */
    void updateDownLoadStatus(DownloadItem downloadItem);
}
