package com.baofeng.mj.util.netutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.PluginRequestBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;

/**
 * Created by wanghongfang on 2017/3/7.
 * 获取直播插件列表
 */
public class PluginListApi extends BaseApi {

    public void getPluginData(ApiCallBack<PluginRequestBean> apiCallBack) { //sypt + syqd + key
        String key = ConfigConstant.MJ_PLUGIN_KEY;
        String version = ApkUtil.getVersionNameMiddle();
        String upgradetype = ConfigUrl.IsOnline?"1":"2";   //升级类型，1:正式升级；2.测试升级
        RequestParams params = new RequestParams();
        params.put("clienttype", 1);  //平台ID,1通用安卓
        params.put("version", version); //版本号
        params.put("upgradetype",upgradetype);  //升级类型，1:正式升级；2.测试升级
        String signStr = "1"+ version+upgradetype + key;
        String sign = MD5Util.MD5(signStr);
        params.put("sign", sign);

        getAsyncHttpClient().get(BaseApplication.getInstance(),ConfigUrl.LIVE_PLUGIN_LIST_URL, params, true, "pluginData", new ApiResponseHandler<PluginRequestBean>(apiCallBack) {

            @Override
            public PluginRequestBean parseResponse(String responseString) {
                PluginRequestBean bean = JSON.parseObject(
                        responseString, new TypeReference<PluginRequestBean>() {
                        });
                return bean;
            }
        });
    }
}
