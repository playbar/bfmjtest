package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.ModouResponse;
import com.baofeng.mj.bean.ParamsInfo;
import com.baofeng.mj.bean.PrePayWXBean;
import com.baofeng.mj.bean.PrePayZFBBean;
import com.baofeng.mj.bean.RechargeBean;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.baofeng.mj.utils.StringUtils;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaominglei on 2016/6/3.
 * <p/>
 * 用户信息
 */
public class UserInfoApi extends BaseApi {

    // 充值渠道ID，微信是10，支付宝是7
    private String wxRechargeChannelId = "10";

    private String ZFBRechargeChannelId = "7";

    private Handler mHandler=new Handler();


    /***
     * 自动登陆
     */
    public void autoLogin(Context context) {
        if (UserSpBusiness.getInstance().isUserLogin()) {
            queryUserInfoById(context, UserSpBusiness.getInstance().getUid(), new ApiCallBack<Response<List<SimpleUserInfo>>>() {
                @Override
                public void onSuccess(Response<List<SimpleUserInfo>> result) {
                    super.onSuccess(result);
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                }
            });
        }
    }

    /***
     * 通过用户Id查询用户信息
     *
     * @param userId
     * @param apiCallBack
     */
    public void queryUserInfoById(final Context context, String userId, ApiCallBack<Response<List<SimpleUserInfo>>> apiCallBack) {
        String url = ConfigUrl.MJ_GET_USER_INFO_URL;
        RequestParams params = new RequestParams();
        String open_verify = MD5Util.MD5(userId + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("user_no", userId));
        list.add(new ParamsInfo("open_verify", open_verify));
        String pastr = Common.getUsercenterJSONParams(list);
        try {
            String openid = URLEncoder.encode(pastr, "utf-8");
            params.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(context, url, params, false, "", new ApiResponseHandler<Response<List<SimpleUserInfo>>>(apiCallBack) {
            @Override
            public Response<List<SimpleUserInfo>> parseResponse(String responseString) {
                Response<List<SimpleUserInfo>> response = JSON.parseObject(responseString, new TypeReference<Response<List<SimpleUserInfo>>>() {
                });
                //保存用户信息到本地
                if (response.data != null && response.data.size() > 0) {
                    saveUserInfo(response.data.get(0));
                }
                //更新魔豆
                updateModou(context);
                return response;
            }
        });
    }


    private void updateModou(final Context context){
        //更新魔豆
        if(!ChannelUtil.getChannelCode("running_platform").equals("0")) {//手机
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                //更新魔豆数量
                updateModouCount(context, new ApiCallBack<String>() {
                    @Override
                    public void onSuccess(String responseBody) {
                        super.onSuccess(responseBody);
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            if (json.getBoolean("status")) {
                                JSONObject joData = json.getJSONObject("data");
                                String recharge_modou = joData
                                        .getString("recharge_modou");
                                String gift_modou = joData.getString("gift_modou");
                                UserSpBusiness.getInstance().updateModouCount(recharge_modou, gift_modou);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                    }
                });
                }
            });
        } else {//一体机
            //更新魔豆数量
            updateModouCount(context, new ApiCallBack<String>() {
                @Override
                public void onSuccess(String responseBody) {
                    super.onSuccess(responseBody);
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        if (json.getBoolean("status")) {
                            JSONObject joData = json.getJSONObject("data");
                            String recharge_modou = joData
                                    .getString("recharge_modou");
                            String gift_modou = joData.getString("gift_modou");
                            UserSpBusiness.getInstance().updateModouCount(recharge_modou, gift_modou);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                }
            });
        }
    }

