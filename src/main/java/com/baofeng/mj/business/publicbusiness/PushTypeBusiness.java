package com.baofeng.mj.business.publicbusiness;

/**
 * Created by liuchuanchi on 2016/4/19.
 * push跳转类型工具类
 */
public class PushTypeBusiness {
    /**
     * push跳转类型
     */
    public static final String redirect_no = "0";//不跳转
    public static final String redirect_out = "1";//外部跳转
    public static final String redirect_inner = "2";//内部跳转

    /**
     * push链接类型
     */
    public static final String link_home = "1";//链接-首页
    public static final String link_task = "2";//链接-任务页
    public static final String link_experience_report = "3";//链接-体验报告页
    public static final String link_category = "4";//链接-分类页
    public static final String link_rank = "5";//链接-榜单页
    public static final String link_channel = "6";//链接-频道页
    public static final String link_special = "7";//链接-专题页
    public static final String link_detail = "8";//链接-详情页

    /**
     * push样式(图片push)
     */
    public static final String style_one = "1";//样式1
    public static final String style_two = "2";//样式2

    public static final int from_where_normal = 0;//正常进入的主页面
    public static final int from_where_out = 1;//从第三方应用进入的主页面
    public static final int from_where_landscape_local = 2;//从横屏进入竖屏本地页面
    public static final int from_where_landscape_download = 3;//从横屏进入竖屏下载页面

    public static final int operate_install_apk = 1;//操作是：安装apk

    public static String FROM_WHERE = "from_where";
    public static String FROM_APP_NAME = "fromAppName";//来自第三方应用的名称
    public static String OPERATE_JSON = "operateJson";//操作json
    public static String NOTIFY_ID = "notify_id";//通知id
    public static String LINK_TYPE = "link_type";//链接类型
    public static String REDIRECT_ID = "redirect_id";//跳转id
    public static String REDIRECT_URL = "redirect_url";//跳转url
    public static String RESOURCE_TYPE_PARENT = "resource_type_parent";//资源类型
    public static String RESOURCE_TYPE = "resource_type";//跳转子类型
}
