package com.baofeng.mj.business.publicbusiness;

import android.content.Context;
import android.text.TextUtils;

import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.viewutil.LanguageValue;

/**
 * Created by liuchuanchi on 2016/4/30.
 */
public class ConfigUrl {
    public static boolean IsOnline = ChannelUtil.getChannelCode("ONLINE_OFFLINE").equals("1") ? true : false; //开关己迁到主项目的AndroidManifest的meta-data下 (1 :线上， 0:线下)

    public static String MALL_API_MOJING_CN_12_62_8090;
    public static String MALL_API_MOJING_CN_12_61_8090;
    public static String PUBLIC_URL_PORT_8091;
    public static String IMG_STATIC_MOJING_CN_12_58_8002;
    public static String PUBLIC_SHOP_URL = "http://m.mall.mojing.cn";//商城网页地址
    public static String MJ_TASKLIST_URL = "http://mall.api-t.mojing.cn/api/?api=active.modougiftlist";//任务地址
    public static String MJ_TASKGET_URL = "http://mall.api-t.mojing.cn/api/?api=active.getmodougift";//领取魔豆
    public static String COMMENT_lIST_URL = "http://mall.api-t.mojing.cn/api/?api=score.list";//评论地址
    public static String SEND_COMMENT_URL = "http://mall.api-t.mojing.cn/api/?api=score.add";
    public static String FUWU_MOJING_CN = "http://fuwu.mojing.cn";//体验报告
    public static String FUWU_MOJING_CN_12_62_7023 = "";//体验报告
    public static String FUWU_MOJING_CN_12_62_8084 = "";
    public static String WHITE_CHECK_URL = "http://192.168.12.62:7023/api/mobile/checklist";//高清测试白名单地址
    public static String WHITE_CHECK_UPDATE_URL = "http://192.168.12.62:7023/api/mobile/addelete";//更新黑白名单地址
    public static String PUBLIC_VERSION_UPDATE_URL = "http://api.mojing.baofeng.com/mj";//app 更新url
    public static String MJ_CMS_MOJING_CN_12_58_8014;
    public static String MJ_SUBS_KEY = "23456789098765432";//订阅签名，线下
    public static String MJ_SUBS_HOST = "http://192.168.12.110/";//订阅线下hostURL
    public static String PAY_MOJING_CN_12_61_80 = "http://192.168.12.62:8084/api/osmap/getosmap";//开屏图接口
    public static String MJ_OTA_URL = "http://fuwu.mojing.cn/api/ota/upgrade"; //OTA接口
    public static String MJ_GLASSES_URL = "http://fuwu.mojing.cn/api/glasses/list";
    public static String REPORT_DOWNLOAD;//上报下载
    public static String HISTORY_HOST;//播放历史
    public static String SEARCH_HOST = "http://192.168.12.58:8021/160630-1-1-1/android/zh/1/";//地址线上地址
    public static String MAIN_TAB_ORDER = "http://api.mojing.baofeng.com/mj/v1.0/channelinfo.php";
    public static String LIVE_PLUGIN_LIST_URL = "http://192.168.12.61:8251/api/plugin/pluginupdate";//线上 直播插件列表

    public static String RECOMMEND_TOP;//推荐页面顶部数据地址
    public static String RECOMMEND_CONTENT;//推荐页面中部数据地址
    /**
     * 充值接口
     */
    public static String USER_PAY_URL = "http://mall.api.mojing.cn/api";
    public static String FIREWARE_UPDATE_URL;

    public static String FEEDBACK_COMMIT;

    public static String GAME_DIALOG_URL = "http://fuwu.mojing.cn/api/promptmanagement/channelinfo";
    public static String HELP_FEEDBACK_URL = "http://fuwu.mojing.cn/help";

