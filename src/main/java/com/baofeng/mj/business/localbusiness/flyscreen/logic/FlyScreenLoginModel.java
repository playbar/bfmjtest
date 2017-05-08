package com.baofeng.mj.business.localbusiness.flyscreen.logic;

import android.content.Context;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.baofeng.mj.bean.BaseMessage;
import com.baofeng.mj.bean.Login;
import com.baofeng.mj.business.localbusiness.flyscreen.util.FlyScreenUtil;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * ClassName: FlyScreenLoginModel
 *
 * @author zhangxin
 * @date: 2015-1-19 下午4:46:17
 * description:
 */
public class FlyScreenLoginModel {

    private static FlyScreenLoginModel instance;
    private Context mContext;
    private FlyScreenTcpSocket tcpClient;
    private Login.LoginResultErrorcode loginErrorCode;
    private Login.ResponseLoginMethod responseLoginMethod;

    private static String sessionId = "";
    private static String serverUri = "";
    private static int serverPort = 0;
    private static boolean bContentEmpty = false;
    //private static HashMap<String, Boolean> hslogin = new HashMap<String, Boolean>();

    private FlyScreenLoginModel() {
        tcpClient = FlyScreenTcpSocket.getSingleInstance();
    }

    /**
     * 单例
     */
    public static FlyScreenLoginModel getSingleInstance(Context pContext) {
        if (instance == null) {
            instance = new FlyScreenLoginModel();
        }

        if (instance.mContext == null && pContext != null) {
            instance.mContext = pContext;
        }

        return instance;
    }

    public static FlyScreenLoginModel getFlyScreenLoginModel() {
        return instance;
    }

    /**
     * 请求可登录的方式 总包+登录总包
     */
    public void requestLoginMethod() {
        byte[] bloginBasic = createLoginBasicMassage(
                Login.LoginMessageType.LoginMessageType_RequestLoginMethod,
                null);
        byte[] bbasic = FlyScreenBaseModel.createBasicMassage(
                BaseMessage.MessageType.MessageType_Login, bloginBasic);
        byte[] bdst = new byte[8 + bbasic.length];
        byte[] btag = FlyScreenUtil.getItagAppToPc_Message();
        byte[] blen = FlyScreenUtil.intToByteArray(bbasic.length);

        System.arraycopy(btag, 0, bdst, 0, 4);
        System.arraycopy(blen, 0, bdst, 4, 4);
        System.arraycopy(bbasic, 0, bdst, 8, bbasic.length);

        tcpClient.send(bdst);
    }

    /**
     * 请求登录 login 总包+登录总包+登录数据包
     * <p/>
     * 参数： LoginMethod: 是否需要密码登录 md5sum ：密码md5值
     */
    public void requestLogin(Login.LoginMethod loginMethod, String pwd) {
        byte[] blogindata = null;
        if (loginMethod == Login.LoginMethod.LoginMethod_None) {
            blogindata = createLoginData(loginMethod, "");
        } else if (loginMethod == Login.LoginMethod.LoginMethod_Md5) {

            String src = responseLoginMethod.getRandomKey() + pwd;
            String md5sum = MD5Util.MD5(src);
            blogindata = createLoginData(loginMethod, md5sum);
        }

        byte[] bloginBasic = createLoginBasicMassage(
                Login.LoginMessageType.LoginMessageType_RequestLogin,
                blogindata);
        byte[] bbasic = FlyScreenBaseModel.createBasicMassage(
                BaseMessage.MessageType.MessageType_Login, bloginBasic);
        byte[] btag = FlyScreenUtil.getItagAppToPc_Message();
        byte[] blen = FlyScreenUtil.intToByteArray(bbasic.length);
        byte[] bdst = new byte[8 + bbasic.length];

        System.arraycopy(btag, 0, bdst, 0, 4);
        System.arraycopy(blen, 0, bdst, 4, 4);
        System.arraycopy(bbasic, 0, bdst, 8, bbasic.length);

        tcpClient.send(bdst);
    }

    /**
     * 请求退出登录： 总包+登录总包+登出数据包
     */
    public void requestLogout() {
        byte[] blogindata = createLogoutData();
        byte[] bloginBasic = createLoginBasicMassage(
                Login.LoginMessageType.LoginMessageType_RequestLogout,
                blogindata);
        byte[] bbasic = FlyScreenBaseModel.createBasicMassage(
                BaseMessage.MessageType.MessageType_Login, bloginBasic);
        byte[] btag = FlyScreenUtil.getItagAppToPc_Message();
        byte[] blen = FlyScreenUtil.intToByteArray(bbasic.length);
        byte[] bdst = new byte[8 + bbasic.length];

        System.arraycopy(btag, 0, bdst, 0, 4);
        System.arraycopy(blen, 0, bdst, 4, 4);
        System.arraycopy(bbasic, 0, bdst, 8, bbasic.length);
        tcpClient.send(bdst);
    }

