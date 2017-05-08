package com.baofeng.mj.business.accountbusiness.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.activity.WBAuthActivity;
import com.baofeng.mj.util.publicutil.ApkUtil;

/**
 * Created by zhaominglei on 2016/5/16.
 * 微信登录
 */
public class SinaLogin extends BaseLoginManager {

    public SinaLogin(Context context,String type){
        super(context,type);
    }
    @Override
    public void onClickLogin() {
        if(!ApkUtil.isAppInstalled("com.sina.weibo")){
            Toast.makeText(mContext, R.string.not_install_weibo, Toast.LENGTH_SHORT).show();
            ((Activity)mContext).finish();
            return;
        }

        Intent intent = new Intent(mContext, WBAuthActivity.class);
        intent.putExtra("type", mType);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }
}
