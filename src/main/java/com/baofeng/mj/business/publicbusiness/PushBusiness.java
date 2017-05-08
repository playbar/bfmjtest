package com.baofeng.mj.business.publicbusiness;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.brbusiness.PushReceiver;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ApiResponseHandler;
import com.baofeng.mj.util.netutil.BaseApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ApplicationUtil;
import com.baofeng.mj.util.publicutil.DateUtil;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.systemutil.NotificationUtil;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuchuanchi on 2016/6/17.
 * push业务（接口push）
 */
public class PushBusiness extends BaseApi {
    private static PushBusiness instance;
    private Executor executor;
    private String partVersionName;//versionName的部分
    private int widthPixels = 0;

    private PushBusiness() {
    }

    private String getPartVersionName(){
        if(TextUtils.isEmpty(partVersionName)){
            partVersionName = ApkUtil.getVersionNameSuffix();
        }
        return partVersionName;
    }

    public static PushBusiness getInstance() {
        if (instance == null) {
            instance = new PushBusiness();
        }
        return instance;
    }

    /**
     * 接收push
     */
    public void receiverPush() {
        if(executor != null){
            ((ScheduledExecutorService) executor).shutdownNow();
            executor = null;
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        ((ScheduledExecutorService) executor).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (ApplicationUtil.mojingAppInForeground()) {//魔镜app在前台
                    Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
                    if(curActivity == null || curActivity instanceof UnityActivity){
                        return;
                    }
                    curActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //push接口回调
                            ApiCallBack<String> apiCallBack = new ApiCallBack<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    super.onSuccess(result);
                                    Log.i("receiverPush---", "result = " + result);
                                    if (TextUtils.isEmpty(result)) {
                                        return;
                                    }
                                    try {
                                        JSONArray jaResult = new JSONArray(result);
                                        for (int i = 0; i < jaResult.length(); i++) {
                                            JSONObject joResult = jaResult.getJSONObject(i);
                                            if(!verifyVersion(joResult)){
                                                continue;//版本号不合法，继续下一次循环
                                            }
                                            if(!verifyTime(joResult)){
                                                continue;//时间不合法，继续下一次循环
                                            }
                                            if ("1".equals(joResult.optString("message_type"))) {// 文字通知
                                                if(needShowTextNotify(joResult.optString("id", ""))){//需要显示文字通知
                                                    textNotify(joResult);//显示文字通知
                                                    String txtPushIds = SettingSpBusiness.getInstance().getTxtPushIds();//获取文本通知id集合
                                                    SettingSpBusiness.getInstance().setTxtPushIds(txtPushIds + joResult.optString("id", "") + ",");
                                                }
                                            } else {//图片通知
                                                if(needShowImageNotify(joResult.optString("id", ""))){//需要显示图片通知
                                                    imageNotify(joResult);//显示图片通知
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                    }
                                }
                            };
                            //请求push接口
                            getAsyncHttpClient().get(BaseApplication.INSTANCE, ConfigUrl.getPushUrl(), null, false, "", new ApiResponseHandler<String>(apiCallBack) {
                                @Override
                                public String parseResponse(String responseString) {
                                    return responseString;
                                }
                            });
                        }
                    });

                }
            }
        }, 0, 15, TimeUnit.SECONDS);//每隔15秒执行一次
    }

    /**
     * 文字通知
     * @param joResult
     */
    private void textNotify(JSONObject joResult){
        Log.i("receiverPush---", "textNotify");
        try {
            final String notifyId = joResult.optString("id", "");//通知id
            final String notifyType = joResult.getString("message_type");//通知类型
            final String notifyContent = joResult.getString("content");//通知内容
            final String redirectType = joResult.getString("redirect_type");//跳转类型
            final String linkType = joResult.getString("link_type");//链接类型
            final String redirectId = joResult.getString("redirect_id");//跳转id
            final String redirectUrl = joResult.getString("redirect_url");//跳转url
            final String resourceTypeParent = joResult.getString("resource_type_parent");//资源类型
            final String resourceType = joResult.getString("resource_type");//资源子类型

            PendingIntent pendingIntent = createPendingIntent(notifyId, redirectType, linkType, redirectId, redirectUrl, resourceTypeParent, resourceType);
            if(TextUtils.isEmpty(redirectType) || PushTypeBusiness.redirect_no.equals(redirectType)){
                NotificationUtil.showNotification(notifyContent, "", pendingIntent, Integer.valueOf(notifyId));
            }else{
                NotificationUtil.showNotification(notifyContent, pendingIntent, Integer.valueOf(notifyId));
            }
            reportPV(notifyId,notifyContent);
        } catch (JSONException e) {
        }
    }

    /**
     * 图片通知
     * @param joResult
     */
    private void imageNotify(final JSONObject joResult) {
        Log.i("receiverPush---","imageNotify");
        try {
            final String notifyId = joResult.optString("id", "");//通知id
            final String notifyType = joResult.getString("message_type");//通知类型
            final String notifyContent = joResult.getString("content");//通知内容
            final String redirectType = joResult.getString("redirect_type");//跳转类型
            final String linkType = joResult.getString("link_type");//链接类型
            final String redirectId = joResult.getString("redirect_id");//跳转id
            final String redirectUrl = joResult.getString("redirect_url");//跳转url
            final String resourceTypeParent = joResult.getString("resource_type_parent");//资源类型
            final String resourceType = joResult.getString("resource_type");//资源子类型
            final String imgUrl = joResult.getString("img_url");//图片url

            if (TextUtils.isEmpty(imgUrl)) {//图片url不存在
                showNotify(notifyId, notifyContent, redirectType, linkType, redirectId, redirectUrl, resourceTypeParent, resourceType, null);
            } else {//下载图片bitmap
                GlideUtil.loadBitmap(BaseApplication.INSTANCE, imgUrl, new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (bitmap != null) {
                            RemoteViews remoteView = null;
                            //if (PushTypeBusiness.style_one.equals(styleType)) {//通知样式1
//                                remoteView = new RemoteViews(BaseApplication.INSTANCE.getPackageName(), R.layout.push_view2);
//                            } else if (PushTypeBusiness.style_two.equals(styleType)) {//通知样式2
                            remoteView = new RemoteViews(BaseApplication.INSTANCE.getPackageName(), R.layout.push_view3);
//                            }
                            if (remoteView != null) {
                                remoteView.setImageViewBitmap(R.id.notify_back, bitmap);
                                showNotify(notifyId, notifyContent, redirectType, linkType, redirectId, redirectUrl, resourceTypeParent, resourceType, remoteView);
                            }
                        }
                    }
                });
                reportPV(notifyId,notifyContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示通知
     */
    private synchronized void showNotify(String notifyId, String notifyContent, String redirectType, String linkType, String redirectId, String redirectUrl, String resourceTypeParent, String resourceType,RemoteViews remoteView) {
        Log.i("receiverPush---", "showNotify");
        if(widthPixels == 0){
            widthPixels = PixelsUtil.getWidthPixels() - 120;
        }
        PendingIntent pendingIntent = createPendingIntent(notifyId, redirectType, linkType, redirectId, redirectUrl, resourceTypeParent, resourceType);
        if(remoteView == null) {
            NotificationUtil.showNotification(notifyContent, "", pendingIntent, Integer.valueOf(notifyId));
        }else{
            //if(TextUtils.isEmpty(title)){
                remoteView.setViewVisibility(R.id.txt_notify_title, View.INVISIBLE);
//            }else{
//                remoteView.setTextViewText(R.id.txt_notify_title, title);
//                int titleLen = title.length();
//                if (remoteView.getLayoutId() != R.layout.push_view1 && PixelsUtil.sp2px(18) * titleLen > widthPixels) {
//                    remoteView.setTextViewTextSize(R.id.txt_notify_content, TypedValue.COMPLEX_UNIT_PX, widthPixels / titleLen);
//                }
//            }
            if(TextUtils.isEmpty(notifyContent)){
                remoteView.setViewVisibility(R.id.txt_notify_content, View.INVISIBLE);
            }else{
                remoteView.setTextViewText(R.id.txt_notify_content, notifyContent);
                int contentLen = notifyContent.length();
                if (remoteView.getLayoutId() != R.layout.push_view1 && PixelsUtil.sp2px(16) * contentLen > widthPixels) {
                    remoteView.setTextViewTextSize(R.id.txt_notify_content, TypedValue.COMPLEX_UNIT_PX, widthPixels / contentLen);
                }
            }
            NotificationUtil.showNotification(remoteView, pendingIntent, Integer.valueOf(notifyId));
        }
    }

    /**
     * 创建PendingIntent
     */
    private PendingIntent createPendingIntent(String notifyId, String redirectType, String linkType, String redirectId, String redirectUrl, String resourceTypeParent, String resourceType){
        if(TextUtils.isEmpty(redirectType) || redirectType.equals(PushTypeBusiness.redirect_no)){//不跳转
            Intent intent = new Intent();
            intent.setAction(PushReceiver.ACTION_REDIRECT_NO);
            return PendingIntent.getBroadcast(BaseApplication.INSTANCE, Integer.valueOf(notifyId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else if(redirectType.equals(PushTypeBusiness.redirect_out)){//外部跳转
            Intent intent = new Intent();
            intent.setAction(PushReceiver.ACTION_REDIRECT_OUT);
            Bundle bundle = new Bundle();
            bundle.putString(PushTypeBusiness.REDIRECT_URL, redirectUrl);//跳转url
            intent.putExtras(bundle);
//            intent.setData(Uri.parse(redirectUrl));//跳转url
            return PendingIntent.getBroadcast(BaseApplication.INSTANCE, Integer.valueOf(notifyId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {//内部跳转
            Intent intent = new Intent();
            intent.setAction(PushReceiver.ACTION_REDIRECT_INNER);
            Bundle bundle = new Bundle();
            bundle.putString(PushTypeBusiness.NOTIFY_ID, notifyId);//通知id
            bundle.putString(PushTypeBusiness.LINK_TYPE, linkType);//链接类型
            bundle.putString(PushTypeBusiness.REDIRECT_ID, redirectId);//跳转id
            bundle.putString(PushTypeBusiness.REDIRECT_URL, redirectUrl);//跳转url
            bundle.putString(PushTypeBusiness.RESOURCE_TYPE_PARENT, resourceTypeParent);//资源类型
            bundle.putString(PushTypeBusiness.RESOURCE_TYPE, resourceType);//资源子类型
            bundle.putInt(PushTypeBusiness.FROM_WHERE, PushTypeBusiness.from_where_normal);
            intent.putExtras(bundle);
            return PendingIntent.getBroadcast(BaseApplication.INSTANCE, Integer.valueOf(notifyId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    /**
     * 验证版本号
     * @param joResult
     * @return true合法，false不合法
     */
    private boolean verifyVersion(JSONObject joResult){
        boolean versionValid = false;//false版本号不合法
        try {
            JSONArray jaVersion = joResult.optJSONArray("version");
            if(jaVersion != null && jaVersion.length() > 0){
                for(int i = 0; i < jaVersion.length(); i++){
                    if(getPartVersionName().equals(jaVersion.getString(i).trim())){
                        versionValid = true;//true版本号合法
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionValid;
    }

    /**
     * 验证时间
     * @param joResult
     * @return true合法，false不合法
     */
    private boolean verifyTime(JSONObject joResult){
        boolean timeValid = false;//false时间不合法
        try {
            String startTime = joResult.getString("start_time");//开始时间
            String endTime = joResult.getString("end_time");//结束时间
            String curTime = DateUtil.date2StringForSecond(new Date());//当前时间
            if(DateUtil.compareDate(curTime, startTime) && DateUtil.compareDate(endTime, curTime)){
                timeValid = true;//true时间合法
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeValid;
    }

    /**
     * true需要显示文本通知，false不需要
     */
    private boolean needShowTextNotify(String notifyId){
        String txtPushIds = SettingSpBusiness.getInstance().getTxtPushIds();//获取文本通知id集合
        if(txtPushIds.contains(notifyId + ",")){
            return false;
        }
        return true;
    }

    /**
     * true需要显示图片通知，false不需要
     */
    private boolean needShowImageNotify(String notifyId){
        String imgPushIds = SettingSpBusiness.getInstance().getImgPushIds();//获取图片通知id集合
        if(imgPushIds.contains(notifyId + ",")){
            return false;
        }
        SettingSpBusiness.getInstance().setImgPushIds(imgPushIds + notifyId + ",");
        return true;
    }

    private void reportPV(String pushid, String pushtitle) {
        ReportPVBean bean = new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("msg_push");
        bean.setPushid(pushid);
        bean.setPushtitle(pushtitle);
        ReportBusiness.getInstance().reportPV(bean);
    }
}