    //保存用户信息到本地
    private void saveUserInfo(SimpleUserInfo simpleUserInfo) {
        if (simpleUserInfo == null) {
            return;
        }
        //用户被冻结
        if (simpleUserInfo.user_is_delete == 2) {
            UserSpBusiness.getInstance().clearUserInfo();
        }
        if (StringUtils.isEmpty(simpleUserInfo.user_name)
                || "0".equals(simpleUserInfo.user_name)) {
            simpleUserInfo.user_name = "";
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUid(simpleUserInfo.user_no);
        userInfo.setNikename(simpleUserInfo.user_name);
        userInfo.setMobile(simpleUserInfo.user_tel);
        userInfo.setEmail(simpleUserInfo.user_email);
        userInfo.setLogoUrl(simpleUserInfo.user_head_url);
        UserSpBusiness.getInstance().saveUserInfo(userInfo);
    }

    /***
     * 根据手机号码获取用户信息，如果获取信息为空，则手机号没注册过
     *
     * @param tel
     * @param apiCallBack
     */
    public void queryUserInfoByTel(String tel, ApiCallBack<Response<List<SimpleUserInfo>>> apiCallBack) {
        String url = ConfigUrl.MJ_GET_USER_INFO_URL;
        RequestParams params = new RequestParams();
        String open_verify = MD5Util.MD5(tel + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("user_tel", tel));
        list.add(new ParamsInfo("open_verify", open_verify));
        String pastr = Common.getUsercenterJSONParams(list);
        try {
            String openid = URLEncoder.encode(pastr, "utf-8");
            params.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<Response<List<SimpleUserInfo>>>(apiCallBack) {
            @Override
            public Response<List<SimpleUserInfo>> parseResponse(String responseString) {
                Response<List<SimpleUserInfo>> response = JSON.parseObject(responseString, new TypeReference<Response<List<SimpleUserInfo>>>() {
                });
                return response;
            }
        });
    }

    /***
     * 查询兑换码
     *
     * @param code
     * @param apiCallBack
     */
    public void queryExchangeByCode(final String code, ApiCallBack<ModouResponse> apiCallBack) {
        String url = ConfigUrl.EXCHANGE_QUERY_URL;
        RequestParams params = new RequestParams();
        String verCode = ApkUtil.getVersionNameSuffix();
        String sign = MD5Util.MD5("mjapk" + code + verCode
                + ConfigConstant.MJ_KEY_LIHAO);
        params.put("code", code);
        params.put("ver", verCode);
        params.put("from", "mjapk");
        params.put("sign", sign);
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<ModouResponse>(apiCallBack) {
            @Override
            public ModouResponse parseResponse(String responseString) {
                ModouResponse response = JSON.parseObject(responseString, ModouResponse.class);
                return response;
            }
        });
    }

