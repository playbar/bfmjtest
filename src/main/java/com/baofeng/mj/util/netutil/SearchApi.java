package com.baofeng.mj.util.netutil;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.KeyWordBean;
import com.baofeng.mj.bean.MainSubContentBean;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.SearchResultBean;

import java.util.List;


/**
 * Created by sunshine on 16/9/20.
 * 全局搜索接口
 */
public class SearchApi extends BaseApi {
    /**
     * 获取全局搜搜热搜词接口
     *
     * @param context
     * @param keyUrl
     * @param keyWordBeanApiCallBack
     */
    public void getKeyWords(Context context, String keyUrl, ApiCallBack<KeyWordBean> keyWordBeanApiCallBack) {
        getAsyncHttpClient().get(context, keyUrl, null, false, "", new ApiResponseHandler<KeyWordBean>(keyWordBeanApiCallBack) {
            @Override
            public KeyWordBean parseResponse(String responseString) {
                KeyWordBean keyWordBean = JSON.parseObject(responseString, new TypeReference<KeyWordBean>() {
                });
                return keyWordBean;
            }
        });
    }

    /**
     * 获取搜索结果列表
     *
     * @param context
     * @param searchUrl
     * @param searchResultBeanApiCallBack
     */
    public void getSearchResult(Context context, String searchUrl, ApiCallBack<SearchResultBean> searchResultBeanApiCallBack) {
        getAsyncHttpClient().get(context, searchUrl, null, false, "", new ApiResponseHandler<SearchResultBean>(searchResultBeanApiCallBack) {
            @Override
            public SearchResultBean parseResponse(String responseString) {
                SearchResultBean searchResultBean = JSON.parseObject(responseString, new TypeReference<SearchResultBean>() {

                });
                return searchResultBean;
            }
        });
    }
}
