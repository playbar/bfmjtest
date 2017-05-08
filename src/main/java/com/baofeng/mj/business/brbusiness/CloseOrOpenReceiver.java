package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;
import com.storm.smart.common.utils.LogHelper;

/**
 * Created by dupengwei on 2017/3/17.
 */

public class CloseOrOpenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogHelper.e("infossss","CloseOrOpenReceiver.intent======="+intent);
        if(null != intent){
            if(intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")){
//                DownloadUtils.getInstance().stopAllDownload(BaseApplication.INSTANCE);
            }else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            }
        }

    }
}
