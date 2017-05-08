package com.baofeng.mj.business.brbusiness;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.unity.UnityActivity;

/**
 * Created by liuchuanchi on 2016/7/12.
 * 通知广播
 */
public class PushReceiver extends BroadcastReceiver {
    public static final String ACTION_REDIRECT_NO = "action.REDIRECT_NO";//不跳转
    public static final String ACTION_REDIRECT_OUT = "action.REDIRECT_OUT";//外部跳转
    public static final String ACTION_REDIRECT_INNER = "action.REDIRECT_INNER";//内部跳转

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null){
            return;
        }
        if(ACTION_REDIRECT_NO.equals(intent.getAction())){//不跳转
        }else if(ACTION_REDIRECT_OUT.equals(intent.getAction())){//外部跳转
            Uri uri = Uri.parse(intent.getStringExtra(PushTypeBusiness.REDIRECT_URL));
            Intent newIntent = new Intent(Intent.ACTION_VIEW, uri);

            Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
            if(curActivity == null){
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }else{
                if(curActivity instanceof UnityActivity){//当前是横屏
                }else{//当前是竖屏
                    curActivity.startActivity(newIntent);
                }
            }
        }else if(ACTION_REDIRECT_INNER.equals(intent.getAction())){//内部跳转
            Intent newIntent = new Intent(BaseApplication.INSTANCE, MainActivityGroup.class);
            newIntent.putExtras(intent.getExtras());
            Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
            if(curActivity == null){
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }else{
                if(curActivity instanceof UnityActivity){//当前是横屏
                }else{//当前是竖屏
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除掉MainActivityGroup之上的所有activity
                    curActivity.startActivity(newIntent);
                }
            }
        }
    }
}
