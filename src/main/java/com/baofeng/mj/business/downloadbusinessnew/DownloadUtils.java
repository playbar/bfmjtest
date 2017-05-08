package com.baofeng.mj.business.downloadbusinessnew;

import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;

import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoBusiness;
import com.baofeng.mj.business.downloadutil.DownloadObserver;
import com.baofeng.mj.business.downloadutil.FileTools;
import com.baofeng.mj.business.downloadutil.PublicoConfig;
import com.baofeng.mj.business.pluginbusiness.PluginDownloadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.threadutil.LocalDownloadProxy;
import com.baofeng.mojing.MojingDownloader;
import com.baofeng.mojing.sdk.download.entity.NativeCallbackInfo;
import com.baofeng.mojing.sdk.download.utils.MjDownloadErrorCode;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.mojing.dl.utils.DownloadConstant;
import com.storm.smart.common.utils.LogHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by dupengwei on 2017/3/24.
 */
public class DownloadUtils implements MojingDownloader.DownloadCallback {
    private static final String TAG = "DownloadUtils";
    private static ArrayList<DownloadItem> waittingInfos = new ArrayList<>();            //等待中的集合
    private static ArrayList<DownloadItem> mDownLoadings = new ArrayList<>();              //下载中集合
    private static DownloadItem mDownLoadItem;
    private Context mContext;
    private final int HANDLER_WHAT_CALLBACK = 0;
    private final int HANDLER_WHAT_START_CALLBACK = 1;
    private BusinessHandler handler;
    public   boolean mIsInit ;
    private static DownloadUtils mInstance;

    public static DownloadUtils getInstance() {
        if (null == mInstance) {
            mInstance = new DownloadUtils();
        }
        return mInstance;
    }

    private DownloadUtils(){
        handler = new BusinessHandler(Looper.getMainLooper());
    }


    @Override
    public void callback(long id, int status, double progress, long errorCode) {
        if (errorCode != 0) {
//            LogHelper.e(TAG, "下载出错:::" + errorCode + "---" + translateErrorCode(errorCode));
        }

        if (handler != null) {
            DownloadItem info = new DownloadItem(DownloadConstant.DownloadItemType.ITEM_TYPE_SIMPLE);
            info.setId(id);
            info.setDownloadStatus(status);
            Double pro = progress * 100;
            int progressInt = pro.intValue();
            info.setProgress(progressInt);
            info.setDownloadErrorCode(errorCode);
            LogHelper.i("infos","title==="+id+"==progress=="+progress+"==status=="+status+"==errorCode=="+errorCode);
            handler.obtainMessage(HANDLER_WHAT_CALLBACK, info).sendToTarget();
        }

    }

