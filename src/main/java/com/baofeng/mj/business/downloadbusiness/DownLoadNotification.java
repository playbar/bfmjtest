package com.baofeng.mj.business.downloadbusiness;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.mojing.dl.domain.DownloadItem;
import com.mojing.dl.utils.DownloadConstant.ApkDownloadType;
import com.storm.smart.common.utils.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 下载通知
 * ClassName: DownLoadNotification <br/>
 * @author linzanxian    
 * @date: 2015年1月19日 上午10:52:16 <br/>  
 * description:下载通知
 */
public class DownLoadNotification {

    private static NotificationManager manager;

    private static DownLoadNotification instance;

    private Context mContext;

    private HashMap<Integer, Notification> mHashMap;

    private int mWarnNotificationID = 12345;

    private ArrayList<String> failedItemList = new ArrayList<String>();

    private ArrayList<String> completeItemList = new ArrayList<String>();

    /**
     * 下载通知单例
     * @author linzanxian  @Date 2015年1月19日 上午10:52:48
     * description:
     * @param context Context
     * @return void
     */
    public static DownLoadNotification getInstance(Context context) {
        if (instance == null) {
            instance = new DownLoadNotification(context);
        }
        return instance;
    }

    /**
     * 下载通知实例
     * @author linzanxian  @Date 2015年1月19日 上午10:52:48
     * description:
     * @param context Context
     * @return void
     */
    private DownLoadNotification(Context mContext) {
        super();
        this.mContext = mContext;
        initData(mContext);
    }

    /**
     * 创建新的下载通知
     * @author linzanxian  @Date 2015年1月19日 上午10:53:40
     * description:添加一个新的下载notification
     * @param item 下载资源项
     * @return void
     */
    public void createNewDownloadNotification(DownloadItem item) {
        // 捆绑软件不显示通知栏
        if (item == null) {
            return;
        }

        if (item.getApkDownloadType() == ApkDownloadType.DOWNLOAD_TYPE_BIND) {
            return;
        }

        if (item.isVideoType()) {
        	createNewVideoDownloadNotification(item);
        } else {
        	createApkDownloadNotification(item);
        }
    }

    /**
     * 创建新的视频下载通知
     * @author linzanxian  @Date 2015年1月19日 上午10:53:40
     * description:添加一个新的视频下载通知
     * @param item 下载资源项
     * @return void
     */
    private void createNewVideoDownloadNotification(DownloadItem item) {
    	/*
        // 创建一个Notification
        Notification notification = new Notification();
        // 设置显示在手机最上边的状态栏的图标
        
        notification.icon = R.drawable.stat_storm_download;
        notification.tickerText = mContext.getText(R.string.download_new_dltask);
        notification.when = System.currentTimeMillis();
        notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.download_notification);
        contentView.setImageViewResource(R.id.imageView, R.drawable.notification_baofeng);
        contentView.setViewVisibility(R.id.cms_progress_bar, View.VISIBLE);
        contentView.setProgressBar(R.id.cms_progress_bar, 0, 0, false);
        contentView.setTextViewText(R.id.contentTitle, getNotificationTitle(item));
        contentView.setTextViewText(R.id.contentSubTitle,
                mContext.getString(R.string.download_video_click_show));
        // 指定个性化视图
        notification.contentView = contentView;

        Intent jumpIntent = getLocalActivityIntent();
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, jumpIntent, 0);

        notification.contentIntent = contentIntent;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        int notificationID = getNotificationId(item);
        mHashMap.put(notificationID, notification);
        manager.notify(notificationID, notification);
        */
    }

    /**
     * 创建新的Apk下载通知
     * @author linzanxian  @Date 2015年1月19日 上午10:55:24
     * description:创建CMS推荐软件下载通知
     * @param item 下载资源项
     * @return void
     */
    private void createApkDownloadNotification(DownloadItem item) {
        // 捆绑软件不显示通知栏
        if (item.getApkDownloadType() == ApkDownloadType.DOWNLOAD_TYPE_BIND) {
            return;
        }
        /*
        Notification notification = new Notification();
        
        notification.icon = R.drawable.stat_storm_download;
        // 4.0以下版本不支持按钮事件，特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        } else {
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }
        RemoteViews expandedView = new RemoteViews(mContext.getPackageName(),
                R.layout.cms_rec_app_notification_upgrade);

        expandedView.setTextViewText(R.id.cms_title_text,
                mContext.getString(R.string.app_recommend_downloading) + item.getTitle());
        expandedView.setImageViewResource(R.id.cms_appIcon, R.drawable.notification_baofeng);

        expandedView.setViewVisibility(R.id.cms_progress_bar, View.VISIBLE);

        expandedView.setViewVisibility(R.id.cms_app_cancel, View.GONE);

        expandedView.setProgressBar(R.id.cms_progress_bar, 0, 0, false);

        expandedView.setTextViewText(R.id.cms_app_retry_text, "点击暂停");

        notification.contentView = expandedView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            notification.contentView.setOnClickPendingIntent(R.id.cms_app_retry_text,
                    getDownloadIntent(item, DownloadCommand.PAUSE_DOWNLOAD));
        } else {
            notification.contentIntent = getDownloadIntent(item, DownloadCommand.PAUSE_DOWNLOAD);
        }
        int notificationID = getNotificationId(item);
        mHashMap.put(notificationID, notification);
        manager.notify(notificationID, notification);
        */
    }

