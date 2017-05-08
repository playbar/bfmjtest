package com.baofeng.mj.unity;

import android.app.Activity;
import android.text.TextUtils;

import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.VideoApi;
import com.baofeng.mj.util.threadutil.HistoryProxy;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuchuanchi on 2016/5/18.
 * unity历史业务
 */
public class UnityHistoryBusiness {
    /**
     * 历史信息写入文件
     * @param historyJson 历史信息json
     */
    public static void writeToHistory(final String historyJson){
        LogHelper.e("infos","historyJson==="+historyJson);
        HistoryProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(historyJson)){
                    try {
                        JSONObject historyJo = new JSONObject(historyJson);
                        int type = historyJo.getInt("type");
                        LogHelper.e("infos","historyType==="+type);
                        if(HistoryBusiness.historyTypeLocal == type) {//本地历史
                            String fileName = historyJo.getString("videoPlayUrl");
                            HistoryBusiness.writeToHistory(historyJson, fileName, type);
                            LogHelper.e("infos","historyFileName==="+fileName);
                        }else{//在线历史
                            String fileName = historyJo.getString("videoId");
                            HistoryBusiness.writeToHistory(historyJson, fileName, type);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 从文件读历史信息
     * @param fileName 文件名称 在线历史传视频id，本地历史传视频路径
     * @param type 0本地历史，1在线历史
     * @return json
     */
    public static String readFromHistory(final String fileName, final int type){
        String historyJson = HistoryBusiness.readFromHistory(fileName, type);
        if(TextUtils.isEmpty(historyJson)){
            historyJson = "";
        }
        return historyJson;
    }

    /**
     * 从文件读历史信息（本地用）
     * @param filePath 文件路径
     * @return json
     */
    public static String readFromHistoryByPath(final String filePath){
        String historyJson = HistoryBusiness.readFromHistory(filePath, 0);
        if(TextUtils.isEmpty(historyJson)){
            historyJson = "";
        }
        return historyJson;
    }

    /**
     * 查询在线历史记录
     * @param page 第几页
     * @param page_cnt 每页多少条
     */
    public static void queryCinemaHistory(final int page, final int page_cnt){
        LogHelper.e("infos","page=="+page+"==page_cnt=="+page_cnt);
        if(UserSpBusiness.getInstance().isUserLogin()){//已登录
            Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
            curActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new VideoApi().queryCinemaHistory(page, page_cnt, new ApiCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            super.onSuccess(result);
                            String resultJson = null;
                            LogHelper.e("infos","=====result===="+result);
                            if(TextUtils.isEmpty(result)){
                                resultJson = HistoryBusiness.createResultJson("0", "请求失败", "0", new JSONArray());
                                LogHelper.e("infos","=====请求失败====");
                            }else{
                                String totalNum = "0";
                                JSONArray localJson = new JSONArray();
                                try {
                                    JSONObject joResult = new JSONObject(result);
                                    LogHelper.e("infos","status===="+joResult.getString("status"));
                                    if("1".equals(joResult.getString("status"))){
                                        totalNum = joResult.getString("total");
                                        localJson = CreateHistoryUtil.netJsonToLocalJson(joResult.getJSONArray("data"));
                                        LogHelper.e("infos","totalNum===="+totalNum+"==localJson=="+localJson);
                                        HistoryBusiness.saveCinemaHistoryToLocal(localJson);//保存在线历史到本地
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                resultJson = HistoryBusiness.createResultJson("1", "请求成功", totalNum, localJson);
                                LogHelper.e("infos","resultJson===="+resultJson);
                            }

                            if(UnityActivity.INSTANCE != null){
                                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                                if (iAndroidCallback != null) {//通知Unity
                                    iAndroidCallback.sendHistoryJSONObject(resultJson);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            super.onFailure(error, content);
                            LogHelper.e("infos","=====error===="+error+"==content=="+content);
                            String resultJson = HistoryBusiness.createResultJson("0", "请求失败", "0", new JSONArray());
                            if(UnityActivity.INSTANCE != null){
                                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                                if (iAndroidCallback != null) {//通知Unity
                                    iAndroidCallback.sendHistoryJSONObject(resultJson);
                                }
                            }
                        }
                    });
                }
            });
        }else{//未登录
            LogHelper.e("infos","=====未登录====");
            HistoryProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                @Override
                public void run() {
                    String result = HistoryBusiness.readAllFromHistory(page, page_cnt, 0, 1);
                    LogHelper.e("infos","=====未登录===="+result);
                    if(UnityActivity.INSTANCE != null){
                        IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                        if (iAndroidCallback != null) {//通知Unity
                            iAndroidCallback.sendHistoryJSONObject(result);
                        }
                    }
                }
            });
        }
    }
}
