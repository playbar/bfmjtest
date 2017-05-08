package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.HomeSubTabVRBean;
import com.baofeng.mj.bean.LiveBean;
import com.baofeng.mj.bean.MainSubContentBean;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.bean.MainTabBean;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.bean.SubList;
import com.baofeng.mj.bean.TopicBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.net.AsyncHttpClient;
import com.baofeng.mj.utils.ACache;

import java.io.File;
import java.util.List;

/**
 * 精选页面网络请求API
 * Created by muyu on 2016/4/1.
 */
public class ChoicenessApi extends BaseApi {

    /**
     * 请求应用主接口一级Tab，二级Tab接口
     * @param context
     * @param apiCallBack
     */
    public void getMainTabList(Context context, ApiCallBack<ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>>> apiCallBack) {
        getAsyncHttpClient().get(context, ConfigUrl.getMainTabUrl(context), null, true, "getMainTabList" , new ApiResponseHandler<ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>> parseResponse(String responseString) {
                ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>>>() {
                        });
                return bean;
            }
        });
    }

    /**
     * 获取首页VR分类
     * @param context
     * @param apiCallBack
     */
    public void getMainSubTabVR(Context context, String url, ApiCallBack<ResponseBaseBean<SubList<HomeSubTabVRBean>>> apiCallBack) {
        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context) + url, null, true, "getMainSubTabVR"+url, new ApiResponseHandler<ResponseBaseBean<SubList<HomeSubTabVRBean>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<SubList<HomeSubTabVRBean>> parseResponse(String responseString) {
                ResponseBaseBean<SubList<HomeSubTabVRBean>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<SubList<HomeSubTabVRBean>>>() {
                        });
                return bean;
            }
        });
    }

    /**
     * 获取首页2D分类
     * @param context
     * @param url
     * @param apiCallBack
     */
    public void getMainSubTab2D(Context context, String url, ApiCallBack<ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>>> apiCallBack) {
        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context) + url, null, true, "getMainSubTab2D"+url , new ApiResponseHandler<ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>>> (apiCallBack) {

            @Override
            public ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>> parseResponse(String responseString) {
                ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>>>() {
                        });
                return bean;
            }
        });
    }


    /**
     * 二级Tab中具体内容接口
     * @param context
     * @param url
     * @param apiCallBack
     */
    public void getMainSubTabInfo(Context context, String url, ApiCallBack<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>> apiCallBack) {
        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context) + url, null, true, "getMainSubTabInfo"+ url, new ApiResponseHandler<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>() {
                        });
                return bean;
            }
        });
    }

    //全景详情页面 type=4
    public void getPanoramaDetailInfo(Context context, String url, ApiCallBack<ResponseBaseBean<PanoramaVideoBean>> apiCallBack) {

        ReportFromBean msg = ReportBusiness.getInstance().get(url);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        client.get(context, ConfigUrl.getServiceUrl(context) + url, null, true, "getPanoramaDetailInfo" + url, new ApiResponseHandler<ResponseBaseBean<PanoramaVideoBean>>(apiCallBack) {

            @Override
            public ResponseBaseBean<PanoramaVideoBean> parseResponse(String responseString) {
                return JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<PanoramaVideoBean>>() {
                        });
            }
        });
    }

    /**
     * 全景详情页缓存数据
     * @param url
     * @param cacheCallBack 缓存回调
     */
    public void getPanoramaDetailInfo(String url, CacheCallBack<ResponseBaseBean<PanoramaVideoBean>> cacheCallBack) {
        File cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = BaseApplication.INSTANCE.getCacheDir();
        }
        String value = ACache.get(cacheDir).getAsString("getPanoramaDetailInfo" + url);
        if (TextUtils.isEmpty(value)) {
            if(cacheCallBack != null){
                cacheCallBack.onCache(null);
            }
        }else{
            if(cacheCallBack != null){
                cacheCallBack.onCache(JSON.parseObject(value, new TypeReference<ResponseBaseBean<PanoramaVideoBean>>() {}));
            }
        }
    }

    //全景详情页面 type=4
    public void getPanoramaDetailInfoNoHeader(Context context, String url, ApiCallBack<ResponseBaseBean<PanoramaVideoBean>> apiCallBack) {

        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context)+ url, null, true, "getPanoramaDetailInfo" + url, new ApiResponseHandler<ResponseBaseBean<PanoramaVideoBean>>(apiCallBack) {

            @Override
            public ResponseBaseBean<PanoramaVideoBean> parseResponse(String responseString) {

                ResponseBaseBean<PanoramaVideoBean> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<PanoramaVideoBean>>() {
                        });
                return bean;
            }
        });
    }

    //直播详情页面 type=5
    public void getLiveDetailInfo(Context context, String url, ApiCallBack<ResponseBaseBean<LiveBean>> apiCallBack) {

        ReportFromBean msg = ReportBusiness.getInstance().get(url);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        client.get(context, ConfigUrl.getServiceUrl(context) + url, null, true, "getLiveDetailInfo" + url, new ApiResponseHandler<ResponseBaseBean<LiveBean>>(apiCallBack) {

            @Override
            public ResponseBaseBean<LiveBean> parseResponse(String responseString) {

                ResponseBaseBean<LiveBean> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<LiveBean>>() {
                        });
                return bean;
            }
        });
    }

    /**
     * 直播详情页缓存数据
     * @param url
     * @param cacheCallBack 缓存回调
     */
    public void getLiveDetailInfo(String url, CacheCallBack<ResponseBaseBean<LiveBean>> cacheCallBack) {
        File cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = BaseApplication.INSTANCE.getCacheDir();
        }
        String value = ACache.get(cacheDir).getAsString("getLiveDetailInfo" + url);
        if (TextUtils.isEmpty(value)) {
            if(cacheCallBack != null){
                cacheCallBack.onCache(null);
            }
        }else{
            if(cacheCallBack != null){
                cacheCallBack.onCache(JSON.parseObject(value, new TypeReference<ResponseBaseBean<LiveBean>>() {}));
            }
        }
    }

    //专题页面 type=8
    public void getTopicDetailInfo(Context context, String url, ApiCallBack<ResponseBaseBean<TopicBean>> apiCallBack) {

        ReportFromBean msg = ReportBusiness.getInstance().get(url);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        client.get(context, ConfigUrl.getServiceUrl(context)+ url, null, true, "getTopicDetailInfo" + url, new ApiResponseHandler<ResponseBaseBean<TopicBean>>(apiCallBack) {

            @Override
            public ResponseBaseBean<TopicBean> parseResponse(String responseString) {
                ResponseBaseBean<TopicBean> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<TopicBean>>() {
                        });
                return bean;
            }
        });
    }

    public void getTopicDetailListInfo(Context context, String url, ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>> apiCallBack) {

        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context)+ url, null, true, "getTopicDetailInfo" + url, new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> parseResponse(String responseString) {

                ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                        });
                return bean;
            }
        });
    }

    //type = 11 列表页面
    public void getAppListInfo(Context context, String url, ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>> apiCallBack) {

        ReportFromBean msg = ReportBusiness.getInstance().get(url);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        client.get(context, ConfigUrl.getServiceUrl(context)+ url, null, true, "getAppListInfo"+url, new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> parseResponse(String responseString) {

                ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                        });
                return bean;
            }
        });
    }

    //type = 11 列表页面
    public void getAppListInfoNoHeader(Context context, String url, ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>> apiCallBack) {

        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context)+ url, null, true, "getAppListInfoNoHeader"+url, new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> parseResponse(String responseString) {

                ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                        });
                return bean;
            }
        });
    }

    //type = 9 频道页
    public void getChoicenessInfo(Context context, String url, ApiCallBack<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>> apiCallBack) {

        ReportFromBean msg = ReportBusiness.getInstance().get(url);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        client.get(context, ConfigUrl.getServiceUrl(context)+ url, null, true, "getChoicenessInfo"+ url, new ApiResponseHandler<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>(apiCallBack) {

            @Override
            public ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> parseResponse(String responseString) {

                ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>() {
                        });
                return bean;
            }
        });
    }


}
