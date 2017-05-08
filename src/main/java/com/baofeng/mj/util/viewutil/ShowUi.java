package com.baofeng.mj.util.viewutil;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.bfmj.sdk.common.App;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/6/14.
 * 显示UI
 */
public class ShowUi {
    /**
     * 显示标题
     * @param tv_title 标题控件
     * @param title 标题
     * @param lengthLimit 长度限制
     */
    public static void showTitle(TextView tv_title , String title, int lengthLimit){
        int lastIndex = title.lastIndexOf(".");
        if(lastIndex > 0){
            String titlePrefix = title.substring(0, lastIndex);
            String titleSuffix = title.substring(lastIndex);
            int titlePrefixLength = titlePrefix.length();
            if(titlePrefixLength <= lengthLimit){
                tv_title.setText("[" + titlePrefix + "]" + titleSuffix);
            }else{
                tv_title.setText("[" + titlePrefix.substring(0, lengthLimit - 5) + "..." + titlePrefix.substring(titlePrefixLength - 3) + "]" + titleSuffix);
            }
        }else{
            tv_title.setText(title);
        }
    }

    /**
     * 飞屏显示title
     * @param tv_title
     * @param title
     * @param lengthLimit
     */
    public static void showFlyScreenTitle(TextView tv_title , String title, int lengthLimit){
        int lastIndex = title.lastIndexOf(".");
        if(lastIndex > 0){
            String titlePrefix = title.substring(0, lastIndex);
            String titleSuffix = title.substring(lastIndex);
            int titlePrefixLength = titlePrefix.length();
            if(titlePrefixLength <= lengthLimit){
                tv_title.setText(titlePrefix + titleSuffix);
            }else{
                tv_title.setText(titlePrefix.substring(0, lengthLimit - 5) + "..." + titlePrefix.substring(titlePrefixLength - 4) + titleSuffix);
            }
        }else{
            tv_title.setText(title);
        }
    }

    /**
     * 显示下载按钮内容（游戏用）
     * @param bt_download 下载按钮
     * @param gameDetailBean 游戏实体类
     */
    public static void setDownloadButtonText(Button bt_download, GameDetailBean gameDetailBean){
        int resType = gameDetailBean.getType();
        String resId = gameDetailBean.getRes_id();
        String resTitle = gameDetailBean.getTitle();
        String downloadUrl = gameDetailBean.getDownload_url();
        String packageName = gameDetailBean.getPackage_name();
        int versionCode = Integer.valueOf(gameDetailBean.getVersioncode());
        setDownloadButtonText(bt_download, resType, resId, resTitle, downloadUrl, packageName, versionCode);
    }

    /**
     * 显示下载按钮内容（游戏用）
     * @param bt_download 下载按钮
     * @param contentInfo 列表实体类
     * @param appExtraBean app扩展类
     */
    public static void setDownloadButtonText(Button bt_download, ContentInfo contentInfo, AppExtraBean appExtraBean){
        int resType = contentInfo.getType();
        String resId = contentInfo.getRes_id();
        String resTitle = contentInfo.getTitle();
        String downloadUrl = "";
        String packageName = appExtraBean.getPackage_name();
        int versionCode = Integer.valueOf(appExtraBean.getVersion_code());
        setDownloadButtonText(bt_download, resType, resId, resTitle, downloadUrl, packageName, versionCode);
    }

    /**
     * 显示下载按钮内容（游戏用）
     * @param bt_download 下载按钮
     * @param resType 资源类型
     * @param resId 资源id
     * @param resTitle 资源标题
     * @param downloadUrl 下载url
     * @param packageName apk包名
     * @param versionCode apk版本号
     */
    public static void setDownloadButtonText(Button bt_download, int resType, String resId, String resTitle, String downloadUrl, String packageName, int versionCode) {
        File file = DownloadResBusiness.getDownloadResFile(resType, resId, resTitle, downloadUrl);
        int apkState = ApkUtil.checkApk(file, packageName, versionCode);
        LogHelper.e("infossss","apkState----"+apkState+"--versoinCode==="+versionCode);
        if(apkState == ApkUtil.NEED_INSTALL && !getUninatllApkInfo(file.getAbsolutePath())){
            apkState = ApkUtil.NEED_DOWNLOAD;
        }
        LogHelper.e("infossss","apkState=="+apkState);
        switch (apkState){//apk状态
            case ApkUtil.NEED_DOWNLOAD://下载apk
                bt_download.setText("下载");
                bt_download.setBackgroundResource(R.drawable.corner_blue_bg);
                bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.btn_normal_color));
                break;
            case ApkUtil.NEED_UPDATE://升级apk
                bt_download.setText("更新");
                bt_download.setBackgroundResource(R.drawable.corner_orange_bg);
                bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.mj_color_orange));
                break;
            case ApkUtil.NEED_INSTALL://安装apk
                bt_download.setText("安装");
                bt_download.setBackgroundResource(R.drawable.corner_blue_bg);
                bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.btn_normal_color));
                break;
            case ApkUtil.CAN_PLAY://打开apk
                bt_download.setText("打开");
                bt_download.setBackgroundResource(R.drawable.corner_blue_bg);
                bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.btn_normal_color));
                break;
            case ApkUtil.NEED_UNZIP://解压zip
                bt_download.setText("安装");
                bt_download.setBackgroundResource(R.drawable.corner_blue_bg);
                bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.btn_normal_color));
                break;
            default:
                break;
        }

        DownloadItem item = BaseApplication.INSTANCE.getDownloadItem(resId);
        if(null != item){
            if(item.getDownloadState() != MjDownloadStatus.DOWNLOADING){
                    if(item.getDownloadState() == MjDownloadStatus.ABORT){
                        bt_download.setText("暂停中");
                    }else if(item.getDownloadState() == MjDownloadStatus.WAITING){
                        bt_download.setText(String.valueOf(DownLoadBusiness.getDownloadProgress(item)));
                    }
            }
        }

    }


    private  static boolean getUninatllApkInfo( String filePath) {
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
