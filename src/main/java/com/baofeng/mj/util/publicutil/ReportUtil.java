package com.baofeng.mj.util.publicutil;

import android.app.Activity;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ApiResponseHandler;
import com.baofeng.mj.util.netutil.BaseApi;

/**
 * Created by liuchuanchi on 2016/7/12.
 * 上报工具类
 */
public class ReportUtil extends BaseApi {
    /**
     * 上报下载量
     * @param resId 资源id
     */
    public void reportDownloadNum(final String resId, final ApiCallBack<String> apiCallBack){
        Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
        if(curActivity != null){
            curActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String reportUrl = ConfigUrl.getReportDownloadUrl(resId);
                    getAsyncHttpClient().get(BaseApplication.INSTANCE, reportUrl, null, false, "", new ApiResponseHandler<String>(apiCallBack) {
                        @Override
                        public String parseResponse(String responseString) {
                            return responseString;
                        }
                    });
                }
            });
        }
    }

}
