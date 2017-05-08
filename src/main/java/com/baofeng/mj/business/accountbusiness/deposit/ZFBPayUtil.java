package com.baofeng.mj.business.accountbusiness.deposit;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.ParamsInfo;
import com.baofeng.mj.bean.PayZFBBean;
import com.baofeng.mj.bean.PrePayZFBBean;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.threadutil.ThreadPoolUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaominglei on 2016/5/19.
 * 支付宝支付
 */
public class ZFBPayUtil {
    private static ZFBPayUtil ourInstance;
    private String SUCCESS = "9000";
    private String SYSTEM_EXCEPTION = "4000";
    private String ORDER_PARAM_ERROR = "4001";
    private String USER_CANCEL = "6001";
    private String NET_EXCEPTION = "6002";

    public static ZFBPayUtil getInstance() {
        synchronized (ZFBPayUtil.class) {
            if (ourInstance == null) {
                ourInstance = new ZFBPayUtil();
            }
            return ourInstance;
        }
    }

    private ZFBPayUtil() {
    }

    public void startPay(final Activity context, String phoneNum, String modouCount) {
        new UserInfoApi().requestPayByZFB(phoneNum, modouCount, new ApiCallBack<Response<PrePayZFBBean>>() {
            @Override
            public void onSuccess(Response<PrePayZFBBean> result) {
                super.onSuccess(result);
                PrePayZFBBean prePayZFBBean = ((Response<PrePayZFBBean>) result).data;
                sendPayReq(prePayZFBBean, context);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!NetworkUtil.isNetworkConnected(BaseApplication.getInstance())) {
                    onPayResult(UserPayBusiness.getInstance().NET_EXCEPTION, null);
                } else {
                    onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL, BaseApplication.getInstance().getResources().getString(R.string.order_generate_exception));
                }
            }
        });
    }

    private void sendPayReq(PrePayZFBBean prePayZFBBean, final Activity context) {
        final String payInfo = generatePayInfo(prePayZFBBean);
        ThreadPoolUtil.runThread(new Runnable() {
            @Override
            public void run() {
                try {
                    PayTask alipay = new PayTask(context);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(payInfo, true);
                    PayZFBBean payResult = new PayZFBBean(result);
                    payResult(payResult);
                } catch (NoClassDefFoundError e) {
                    onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL, "未安装支付宝客户端");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String generatePayInfo(PrePayZFBBean orderData) {
        List<ParamsInfo> params = new LinkedList<ParamsInfo>();
        params.add(new ParamsInfo("service", orderData.service));
        params.add(new ParamsInfo("partner", orderData.partner));
        params.add(new ParamsInfo("_input_charset", orderData._input_charset));
        params.add(new ParamsInfo("notify_url", orderData.notify_url));
        params.add(new ParamsInfo("out_trade_no", orderData.out_trade_no));
        params.add(new ParamsInfo("subject", orderData.subject));
        params.add(new ParamsInfo("payment_type", orderData.payment_type));
        params.add(new ParamsInfo("seller_id", orderData.seller_id));
        params.add(new ParamsInfo("total_fee", orderData.total_fee));
        params.add(new ParamsInfo("body", orderData.body));
        params.add(new ParamsInfo("sign", orderData.sign));
        params.add(new ParamsInfo("sign_type", orderData.sign_type));
        StringBuilder sb = new StringBuilder();
        final int length = params.size();
        for (int i = 0; i < length; i++) {
            sb.append(params.get(i).getKey());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        String ret = sb.toString();
        ret = ret.substring(0, sb.length() - 1);
        return ret;
    }

    private void payResult(PayZFBBean payresult) {
        String payResultStatus = payresult.getResultStatus();
        if (payResultStatus.equals(SUCCESS)) {
            onPayResult(UserPayBusiness.getInstance().PAY_SUCCESS, null);
        } else if (payResultStatus.equals(SYSTEM_EXCEPTION)
                || payResultStatus.equals(ORDER_PARAM_ERROR)) {
            String memo=payresult.getMemo();
            if(TextUtils.isEmpty(memo)){
                onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL, BaseApplication.getInstance().getResources().getString(R.string.order_generate_exception));
            }else{
                onPayResult(UserPayBusiness.getInstance().ORDER_GENERATE_FAIL,memo);
            }
        } else if (payResultStatus.equals(NET_EXCEPTION)) {
            onPayResult(UserPayBusiness.getInstance().NET_EXCEPTION, BaseApplication.INSTANCE.getResources().getString(R.string.network_exception));
        } else if (payResultStatus.equals(USER_CANCEL)) {
            onPayResult(UserPayBusiness.getInstance().PAY_USER_CANCEL, null);
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