    static {
        if (IsOnline) {
            MALL_API_MOJING_CN_12_62_8090 = "http://mall.api.mojing.cn";
            MALL_API_MOJING_CN_12_61_8090 = "http://mall.api.mojing.cn";
            IMG_STATIC_MOJING_CN_12_58_8002 = "http://img.static.mojing.cn";
            PUBLIC_URL_PORT_8091 = "http://mall.api.mojing.cn";
            MJ_TASKLIST_URL = "http://mall.api.mojing.cn/api/?api=active.modougiftlist";
            MJ_TASKGET_URL = "http://mall.api.mojing.cn/api/?api=active.getmodougift";
            COMMENT_lIST_URL = "http://mall.api.mojing.cn/api/?api=score.list";//评论地址
            SEND_COMMENT_URL = "http://mall.api.mojing.cn/api/?api=score.add";
            FUWU_MOJING_CN = "http://fuwu.mojing.cn";
            FUWU_MOJING_CN_12_62_7023 = "http://fuwu.mojing.cn";
            FUWU_MOJING_CN_12_62_8084 = "http://fuwu.mojing.cn";
            WHITE_CHECK_URL = "http://fuwu.mojing.cn/api/mobile/checklist";
            WHITE_CHECK_UPDATE_URL = "http://fuwu.mojing.cn/api/mobile/addelete";//更新黑白名单地址
            MJ_SUBS_HOST = "http://dingzhi.mojing.cn/";
            MJ_SUBS_KEY = "9307a0236f60d0db31dbf9f3fa6399a1";
            MJ_CMS_MOJING_CN_12_58_8014 = "http://mj.cms.mojing.cn";
            PUBLIC_VERSION_UPDATE_URL = "http://api.mojing.baofeng.com/mj";
            PAY_MOJING_CN_12_61_80 = "http://fuwu.mojing.cn/api/osmap/getosmap";
            REPORT_DOWNLOAD = "http://api.cms.mojing.cn";
            HISTORY_HOST = "http://api.cms.mojing.cn";
            FIREWARE_UPDATE_URL = "http://fuwu.mojing.cn/api/firmware/firmware";
            MJ_GLASSES_URL = "http://fuwu.mojing.cn/api/glasses/list";
            SEARCH_HOST = "http://res.static.mojing.cn/160630-1-1-1/android/zh/1/";
            FEEDBACK_COMMIT = "http://fuwu.mojing.cn/api/feedback/add";
            USER_PAY_URL = "http://mall.api.mojing.cn/api";
            MAIN_TAB_ORDER = "http://api.mojing.baofeng.com/mj/v1.0/channelinfo.php";
            LIVE_PLUGIN_LIST_URL = "http://fuwu.mojing.cn/api/plugin/pluginupdate";
            RECOMMEND_TOP = "http://res.static.mojing.cn/160630-1-1-1/android/zh/1/block/blockinfo/457017.js ";
//            RECOMMEND_CONTENT = "http://res.static.mojing.cn/160630-1-1-1/android/zh/1/rec_index/";
            RECOMMEND_CONTENT = "http://res.static.mojing.cn/170101-1-1-1/android/zh/1/rec_index/recapp-rand";
            GAME_DIALOG_URL = "http://fuwu.mojing.cn/api/promptmanagement/channelinfo";
            HELP_FEEDBACK_URL = "http://fuwu.mojing.cn/help";

        } else {
            MALL_API_MOJING_CN_12_62_8090 = "http://192.168.12.62:8090";
            MALL_API_MOJING_CN_12_61_8090 = "http://192.168.12.61:8090";
            IMG_STATIC_MOJING_CN_12_58_8002 = "http://192.168.12.58:8002";
            PUBLIC_URL_PORT_8091 = "http://192.168.12.62:8090";
            MJ_SUBS_HOST = "http://192.168.12.110/";
            COMMENT_lIST_URL = "http://mall.api-t.mojing.cn/api/?api=score.list";//评论地址
            FUWU_MOJING_CN = "http://192.168.12.61:8881";
            FUWU_MOJING_CN_12_62_7023 = "http://192.168.12.62:7023";
            FUWU_MOJING_CN_12_62_8084 = "http://192.168.12.62:8084";
            MJ_SUBS_KEY = "23456789098765432";
            MJ_TASKLIST_URL = "http://mall.api-t.mojing.cn/api/?api=active.modougiftlist";
            MJ_TASKGET_URL = "http://mall.api-t.mojing.cn/api/?api=active.getmodougift";
            SEND_COMMENT_URL = "http://mall.api-t.mojing.cn/api/?api=score.add";
            WHITE_CHECK_URL = "http://192.168.12.62:7023/api/mobile/checklist";
            WHITE_CHECK_UPDATE_URL = "http://192.168.12.62:7023/api/mobile/addelete";//更新黑白名单地址
            MJ_CMS_MOJING_CN_12_58_8014 = "http://192.168.12.58:8014";
            PUBLIC_VERSION_UPDATE_URL = "http://192.168.12.61:8886/release_api";
            PAY_MOJING_CN_12_61_80 = "http://192.168.12.62:8084/api/osmap/getosmap";
            REPORT_DOWNLOAD = "http://mojingcms.cc/mojingcms";
            HISTORY_HOST = "http://192.168.12.58:8021";
            FIREWARE_UPDATE_URL = "http://192.168.12.62:8084/api/firmware/firmware";
            MJ_GLASSES_URL = "http://192.168.12.62:8084/api/glasses/list";
            SEARCH_HOST = "http://192.168.12.58:8021/160630-1-1-1/android/zh/1/";
            FEEDBACK_COMMIT = "http://192.168.12.62:8084/api/feedback/add";
            USER_PAY_URL = "http://192.168.12.62:8090/api";
            MAIN_TAB_ORDER = "http://192.168.12.61:8886/release_api/v1.0/channelinfo.php";
            LIVE_PLUGIN_LIST_URL = "http://192.168.12.61:8251/api/plugin/pluginupdate";
            RECOMMEND_TOP = "http://192.168.12.58:8021/160630-1-1-1/android/zh/1/block/blockinfo/456871.js ";
//            RECOMMEND_CONTENT = "http://192.168.12.58:8023/160630-1-1-1/android/zh/1/rec_index/";
            RECOMMEND_CONTENT = "http://192.168.12.58:8021/170101-1-1-1/android/zh/1/rec_index/recapp-rand";
            GAME_DIALOG_URL = "http://192.168.12.62:7023/api/promptmanagement/channelinfo";
//            GAME_DIALOG_URL = "http://xml.fuwu.mojing.cn/api/promptmanagement/channelinfo";
//            GAME_DIALOG_URL = "http://192.168.12.61:8091/api/promptmanagement/channelinfo";
            HELP_FEEDBACK_URL = "http://192.168.12.62:8084/help";
        }
    }