    // 生成请求退出数据
    private byte[] createLogoutData() {
        Login.RequestLogout.Builder builder = Login.RequestLogout.newBuilder();
        sessionId = SettingSpBusiness.getInstance().getFlyScreenSessionId();
        builder.setSessionID(sessionId);
        Login.RequestLogout logout = builder.build();
        byte[] bmsg = logout.toByteArray();
        return bmsg;
    }

    // 生成请求登录数据
    private byte[] createLoginData(Login.LoginMethod loginMethod, String md5sum) {
        Login.RequestLogin.Builder builder = Login.RequestLogin.newBuilder();
        builder.setMethod(loginMethod);
        builder.setMd5Sum(md5sum);
        HashMap<String, String> hs = Common.getHardwareInfo();
        builder.setDeviceName(hs.get("PRODUCT"));
        builder.setMajorClientVer(ApkUtil.getVersionNameSuffix());
        builder.setMinorClientVer("1111");
//        builder.setDeviceID( ( (TelephonyManager)(mContext.getSystemService(Context.TELEPHONY_SERVICE))).getDeviceId() );
        builder.setDeviceID("123");
        builder.setDeviceType(Login.DeviceType.DeviceType_mojing);

        Login.RequestLogin login = builder.build();
        byte[] bmsg = login.toByteArray();
        return bmsg;
    }

    // 生成登录总包字节数组
    private byte[] createLoginBasicMassage(Login.LoginMessageType msgType,
                                           byte[] detailMsg) {
        Login.BasicLoginMessage.Builder builder = Login.BasicLoginMessage
                .newBuilder();
        builder.setMt(msgType);
        if (detailMsg != null) {
            String s = "";
            try {
                s = new String(detailMsg, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            builder.setDetailMsg(s);
        }

        Login.BasicLoginMessage basicMsg = builder.build();
        byte[] bmsg = basicMsg.toByteArray();
        return bmsg;
    }

    // -------------处理接收部分---------------//
    // 处理接收的登录模块逻辑
    public void dealReceiveLoginModelData(ByteString s) {
        byte[] bbasicLogin;
        try {
            //bbasicLogin = s.getBytes("UTF-8");
            Login.BasicLoginMessage basicLoginMessage = Login.BasicLoginMessage
                    .parseFrom(s);
            Login.LoginMessageType lmt = basicLoginMessage.getMt();
            ByteString basicLoginDetaiMsg = basicLoginMessage.getDetailMsgBytes();
            String basicLoginDetaiMsg2 = basicLoginMessage.getDetailMsg();
            if (lmt == Login.LoginMessageType.LoginMessageType_ResponseLoginMethod) {
                dealReceiveLoginMethodData(basicLoginDetaiMsg2);
            } else if (lmt == Login.LoginMessageType.LoginMessageType_ResponseLogin) {
                dealReceiveLoginData(basicLoginDetaiMsg);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 处理接收的登录方式逻辑
    private void dealReceiveLoginMethodData(String basicLoginDetaiMsg) {

        byte[] bLoginMethodResponse;
        try {
            bLoginMethodResponse = basicLoginDetaiMsg.getBytes("UTF-8");
            responseLoginMethod = Login.ResponseLoginMethod
                    .parseFrom(bLoginMethodResponse);
            FlyScreenTcpSocket.HandlerCallBack devHandler = tcpClient.getDevListHandler();
            Message msg = Message.obtain();
            msg.what = 2;
//			devHandler.sendMessage(msg);
            devHandler.handlerMessage(msg);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 处理接收的登录逻辑
    private void dealReceiveLoginData(ByteString basicLoginDetaiMsg) {

        byte[] bLoginResponse;
        try {
            //bLoginResponse = basicLoginDetaiMsg.getBytes("UTF-8");
            Login.ResponseLogin responseLogin = Login.ResponseLogin
                    .parseFrom(basicLoginDetaiMsg);

            loginErrorCode = responseLogin.getErrorcode();
            if (loginErrorCode == Login.LoginResultErrorcode.LoginResultErrorcode_OK) {
                sessionId = responseLogin.getSessionID();
                serverPort = responseLogin.getServerPort();
                bContentEmpty = responseLogin.getBContentEmpty();
                SettingSpBusiness.getInstance().setFlyScreenSessionId(sessionId);
            }

            FlyScreenTcpSocket.HandlerCallBack devHandler = tcpClient.getDevListHandler();
            Message msg = Message.obtain();
            msg.what = 3;
            devHandler.handlerMessage(msg);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Login.LoginResultErrorcode getLoginErrorCode() {
        return loginErrorCode;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public Login.ResponseLoginMethod getResponseLoginMethod() {
        return responseLoginMethod;
    }

    public static String getServerUri() {
        return serverUri;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static boolean isbContentEmpty() {
        return bContentEmpty;
    }

    public static void loginout() {
        if (instance != null) {
            instance.requestLogout();
        }
    }
}
