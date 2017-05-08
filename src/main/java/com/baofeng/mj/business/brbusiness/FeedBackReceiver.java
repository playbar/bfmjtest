package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.baofeng.mj.business.sqlitebusiness.SqliteManager;
import com.baofeng.mj.util.threadutil.SqliteProxy;

/**
 * Created by hanyang on 2016/6/28.
 * 友盟反馈广播
 */
public class FeedBackReceiver extends BroadcastReceiver {
    private final String FEED_BACK_ID = "1";
    public static final String Umeng_FEEDBACK = "Umengfeedback";

    @Override
    public void onReceive(Context context, Intent intent) {
        cleanFeedBack();
//        Intent feedbackIntent = new Intent(BaseApplication.INSTANCE, ConversationActivity.class);
//        feedbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        BaseApplication.INSTANCE.startActivity(feedbackIntent);
    }

    /**
     * 清楚反馈内容
     */
    public void cleanFeedBack() {
        SqliteProxy.getInstance().addProxyRunnable(new SqliteProxy.ProxyRunnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put("content", "");//置为空串
                SqliteManager.getInstance().updateFeedBack(FEED_BACK_ID, cv);
                SqliteManager.getInstance().closeSQLiteDatabase();
            }
        });
    }
}
