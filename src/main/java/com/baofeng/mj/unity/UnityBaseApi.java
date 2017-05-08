package com.baofeng.mj.unity;

import com.baofeng.mj.net.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

/**网络请求API基类
 * Created by muyu on 2016/4/1.
 */
public class UnityBaseApi {
    //    private AsyncHttpClient asyncHttpClient;
    //    private AsyncHttpClient noneRetryAsyncHttpClient;

    protected SyncHttpClient getAsyncHttpClient() {
        //if (asyncHttpClient == null) {
        //   asyncHttpClient = new AsyncHttpClient();
        //}
        SyncHttpClient asyncHttpClient = new SyncHttpClient();
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
