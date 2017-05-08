package com.baofeng.mj.util.netutil;

import com.baofeng.mj.business.firewarebusiness.FirewareBusiness;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;

/**
 * Created by zhanglei1 on 2016/8/3.
 */
public class FirewareUpdateApi extends BaseApi {
    public void checkFirewareUpdate(int type,FirewareBusiness.FirewareData data, ApiCallBack<String> apiCallBack) {
        String url = ConfigUrl.FIREWARE_UPDATE_URL;
        RequestParams params = new RequestParams();
        params.put("pid",data.pid);
        params.put("vid", data.vid);
        int sjbbbh = 0;
        String gjlx = "";
        String key = "cca0cebb4f91491547836f5c92a8b54f";
        switch (type)
        {
            case FirewareBusiness.FIREWARE_TYPE_MCU:
            {
                sjbbbh = data.mcu;
                gjlx = "MCU";
            }
            break;
            case FirewareBusiness.FIREWARE_TYPE_BLE:
            {
                sjbbbh= data.ble;
                gjlx = "BLE";
            }
            break;
            default:break;
        }
        params.put("gjlx", gjlx);
        params.put("sjbbbh", sjbbbh);
        params.put("sjlx", Integer.parseInt(ChannelUtil.getChannelCode("ONLINE_OFFLINE")));
        String sign = MD5Util.MD5(String.valueOf(data.pid) + String.valueOf(data.vid) + gjlx + key);
        params.put("sign",sign);

        System.out.println(String.format("zl->Android FirewareUpdate Start Request Http:%s, Params:%s", url, params));
        getAsyncHttpClient().get(url, params, new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }
}
