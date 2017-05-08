package com.baofeng.mj.business.downloadbusiness;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SearchSpBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.LoginActivity;
import com.baofeng.mj.ui.activity.SearchActivity;
import com.baofeng.mj.ui.activity.SettingActivity;
import com.baofeng.mj.ui.dialog.GameOpenDialog;
import com.baofeng.mj.ui.dialog.LoginForDownloadDialog;
import com.baofeng.mj.ui.dialog.NetworkErrorDialog;
import com.baofeng.mj.ui.dialog.OpenGprsDialog;
import com.baofeng.mj.ui.dialog.StickGameDownloadDialog;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.UnZipUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.netutil.GameApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.threadutil.LocalDownloadProxy;
import com.baofeng.mj.util.viewutil.ShowUi;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mj.util.zxingutil.decoding.FinishListener;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.sina.weibo.sdk.api.share.Base;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author liuchuanchi
 * @description: 下载业务
 */
public abstract class DownLoadBusiness<T> implements View.OnClickListener {
    private Activity activity;
    private HashSet<TextView> btHashSet = new HashSet<TextView>();//下载按钮集合
    private long time = 0;
    public DownLoadBusiness(Activity mActivity) {
        activity = mActivity;
    }

    /**
     * 加入下载按钮（游戏用）
     *
     * @param bt_download  下载按钮
     * @param contentInfo  列表实体类
     * @param appExtraBean app扩展类
     */
    public void addDownloadButton(Button bt_download, ContentInfo contentInfo, AppExtraBean appExtraBean) {
        if (bt_download != null) {
            btHashSet.add(bt_download);
            bt_download.setOnClickListener(this);
            ShowUi.setDownloadButtonText(bt_download, contentInfo, appExtraBean);
        }
    }

