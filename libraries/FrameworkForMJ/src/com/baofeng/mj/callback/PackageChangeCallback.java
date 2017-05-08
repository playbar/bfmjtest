package com.baofeng.mj.callback;

/**
 * Created by muyu on 2016/11/25.
 */
public interface PackageChangeCallback {
    /**
     * 安装的回调方法
     * @param resultCode
     *           1   安装成功
     *           0   参数错误
     *          -1   路径不存在
     *          -2   安装失败
     *          -3   解析安装包时出现问题
     */
    void onPackageInstalled(int resultCode);
}

