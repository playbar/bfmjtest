package com.bfmj.sdk.common;

import com.bfmj.sdk.util.Common;

/**
 * Created by linzanxian on 2016/3/21.
 */
public class Constants {
    /** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      = "1117744451";
    //weixin账号 app_id
    public static final String APP_KEY_WEIXIN = "wx3b2c51a5e12a93da";
    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";



    /**
     * 暴风SSO密钥
     */
    public static final String BF_KEY = "a318c3db00ef4679a8425ff8f0de3221";

    /**
     * 魔镜加密
     */
    public static final String MJ_KEY="@)(*$s123";

    /**
     * 魔镜加密
     */
    public static final String MJ_KEY_LIHAO="Bf@)(*$s1&2^3XVF#Mj";
    /**
     * 魔镜用户中心加密key
     */
    public static final String MJ_UESRCENTER_KEY = "0p9o8i7u";

    /**
     * 检查用户登录token过期的key
     */
    public static final String MJ_CHECKLOGIN_KEY = "45a9a9a3eedf7864";

    /**
     * 线下支付加密
     */
    public static final String BF_TEST_PAY_KEY = "4c48a7afc5baf081bd8a2de9a829ed3a";
    /**
     * 线上支付加密
     */
    public static final String BF_PAY_KEY = "7942ac1f78398454e5ac7af24737e4d0";

    /**
     * 魔镜 请求魔豆处理的key
     */
    public static final String GET_MODOU_URL_KEY = "2a0848847cbf215111ff405c3ae680b8";
    public static final int mOneScreenNum = 4;//一屏显示的数量
    public static final int headStayTimeShort = 500;//头控停留时间
    public static final int headStayTime = 1000;//头控停留时间
    public static final int headStayTimeLong = 1500;//头控停留时间
    public static final int headClickTime = 1500;//头控点击时间
    public static final int headClickTimeShort = 500;//头控点击时间
    public static final float mY = Common.getUnit(0.2f);
    public static final float mZ = 0.4f;
    public static final float scaleNormal = 1.0f;
    public static final float scaleSelect = 1.5f;
    public static final int movePageDelayTime = 700;//延迟翻页间隔
}
