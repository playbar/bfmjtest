package com.baofeng.mj.util.systemutil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;

/**
 * APP通知工具类
 * @author yebo
 */
public class NotificationUtil {
	public static final int NOTIFICATION_ID = 1001; // 通知id
	public static final String contentTitle = "暴风魔镜";
	public static final String contentInfo = "点击查看";

	/**
	 * 显示通知
	 */
	public static void showNotification(String contentText, PendingIntent contentIntent, int notifyId) {
		Notification notification = getNotification(contentTitle, contentText, contentInfo, contentIntent);
		NotificationManager notificationManager = (NotificationManager) BaseApplication.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notifyId, notification);
	}

	/**
	 * 显示通知
	 */
	public static void showNotification(String contentText, String contentInfo, PendingIntent contentIntent, int notifyId) {
		Notification notification = getNotification(contentTitle, contentText, contentInfo, contentIntent);
		NotificationManager notificationManager = (NotificationManager) BaseApplication.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notifyId, notification);
	}

	/**
	 * 显示通知
	 */
	public static void showNotification(RemoteViews remoteView, PendingIntent contentIntent, int notifyId){
		Notification notification = getNotification(remoteView, contentIntent);
		NotificationManager notificationManager = (NotificationManager) BaseApplication.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notifyId, notification);
	}

	/**
	 * 获取通知
	 */
	public static Notification getNotification(String contentTitle, String contentText, String contentInfo, PendingIntent contentIntent) {
		Builder mBuilder = new Builder(BaseApplication.INSTANCE);
		mBuilder.setContentTitle(contentTitle)
				.setContentText(contentText)
				.setContentInfo(contentInfo)
				.setContentIntent(contentIntent)
				.setTicker(contentText)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(BaseApplication.INSTANCE.getResources(), R.drawable.ic_launcher))
				.setAutoCancel(true)
				.setOngoing(false)
				.setPriority(Notification.PRIORITY_DEFAULT);
		if (Build.VERSION.SDK_INT >= 21) {
			mBuilder.setSmallIcon(R.drawable.mj_5_icon);
		}
		Notification notification = mBuilder.build();
		notification.contentIntent = contentIntent;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}

	/**
	 * 获取通知
	 */
	public static Notification getNotification(RemoteViews remoteView, PendingIntent contentIntent){
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BaseApplication.INSTANCE);
		mBuilder.setContent(remoteView)
				.setContentIntent(contentIntent)
				.setTicker("点击查看")
				.setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true)
				.setOngoing(false)
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setWhen(System.currentTimeMillis());
		if (Build.VERSION.SDK_INT >= 21) {
			mBuilder.setSmallIcon(R.drawable.mj_5_icon);
		}
		Notification notification = mBuilder.build();
		notification.bigContentView = remoteView;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}
}
