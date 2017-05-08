package com.baofeng.mj.util.netutil;

import android.content.Context;

/**
 * 评论接口
 * Created by hanyang on 2016/6/13.
 */
public class CommentInfoApi extends BaseApi {
    /**
     * 获取评论列表
     *
     * @param context
     * @param listUrl
     * @param apiCallBack
     */
    public void getCommentList(Context context, String listUrl, ApiCallBack<String> apiCallBack) {
        getAsyncHttpClient().get(context, listUrl, null, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }
}
