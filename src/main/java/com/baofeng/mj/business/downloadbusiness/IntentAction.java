package com.baofeng.mj.business.downloadbusiness;

/**
 * 下载跳转接口类
 * ClassName: IntentAction <br/>
 * @author linzanxian    
 * @date: 2015年1月19日 上午10:47:49 <br/>  
 * description:跳转接口类
 */
public interface IntentAction {
    public static final String ACTION_WIFI_MODE = "com.smart.action.WIFI_MODE";

    public static final String ACTION_WIFI_VERIFIER = "com.smart.action.WIFI_VERIFIER";

    public static final String ACTION_USB_MODE = "com.smart.action.USB_MODE";

    public static final String ACTION_USB_CONNECTED = "com.storm.smart.action.USB_CONNECTED";

    public static final String ACTION_USB_DISCONNECTED = "com.storm.smart.action.USB_DISCONNECTED";

    public static final String ACTION_WIFI_DISCONNECTED = "com.storm.smart.action.WIFI_DISCONNECTED";

    public static final String ACTION_WIFI_CONNECTED = "com.storm.smart.action.WIFI_CONNECTED";

    public static final String ACTION_WIFI_CONNECT = "com.smart.action.WIFI_MODE";

    public static final String ACTION_USB_CONNECT = "com.smart.action.USB_MODE";

    public static final String ACTION_LOAD_OLD_DOWNLOAD = "load_old";

    public static final String ACTION_ADD_TO_DOWNLOAD = "add_to_download";

    public static final String ACTION_PAUSE_DOWNLOAD = "pause_download";

    public static final String ACTION_PAUSE_FAKEDOWNLOAD = "pause_fake_download";

    public static final String ACTION_PAUSEQUEUE_DOWNLOAD = "pause_queue_download";

    public static final String ACTION_RESUME_DOWNLOAD = "resume_download";

    public static final String ACTION_RESUMEQUEUE_DOWNLOAD = "resume_queue_download";

    public static final String ACTION_FAKE_DOWNLOAD = "fake_download";

    public static final String ACTION_RESUME_DOWNLOAD_PROGRESS = "resume_progress";

    public static final String ACTION_QUEUE_SUCCESS = "com.storm.smart.action.QUEUE_SUCCESS";

    public static final String ACTION_NETWORK_DISCONNECT = "com.storm.smart.action.NETWORK_DISCONNECT";

    public static final String ACTION_NETWORK_3GONLY = "com.storm.smart.action.NETWORK_3G";

    public static final String ACTION_3G_DOWNLOAD = "com.storm.smart.action.3G_DOWNLOAD";

    public static final String ACTION_TASK_EXISTS = "com.storm.smart.action.TASK_EXISTS";

    public static final String ACTION_TASK_FULL = "com.storm.smart.action.TASK_FULL";

    public static final String ACTION_RESOURCE_UNEXISTS = "com.storm.smart.action.RESOURCE_EXISTS";

    public static final String ACTION_NETWORK_PAUSE = "com.storm.smart.action.NETWORK_PAUSE";

    String ACTION_SD_UNMOUNTED = "com.storm.smart.action.SD_UNMOUNTED";

    public static final String ACTION_DOWNLOAD_LIMIT = "com.storm.smart.action.DOWNLOAD_LIMIT";

    public static final String ACTION_SD_NOSPACE = "com.storm.smart.action.SD_NOSPACE";

    public static final String ACTION_URI_INVALID = "com.storm.smart.action.URI_INVALID";

    public static final String ACTION_REFRESH_NOTIFY = "com.storm.smart.action.REFRESH_NOTIFY";

    public static final String ACTION_CANCEL_NOTIFY = "com.storm.smart.action.CANCEL_NOTIFY";

    public static final String ACTION_PHASE_FINISHED = "com.storm.smart.action.PHASE_finished";

    public static final String ACTION_UPDATE_CAPACITY = "com.storm.smart.action.UPDATE_CAPACITY";

    public static final String ACTION_3G_ENDDOWNLOAD = "com.storm.smart.action.3G_ENDDOWNLOAD";

    public static final String ACTION_FILE_UNEXISTS = "com.storm.smart.action.FILE_UNEXISTS";

    public static final String ACTION_CHANGE_NET = "com.storm.smart.action.CHANGE_NET";

    public static final String ACTION_NETWORK_FAIL = "com.storm.smart.action.CONNECT_FAIL";

    public static final String ACTION_DOWNLOAD_COMPLETE = "com.storm.smart.action.download.complete";
    
