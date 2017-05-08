package com.baofeng.mj.util.netutil;

import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.loopj.android.http.RequestParams;

/**
 * Created by zhaominglei on 2016/6/17.
 */
public class AppUpdateApi extends BaseApi {


    public void checkAppUpdate(String localVersionCode,
                               String sysVersion, String mac,
                               ApiCallBack<String> apiCallBack) {
        String url = ConfigUrl.VERSION_CODE_PATH;
        RequestParams params = new RequestParams();
        params.put("type", "m");
        params.put("tvver", localVersionCode);
        params.put("sysver", sysVersion);
        params.put("mac", mac);
        getAsyncHttpClient().get(url, params, new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }
}
