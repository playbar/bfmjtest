package com.baofeng.mj.business.accountbusiness;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppUpdateBean;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.dialog.AppUpdateDialog;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.AppUpdateApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;
import java.util.Date;

/**
 * Created by zhaominglei on 2016/6/17.
 * <p/>
 * 版本更新
 */
public class AppUpdateBusiness {
    private AppUpdateBean mAppUpdateBean;
//    private AppForceUpdateListener mAppUpdateListener;
    private AppUpdateDialog mAppUpdateDialog;
    private boolean isClickHome = false;

    public AppUpdateBusiness(){
        this.isClickHome = false;
    }

    /**
     *
     * @param mContext
     * @param isShowNew 是否显示最新版本Dialog
     * @param isDismiss　升级取消后是否继续显示 true：取消后，不再显示 false：取消后可继续显示
     */
    public void checkUpdate(final Context mContext, final boolean isShowDialog, final boolean isShowNew, final boolean isDismiss) {
        checkLocalAPK();
        String sysVersion = android.os.Build.VERSION.RELEASE;
        new AppUpdateApi().checkAppUpdate(ApkUtil.getVersionNameSuffix(), sysVersion, Common.getMac(), new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                LogHelper.e("infosss","result=="+result);
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                try {
                    AppUpdateBean updateInfo = JSON.parseObject(result, AppUpdateBean.class);
                    if (updateInfo != null && (!TextUtils.isEmpty(updateInfo.getLastVer())) && (!TextUtils.isEmpty(updateInfo.getDownload()))) {
                        mAppUpdateBean = updateInfo;
                        if(isShowDialog) {
                            if (isUpdate()) {
                                if (mAppUpdateBean.getIs_force_update() == 1) {
                                    if(isClickHome){
                                        isClickHome = false;
                                        startDownload(mContext);
                                    }else{
                                        showForceUpdateDialog(mContext);
                                    }

                                } else if (isDismiss && SettingSpBusiness.getInstance().getDismiss(updateInfo.getLastVer())) {
                                    //取消后不显示，并且点击过 取消 返回 true
                                } else {
                                    showUpdateDialog(mContext);
                                }
                            } else if (isShowNew) {
                                showNewDialog(mContext);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkLocalAPK() {
        String path1 = DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_apply) + File.separator + DownloadItemUtil.DOWNLOAD_TITLE_MJ + ".apk";
        String path2 = DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_apply) + File.separator + DownloadItemUtil.DOWNLOAD_ID_MJ + ".apk";
        //已下载的apk和安装的apk版本相同
        if (ApkUtil.checkAPKExist(path1, ApkUtil.getVersionNameSuffix())) {
            new File(path1).delete();
        } else if(ApkUtil.checkAPKExist(path2, ApkUtil.getVersionNameSuffix())) {
            new File(path2).delete();
        }
    }

    public boolean isSameDay() {
        boolean sameDay = true;
        long lastUpdateTime = SettingSpBusiness.getInstance().getLastUpdateTime();
        if (lastUpdateTime < 0) {
            SettingSpBusiness.getInstance().setLastUpdateTime(System.currentTimeMillis());
            sameDay = false;
        } else {
            Date start = new Date(lastUpdateTime);
            Date now = new Date(System.currentTimeMillis());
            int gapCount = Common.getGapCount(start, now);
            if (gapCount >= 1) {
                SettingSpBusiness.getInstance().setLastUpdateTime(System.currentTimeMillis());
                sameDay = false;
            }
        }
        return sameDay;
    }

    public void startDownload(Context context) {
        if (!NetworkUtil.isNetworkConnected(BaseApplication.getInstance())) {
            Toast.makeText(context, R.string.network_exception, Toast.LENGTH_SHORT).show();
            return;
        }
        //本地已存在
        String path = DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_apply) + File.separator + DownloadItemUtil.DOWNLOAD_ID_MJ + ".apk";
        if(mAppUpdateBean==null)
            return;
        if (ApkUtil.checkAPKExist(path, mAppUpdateBean.getLastVer())) {
            ApkUtil.installApk(context, path);
            return;
        }
        if (!TextUtils.isEmpty(mAppUpdateBean.getDownload())) {
            DownloadItem downloadItem = DownloadItemUtil.createDownloadItemForMojing(mAppUpdateBean.getDownload());
            downloadItem.setDownloadStatus(0);
            DemoUtils.startDownload(BaseApplication.getInstance(), downloadItem);
            getAppUpdateDialog(context).showProgressDialog(context);
        }
    }

    public void stopDownload() {
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItemForMojing(mAppUpdateBean.getDownload());
        downloadItem.setDownloadStatus(MjDownloadStatus.DOWNLOADING);
        DemoUtils.pauseDownload(BaseApplication.getInstance(), downloadItem);
    }


    /***
     * 安装更新的apk
     */
    public void installApk(Context context) {
        String path1 = DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_apply) + File.separator + DownloadItemUtil.DOWNLOAD_TITLE_MJ + ".apk";
        String path2 = DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_apply) + File.separator + DownloadItemUtil.DOWNLOAD_ID_MJ + ".apk";
        if (ApkUtil.checkAPKExist(path1, mAppUpdateBean.getLastVer())) {
            ApkUtil.installApk(context, path1);
        } else if(ApkUtil.checkAPKExist(path2, mAppUpdateBean.getLastVer())) {
            ApkUtil.installApk(context, path2);
        } else {
            Toast.makeText(context, "下载安装包出错，将重新下载", Toast.LENGTH_LONG).show();
            startDownload(context);
        }
    }

    public void showForceUpdateDialog(Context context) {
        getAppUpdateDialog(context).showForceUpdateDialog(context);
    }

    public void showUpdateDialog(Context context){
        getAppUpdateDialog(context).showUpdateDialog(context, new AppUpdateDialog.DismissListener() {
            @Override
            public void dismiss() {
                SettingSpBusiness.getInstance().setDismiss(mAppUpdateBean.getLastVer(), true);
            }
        });
    }

    public void showNewDialog(Context context){
        getAppUpdateDialog(context).showNewDialog(context);
    }

    public AppUpdateDialog getAppUpdateDialog(Context mContext) {
        if(mAppUpdateDialog == null){
            mAppUpdateDialog = new AppUpdateDialog(mContext, this);
        }
        return mAppUpdateDialog;
    }

    public AppUpdateBean getAppUpdateBean() {
        return mAppUpdateBean;
    }

    public void setAppUpdateBean(AppUpdateBean bean){
        mAppUpdateBean = bean;
    }


    public boolean isUpdate() {
        String localVersionCode = ApkUtil.getVersionNameSuffix();
        if (TextUtils.isEmpty(localVersionCode)) {
            return false;
        }
        boolean isUpdate = (mAppUpdateBean != null) && (!TextUtils.isEmpty(mAppUpdateBean.getLastVer()))
                && (!mAppUpdateBean.getLastVer().contains(localVersionCode));
        SettingSpBusiness.getInstance().setNeedUpdate(isUpdate);
        return isUpdate;
    }

    public boolean isForceUpdate() {
        return isUpdate() && mAppUpdateBean.getIs_force_update() == 1;
    }

//    public void setAppUpdateListener(AppForceUpdateListener appUpdateListener) {
//        this.mAppUpdateListener = appUpdateListener;
//    }

    public void dismisssUpdateProgressDialog(){
        if(mAppUpdateDialog!=null){
            mAppUpdateDialog.dismisssUpdateProgressDialog();
        }
    }

//    public interface AppForceUpdateListener {
//        public void onForceUpdate(boolean isForce);
//    }

    public void setIsClickHome(boolean isClickHome){
        this.isClickHome = isClickHome;
    }
}
