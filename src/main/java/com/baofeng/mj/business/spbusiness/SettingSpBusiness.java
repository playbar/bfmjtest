package com.baofeng.mj.business.spbusiness;

import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;

import com.baofeng.mj.business.firewarebusiness.FirewareBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesSdkBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.SecurePreferences;
import com.baofeng.mj.util.systemutil.BrightnessUtil;
import com.baofeng.mj.vrplayer.utils.SoundUtils;
import com.baofeng.mojing.MojingSDK;
import com.bfmj.sdk.dao.GLobShareprefenceKey;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import org.json.JSONObject;


/**
 * @author liuchuanchi
 * @description: 设置保存业务
 */
public class SettingSpBusiness {
    public static final String PUBLIC_GLASSES_IDS_PREFENCE = "public_glasses_ids_prefence";
    //声音
    public static final String  PLAYER_SOUND_VALUE_PREFENCE = "player_sound_value_prefence";
    //是否静音
    public static final String PLAYER_SOUND_ISMUTE="player_sound_ismute";
    private static SettingSpBusiness instance;
    private SecurePreferences securePreferences;
    private SecurePreferences.Editor editor;

    private SettingSpBusiness() {
    }

    public static SettingSpBusiness getInstance() {
        if (instance == null) {
            instance = new SettingSpBusiness();
        }
        return instance;
    }

    /**
     * 初始化SecurePreferences
     */
    private void initSecurePreferences() {
        if (securePreferences == null) {
            securePreferences = new SecurePreferences(ConfigConstant.PACKAGE_NAME + ".setting", Context.MODE_PRIVATE);
            editor = securePreferences.edit();
        }
    }


    /**
     * 保存上次设置的声音
     *
     * @param userCurVolume 0-100
     */
    public void setCurrentVolume(int userCurVolume) {
        initSecurePreferences();
        editor.putInt("currentVolumeMode", userCurVolume);
        editor.commit();
    }

    /**
     * 获取上次保存的声音大小
     *
     * @return 0-100
     */
    public int getCurrentVolume() {
        initSecurePreferences();
        return securePreferences.getInt("currentVolumeMode", 0);
    }


    /**
     * 设置亮度模式
     *
     * @param brightnessMode 0：自动 1：手动
     */
    public void setBrightnessMode(int brightnessMode) {
        initSecurePreferences();
        editor.putInt("brightnessMode", brightnessMode);
        editor.commit();
    }

    /**
     * 获取亮度模式
     *
     * @return 0：自动 1：手动
     */
    public int getBrightnessMode() {
        initSecurePreferences();
        return securePreferences.getInt("brightnessMode", 0);
    }

    /**
     * 设置亮度值
     *
     * @param brightnessValue 亮度值 0 - 255
     */
    public void setBrightnessValue(int brightnessValue) {
        initSecurePreferences();
        editor.putInt("brightnessValue", brightnessValue);
        editor.commit();
    }

    /**
     * 获取亮度值
     *
     * @return 亮度值 0 - 255
     */
    public int getBrightnessValue() {
        initSecurePreferences();
        int value = securePreferences.getInt("brightnessValue", -1);
        if (value < 0){
            value = BrightnessUtil.isAutoBrightnessMode() ? (int)(255 * 0.3) : BrightnessUtil.getSysBrightnessValue();
        }
        return value;
    }

    /**
     * 设置系统亮度值
     *
     * @param brightnessValue 亮度值 0 - 255
     */
    public void setSystemBrightnessValue(int brightnessValue) {
        initSecurePreferences();
        editor.putInt("systemBrightnessValue", brightnessValue);
        editor.commit();
    }

    /**
     * 获取系统亮度值
     *
     * @return 亮度值 0 - 255
     */
    public int getSystemBrightnessValue() {
        initSecurePreferences();
        return securePreferences.getInt("systemBrightnessValue", BrightnessUtil.getSysBrightnessValue());
    }

