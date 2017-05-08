package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.GameDialogBean;
import com.baofeng.mj.bean.OTABean;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.TabOrderBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;

/**
 * 应用网络请求API
 * Created by muyu on 2016/6/21.
 */
public class MainAPI extends BaseApi {

    public void getOTAData(Context context, ApiCallBack<OTABean> apiCallBack) {
        int versionCode = ApkUtil.getVersionCode();
        RequestParams params = new RequestParams();

        params.put("version", versionCode);  //版本号
        params.put("is_online", 0); //is_online=0为测试，不传is_online参数，为正式
        params.put("upgrade_version", 0); //升级包版本号
        String sign = MD5Util.MD5(versionCode + ConfigConstant.MJ_OTA_KEY); //version + key
        params.put("sign", sign);

        getAsyncHttpClient().get(context, ConfigUrl.MJ_OTA_URL, params, false, "", new ApiResponseHandler<OTABean>(apiCallBack) {

            @Override
            public OTABean parseResponse(String responseString) {
                OTABean bean = JSON.parseObject(
                        responseString, new TypeReference<OTABean>() {
                        });
                return bean;
            }
        });
    }

    /**
     * 获取splash数据
     */
    public void getSplashInfo(ApiCallBack<String> apiCallBack) {
        RequestParams requestParams = new RequestParams();
        String version_id = ApkUtil.getVersionNameSuffix();
        String sign = MD5Util.MD5(version_id + 1 + ConfigConstant.MJ_FLASH_KEY);
        requestParams.put("platform", 1);
        requestParams.put("version_id", version_id);
        String key = SettingSpBusiness.getInstance().getGlassesModeKey();
        GlassesNetBean bean = GlassesManager.getGlassesNetBean();
        if(bean != null){
            String glassId = bean.getGlass_id();
            if (!TextUtils.isEmpty(glassId)) {
                requestParams.put("glass_id", glassId);
            }
        }

        requestParams.put("sign", sign);
        getAsyncHttpClient().get(BaseApplication.INSTANCE, ConfigUrl.getSplashImgUrl(), requestParams, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 渠道审核
     */
    public void channelCheck(ApiCallBack<String> apiCallBack) {
        String versionstring = ApkUtil.getVersionNameSuffix();
        String type = String.valueOf(ConfigConstant.getMojingApp());
        String curr_time = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = MD5Util.MD5(versionstring + curr_time + ConfigConstant.MJ_KEY_2);

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(ConfigUrl.getChannelCheckUrl())
                .append("versionstring=").append(versionstring)
                .append("&type=").append(type)
                .append("&curr_time=").append(curr_time)
                .append("&sign=").append(sign);
        getAsyncHttpClient().get(BaseApplication.INSTANCE, sbUrl.toString(), null, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**wiki上接口文档
     * http://192.168.12.66/projects/bf-mj-pt/wiki/%E9%AD%94%E9%95%9C%E5%8F%91%E7%89%88%E6%B8%A0%E9%81%93%E4%BF%A1%E6%81%AF%E6%8E%A5%E5%8F%A3
     * @param apiCallBack
     */
    public void getTabOrderInfo(ApiCallBack<Response<TabOrderBean>> apiCallBack){
        String versionstring = ApkUtil.getVersionNameSuffix();
        RequestParams params = new RequestParams();
        params.put("ver", versionstring);  //版本号

        getAsyncHttpClient().get(BaseApplication.INSTANCE, ConfigUrl.MAIN_TAB_ORDER, params, false, "", new ApiResponseHandler<Response<TabOrderBean>>(apiCallBack) {

            @Override
            public Response<TabOrderBean> parseResponse(String responseString) {
                Response<TabOrderBean> bean = JSON.parseObject(
                        responseString, new TypeReference<Response<TabOrderBean>>() {
                        });
                return bean;
            }
        });
    }

    public void getGameDialogInfo(ApiCallBack<Response<GameDialogBean>> apiCallBack){
        String versionstring = ApkUtil.getVersionNameSuffix();//"4.10.1208.1201"
        String channelId = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        String curr_time = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = MD5Util.MD5(versionstring + channelId + curr_time + ConfigConstant.MJ_KEY_3);
        String url = ConfigUrl.GAME_DIALOG_URL + "?version_num=" + versionstring + "&channel=" + channelId + "&time=" + curr_time + "&sign=" + sign;
        getAsyncHttpClient().get(BaseApplication.INSTANCE, url, null, false, "", new ApiResponseHandler<Response<GameDialogBean>>(apiCallBack) {
            @Override
            public Response<GameDialogBean> parseResponse(String responseString) {
                Response<GameDialogBean> bean = JSON.parseObject(
                        responseString, new TypeReference<Response<GameDialogBean>>() {
                        });
                return bean;
            }
        });
    }
}
