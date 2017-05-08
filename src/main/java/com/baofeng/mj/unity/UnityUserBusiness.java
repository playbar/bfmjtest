package com.baofeng.mj.unity;

import android.app.Activity;
import android.text.TextUtils;

import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.paybusiness.PayBusiness;
import com.baofeng.mj.business.paybusiness.PayConfigBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ResponseCodeUtil;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.threadutil.HistoryProxy;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by liuchuanchi on 2016/8/25.
 * Unity用户业务类
 */
public class UnityUserBusiness {
    private final static int SUCCESS = 1;
    private final static int FAILURE = -1;
    /**
     * true已登录，false未登录
     */
    public static boolean isUserLogin() {
        return UserSpBusiness.getInstance().isUserLogin();
    }

    /**
     * 获取用户信息
     */
    public static String getUserInfoJo() {
        return UserSpBusiness.getInstance().getUserInfoJo();
    }

    /**
     * 检查是否购买过
     */
    public static void checkIfPayed(final int resType, final String resId){
        final Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PayBusiness().checkIfPayed(curActivity, resId, String.valueOf(resType), new ApiCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        if (TextUtils.isEmpty(result)) {
                            sendIfPayed(2,resId);//请求失败
                        } else {
                            try {
                                JSONObject joResult = new JSONObject(result);
                                if (0 == joResult.getInt("status")) {//已购买
                                    sendIfPayed(0,resId);
                                } else if (1 == joResult.getInt("status")) {//未购买
                                    sendIfPayed(1,resId);
                                } else {
                                    sendIfPayed(2,resId);//请求失败
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        sendIfPayed(2,resId);//请求失败
                    }
                });
            }
        });
    }

    /**
     * 发送是否购买
     * @param status 0已购买，1未购买，2请求失败
     */
    private static void sendIfPayed(int status,String resId){
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                iAndroidCallback.sendIfPayed(status,resId);
            }
        }
    }

    /**
     * 去购买
     */
    public static void gotoPay(int resType, final String resId, String resTitle, int payment_type, float payment_count){
        final String payUrl = PayConfigBusiness.createPayUrl(resType, resId, resTitle, payment_type, payment_count);
        final Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PayBusiness().gotoPay(curActivity, payUrl, new PayBusiness.PayCallBack() {
                    @Override
                    public void callBack(String code, String data) {
                        if (ResponseCodeUtil.SUCCESS.equals(code)) {//支付成功
                            sendPayStatus(0,resId);
                        } else {//支付失败
                            if (ResponseCodeUtil.ifHasPayed(code)) {//资源已订购
                                sendPayStatus(1,resId);
                            }else if (ResponseCodeUtil.modouNotEnough(code)) {//魔豆不足
                                sendPayStatus(2,resId);
                            }else if (ResponseCodeUtil.mobiNotEnough(code)){//魔币不足
                                sendPayStatus(3,resId);
                            }else {//其他
                                sendPayStatus(4,resId);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 发送购买结果
     * @param status 0购买成功，1资源已订购，2魔豆余额不足，3魔币余额不足，4购买失败
     */
    private static void sendPayStatus(int status,String resId){
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                iAndroidCallback.sendPayStatus(status,resId);
            }
        }
    }


    public static  void login(String userName,String password){// 1成功 -1 失败
        new UnityUserInfoApi().login(BaseApplication.INSTANCE, userName, password, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
                        UnityActivity.INSTANCE.getIAndroidCallback().sendLoginMsg(FAILURE,"登录失败");
                    }
                } else {
                    try {
                        JSONObject loginJo = new JSONObject(result);
                        if (loginJo.getBoolean("status")) {//登录成功
                            JSONObject data = loginJo.getJSONObject("data");
                            if (!data.isNull("user_no")) {
                                queryUserInfoById(data.getString("user_no"));//查询用户信息
                            }
                        } else {//登录失败
                            String msg = loginJo.getString("msg");
                            if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
                                UnityActivity.INSTANCE.getIAndroidCallback().sendLoginMsg(FAILURE,msg);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                String  msg = "数据加载失败";
                if (!NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE)) {
                    msg = "服务器连接失败，请检查网络";
                }
                if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
                    UnityActivity.INSTANCE.getIAndroidCallback().sendLoginMsg(FAILURE,msg);
                }
            }
        });

    }

    public static void logout(){
        if (UserSpBusiness.getInstance().isUserLogin()) {
            UserSpBusiness.getInstance().clearUserInfo();
            //退出登录，删除在线临时播放记录
            HistoryBusiness.deleteAllCinemaTempHistory();
            if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
                UnityActivity.INSTANCE.getIAndroidCallback().sendLogout(SUCCESS);
            }

        }
    }


    /**
     * 查询用户信息
     */
    private static void queryUserInfoById(String uid) {

        new UnityUserInfoApi().queryUserInfoById(BaseApplication.INSTANCE, uid, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
            @Override
            public void onSuccess(Response<List<SimpleUserInfo>> result) {
                super.onSuccess(result);
                if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
                    UnityActivity.INSTANCE.getIAndroidCallback().sendLoginMsg(SUCCESS,"成功");
                }

                //上报在线播放历史
                HistoryProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                    @Override
                    public void run() {
                        HistoryBusiness.reportAllCinemaTempHistory();
                    }
                });
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
                    UnityActivity.INSTANCE.getIAndroidCallback().sendLoginMsg(FAILURE,content);
                }
            }
        });

    }




}
