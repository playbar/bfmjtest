package com.baofeng.mj.business.accountbusiness.login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.ui.activity.LoginActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by zhaominglei on 2016/5/16.
 * 微信登录
 */
public class WeChatLogin extends BaseLoginManager {

    private IWXAPI mWeixinAPI;
    private String mAppId;

    public WeChatLogin(Context context, String type) {
        super(context, type);
        mAppId = ConfigConstant.APP_KEY_WEIXIN;
        mWeixinAPI = WXAPIFactory.createWXAPI(mContext, mAppId, false);
    }

    @Override
    public void onClickLogin() {
        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(mContext, mAppId, false);
        }
        if (!mWeixinAPI.isWXAppInstalled()) {
            //提醒用户没有按照微信
            Toast.makeText(mContext, R.string.not_install_wechat, Toast.LENGTH_SHORT).show();
            startLoginActivity(mContext);
            return;
        }
        mWeixinAPI.registerApp(mAppId);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//WEIXIN_SCOPE;
        req.state = "mj";//WEIXIN_STATE;
        mWeixinAPI.sendReq(req);
    }

    public void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
