package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppUpdateBean;
import com.baofeng.mj.business.accountbusiness.AppUpdateBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.publicutil.ApplicationUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;
import java.util.List;

/**
 * Created by hanyang on 2016/5/16.
 * 检查更新对话框
 */
public class AppUpdateDialog {
    private Context mContext;
    private AppUpdateBusiness mBussiness;
    private Dialog noUpdateDialog;//未更新对话框
    private Dialog hasUpdateDialog;//已更新对话框
    private Dialog forceUpdateDialog;//强制更新对话框
    private CustomProgressDialog updateProgressDialog;//更新进度对话框
    private TextView updateVersion;
    TextView promptTV;
    public AppUpdateDialog(Context context, AppUpdateBusiness business) {
        this.mContext = context;
        this.mBussiness = business;
    }

    /**
     * 创建未更新对话框
     *
     * @param context
     */
    private void createNoUpdateDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_no_update_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        noUpdateDialog = new Dialog(context, R.style.alertdialog);
        noUpdateDialog.setContentView(view, params);
        noUpdateDialog.setCancelable(false);
        TextView update_dialog_ok = (TextView) view.findViewById(R.id.update_dialog_ok);
        update_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noUpdateDialog.dismiss();
            }
        });
    }

    /**
     * 创建已更新对话框
     *
     * @param context
     */
    private void createHasUpdateDialog(final Context context, final DismissListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_has_update_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        hasUpdateDialog = new Dialog(context, R.style.alertdialog);
        hasUpdateDialog.setContentView(view, params);
        hasUpdateDialog.setCancelable(false);
        updateVersion = (TextView) view.findViewById(R.id.tv_app_update_version);
        promptTV = (TextView) view.findViewById(R.id.tv_app_update_prompt);
         updateText();
        TextView has_update_dialog_cancle = (TextView) view.findViewById(R.id.has_update_dialog_cancle);
        has_update_dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasUpdateDialog.dismiss();
                if(listener != null) {
                    listener.dismiss();
                }
                BaseApplication.INSTANCE.setAppUpdateDialog(null);
            }
        });
        TextView confirm = (TextView) view.findViewById(R.id.has_update_dialog_ok);
        confirm.setText(LanguageValue.getInstance().getValue(context, "SID_UPGRADE_NOW"));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasUpdateDialog.dismiss();
//                SettingSpBusiness.getInstance().setNeedUpdate(false);
                BaseApplication.INSTANCE.setAppUpdateDialog(AppUpdateDialog.this);
                mBussiness.startDownload(context);
            }
        });

        promptTV.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void updateText(){
        String sNewVersionFormat = BaseApplication.INSTANCE.getResources().getString(R.string.check_new_version);
        AppUpdateBean appUpdateBean = mBussiness.getAppUpdateBean();
        String finalNewVersion = String.format(sNewVersionFormat, appUpdateBean == null ? "" : appUpdateBean.getLastVer());
        String prompt = appUpdateBean == null ? "" : appUpdateBean.getPrompt();
        updateContent(finalNewVersion,prompt);
    }

    private void updateContent(String content,String prompt){
        updateVersion.setText(content);
        promptTV.setText(prompt);
    }

    private void createForceUpdateDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_force_update_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        forceUpdateDialog = new Dialog(context, R.style.alertdialog);
        forceUpdateDialog.setContentView(view, params);
        forceUpdateDialog.setCancelable(false);
        TextView confirm = (TextView) view.findViewById(R.id.force_update_dialog_ok);
        confirm.setText(LanguageValue.getInstance().getValue(context, "SID_UPGRADE_NOW"));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceUpdateDialog.dismiss();
                mBussiness.startDownload(context);
            }
        });
    }

    private void createUpdateProgressDialog(Context context) {
        updateProgressDialog = new CustomProgressDialog(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProgressDialog.dismiss();
                mBussiness.stopDownload();
                if (mBussiness.isForceUpdate()) {
                    ApplicationUtil.exitApp();
                }
            }
        });
        updateProgressDialog.setMax(100);
        updateProgressDialog.setCancelable(false);
    }

    public void showProgressDialog(Context context) {
        if (updateProgressDialog == null) {
            createUpdateProgressDialog(context);
        }
        if (!updateProgressDialog.isShowing()) {
            updateProgressDialog.show();
        }
    }

    public boolean isProgressShowing(){
        if (updateProgressDialog != null) {
            return updateProgressDialog.isShowing();
        }
        return false;
    }

    public void showForceUpdateDialog(Context context) {
        if(forceUpdateDialog == null) {
            createForceUpdateDialog(context);
        }
        forceUpdateDialog.show();
        BaseApplication.INSTANCE.setAppUpdateDialog(AppUpdateDialog.this);
    }

    public void showUpdateDialog(Context context, DismissListener listener) {
        if(hasUpdateDialog == null) {
            createHasUpdateDialog(context, listener);
        }
        hasUpdateDialog.show();
    }

    public void showNewDialog(Context context){
        if(noUpdateDialog == null) {
            createNoUpdateDialog(context);
        }
        noUpdateDialog.show();
    }
    //打开插件时检测版本较低时需要更新软件版本
    public void showUpdateDialog(Context context,String content){
        if (hasUpdateDialog == null) {
            createHasUpdateDialog(context, null);
        }
        //显示打开插件时的提示文案
        updateContent(content,"");
        if (!hasUpdateDialog.isShowing()) {
            hasUpdateDialog.show();
        }
        //检测弹窗消失后将文案还原
        hasUpdateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateText();
            }
        });
        BaseApplication.INSTANCE.setAppUpdateDialog(AppUpdateDialog.this);
    }
    /**
     * 显示对话框
     */
    public void showDialog(Activity context, boolean hasUpdate) {
        if (hasUpdate) {//已经更新
            if (hasUpdateDialog == null) {
                createHasUpdateDialog(context, null);
            }
            if (!hasUpdateDialog.isShowing()) {
                hasUpdateDialog.show();
            }
            BaseApplication.INSTANCE.setAppUpdateDialog(AppUpdateDialog.this);
        } else {//未更新
            if (noUpdateDialog == null) {
                createNoUpdateDialog(context);
            }
            if (!noUpdateDialog.isShowing()) {
                noUpdateDialog.show();
            }
        }
    }

    /**
     * 更新已下载
     */

    public void updateDownloaded(final DownloadItem downloadItem) {
        if (mContext == null) {
            return;
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File file = DownloadResBusiness.getDownloadResFileNoEx(downloadItem.getDownloadType(), downloadItem.getTitle(), downloadItem.getAid());
                if (file != null) {
                    LogHelper.e("infosss","file.path------"+file.getAbsolutePath());
                    file.renameTo(DownloadResBusiness.getDownloadResFileHasEx(downloadItem.getDownloadType(), downloadItem.getTitle(),downloadItem.getAid() ,downloadItem.getHttpUrl()));
                }
                mBussiness.installApk(mContext);
                ApplicationUtil.exitApp();

            }
        });
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(final DownloadItem downloadItem) {
        if (mContext == null) {
            return;
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (updateProgressDialog != null) {
                    int progress = downloadItem.getProgress();
                    if (progress < 100) {
                        updateProgressDialog.setProgress(progress);
                    } else {
                        updateProgressDialog.dismiss();
                    }
                }
            }
        });
    }

    public void dismisssUpdateProgressDialog() {
        if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
            updateProgressDialog.dismiss();
        }
    }

    public interface DismissListener {
        void dismiss();
    }
}


