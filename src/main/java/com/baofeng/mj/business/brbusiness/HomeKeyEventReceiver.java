package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baofeng.mj.business.publicbusiness.BaseApplication;

import java.util.ArrayList;

/**
 * Created by liuchuanchi on 2016/6/23.
 * Home键监听
 */
public class HomeKeyEventReceiver extends BroadcastReceiver {
    String SYSTEM_REASON = "reason";
    String SYSTEM_HOME_KEY = "homekey";
    String SYSTEM_HOME_KEY_LONG = "recentapps";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                // 表示按了home键,程序到了后台
                BaseApplication.INSTANCE.setEnableToLandscapeCondition2(true);
            } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                // 表示长按home键,显示最近使用的程序列表

            }
        }
    }
}
