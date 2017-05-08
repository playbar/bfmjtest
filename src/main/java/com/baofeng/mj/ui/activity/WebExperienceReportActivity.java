package com.baofeng.mj.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.accountbusiness.ExperienceReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.publicutil.ApkUtil;

public class WebExperienceReportActivity extends Activity  implements ExperienceReportBusiness.ExperienceReportListener {

    private AppTitleBackView experience_report_title_bar;
    private WebView mWebView;
    private TextView mExperienceReportHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (!UserSpBusiness.getInstance().isUserLogin()) {
//            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
        setContentView(R.layout.activity_web_experience_report);
        findViewByIds();
        ExperienceReportBusiness.getInstance().setExperienceReportListener(this);
        ExperienceReportBusiness.getInstance().checkExperienceReport(this);
        init();
    }

    private void findViewByIds() {
        experience_report_title_bar = (AppTitleBackView) findViewById(R.id.experience_report_title_bar);
        mWebView = (WebView) findViewById(R.id.web_view);
        mExperienceReportHint = (TextView) findViewById(R.id.experience_report_hint);
    }

    private void init() {
        mWebView = (WebView) findViewById(R.id.web_view);
        // WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
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
        mWebView.getSettings().setBuiltInZoomControls(true);
        // 扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        // 自适应屏幕
//		mWebView.getSettings()
//				.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        experience_report_title_bar.getNameTV().setText(getResources().getString(R.string.mj_experience_report));
        experience_report_title_bar.getInvrImgBtn().setVisibility(View.GONE);

        String url = SettingSpBusiness.getInstance().getReprotUrl();
        if (url.equals("")) {
            mExperienceReportHint.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        } else {
            mWebView.loadUrl(setReportUrl(url));
            mWebView.setVisibility(View.VISIBLE);
            mExperienceReportHint.setVisibility(View.GONE);
        }
    }

    private String setReportUrl(String webUrl) {
        if (TextUtils.isEmpty(webUrl)) {
            return "";
        }
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        String uid = userInfo.getUid();
        String username = userInfo.getNikename();
        String mobile = userInfo.getMobile();
        String email = userInfo.getEmail();
        String app_ver = ApkUtil.getVersionNameSuffix();
        String facility = android.os.Build.MODEL;
        String sys_ver = android.os.Build.VERSION.RELEASE;
        StringBuffer sb = new StringBuffer();
        sb.append(webUrl);
        sb.append("&tpl_version=2&uid=");
        sb.append(uid);
        sb.append("&username=");
        sb.append(username);
        sb.append("&mobile=");
        sb.append(mobile);
        sb.append("&email=");
        sb.append(email);
        sb.append("&app_ver=");
        sb.append(app_ver);
        sb.append("&facility=");
        sb.append(facility);
        sb.append("&sys_ver=");
        sb.append(sys_ver);
        return sb.toString();
    }

    @Override
    public void onExperienceReport() {
        init();
    }
}
