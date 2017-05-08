package com.baofeng.mj.business.accountbusiness;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.WebExperienceReportActivity;
import com.baofeng.mj.ui.dialog.ExperienceReportDialog;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.Common;

import org.json.JSONObject;

/**
 * Created by zhaominglei on 2016/6/17.
 * 体验报告业务类
 */
public class ExperienceReportBusiness {

    private static ExperienceReportBusiness instance;

    private int CANCEL_MAX_TIMES = 3;


    private ExperienceReportListener mExperienceReportListener;

    private ExperienceReportBusiness() {
    }

    public static ExperienceReportBusiness getInstance() {
        synchronized (ExperienceReportBusiness.class) {
            if (instance == null) {
                instance = new ExperienceReportBusiness();
            }
            return instance;
        }
    }

    public void showReportDialog(Context context) {
        ExperienceReportDialog reportDialog = new ExperienceReportDialog(context);
        reportDialog.show();
    }

    public void startWebExperienceReportActivity(Context context) {
        context.startActivity(new Intent(context, WebExperienceReportActivity.class));
        //现在版本体验报告不再显示
        SettingSpBusiness.getInstance().setReportDialogCancelCount(CANCEL_MAX_TIMES);
        SettingSpBusiness.getInstance().setLastReportAppVersion(ApkUtil.getVersionNameSuffix());
    }

    public void getExperienceReportData(Context context) {
        new UserInfoApi().getExperienceReportInfo(context, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getString("code").equals("1")) {
                        JSONObject info = json.getJSONObject("info");
                        String finish_time = info.getString("finish_time");
                        String create_time = info.getString("create_time");
                        String webUrl = info.getString("url");
                        long creatTime = Long.parseLong(Common.getTimestamps(create_time));
                        long finishTime = Long.parseLong(Common.getTimestamps(finish_time));
                        long currentTime = System.currentTimeMillis();
                        if (creatTime < currentTime && finishTime > currentTime) {
                            //符合日期条件
                            SettingSpBusiness.getInstance().setReportUrl(webUrl);
                            SettingSpBusiness.getInstance().setReportDialogCancelCount(0);
                        } else {
                            //不显示体验报告
                            SettingSpBusiness.getInstance().setReportDialogCancelCount(CANCEL_MAX_TIMES);
                        }
                    } else {
                        SettingSpBusiness.getInstance().setReportDialogCancelCount(CANCEL_MAX_TIMES);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mExperienceReportListener != null) {
                    mExperienceReportListener.onExperienceReport();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (mExperienceReportListener != null) {
                    mExperienceReportListener.onExperienceReport();
                }
            }
        });
    }

    public void checkExperienceReport(Context context) {
        getExperienceReportData(context);
//        if (!isExperienceReportShow()) {
//            showReportDialog(context);
//        }
    }

    /***
     * 体验报告是否显示
     *
     * @return
     */
    public boolean isExperienceReportShow() {
        //取消次数小于3次，并且上次体验报告的版本和本次不同 并且体验报告地址不为空
        return (SettingSpBusiness.getInstance().getReportDialogCancelCount() < CANCEL_MAX_TIMES
                && (!SettingSpBusiness.getInstance().getLastReportAppVersion().equals(ApkUtil.getVersionNameSuffix()))
                && TextUtils.isEmpty(SettingSpBusiness.getInstance().getReprotUrl()));
    }

    public void setExperienceReportListener(ExperienceReportListener experienceReportListener) {
        this.mExperienceReportListener = experienceReportListener;
    }


    public interface ExperienceReportListener {
        public void onExperienceReport();
    }

}
