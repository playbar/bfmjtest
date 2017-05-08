package com.baofeng.mj.util.netutil;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.SubAlbumBean;
import com.loopj.android.http.RequestParams;

/**
 * Created by hanyang on 2016/6/21.
 * 订阅接口
 */
public class SubApi extends BaseApi {
    /**
     * 扫描订阅接口
     *
     * @param context
     * @param url
     * @param requestParams
     * @param apiCallBack
     */
    public void Subscribe(Context context, String url, RequestParams requestParams, ApiCallBack<String> apiCallBack) {
        getAsyncHttpClient().post(context, url, requestParams, new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 获取用户订阅专辑列表
     *
     * @param context
     * @param url
     * @param requestParams
     * @param apiCallBack
     */
    public void getAlbumList(Context context, String url, RequestParams requestParams, ApiCallBack<SubAlbumBean> apiCallBack) {
        getAsyncHttpClient().post(context, url, requestParams, new ApiResponseHandler<SubAlbumBean>(apiCallBack) {
            @Override
            public SubAlbumBean parseResponse(String responseString) {
                SubAlbumBean subAlbumBean = JSON.parseObject(responseString, new TypeReference<SubAlbumBean>() {
                });
                return subAlbumBean;
            }
        });
    }

    /**
     * 取消订阅接口
     *
     * @param context
     * @param url
     * @param requestParams
     * @param apiCallBack
     */
    public void cancleAlbum(Context context, String url, RequestParams requestParams, ApiCallBack<String> apiCallBack) {
        getAsyncHttpClient().post(context, url, requestParams, new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }
}