    @Override
    public void onClick(View v) {
        if(System.currentTimeMillis()-time<1000){//1000毫秒点击间隔
            return;
        }
        time = System.currentTimeMillis();
        Object obj = v.getTag();
        if (obj != null) {
            HashMap<String, Object> requestParams = getRequestParams((T) obj);
            int resType = Integer.parseInt(String.valueOf(requestParams.get("resType")));
            String resId = String.valueOf(requestParams.get("resId"));
            String resTitle = String.valueOf(requestParams.get("title"));
            String detailUrl = String.valueOf(requestParams.get("detailUrl"));
            String packageName = String.valueOf(requestParams.get("packageName"));
            int versionCode = Integer.valueOf(String.valueOf(requestParams.get("versionCode")));
            //报数
            ReportFromBean bean = ReportBusiness.getInstance().get((String) requestParams.get("resId"));
            if (bean != null) {
                bean.setCompid((String) requestParams.get("parentResId"));
                bean.setComponenttype((String) requestParams.get("layoutType"));
                bean.setCompsubid(resId);
                bean.setCompsubtitle(resTitle);
                bean.setTitle(resTitle);
            }

            if(!NetworkUtil.networkEnable()){//无网络
                if (ResTypeUtil.isGameOrApp(resType)) {//游戏或者应用
                    File file = DownloadResBusiness.getApkFile(resType,resId, resTitle);//下载的资源文件
                    int apkState = ApkUtil.checkApk(file, packageName, versionCode);
                    if(apkState == ApkUtil.NEED_INSTALL && !getUninatllApkInfo(file.getAbsolutePath())){
                        apkState = ApkUtil.NEED_DOWNLOAD;
                    }
                    switch (apkState) {//apk状态
                        case ApkUtil.NEED_INSTALL://安装apk
                            ApkUtil.installApk(activity, file.getAbsolutePath());
                            break;
                        case ApkUtil.CAN_PLAY://打开apk
                            ApkUtil.startPlayApk(activity, packageName);
                            break;
                        case ApkUtil.NEED_UNZIP://解压zip
                            DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(resType, resId, resTitle, ".zip");
                            downloadItem.setAppFromType(ConstantKey.OBB);
                            UnZipUtil.unZip(downloadItem, new UnZipUtil.UnZipNotify() {
                                @Override
                                public void notify(DownloadItem downloadItem, int unZipResult) {
                                    if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                                        unZipNotify(downloadItem);
                                    }
                                }
                            });
                            break;
                        default:
                            Toast.makeText(activity, "网络已断开", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }else{
                requestDetailInfo(resType, detailUrl);//请求详情页数据
            }
        }
    }

    /**
     * 请求详情页数据
     *
     * @param resType   资源类型
     * @param detailUrl 资源详情页url
     */
    public void requestDetailInfo(int resType, final String detailUrl) {
        switch (resType) {
            case ResTypeUtil.res_type_game://游戏
            case ResTypeUtil.res_type_apply://应用
                new GameApi().getGameDetailInfoNoHeader(activity, detailUrl, new ApiCallBack<ResponseBaseBean<GameDetailBean>>() {
                    @Override
                    public void onSuccess(ResponseBaseBean<GameDetailBean> result) {
                        if (result != null) {
                            if (result.getStatus() == 0) {
                                if (result.getData() != null) {

                                    DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(result.getData(), detailUrl);//下载的实体类
                                    boolean stickPlayMode = false;
                                    List<String> playmodeList = result.getData().getPlay_mode();
                                    for(String str: playmodeList){
                                        if(str.equals("6")){ //体感游戏
                                            stickPlayMode = true;
                                        }
                                    }
                                    downloadJudge(downloadItem, detailUrl,result.getData().getSource(),result.getData().getRes_id(), stickPlayMode);//下载判断
                                }
                            }
                        }
                    }
                });
                break;
            case ResTypeUtil.res_type_video://全景视频
            case ResTypeUtil.res_type_roaming://全景漫游
                new ChoicenessApi().getPanoramaDetailInfoNoHeader(activity, detailUrl, new ApiCallBack<ResponseBaseBean<PanoramaVideoBean>>() {
                    @Override
                    public void onSuccess(ResponseBaseBean<PanoramaVideoBean> result) {
                        if (result != null) {
                            if (result.getStatus() == 0) {
                                if (result.getData() != null) {
                                    DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(result.getData(), detailUrl);//下载的实体类
                                    downloadJudge(downloadItem, detailUrl,"-1",result.getData().getRes_id(), false);//下载判断
                                }
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public  static boolean getUninatllApkInfo( String filePath) {
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
    /**
     * 下载判断
     */
    public void downloadJudge(DownloadItem downloadItem, String detailUrl, String source, String res_id, boolean stickPlayMode) {
        File file = DownloadResBusiness.getDownloadResFile(downloadItem);//下载的资源文件
        if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
            String packageName = downloadItem.getPackageName();//游戏包名
            int versionCode = Integer.valueOf(downloadItem.getApkVersionCode());//游戏版本号
            int apkState = ApkUtil.checkApk(file, packageName, versionCode);
            LogHelper.e("infoss","state=="+downloadItem.getDownloadState());
            if(apkState == ApkUtil.NEED_INSTALL && !getUninatllApkInfo(file.getAbsolutePath())){
                apkState = ApkUtil.NEED_DOWNLOAD;
            }
            LogHelper.e("infoss","apkstate==="+apkState);
            switch (apkState) {//apk状态
                case ApkUtil.NEED_DOWNLOAD://下载apk
                    //下载前check是否是体感游戏
                    if(stickPlayMode //此款游戏为体感游戏
                            && !SettingSpBusiness.getInstance().getGamenoMoreTips()) { //用户没有选中不再提示框
//                    if(stickPlayMode) {
                        showDialog(downloadItem);
                    } else {
                        downloadHandler(downloadItem, false);
                    }
                    break;
                case ApkUtil.NEED_UPDATE://升级apk
                    downloadHandler(downloadItem,true);
                    break;
                case ApkUtil.NEED_INSTALL://安装apk
                    ApkUtil.installApk(activity, file.getAbsolutePath());
                    break;
                case ApkUtil.CAN_PLAY://打开apk
                    if(!source.equals("官方合作")&& SearchSpBusiness.getInstance().getGameOpenState(res_id)==0){
                        new GameOpenDialog().showDialog(activity,downloadItem.getPackageName(),res_id);
                    }else{
                        ApkUtil.startPlayApk(activity, downloadItem.getPackageName());
                    }
                    break;
                case ApkUtil.NEED_UNZIP://解压zip
                    downloadItem.setAppFromType(ConstantKey.OBB);
                    UnZipUtil.unZip(downloadItem, new UnZipUtil.UnZipNotify() {
                        @Override
                        public void notify(DownloadItem downloadItem, int unZipResult) {
                            if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                                unZipNotify(downloadItem);
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
            //报数
            if(activity instanceof SearchActivity){//搜索页
                String clickType = ReportBusiness.getClickType(apkState);//点击类型
                ((SearchActivity) activity).reportGameClick(downloadItem.getAid(), downloadItem.getTitle(), clickType);
            }else{//其他
                reportClick(downloadItem.getAid(), apkState);
            }
        } else {//不是游戏
            if (file.exists()) {//播放
                if(StartActivityHelper.needPlayWithDownload(file, downloadItem.getDownloadType())){
                    StartActivityHelper.playPanoramaWithDownloaded(activity, downloadItem);
                }else{
                    StartActivityHelper.startPanoramaGoUnity(activity, downloadItem.getDownloadType(), detailUrl, "", "","detail", StartActivityHelper.online_resource_from_default);
                }
            } else {//下载
                downloadHandler(downloadItem,false);
            }
        }
    }

    private StickGameDownloadDialog downloadDialog;
    private void showDialog(final DownloadItem downloadItem){
        if (downloadDialog == null) {
            downloadDialog = new StickGameDownloadDialog(activity, new StickGameDownloadDialog.DownloadCallBack() {
                @Override
                public void onConfirm(boolean isChecked) { //不再提示，无需再计数
                    SettingSpBusiness.getInstance().setGameNoMoreTips(isChecked);
                    downloadHandler(downloadItem, false);
                    if(!isChecked) {
                        int count = SettingSpBusiness.getInstance().getGameDownloadCount();
                        if (count >= 3) {
                            SettingSpBusiness.getInstance().setGameNoMoreTips(true);
                            return;
                        }
                        count = count + 1;
                        SettingSpBusiness.getInstance().setGameDownloadCount(count); //提示后连续点击三次下载以后不再提示
                    }
                }

                @Override
                public void onCancel() {
                    SettingSpBusiness.getInstance().setGameDownloadCount(0);
                }
            });
        }
        downloadDialog.show();
    }

    /**
     * 下载处理
     */
    public void downloadHandler(DownloadItem downloadItem,boolean isUpdate) {
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(downloadItem.getAid());// 获取下载中的DownloadItem
        if (downloadingItem != null) { // 如果是下载中
            if (MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()) {
                DemoUtils.pauseDownload(activity, downloadingItem);//暂停下载
            } else {
                if(isUpdate){
                    if(MjDownloadStatus.ABORT == downloadingItem.getDownloadState()){
                        DemoUtils.startDownload(activity, downloadingItem);//继续下载
                    }else{
                        DownloadUtils.getInstance().updateApk(BaseApplication.INSTANCE,downloadItem);
                    }
                }else {
                    DemoUtils.startDownload(activity, downloadingItem);//继续下载
                }

            }
        } else if (UserSpBusiness.getInstance().notLoginForDownload()) {// 未登录时，超过下载限制
            showLoginForDownloadDialog(activity);//提示登录再下载
        }
//            else if (needBuy(context, info)) {// 需要购买
//                toBuy(context, info);//开始购买
//            }
        else if (!NetworkUtil.networkEnable()) {//无网络
            showNetworkErrorDialog(activity);
        } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络下载
            showOpenGprsDialog(activity);// 提示WiFi不可用，是否开启gprs网络下载
        } else {
            downloadStart(downloadItem);//开始下载
        }
    }

    /**
     * 开始下载
     */
    public static void downloadStart(DownloadItem downloadItem) {
        JSONObject jo = DownloadItemUtil.createJSONObject(downloadItem);//创建资源信息json
        String baseInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading,downloadItem.getTitle() ,downloadItem.getAid());
        FileCommonUtil.writeFileString(jo.toString(), baseInfoPath);//资源信息保存到正在下载文件夹

        DemoUtils.startDownload(BaseApplication.INSTANCE, downloadItem);// 开始下载
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(final DownloadItem downloadItem) {
//        LogHelper.e("info","downloadType=="+downloadItem.getDownloadType()+"==packageName=="+downloadItem.getPackageName());
        if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
            final String packageName = downloadItem.getPackageName();
            for (final TextView button : btHashSet) {
                final Object obj = button.getTag();
                if (obj == null) {
                    continue;
                }

                if (packageName.equals(getPackageName((T) obj))) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText("安装");
                        }
                    });
                }
            }
        } else {//不是
            final String resId = downloadItem.getAid();
            for (final TextView button : btHashSet) {
                final Object obj = button.getTag();
                if (obj == null) {
                    continue;
                }
                if (resId.equals(getResId((T) obj))) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText("VR播放");
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(int downloadingSize,List<DownloadItem> tempDownloadingList) {
        if (downloadingSize == 0  || tempDownloadingList.isEmpty()) {
            return;
        }
        List<DownloadItem> downloadingList = new ArrayList<>();
//        downloadingList.clear();
        downloadingList.addAll(tempDownloadingList);
        for (DownloadItem downloadItem : downloadingList) {
            for (final TextView button : btHashSet) {
                final Object obj = button.getTag();
                if (obj == null) {
                    continue;
                }
                if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
                    if (downloadItem.getPackageName().equals(getPackageName((T) obj))) {
                        updateProgress(button, downloadItem);//更新下载进度
                    }
                } else {//不是
                    if (downloadItem.getAid().equals(getResId((T) obj))) {
                        updateProgress(button, downloadItem);//更新下载进度
                        break;
                    }
                }
            }
        }
    }

    /**
     * 更新下载进度
     */
    private void updateProgress(final TextView button, final DownloadItem downloadItem) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int downloadState = downloadItem.getDownloadState();
                if (MjDownloadStatus.ABORT == downloadState) {//已暂停
                    button.setText("暂停中");
                } else if(downloadState == MjDownloadStatus.ERROR){
                    button.setText(activity.getResources().getString(R.string.download_continue));
                }else {
                    int progress = getDownloadProgress(downloadItem);
                    button.setText(progress + "%");
                }
            }
        });
    }

    /**
     * apk安装完成的回调
     */
    public void apkInstallNotify(String packageName) {
        for (final TextView button : btHashSet) {
            final Object obj = button.getTag();
            if (obj != null && packageName.equals(getPackageName((T) obj))) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("打开");
                    }
                });
            }
        }
    }

    /**
     * 解压完成的回调
     */
    public void unZipNotify(DownloadItem downloadItem) {
        if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
            File file = DownloadResBusiness.getDownloadResFile(downloadItem);//下载的资源文件
            ApkUtil.installApk(activity, file.getAbsolutePath());//安装apk
        } else {//不是
            for (final TextView button : btHashSet) {
                final Object obj = button.getTag();
                if (obj != null && downloadItem.getAid().equals(getResId((T) obj))) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText("VR播放");
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * 删除正在下载
     */
    public void deleteDownloading(final DownloadItem downloadItem) {
        if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
            final String packageName = downloadItem.getPackageName();
            for (final TextView button : btHashSet) {
                final Object obj = button.getTag();
                if (obj == null) {
                    continue;
                }
                if (packageName.equals(getPackageName((T) obj))) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText("下载");
                        }
                    });
                }
            }
        } else {//不是
            final String resId = downloadItem.getAid();
            for (final TextView button : btHashSet) {
                final Object obj = button.getTag();
                if (obj == null) {
                    continue;
                }
                if (resId.equals(getResId((T) obj))) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText("下载");
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * 删除下载
     *
     * @param downloadItem 下载实体类
     */
    public static void deleteDownload(final DownloadItem downloadItem, final DeleteDownloadCallback deleteDownloadCallback) {
//        DemoUtils.pauseDownload(BaseApplication.INSTANCE, downloadItem);//暂停下载
        //downloadItem.setDownloadState(DownloadConstant.DownloadState.TASK_STATE_PAUSE);
        DemoUtils.clearDownloadItemNotification(BaseApplication.INSTANCE, downloadItem);//清除下载通知
        DemoUtils.deleteDownload(BaseApplication.INSTANCE, downloadItem);//删除下载
        DownloadResInfoBusiness.deleteDownloadResInfoFile(downloadItem);//删除资源信息文件
        LocalDownloadProxy.getInstance().addProxyRunnable(new LocalDownloadProxy.ProxyRunnable() {
            @Override
            public void run() {
                DownloadResInfoSaveBusiness.deleteFile(downloadItem);
            }
        });
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DownloadResBusiness.deleteDownloadResFile(downloadItem);//删除资源文件
        if (deleteDownloadCallback != null) {
            deleteDownloadCallback.deleteCallback();
        }
    }

    /**
     * 提示登录再下载
     */
    public static void showLoginForDownloadDialog(final Activity activity) {
        new LoginForDownloadDialog(activity).showDialog("还未登录哦！", new LoginForDownloadDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        });
    }

    /**
     * 提示网络不可用
     *
     * @param activity
     */
    public static void showNetworkErrorDialog(final Activity activity) {
        new NetworkErrorDialog(activity).showDialog();
    }

    /**
     * 提示WiFi不可用，是否打开gprs对话框
     */
    public static void showOpenGprsDialog(final Activity activity) {
        new OpenGprsDialog(activity).showDialog(OpenGprsDialog.title_download, OpenGprsDialog.tip_download, new OpenGprsDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                activity.startActivity(new Intent(activity, SettingActivity.class));
            }
        });
    }

    /**
     * 获取下载进度
     */
    public static int getDownloadProgress(DownloadItem downloadItem) {
//        long downloadedSize = downloadItem.getOffset();//已下载大小
//        long totalSize = downloadItem.getTotalLen();//下载总大小
//        return getDownloadProgress(downloadedSize, totalSize);
        return downloadItem.getProgress();
    }

    /**
     * 获取下载进度
     */
    public static int getDownloadProgress(long downloadedSize, long totalSize) {
        if (totalSize == 0) {
            return 0;
        }
        float curSize = (float) downloadedSize * 100 / totalSize;
        return (int) (curSize > 100 ? 100 : curSize);
    }

    /**
     * 获取资源id
     */
    public abstract String getResId(T object);

    /**
     * 获取资源包名
     */
    public abstract String getPackageName(T object);

    /**
     * 获取请求详情数据，所需要的参数
     */
    public abstract HashMap<String, Object> getRequestParams(T object);

    public abstract String getTitle(T object);
    public interface DeleteDownloadCallback {
        void deleteCallback();
    }

    //报数
    public void reportClick(String resId, int type) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(ReportBusiness.getInstance().getClickType(type));
        bean.setTpos("1");
        bean.setGameid(resId);
        ReportFromBean fromBean = ReportBusiness.getInstance().get(resId);
        if (fromBean != null) {
            bean.setCompid(fromBean.getCompid());
            bean.setComponenttype(fromBean.getComponenttype());
            bean.setCompsubid(fromBean.getCompsubid());
            bean.setCompsubtitle(fromBean.getCompsubtitle());
            bean.setPagetype(fromBean.getPagetype());
            bean.setTitle(fromBean.getTitle());
            if (!TextUtils.isEmpty(fromBean.getTopicid())) {
                bean.setTopicid(fromBean.getTopicid());
            }
            if(!TextUtils.isEmpty(fromBean.getColid())){
                bean.setColid(fromBean.getColid());
            }
            if(!TextUtils.isEmpty(fromBean.getSubcateid())){
                bean.setSubcateid(fromBean.getSubcateid());
            }
        }
        ReportBusiness.getInstance().reportClick(bean);
    }
}