    /***
     * 飞屏设置跳过引导页
     *
     * @param skipGuide
     */
    public void setFlyScreenSkipGuide(boolean skipGuide) {
        initSecurePreferences();
        editor.putBoolean("skipGuide", skipGuide);
        editor.putBoolean("beginGuide", !skipGuide);
        editor.commit();
    }

    /**
     * 飞屏跳过引导页
     */
    public boolean getFlyScreenSkipGuide() {
        initSecurePreferences();
        return securePreferences.getBoolean("skipGuide", false);
    }

    public boolean getFlyScreenBeginStepGuide() {
        initSecurePreferences();
        return securePreferences.getBoolean("beginGuide", false);
    }

    public void setFlyScreenBeginStepGuide(boolean b) {
        initSecurePreferences();
        editor.putBoolean("beginGuide", b);
        editor.commit();
    }


    /***
     * 飞屏Session Id
     *
     * @param sessionId
     */
    public void setFlyScreenSessionId(String sessionId) {
        initSecurePreferences();
        editor.putString("sessionId", sessionId);
        editor.commit();
    }

    /***
     * 获取飞屏session ID
     *
     * @return
     */
    public String getFlyScreenSessionId() {
        initSecurePreferences();
        return securePreferences.getString("sessionId", "");
    }

    /**
     * 设置是否可以GPRS下载
     *
     * @param gprsDownload true可以，false不可以
     */
    public void setCanGPRSDownload(boolean gprsDownload) {
        initSecurePreferences();
        editor.putBoolean("canGprsDownload", gprsDownload);
        editor.commit();
    }

    /**
     * 获取是否可以GPRS下载
     *
     * @return true可以，false不可以
     */
    public boolean getCanGPRSDownload() {
        initSecurePreferences();
        return securePreferences.getBoolean("canGprsDownload", false);
    }

    //保存新浪登陆token
    public void setSinaAccessToken(Oauth2AccessToken token) {
        initSecurePreferences();
        editor.putString("uid", token.getUid());
        editor.putString("access_token", token.getToken());
        editor.putLong("expires_in", token.getExpiresTime());
        editor.commit();
    }

    public Oauth2AccessToken getSinaAccessToken() {
        initSecurePreferences();
        Oauth2AccessToken token = new Oauth2AccessToken();
        token.setUid(securePreferences.getString("uid", ""));
        token.setToken(securePreferences.getString("access_token", ""));
        token.setExpiresTime(securePreferences.getLong("expires_in", 0));
        return token;
    }

