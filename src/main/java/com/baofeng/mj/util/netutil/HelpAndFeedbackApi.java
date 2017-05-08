package com.baofeng.mj.util.netutil;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.bean.FeedbackCommitResult;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;

/**
 * 帮助与意见反馈请求api
 * Created by yushaochen on 2017/1/3.
 */

public class HelpAndFeedbackApi extends BaseApi {

    private static final String KEY = "Bf@)(*$s1&2^3XVF#Mj";

    /**
     * 提交意见反馈内容
     * @param context
     * @param p_content
     * @param phone_num
     * @param p_type
     * @param apiCallBack
     */
    public void feedbackCommit(Context context, String p_content, String phone_num, String p_type, ApiCallBack<FeedbackCommitResult> apiCallBack){
        RequestParams requestParams = new RequestParams();
        //客户端类型（1通用安卓，2通用ios）
        requestParams.put("app_type", 1);
        //app版本号（1.12.1）
        requestParams.put("app_version", ApkUtil.getVersionNameSuffix());
        //问题描述
        requestParams.put("detail", p_content);

        long currentTimeMillis = System.currentTimeMillis()/1000;
        //反馈时间(时间戳) 秒为单位
        requestParams.put("add_time", currentTimeMillis);
        //签名(md5 客户端类型+app版本号+反馈时间+key)
        requestParams.put("sign", MD5Util.MD5("1"+ApkUtil.getVersionNameSuffix()+currentTimeMillis+KEY));
        //手机品牌（apple）
        requestParams.put("phone_brand", Build.BRAND);
        //手机型号（iphone6s）
        requestParams.put("phone_model", Build.MODEL);
        //手机操作系统（ios）
        requestParams.put("phone_os", "android");
        //眼镜名称（魔镜5代）
        GlassesNetBean bean = GlassesManager.getGlassesNetBean();
        if(null != bean){
            requestParams.put("glass_name",bean.getGlass_name());
        }
        //问题类型(在线播放、本地视频、游戏、头控选择、遥控器、飞屏、充值购买、观影体验、界面功能、其他)
        if(!TextUtils.isEmpty(p_type)) {
            requestParams.put("question_type", p_type);
        }
        //联系方式（手机号）
        if(!TextUtils.isEmpty(phone_num)) {
            requestParams.put("phone_number", phone_num);
        }
        //用户设备的Android版本号
        requestParams.put("device_version",Build.VERSION.RELEASE);
        getAsyncHttpClient().get(context, ConfigUrl.FEEDBACK_COMMIT, requestParams, new ApiResponseHandler<FeedbackCommitResult>(apiCallBack) {
            @Override
            public FeedbackCommitResult parseResponse(String responseString) {
                FeedbackCommitResult bean = JSON.parseObject(responseString, new TypeReference<FeedbackCommitResult>() {
                });
                return bean;
            }
        });
    }
}