    /**
     * 设置apk下载完成通知栏样式
     * @author linzanxian  @Date 2015年1月19日 上午10:56:05
     * description:设置apk下载完成视图
     * @param item 下载资源项
     * @return void
     */
    public void setApkNotificationDownloadCompleteView(DownloadItem item) {
        if (item == null) {
            return;
        }
        if (item.getApkDownloadType() == ApkDownloadType.DOWNLOAD_TYPE_BIND) {
            return;
        }
        if (mHashMap == null) {
            return;
        }
        if (!item.isVideoType()) {
            int notificationID = getNotificationId(item);
            Notification notification = mHashMap.get(notificationID);
            if (notification == null || notification.contentView == null) {
                return;
            }
            notification.flags = 0;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            /*
            notification.contentView.setViewVisibility(R.id.cms_progress_bar, View.GONE);
            notification.contentView.setViewVisibility(R.id.cms_app_retry_text, View.VISIBLE);
            notification.contentView.setTextViewText(R.id.cms_app_retry_text, "点击安装");
            notification.contentView.setTextViewText(R.id.cms_title_text, item.getTitle() + "下载完成");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                notification.contentView.setOnClickPendingIntent(R.id.cms_app_retry_text,
                        getDownloadIntent(item, DownloadCommand.DOWNLOAD_APK_INSTALL));
            } else {
                notification.contentIntent = getDownloadIntent(item,
                        DownloadCommand.DOWNLOAD_APK_INSTALL);
            }
            manager.notify(notificationID, notification);
            mHashMap.put(notificationID, notification);
            */
        }
    }

    /**
     * 设置APK下载失败通知栏样式
     * @author linzanxian  @Date 2015年1月19日 上午10:57:05
     * description:设置APK下载失败通知栏样式
     * @param item 下载资源项
     * @return void
     */
    public void setApkNotificationDownloadFailView(DownloadItem item) {
        if (item == null) {
            return;
        }
        if (item.getApkDownloadType() == ApkDownloadType.DOWNLOAD_TYPE_BIND) {
            return;
        }
        if (mHashMap == null) {
            return;
        }
        if (!item.isVideoType()) {
            int notificationID = getNotificationId(item);
            Notification notification = mHashMap.get(notificationID);
            if (notification == null || notification.contentView == null) {
                return;
            }
            /*

            notification.contentView.setTextViewText(R.id.cms_app_retry_text,
                    mContext.getString(R.string.app_recommend_download_retry_text));
            notification.contentView.setViewVisibility(R.id.cms_app_cancel, View.VISIBLE);
            notification.contentView.setTextViewText(R.id.cms_title_text, item.getTitle()
                    + mContext.getString(R.string.dl_apk_download_fail));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                notification.contentView.setViewVisibility(R.id.cms_app_cancel, View.VISIBLE);
                notification.contentView.setOnClickPendingIntent(R.id.cms_app_retry_text,
                        getDownloadIntent(item, DownloadCommand.START_DOWNLOAD));
                notification.contentView.setOnClickPendingIntent(R.id.cms_app_cancel,
                        getDownloadIntent(item, DownloadCommand.DELETE_DOWNLOAD));
            } else {
                notification.contentView.setViewVisibility(R.id.cms_app_cancel, View.GONE);
                notification.contentIntent = getDownloadIntent(item, DownloadCommand.START_DOWNLOAD);
            }
            manager.notify(notificationID, notification);
            mHashMap.put(notificationID, notification);
            */
        }
    }

    /**
     * 设置APK下载暂停通知栏样式
     * @author linzanxian  @Date 2015年1月19日 上午10:58:10
     * description:设置APK下载暂停通知栏样式
     * @param item 下载资源项
     * @return void
     */
    public void setApkNotificationDownloadPauseView(DownloadItem item) {
        LogHelper.d("Notification", "下载setApkNotificationDownloadPauseView");
        if (item == null) {
            return;
        }
        if (item.getApkDownloadType() == ApkDownloadType.DOWNLOAD_TYPE_BIND) {
            return;
        }
        if (mHashMap == null) {
            return;
        }
        if (!item.isVideoType()) {
            int notificationID = getNotificationId(item);
            Notification notification = mHashMap.get(notificationID);
            if (notification == null || notification.contentView == null) {
                return;
            }
            /*

            notification.contentView.setViewVisibility(R.id.cms_progress_bar, View.GONE);
            notification.contentView.setViewVisibility(R.id.cms_app_retry_text, View.VISIBLE);
            notification.contentView.setTextViewText(R.id.cms_app_retry_text, "点击继续");
            notification.contentView
                    .setTextViewText(R.id.cms_title_text, item.getTitle() + "下载已暂停");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                notification.contentView.setViewVisibility(R.id.cms_app_cancel, View.VISIBLE);
                notification.contentView.setOnClickPendingIntent(R.id.cms_app_cancel,
                        getDownloadIntent(item, DownloadCommand.DELETE_DOWNLOAD));
                notification.contentView.setOnClickPendingIntent(R.id.cms_app_retry_text,
                        getDownloadIntent(item, DownloadCommand.START_DOWNLOAD));
            } else {
                notification.contentIntent = getDownloadIntent(item, DownloadCommand.START_DOWNLOAD);
            }
            manager.notify(notificationID, notification);
            mHashMap.put(notificationID, notification);
            */
        }
    }