    public static String getServiceUrl(Context mContext) {
        return "http://" + LanguageValue.getInstance().getServerValue(mContext, "hostname") + getServiceUrlSuffix();
    }

    public static String getServiceUrlSuffix() {
		//TODO dupengwei
        if(UnityActivity.mIsYTJ){
            return "/161010-1-1-1/oneplus/zh/";
        }else {
            return "/170501-1-" + BaseApplication.INSTANCE.channelCheckState + "-1/android/zh/";
        }


    }

    public static String getServiceBaseUrl(Context mContext) {
        return "http://" + LanguageValue.getInstance().getServerValue(mContext, "hostname");
    }

    public static String getMainTabUrl(Context mContext) {
//        return SERVICE_URL + "1/nav-index.js";
        return getServiceUrl(mContext) + LanguageValue.getInstance().getServerValue(mContext, "nav_enter");
//    	return SERVICE_URL + "1/nav.js";
    }

    //查询所有支付信息
    public static String queryAllModouUrl() {
        return MALL_API_MOJING_CN_12_62_8090 + "/api/?api=consume.getAllBill";
    }

    //魔豆支付接口
    public static String getModouPayUrl() {
        //return MALL_API_MOJING_CN_12_62_8090 + "/api?api=consume.v3.paymodou";
        return MALL_API_MOJING_CN_12_62_8090 + "/api?api=consume.v4.paymodou";
    }

    //魔币支付接口
    public static String getMobiPayUrl() {
        //return MALL_API_MOJING_CN_12_62_8090 + "/api?api=consume.v3.paymobi";
        return MALL_API_MOJING_CN_12_62_8090 + "/api?api=consume.v4.paymobi";
    }

    //验证码地址
    public final static String MJ_VERIFYCODE_URL = "http://sso.mojing.cn/user/api/sendsmscode";
    //重设密码地址
    public final static String MJ_RESETPWD_URL = "http://sso.mojing.cn/user/api/getuserpassword";
    //绑定手机号接口
    public final static String MJ_BINDPHONE_URL = "http://sso.mojing.cn/user/api/partyregist";
    //注册地址
    public final static String MJ_REGISTER_URL = "http://sso.mojing.cn/user/api/apiregist";
    //第三方注册
    public final static String MJ_REGISTER_URL_NEW = "http://sso.mojing.cn/static/app/user20151030/register.html";

    //第三方登录页面
    public final static String MJ_LOGIN_URL_NEW = "http://sso.mojing.cn/static/app/user20151030/login.html";