    public static final String ACTION_DOWNLOAD_SUCCESS = "com.storm.smart.action.download.SUCCESS_ACTION";

    /** 删除离线下载任务广播(发送给下载进度栏) */
    public static final String ACTION_DEL_DOWNLOAD_SUCCESS = "com.storm.smart.action.download.DEL_ACTION";

    /** 删除离线下载任务广播 (发送给详情页) */
    public static final String ACTION_DEL_DOWNLOAD_SUCCESS_TO_DETAILS_ACTIVITY = "com.storm.smart.download.DEL_TO_DETAILS_ACTIVITY";

    public static final String ACTION_DEL_DOWNLOADALL_SUCCESS = "com.storm.downloadlist.null";

    public static final String ACTION_LIB_NETFAIL = "com.storm.smart.action.LIB_NETFAIL";

    public static final String ACTION_PULL_MESSAGE = "com.storm.action.PULL_MESSAGE";

//    public static final String ACTION_LOAD_SUCCESS = "com.storm.smart.action.LOAD_SUCCESS";
//
//    public static final String ACTION_LOAD_FAIL = "com.storm.smart.action.LOAD_FAIL";

    public static final String ACTION_SHOW_BANNER = "com.storm.smart.action.SHOW_BANNER";

    public static final String ACTION_DISMISS_BANNER = "com.storm.smart.action.DISMISS_BANNER";

    // public static final String ACTION_COMPLETE_STATUS =
    // "com.storm.smart.action.COMPLETE_STATUS";

    /** 有新的视频下载任务广播 */
    public static final String ACTION_NEW_DOWNLOAD_ACTION = "com.storm.newdownload.action";

    /** 删除一个或者多个视频下载通知栏广播 */
    public static final String ACTION_DEL_DOWNLOAD_NOTIFICATION = "com.storm.download.delete.notification";

    /** 进入下载页面删除下载结束通知栏广播 */
    public static final String ACTION_DEL_NOTIFICATION_IN_DOWNLOADPAGE = "com.storm.delete.notification.in.downloadpage";

    /** 退出应用时不在后台下载视频删除所有下载通知栏广播 */
    public static final String ACTION_DEL_ALL_NOTIFICATION_EXIT_APP = "com.storm.delete.notification.exit.app";

    public static final String ACTION_DETAIL_BROWSER_ACTIVITY = "com.storm.smart.activity.DetailsBrowserActivity";
    
    /** 删除离线下载任务广播(发送给下载进度栏) */
    public static final String ACTION_DETAIL_PLAY_SEQ = "com.storm.smart.action.detail.PlaySeq";
    
    /** 广播：浏览器播放时点击back键后发送详情页竖屏的广播 */
    public static final String MSG_PORTRAIT_DETAILS_ACTIVITY = "com.storm.portrait.details.activity";
    
    /** 广播：浏览器播放时点击back键后发送详情页竖屏的广播 */
    public static final String MSG_LANDSCAPE_DETAILS_ACTIVITY = "com.storm.landscape.details.activity";
    
    public static final String ACTION_DOWNLOAD_TIP = "com.storm.smart.download.action.tips";
    
    public static final String ACTION_SHOW_SELECT_SDCARD_DIALOG = "com.storm.smart.action.sdcardselect";

    /** 广播：播放器页面mp3对话框消失则恢复播放的广播 */
    public static final String ACTION_RESUME_PLAY_AFTER_DISMP3_DIALOG = "com.storm.smart.action.resume.play";
    
    /** 广播：删除下载任务时显示删除进度对话框 */
    public static final String ACTION_SHOW_DELETE_PROGRESS_DIALOG = "com.storm.smart.action.show.dialog";
    
    /** 广播：删除下载任务完成时隐藏删除进度对话框*/
    public static final String ACTION_DISMISS_DELETE_PROGRESS_DIALOG = "com.storm.smart.action.dismiss.dialog";
    
    
    /** 广播：酷我关联播放时调用酷我下载当前视频的广播*/
    public static final String ACTION_START_KUWO_DOWNLOAD = "cn.kuwo.player.intent.action.mvdownload";
    
    /** 广播：酷我关联播放时传给酷我的广播标示来自于暴风*/
    public static final String ACTION_COM_STORM_SMART = "com.storm.smart.action";
    
    /** 广播：详情页点击站点图标切换站点播放的广播 */
    public static final String MSG_DETAILS_ACTIVITY_CLICK_SITE = "com.storm.details.activity.click.site";
}
