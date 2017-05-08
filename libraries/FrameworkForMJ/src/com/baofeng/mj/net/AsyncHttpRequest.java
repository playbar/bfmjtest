package com.baofeng.mj.net;

import android.text.TextUtils;
import android.util.Log;

import com.anjoyo.framework.BuildConfig;
import com.baofeng.mj.utils.ACache;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by fei-ke on 2015/1/20.
 */
public class AsyncHttpRequest extends com.loopj.android.http.AsyncHttpRequest implements Runnable {
    private final TextHttpResponseHandler responseHandler;

    //缓存相关
    private String cacheKey;
    private boolean isCacheAble;
    private int cacheTime = 0;//保存时间，单位秒,默认0永久保存
    private File cacheDir;//缓存目录

    public AsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request,
                            TextHttpResponseHandler responseHandler, boolean isCacheAble, String cacheKey, File cacheDir, int cacheTime) {
        super(client, context, request, responseHandler);

        this.responseHandler = responseHandler;

        //
        this.isCacheAble = isCacheAble;
        this.cacheDir = cacheDir;
        this.cacheTime = cacheTime;
        this.cacheKey = TextUtils.isEmpty(cacheKey) ? request.getURI().toString() : cacheKey;
    }


    @Override
    protected void makeRequest() throws IOException {
        if (isCancelled()) {
            return;
        }
        // Fixes #115
        if (request.getURI().getScheme() == null) {
            // subclass of IOException so processed in the caller
            throw new MalformedURLException("No valid URI scheme was provided");
        }

        // ---------读取缓存------------------------
        if (isCacheAble) {
            ACache aCache = ACache.get(cacheDir);
            if (BuildConfig.DEBUG) {
                Log.i("读缓存 key:", "" + cacheKey);
            }
            byte[] cache = aCache.getAsBinary(cacheKey);
            if (cache != null) {
                responseHandler.sendCacheMessage(cache);
            }
        }
        // ---------读取缓存结束------------------------

        HttpResponse response = client.execute(request, context);

        //------------- 写缓存--------------
        if (!isCancelled() && responseHandler != null) {
            byte[] cache = responseHandler.sendResponseMessageWithCache(response);
            if (isCacheAble && cache != null) {
                ACache aCache = ACache.get(cacheDir);
                if (BuildConfig.DEBUG) {
                    Log.i("写缓存 key:", "" + cacheKey);
                }
                if (cacheTime == 0) {
                    aCache.put(cacheKey, cache);
                } else {
                    aCache.put(cacheKey, cache, cacheTime);
                }
            }
        }
        //------------- 写缓存结束-------------

    }
}
