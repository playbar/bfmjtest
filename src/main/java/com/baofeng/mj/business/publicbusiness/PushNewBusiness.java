package com.baofeng.mj.business.publicbusiness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.brbusiness.PushReceiver;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.netutil.BaseApi;
import com.baofeng.mj.util.systemutil.NotificationUtil;
import com.baofeng.mj.util.viewutil.MainTabUtil;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by liuchuanchi on 2016/6/17.
 * push业务（友盟push）
 */
public class PushNewBusiness extends BaseApi {

    /**
     * 初始化友盟push
     */
    public static void initUmengPush(Context context){
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.setDebugMode(false);//关掉log
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);//sdk开启通知声音
		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);//sdk开启指示器
		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);//sdk关掉震动
        mPushAgent.setMessageHandler(new UmengMessageHandler() {
            //自定义消息的回调方法
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject joResult = new JSONObject(msg.custom);
                            String notifyId = joResult.optString("id", "");//通知id
                            Notification notification = PushNewBusiness.getNotification(joResult);
                            NotificationManager notificationManager = (NotificationManager) BaseApplication.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(Integer.valueOf(notifyId), notification);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            //自定义通知栏样式的回调方法
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
//                        try {
//                            JSONObject joResult = new JSONObject(msg.custom);
//                            Notification notification = PushNewBusiness.getNotification(joResult);
//                            if(notification != null){
//                                return notification;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    default://默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        });

        //自定义行为的回调处理，如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
            }
        });


        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                File file = new File(FileStorageUtil.getMojingDir(), "deviceToken.txt");
                FileCommonUtil.writeFileString(deviceToken, file);
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
    }

    /**
     * 获取Notification
     */
    public static Notification getNotification(JSONObject joResult) {
        Notification notification = null;
        try {
            final String notifyId = joResult.optString("id", "");//通知id
            //final String notifyType = joResult.getString("message_type");//通知类型
            final String notifyContent = joResult.getString("content");//通知内容
            final String redirectType = joResult.getString("redirect_type");//跳转类型
            final String linkType = joResult.getString("link_type");//链接类型
            final String redirectId = joResult.getString("redirect_id");//跳转id
            final String redirectUrl = joResult.getString("redirect_url");//跳转url
            final String resourceTypeParent = joResult.getString("resource_type_parent");//资源类型
            final String resourceType = joResult.getString("resource_type");//资源子类型

            PendingIntent pendingIntent = null;
            if(TextUtils.isEmpty(redirectType) || redirectType.equals(PushTypeBusiness.redirect_no)){//不跳转
                Intent intent = new Intent();
                intent.setAction(PushReceiver.ACTION_REDIRECT_NO);
                pendingIntent = PendingIntent.getBroadcast(BaseApplication.INSTANCE, Integer.valueOf(notifyId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(redirectType.equals(PushTypeBusiness.redirect_out)){//外部跳转
                Intent intent = new Intent();
                intent.setAction(PushReceiver.ACTION_REDIRECT_OUT);
                //intent.setData(Uri.parse(redirectUrl));//跳转url（会导致接收不到广播，所以注释）
                intent.putExtra(PushTypeBusiness.REDIRECT_URL, redirectUrl);//跳转url
                pendingIntent = PendingIntent.getBroadcast(BaseApplication.INSTANCE, Integer.valueOf(notifyId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                pendingIntent = PendingIntent.getBroadcast(BaseApplication.INSTANCE, Integer.valueOf(notifyId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(PushTypeBusiness.link_home.equals(linkType)){//跳转到首页也就是推荐页，置为推荐页
                    SettingSpBusiness.getInstance().setMCurrentTab(MainTabUtil.HOME);
                }
            }

            if (TextUtils.isEmpty(redirectType) || PushTypeBusiness.redirect_no.equals(redirectType)){
                notification = NotificationUtil.getNotification(NotificationUtil.contentTitle, notifyContent, "", pendingIntent);
            } else {
                notification = NotificationUtil.getNotification(NotificationUtil.contentTitle, notifyContent, NotificationUtil.contentInfo, pendingIntent);
            }
            reportPV(notifyId, notifyContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notification;
    }

    private static void reportPV(String pushid, String pushtitle) {
        ReportPVBean bean = new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("msg_push");
        bean.setPushid(pushid);
        bean.setPushtitle(pushtitle);
        ReportBusiness.getInstance().reportPV(bean);
    }
}
