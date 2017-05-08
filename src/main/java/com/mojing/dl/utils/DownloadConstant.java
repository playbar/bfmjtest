
package com.mojing.dl.utils;

public interface DownloadConstant {

    public static final String defaultUserAgent = "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    /**
     * 每个下载任务的默认下载线程数
     */
    public static final int DEFAULT_DOWNLOADTHREAD_COUNT = 1;

    /**
     * 每个下载任务的默认下载失败重试次数
     */
    public static final int DEFAULT_DOWNLOAD_RETRY_COUNT = 2;

    public static final String DOWNLOAD_ITEM = "downloadItem";
    
    public static final String DOWNLOAD_ROOT_DIR = ".baofeng-download-root";

    public static final String NEW_DOWNLOAD_ROOT_DIR = "baofeng/.download";

    /**
     * 本地缓存界面离线缓存单个文件删除时是否应该弹框
     */
    public static final String DELETE_DOWNLOND_VIDEO = "delete_downlond_video";

    public interface PauseReason {
        public int USER_CLICK = 0;

        public int WIFI_UNREACHABLE = 1;

        public int SDCARD_UNPREPARED = 2;

        public int MOBILE_NETWORK_UNPERMITTED = 3;
        
        public int NETWORK_UNREACHABLE = 4;

        public int OTHER = 1000;
    }

    public interface ResumeReason {
        public int USER_CLICK = 0;

        public int WIFI_RESUME = 1;

        public int SDCARD_PREPARED = 2;

        public int MOBILE_NETWORK_PERMITTED = 3;

        public int MOBILE_NETWORK_RESUME = 4;
        
        public int OTHER = 1000;
    }

    public interface DownloadCommand {
        public String DL_COMMAND = "download_command";

        public int START_DOWNLOAD = 1;

        public int PAUSE_DOWNLOAD = 2;

        public int DELETE_DOWNLOAD = 3;

        public int PAUSE_ALL_DOWNLOADS = 4;

        public int PAUSE_ALL_APK_DOWNLOADS = 5;

        public int PAUSE_ALL_VIDEO_DOWNLOADS = 6;

        public int RESUM_ALL_DOWNLOADS = 7;

        public int DOWNLOAD_APK_INSTALL = 8;

        public int CHANGE_DL_MAX_COUNT = 9;

        public int DL_SPEED_LIMIT_OPEN = 10;

        public int DL_SPEED_LIMIT_CLOSE = 11;

        public int DL_CREATE_BIND_APK_SHORCUT = 12;

        public int CLEAR_NOTIFICATION = 13;
        
        public int CHANGE_DOWNLOAD_PATH = 14;
        
        public int DL_PREDOWNLOAD_COMMAND = 15;
        
        public int DL_DELETE_PREDOWNLOAD = 16;
        
        public int DL_ADD_WAITING_DOWNLOAD_TASK = 17;
        
        public int DL_DOWNLOAD_LIST_IN_3G = 18;
        
        public int DL_CHECK_GAME_UPDATE = 19;
        
        public int DL_DELETE_GAME_FILE = 20;
        
        public int DL_CLEAR_DOWNLOAD_NOTIFICATION = 21;
        
        public int DL_START_DOWNLOAD_PRELIST = 22;
        
        public int DL_SEND_GAME_INSTALL_COUNT = 23;
        
        public int RESUME_ALL_VIDEO_DOWNLOADS = 24;
        
        public int DL_DOWNLOAD_PRE_GAME_APP = 25;
    }

    /**
     * 下载模式
     */
    public interface IDownloadMode {

        // 普通下载模式
        final int MODE_NORMAL_SPEED = 1;

        // 极速下载模式
        final int MODE_EXTREME_SPEED = 2;
    }

    /**
     * 下载项DownloadItem的类别，1,2,3分别为APK文件下载，暴风视频下载和普通文件下载
     * 
     * 2014-5-21增加游戏大厅app下载类型ITEM_TYPE_GAME
     * @author chengzhenyu
     */
    public interface DownloadItemType {
        public int ITEM_TYPE_APK = 1;

        public int ITEM_TYPE_VIDEO = 2;

        public int ITEM_TYPE_SIMPLE = 3;

        public int ITEM_TYPE_P2P = 4;
        
        public int ITEM_TYPE_GAME = 5;
    }

    /**
     * 下载类型 1 强制下载：3G预约状态下也可正常下载 2.预约下载 ：3G状态下任务暂停，等待网络恢复 3.正常下载
     */
    public interface DownloadType {
        public int DOWN_TYPE_FORCEDOWNLOAD = 1;

        public int DOWN_TYPE_APPOINTMENT = 2;

        public int DOWN_TYPE_NORMAL = 3;
    }

    /**
     * 当前文件的下载状态
     * 
     * @author chengzhenyu
     */
    public interface DownloadState {
//        public static final int TASK_STATE_NO_BEGIN = 0;// 还未开始下载
//
//        public static final int TASK_STATE_PAUSE = 1;// 确定暂停
//
//        public static final int TASK_STATE_DOWNLOADING = 2;// 正在下载中
//
//        public static final int TASK_STATE_COMPLETE = 3;// 完成
//
//        public static final int TASK_STATE_WAITING = 4;// 等待下载
//
//        public static final int TASK_STATE_FAIL = 5;// 下载失败

//        public static final int DEFAULT = 0;
//        public static final int CONNECTING = 1;
//        public static final int DOWNLOADING = 2;
//        public static final int PAUSED = 3;
//        public static final int COMPLETE = 4;
//        public static final int ERROR = 5;
//        public static final int ABORT = 6;
//        public static final int WAITING = 7;

    }

