package com.baofeng.mj.unity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by hanyang on 16/10/10.
 * unity调用科大讯飞语音类
 */

public class UnityIatBusiness {
    private static SpeechRecognizer mIat;
    private static String mEngineType = SpeechConstant.TYPE_CLOUD;
    private static HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private static String TAG = "VOICE";//添加log用于前期调试,正是发版去掉

    /**
     * 科大讯飞初始化设置
     */
    public static void initIat() {
        SpeechUtility.createUtility(BaseApplication.INSTANCE, "appid=" + "552c8333");
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(UnityActivity.INSTANCE, null);
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        UnityActivity.INSTANCE.startBluetooth();
    }

    /**
     * 科大讯飞开始监听
     */
    public static void startIat() {
        if(null != mIat && mIat.isListening()){
            mIat.stopListening();
            mIat.cancel();
            LogHelper.e("infos","=====stop=======");
        }
        mIatResults.clear();
        initIat();
        mIat.startListening(mRecoListener);
        LogHelper.e("infos","=====startIat=======");
    }

    //听写监听器
    private static RecognizerListener mRecoListener = new RecognizerListener() {
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            LogHelper.e("infos","result=="+resolveResult(results));
            Log.e("infos", "result" + results.toString()+"==isLast=="+isLast);
            if(!isLast){
                return;
            }

            if(UnityActivity.INSTANCE != null){
                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                if (iAndroidCallback != null) {//通知Unity
                    iAndroidCallback.sendIatResult(resolveResult(results));
                }
            }
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            //打印错误码描述
            LogHelper.e("infos","onError=="+error.getPlainDescription(true));
            if(UnityActivity.INSTANCE != null){
                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                if (iAndroidCallback != null) {//通知Unity
                    iAndroidCallback.sendIatError(error.getErrorCode());
                }
            }
        }

        //开始录音
        public void onBeginOfSpeech() {
            LogHelper.e("infos","=========onBegin=========");
            if(UnityActivity.INSTANCE != null){
                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                if (iAndroidCallback != null) {//通知Unity
                    iAndroidCallback.sendIatBegin();
                }
            }
        }

        //volume音量值0~30,data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
            Log.i("onVolumeChanged","data=="+new String(data));
            if(UnityActivity.INSTANCE != null){
                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                if (iAndroidCallback != null) {//通知Unity
                    iAndroidCallback.sendIatVolumeAndData(volume,new String(data));//传给u3d用来绘制声音振幅图形
                }
            }
        }

        //结束录音
        public void onEndOfSpeech() {
            LogHelper.e("infos","=========end=========");
            if(UnityActivity.INSTANCE != null){
                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                if (iAndroidCallback != null) {//通知Unity
                    iAndroidCallback.sendIatEnd();
                }
            }
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

    };

    /**
     * 语音转义结果解析
     *
     * @param results
     */
    private static String resolveResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogHelper.e("infos","sn = " + sn + "; text = " + text);
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        return resultBuffer.toString();
    }

    /**
     * 结果解析工具类
     *
     * @param json
     * @return
     */
    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }


}