    public synchronized String getGlassesIds() {
        GlassesNetBean bean = GlassesManager.getGlassesNetBean();
        String ids="";
        if(null != bean){
        try {
            JSONObject json = new JSONObject();
            json.put("manufactureid", bean.getCompany_id());
            json.put("productid", bean.getProduct_id());
            json.put("glassesid", bean.getLens_id());
            ids = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        }

        return ids;
    }


    public synchronized void setGlassesIds(String ids) {
        initSecurePreferences();
        editor.putString(PUBLIC_GLASSES_IDS_PREFENCE, ids);
        editor.commit();
    }

    public void setString(String key, String value) {
        initSecurePreferences();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        initSecurePreferences();
        return securePreferences.getString(key, "");
    }

    public String getGlassesModeKey() {
        GlassesSdkBean bean = GlassesManager.getGlassesSdkBean();
        String key = "";
        if(bean != null){
            key = bean.getGlassesKey();
        }
        return key;
    }

    /**
     * 保存服务器上返回眼镜列表上的glassesId，不同于sdk中的三个Id唯一确认一款眼镜
     * @param glassesId
     */
    public void setCMSGlassesId(String glassesId){
        initSecurePreferences();
        editor.putString("cms_glassesId", glassesId);
        editor.commit();
    }

    public String getCMSGlassesId(){
        initSecurePreferences();
        return securePreferences.getString("cms_glassesId","");
    }




    /**
     * 设置是否完成引导页面
     * @param finish
     */
    public void setFinishGuide(boolean finish){
        initSecurePreferences();
        editor.putBoolean("isFirstGuide", finish);
        editor.commit();
    }

    /**
     * 设置是否完成引导页面
     */
    public boolean getFinishGuide(){
        initSecurePreferences();
        return securePreferences.getBoolean("isFirstGuide", false);
    }

    /**
     * 设置本地视频排序规则
     *
     * @param sortRule 0
     */
    public void setLocalVideoSort(int sortRule) {
        initSecurePreferences();
        editor.putInt("localVideoSort", sortRule);
        editor.commit();
    }

    public int getLocalVideoSort() {
        initSecurePreferences();
        return securePreferences.getInt("localVideoSort", 0);
    }

    /**
     * 增强模式二、三代
     *
     * @param id（1为720p，2为二代，3为三代）
     */
    public void setStrongMode(int id) {
        initSecurePreferences();
        editor.putInt("strongMode", id);
        editor.commit();
    }

    /**
     * 获取增强模式
     *
     * @return
     */
    public int getStrongMode() {
        initSecurePreferences();
        return securePreferences.getInt("strongMode", 2);
    }

    //体验报告url
    public void setReportUrl(String url) {
        initSecurePreferences();
        editor.putString("reportUrl", url);
        editor.commit();
    }

    public String getReprotUrl() {
        initSecurePreferences();
        return securePreferences.getString("reportUrl", "");
    }


    /***
     * 设置体验报告取消次数，如果直接进入体验报告页面，设置为3
     *
     * @param num
     */
    public void setReportDialogCancelCount(int num) {
        String uid = UserSpBusiness.getInstance().getUid();
        if (!TextUtils.isEmpty(uid) && !"-1".equals(uid)) {//已登录
            editor.putInt("reportDialogCancelCount" + uid, num);
            editor.commit();
        }
    }

    /***
     * 获取体验报告取消次数
     *
     * @return
     */
    public int getReportDialogCancelCount() {
        String uid = UserSpBusiness.getInstance().getUid();
        initSecurePreferences();
        return securePreferences.getInt("reportDialogCancelCount" + uid, 0);
    }

    /***
     * 设置上一次
     *
     * @param version
     */
    public void setLastReportAppVersion(String version) {
        String uid = UserSpBusiness.getInstance().getUid();
        if (!TextUtils.isEmpty(uid) && !"-1".equals(uid)) {//已登录
            editor.putString("lastReportAppVersion" + uid, version);
            editor.commit();
        }
    }

    /***
     * 获取体验报告取消次数
     *
     * @return
     */
    public String getLastReportAppVersion() {
        String uid = UserSpBusiness.getInstance().getUid();
        initSecurePreferences();
        return securePreferences.getString("lastReportAppVersion" + uid, "");
    }

    /**
     * 反锯齿开关
     *
     * @param anti_aliasing
     */
    public void setAnti_aliasing(int anti_aliasing) {
        initSecurePreferences();
        editor.putInt("anti_aliasing", anti_aliasing);
        editor.commit();
    }

    /**
     * 获取是否设置反锯齿，0：未设置，1：设置
     *
     * @return
     */
    public int getAnti_aliasing() {
        initSecurePreferences();
        return securePreferences.getInt("anti_aliasing", 0);
    }

    /**
     * 曲面开关
     *
     * @param surSwitch
     */
    public void setSur_Switch(int surSwitch) {
        initSecurePreferences();
        editor.putInt("surSwitch", surSwitch);
        editor.commit();
    }

    /**
     * 获取是否设置曲面，0：未设置，1：设置
     *
     * @return
     */
    public int getSur_Switch() {
        initSecurePreferences();
        return securePreferences.getInt("surSwitch", 1);
    }

    /**
     * 球模背景开关
     *
     * @param bgSwitch
     */
    public void setBgSwitch(int bgSwitch) {
        initSecurePreferences();
        editor.putInt("bgSwitch", bgSwitch);
        editor.commit();
    }

    /**
     * 获取是否设置球模背景，0：未设置，1：设置
     *
     * @return
     */
    public int getBgSwitch() {
        initSecurePreferences();
        return securePreferences.getInt("bgSwitch", 1);
    }

    /**
     * 过渡动画特效开关
     *
     * @param transAniSwitch
     */
    public void setTrans_Ani_Switch(int transAniSwitch) {
        initSecurePreferences();
        editor.putInt("transAniSwitch", transAniSwitch);
        editor.commit();
    }

    /**
     * 获取是否设置过渡动画特效，0：未设置，1：设置
     *
     * @return
     */
    public int getTrans_Ani_Switch() {
        initSecurePreferences();
        return securePreferences.getInt("transAniSwitch", 0);
    }

    /**
     * 设置透明效果
     *
     * @param transSwitch
     */
    public void setTrans_Switch(int transSwitch) {
        initSecurePreferences();
        editor.putInt("transSwitch", transSwitch);
        editor.commit();
    }

    /**
     * 获取是否设置透明效果，0：未设置，1：设置
     *
     * @return
     */
    public int getTrans_Switch() {
        initSecurePreferences();
        return securePreferences.getInt("transSwitch", 0);
    }

    /**
     * 设置Mask特效
     *
     * @param mask
     */
    public void setMask(int mask) {
        initSecurePreferences();
        editor.putInt("mask", mask);
        editor.commit();
    }

    /**
     * 获取是否设置Mask特效效果，0：未设置，1：设置
     *
     * @return
     */
    public int getMask() {
        initSecurePreferences();
        return securePreferences.getInt("mask", 0);
    }

    /**
     * 保存文本通知id集合
     *
     * @param txtPushIds 文本通知id集合
     */
    public void setTxtPushIds(String txtPushIds) {
        initSecurePreferences();
        editor.putString("txtPushIds", txtPushIds);
        editor.commit();
    }

    /**
     * 获取文本通知id集合
     *
     * @return
     */
    public String getTxtPushIds() {
        initSecurePreferences();
        return securePreferences.getString("txtPushIds", "");
    }

    /**
     * 保存图片通知id集合
     *
     * @param imgPushIds 图片通知id集合
     */
    public void setImgPushIds(String imgPushIds) {
        initSecurePreferences();
        editor.putString("imgPushIds", imgPushIds);
        editor.commit();
    }

    /**
     * 获取图片通知id集合
     *
     * @return
     */
    public String getImgPushIds() {
        initSecurePreferences();
        return securePreferences.getString("imgPushIds", "");
    }

    /***
     * 设置更新app时间
     *
     * @param updateTime
     */
    public void setLastUpdateTime(long updateTime) {
        initSecurePreferences();
        editor.putLong("lastUpdateTime", updateTime);
        editor.commit();
    }

    /***
     * 获取app更新时间
     *
     * @return
     */
    public long getLastUpdateTime() {
        initSecurePreferences();
        return securePreferences.getLong("lastUpdateTime", -1l);
    }

    /**
     * 存储是否需要升级应用状态
     * @param needUpdate
     */
    public void setNeedUpdate(boolean needUpdate){
        initSecurePreferences();
        editor.putBoolean("need_update", needUpdate);
        editor.commit();
    }

    /**
     * 返回是否需要升级应用状态
     * @return
     */
    public boolean getNeedUpdate(){
        initSecurePreferences();
        return securePreferences.getBoolean("need_update", false);
    }

    /**
     * 版本更新逻辑：当前版本存在（非强制）更新时，
     * 弹窗提示新版本特性提示更新。
     * 弹窗只出现一次：用户关闭后不再出现。
     * 当存在下个新版本时，再弹窗提示更新。
     * 标注对应版本是否点击取消按钮
     * @param version
     * @param dismiss
     */
    public void setDismiss(String version,boolean dismiss){
        initSecurePreferences();
        editor.putString("click_dismiss", version + "-" + dismiss);
        editor.commit();
    }

    /**
     * 获取指定版本是否点击过取消过升级，如点击过，下次主页面不再显示升级Dialog
     * @param version
     * @return
     */
    public boolean getDismiss(String version){
        initSecurePreferences();
        String info = securePreferences.getString("click_dismiss", "");
        if(!"".equals(info)){
            String[] infos = info.split("-");
            if(infos[0].equals(version)){
                if("true".equals(infos[1])){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置客服信息小红点逻辑，true有新回复，false,无新回复
     *
     * @param has
     */
    public synchronized void setHasContent(boolean has) {
        initSecurePreferences();
        editor.putBoolean("hasReplay", has);
        editor.commit();
    }

    /**
     * 获取客服信息小红点逻辑，true有新回复，false,无新回复
     *
     * @return
     */
    public synchronized boolean getHasContent() {
        initSecurePreferences();
        return securePreferences.getBoolean("hasReplay", false);
    }

    /**
     * 设置高清测试结果
     *
     * @param high
     */
    public void setHigh(int high) {
        initSecurePreferences();
        editor.putInt("high", high);
        editor.commit();
    }

    /**
     * 获取高清测试结果
     *
     * @return
     */
    public int getHigh() {
        initSecurePreferences();
        return securePreferences.getInt("high", -1);
    }

    /**
     * 设置曲面
     *
     * @param hook
     */
    public void setHook(int hook) {
        initSecurePreferences();
        editor.putInt("hook", hook);
        editor.commit();
    }

    /**
     * 获取曲面
     *
     * @return
     */
    public int getHook() {
        initSecurePreferences();
        return securePreferences.getInt("hook", -1);
    }

    /**
     * 设置otg
     *
     * @param otg
     */
    public void setOtg(int otg) {
        initSecurePreferences();
        editor.putInt("otg", otg);
        editor.commit();
    }

    /**
     * 获取otg
     *
     * @return
     */
    public int getOtg() {
        initSecurePreferences();
        return securePreferences.getInt("otg", -1);
    }

    /**
     * 记录当前tab
     */
    public void setMCurrentTab(int mCurrentTab){ //按照实际tab记录 从0开始
        initSecurePreferences();
        editor.putInt("mCurrentTab", mCurrentTab);
        editor.commit();
    }

    /***
     * 获取当前tab
     */
    public int getMCurrentTab(){
        initSecurePreferences();
        return securePreferences.getInt("mCurrentTab", 0);
    }

    //记录每个Tab最后一次点击时的具体位置
    public void setSubTabPosition(int mainTabPosition, int subTabPosition) {
        initSecurePreferences();
        editor.putInt("main_sub_resid" + mainTabPosition, subTabPosition);
        editor.commit();
    }

    //获取每个Tab最后一次点击时的具体位置
    public int getSubTabPosition(int mainTabPosition) {
        initSecurePreferences();
        return securePreferences.getInt("main_sub_resid" + mainTabPosition, 0);
    }

    //判读Tab是否点击过，如点击过，加载缓存数据
    public void setTabClickStatus(int tabPosition, int subTabPosition, int categoryTabPosition, boolean status) {
        initSecurePreferences();
        editor.putBoolean("main_tab_status" + tabPosition + subTabPosition + categoryTabPosition , status);
        editor.commit();
    }

    public boolean getTabClickStatus(int tabPosition, int subTabPosition, int categoryTab) {
        initSecurePreferences();
        return securePreferences.getBoolean("main_tab_status" + tabPosition + subTabPosition + categoryTab, false);
    }

    //是否缓存过推荐数据，重新进入应用时置空，重新获取
    public void setRecommendData(boolean isLoaded){
        initSecurePreferences();
        editor.putBoolean("recommend_status", isLoaded);
        editor.commit();
    }

    public boolean getRecommendData(){
        initSecurePreferences();
        return securePreferences.getBoolean("recommend_status", false);
    }

    //是否缓存过Home VR中的categoryTab数据，重新进入应用时置空，重新获取
    public void setHomeVRCate(boolean isLoaded){
        initSecurePreferences();
        editor.putBoolean("home_vr_status", isLoaded);
        editor.commit();
    }

    public boolean getHomeVRCate(){
        initSecurePreferences();
        return securePreferences.getBoolean("home_vr_status", false);
    }

    //是否缓存过Home 2D中的categoryTab数据，重新进入应用时置空，重新获取
    public void setHome2DCate(boolean isLoaded){
        initSecurePreferences();
        editor.putBoolean("home_2d_status", isLoaded);
        editor.commit();
    }

    public boolean getHome2DCate(){
        initSecurePreferences();
        return securePreferences.getBoolean("home_2d_status", false);
    }

    public static final int VR_MAX_TAB = 16; //VR下Category数
    public static final int TD_MAX_TAB = 5;  //2D下Category数
    public void clearTabInfo() {
        setHomeVRCate(false);
        setHome2DCate(false);
        setRecommendData(false);
        //清除Tab指定一级，二级,CategoryTab
        for (int i = 0; i < VR_MAX_TAB; i++) {
            setTabClickStatus(0, 0, i, false);
        }
        for (int i = 0; i < TD_MAX_TAB; i++) {
            setTabClickStatus(0, 2, i, false);
        }
        for (int i = 0; i < TD_MAX_TAB; i++) {
            setTabClickStatus(1, i, 0, false);
        }
    }

    /**
     * 设置固件版本号
     */
    public void setFirewareVersionCode(int type, int versionCode) {
        initSecurePreferences();
        switch (type) {
            case FirewareBusiness.FIREWARE_TYPE_MCU:
                editor.putInt("fireware_mcu", versionCode);
            case FirewareBusiness.FIREWARE_TYPE_BLE:
                editor.putInt("fireware_ble", versionCode);
                break;
            default:
                break;
        }
        editor.commit();
    }

    /**
     * 获取固件MCU版本号
     */
    public int getFirewareVersionCode(int type) {
        initSecurePreferences();
        switch (type) {
            case FirewareBusiness.FIREWARE_TYPE_MCU:
                return securePreferences.getInt("fireware_mcu", 0);
            case FirewareBusiness.FIREWARE_TYPE_BLE:
                return securePreferences.getInt("fireware_ble", 0);
            default:
                break;
        }
        return 0;
    }

    /**
     * 保存手机信息
     *
     * @param info
     */
    public void setPhinfo(String info) {
        initSecurePreferences();
        editor.putString("phInfo", info);
        editor.commit();
    }

    /**
     * 获取手机信息
     *
     * @return
     */
    public String getPhoneInfo() {
        initSecurePreferences();
        return securePreferences.getString("phInfo", "");
    }

    /**
     * 设置控制方式
     * @param mode 默认头控加手柄：0 纯手柄：1
     */
    public void setControlMode(int mode){
        initSecurePreferences();
        editor.putInt("control_mode", mode);
        editor.commit();
    }

    /**
     * 获取控制方式
     * @return
     */
    public int getControlMode(){
        initSecurePreferences();
        return securePreferences.getInt("control_mode", 0);
    }

    /**
     * 设置在线播放操控模式
     * @param mode 0: 极简模式  1：沉浸模式
     */
    public void setPlayerMode(int mode){
        initSecurePreferences();
        editor.putInt("player_mode", mode);
        editor.commit();
    }

    /**
     * 获取在线播放操控模式
     * @return 0: 极简模式  1：沉浸模式  -1：无选择
     */
    public int getPlayerMode(){
        initSecurePreferences();
        return  securePreferences.getInt("player_mode",-1);
    }

    /**
     * 是否颠倒首页Tab
     * @param order
     */
    public void setTabOrder(int order){
        initSecurePreferences();
        String channel = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        editor.putInt("tab_order"+channel, order);
        editor.commit();
    }

    /**
     *是否颠倒首页Tab
     * @return
     */
    public int getTabOrder(){
        initSecurePreferences();
        String channel = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        return  securePreferences.getInt("tab_order"+channel,0);
    }

    /**
     * 首次进入应用市场，请求接口，提示
     * @param finishGuide
     */
    public void setGameTips(boolean finishGuide){
        initSecurePreferences();
        editor.putBoolean("game_tips", finishGuide);
        editor.commit();
    }

    public boolean getGameTips(){
        initSecurePreferences();
        return  securePreferences.getBoolean("game_tips", false);
    }

    /**
     * 体感游戏下载提示，不再显示弹框
     * @param checked
     */
    public void setGameNoMoreTips(boolean checked){
        initSecurePreferences();
        editor.putBoolean("game_no_more_tips", checked);
        editor.commit();
    }

    public boolean getGamenoMoreTips(){
        initSecurePreferences();
        return  securePreferences.getBoolean("game_no_more_tips", false);
    }

    /**
     * 点击体感游戏下载，弹框后继续下载次数
     * @param count
     */
    public void setGameDownloadCount(int count){
        initSecurePreferences();
        editor.putInt("game_download_count", count);
        editor.commit();
    }

    public int getGameDownloadCount(){
        initSecurePreferences();
        return  securePreferences.getInt("game_download_count", 0);
    }

    /**
     * 设置是否完成VR引导页面
     * @param finish
     */
    public void setVrGuide(boolean finish){
        initSecurePreferences();
        editor.putBoolean("isVrGuide", finish);
        editor.commit();
    }

    /**
     * 设置是否完成引导页面
     */
    public boolean getVrGuide(){
        initSecurePreferences();
        return securePreferences.getBoolean("isVrGuide", false);
    }

    /**
     * 设置选择的场景
     * @param
     */
    public void setSkyboxIndex(int index){
        initSecurePreferences();
        editor.putInt("skyboxIndex", index);
        editor.commit();
    }

    /**
     * 获取选择的场景
     * @return
     */
    public int getSkyboxIndex(){
        initSecurePreferences();
        return  securePreferences.getInt("skyboxIndex",0);
    }

    public void setLeftMode(boolean isLeft) {
        initSecurePreferences();
        editor.putBoolean("lefMode", isLeft);
        editor.commit();
    }

    public boolean getLeftMode() {
        initSecurePreferences();
        return securePreferences.getBoolean("lefMode", false);
    }

    /**
     * 首次锁屏提示
     * @return
     */
   public boolean getGLPlayerFirstLockTip(){
       initSecurePreferences();
      return securePreferences.getBoolean("gl_player_first_locked",true);
   }
    public void setGLPlayerFirstLockTip(boolean isFirstlocked){
        initSecurePreferences();
        editor.putBoolean("gl_player_first_locked", isFirstlocked);
        editor.commit();
    }

    /**
     * 首次解锁提示
     * @return
     */
    public boolean getGLPlayerFirstUnLockTip(){
        initSecurePreferences();
        return securePreferences.getBoolean("gl_player_first_unlocked",true);
    }
    public void setGLPlayerFirstUnLockTip(boolean isFirstunlocked){
        initSecurePreferences();
        editor.putBoolean("gl_player_first_unlocked", isFirstunlocked);
        editor.commit();
    }

    public void setPlayerSoundValue(int value){
        initSecurePreferences();
        editor.putInt(PLAYER_SOUND_VALUE_PREFENCE,value);
        editor.commit();
    }
    public int getPlayerSoundValue(){
        initSecurePreferences();
        int defvalue = SoundUtils.GetCurrentVolumePercent();
        return securePreferences.getInt(PLAYER_SOUND_VALUE_PREFENCE,defvalue);
    }

    public void setPlayerSoundMute(boolean isMute){
        initSecurePreferences();
        editor.putBoolean(PLAYER_SOUND_ISMUTE,isMute);
        editor.commit();
    }

    public boolean getPlayerSoundMute(){
        initSecurePreferences();
        return securePreferences.getBoolean(PLAYER_SOUND_ISMUTE,false);
    }
}
