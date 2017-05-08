package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.utils.ACache;
import com.baofeng.mojing.MojingSDK;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * 推荐页面请求api
 * Created by yushaochen on 2017/1/3.
 */

public class RecommendApi extends BaseApi {

    /**
     * @param context
     * @param apiCallBack
     */
    public void recommendTopReq(Context context, ApiCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>> apiCallBack){

        getAsyncHttpClient().get(context, ConfigUrl.RECOMMEND_TOP, true, "recommendTopReq", new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>(apiCallBack) {
            @Override
            public ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {
                });
                return bean;
            }
        });
    }

    public void getRecommendTopReqCache(CacheCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>> cacheCallBack) {
        File cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = BaseApplication.INSTANCE.getCacheDir();
        }
        String value = ACache.get(cacheDir).getAsString("recommendTopReq");
        if (TextUtils.isEmpty(value)) {
            if(cacheCallBack != null){
                cacheCallBack.onCache(null);
            }
        }else{
            if(cacheCallBack != null){
                cacheCallBack.onCache(JSON.parseObject(value, new TypeReference<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {}));
            }
        }
    }

    /**
     * @param context
     * @param apiCallBack
     */
    public void recommendContentReq(Context context, ApiCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>> apiCallBack){
        RequestParams params = new RequestParams();
        String uuid = MojingSDK.getUserID(context);
        if(uuid != null && !"".equals(uuid)) {
            params.put("deviceid", uuid);
        }
        if (UserSpBusiness.getInstance().isUserLogin()) {
            params.put("userid", UserSpBusiness.getInstance().getUid());
        }

        getAsyncHttpClient().get(context, ConfigUrl.RECOMMEND_CONTENT+new Random().nextInt(1000)+".js", params, true, "recommendContentReq", new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>(apiCallBack) {
            @Override
            public ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {
                });
                return bean;
            }
        });
    }

    public void getRecommendContentReqCache(CacheCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>> cacheCallBack) {
        File cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = BaseApplication.INSTANCE.getCacheDir();
        }
        String value = ACache.get(cacheDir).getAsString("recommendContentReq");
        if (TextUtils.isEmpty(value)) {
            if(cacheCallBack != null){
                cacheCallBack.onCache(null);
            }
        }else{
            if(cacheCallBack != null){
                cacheCallBack.onCache(JSON.parseObject(value, new TypeReference<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {}));
            }
        }
    }
}
