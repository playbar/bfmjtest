package com.baofeng.mj.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;

import org.json.JSONObject;

import java.util.List;

public class StormLoginActivity extends BaseActivity {

    private static final String REDIRECT_URI = "http://sso.mojing.cn/user/party/ssobaofeng";

    private String mWebviewUrl;

    private WebView mWebView;

    private Context mContext;
    //登录、注册
    private String mType;

    private String mOpenType="login_baofeng";

    private String mOpenId;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mContext = this;
        mWebviewUrl = ConfigUrl.STORM_OAUTH2 + "?display=mobile&client_id=" + ConfigConstant.MJ_STORM_APP_ID + "&redirect_uri=" + REDIRECT_URI;
        mWebView = (WebView) findViewById(R.id.webview);
        initView();
    }

    private void initView() {

        WebSettings webSettings = mWebView.getSettings();
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置 缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 database storage API 功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // / 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //清除缓存
        mWebView.clearCache(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);


        mWebView.setWebChromeClient(new WebChromeClient() {
                                        @Override
                                        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                                            return super.onJsAlert(view, url, message, result);
                                        }
                                    }
        );

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                if (url.startsWith(REDIRECT_URI)) {
                    int index = url.indexOf("token");
                    if (index > 0) {
                        String token = url.substring(index + 6);
                        mOpenId=token;
                        queryStormUserInfo(token);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mWebView.loadUrl(mWebviewUrl);
    }

    @Override
    protected void onResume() {
        if (mWebView != null)
            mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
        Common.hideSoftInput(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void queryUserInfoById(final String uid) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new UserInfoApi().queryUserInfoById(mContext, uid, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
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
                            //用户信息为空
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

    private void queryStormUserInfo(final String token) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new UserInfoApi().getStormUserInfo(mContext, token, new ApiCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        try {
                            if (TextUtils.isEmpty(result)) {
                                return;
                            }
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getInt("status") == 1) {
                                JSONObject subObj=jsonObject.getJSONObject("info");
                                String userId=subObj.getString("userid");
                                queryUserInfoById(userId);
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
        intent.putExtra("openId",mOpenId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

}
