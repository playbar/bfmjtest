package com.baofeng.mj.business.pluginbusiness;

import android.util.Log;

import com.baofeng.mj.bean.ApkItem;
import com.baofeng.mj.bean.PluginDownloadInfo;
import com.baofeng.mj.bean.PluginRequestBean;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.sqlitebusiness.SqliteManager;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.PluginListApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.baofeng.mj.util.threadutil.SqliteProxy;
import com.baofeng.mojing.MojingDownloader;
import com.baofeng.mojing.sdk.download.utils.MjDownloadErrorCode;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghongfang on 2017/3/1.
 * 插件下载管理类   (处理插件下载，更新)
 */
public class PluginDownloadBusiness  implements MojingDownloader.DownloadCallback {
    private final int HANDLER_WHAT_CALLBACK = 0;
    private final int HANDLER_WHAT_START_CALLBACK = 1;
    private List<ApkItem> NeedDownloadItems = new ArrayList<ApkItem>();
    private List<PluginDownloadInfo> DownloadItems = new ArrayList<PluginDownloadInfo>();
    private static PluginDownloadBusiness mInstance;
    /* 标示是否有下载中的插件*/
    private boolean mDownloadingFlag = false;

    private List<IPluginDownloadListener> IObservers = new ArrayList<>();

    private PluginDownloadBusiness(){
    }

    /**
     * 绑定添加监听者
     * @param listener
     */
    public void onBindListener(IPluginDownloadListener listener){
        IObservers.add(listener);
    }

    /**
     * 解除绑定
     * @param listener
     */
    public void unBindListener(IPluginDownloadListener listener){
        if(IObservers!=null&&IObservers.contains(listener)){
            IObservers.remove(listener);
        }
    }


    public static PluginDownloadBusiness getmInstance(){
        if(mInstance==null){
            mInstance = new PluginDownloadBusiness();
        }

        return mInstance;
    }

    /**
     * 是否有插件下载
     * @return
     */
    public boolean getDownloadingFlag(){
        return mDownloadingFlag;
    }

    /**
     * 检测网络并请求网络插件列表数据
     */
    public void checkRequestPluginData(){
        if(!NetworkUtil.isNetworkConnected(BaseApplication.getInstance())||!NetworkUtil.canPlayAndDownload()){
            return;
        }
        requestPluginData(null);
    }