    /***
     * 兑换券领取魔豆礼券
     *
     * @param code        兑换码
     * @param exchangeUid 接受充值的uid
     * @param moblie      接受充值的手机号
     * @param apiCallBack
     */
    public void exChangeGiftModou(String code, final String exchangeUid, String moblie, ApiCallBack<ModouResponse> apiCallBack) {
        final String uid = UserSpBusiness.getInstance().getUid();
        String url = ConfigUrl.EXCHANGE_MODOU_GIFT_URL;
        RequestParams params = new RequestParams();
        String verCode = ApkUtil.getVersionNameSuffix();
        String sign = MD5Util.MD5(uid + "mjapk" + code + verCode + exchangeUid
                + moblie + ConfigConstant.MJ_KEY_LIHAO);
        params.put("userid", uid);
        params.put("code", code);
        params.put("ver", verCode);
        params.put("from", "mjapk");
        params.put("rechargeuid", uid);
        params.put("rechargemobile", moblie);
        params.put("sign", sign);
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<ModouResponse>(apiCallBack) {
            @Override
            public ModouResponse parseResponse(String responseString) {
                ModouResponse response = JSON.parseObject(responseString, ModouResponse.class);
                if (uid.equals(exchangeUid)) {
                    UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
                    String localGiftModou = userInfo.getGift_modou();
                    int addGiftModou = response.modou;
                    if (!TextUtils.isEmpty(localGiftModou)) {
                        float total = Float.parseFloat(localGiftModou) + addGiftModou;
                        UserSpBusiness.getInstance().updateModouCount(userInfo.getRecharge_modou(), String.valueOf(total));
                    }
                }
                return response;
            }
        });
    }

    /***
     * 更新魔豆
     *
     * @param apiCallBack
     */
    public void updateModouCount(Context context, ApiCallBack<String> apiCallBack) {
        UserInfo user = UserSpBusiness.getInstance().getUserInfo();
        String url = ConfigUrl.MJ_CHECK_COUNT_URL;
        String uid = user.getUid();
        if (uid == null) {
            uid = "";
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("time", time);
        params.put("sign", MD5Util.MD5(time + uid + ConfigConstant.MJ_KEY_LIHAO));
        getAsyncHttpClient().get(context, url, params, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 微信支付请求接口
     *
     * @return void
     * @author panxin @Date 2015-10-23
     */
    public void requestPayByWX(String mobile, String modouNum, ApiCallBack<Response<PrePayWXBean>> apiCallBack) {
        String url = ConfigUrl.USER_PAY_URL;
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        String uid = userInfo.getUid();
        String nikename = userInfo.getNikename();
        RequestParams params = new RequestParams();
        params.put("api", "pay.wx");
        params.put("rechargetype", wxRechargeChannelId);
        params.put("rechargeuid", uid);
        params.put("rechargephone", mobile);
        params.put("rechargenickname", nikename);
        params.put("rechargemodou", modouNum);
        String token = MD5Util.getMessageDigest((wxRechargeChannelId + uid + mobile
                + nikename + modouNum + ConfigConstant.MJ_KEY_LIHAO).getBytes());
        params.put("rechargetoken", token);
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<Response<PrePayWXBean>>(apiCallBack) {
            @Override
            public Response<PrePayWXBean> parseResponse(String responseString) {
                Response<PrePayWXBean> response = JSON.parseObject(responseString, new TypeReference<Response<PrePayWXBean>>() {
                });
                return response;
            }
        });
    }

    /***
     * 支付宝支付接口
     *
     * @param mobile
     * @param modouNum
     * @param apiCallBack
     */
    public void requestPayByZFB(String mobile, String modouNum, ApiCallBack<Response<PrePayZFBBean>> apiCallBack) {
        String url = ConfigUrl.USER_PAY_URL;
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        String uid = userInfo.getUid();
        String nikename = userInfo.getNikename();
        RequestParams params = new RequestParams();
        params.put("api", "pay.alipay");
        params.put("rechargetype", ZFBRechargeChannelId);
        params.put("rechargeuid", uid);
        params.put("rechargephone", mobile);
        params.put("rechargenickname", nikename);
        params.put("rechargemodou", modouNum);
        String token = MD5Util.getMessageDigest((ZFBRechargeChannelId + uid + mobile
                + nikename + modouNum + ConfigConstant.MJ_KEY_LIHAO).getBytes());
        params.put("rechargetoken", token);
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<Response<PrePayZFBBean>>(apiCallBack) {
            @Override
            public Response<PrePayZFBBean> parseResponse(String responseString) {
                Response<PrePayZFBBean> response = JSON.parseObject(responseString, new TypeReference<Response<PrePayZFBBean>>() {
                });
                return response;
            }
        });
    }

    /***
     * 获取微信openId
     * @param code
     * @param apiCallBack
     */
    public void getWxOpenId(String code,ApiCallBack<String> apiCallBack){
        String url =ConfigUrl.WECHAT_LOGIN_URL;
        RequestParams params = new RequestParams();
        params.put("appid", ConfigConstant.APP_KEY_WEIXIN);
        params.put("secret", ConfigConstant.SECRET_WEIXIN);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /***
     * 查询充值记录
     *
     * @param apiCallBack
     */
    public void queryRechargeRecord(ApiCallBack<Response<List<RechargeBean>>> apiCallBack) {
        UserInfo user = UserSpBusiness.getInstance().getUserInfo();
        String url = ConfigUrl.CHARGE_GETORDERS;
        RequestParams params = new RequestParams();
        params.put("uid", user.getUid());
        params.put("mobile", user.getMobile());
        params.put(
                "sign",
                MD5Util.MD5(user.getUid() + user.getMobile()
                        + ConfigConstant.MJ_KEY_LIHAO));
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<Response<List<RechargeBean>>>(apiCallBack) {
            @Override
            public Response<List<RechargeBean>> parseResponse(String responseString) {
                Response<List<RechargeBean>> response = JSON.parseObject(responseString, new TypeReference<Response<List<RechargeBean>>>() {
                });
                return response;
            }
        });
    }

    /***
     * 上传用户头像
     *
     * @param file
     * @param apiCallBack
     */
    public void uploadHeadPortrait(File file, ApiCallBack<Response<String>> apiCallBack) {
        UserInfo user = UserSpBusiness.getInstance().getUserInfo();
        String url = ConfigUrl.MJ_SETUSERHEADLOGO_URL;
        final RequestParams params = new RequestParams();
        String data = user.getUid() + "&" + "200";
        String open_verify = MD5Util.MD5(data + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("user_no", user.getUid()));
        list.add(new ParamsInfo("head_size", "200"));
        list.add(new ParamsInfo("open_verify", open_verify));
        String pastr = Common.getUsercenterJSONParams(list);
        try {
            String openid = URLEncoder.encode(pastr, "utf-8");
            params.put("open_id", openid);
            params.put("head_file", file);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().post(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<Response<String>>(apiCallBack) {
            @Override
            public Response<String> parseResponse(String responseString) {
                Response<String> response = JSON.parseObject(responseString, new TypeReference<Response<String>>() {
                });
                return response;
            }
        });
    }

    /***
     * 设置昵称
     *
     * @param nickName
     * @param apiCallBack
     */
    public synchronized void setUserNickName(String nickName, ApiCallBack<Response<String>> apiCallBack) {
        String url = ConfigUrl.MJ_SETNICKNAME_URL;
        UserInfo user = UserSpBusiness.getInstance().getUserInfo();
        final RequestParams params = new RequestParams();
        String open_verify = MD5Util.MD5(user.getUid() + "&"
                + nickName + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("user_no", user.getUid()));
        list.add(new ParamsInfo("user_name", nickName));
        list.add(new ParamsInfo("open_verify", open_verify));
        String pastr = Common.getUsercenterJSONParams(list);
        try {
            String openid = URLEncoder.encode(pastr, "utf-8");
            params.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, params, new ApiResponseHandler<Response<String>>(apiCallBack) {
            @Override
            public Response<String> parseResponse(String responseString) {
                Response<String> response = JSON.parseObject(responseString, new TypeReference<Response<String>>() {
                });
                return response;
            }
        });
    }

    /**
     * 登录
     *
     * @param phoneNum    手机号
     * @param password    密码
     * @param apiCallBack 回调
     */
    public void login(Context context, String phoneNum, String password, ApiCallBack<String> apiCallBack) {
        String open_verify = MD5Util.MD5(phoneNum + "&" + password
                + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("login_name", phoneNum));
        list.add(new ParamsInfo("login_pwd", password));
        list.add(new ParamsInfo("open_verify", open_verify));
        RequestParams paramMap = new RequestParams();
        try {
            String openid = URLEncoder.encode(Common.getUsercenterJSONParams(list), "utf-8");
            paramMap.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getAsyncHttpClient().get(context, ConfigUrl.MJ_LOGIN_URL, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 加载资源已支付记录
     */
    public void loadingPayed(Context context, ApiCallBack<String> apiCallBack) {
        String uid = UserSpBusiness.getInstance().getUid();
        long time = (System.currentTimeMillis() / 1000);
        String safecode = MD5Util.MD5(time + uid + ConfigConstant.MJ_KEY_LIHAO);
        String url = ConfigUrl.queryAllModouUrl() + "&uid=" + uid + "&time=" + time + "&sign=" + safecode;
        getAsyncHttpClient().get(context, url, null, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 获取验证码
     *
     * @param regist_tel
     * @param send_type send_type的值分为：tel_regist（注册短信），tel_getpwd（找回密码），tel_update（
     *            修改手机号），sec_update（修改密保），tel_bind（手机号绑定）
     */
    public void getVerifyCode(Context context, String regist_tel, String send_type, ApiCallBack<String> apiCallBack) {
        if (TextUtils.isEmpty(regist_tel)) {
            Toast.makeText(context, "请确认手机号不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String open_verify = MD5Util.MD5(regist_tel + "&" + send_type
                + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("regist_tel", regist_tel));
        list.add(new ParamsInfo("send_type", send_type));
        list.add(new ParamsInfo("open_verify", open_verify));
        RequestParams paramMap = new RequestParams();
        try {
            String openid = URLEncoder.encode(Common.getUsercenterJSONParams(list), "utf-8");
            paramMap.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(context, ConfigUrl.MJ_VERIFYCODE_URL, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 注册
     *
     * @param context
     * @param regist_tel
     * @param password
     * @param verifyCode
     * @param apiCallBack
     */
    public void regist(Context context, String regist_tel, String password, String verifyCode, ApiCallBack<String> apiCallBack) {
        if (TextUtils.isEmpty(regist_tel)) {
            Toast.makeText(context, "请确认手机号不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "请确认密码不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(context, "请确认验证码不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String open_verify = MD5Util.MD5(regist_tel + "&" + password + "&" + verifyCode
                + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("user_tel", regist_tel));
        list.add(new ParamsInfo("user_pwd", password));
        list.add(new ParamsInfo("user_check", verifyCode));
        list.add(new ParamsInfo("open_verify", open_verify));
        RequestParams paramMap = new RequestParams();
        try {
            String openid = URLEncoder.encode(Common.getUsercenterJSONParams(list), "utf-8");
            paramMap.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(context, ConfigUrl.MJ_REGISTER_URL, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 重置密码
     *
     * @param context
     * @param user_tel     用户手机号
     * @param user_new_pwd 用户新密码
     * @param verifyCode   校验码
     * @param apiCallBack
     */
    public void resetPwd(Context context, String user_tel, String user_new_pwd, String verifyCode, ApiCallBack<String> apiCallBack) {
        if (TextUtils.isEmpty(user_tel)) {
            Toast.makeText(context, "请确认手机号不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(user_new_pwd)) {
            Toast.makeText(context, "请确认密码不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(context, "请确认验证码不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String open_verify = MD5Util.MD5(user_tel + "&" + user_new_pwd + "&" + verifyCode
                + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("user_tel", user_tel));
        list.add(new ParamsInfo("user_new_pwd", user_new_pwd));
        list.add(new ParamsInfo("user_check", verifyCode));
        list.add(new ParamsInfo("open_verify", open_verify));
        RequestParams paramMap = new RequestParams();
        try {
            String openid = URLEncoder.encode(Common.getUsercenterJSONParams(list), "utf-8");
            paramMap.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(context, ConfigUrl.MJ_RESETPWD_URL, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 绑定手机号
     *
     * @param logintype  登录方式：login_weixin:微信，login_qq:腾讯qq，login_weibo:微博，login_baofeng:暴风影音
     * @param openid     第三方登陆后返回唯一用户ID
     * @param user_tel   手机号
     * @param user_check 验证码
     */
    public void bindPhone(Context context, String logintype, String openid, String user_tel, String user_check, ApiCallBack<String> apiCallBack) {
        if (TextUtils.isEmpty(user_tel)) {
            Toast.makeText(context, "请确认手机号不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(user_check)) {
            Toast.makeText(context, "请确认验证码不为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String open_verify = MD5Util.MD5(logintype + "&" + openid + "&" + user_tel + "&" + user_check
                + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("logintype", logintype));
        list.add(new ParamsInfo("openid", openid));
        list.add(new ParamsInfo("user_tel", user_tel));
        list.add(new ParamsInfo("user_check", user_check));
        list.add(new ParamsInfo("open_verify", open_verify));
        RequestParams paramMap = new RequestParams();

        try {
            String paraOpenid = URLEncoder.encode(Common.getUsercenterJSONParams(list), "utf-8");
            paramMap.put("open_id", paraOpenid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().get(context, ConfigUrl.MJ_BINDPHONE_URL, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /***
     *  通过第三方openid获取用户编号
     * @param context
     * @param openType
     * @param openId
     * @param apiCallBack
     */
    public void queryUserIdByOpenId(Context context, String openType, String openId,ApiCallBack<String> apiCallBack) {
        String currentTime=String.valueOf(System.currentTimeMillis() / 1000);
        String open_verify = MD5Util.MD5(openType  + openId + currentTime
                + ConfigConstant.MJ_UESR_PARTY_CHAR);
        RequestParams paramMap = new RequestParams();
        paramMap.put("open_type", openType);
        paramMap.put("open_id", openId);
        paramMap.put("open_time", currentTime);
        paramMap.put("open_verify", open_verify);
        getAsyncHttpClient().get(context, ConfigUrl.MJ_NO_BY_OPENID_URL, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }


    /**
     * 获取任务列表
     *
     * @param context
     * @param userId      用户id
     * @param from        获取途径，默认mjapk
     * @param version     版本号
     * @param apiCallBack
     */
    public void getTaskList(Context context, String userId, String from, String version, ApiCallBack<String> apiCallBack) {
        String key = "Bf@)(*$s1&2^3XVF#Mj";
        String sign = MD5Util.MD5(userId + from + version + key);
        RequestParams params = new RequestParams();
        params.put("userid", userId);
        params.put("from", from);
        params.put("ver", version);
        params.put("sign", sign);
        getAsyncHttpClient().get(context, ConfigUrl.MJ_TASKLIST_URL, params, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 领取任务
     *
     * @param context
     * @param userId      用户id
     * @param from        获取途径
     * @param version     版本号
     * @param aid         任务id
     * @param apiCallBack
     */
    public void getTask(Context context, String userId, String from, String version, String aid, ApiCallBack<String> apiCallBack) {
        String key = "Bf@)(*$s1&2^3XVF#Mj";
        String sign = MD5Util.MD5(userId + from + version + aid + key);
        RequestParams params = new RequestParams();
        params.put("userid", userId);
        params.put("from", from);
        params.put("ver", version);
        params.put("aid", aid);
        params.put("sign", sign);
        getAsyncHttpClient().get(context, ConfigUrl.MJ_TASKGET_URL, params, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }


    /***
     * 获取暴风影音的用户信息
     */
    public void getStormUserInfo(Context context,String token,ApiCallBack<String> apiCallBack){
        String open_verify = MD5Util.MD5(token  + ConfigConstant.MJ_STORM_APP_ID + ConfigConstant.MJ_STORM_APP_KEY);
        RequestParams paramMap = new RequestParams();
        paramMap.put("token", token);
        paramMap.put("client_id", ConfigConstant.MJ_STORM_APP_ID);
        paramMap.put("sign", open_verify);
        getAsyncHttpClient().get(context, ConfigUrl.STORM_USER_INFO, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }


    /***
     *  修改手机号码
     * @param context
     * @param userId 用户ID
     * @param userNewPhone 手机号码
     * @param check 验证码
     * @param apiCallBack
     */
    public void changePhone(Context context,String userId,String userNewPhone,String check,ApiCallBack<String> apiCallBack){
        String url = ConfigUrl.MJ_SET_USER_TEL_URL;
        String open_verify = MD5Util.MD5(userId + "&" + userNewPhone + "&"
                + check + ConfigConstant.MJ_UESR_CENTER_KEY
                + ConfigConstant.MJ_UESR_CENTER_KEY);
            List<ParamsInfo> list = new ArrayList<ParamsInfo>();
            list.add(new ParamsInfo("user_no", userId));
            list.add(new ParamsInfo("user_tel", userNewPhone));
            list.add(new ParamsInfo("user_check", check));
            list.add(new ParamsInfo("open_verify", open_verify));
            RequestParams paramMap = new RequestParams();
            try {
                String openid = URLEncoder.encode(Common.getUsercenterJSONParams(list), "utf-8");
                paramMap.put("open_id", openid);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        getAsyncHttpClient().get(context, url, paramMap, false, "", new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }


    /***
     * 体验报告
     * @param context
     * @param apiCallBack
     */
    public void getExperienceReportInfo(Context context,ApiCallBack<String> apiCallBack){
        String currentVersion = ApkUtil.getVersionNameSuffix();
        RequestParams paramMap = new RequestParams();
        paramMap.put("version", currentVersion);
        getAsyncHttpClient().post(context, ConfigUrl.USER_EXPERIENCE_REPORT, paramMap, new ApiResponseHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 获取是否有礼券活动
     * @param apiCallBack
     */
    public void getIfHasGift(ApiCallBack<String> apiCallBack) {
        String uid = UserSpBusiness.getInstance().getUid();
        String partVersionName = ApkUtil.getVersionNameSuffix();
        String sign = MD5Util.MD5(uid + "mjapk" + partVersionName + ConfigConstant.MJ_KEY_LIHAO);

        List<ParamsInfo> list = new ArrayList<ParamsInfo>();
        list.add(new ParamsInfo("userid", uid));
        list.add(new ParamsInfo("ver", partVersionName));
        list.add(new ParamsInfo("from", "mjapk"));
        list.add(new ParamsInfo("sign", sign));

        RequestParams params = new RequestParams();
        try {
            String pastr = Common.getUsercenterJSONParams(list);
            String openid = URLEncoder.encode(pastr, "utf-8");
            params.put("open_id", openid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getAsyncHttpClient().get(BaseApplication.INSTANCE, ConfigUrl.getIfHasNewGiftUrl(), params, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }
}
