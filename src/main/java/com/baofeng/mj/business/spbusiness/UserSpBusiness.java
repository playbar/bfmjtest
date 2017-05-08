package com.baofeng.mj.business.spbusiness;

import android.content.Context;
import android.text.TextUtils;

import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.util.publicutil.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author liuchuanchi
 * @description: 用户登录信息保存业务
 */
public class UserSpBusiness {
    public static final String UID = "uid";
    public static final String USER_NAME = "userName";
    public static final String NICK_NAME = "nickName";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String RECHARGE_MODOU = "rechargeModou";
    public static final String GIFT_MODOU = "giftModou";
    public static final String BF_CSID = "bfcsid";
    public static final String ST = "st";
    public static final String SSO_TTL = "ssottl";
    public static final String SSO_STATUS = "ssostatus";
    public static final String LOGO_URL = "logoUrl";
    public static final String GIFT_TASK_LAST_AID = "giftTaskLastAid";
    public static final String WECHAT_LOGIN = "wechatLogin";
    public static final String FIRST_LOGIN = "firstLogin";
    public static final String DOWNLOAD_NUM = "download_num";//下载次数
    public static final String GIFT_TIME = "giftTime";//礼券时间
    public static final String HAS_NEW_GIFT = "hasNewGift";//有新的礼券活动
    private static UserSpBusiness instance;
    private SecurePreferences securePreferences;
    private SecurePreferences.Editor editor;
    private UserInfo userInfo;//用户登录信息

    private UserSpBusiness() {
    }

    public static UserSpBusiness getInstance() {
        if (instance == null) {
            instance = new UserSpBusiness();
        }
        return instance;
    }

    /**
     * 初始化SecurePreferences
     */
    private void initSecurePreferences() {
        if (securePreferences == null) {
            securePreferences = new SecurePreferences(ConfigConstant.PACKAGE_NAME + ".user", Context.MODE_PRIVATE);
            editor = securePreferences.edit();
        }
    }

    /**
     * 初始化用户登录信息
     */
    private void initUserInfo() {
        if (userInfo == null) {
            userInfo = new UserInfo();
            initSecurePreferences();
            userInfo.setUid(securePreferences.getString(UID, ""));
            userInfo.setUsername(securePreferences.getString(USER_NAME, ""));
            userInfo.setNikename(securePreferences.getString(NICK_NAME, ""));
            userInfo.setMobile(securePreferences.getString(MOBILE, ""));
            userInfo.setEmail(securePreferences.getString(EMAIL, ""));
            userInfo.setRecharge_modou(securePreferences.getString(RECHARGE_MODOU, ""));
            userInfo.setGift_modou(securePreferences.getString(GIFT_MODOU, ""));
            userInfo.setBfcsid(securePreferences.getString(BF_CSID, ""));
            userInfo.setSt(securePreferences.getString(ST, ""));
            userInfo.setSsottl(securePreferences.getInt(SSO_TTL, -1));
            userInfo.setSsostatus(securePreferences.getString(SSO_STATUS, ""));
            userInfo.setLogoUrl(securePreferences.getString(LOGO_URL, ""));
        }
    }

