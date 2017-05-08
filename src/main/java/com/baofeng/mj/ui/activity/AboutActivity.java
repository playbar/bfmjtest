package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.pluginbusiness.PluginOperateBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mojing.MojingSDK;


/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {
    private AppTitleBackView appTitleLayout;
    private TextView version_code, version_name, model_tag, sdk_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        appTitleLayout = (AppTitleBackView) findViewById(R.id.about_title_layout);
        appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_ABOUT"));
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        version_code = (TextView) findViewById(R.id.version_code);
        version_name = (TextView) findViewById(R.id.version_name);
        model_tag = (TextView) findViewById(R.id.model_tag);
        sdk_tag = (TextView) findViewById(R.id.sdk_tag);
        String verName = ApkUtil.getVersionNameSuffix();
        version_code.setText("V" + verName + "(" + ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID") + ")");
        if (Common.isPublicVersion(getApplicationContext(), verName)) {
            version_name.setText("[正式版]");
        } else {
            version_name.setText("[开发版]");
        }
        model_tag.setText("MODEL:[" + Build.MODEL + "]");

        sdk_tag.setText("SDK:" + getSDKVersion());

    }

    /**
     * 获取sdk版本号
     *
     * @return
     */
    private String getSDKVersion() {
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(this.getApplicationContext());
        }
        String version = MojingSDK.GetSDKVersion();
        try {
            int start = version.indexOf("V");
            int end = version.indexOf(")") + 1;
            version = version.substring(start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }
}
