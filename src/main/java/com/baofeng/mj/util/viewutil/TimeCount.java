package com.baofeng.mj.util.viewutil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.GoUnity;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.morgoo.helper.Log;
import com.storm.smart.common.utils.LogHelper;

/**
 * 页面显示计时器
 * Created by muyu on 2016/5/23.
 */
public class TimeCount extends CountDownTimer {
    public Context context;
    public TextView view;
    private String jsonParam;

    public TimeCount(final Context context, long millisInFuture, long countDownInterval, String jsonParam, TextView view) {
        super(millisInFuture, countDownInterval);
        this.view = view;
        this.context = context;
        this.jsonParam = jsonParam;
    }

    @Override
    public void onFinish() {// 完成
        LogHelper.e("px", "--------GoUnity--onFinish--------------");
        view.setText(0+ "");
        rportVV("wait");
        MjDownloadSDK.stopAll(BaseApplication.INSTANCE);
        DownloadUtils.getInstance().mIsInit = false;
        Intent intent = new Intent(context, UnityActivity.class);
        intent.putExtra("hierarchy", jsonParam);
        context.startActivity(intent);
        ((GoUnity)context).finish();
    }

    @Override
    public void onTick(long millisUntilFinished) {// 读秒
        view.setText(millisUntilFinished / 1000 + "");
    }


    private void rportVV(String counttype) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("count");
        bean.setPagetype("countdown");
        bean.setCountype(counttype);
        ReportBusiness.getInstance().reportClick(bean);
    }
}