    /**
     * 请求网络插件列表数据
     */
    public void requestPluginData(final PluginUIBusiness.IqueryPluginListener listener){

        new PluginListApi().getPluginData(new ApiCallBack<PluginRequestBean>() {

            @Override
            public void onSuccess(PluginRequestBean result) {
//                delete();
                if (result != null) {
                    if (result.isStatus()) {
                         checkNeedDownload(result.getData());
                        if(listener!=null){
                            listener.callback(result);
                        }
                        return;
                    }
                }
                if(listener!=null){
                    listener.callback(result);
                }
//                TestDownload();

            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
    }

    private void delete(){
        SqliteManager.getInstance().deleteFromPlugin("1");
        SqliteManager.getInstance().closeSQLiteDatabase();
        PluginOperateBusiness.getInstance().doUninstall("com.rgbvr.show");
    }

    private void TestDownload(){
//        ApkItem item = new ApkItem();
//        item.setPlugin_version_name("2");
//        item.setPlugin_id("1");
//        item.setDownload_url("http://dl.mojing.cn/mobile/14891111931009595346.apk");
//        item.setVersion_name("0");
//        item.setPlugin_name("小花秀.apk");
//        item.setUpgrade_type("1");
//        item.setApk_name("com.rgbvr.show");
//        List<ApkItem> list = new ArrayList<>();
//        list.add(item);
//        checkNeedDownload(list);
       final PluginDownloadInfo info = new PluginDownloadInfo();
        info.setApk_name("com.rgbvr.show");
        info.setPlugin_id("1");
        info.setPlugin_name("xhx_2.2.0.apk");
        info.setPlugin_version_name("9999");
        info.setVersion_name("4.00.0000");
        info.setPath(DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_plugin)+"/"+info.getPlugin_name());
        ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                PluginOperateBusiness.getInstance().doInstall(info);
            }
        });

    }

    /**
     * 检查需要下载的插件列表
     * @param data
     */
    private void checkNeedDownload(final List<ApkItem> data){
        if(data==null||data.size()<=0)
            return;
        if(NeedDownloadItems!=null) {
            NeedDownloadItems.clear();
        }
        new Thread("checkDownload"){
            @Override
            public void run() {
                //获取已安装 已下载 下载中的插件列表

                List<PluginDownloadInfo> mPluginInfos =  SqliteProxy.getInstance().addProxyExecute(new SingleThreadProxy.ProxyExecute<List<PluginDownloadInfo>>() {
                    @Override
                    public List<PluginDownloadInfo> execute() {
                        List<PluginDownloadInfo> infos= SqliteManager.getInstance().queryPluginDownloaded();
                        SqliteManager.getInstance().closeSQLiteDatabase();
                        return infos;
                    }
                });

                for(ApkItem item:data){
                    item.setPlugin_version_name((Integer.parseInt(item.getPlugin_version_name())+1)+"");
                    boolean isindstalled = false;
                    for (int i=0;i<mPluginInfos.size();i++){
                        PluginDownloadInfo item1 = mPluginInfos.get(i);
                        //包名相同时并且网络上插件版本较低或者与已安装相等时不需要下载或升级
                        if(item1.getApk_name().equals(item.getApk_name())&& ApkUtil.compareVersion(item.getPlugin_version_name(),item1.getPlugin_version_name())<=0){
                            isindstalled = true;
                            break;
                        }

                    }
                    if(!isindstalled) {
                        NeedDownloadItems.add(item);
                    }

                }
                if(NeedDownloadItems!=null&&NeedDownloadItems.size()>0){
                    DownloadItems.clear();
                    DownloadItems = changeDownloadInfo(NeedDownloadItems);

                    addTaskList(DownloadItems);
                }

            }
        }.start();
    }



    @Override
    public void callback(long id, int status, double progress, long errorCode) {
      final   PluginDownloadInfo info = new PluginDownloadInfo();
        info.setId(id);
        info.setStatus(status);
        info.setProgress(progress);
        info.setErrorCode(errorCode);

        ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                handleMessage(info,HANDLER_WHAT_CALLBACK);
            }
        });


    }

    @Override
    public void startCallback(long id, String url, String path, int status) {
       final PluginDownloadInfo info = new PluginDownloadInfo();
        info.setId(id);
        info.setStatus(status);
        info.setDownload_url(url);
        info.setPath(path);
        if (status == MjDownloadStatus.ERROR) {
            info.setErrorCode(MjDownloadErrorCode.START_ERROR);
        }
        ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                handleMessage(info,HANDLER_WHAT_START_CALLBACK);
            }
        });

    }


    /**
     * 网络上的ApkItem数据转成下载需要的PluginDownloadInfo数据模型
     * @param list
     * @return
     */
    public List<PluginDownloadInfo> changeDownloadInfo(List<ApkItem> list){
        if(list==null||list.size()<=0) {
            return null;
        }
        List<PluginDownloadInfo> infos = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            ApkItem item = list.get(i);
            infos.add(getPluginInfoByApkItem(item));
        }
        return infos;
    }

    public void stopDownload(){
        if(DownloadItems!=null&&DownloadItems.size()>0){
            for (int i= 0;i<DownloadItems.size();i++){
                PluginDownloadInfo downloadInfo = DownloadItems.get(i);
                if(downloadInfo.getStatus()!= MjDownloadStatus.COMPLETE&&downloadInfo.getStatus()!=PluginDownloadInfo.Installed&&downloadInfo.getStatus()!=PluginDownloadInfo.Installing){
                    MjDownloadSDK.pause(downloadInfo.getId());
                }
            }
        }
    }
    public void updateDownload(){
        boolean mflag = false;
        if(DownloadItems!=null&&DownloadItems.size()>0){
            for (int i= 0;i<DownloadItems.size();i++){
                PluginDownloadInfo downloadInfo = DownloadItems.get(i);
                if(downloadInfo.getStatus()!= MjDownloadStatus.COMPLETE&&downloadInfo.getStatus()!=PluginDownloadInfo.Installed&&downloadInfo.getStatus()!=PluginDownloadInfo.Installing){
                    mflag = true;
                    addTask(downloadInfo);
                }
            }
        }
        if(mflag==true){
           DownloadUtils.getInstance().stopDownloading(BaseApplication.getInstance());
        }
    }
    /**
     * 添加 下载任务列表
     */
    private synchronized void addTaskList(List<PluginDownloadInfo> infos){
        if(infos==null||infos.size()<=0)
            return;
          onResume();
        //优先下载插件  先暂停其他资源下载
         DownloadUtils.getInstance().stopDownloading(BaseApplication.getInstance());
          for (int i=0;i<infos.size();i++){
              addTask(infos.get(i));
          }
    }

    /**
     * 添加一个下载任务
     */
    private synchronized void addTask(PluginDownloadInfo downloadInfo){
        if(downloadInfo==null)
            return;
        mDownloadingFlag = true;
        MjDownloadSDK.start(BaseApplication.getInstance(), downloadInfo.getDownload_url(), downloadInfo.getPath(), downloadInfo.getCacheType());
    }

    /**
     * 删除一个下载任务
     */
    public synchronized void deleteTask(PluginDownloadInfo downloadInfo){
        if (downloadInfo == null)
            return;
        MjDownloadSDK.delete(downloadInfo.getId());
        downloadInfo.setStatus(MjDownloadStatus.DEFAULT);
    }

    /**
     * 清除缓存
     */
    public void cleanCache() {
        MjDownloadSDK.cleanCache();
    }

    public void onPause() {
        MjDownloadSDK.removeCallback(this);
    }

    public void onResume() {
        MjDownloadSDK.addCallback(this);
    }

    /**
     * PluginDownloadInfo 对象转换ApkItem
     * @param info
     * @return
     */
    private ApkItem getApkItemByDownloadInfo(PluginDownloadInfo info){
        if(info==null)
            return null;
        ApkItem item = new ApkItem();
        item.setPlugin_name(info.getPlugin_name());
        item.setVersion_name(info.getVersion_name());
        item.setDownload_url(info.getDownload_url());
        item.setApk_name(info.getApk_name());
        item.setPlugin_id(info.getPlugin_id());
        item.setApkfile(info.getPath());
        item.setPlugin_version_name(info.getPlugin_version_name());
        return item;
    }
    /**
     *ApkItem  对象转换 PluginDownloadInfo
     * @param item
     * @return
     */
    private PluginDownloadInfo getPluginInfoByApkItem(ApkItem item){
        PluginDownloadInfo info = new PluginDownloadInfo();
        info.setPlugin_name(item.getPlugin_name());
        info.setDownload_url(item.getDownload_url());
        info.setPlugin_id(item.getPlugin_id());
        info.setPlugin_version_name(item.getPlugin_version_name());
        info.setUpgrade_type(item.getUpgrade_type());
        info.setApk_name(item.getApk_name());
        info.setVersion_name(item.getVersion_name());
        info.setPath(DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_plugin)+"/"+info.getPlugin_name());
        return info;
    }


    public boolean handleMessage(final PluginDownloadInfo info,int what) {
        switch (what) {
            case HANDLER_WHAT_CALLBACK:
                //删除任务时，底层会有一个中止的回调，特殊处理
//                if (!downloadingIds.contains(info.getId())) {
//                    return;
//                }
                for (int i=0;i<DownloadItems.size();i++) {
                    final PluginDownloadInfo downloadInfo = DownloadItems.get(i);
//                    Log.d("login","---HANDLER_WHAT_CALLBACK downloadInfo.id = "+downloadInfo.getId()+",info.getId() = "+info.getId()+",downloadurl = "+downloadInfo.getPath()+" infourl="+info.getPath());
                    if (downloadInfo.getId() == info.getId()) {

//                        Log.d("login","---HANDLER_WHAT_CALLBACK status = "+info.getStatus()+",downloadurl = "+info.getDownload_url());
                        downloadInfo.setStatus(info.getStatus());
                        downloadInfo.setProgress(info.getProgress());
                        downloadInfo.setErrorCode(info.getErrorCode());
                        if(info.getStatus()== MjDownloadStatus.COMPLETE){
//                            downloadInfo.setStatus(PluginDownloadInfo.Installing);

                            SqliteProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                                @Override
                                public void run() {
                                    SqliteManager.getInstance().addPlugin(downloadInfo);
                                    SqliteManager.getInstance().closeSQLiteDatabase();
                                }
                            });

                        }
//                        if(info.getStatus()== MjDownloadStatus.COMPLETE||info.getStatus()==MjDownloadStatus.ERROR||info.getStatus()==MjDownloadStatus.ABORT) {
                            notifyListener(downloadInfo.getStatus(), downloadInfo.getProgress(), downloadInfo.getPlugin_id());
//                        }
                    }
                }
                break;
            case HANDLER_WHAT_START_CALLBACK:

                for (int i=0;i<DownloadItems.size();i++) {
                  final   PluginDownloadInfo downloadInfo = DownloadItems.get(i);
                    if (downloadInfo.getDownload_url().equals(info.getDownload_url())
                            && downloadInfo.getPath() != null && downloadInfo.getPath().equals(info.getPath())) { //开始下载的回调根据url和path判断
                        if (info.getId() != 0 && (info.getStatus() == MjDownloadStatus.CONNECTING||info.getStatus() == MjDownloadStatus.DOWNLOADING)) {
                            downloadInfo.setId(info.getId());
                        }
                        downloadInfo.setStatus(info.getStatus());
                        downloadInfo.setErrorCode(info.getErrorCode());
                        downloadInfo.setProgress(info.getProgress());
                        SqliteProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                            @Override
                            public void run() {
                                PluginDownloadInfo info1 = SqliteManager.getInstance().getFromLocalPluginInfo(downloadInfo.getPlugin_id());
                                if(info1!=null&&info1.getStatus()==PluginDownloadInfo.Installed&&ApkUtil.compareVersion(downloadInfo.getPlugin_version_name(),info1.getPlugin_version_name())>0){//插件更新下载
                                  downloadInfo.setPlugin_upgrade(1);
                                }
                                SqliteManager.getInstance().addPlugin(downloadInfo);
                                SqliteManager.getInstance().closeSQLiteDatabase();
                            }
                        });

                        notifyListener(downloadInfo.getStatus(),downloadInfo.getProgress(),downloadInfo.getPlugin_id());

                    }
                }
                if(info.getStatus()==MjDownloadStatus.COMPLETE||info.getStatus()==MjDownloadStatus.ABORT||info.getStatus()==MjDownloadStatus.ERROR) {
                    checkDownloadedAll();
                }
                    break;
            default:
                break;
        }
        return false;
    }

    /**
     * 检查插件下载完后 自动启动下载资源
     */
    private void checkDownloadedAll(){
        if(DownloadItems!=null&&DownloadItems.size()>0){
            boolean all_complete  = true;
            for (int i = 0;i<DownloadItems.size();i++){
                PluginDownloadInfo info = DownloadItems.get(i);
                if(!(info.getStatus()==MjDownloadStatus.COMPLETE||info.getStatus()==MjDownloadStatus.ABORT||info.getStatus()==MjDownloadStatus.ERROR)){
                    all_complete = false;
                    break;
                }

            }
            if(all_complete){
                mDownloadingFlag = false;
                DownloadUtils.getInstance().startAllDownload(BaseApplication.getInstance());
            }
        }
    }


    public void notifyListener(int state,double progress,String pluginID){
        if(IObservers==null||IObservers.size()<=0)
            return;
        for (IPluginDownloadListener listener:IObservers){
            listener.onDownloadStatusChange(state,progress,pluginID);
        }
    }

    public interface IPluginDownloadListener{
        void  onDownloadStatusChange(int state,double progress,String pluginId);
    }
}
