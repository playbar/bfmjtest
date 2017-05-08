package com.baofeng.mj.business.accountbusiness.deposit;

import android.app.Activity;
import android.content.Context;

import com.baofeng.mj.bean.ParamsInfo;
import com.baofeng.mj.bean.PrePayWXBean;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaominglei on 2016/5/19.
 * 微信支付
 */
public class WXPayUtil {

    private IWXAPI wxapi;

    private static WXPayUtil ourInstance;

    public static WXPayUtil getInstance() {
        synchronized (WXPayUtil.class) {
            if (ourInstance == null) {
                ourInstance = new WXPayUtil();
            }
            return ourInstance;
        }
    }

    private WXPayUtil() {
    }

    /***
     * 注册APP到微信
     *
     * @param context
     */
    public boolean registerApp(Context context) {
        wxapi = WXAPIFactory.createWXAPI(context, null);
        return wxapi.registerApp(ConfigConstant.APP_KEY_WEIXIN);
    }

    public boolean isWXAppInstalled() {
        if (wxapi == null) {
            throw new IllegalArgumentException("The wxapi is null.");
        }
        return wxapi.isWXAppInstalled();
    }

    public boolean isWXAppSupportAPI() {
        if (wxapi == null) {
            throw new IllegalArgumentException("The wxapi is null.");
        }
        return wxapi.isWXAppSupportAPI();
    }

    /***
     * 开始支付
     *
     * @param phoneNum
     * @param modouCount
     */
    public void startPay(Activity context, String phoneNum, String modouCount) {
        if (!registerApp(context)) {
            onPayResult(UserPayBusiness.getInstance().PAY_FAIL, "微信注册失败");
            return;
        }
        if (!isWXAppInstalled()) {
            onPayResult(UserPayBusiness.getInstance().PAY_FAIL, "您尚未安装微信应用");
            return;
        }
        if (!isWXAppSupportAPI()) {
            onPayResult(UserPayBusiness.getInstance().PAY_FAIL, "该版本暂不支持微信支付");
            return;
        }
        new UserInfoApi().requestPayByWX(phoneNum, modouCount, new ApiCallBack<Response<PrePayWXBean>>() {
            @Override
            public void onSuccess(Response<PrePayWXBean> result) {
                super.onSuccess(result);
                onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_SUCCESS, "订单生成成功");
                PrePayWXBean prePayWXBean = ((Response<PrePayWXBean>) result).data;
                sendPayReq(prePayWXBean);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!NetworkUtil.isNetworkConnected(BaseApplication.getInstance())) {
                    onPayResult(UserPayBusiness.getInstance().NET_EXCEPTION, null);
                } else {
                    onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL, "订单生成异常");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCache(Response<PrePayWXBean> result) {
                super.onCache(result);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        });
//        UserInfoBusiness.getInstance().requestPayByWX(phoneNum, modouCount, new UserInfoBusiness.IUserCallBack() {
//            @Override
//            public void onResult(int code, Object data) {
//                try {
//                    if (code == UserInfoBusiness.getInstance().success
//                            && data instanceof Response
//                            && ((Response) data).status) {
//                        onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_SUCCESS, "订单生成成功");
//                        PrePayWXBean prePayWXBean = ((Response<PrePayWXBean>) data).data;
//                        sendPayReq(prePayWXBean);
//                    } else if (code == UserInfoBusiness.getInstance().fail ||
//                            code == UserInfoBusiness.getInstance().exception) {
//                        //网络获取失败
//                        if (!NetworkUtil.isNetworkConnected(BaseApplication.getInstance())) {
//                            onPayResult(UserPayBusiness.getInstance().NET_EXCEPTION, null);
//                        } else {
//                            onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL, "订单生成异常");
//                        }
//                    } else {
//                        //其他异常
//                        onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL, null);
//                    }
//                } catch (Exception e) {
//                    onPayResult(UserPayBusiness.getInstance().PAY_EXCEPTION, "订单生成异常");
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    /***
     * 向微信发送付费请求
     *
     * @param prePayWXBean
     */
    private void sendPayReq(PrePayWXBean prePayWXBean) {
        if (wxapi == null) {
            throw new IllegalArgumentException("The wxapi is null.");
        }
        PayReq req = generateRequestPara(prePayWXBean);
        wxapi.sendReq(req);
    }

    public PayReq generateRequestPara(PrePayWXBean prePayWXBean) {
        PayReq req = new PayReq();
        req.appId = prePayWXBean.getAppid();
        req.partnerId = prePayWXBean.getMch_id();
        req.prepayId = prePayWXBean.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = prePayWXBean.getNonce_str();
        req.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        req.sign = generateAppSign(req);
        return req;
    }

    /***
     * @param req
     * @return
     */
    private String generateAppSign(PayReq req) {
        List<ParamsInfo> params = new LinkedList<ParamsInfo>();
        params.add(new ParamsInfo("appid", req.appId));
        params.add(new ParamsInfo("noncestr", req.nonceStr));
        params.add(new ParamsInfo("package", req.packageValue));
        params.add(new ParamsInfo("partnerid", req.partnerId));
        params.add(new ParamsInfo("prepayid", req.prepayId));
        params.add(new ParamsInfo("timestamp", req.timeStamp));
        StringBuilder sb = new StringBuilder();
        final int length = params.size();
        for (int i = 0; i < length; i++) {
            sb.append(params.get(i).getKey());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(ConfigConstant.WX_API_KEY);
        String appSign = MD5Util.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

    /***
     * 微信支付回调
     *
     * @param baseResp
     */
    public void onResponse(BaseResp baseResp) {
        switch (baseResp.errCode) {
            // 支付成功
            case BaseResp.ErrCode.ERR_OK:
                onPayResult(UserPayBusiness.getInstance().PAY_SUCCESS, null);
                break;
            //一般错误
            case BaseResp.ErrCode.ERR_COMM:
                onPayResult(UserPayBusiness.getInstance().PAY_FAIL, "微信网络连接出错");
                break;
            // 用户取消
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                onPayResult(UserPayBusiness.getInstance().PAY_USER_CANCEL, null);
                return;
        }
    }

    public void onPayResult(int code, Object obj) {
        UserPayBusiness.IUserPayCallback callback = UserPayBusiness.getInstance().getUserPayCallback();
        if (callback == null) {
            return;
        }
        callback.onPayResult(code, obj);
    }
}
