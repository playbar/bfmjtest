package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.GlassesBean;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;


/**
 * Created by muyu on 2016/8/4.
 */
public class GlassesApi extends BaseApi {

    public void getGlassesData(Context context, ApiCallBack<GlassesBean> apiCallBack) { //sypt + syqd + key
        String channelCode = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID"); //渠道号
        String key = "cca0cebb4f91491547836f5c92a8b54f";
        String signStr = "1"+ channelCode + key;
        String sign = MD5Util.MD5(signStr);
        RequestParams params = new RequestParams();

        params.put("sypt", 1);  //平台ID,1通用安卓，2迷你安卓，3通用IOS，4迷你IOS，5小魔+安卓，6小魔IOS
        params.put("syqd", channelCode); //渠道号，如不传渠道号，默认返回平台相关
        params.put("sign", sign);
        getAsyncHttpClient().get(context, ConfigUrl.MJ_GLASSES_URL, params, true, "GlassesData", new ApiResponseHandler<GlassesBean>(apiCallBack) {

            @Override
            public GlassesBean parseResponse(String responseString) {
                GlassesBean bean = JSON.parseObject(
                        responseString, new TypeReference<GlassesBean>() {
                        });
                return bean;
            }
        });
    }
}