    /*
     * sdk在无网络状态下，调用下载，会返回id并且状态为1,这个时候，需要判断网络，如果没有网络，就不进行保存和删除操作
     * 要不然会导致数据丢失
     *
     */
    @Override
    public void startCallback(long id, String url, String path, int status) {
        LogHelper.e("setDownloadStatus", "id==" + id + "==url==" + url + "==path==" + path + "==status==" + status + "===downloaditem===" + mDownLoadItem);
        if (status == 5) {
            LogHelper.e("errorCode", "errorCode===" + MojingDownloader.JobGetLastErrorCode(id));
        }

        if (mDownLoadItem != null && NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE) ) {
            if (mDownLoadItem.getHttpUrl().equals(url) && mDownLoadItem.getFilePathName().equals(path)) {
                mDownLoadItem.setId(id);
                mDownLoadItem.setHttpUrl(url);
                mDownLoadItem.setDownloadStatus(status);
                if (mDownLoadItem.getDownloadState() == MjDownloadStatus.ERROR) {
                    mDownLoadItem.setDownloadErrorCode(MjDownloadErrorCode.START_ERROR);
                } else if (mDownLoadItem.getId() == 0 && mDownLoadItem.getDownloadState() == MjDownloadStatus.WAITING) {//等待中的item返回的id为0
                    addWaitting(mDownLoadItem);
                } else {
                    if (mDownLoadItem.getId() != 0 &&
                            (mDownLoadItem.getDownloadState() == MjDownloadStatus.CONNECTING
                                    || mDownLoadItem.getDownloadState() == MjDownloadStatus.DEFAULT
                                    || mDownLoadItem.getDownloadState() == MjDownloadStatus.DOWNLOADING)
                            ) {
                        if (!isDownloading(mDownLoadItem)) {
                            mDownLoadings.add(mDownLoadItem);
                        }
                        FileTools.writeDownLoadingFile(mDownLoadings);
                        if (isWaiting(mDownLoadItem)) {
                            waittingInfos.remove(mDownLoadItem);
                            FileTools.writeWaittingFile(waittingInfos);
                        }
                    } else if (mDownLoadItem.getId() != 0 ) {
                        addWaitting(mDownLoadItem);
                    }

                }
                if (status != MjDownloadStatus.DOWNLOADING) {
                    //通知刷新
                    DownloadObserver.getInstance().notifyDownLoadCallBack(mDownLoadItem);
                    changeStatus(mDownLoadItem);

                }


            }
        }
    }

    private void changeStatus(DownloadItem downloadItem) {
        LogHelper.e("setDownloadStatus","downloadItem=="+downloadItem.getTitle()+"==state=="+downloadItem.getDownloadState());
        switch (downloadItem.getDownloadState()) {
            case MjDownloadStatus.WAITING:
                addWaitting(downloadItem);
                break;
            case MjDownloadStatus.COMPLETE:
                waittingToRun(mContext);
                FileTools.writeDownLoadingFile(mDownLoadings);
                break;
            case MjDownloadStatus.ABORT:
            case MjDownloadStatus.PAUSED:
            case MjDownloadStatus.ERROR:

                waittingToRun(mContext);

                int count = 0;
                if (downloadItem.getDownloadState() == MjDownloadStatus.ABORT
                        || downloadItem.getDownloadState() == MjDownloadStatus.ERROR) {
                    count = downloadItem.getAbortCount();
                    count++;
                    downloadItem.setAbortCount(count);
                }
                LogHelper.e("infosss","count==="+count);
                if (count <= 2) {
                    FileTools.writeDownLoadingFile(mDownLoadings);
                    FileTools.writeWaittingFile(waittingInfos);
                }

                break;
            default:
                break;
        }

        for(DownloadItem item: mDownLoadings){
            if(item == null){
                continue;
            }
            LogHelper.e("setDownloadStatus","title==="+item.getTitle()+"==state=="+item.getDownloadState());
        }
        for(DownloadItem in : waittingInfos){
            if(in == null){
                continue;
            }
            LogHelper.e("setDownloadStatus","title-----"+in.getTitle()+"-----state=="+in.getDownloadState());
        }
    }

    public void onPause() {
        MjDownloadSDK.removeCallback(this);
    }

    public void onResume(Context context) {
        this.mContext = context;
        MjDownloadSDK.addCallback(this);
    }


    public void pauseDownloading(DownloadItem downloadItemInfo) {
        DownloadItem item = queryInfo(downloadItemInfo, false);
        MjDownloadSDK.pause(item.getId());
    }

    public void stopDownloading(Context context) {
        ArrayList<DownloadItem> allDownLoadings = getAllDownLoadings(context);
        for (DownloadItem downItem : allDownLoadings) {
            LogHelper.e("infossss", "state===" + downItem.getDownloadState());
            if (downItem.getDownloadState() == MjDownloadStatus.DOWNLOADING) {
                MjDownloadSDK.pause(downItem.getId());
                downItem.setPauseReason(PublicoConfig.Reason.LOCAL_PAUSE);
            }
        }

    }
    public void changePauseReason(Context context) {
        ArrayList<DownloadItem> allDownLoadings = getAllDownLoadings(context);
        for (DownloadItem downItem : allDownLoadings) {
            LogHelper.e("infossss", "state------" + downItem.getDownloadState());
            if (downItem.getDownloadState() == MjDownloadStatus.DOWNLOADING) {
                downItem.setPauseReason(PublicoConfig.Reason.LOCAL_PAUSE);
            }
        }
    }

    /**
     * 启动下载
     *
     * @param context
     * @param downloadItemInfo
     */
    public void startDownload(Context context, DownloadItem downloadItemInfo) {
        if(!MjDownloadSDK.isEnabled()){
            BaseApplication.INSTANCE.initDownloadInfo();
            LogHelper.e("infossss","======初始化下载库=======");
        }
        LogHelper.e("infosss","title=="+downloadItemInfo.getTitle());
        if(!waittingInfos.isEmpty()){//同步状态，u3d没有传过来状态
           /* for(int i = 0; i < waittingInfos.size();i++){
                if(downloadItemInfo.getAid().equals(waittingInfos.get(i).getAid())
                        && waittingInfos.get(i).getDownloadState() == MjDownloadStatus.COMPLETE){
                    waittingInfos.remove(i);
                    i--;
                }
            }*/

            for(DownloadItem in : waittingInfos){
                if(in.getAid().equals(downloadItemInfo.getAid())){
                    if(in.getDownloadState() != MjDownloadStatus.COMPLETE){
                        downloadItemInfo.setDownloadStatus(in.getDownloadState());
                    }
                    downloadItemInfo.setPauseReason(PublicoConfig.Reason.DEFAULT_REASON);
                }
            }

        }

//        if(downloadItemInfo.getDownloadState() == MjDownloadStatus.ERROR){//错误重新下载
//            downloadItemInfo.setDownloadErrorCount(0);
//        }

        LogHelper.e("infosss","=====startDownload====="+downloadItemInfo.getDownloadState());
        if(!mDownLoadings.isEmpty() && (downloadItemInfo.getDownloadState() == MjDownloadStatus.ABORT
                                      || downloadItemInfo.getDownloadState() == MjDownloadStatus.WAITING
                                       || downloadItemInfo.getDownloadState() == MjDownloadStatus.ERROR)){

            int count = 0;
            for(DownloadItem temp:mDownLoadings){
                if(temp.getDownloadState() == MjDownloadStatus.DOWNLOADING){
                    count++;
                }
            }
            if(count >= 3){
                sortCreateTimeByDownLoadItems(mDownLoadings);
                DownloadItem tempItem;
                for(int i = mDownLoadings.size() - 1; i >= 0;i--){
                    tempItem = mDownLoadings.get(i);
                    if(tempItem.getDownloadState() != MjDownloadStatus.DOWNLOADING){
                        continue;
                    }
                    MjDownloadSDK.pause(tempItem.getId());
                    tempItem.setDownloadStatus(MjDownloadStatus.ABORT);
                    break;
                }
            }

        }
        FileStorageUtil.getDownloadDir();
        DownloadItem item = queryInfo(downloadItemInfo,false);
        mDownLoadItem = item;
        LogHelper.e(TAG, "item==" + item.getDownloadState() + "===cacheType==" + item.getCacheType() + "==reason==" + item.getPauseReason() + "==titile==" + item.getTitle() + "==download.statue===" + item.getDownloadState() + "==progress=="+item.getProgress());
        switch (item.getDownloadState()) {
            case MjDownloadStatus.DOWNLOADING:
                if(BaseApplication.isFromUnityOrStartApp){
                    MjDownloadSDK.start(context, item.getHttpUrl(), item.getFilePathName(), item.getCacheType());
                }else{
                    MjDownloadSDK.pause(item.getId());
                    item.setDownloadStatus(MjDownloadStatus.ABORT);
                    waittingToRun(context);
                }

                break;
            case MjDownloadStatus.DEFAULT:
            case MjDownloadStatus.WAITING:
                LogHelper.e("infossss", "httpurl==" + item.getHttpUrl() + "==filepathName==" + item.getFilePathName() + "==cacheType==" + item.getCacheType() + "===context===" + context);
                MjDownloadSDK.start(context, item.getHttpUrl(), item.getFilePathName(), item.getCacheType());
                break;

            case MjDownloadStatus.PAUSED:
            case MjDownloadStatus.ABORT:
            case MjDownloadStatus.CONNECTING:
//            case MjDownloadStatus.COMPLETE:
            case MjDownloadStatus.ERROR:
               /* if (item.getDownloadState() == MjDownloadStatus.ERROR
                        &&  item.getDownloadErrorCode() != MjDownloadErrorCode.NETWORK_ERROR ) {
                    MjDownloadSDK.delete(item.getId());
                }*/
//                item.setPauseReason(PublicoConfig.Reason.NO_PAUSE);
                MjDownloadSDK.start(context, item.getHttpUrl(), item.getFilePathName(), item.getCacheType());
                break;

            default:
                break;
        }

    }


     public void updateApk(Context context, DownloadItem downloadItemInfo){
         for(int i = 0; i < waittingInfos.size();i++){
             if(waittingInfos.get(i).getAid().equals(downloadItemInfo.getAid())){
                 waittingInfos.remove(i);
                 i--;
             }
         }
         DownloadResInfoBusiness.deleteDownloadResInfoFile(downloadItemInfo);//删除资源信息文件
         DownloadResBusiness.deleteDownloadResFile(downloadItemInfo);//删除资源文件
         mDownLoadItem = downloadItemInfo;
        NativeCallbackInfo info = MjDownloadSDK.queryInfo(downloadItemInfo.getHttpUrl(), downloadItemInfo.getFilePathName(), PublicoConfig.CacheType.LOCAL_NETWORK_CACHE);
       if(info!=null){
           MjDownloadSDK.delete(info.getJobID());
       }

         MjDownloadSDK.start(context, downloadItemInfo.getHttpUrl(), downloadItemInfo.getFilePathName(), downloadItemInfo.getCacheType());
    }




    /**
     * 暂停下载
     */
    public void pauseDownload(Context context, DownloadItem downloadItemInfo) {
        LogHelper.e(TAG, "pauseDownload.state==" + downloadItemInfo.getDownloadState() + "==title==" + downloadItemInfo.getTitle());
        MjDownloadSDK.pause(downloadItemInfo.getId());
        downloadItemInfo.setDownloadStatus(MjDownloadStatus.ABORT);
        waittingToRun(context);
    }

    /**
     * 暂停下载
     */
    public void unityPauseDownload(Context context, DownloadItem downloadItemInfo) {
        NativeCallbackInfo info = MjDownloadSDK.queryInfo(downloadItemInfo.getHttpUrl(), downloadItemInfo.getFilePathName(), PublicoConfig.CacheType.LOCAL_NETWORK_CACHE);
        if (null != info) {
            MjDownloadSDK.pause(info.getJobID());
        }
        downloadItemInfo.setDownloadStatus(MjDownloadStatus.ABORT);

        waittingToRun(context);
    }


    /**
     * 删除下载
     */
    public synchronized void deleteDownload(Context context, DownloadItem item) {
        LogHelper.e("infossss", "=======deleteDownload=======" + item.getTitle() + "===state===" + item.getDownloadState());
        if (item.getId() == 0) {
            List<DownloadItem> list = new ArrayList<>();
            list.clear();
            list.addAll(mDownLoadings);
            list.addAll(waittingInfos);
            for (DownloadItem in : list) {
                if (in.getAid().equals(item.getAid())) {
                    item.setId(in.getId());
                }
            }
        }
        LogHelper.e("infossss", "id==" + item.getId() + "===state==" + item.getDownloadState());
        MjDownloadSDK.delete(item.getId());
        if (item.getDownloadState() == MjDownloadStatus.DOWNLOADING) {
            for (int i = 0; i < mDownLoadings.size(); i++) {
                if (mDownLoadings.get(i).getAid().equals(item.getAid())) {
                    mDownLoadings.remove(i);
                    i--;
                }
            }
//            FileTools.writeDownLoadingFile(mDownLoadings);
        } else {
            for (int i = 0; i < mDownLoadings.size(); i++) {
                if (mDownLoadings.get(i).getAid().equals(item.getAid())) {
                    mDownLoadings.remove(i);
                    i--;
                }
            }
//            FileTools.writeDownLoadingFile(mDownLoadings);
            for (int i = 0; i < waittingInfos.size(); i++) {
                if (waittingInfos.get(i).getAid().equals(item.getAid())) {
                    waittingInfos.remove(i);
                    i--;
                }
            }
//            FileTools.writeWaittingFile(waittingInfos);

        }
        FileTools.writeWaittingFile(waittingInfos);
        FileTools.writeDownLoadingFile(mDownLoadings);
        waittingToRun(context);
    }

    public void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }

    /**
     * 查询item状态
     */
    public DownloadItem queryInfo(DownloadItem downloadItem, boolean isQueryState) {
        if (downloadItem == null) {
            return null;
        }
        NativeCallbackInfo info = MjDownloadSDK.queryInfo(downloadItem.getHttpUrl(), downloadItem.getFilePathName(), PublicoConfig.CacheType.LOCAL_NETWORK_CACHE);
        if (info == null || info.getJobID() == 0) {//未下载、等待队列中
            //查询是否在等待队列中
            if (downloadItem.getDownloadState() == MjDownloadStatus.WAITING) {
                downloadItem.setDownloadStatus(MjDownloadStatus.WAITING);
            } else if (downloadItem.getDownloadState() == MjDownloadStatus.COMPLETE) {
                downloadItem.setDownloadStatus(MjDownloadStatus.COMPLETE);
            } else {
                downloadItem.setDownloadStatus(MjDownloadStatus.DEFAULT);
            }
        } else {
            downloadItem.setId(info.getJobID());
            if (isQueryState) {
                downloadItem.setDownloadStatus(info.getStatus());
            }
            if (downloadItem.getDownloadState() != MjDownloadStatus.ERROR
                    && downloadItem.getDownloadState() != MjDownloadStatus.CONNECTING
                    ) {
                LogHelper.e("testss", "===state=====" + downloadItem.getDownloadState() + "==info.progress===" + info.getProgress());
                downloadItem.setProgress(Double.valueOf(info.getProgress() * 100).intValue());
                downloadItem.setTotalLen(info.getTotalLen());
                downloadItem.setOffset(info.getOffset());
            }

        }
        return downloadItem;
    }

    /**
     * 加入等待队列
     */
    private synchronized void addWaitting(DownloadItem downItemInfo) {
        if (!isWaiting(downItemInfo)) {
            LogHelper.e("infosss","info=="+downItemInfo.getTitle()+"===state=="+downItemInfo.getDownloadState());
            waittingInfos.add(downItemInfo);
            FileTools.writeWaittingFile(waittingInfos);
        }
    }

    /**
     * @param downItem
     * @return
     */
    private synchronized boolean isDownloading(DownloadItem downItem) {
        for (int i = 0; i < mDownLoadings.size(); i++) {
            if (mDownLoadings.get(i).getAid().equals(downItem.getAid())) {
                mDownLoadings.get(i).setDownloadStatus(downItem.getDownloadState());
                return true;
            }
        }
        return false;
    }

    /**
     * item是否在等待集合中
     *
     * @param downItem
     * @return
     */
    private boolean isWaiting(DownloadItem downItem) {
        for (int i = 0; i < waittingInfos.size(); i++) {
            LogHelper.e("infosss","isWatting==="+waittingInfos.get(i).getTitle()+"===state=="+waittingInfos.get(i).getDownloadState()+"=aid=="+waittingInfos.get(i).getAid()+"===downaid=="+downItem.getAid());
            if (waittingInfos.get(i).getAid().equals(downItem.getAid())) {
                waittingInfos.get(i).setDownloadStatus(downItem.getDownloadState());
                return true;
            }
        }

        return false;
    }


    /**
     * 等待队列开始下载
     */
    public synchronized void waittingToRun(Context context) {
        LogHelper.e("setDownloadStatus", "wattinginfos.size===" + waittingInfos.size());
        if (!waittingInfos.isEmpty() /*&& !PluginDownloadBusiness.getmInstance().getDownloadingFlag()*/) {
            sortByDownLoadStatusBigtoSmall(waittingInfos);
            List<DownloadItem> list = new ArrayList<>();
            List<DownloadItem> wattingList = new ArrayList<>();
            for (DownloadItem in : waittingInfos) {
                if (in.getDownloadState() == MjDownloadStatus.ERROR) {
                    list.add(in);
                }

                if (in.getDownloadState() == MjDownloadStatus.WAITING) {
                    wattingList.add(in);
                }
            }
            sortCreateTimeByDownLoadItems(wattingList);
            DownloadItem info = waittingInfos.get(0);

            if (!wattingList.isEmpty()) {
                info = wattingList.get(0);
            }

            if (info.getDownloadState() == MjDownloadStatus.ABORT && !list.isEmpty()) {
                info = list.get(0);
            }
            LogHelper.e("setDownloadStatus", "info.title==" + info.getTitle() + "===state====" + info.getDownloadState());
            if (info.getDownloadState() == MjDownloadStatus.WAITING
                    || info.getDownloadState() == MjDownloadStatus.DEFAULT
                    || info.getDownloadState() == MjDownloadStatus.CONNECTING) {
                mDownLoadItem = info;
                MjDownloadSDK.start(context, info.getHttpUrl(), info.getFilePathName(), PublicoConfig.CacheType.LOCAL_NETWORK_CACHE);
            }
            FileTools.writeWaittingFile(waittingInfos);
        }
    }

    public void getAllData() {
        getDownloadInfoData();
    }

    private void getDownloadInfoData() {//同步数据
        LocalDownloadProxy.getInstance().addProxyRunnable(new LocalDownloadProxy.ProxyRunnable() {
            @Override
            public void run() {
                FileTools.readDownLoadingFile(true);
                FileTools.readWaittingFile(true);

//                Log.w("px","getDownloadInfoData11     "+waittingInfos.size());
//                DownloadResInfoSaveBusiness.getDownloadInfoData(new DownloadResInfoSaveBusiness.DownloadInfoCallback() {
//                    @Override
//                    public void downloadInfoCallback(final TreeMap<Long, String> downloadInfoMap) {
//                                if (downloadInfoMap != null && downloadInfoMap.size() > 0) {
//                                    List<DownloadItem> tempDownloadList = new ArrayList<DownloadItem>();
//                                    Iterator iter = downloadInfoMap.entrySet().iterator();
//                                    while (iter.hasNext()) {
//                                        Map.Entry entry = (Map.Entry) iter.next();
//                                        try {
//                                            String json = (String) entry.getValue();
//                                            DownloadItem item = DownloadItemUtil.createDownloadItem(json);
//                                            tempDownloadList.add(item);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    if (tempDownloadList.size() > 0) {
//                                        for(int i = 0; i < waittingInfos.size();i++){
//                                            DownloadItem item = waittingInfos.get(0);
//                                            Log.w("px","item  "+item.getDownloadState());
//                                            boolean isContain = false;
//                                            for(DownloadItem in : tempDownloadList){
//                                                if(in.getAid().equals(item.getAid())){
//                                                    item.setDownloadStatus(MjDownloadStatus.COMPLETE);
//                                                    isContain = true;
//                                                }
//                                            }
//                                            LogHelper.e("infoss","title==="+item.getTitle()+"==isContain=="+isContain);
//                                            if(!isContain){
//                                                waittingInfos.remove(i);
//                                                i--;
//                                            }
//                                        }
//
//                                        for(DownloadItem item : mDownLoadings){
//                                            for(DownloadItem in : tempDownloadList){
//                                                if(in.getAid().equals(item.getAid())){
//                                                    item.setDownloadStatus(MjDownloadStatus.COMPLETE);
//                                                }
//                                            }
//                                        }
//
//                                    }
//                                }
//                        Log.w("px","getDownloadInfoData22     "+waittingInfos.size());
//                            }
//                        });
            }
        });

    }


    /**
     * 获取下载中的集合
     *
     * @return
     */
    public ArrayList<DownloadItem> getAllDownLoadings(Context context) {
        ArrayList<DownloadItem> downLoadItems = new ArrayList<>();
        downLoadItems.clear();

        downLoadItems.addAll(mDownLoadings);
        downLoadItems.addAll(waittingInfos);
   
            sortCreateTimeByDownLoadItems(downLoadItems);


        for (DownloadItem downItem : downLoadItems) {
            if (downItem != null && downItem.getTotalLen() == 0) {
                queryInfo(downItem, false);
            }
          LogHelper.e("dududu","downItem===title------"+downItem.getTitle()+"==status==="+downItem.getDownloadState()+"===reason=="+downItem.getPauseReason()+"===createTime=="+downItem.getCreateTime()+"==progress=="+downItem.getProgress());
        }
        return downLoadItems;
    }

    public synchronized boolean isContainsDownLoad(List<DownloadItem> downloadItemList, DownloadItem downloadItem) {
        for (int i = 0; i < downloadItemList.size(); i++) {
            if (downloadItem.getAid().equals(downloadItemList.get(i).getAid())) {
                return true;
            }
        }

        return false;
    }

    private synchronized void deleteWatting(DownloadItem item) {
        for (int i = 0; i < waittingInfos.size(); i++) {
            if (item.getAid().equals(waittingInfos.get(i).getAid())) {
                waittingInfos.remove(i);
                i--;
            }
        }
    }

    /**
     * 获取下载中的集合
     *
     * @return
     */
    public ArrayList<DownloadItem> getAllDownLoadsByState(Context context, int state, boolean unRevert) {
        ArrayList<DownloadItem> loadings = new ArrayList<>();
        loadings.clear();
        loadings.addAll(mDownLoadings);
        loadings.addAll(waittingInfos);

        ArrayList<DownloadItem> downLoadItemByStatus = new ArrayList<>();
        downLoadItemByStatus.clear();
        for (DownloadItem in : loadings) {
            DownloadItem downloadItem = queryInfo(in, false);
            if (unRevert) {
                if (downloadItem.getDownloadState() == state) {
                    if (!isContainsDownLoad(downLoadItemByStatus, downloadItem)) {
                        downLoadItemByStatus.add(downloadItem);
                    }

                }
            } else {
                if (downloadItem.getDownloadState() != state) {
                    if (!isContainsDownLoad(downLoadItemByStatus, downloadItem)) {
                        downLoadItemByStatus.add(downloadItem);
                    }
                }
            }
        }


            sortCreateTimeByDownLoadItems(downLoadItemByStatus);


        for (DownloadItem test : downLoadItemByStatus) {
            if(null == test){
                continue;
            }
            LogHelper.e("dududu", "aid----" + test.getAid() + "---title---" + test.getTitle() + "---size---" + downLoadItemByStatus.size() + "===state====" + test.getDownloadState() + "==reason==" + test.getPauseReason());
        }
        return downLoadItemByStatus;
    }


    public void sortCreateTimeByDownLoadItems(List<DownloadItem> downloadItems) {
        Collections.sort(downloadItems, new Comparator<DownloadItem>() {
            @Override
            public int compare(DownloadItem p1, DownloadItem p2) {
                if (p1 == null || p2 == null) {
                    return 0;
                }
                return (p1.getCreateTime() - p2.getCreateTime()) == 0 ? 0 : (int) (p1.getCreateTime() - p2.getCreateTime());
            }
        });
    }

    public   void sortFinishTimeByDownLoadItems(List<DownloadItem> downloadItems) {
        Collections.sort(downloadItems, new Comparator<DownloadItem>() {
            @Override
            public int compare(DownloadItem p1, DownloadItem p2) {
                if(p1 == null || p2 == null){
                    return 0;
                }
                return (p2.getFinishTime() - p1.getFinishTime()) == 0 ? 0 : (int) (p2.getFinishTime() - p1.getFinishTime());
            }
        });
    }
    private  void sortByDownLoadStatusBigtoSmall(List<DownloadItem> downloadItems) {
        Collections.sort(downloadItems, new Comparator<DownloadItem>() {
            @Override
            public int compare(DownloadItem p1, DownloadItem p2) {
                if (p1 == null || p2 == null) {
                    return 0;
                }
                return (p2.getDownloadState() - p1.getDownloadState()) == 0 ? 0 : (p2.getDownloadState() - p1.getDownloadState());
            }
        });
    }

    private void sortByDownLoadStatusSmallloBig(List<DownloadItem> downloadItems) {
        Collections.sort(downloadItems, new Comparator<DownloadItem>() {
            @Override
            public int compare(DownloadItem p1, DownloadItem p2) {
                if (p1 == null || p2 == null) {
                    return 0;
                }
                return (p1.getDownloadState() - p2.getDownloadState()) == 0 ? 0 : (p1.getDownloadState() - p2.getDownloadState());
            }
        });
    }
	

    /**
     * 获取下载路径 漫游解压之前的
     *
     * @param item
     * @return
     */
    public String getDownloadPath(DownloadItem item) {
        return new File(item.getFileDir(), item.getTitle() + item.getSite()).getAbsolutePath();
    }

    /**
     * 获取解压之后的位置
     *
     * @param item
     * @return
     */
    public String getUnzipPath(DownloadItem item) {
        return item.getFileDir() + "/" + item.getAid();
    }


    /**
     * 设置下载中的数据
     *
     * @param mDownLoading
     */
    public void setDownLoadDate(ArrayList<DownloadItem> mDownLoading) {
        if (mDownLoading != null && mDownLoading.size() > 0) {
            mDownLoadings.clear();
            mDownLoadings.addAll(mDownLoading);
            for(DownloadItem in : mDownLoading){
                LogHelper.e("下载文件中有的数据","title=="+in.getTitle()+"==state=="+in.getDownloadState());
            }
        }

    }

    /**
     * 设置等待中的数据
     *
     * @param waittingInfo
     */
    public void setWaittingDate(ArrayList<DownloadItem> waittingInfo) {
        if (waittingInfo != null && waittingInfo.size() > 0) {
            waittingInfos.clear();
            waittingInfos.addAll(waittingInfo);
            for(DownloadItem in : waittingInfos){
                LogHelper.e("等待文件中有的数据","title=="+in.getTitle()+"==state=="+in.getDownloadState());
            }
        }
    }

    public void pauseAllDownload(Context context) {
        ArrayList<DownloadItem> allDownLoadings = getAllDownLoadings(context);
        for (DownloadItem downItem : allDownLoadings) {
            if(downItem == null){
                continue;
            }
            if (downItem.getDownloadState() == MjDownloadStatus.DOWNLOADING) {
                pauseDownloading(downItem);
            }
        }
    }

    /**
     * 开启非手动暂停的任务
     *
     * @param context
     */
    public void startAllDownload(Context context) {
        ArrayList<DownloadItem> allDownLoadings = getAllDownLoadings(context);
        for(DownloadItem item: mDownLoadings){
            LogHelper.e("px","titleing==="+item.getTitle()+"==state=="+item.getDownloadState());
        }
        for(DownloadItem in : waittingInfos){
            LogHelper.e("px","titlewait-----"+in.getTitle()+"-----state=="+in.getDownloadState());
        }
        if (allDownLoadings != null && !allDownLoadings.isEmpty()) {
            for (DownloadItem downItem : allDownLoadings) {
                if(downItem == null){
                    continue;
                }

                LogHelper.e("px", "state===" + downItem.getDownloadState() + "===reason==" + downItem.getPauseReason() + "==title====" + downItem.getTitle() + "==progress==" + downItem.getProgress() + "===total==" + downItem.getTotalLen() + "===offset==" + downItem.getOffset());

                if ((downItem.getDownloadState() == MjDownloadStatus.ERROR
                        || downItem.getDownloadState() == MjDownloadStatus.ABORT)
                        && downItem.getPauseReason() == PublicoConfig.Reason.LOCAL_PAUSE) {
                    if (!isDownloading(downItem)) {
                        mDownLoadings.add(downItem);
                        deleteWatting(downItem);
                    }
                    MjDownloadSDK.start(context, downItem.getHttpUrl(), downItem.getFilePathName(), downItem.getCacheType());
                } else if (downItem.getDownloadState() == MjDownloadStatus.DOWNLOADING
                        || downItem.getDownloadState() == MjDownloadStatus.CONNECTING
                        || downItem.getDownloadState() == MjDownloadStatus.DEFAULT
                        ) {//是否需要再次处理下
                    MjDownloadSDK.start(context, downItem.getHttpUrl(), downItem.getFilePathName(), downItem.getCacheType());
                }
            }
        }
    }

    /**
     * 开启网络错误的所有item
     *
     * @param context
     */
    public void startAllNetErrorDownload(Context context) {
        ArrayList<DownloadItem> allDownLoadings = getAllDownLoadings(context);
        if (allDownLoadings != null && allDownLoadings.size() > 0) {
            for (DownloadItem downItem : allDownLoadings) {
                if (downItem.getDownloadState() == MjDownloadStatus.ERROR
                        && downItem.getDownloadErrorCode() == MjDownloadErrorCode.NETWORK_ERROR) {
                    startDownload(context, downItem);
                }
            }
        }
    }


    /**
     * errorCode转中文
     *
     * @param errorCode
     */
    public String translateErrorCode(long errorCode) {
        String errorStr = "";
        if (errorCode == MjDownloadErrorCode.START_ERROR) {
            errorStr = "启动失败";
        } else if (errorCode == MjDownloadErrorCode.NETWORK_ERROR) {
            errorStr = "网络异常(NETWORK_ERROR)";
        } else if (errorCode == MjDownloadErrorCode.NEED_WAIT) {
            errorStr = "下载队列满(NEED_WAIT)";
        } else if (errorCode == MjDownloadErrorCode.JOB_EXIST) {
            errorStr = "已经在下载队列(JOB_EXIST)";
        } else if (errorCode == MjDownloadErrorCode.JOB_NOTFOUND) {
            errorStr = "资源未找到(JOB_NOTFOUND)";
        } else if (errorCode == MjDownloadErrorCode.INVALID_HANDLE) {
            errorStr = "INVALID_HANDLE";
        } else if (errorCode == MjDownloadErrorCode.NO_MEMORY) {
            errorStr = "NO_MEMORY";
        } else if (errorCode == MjDownloadErrorCode.FILE_CACHE_LACK) {
            errorStr = "文件缓存不足(FILE_CACHE_LACK)";
        } else if (errorCode == MjDownloadErrorCode.MEMORY_CACHE_LACK) {
            errorStr = "内存缓存不足(MEMORY_CACHE_LACK)";
        } else if (errorCode == MjDownloadErrorCode.FILE_TOO_BIG) {
            errorStr = "文件大小超出限制(FILE_TOO_BIG)";
        } else if (errorCode == MjDownloadErrorCode.OPENFILE_ERROR) {
            errorStr = "打开文件失败(OPENFILE_ERROR)";
        } else if (errorCode == MjDownloadErrorCode.WRITEFILE_ERROR) {
            errorStr = "写文件失败(WRITEFILE_ERROR)";
        } else if (errorCode == MjDownloadErrorCode.DB_INFO_CREATE_ERROR) {
            errorStr = "创建数据库失败(DB_INFO_CREATE_ERROR)";
        } else if (errorCode == MjDownloadErrorCode.NETWORK_INIT_FAILED) {
            errorStr = "网络模组创建失败(NETWORK_INIT_FAILED)";
        } else if (errorCode == MjDownloadErrorCode.THREAD_ERROR) {
            errorStr = "线程错误(THREAD_ERROR)";
        } else if (errorCode == MjDownloadErrorCode.DB_INFO_QUERY_ERROR) {
            errorStr = "查询数据库失败(DB_INFO_QUERY_ERROR)";
        } else if (errorCode == MjDownloadErrorCode.FILE_NOT_EXIST) {
            errorStr = "未找到已下载文件(FILE_NOT_EXIST)";
        } else if (errorCode == MjDownloadErrorCode.URL_INVALID) {
            errorStr = "URL无法解析(URL_INVALID)";
        } else if (errorCode == MjDownloadErrorCode.NO_ERROR) {
            errorStr = "NO_ERROR";
        }
        return errorStr;
    }


    public void clearCache() {
        MjDownloadSDK.cleanCache();
    }


    private class BusinessHandler extends Handler {
        public BusinessHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            DownloadItem item ;
            switch (what) {
                case HANDLER_WHAT_CALLBACK:
                    item = (DownloadItem) msg.obj;
                    updateData(item);
                    break;
                case HANDLER_WHAT_START_CALLBACK:

                    break;
                default:
                    break;
            }
        }
    }

    public void stopAllDownload(Context context){
        MjDownloadSDK.stopAll(context);
    }
    public void deleteUninstallItem(String packageName){
        for(int i = 0; i< waittingInfos.size();i++){
            if(waittingInfos.get(i).getPackageName().equals(packageName)){
                LogHelper.e("infossss","pacakgename=="+waittingInfos.get(i).getPackageName()+"===name=="+packageName);
                waittingInfos.remove(i);
                i--;
            }
        }
        FileTools.writeWaittingFile(waittingInfos);
    }


    private synchronized void updateData(DownloadItem item){
        if(item == null){
            return;
        }
        int status = item.getDownloadState();
        int progressInt = item.getProgress();
        long errorCode = item.getDownloadErrorCode();
        long id = item.getId();
        for (int i = 0; i < mDownLoadings.size(); i++) {
            DownloadItem downloadItemInfo = mDownLoadings.get(i);
            if (downloadItemInfo != null) {
                if (downloadItemInfo.getId() == id) {
                    //写文件判断
                    if (downloadItemInfo.getProgress() != progressInt || downloadItemInfo.getDownloadState() != status
                            || downloadItemInfo.getDownloadState() == MjDownloadStatus.ABORT) {

                        if (status == MjDownloadStatus.CONNECTING) {
                            FileTools.writeDownLoadingFile(mDownLoadings);
                        }
                        if (status == MjDownloadStatus.DOWNLOADING) {
                            downloadItemInfo.setProgress(progressInt);
                        }

                        if(status == MjDownloadStatus.COMPLETE){//用来排序
                            downloadItemInfo.setFinishTime(System.currentTimeMillis());
                        }

                        if (status != MjDownloadStatus.CONNECTING && status != MjDownloadStatus.DEFAULT) {
                            downloadItemInfo.setDownloadStatus(status);
                        }
                        LogHelper.e("setDownloadStatus","title=="+downloadItemInfo.getTitle()+"==status="+status+"==sateeeee=="+downloadItemInfo.getDownloadState());
                        downloadItemInfo.setDownloadErrorCode(errorCode);
                        if (downloadItemInfo.getDownloadState() == MjDownloadStatus.DOWNLOADING) {
                            if (isContainsDownLoad(waittingInfos, downloadItemInfo)) {
                                waittingInfos.remove(downloadItemInfo);
                            }
                        } else {
                            if (status == MjDownloadStatus.ABORT
                                    || status == MjDownloadStatus.COMPLETE
                                    || status == MjDownloadStatus.ERROR
                                    || status == MjDownloadStatus.WAITING) {
                                if (!isContainsDownLoad(waittingInfos, downloadItemInfo)) {
                                    waittingInfos.add(downloadItemInfo);
                                }
                                mDownLoadings.remove(i);
                                i--;
                            }

                        }

                        DownloadObserver.getInstance().notifyDownLoadCallBack(downloadItemInfo);
                        changeStatus(downloadItemInfo);
                        if (status == MjDownloadStatus.ABORT) {
                            FileTools.writeDownLoadingFile(mDownLoadings);
                        }
//

                    }
                }
            }
        }
    }
}
