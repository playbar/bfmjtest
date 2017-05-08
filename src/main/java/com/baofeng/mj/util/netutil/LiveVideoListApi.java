package com.baofeng.mj.util.netutil;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;

import java.util.ArrayList;

/**
 * 直播列表请求api
 * Created by yushaochen on 2017/3/1.
 */

public class LiveVideoListApi extends BaseApi {

    /**
     * 获取直播列表内容
     * @param context
     * @param reqUrl
     * @param apiCallBack
     */
    public void getList(Context context, String reqUrl, ApiCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>> apiCallBack){

        getAsyncHttpClient().get(context, ConfigUrl.getServiceUrl(context)+ reqUrl, null, false, "",new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>(apiCallBack) {
            @Override
            public ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {
                });
                return bean;
            }
        });
    }
}
