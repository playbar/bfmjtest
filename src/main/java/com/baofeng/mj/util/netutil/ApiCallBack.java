package com.baofeng.mj.util.netutil;

/**
 * 网络请求接口回调
 * Created by muyu on 2016/4/1.
 */
public abstract class ApiCallBack<T> {
    public void onSuccess(T result) {
    }

    public void onFailure(Throwable error, String content) {
    }

    public void onFinish() {
    }

    public void onStart() {
    }

    public void onCache(T result) {
    }

    public void onProgress(int bytesWritten, int totalSize) {

    }
}