    /**
     * 显示成功或者失败通知
     * @author linzanxian  @Date 2015年1月19日 上午10:59:28
     * description:显示成功或者失败的nofification
     * @param completeItem 己完成下载资源项
     * @param failedItem 下载失败资源项
     * @return void
     */
    public void showWarnNotificaction(DownloadItem completeItem, DownloadItem failedItem) {
        // 如果完成个数和结束个数都为0则不显示
    	/*
        if (failedItem != null) {
            //LogHelper.d(TAG, "showWarnNotificaction failedItem =  " + failedItem.getAid() + failedItem.getSeq());
            if (!failedItemList.contains(failedItem.getAid() + failedItem.getSeq())) {
                failedItemList.add(failedItem.getAid() + failedItem.getSeq());
            }
        }

        if (completeItem != null) {
            if (!completeItemList.contains(completeItem.getAid() + completeItem.getSeq())) {
                completeItemList.add(completeItem.getAid() + completeItem.getSeq());
            }
        }

        createNewWarnningNotification(completeItemList.size(), failedItemList.size());
        warnningNotification.contentView.setTextViewText(R.id.download_fail_text,
                getFailedText(completeItemList.size(), failedItemList.size()));
        manager.notify(mWarnNotificationID, warnningNotification);
        */
    }

    /**
     * 清除下载通知
     * @author linzanxian  @Date 2015年1月19日 上午11:00:24
     * description:清除完成或失败下载通知
     * @return void
     */
    public void cancelWarnningNotification() {
        this.completeItemList.clear();
        this.failedItemList.clear();
        manager.cancel(mWarnNotificationID);
    }

    /**
     * 清除通知状态栏
     * @author linzanxian  @Date 2015年1月19日 上午11:01:15
     * description:取消通知状态栏
     * @param item 下载资源项
     * @return void
     */
    public void clearNotification(DownloadItem item) {
        manager.cancel(getNotificationId(item));
    }

    /**
     * 清除通知状态栏
     * @author linzanxian  @Date 2015年1月19日 上午11:01:15
     * description:取消通知状态栏
     * @param id ID
     * @return void
     */
    public void clearNotification(int id) {
        manager.cancel(id);
    }

    /**
     * 清除所有下载通知栏
     * @author linzanxian  @Date 2015年1月19日 上午11:01:15
     * description:清除所有下载通知栏
     * @param item 下载资源项
     * @return void
     */
    public void cancelAllNotification() {
        manager.cancelAll();
    }

    /**
     * 更新通知状态栏下载进度
     * @author linzanxian  @Date 2015年1月19日 上午11:01:15
     * description:更新通知状态栏下载进度
     * @param item 下载资源项
     * @return void
     */
    public void updateNotification(DownloadItem item) {
        if (item == null) {
            return;
        }
        if (mHashMap == null) {
            return;
        }
        /*
        int mId = getNotificationId(item);
        Notification notification = mHashMap.get(mId);

        if (notification != null) {
            notification.contentView.setProgressBar(R.id.cms_progress_bar, item.getTotalSize(), item.getDownloadedSize(), false);
            manager.notify(getNotificationId(item), notification);
        }
        */
    }

    /**
     * 广播注册
     * @author linzanxian  @Date 2015年1月19日 上午11:01:15
     * description:广播注册
     * @param context Context
     * @return void
     */
    @SuppressLint("UseSparseArrays")
	private void initData(Context context) {
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mHashMap == null) {
            mHashMap = new HashMap<Integer, Notification>();
        }
    }

    /**
     * 获取通知栏ID
     * @author linzanxian  @Date 2015年1月19日 上午11:01:15
     * description:根据下载任务获取通知栏id
     * @param item 下载资源项
     * @return int ID
     */
    private int getNotificationId(DownloadItem item) {
    	/*
        if (item == null) {
            return 0;
        }
        if (item.isVideoType()) {
        	return getNotificationIdForVideo(item);
        } else {
        	return getNotificationIdForApk(item);
        }
        */
    	return 0;
    }
    
    /**
     * 通知栏是否己存在
     * @author linzanxian  @Date 2015年1月19日 上午11:04:13
     * description:通知栏是否己存在
     * @param item 下载资源项
     * @return boolean 是否存在
     */
    public boolean isNotificationExist(DownloadItem item){
    	if(item==null){
    		return false;
    	}
    	return mHashMap.containsKey(getNotificationId(item));
    }
}
