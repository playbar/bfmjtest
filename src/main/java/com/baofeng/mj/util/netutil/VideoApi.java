package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SelectBean;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.net.AsyncHttpClient;
import com.baofeng.mj.util.publicutil.HMACSHA1;
import com.baofeng.mj.utils.ACache;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by hanyang on 2016/5/18.
 * 视频网络数据接口
 */
public class VideoApi extends BaseApi {
    /**
     * 视频详情页接口
     *
     * @param context
     * @param apiCallBack
     */
    public void getVideoDetailInfo(Context context, String url, ApiCallBack<ResponseBaseBean<VideoDetailBean>> apiCallBack) {
        ReportFromBean msg = ReportBusiness.getInstance().get(url);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        client.get(context, ConfigUrl.getServiceUrl(context) + url, null, true, "getVideoDetailInfo" + url, new ApiResponseHandler<ResponseBaseBean<VideoDetailBean>>(apiCallBack) {
            @Override
            public ResponseBaseBean<VideoDetailBean> parseResponse(String responseString) {
                ResponseBaseBean<VideoDetailBean> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<VideoDetailBean>>() {
                });
                return bean;
            }
        });
    }

    /**
     * 视频详情页缓存数据
     * @param cacheCallBack
     */
    public void getVideoDetailInfo(String url, CacheCallBack<ResponseBaseBean<VideoDetailBean>> cacheCallBack) {
        File cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = BaseApplication.INSTANCE.getCacheDir();
        }
        String value = ACache.get(cacheDir).getAsString("getVideoDetailInfo" + url);
        if (TextUtils.isEmpty(value)) {
            if(cacheCallBack != null){
                cacheCallBack.onCache(null);
            }
        }else{
            if(cacheCallBack != null){
                cacheCallBack.onCache(JSON.parseObject(value, new TypeReference<ResponseBaseBean<VideoDetailBean>>() {}));
            }
        }
    }

    /**
     * 查询在线历史记录
     * @param page 第几页
     * @param page_cnt 每页多少条
     * @param apiCallBack
     */
    public void queryCinemaHistory(int page, int page_cnt, ApiCallBack<String> apiCallBack) {
        requestCinemaHistory(2, page, page_cnt, "", apiCallBack);
    }

    /**
     * 上报在线历史记录
     * @param logs 历史记录
     * @param apiCallBack
     */
    public void reportCinemaHistory(String logs, ApiCallBack<String> apiCallBack) {
        requestCinemaHistory(1, 1, 10, logs, apiCallBack);
    }

    /**
     * 请求在线历史记录
     * @param operate 1只插入，2只查询，3即插入又查询
     * @param page 第几页
     * @param page_cnt 每页多少条
     * @param logs 历史记录
     * @param apiCallBack
     */
    public void requestCinemaHistory(int operate, int page, int page_cnt, String logs, ApiCallBack<String> apiCallBack) {
        RequestParams params = new RequestParams();
        try {
            String uid = UserSpBusiness.getInstance().getUid();
            String sign_time = String.valueOf(System.currentTimeMillis() / 1000);
            StringBuilder sbSign = new StringBuilder();
            sbSign.append("logs=").append(logs).append("&operate=").append(operate).append("&page=")
                  .append(page).append("&page_cnt=").append(page_cnt).append("&sign_time=").append(sign_time)
                  .append("&user_id=").append(uid);
            String encodeStr = URLEncoder.encode(sbSign.toString(), "utf-8");
            String sign = HMACSHA1.getSignature(encodeStr, ConfigUrl.IsOnline ? ConfigConstant.MJ_KEY_1 : "abc");
            params.put("user_id", uid);
            params.put("sign_time", sign_time);
            params.put("sign", sign);
            params.put("operate", operate);
            params.put("page", page);
            params.put("page_cnt", page_cnt);
            params.put("logs", logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().post(BaseApplication.INSTANCE, ConfigUrl.getRequestHistoryUrl(), params, new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 删除在线历史记录
     * @param operate 1：单条删除[objec_id必填]，2：全部删除
     * @param object_id 资源id
     * @param apiCallBack
     */
    public void deleteCinemaHistory(String operate, String object_id, ApiCallBack<String> apiCallBack) {
        RequestParams params = new RequestParams();
        try {
            String uid = UserSpBusiness.getInstance().getUid();
            String sign_time = String.valueOf(System.currentTimeMillis() / 1000);
            StringBuilder sbSign = new StringBuilder();
            sbSign.append("object_id=").append(object_id).append("&operate=").append(operate)
                    .append("&sign_time=").append(sign_time).append("&user_id=").append(uid);
            String encodeStr = URLEncoder.encode(sbSign.toString(), "utf-8");
            String sign = HMACSHA1.getSignature(encodeStr, ConfigUrl.IsOnline ? ConfigConstant.MJ_KEY_1 : "abc");
            params.put("user_id", uid);
            params.put("sign_time", sign_time);
            params.put("sign", sign);
            params.put("operate", operate);
            params.put("object_id", object_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getAsyncHttpClient().post(BaseApplication.INSTANCE, ConfigUrl.getDeleteHistoryUrl(), params,new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 获取所有筛选类型数据
     *
     * @param context
     * @param url
     * @param apiCallBack
     */
    public void getCateList(Context context, String url, ApiCallBack<ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>>> apiCallBack) {
        getAsyncHttpClient().get(context, url, null, false, "", new ApiResponseHandler<ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>>>(apiCallBack) {
            @Override
            public ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>> parseResponse(String responseString) {
                ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>>>() {
                });
                return bean;
            }
        });
    }

    /**
     * 获取筛选数据
     *
     * @param context
     * @param url
     * @param apiCallBack
     */
    public void getSelectApi(Context context, String url, ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>> apiCallBack) {
        getAsyncHttpClient().get(context, url, null, false, "", new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>(apiCallBack) {
            @Override
            public ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                });
                return bean;
            }
        });
    }
}