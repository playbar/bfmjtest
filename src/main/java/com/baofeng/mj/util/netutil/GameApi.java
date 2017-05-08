package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.CommentListBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.net.AsyncHttpClient;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.baofeng.mj.utils.ACache;

import java.io.File;
import java.util.List;

/**
 * Created by hanyang on 2016/5/17.
 */
public class GameApi extends BaseApi {
    /**
     * 游戏列表接口
     *
     * @param context
     * @param listUrl
     * @param apiCallBack
     */
    public void getGameList(Context context, String listUrl, ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>> apiCallBack) {
        getAsyncHttpClient().get(context, listUrl, null, false, "", new ApiResponseHandler<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>(apiCallBack) {
            @Override
            public ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                });
                return bean;
            }
        });
    }

    /**
     * 游戏资源详情接口
     *
     * @param context
     * @param detailUrl
     * @param apiCallBack
     */
    public void getGameDetailInfo(Context context, String detailUrl, ApiCallBack<ResponseBaseBean<GameDetailBean>> apiCallBack) {
        //报数
        String key=detailUrl.replace(ConfigUrl.getServiceUrl(context),"");
        ReportFromBean msg = ReportBusiness.getInstance().get(key);
        AsyncHttpClient client = getAsyncHttpClient();
        if (msg != null) {
            client.addHeader("User-Agent", ReportBusiness.getInstance().getHeaderAgent(msg));
        }
        if (!detailUrl.startsWith("http://")) {
            detailUrl = ConfigUrl.getGameDetailUrl(context, detailUrl);
        }
        client.get(context, detailUrl, null, true, "getGameDetailInfo" + detailUrl, new ApiResponseHandler<ResponseBaseBean<GameDetailBean>>(apiCallBack) {
            @Override
            public ResponseBaseBean<GameDetailBean> parseResponse(String responseString) {
                ResponseBaseBean<GameDetailBean> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<GameDetailBean>>() {
                });
                return bean;
            }
        });
    }

    /**
     * 游戏资源缓存数据
     * @param url
     * @param cacheCallBack 缓存回调
     */
    public void getGameDetailInfo(String url, CacheCallBack<ResponseBaseBean<GameDetailBean>> cacheCallBack) {
        File cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = BaseApplication.INSTANCE.getCacheDir();
        }
        String value = ACache.get(cacheDir).getAsString("getGameDetailInfo" + url);
        if (TextUtils.isEmpty(value)) {
            if(cacheCallBack != null){
                cacheCallBack.onCache(null);
            }
        }else{
            if(cacheCallBack != null){
                cacheCallBack.onCache(JSON.parseObject(value, new TypeReference<ResponseBaseBean<GameDetailBean>>() {}));
            }
        }
    }

    /**
     * 游戏资源详情接口
     *
     * @param context
     * @param detailUrl
     * @param apiCallBack
     */
    public void getGameDetailInfoNoHeader(Context context, String detailUrl, ApiCallBack<ResponseBaseBean<GameDetailBean>> apiCallBack) {
        if (!detailUrl.startsWith("http://")) {
            detailUrl = ConfigUrl.getGameDetailUrl(context, detailUrl);
        }
        getAsyncHttpClient().get(context, detailUrl, null, false, "", new ApiResponseHandler<ResponseBaseBean<GameDetailBean>>(apiCallBack) {
            @Override
            public ResponseBaseBean<GameDetailBean> parseResponse(String responseString) {
                ResponseBaseBean<GameDetailBean> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<GameDetailBean>>() {
                });
                return bean;
            }
        });
    }

    /**
     * 获取评论信息接口
     *
     * @param context
     * @param uid
     * @param pageNum
     * @param dataNum
     * @param res_id
     * @param res_type
     * @param apiCallBack
     */
    public void getGameCommentList(Context context, String uid, int pageNum, int dataNum, String res_id, int res_type, ApiCallBack<ResponseBaseBean<CommentListBean>> apiCallBack) {
        final String MJ_KEY_LIHAO = "Bf@)(*$s1&2^3XVF#Mj";
        StringBuffer sb = new StringBuffer();
        sb.append("&page=").append(pageNum);
        sb.append("&res_id=").append(res_id);
        sb.append("&page_num=").append(dataNum);
        sb.append("&res_type=").append(res_type);
        StringBuffer sbCode = new StringBuffer();
        sbCode.append(pageNum).append(res_id).append(dataNum).append(res_type).append(MJ_KEY_LIHAO);
        sb.append("&sign=").append(MD5Util.MD5(sbCode.toString()));
        getAsyncHttpClient().get(context, ConfigUrl.COMMENT_lIST_URL + sb.toString(), null, false, "", new ApiResponseHandler<ResponseBaseBean<CommentListBean>>(apiCallBack) {
            @Override
            public ResponseBaseBean<CommentListBean> parseResponse(String responseString) {
                ResponseBaseBean<CommentListBean> bean = JSON.parseObject(responseString, new TypeReference<ResponseBaseBean<CommentListBean>>() {
                });
                return bean;
            }
        });
    }

    /**
     * 添加评论接口
     *
     * @param context
     * @param uid
     * @param nickName
     * @param res_id
     * @param res_type
     * @param res_name
     * @param score
     * @param content
     * @param apiCallBack
     */
    public void sendComment(Context context, String uid, String nickName, String res_id, int res_type, String res_name, int score, String content, ApiCallBack<String> apiCallBack) {
        String version = ApkUtil.getVersionNameSuffix();
        final String MJ_KEY_LIHAO = "Bf@)(*$s1&2^3XVF#Mj";
        StringBuffer sb = new StringBuffer();
        sb.append("&uid=").append(uid);
        sb.append("&nickname=").append(nickName);
        sb.append("&res_id=").append(res_id);
        sb.append("&res_type=").append(res_type);
        sb.append("&res_name=").append(res_name);
        sb.append("&score=").append(score);
        sb.append("&content=").append(content);
        sb.append("&platform_id=").append("0");
        sb.append("&version=").append(version);
        final String sign = MD5Util.MD5(uid + nickName + res_id + res_type + res_name + score + content + 0 + version + MJ_KEY_LIHAO);
        sb.append("&sign=").append(sign);
        getAsyncHttpClient().get(context, ConfigUrl.SEND_COMMENT_URL + sb.toString(), null, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }
}