    //通过user_no或user_tel或user_email或user_name获取用户信息
    public final static String MJ_GET_USER_INFO_URL = "http://sso.mojing.cn/user/api/userinfo";
    //第三方登录
    public final static String MJ_LOGIN_SET_URL = "http://sso.mojing.cn/user/center/loginset";

    //通过第三方openid获取用户编号¶
    public final static String MJ_NO_BY_OPENID_URL = "http://sso.mojing.cn/user/api/nobyopenid";
    //登录
    public final static String MJ_LOGIN_URL = "http://sso.mojing.cn/user/api/apilogin";
    //微信登录
    public final static String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    //魔豆数量更新
    public static String MJ_CHECK_COUNT_URL = PUBLIC_URL_PORT_8091
            + "/api/?api=user.getInfo";
    //兑换券领取魔豆查询
    public static final String EXCHANGE_QUERY_URL = PUBLIC_URL_PORT_8091
            + "/api/?api=topup.getexchangecodeinfo";
    //兑换券领取魔豆礼券
    public static final String EXCHANGE_MODOU_GIFT_URL = PUBLIC_URL_PORT_8091
            + "/api/?api=topup.getexchangemodougift";



    /**
     * 充值订单查询接口
     */
    public static String CHARGE_GETORDERS = PUBLIC_URL_PORT_8091
            + "/api/?api=deposit.getOrder";

    /**
     * 魔镜用户中心修改头像
     */
    public final static String MJ_SETUSERHEADLOGO_URL = "http://sso.mojing.cn/user/api/setuserhead";

    /**
     * 修改用户昵称
     */
    public final static String MJ_SETNICKNAME_URL = "http://sso.mojing.cn/user/api/setusername";

    /**
     * 发送短信验证码
     */
    public final static String MJ_SEND_SMS_CODE_URL = "http://sso.mojing.cn/user/api/sendsmscode";

    /**
     * 修改用户手机号
     */
    public final static String MJ_SET_USER_TEL_URL = "http://sso.mojing.cn/user/api/setusertel";

    /***
     * 暴风影音第三方登陆授权
     */
    public static String STORM_OAUTH2 = "http://sso.baofeng.net/api/auth/oauth2";

    /***
     * 暴风影音用户信息
     */
    public static String STORM_USER_INFO = "http://sso.baofeng.net/api/auth/userinfo";

    /**
     * 用户体验报告接口
     */
    //public static final String USER_EXPERIENCE_REPORT = FUWU_MOJING_CN + "/question/question/index";
    public static final String USER_EXPERIENCE_REPORT = FUWU_MOJING_CN + "/question/getmainapi?";

    /**
     * 所有筛选类型接口
     */
//    public final static String MJ_CATE_LIST_URL = SERVICE_URL + "/1/catalist/index.js";
    public static String getMjCateListUrl(Context mContext, String cateUrl) {
        return getServiceUrl(mContext) + cateUrl;
    }

    /**
     * 游戏列表页接口
     *
     * @param urlSuffix
     * @return
     */
    public static String getListUrl(Context mContext, String urlSuffix) {
        return getServiceUrl(mContext) + urlSuffix;
    }

    /**
     * 游戏详情页接口
     *
     * @param urlSuffix
     * @return
     */
    public static String getGameDetailUrl(Context mContext, String urlSuffix) {
        return getServiceUrl(mContext) + urlSuffix;
    }

    /**
     * 获取筛选数据
     *
     * @param urlPart
     * @param startNum 起始位置
     * @param dataNum  请求的数据数量
     * @param urlTail  url尾部
     * @return
     */
    public static String getSelectDataUrl(Context mContext, String urlPart, int startNum, int dataNum, String urlTail) {
        return getServiceUrl(mContext) + urlPart + "start" + startNum + "-" + "num" + dataNum + urlTail;
    }

    /**
     * 高清测试白名单接口
     *
     * @param serOpter 手机运营商：联通、移动、未知
     * @param brand    手机品牌
     * @param model    手机型号
     * @param insCode  手机cpu指令型号
     * @param hardCode 手机cpu硬件型号
     * @param time     时间戳
     * @param sign     加密sign
     * @return
     */
    public static String getCheckUrl(String serOpter, String brand, String model, String insCode, String hardCode, String time, String sign) {
        if (TextUtils.isEmpty(serOpter) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(model) || TextUtils.isEmpty(insCode)
                || TextUtils.isEmpty(hardCode) || TextUtils.isEmpty(time) || TextUtils.isEmpty(sign)) {
            return "";
        } else {
            return WHITE_CHECK_URL + "?service_operator=" + serOpter + "&mobile_brand=" + brand + "&mobile_model=" + model
                    + "&cpu_instruct_model=" + insCode + "&cpu_hardware_model=" + hardCode + "&time=" + time + "&sign=" + sign;
        }
    }

