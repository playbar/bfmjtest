package com.baofeng.mj.wxapi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.ui.activity.BaseActivity;
import com.baofeng.mj.ui.activity.BindPhoneActivity;
import com.baofeng.mj.ui.activity.LoginActivity;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.List;

/***
 * 微信分享回调
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    //IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    private String app_id = ConfigConstant.APP_KEY_WEIXIN;

    private Handler mHandler = new Handler();

    private String mOpenType = "login_weixin";

    private Context mContext;

    private String mOpenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        api = WXAPIFactory.createWXAPI(this, app_id, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) resp).code;
                new UserInfoApi().getWxOpenId(code, new ApiCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.has("openid") && jsonObject.has("unionid")) {
                                mOpenId = jsonObject.getString("openid") + "|" + jsonObject.getString("unionid");
                                queryUserIdByOpenId();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                    }

                });
//                JoinLoginNetWork.getInstance().WeixinGetOpenid(code, new com.baofeng.mj.login.join.JoinLoginNetWork.IcallBack() {
//
//                    @Override
//                    public void resultCallBack(final Object obj) {
//                        JSONObject jsonObj = (JSONObject) obj;
//                        String openid = "";
//                        if(jsonObj.has("openid")){
//                            try {
//                                openid = jsonObj.getString("openid");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        network(openid);
//                    }
//                });
                break;

            //分享取消
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                startLoginActivity();
                break;

            //分享拒绝
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                startLoginActivity();
                break;

            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void queryUserIdByOpenId() {
        new UserInfoApi().queryUserIdByOpenId(this, mOpenType, mOpenId, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        //找到用户信息的
                        queryUserInfoById(jsonObject.getString("msg"));
                    } else {
                        //没有找到用户信息
                        startPhoneBindActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
    }

    private void queryUserInfoById(final String id) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new UserInfoApi().queryUserInfoById(mContext, id, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
                    @Override
                    public void onSuccess(Response<List<SimpleUserInfo>> result) {
                        super.onSuccess(result);
                        if (result.data != null && result.data.size() > 0) {
                            SimpleUserInfo userInfo = result.data.get(0);
                            if (TextUtils.isEmpty(userInfo.user_tel)) {
                                startPhoneBindActivity();
                            } else {
                                startAccountActivity();
                            }
                        }else{
                            startPhoneBindActivity();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        startLoginActivity();
                    }
                });
            }
        });
    }

    //登陆过程异常 返回登陆页
    private void startLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
        finish();
    }

    //手机号码为空时
    private void startPhoneBindActivity() {
        Intent intent = new Intent(mContext, BindPhoneActivity.class);
        intent.putExtra("loginType", mOpenType);
        intent.putExtra("openId", mOpenId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        finish();
    }

    //登陆成功
    private void startAccountActivity() {
//        Intent intent = new Intent(mContext, AccountActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        mContext.startActivity(intent);

        if(null != LoginActivity.loginActivity) {
            LoginActivity.loginActivity.finish();
        }

        finish();
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
