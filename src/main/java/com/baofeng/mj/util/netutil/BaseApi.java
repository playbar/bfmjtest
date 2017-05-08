package com.baofeng.mj.util.netutil;

import com.baofeng.mj.net.AsyncHttpClient;

/**网络请求API基类
 * Created by muyu on 2016/4/1.
 */
public class BaseApi {
    //    private AsyncHttpClient asyncHttpClient;
    //    private AsyncHttpClient noneRetryAsyncHttpClient;

    protected AsyncHttpClient getAsyncHttpClient() {
        //if (asyncHttpClient == null) {
        //   asyncHttpClient = new AsyncHttpClient();
        //}
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(20 * 1000);
        //asyncHttpClient.setCookieStore(PersistentCookieStore(BaseApplication.getInstance()));
        return asyncHttpClient;
    }

    protected AsyncHttpClient getNoRetryAsyncHttpClient() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(20 * 1000);
        asyncHttpClient.setMaxRetriesAndTimeout(0, 0);
        return asyncHttpClient;
    }


}