    /**
     * 下载错误代码
     * 
     * @author chengzhenyu
     */
    public interface ErrorCode {
        public static final int ERROR_CODE_CRACK_FAILED = 1;// 文件下载地址破解失败

        public static final int ERROR_CODE_HTTP_FAILED = 2;// 下载过程中因http获取数据异常导致失败

        public static final int ERROR_CODE_GET_FILE_INFO_FAILED = 3;// 破解成功后获取文件信息时失败
        
        public static final int ERROR_CODE_SDCARD_SPACE_FULL = 4;  //下载过程中磁盘空间不足
        
        public static final int ERROR_CODE_SDCARD_GONE = 5;  //下载过程中磁盘被卸载
        
        public static final int ERROR_CODE_CRACK_NULL = 6;  //下载过程破解返回空
        
        public static final int ERROR_CODE_CRACK_UNSATISFIED = 7;  //下载过程中得到的破解信息不符合筛选条件

    }

    public interface ApkDownloadType {

        /**
         * APK类型.升级
         */
        int DOWNLOAD_TYPE_UPDATE = 1;
        
        int DOWNLOAD_TYPE_UPDATE_FORCE = 4;

        /**
         * APK类型.播放插件
         */
        int DOWNLOAD_TYPE_BF_PLUGIN = 2;

        /**
         * APK类型捆绑
         */
        int DOWNLOAD_TYPE_BIND = 3;
        
        int DOWNLOAD_TYPE_PREDOWN = 5;
        
    }

    public interface ApkFromType {
        /**
         * 点亮
         */
        public static String APP_DOWNLOAD_TYPE_START = "start";

        /**
         * 首页推荐按钮
         */
        public static String APP_DOWNLOAD_TYPE_WEIGHING = "weighing";

        /**
         * 天天動聽
         */
        public static String APP_DOWNLOAD_TYPE_TTPOD = "ttpod";

        /**
         * 一键省电
         */
        public static String APP_DOWNLOAD_TYPE_ONEKYE = "onekye";

        /**
         * 焦点图
         */
        public static String APP_DOWNLOAD_TYPE_FOCUS = "focus";

        /**
         * 詳情頁
         */
        public static String APP_DOWNLOAD_TYPE_DETAIL = "detail";
        /**
         * 詳情頁banner
         */
        public static String APP_DOWNLOAD_TYPE_DETAIL_BANNER = "detail_banner";

        /**
         * 快播
         */
        public static String APP_DOWNLOAD_TYPE_KUAIBO = "kuaibo";

        /**
         * 捆綁
         */
        public static String APP_DOWNLOAD_TYPE_BIND = "bind";

        /** 茄子对话框展示 */
        public static String APP_DOWNLOAD_TYPE_QIEZI_DIALOG = "qiezi_dialog";

        /** 茄子浮窗展示 */
        public static String APP_DOWNLOAD_TYPE_QIEZI_BANNER = "qiezi_banner";

        /**
         * 小站插件下载
         */
        public static String APP_DOWNLOAD_TYPE_SMALLSITE = "smallsite";

        /**
         * 专题块产品合作Apk下载
         */
        public static String APP_DOWNLOAD_TYPE_PRODUCT_COOPERATE = "product_cooperate";

        /**
         * 详情页产品合作Apk下载
         */
        public static String APP_DOWNLOAD_TYPE_PRODUCT_COOPERATE_DETAIL = "product_cooperate_detail";
        
        /**
         * 今日头条
         */
        public static String APP_DOWNLOAD_TYPE_HEADLINE = "headline";
        
        /**
         * 本地视频页清理按钮合作方下载
         */
        public static String APP_DOWNLOAD_TYPE_CLEARBUT_COOPERATE = "clearbut_cooperate";
    }

    public static final String UNIT_GB = "G";

    public static final String UNIT_MB = "M";

    public static final String UNIT_KB = "K";

    public static final float SIZE_GB = 1073741824;

    public static final float SIZE_MB = 1048576;

    public static final float SIZE_KB = 1024;

    public static final String DOT = ".";

    /**
     * 下载界面的菜单项对应的行为
     */

    /** 离线播放 */
    public static final int MENU_ACTION_PLAY_FILE = 0;

    /** 极速传片 */
    public static final int MENU_ACTION_TRANSPORT_FILE = 1;

    /** 删除 */
    public static final int MENU_ACTION_DELETE_FILE = 2;

    /** 暂停缓存 */
    public static final int MENU_ACTION_PAUSE_DOWNLOAD_FILE = 3;

    /** 继续缓存 */
    public static final int MENU_ACTION_CONTINUE_DOWNLOAD_FILE = 4;

    /** 重新缓存 */
    public static final int MENU_ACTION_RETRY_DOWNLOAD_FILE = 5;

    /** 使用流量缓存 */
    public static final int MENU_ACTION_USE_3G_DOWNLOAD_FILE = 6;

    /** 影片详情 */
    public static final int MENU_ACTION_DETAIL_FILE = 7;

    /** 全部暂停 */
    public static final int MENU_ACTION_PAUSE_ALL_DOWNLOAD_FILE = 8;

    /** 全部开始 */
    public static final int MENU_ACTION_START_ALL_DOWNLOAD_FILE = 9;

    /** 展开 */
    public static final int MENU_ACTION_CLICK_FILE = 10;

    /** 预约下载 */
    public static final int MENU_ACTION_DOWNLOAD_ORDER_FILE = 11;

    /** 失败继续缓存 */
    public static final int MENU_ACTION_DOWNLOAD_FAIL_CONTINUE = 12;

    public static final String FROM_DOWNLOAD = "download";
}
