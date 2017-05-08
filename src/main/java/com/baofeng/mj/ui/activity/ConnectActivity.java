package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.publicutil.WeixinShare;

/**
 * 联系方式界面
 */
public class ConnectActivity extends BaseActivity implements View.OnClickListener {
    private AppTitleBackView connect_title;
    private RelativeLayout web_layout, bbs_layout, tel_layout, qq_layout, wx_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initView();
    }

    private void initView() {
        connect_title = (AppTitleBackView) findViewById(R.id.connect_title);
        connect_title.getNameTV().setText("联系方式");
        connect_title.getInvrImgBtn().setVisibility(View.GONE);
        web_layout = (RelativeLayout) findViewById(R.id.web_layout);
        web_layout.setOnClickListener(this);
        bbs_layout = (RelativeLayout) findViewById(R.id.bbs_layout);
        bbs_layout.setOnClickListener(this);
        tel_layout = (RelativeLayout) findViewById(R.id.tel_layout);
        tel_layout.setOnClickListener(this);
        qq_layout = (RelativeLayout) findViewById(R.id.qq_layout);
        qq_layout.setOnClickListener(this);
        wx_layout = (RelativeLayout) findViewById(R.id.wx_layout);
        wx_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.web_layout == id) {
            openWeb("http://mojing.cn");
        } else if (R.id.bbs_layout == id) {
            openWeb("http://bbs.mojing.cn");
        } else if (R.id.tel_layout == id) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:400-810-8689"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (R.id.qq_layout == id) {
            openWeb("http://bbs.mojing.cn/thread-9801-1-1.html");
        } else if (R.id.wx_layout == id) {
            WeixinShare weixin = new WeixinShare(this);
            boolean status = weixin.openWeiXinApp();
            if (status == false) {
                Toast.makeText(this, "尚未安装微信", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWeb(String url) {
        if (TextUtils.isEmpty(url))
            return;

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
