package com.baofeng.mj.business.paybusiness;

import android.content.Context;
import android.text.TextUtils;

import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ResponseCodeUtil;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ApiResponseHandler;
import com.baofeng.mj.util.netutil.BaseApi;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 魔豆，魔币支付工具类
 */
public class PayBusiness extends BaseApi {
    /***
     * 检查是否购买过
     */
    public void checkIfPayed(final Context context, String res_id, String res_type, ApiCallBack<String> apiCallBack) {
        String uid = UserSpBusiness.getInstance().getUid();
        String mobile = UserSpBusiness.getInstance().getMobile();
        String safecode = MD5Util.MD5(uid + mobile + res_id + res_type + ConfigConstant.MJ_KEY_LIHAO);

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("mobile", mobile);
        params.put("res_id", res_id);
        params.put("res_type", res_type);
        params.put("safecode", safecode);

        getAsyncHttpClient().get(context, ConfigUrl.getIfPayedUrl(), params, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 去支付
     */
    public void gotoPay(final Context context, final String payUrl, final PayCallBack payCallBack) {
        ApiCallBack<String> apiCallBack = new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) {
                    if(payCallBack != null){
                        payCallBack.callBack(ResponseCodeUtil.NET_ERROR, null);
                    }
                } else {
                    try {
                        JSONObject joResult = new JSONObject(result);
                        String code = joResult.getString("code");
                        if (ResponseCodeUtil.SUCCESS.equals(code)) {// 支付成功
                            JSONObject joData = joResult.getJSONObject("data");

                            String recharge = joData.getString("recharge_modou");
                            String gift = joData.getString("gift_modou");
                            UserSpBusiness.getInstance().updateModouCount(recharge, gift);

                            if(payCallBack != null){
                                payCallBack.callBack(code, joData.toString());
                            }
                        }else{//支付失败
                            if(payCallBack != null){
                                payCallBack.callBack(code, null);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(payCallBack != null){
                            payCallBack.callBack(ResponseCodeUtil.NET_ERROR, null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if(payCallBack != null){
                    payCallBack.callBack(ResponseCodeUtil.NET_ERROR, null);
                }
            }
        };
        getAsyncHttpClient().get(context, payUrl, null, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    public interface PayCallBack {
        void callBack(String code, String data);
    }
}
