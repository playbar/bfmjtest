package com.baofeng.mj.util.publicutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * 微信分享
 */
public class WeixinShare {
    private Context mContext;
    private IWXAPI wxApi;
    private int flag = 0; //0 分享到微信好友  1 分享到微信朋友圈
    private String mWebUrl = ""; //内容链接
    private String mTitle = "给你推荐个好玩的玩具叫“暴风魔镜”"; //标题
    private String mDescription = "快来跟我一起体验神奇的虚拟世界吧~"; //介绍
    private Bitmap mBitmap = null;

    /**
     * 实例化
     *
     * @return void
     * description:实例化
     * @param context Context
     */
    public WeixinShare(Context context) {
        mContext = context;

        regToWx();
    }

    /**
     * 注册到微信
     *
     * @return void
     * description:注册到微信
     */
    private void regToWx() {
        wxApi = WXAPIFactory.createWXAPI(mContext, "wx59cc7232556dc6d1", true);
        wxApi.registerApp("wx59cc7232556dc6d1");
    }

    /**
     * 设置链接
     *
     * @return void
     * description:设置消息的网页链接
     */
    public void setWebUrl(String url) {
        mWebUrl = url;
    }

    /**
     * 设置标题
     *
     * @return void
     * description:设置标题
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * 设置介绍内容
     *
     * @return void
     * description:设置介绍内容
     */
    public void setDescription(String description) {
        mDescription = description;
    }

    /**
     * 设置图片
     *
     * @return void
     * description:设置介绍内容
     */
    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    /**
     * 检测微信是否安装
     *
     * @return boolean
     * description:检测微信是否安装
     */
    public boolean checkInstall() {
        if (!wxApi.isWXAppInstalled() || !wxApi.isWXAppSupportAPI()) {
            return false;
        }

        return true;
    }

    /**
     * 分享
     *
     * @return void
     * description:执行分享
     */
    public void share() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mWebUrl;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;

        //图片资源
        if (mBitmap != null) {
            msg.thumbData = bmpToByteArray(mBitmap, true);
            mBitmap = null;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    /**
     * @param bmp
     * @param needRecycle
     * @return
     * @date 2015-05-15 10:40
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }


    /**
     * 创建事件
     *
     * @return String
     * description:创建事件
     * @param type 类型
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * @return 没有安装时返回false
     * description:启动微信app
     */
    public boolean openWeiXinApp() {
        if (!wxApi.isWXAppInstalled()) {
            return false;
        }
        wxApi.openWXApp();
        return true;
    }
}