    /**
     * 保存用户登录信息
     *
     * @param userInfo
     */
    public void saveUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        initSecurePreferences();
        editor.putString(UID, userInfo.getUid());
        editor.putString(USER_NAME, userInfo.getUsername());
        editor.putString(NICK_NAME, userInfo.getNikename());
        editor.putString(MOBILE, userInfo.getMobile());
        editor.putString(EMAIL, userInfo.getEmail());
        editor.putString(RECHARGE_MODOU, userInfo.getRecharge_modou());
        editor.putString(GIFT_MODOU, userInfo.getGift_modou());
        editor.putString(BF_CSID, userInfo.getBfcsid());
        editor.putString(ST, userInfo.getSt());
        editor.putInt(SSO_TTL, userInfo.getSsottl());
        editor.putString(SSO_STATUS, userInfo.getSsostatus());
        editor.putString(LOGO_URL, userInfo.getLogoUrl());
        editor.commit();
    }

    /**
     * 保存一体机那边已经登录的用户信息
     *
     * @param uid
     * @param userName
     * @param mobile
     * @param logoUrl
     */
    public void saveUserInfo(String uid, String userName, String mobile, String logoUrl) {
        initSecurePreferences();
        editor.putString(UID, uid);
        editor.putString(USER_NAME, userName);
        editor.putString(MOBILE, mobile);
        editor.putString(LOGO_URL, logoUrl);
        editor.commit();
    }

    /**
     * 清除用户登录信息
     */
    public void clearUserInfo() {
        this.userInfo = null;
        initSecurePreferences();
        editor.putString(UID, "");
        editor.putString(USER_NAME, "");
        editor.putString(NICK_NAME, "");
        editor.putString(MOBILE, "");
        editor.putString(EMAIL, "");
        editor.putString(RECHARGE_MODOU, "");
        editor.putString(GIFT_MODOU, "");
        editor.putString(BF_CSID, "");
        editor.putString(ST, "");
        editor.putInt(SSO_TTL, 0);
        editor.putString(SSO_STATUS, "");
        editor.putString(LOGO_URL, "");
        editor.putInt(GIFT_TASK_LAST_AID, 0);
        editor.putString(WECHAT_LOGIN, "");
//        editor.putInt(DOWNLOAD_NUM, 0);
        editor.putInt(GIFT_TIME, 0);
        editor.putBoolean(HAS_NEW_GIFT, false);
        editor.commit();
    }

    public void updateModouCount(String recharge, String gift) {
        userInfo.setRecharge_modou(recharge);
        userInfo.setGift_modou(gift);
        initSecurePreferences();
        editor.putString(RECHARGE_MODOU, userInfo.getRecharge_modou());
        editor.putString(GIFT_MODOU, userInfo.getGift_modou());
        editor.commit();
    }

    public void updateRechargeModou(String recharge) {
        userInfo.setRecharge_modou(recharge);
        initSecurePreferences();
        editor.putString(RECHARGE_MODOU, userInfo.getRecharge_modou());
        editor.commit();
    }

    public void updateGiftModou(String gift) {
        userInfo.setGift_modou(gift);
        initSecurePreferences();
        editor.putString(GIFT_MODOU, userInfo.getGift_modou());
        editor.commit();
    }

    public String getUid() {
        initUserInfo();
        return userInfo.getUid();
    }

    public String getNickName() {
        initUserInfo();
        return userInfo.getNikename();
    }

    public boolean isUserLogin() {
        if (TextUtils.isEmpty(getUid())) {
            return false;
        }
        return true;
    }

    public String getUserName() {
        initUserInfo();
        return userInfo.getUsername();
    }

    public String getMobile() {
        initUserInfo();
        return userInfo.getMobile();
    }

    public void setFirstLogin(boolean firstLogin) {
        initSecurePreferences();
        editor.putBoolean(FIRST_LOGIN, firstLogin);
        editor.commit();
    }

    public boolean isFirstLogin() {
        initSecurePreferences();
        return securePreferences.getBoolean(FIRST_LOGIN, false);
    }

    public UserInfo getUserInfo() {
        initUserInfo();
        return userInfo;
    }

    /**
     * 获取用户信息json
     * @return 返回用户信息
     */
    public String getUserInfoJo(){
        initUserInfo();
        if(!TextUtils.isEmpty(userInfo.getUid())){//用户已登录
            try {
                JSONObject userInfoJo = new JSONObject();
                userInfoJo.put(UID, userInfo.getUid());
                userInfoJo.put(USER_NAME, userInfo.getUsername());
                userInfoJo.put(NICK_NAME, userInfo.getNikename());
                userInfoJo.put(MOBILE, userInfo.getMobile());
                userInfoJo.put(EMAIL, userInfo.getEmail());
                userInfoJo.put(RECHARGE_MODOU, userInfo.getRecharge_modou());
                userInfoJo.put(GIFT_MODOU, userInfo.getGift_modou());
                userInfoJo.put(LOGO_URL, userInfo.getLogoUrl());
                return userInfoJo.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /***
     * 保存微信登录方式
     *
     * @param wechatLogin
     */
    public void setWechatLogin(String wechatLogin) {
        initSecurePreferences();
        editor.putString(WECHAT_LOGIN, wechatLogin);
        editor.commit();
    }

    public String getWechatLogin() {
        initSecurePreferences();
        return securePreferences.getString(WECHAT_LOGIN, "");
    }

    /**
     * 设置未登录下载次数
     * @param downloadNum 下载次数
     */
    public void setDownloadNum(int downloadNum) {
        if (downloadNum <= 0 || downloadNum > 6) {
            return;
        }
        initSecurePreferences();
        editor.putInt(DOWNLOAD_NUM, downloadNum);
        editor.commit();
    }

    /**
     * 获取未登录下载次数
     */
    public int getDownloadNum() {
        initSecurePreferences();
        return securePreferences.getInt(DOWNLOAD_NUM, 0);
    }

    /**
     * 未登录是否可以下载
     *
     * @return true不可以下载，false可以
     */
    public boolean notLoginForDownload() {
//        if (!isUserLogin() && getDownloadNum() >= 5) {
//            return true;//未登录，下载次数超过5次，不可以下载
//        }
        return false;//可以下载
    }

    /***
     * 修改用户头像
     *
     * @param url
     */
    public void setLogoUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        userInfo.setLogoUrl(url);
        initSecurePreferences();
        editor.putString(LOGO_URL, userInfo.getLogoUrl());
        editor.commit();
    }

    /***
     * 设置昵称
     *
     * @param name
     */
    public void setNickName(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        userInfo.setNikename(name);
        initSecurePreferences();
        editor.putString(NICK_NAME, userInfo.getNikename());
        editor.commit();
    }

    /***
     * 设置手机号码
     *
     * @param mobile
     */
    public void setMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return;
        }
        userInfo.setMobile(mobile);
        initSecurePreferences();
        editor.putString(MOBILE, userInfo.getMobile());
        editor.commit();
    }

    /**
     * 保存礼券时间
     *
     * @param giftTime 礼券时间
     */
    public void setGiftTime(int giftTime) {
        initSecurePreferences();
        editor.putInt(GIFT_TIME, giftTime);
        editor.commit();
    }

    /**
     * 获取礼券时间
     *
     * @return
     */
    public int getGiftTime() {
        initSecurePreferences();
        return securePreferences.getInt(GIFT_TIME, 0);
    }

    /**
     * 设置有新的礼券活动
     */
    public void setHasNewGift(boolean hasNewGift) {
        initSecurePreferences();
        editor.putBoolean(HAS_NEW_GIFT, hasNewGift);
        editor.commit();
    }

    /**
     * 获取是否有新的礼券活动
     *
     * @return true有，false没有
     */
    public boolean getHasNewGift() {
        initSecurePreferences();
        return securePreferences.getBoolean(HAS_NEW_GIFT, false);
    }

    /**
     * 保存uid
     * @param uid
     */
    public void setUid(String uid) {
        initSecurePreferences();
        editor.putString(UID, uid);
        editor.commit();
    }
}