package com.baofeng.mj.business.sebusiness;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.baofeng.mj.business.brbusiness.FeedBackReceiver;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.systemutil.NotificationUtil;

import java.util.List;

/**
 * Created by hanyang on 2016/6/28.
 * 友盟反馈服务
 */
public class FeedbackService extends Service {
    public static final int RECEIVE_FEEDBACK_REPLY = 0x100;
    public static final String SERVICE_NAME = "com.baofeng.mj.business.sebusiness.FeedbackService";
    public static final String FEEDBACK_ACTIVITY_NAME = "com.umeng.fb.ConversationActivity";
    private final String FEED_BACK_ID = "1";

    private static Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        init();
    }

    @SuppressLint("HandlerLeak")
//    private void init() {
//        final FeedbackAgent feedbackAgent = new FeedbackAgent(BaseApplication.INSTANCE);
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == RECEIVE_FEEDBACK_REPLY) {
//                    // 如果反馈回复页面当前在运行，后台服务不检测回复
//                    if (!isTopActivity(FEEDBACK_ACTIVITY_NAME)) {
//                        try {
//                            Conversation conversation = feedbackAgent
//                                    .getDefaultConversation();
//                            conversation.sync(new Conversation.SyncListener() {
//
//                                @Override
//                                public void onSendUserReply(List<Reply> paramList) {
//
//                                }
//
//                                @Override
//                                public void onReceiveDevReply(List<DevReply> list) {
//                                    if (list != null && !list.isEmpty()) {
//                                        final String content = list.get(0).getContent();
//                                        if (!TextUtils.isEmpty(content)) {
//
//                                            SqliteProxy.getInstance().addProxyRunnable(new SqliteProxy.ProxyRunnable() {
//                                                @Override
//                                                public void run() {
//                                                    ContentValues cv = new ContentValues();
//                                                    cv.put("id", FEED_BACK_ID);
//                                                    cv.put("content", content);
//                                                    cv.put("visite_account_page", 0);
//                                                    if (!SqliteManager.getInstance().checkIfExistFromFeedBack(FEED_BACK_ID)) {
//                                                        SqliteManager.getInstance().insertFeedBack(cv);
//                                                    } else {
//                                                        SqliteManager.getInstance().updateFeedBack(FEED_BACK_ID, cv);
//                                                    }
//                                                    SqliteManager.getInstance().closeSQLiteDatabase();
//                                                }
//                                            });
//                                            if (!isTopActivity(FEEDBACK_ACTIVITY_NAME)) {
//                                                showReplyNotification();
//                                            }
//                                        }
//                                    }
//                                    startReceiveReply();
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        startReceiveReply();
//                    }
//                }
//            }
//        };
//        startReceiveReply();
//    }

    /**
     * 启动接收回复
     */
    private void startReceiveReply() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = RECEIVE_FEEDBACK_REPLY;
                handler.sendMessage(msg);
            }
        }, 10000);
    }

    public static Intent getIntent() {
        return new Intent(SERVICE_NAME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /***
     * 显示反馈回复通知
     */
    protected void showReplyNotification() {
        //点击后发广播
        Intent feedbackNotifyIntent = new Intent(BaseApplication.INSTANCE, FeedBackReceiver.class);
        feedbackNotifyIntent.setAction(FeedBackReceiver.Umeng_FEEDBACK);

        PendingIntent contentIntent = PendingIntent
                .getBroadcast(BaseApplication.INSTANCE, 0, feedbackNotifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        String content = "您的魔镜问题反馈有新回复";
        String extra = "点击查看";
        NotificationUtil.showNotification(content, extra, contentIntent, NotificationUtil.NOTIFICATION_ID);
        SettingSpBusiness.getInstance().setHasContent(true);
    }

    /**
     * 判断当前是否显示某Activity
     *
     * @param className
     * @return
     */
    private boolean isTopActivity(String className) {
        ActivityManager manager = (ActivityManager) BaseApplication.INSTANCE
                .getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String activityName = null;
        if (null != runningTaskInfos && runningTaskInfos.size() > 0) {
            activityName = (runningTaskInfos.get(0).topActivity).getClassName();
        }
        if (activityName != null && activityName.contains(className)) {
            return true;
        } else {
            return false;
        }
    }
}