    /**
     * 获取推送的接口
     */
    public static String getPushUrl() {
        int mojingApp = ConfigConstant.getMojingApp();
        if(ConfigConstant.MOJING_PRO == mojingApp) {//魔镜pro
            return FUWU_MOJING_CN_12_62_8084 + "/resource/android/message_push/tongyong.js";
        }else {//魔镜mini
            return FUWU_MOJING_CN_12_62_8084 + "/resource/android/message_push/mini.js";
        }
    }

    /**
     * 获取是否有最新礼券活动接口
     *
     * @return
     */
    public static String getIfHasNewGiftUrl() {
        return MALL_API_MOJING_CN_12_61_8090 + "/api?api=active.newmodougift";
    }

    //安装包升级接口
    public static String VERSION_CODE_PATH = PUBLIC_VERSION_UPDATE_URL + "/v1.0/mjupgrade.php";

    /**
     * 获取渠道审核url
     */
    public static String getChannelCheckUrl() {
        return PUBLIC_VERSION_UPDATE_URL + "/v1.0/mjiosgetconfig.php?";
    }

    /**
     * 获取历史记录url（上报历史记录，获取历史记录，公用此接口）
     *
     * @return
     */
    public static String getRequestHistoryUrl() {
        return HISTORY_HOST + "/api/record/openlog";
    }

    /**
     * 获取删除历史记录url
     *
     * @return
     */
    public static String getDeleteHistoryUrl() {
        return HISTORY_HOST + "/api/record/removelog";
    }

    /**
     * 扫码订阅地址
     *
     * @return
     */
    public static String getSubUrl() {
        return MJ_SUBS_HOST + "Api/customize/getalbumprofile";
    }

    /**
     * 获取用户订阅专辑地址
     *
     * @return
     */
    public static String getAlbumUrl() {
        return MJ_SUBS_HOST + "Api/customize/getalbumlist";
    }

    /**
     * 取消订阅地址
     *
     * @return
     */
    public static String getCancleAlbumUrl() {
        return MJ_SUBS_HOST + "Api/customize/albumunsubscribe";
    }

    /**
     * 获取列表页url
     *
     * @param url
     * @param pageNum
     * @param dataNum
     * @return
     */
    public static String getListMoreUrl(String url, int pageNum, int dataNum) {
        return Common.getUrlHead(url) + "-" + "start" + pageNum + "-" + "num" + dataNum + Common.getUrlTail(url);
    }

    /**
     *
     * @param url
     * @param keyname
     * @param resid
     * @param pageNum
     * @param dataNum
     * @return
     */
    public static String getCategoryUrl(String url, String keyname, String resid, int pageNum, int dataNum){
        return Common.getUrlHead(url) + "-" + keyname + resid + "-" + "start" + pageNum + "-" + "num" + dataNum + Common.getUrlTail(url);
    }

    public static String getCategoryUrl1(String url, String keyname, String resid, String category, int pageNum, int dataNum){
        return Common.getUrlHead(url) + "-" + keyname + resid + "-" + category + "start" + pageNum + "-" + "num" + dataNum + Common.getUrlTail(url);
    }


    /**
     * 获取splash图片Url
     */
    public static String getSplashImgUrl() {
        return PAY_MOJING_CN_12_61_80;
    }

    /**
     * 获取上报url
     *
     * @param resId 资源id
     */
    public static String getReportDownloadUrl(String resId) {
        return REPORT_DOWNLOAD + "/api/update_count?id=" + resId;
    }

    /**
     * 获取是否支付url
     *
     * @return
     */
    public static String getIfPayedUrl() {
        return MALL_API_MOJING_CN_12_62_8090 + "/api/?api=topup.getcheckconsume";
    }

    /**
     * 获取搜索热词接口地址
     *
     * @return
     */
    public static String getSearchKeyWord() {
        return SEARCH_HOST + "hotwords.js";
    }

    /**
     * 获取搜索结果列表地址
     *
     * @return
     */
    public static String getSearchListUrl(String urlTail) {
        return SEARCH_HOST + "search/" + urlTail;
    }
}
