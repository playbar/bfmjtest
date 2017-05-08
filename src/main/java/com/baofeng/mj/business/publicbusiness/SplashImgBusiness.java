package com.baofeng.mj.business.publicbusiness;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.MainAPI;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.ImageUtil;
import com.baofeng.mj.util.publicutil.MJOkHttpUtil;
import com.baofeng.mojing.sdk.login.utils.OkHttpUtil;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by liuchuanchi on 2016/7/4.
 * splash图片业务
 */
public class SplashImgBusiness {
    private static String splashImgFolder;
    private static String splashInfoFolder;

    /**
     * 获取splash图片文件夹
     */
    public static String getSplashImgFolder() {
        if (TextUtils.isEmpty(splashImgFolder)) {
            splashImgFolder = FileStorageUtil.getExternalMojingCacheDir() + "splash/splashImg";
        }
        FileStorageUtil.mkdir(splashImgFolder);
        return splashImgFolder;
    }

    /**
     * 获取splash信息文件夹
     */
    public static String getSplashInfoFolder() {
        if (TextUtils.isEmpty(splashInfoFolder)) {
            splashInfoFolder = FileStorageUtil.getExternalMojingCacheDir() + "splash/splashInfo";
        }
        FileStorageUtil.mkdir(splashInfoFolder);
        return splashInfoFolder;
    }

    /**
     * 获取splash图片
     */
    public static File getSplashImg(String imageUrl) {
        return new File(getSplashImgFolder(), imageUrl.hashCode() + ".png");
    }

    /**
     * 获取splash信息
     */
    public static File getSplashInfo() {
        return new File(getSplashInfoFolder(), ApkUtil.getVersionNameSuffix() + ".info");
    }

    /**
     * 创建splash信息
     */
    public static void createSplashInfo() {
        new MainAPI().getSplashInfo(new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject joResult = new JSONObject(result);
                    if (joResult.getBoolean("status")) { // 请求成功
                        FileCommonUtil.writeFileString(result, getSplashInfo());//splash信息存入本地
                        JSONArray jaResult = joResult.getJSONArray("data");
                        if (jaResult != null && jaResult.length() > 0) {
                            for (int i = 0; i < jaResult.length(); i++) {//循环
                                JSONArray imgInfos = jaResult.getJSONObject(i).getJSONArray("img_info");
                                String imageUrl = ((JSONObject) imgInfos.get(0)).getString("download_url");//图片url
                                final File file = getSplashImg(imageUrl);//根据图片url获取本地图片
                                if (file.exists()) {//本地图片已经存在
                                    continue;//进入下一次循环
                                }
                               //下载图片到本地 修改开屏图显示锯齿bug  add by whf 20170314
                                MJOkHttpUtil.getAsynloadFile(imageUrl,file.getName(),file.getParent());

                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
