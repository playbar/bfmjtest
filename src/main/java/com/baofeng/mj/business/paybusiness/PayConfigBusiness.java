package com.baofeng.mj.business.paybusiness;

import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.PaySuccessInfo;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuchuanchi on 2016/5/21.
 * 创建支付(实体类)工具类
 */
public class PayConfigBusiness {
    /**
     * 生成支付链接
     */
    public static String createPayUrl(PanoramaVideoBean panoramaVideoBean){
        int resType = panoramaVideoBean.getType();
        String resId = panoramaVideoBean.getRes_id();
        String resTitle = panoramaVideoBean.getTitle();
        int payment_type = panoramaVideoBean.getPayment_type();
        float payment_count = panoramaVideoBean.getPayment_count();
        return createPayUrl(resType, resId, resTitle, payment_type, payment_count);
    }

    /**
     * 生成支付链接
     */
    public static String createPayUrl(int resType, String resId, String resTitle, int payment_type, float payment_count) {
        String uid = UserSpBusiness.getInstance().getUid();
        String mobile = UserSpBusiness.getInstance().getMobile();
        String version = ApkUtil.getVersionName();
        int time = (int) (System.currentTimeMillis() / 1000);
        String appid = ConfigConstant.PAY_APPID;
        String platFormId = ConfigConstant.PAY_PLATFORM;
        String payChannel = ConfigConstant.PAY_CHANNEL;
        if (ResTypeUtil.isGameOrApp(resType)) {//游戏或者应用
            payChannel = "15";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("uid=").append(uid);
        if(1 == payment_type){//魔豆
            sb.append("&modou=").append(payment_count);
        }else{//魔币
            sb.append("&mobi=").append(payment_count);
        }
        sb.append("&mobile=").append(mobile);
        sb.append("&version=").append(version);
        sb.append("&remark=").append(resTitle);
        sb.append("&platform=").append(platFormId);
        sb.append("&channel=").append(payChannel);
        sb.append("&appid=").append(appid);
        sb.append("&release_channel=").append(ChannelUtil.getChannelCode("CHANNEL"));
        sb.append("&time=").append(time);
        sb.append("&res_id=").append(resId);
        sb.append("&res_type=").append(resType);
        sb.append("&res_title=").append(resTitle);
//        sb.append("&business_id=").append(businessId);
//        sb.append("&business_name=").append(businessName);

        StringBuffer sbCode = new StringBuffer();
        sbCode.append(time).append(uid).append(payment_count).append(mobile).append(version);
        sbCode.append(platFormId).append(appid).append(ConfigConstant.MJ_KEY_LIHAO);
        sb.append("&sign=").append(MD5Util.MD5(sbCode.toString()));

        if(1 == payment_type) {//魔豆
            return ConfigUrl.getModouPayUrl() + "&" + sb.toString();
        }else{//魔币
            return ConfigUrl.getMobiPayUrl() + "&" + sb.toString();
        }
    }

    /**
     * @author liuchuanchi
     * @description: 创建支付成功对象
     * @param joData
     * @return
     */
    public static PaySuccessInfo createPaySuccessInfo(JSONObject joData){
        PaySuccessInfo info = new PaySuccessInfo();
        try {
            if(joData != null){
                info.setBind_xcode(joData.getString("bind_xcode"));
                info.setCreate_time(joData.getString("create_time"));
                info.setGift_modou(joData.getString("gift_modou"));
                info.setId(joData.getString("id"));
                info.setIs_freeze(joData.getString("is_freeze"));
                info.setMobile(joData.getString("mobile"));
                info.setNickname(joData.getString("nickname"));
                info.setRecharge_modou(joData.getString("recharge_modou"));
                info.setRegtype(joData.getString("regtype"));
                info.setUid(joData.getString("uid"));
                info.setUname(joData.getString("uname"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }
}
