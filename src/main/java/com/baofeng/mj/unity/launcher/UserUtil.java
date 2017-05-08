package com.baofeng.mj.unity.launcher;

import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;


/**
 * Created by qiguolong on 2016/5/5.
 * 现在非共享 如果以后改变则 使用provider See {@link }
 * 大致流程：
 * 绑定手机号时 会生成一个生成一个（序列号+时间戳）的唯一码储存并 交给服务端作为判断依据。
 * 服务端发动短信给该手机号 ，点击链接则绑定成功，客户端这边开启60秒计时，每三秒根据返回的token 请求服务端是否绑定成功。
 */
public class UserUtil {
    /**
     * 存用户数据 以电话唯一
     * 现在不做成共享直接存取文件
     *
     * @param userInfo
     */
    public static void saveUser(UserInfo userInfo) {
        FileTools.writeSerFile(userInfo, FileConfig.getInstance().getUserCacheePath() + File
                .separator + userInfo.getPhoneNumber());

    }

    /**
     * 取用户数据 以电话唯一 如果没有则以生成一个（序列号+时间戳）的标识码 并保存
     * 现在不做成共享直接存取文件
     *
     * @param phone 电话
     */
    public static UserInfo readUser(String phone) {
        if (TextUtils.isEmpty(phone))
            return null;
        Serializable serializable = FileTools.readSerFile(FileConfig.getInstance()
                .getUserCacheePath() + File.separator + phone);
        if (serializable == null) {
            UserInfo userInfo = new UserInfo(phone, Build.SERIAL + System.currentTimeMillis());
            saveUser(userInfo);
            return userInfo;
        }
        return (UserInfo) serializable;

    }

    /**
     * 存储当前手机
     *
     * @param phone
     */
    public static void saveUserPhone(String phone) {
        SharedPreferencesUtil.getInstance().setString(GlobShareprefenceKey.USERPHONE, phone);

    }

    /**
     * 获取当前手机
     *
     * @return
     */
    public static String getCurrentUserphone() {
        return SharedPreferencesUtil.getInstance().getString(GlobShareprefenceKey.USERPHONE, "");
    }

    /**
     * 获取当前用户信息 用来传给魔镜app
     *
     * @return
     */
    public static UserInfo getCurrentUserInfo() {
        return readUser(getCurrentUserphone());
    }

    /**
     * 获取当前用户信息 用来传给其他app
     *
     * @return json 格式
     */
    public static String getCurrentUserJson() {
        UserInfo userInfo = readUser(getCurrentUserphone());
        if (userInfo == null)
            return "";
        return GsonUtil.toJsonData(userInfo);
    }
}
