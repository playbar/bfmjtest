package com.baofeng.mj.util.netutil;

import android.os.Message;

import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.net.TextHttpResponseHandler;
import com.baofeng.mj.utils.LogMessage;

import org.apache.http.Header;

/**
 * 异步联网请求解析帮助类
 * Created by muyu on 2016/4/1.
 */
public abstract class ApiResponseHandler<T> extends TextHttpResponseHandler {
    private static final String TAG = "ApiResponseHandler";
    protected ApiCallBack<T> mApiCallBack;
    private int PARSED_MESSAGE = 10;
    private int PARSED_CACHE_MESSAGE = 11;

    public ApiResponseHandler(ApiCallBack<T> apiCallBack) {
        this.mApiCallBack = apiCallBack;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        if (mApiCallBack != null) {
            if (message.what == PARSED_MESSAGE) {
                T obj = (T) message.obj;
                if (obj instanceof ResponseBaseBean) {
                    ResponseBaseBean bean = (ResponseBaseBean) obj;
                    if (bean.getStatus() == ResponseBaseBean.TOKEN_INVALID) {
//                        BaseActivity.tokenEventBus.post(new TokenInvalidEvent(bean.getMessage()));
                    }
                }

                try {
                    mApiCallBack.onSuccess(obj);
                } catch (Exception e) {
                    mApiCallBack.onFailure(new NullPointerException("出错啦，请稍后重试"), "出错啦，请稍后重试");
                    e.printStackTrace();
                }

            } else if (message.what == PARSED_CACHE_MESSAGE) {
                mApiCallBack.onCache((T) message.obj);
            }
        }
    }

    @Override
    protected void sendMessage(Message msg) {
        int what = msg.what;
        //成功时和读取缓存时在后台线程调用，以便于后面的解析在后台线程进行
        if (what == SUCCESS_MESSAGE || what == CACHE_MESSAGE) {
            handleMessage(msg);
            return;
        }
        super.sendMessage(msg);
    }

    @Override
    public void onStart() {
        if (mApiCallBack != null) {
            mApiCallBack.onStart();
        }
//        if (NetworkUtils.getNetworkState(BaseApplication.getInstance()) == NetworkUtils.TYPE_NO) {
//            ToastUtil.show(BaseApplication.getInstance(), R.string.network_error);
//        }
    }

    @Override
    public void onFinish() {
        if (mApiCallBack != null) {
            mApiCallBack.onFinish();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString,
                          Throwable throwable) {
        if (mApiCallBack != null) {
            mApiCallBack.onFailure(throwable, responseString);
        }
    }

    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        if (mApiCallBack != null) {
            mApiCallBack.onProgress(bytesWritten, totalSize);
        }
    }

    @Override
    // 后台线程解析
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        LogMessage.v(TAG, responseString + "");
        T t = parseResponse(responseString);
        sendMessage(obtainMessage(PARSED_MESSAGE, t));

    }

    @Override
    public void onCache(String cacheString) {
        try {
            T t = parseResponse(cacheString);
            sendMessage(obtainMessage(PARSED_CACHE_MESSAGE, t));
        } catch (Throwable t) {
            LogMessage.i("cache-string", cacheString);
            t.printStackTrace();
        }
    }

    @Override
    public void onCancel() {
        sendFinishMessage();
    }

    public abstract T parseResponse(String responseString);
}