package com.baofeng.mj.util.publicutil;

import android.text.TextUtils;

import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;

/**
 * Created by dupengwei on 2017/4/21.
 */

public class GlassesUtils {

    /**
     * true需要判断文件是否存在，false不需要
     * @param needJudgeFile
     *
     */
    public static void setDefaultGlasses(boolean needJudgeFile){
        String channelId = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        if(!TextUtils.isEmpty(channelId)){
            String cmsGlassesId = ChannelUtil.getChannelCode("GLASSES_ID");
            GlassesManager.setDefaultGlasses(BaseApplication.INSTANCE,channelId,cmsGlassesId,needJudgeFile);
        }

    }


}
