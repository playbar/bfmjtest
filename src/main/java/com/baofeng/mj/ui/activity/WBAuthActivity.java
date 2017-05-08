/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baofeng.mj.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.UsersAPI;

import org.json.JSONObject;

import java.util.List;

/**
 * 该类主要演示如何进行授权、SSO登陆。
 *
 * @author SINA
 * @since 2013-09-29
 */
public class WBAuthActivity extends BaseActivity {
    private final String mPageName = "weibo";
    /** 显示认证后的信息，如 AccessToken */
    // private TextView mTokenText;
    /** 微博 Web 授权类，提供登陆等功能 */
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;
    private String mOpenId;
    private String type;
    private AuthInfo mAuthInfo;
    private Context mContext;
    private String mOpenType;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_login);
        type = getIntent().getExtras().getString("type");
        mContext = this;
        mOpenType = "login_weibo";
        // 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, ConfigConstant.APP_KEY, ConfigConstant.REDIRECT_URL, ConfigConstant.SCOPE);
        mSsoHandler = new SsoHandler(WBAuthActivity.this, mAuthInfo);
        try {
            mSsoHandler.authorize(new AuthListener());
        } catch (Exception e) {
        }
        //mAccessToken = SettingSpBusiness.getInstance().getSinaAccessToken();
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                SettingSpBusiness.getInstance().setSinaAccessToken(mAccessToken);
                UsersAPI mUsersAPI = new UsersAPI(WBAuthActivity.this, ConfigConstant.APP_KEY, mAccessToken);
                mOpenId = mAccessToken.getUid();
                queryUserIdByOpenId();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                Toast.makeText(mContext, "请确保签名正确", Toast.LENGTH_SHORT).show();
            }
            finish();
        }

        @Override
        public void onCancel() {
            startLoginActivity();
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (e.getMessage().equals("21330")) {
            }
            startLoginActivity();
            finish();
        }
    }

    /***
     * 通过openid 查询用户信息
     */
    private void queryUserIdByOpenId() {
        new UserInfoApi().queryUserIdByOpenId(mContext, mOpenType, mOpenId, new ApiCallBack<String>() {
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

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     */
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}