package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.baofeng.mj.R;
import com.baofeng.mj.business.accountbusiness.login.BaseLoginManager;
import com.baofeng.mj.business.accountbusiness.login.SinaLogin;
import com.baofeng.mj.business.accountbusiness.login.TencentLogin;
import com.baofeng.mj.business.accountbusiness.login.WeChatLogin;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;

public class NativeLoginActivity extends BaseActivity {

    private String mType;
    private int mLoginType;
    private BaseLoginManager mBaseLoginManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_login);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mType = getIntent().getExtras().getString("type");
            mLoginType = getIntent().getExtras().getInt("loginType");
        }
        if (mLoginType == 1) {
            mBaseLoginManager = new SinaLogin(this, mType);
            mBaseLoginManager.onClickLogin();
        } else if (mLoginType == 2) {
            //QQ为startActivityForResult 暂不finish
            mBaseLoginManager = new TencentLogin(this, mType);
            mBaseLoginManager.onClickLogin();
        } else if (mLoginType == 3) {
            mBaseLoginManager = new WeChatLogin(this, mType);
            mBaseLoginManager.onClickLogin();
            UserSpBusiness.getInstance().setWechatLogin(mType);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int qqRequestCode = 11101;
        if(requestCode == qqRequestCode&&mBaseLoginManager instanceof TencentLogin){
            ((TencentLogin)mBaseLoginManager).onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
