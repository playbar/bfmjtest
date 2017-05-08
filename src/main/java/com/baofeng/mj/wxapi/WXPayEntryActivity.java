package com.baofeng.mj.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.baofeng.mj.business.accountbusiness.deposit.WXPayUtil;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.ui.activity.BaseActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/***
 * 微信支付回调
 */

public class WXPayEntryActivity extends BaseActivity  implements IWXAPIEventHandler {

    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, ConfigConstant.APP_KEY_WEIXIN);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    /***
     * 请求 微信app Request
     * @param baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /***
     * 响应 微信App response
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        WXPayUtil.getInstance().onResponse(baseResp);
        finish();
    }
}
