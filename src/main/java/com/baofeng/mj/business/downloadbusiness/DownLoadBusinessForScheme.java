package com.baofeng.mj.business.downloadbusiness;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.netutil.GameApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;

import java.io.File;

/**
 * Created by liuchuanchi on 2016/7/11.
 * 下载业务（外部打开用）
 */
public class DownLoadBusinessForScheme {
    /**
     * 请求详情页数据，之后开始下载
     * @param activity 上下文
     * @param resType 资源类型
     * @param detailUrl 资源详情页url
     */
    public static void requestDetailInfo(final Activity activity, final int resType, final String detailUrl){
        switch (resType){
            case ResTypeUtil.res_type_game://游戏
            case ResTypeUtil.res_type_apply://应用
                new GameApi().getGameDetailInfo(BaseApplication.INSTANCE, detailUrl, new ApiCallBack<ResponseBaseBean<GameDetailBean>>() {
                    @Override
                    public void onSuccess(ResponseBaseBean<GameDetailBean> result) {
                        if (result != null) {
                            if (result.getStatus() == 0) {
                                if (result.getData() != null) {
                                    DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(result.getData(), detailUrl);//下载的实体类
                                    downloadJudge(activity, downloadItem);//下载判断
                                }
                            }
                        }
                    }
                });
                break;
            case ResTypeUtil.res_type_video://全景视频
            case ResTypeUtil.res_type_roaming://全景漫游
                new ChoicenessApi().getPanoramaDetailInfo(BaseApplication.INSTANCE, detailUrl, new ApiCallBack<ResponseBaseBean<PanoramaVideoBean>>() {
                    @Override
                    public void onSuccess(ResponseBaseBean<PanoramaVideoBean> result) {
                        if (result.getStatus() == 0) {
                            if (result.getData() != null) {
                                DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(result.getData(), detailUrl);//下载的实体类
                                downloadJudge(activity, downloadItem);//下载判断
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 下载判断
     */
    public static void downloadJudge(final Activity activity, DownloadItem downloadItem){
        File file = DownloadResBusiness.getDownloadResFile(downloadItem);//下载的资源文件
        if(ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())){//游戏或者应用
            String packageName = downloadItem.getPackageName();//游戏包名
            int versionCode = Integer.valueOf(downloadItem.getApkVersionCode());//游戏版本号
            int apkState = ApkUtil.checkApk(file, packageName, versionCode);

            if(apkState == ApkUtil.NEED_INSTALL && !getUninatllApkInfo(file.getAbsolutePath())){
                apkState = ApkUtil.NEED_DOWNLOAD;
            }

            switch (apkState){//apk状态
                case ApkUtil.NEED_DOWNLOAD://下载apk
                    downloadHandler(activity, downloadItem,false);
                    break;
                case ApkUtil.NEED_UPDATE://升级apk
                    downloadHandler(activity, downloadItem,true);
                    break;
                default:
                    break;
            }
        }else{//不是游戏
            if(!file.exists()){//资源文件不存在
                downloadHandler(activity, downloadItem,false);
            }
        }
    }

    /**
     * 下载处理
     */
    public static void downloadHandler(final Activity activity, DownloadItem downloadItem,boolean isUpate) {
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(downloadItem.getAid());// 获取下载中的DownloadItem
        if (downloadingItem != null) {// 如果是下载中
            if(MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()){
                DemoUtils.pauseDownload(activity, downloadItem);//暂停下载
            }else{
                if(isUpate){
                    if(MjDownloadStatus.ABORT == downloadingItem.getDownloadState()){
                        DemoUtils.startDownload(activity, downloadingItem);//继续下载
                    }else{
                        DownloadUtils.getInstance().updateApk(BaseApplication.INSTANCE,downloadItem);
                    }
                }else {
                    DemoUtils.startDownload(activity, downloadItem);//继续下载
                }

            }
        }else if (UserSpBusiness.getInstance().notLoginForDownload()) {// 未登录时，超过下载限制
            DownLoadBusiness.showLoginForDownloadDialog(activity);//提示登录再下载
        }
//            else if (needBuy(context, info)) {// 需要购买
//                toBuy(context, info);//开始购买
//            }
        else if(!NetworkUtil.networkEnable()){//无网络
            DownLoadBusiness.showNetworkErrorDialog(activity);
        }else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络下载
            DownLoadBusiness.showOpenGprsDialog(activity);// 提示WiFi不可用，是否开启gprs网络下载
        }else {
            DownLoadBusiness.downloadStart(downloadItem);//开始下载
        }
    }

    private   static boolean getUninatllApkInfo( String filePath) {
        boolean result = false;
        try {
            PackageManager pm = BaseApplication.INSTANCE.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
