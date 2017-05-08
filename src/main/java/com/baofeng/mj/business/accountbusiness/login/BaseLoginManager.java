package com.baofeng.mj.business.accountbusiness.login;

import android.content.Context;

/**
 * Created by zhaominglei on 2016/5/16.
 */
public abstract class BaseLoginManager {
    protected Context mContext;
    protected String mType;

    public BaseLoginManager(){

    }
    public BaseLoginManager(Context context, String type) {
        this.mContext = context;
        this.mType = type;
    }

    public abstract void onClickLogin();
}
