package com.baofeng.mj.business.publicbusiness;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ApiResponseHandler;
import com.baofeng.mj.util.netutil.BaseApi;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.loopj.android.http.RequestParams;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyang on 2016/5/16.
 * 黑白名单
 */
public class WhiteCheckBusiness extends BaseApi {
    private WhiteCheckBusiness instance;

    public WhiteCheckBusiness() {
    }

    public WhiteCheckBusiness getInstance() {
        if (instance == null) {
            instance = new WhiteCheckBusiness();
        }
        return instance;
    }

    /**
     * 接口请求
     *
     * @param context
     */
    public void getData(final Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("isChecked", Context.MODE_PRIVATE);
//        if (sharedPreferences.getBoolean("isChecked", false)) {
//            return;
//        }
//        int result = SettingSpBusiness.getInstance().getHigh();
//        if (result == 2) { //如果通过白名单，则不用反复调用接口
//            return;
//        }
//        //白名单高清地址
        String url = ConfigUrl.getCheckUrl(getServiceOperator(context), getMobileBrand(), getMobileModel(),
                getCpuInsCode(), getCpuHardCode(), String.valueOf(gettime()), getSign(context));
        String phoinfo = getServiceOperator(context) + "|" + getMobileBrand() + "|" + getMobileModel() +
                "|" + getCpuInsCode() + "|" + getCpuHardCode();
        SettingSpBusiness.getInstance().setPhinfo(phoinfo);
        LogHelper.i("infos","url=="+url+"==phoneinfo=="+phoinfo);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        getCheckResult(context, url, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
//                setIcChecked(context);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.has("code") && json.getInt("code") == 0) {//数据请求成功
                        LogHelper.i("infos","====code==0");
                        if (json.has("data")) {
                            localSaveHdTestResult(context, json.getJSONObject("data"));
                        } else {
                            LogHelper.i("infos","====no has data====");
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                return;
            }
        });
    }

    /**
     * 数据接口
     *
     * @param context
     * @param url
     * @param apiCallBack
     */
    private void getCheckResult(Context context, String url, ApiCallBack<String> apiCallBack) {
        getAsyncHttpClient().get(context, url, null, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    public void updateWhiteCheckness(final Context context) {
        getUpdateResult(context, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
//                setIcChecked(context);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.has("code") && json.getInt("code") == 0) {//数据请求成功,数据为0，认为通过高清测试
                        SettingSpBusiness.getInstance().setHigh(2);
                    } else {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                return;
            }
        });
    }

    private void getUpdateResult(Context context, ApiCallBack<String> apiCallBack) {
        RequestParams params = new RequestParams();
        params.put("operate_type", "add");  //string （add/delete） 	操作类型
        params.put("manage_id", 1); //管理名单id
        params.put("service_operator", getServiceOperator(context)); //运营商
        params.put("mobile_brand", getMobileBrand()); //手机品牌
        params.put("mobile_model", getMobileModel());//手机型号
        params.put("cpu_instruct_model", getCpuInsCode()); //cpu指令型号
        params.put("cpu_hardware_model", getCpuHardCode());//cpu硬件型号

        getAsyncHttpClient().get(context, ConfigUrl.WHITE_CHECK_UPDATE_URL, params, false, "", new ApiResponseHandler<String>(apiCallBack) {
            @Override
            public String parseResponse(String responseString) {
                return responseString;
            }
        });
    }

    /**
     * 保存是否访问过接口
     *
     * @param context
     */
    public static void setIcChecked(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("isChecked", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isChecked", true);
        editor.commit();
    }

    /**
     * 保存数据
     *
     * @param context
     */
    public static void localSaveHdTestResult(Context context, JSONObject jsonObject) {
        LogHelper.i("infos","====jsonObject===="+jsonObject);
        if (null != jsonObject) {
            try {
                if (jsonObject.has("high")) {
                    LogHelper.i("infos","====high===="+jsonObject.getString("high"));
                    SettingSpBusiness.getInstance().setHigh(Integer.valueOf(jsonObject.getString("high")));
                }
                if (jsonObject.has("hook")) {
                    int hook = Integer.valueOf(jsonObject.getString("hook"));
                    if (hook == 2) {
                        SettingSpBusiness.getInstance().setBgSwitch(1);
                        SettingSpBusiness.getInstance().setAnti_aliasing(1);
                        SettingSpBusiness.getInstance().setSur_Switch(1);
                        SettingSpBusiness.getInstance().setTrans_Ani_Switch(1);
                        SettingSpBusiness.getInstance().setTrans_Switch(1);
                        SettingSpBusiness.getInstance().setMask(1);
                    }
                    SettingSpBusiness.getInstance().setHook(Integer.valueOf(jsonObject.getString("hook")));
                }
                if (jsonObject.has("otg")) {
                    SettingSpBusiness.getInstance().setOtg(Integer.valueOf(jsonObject.getString("otg")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    /**
     * 获取手机运营商信息
     *
     * @param context
     * @return
     */
    public static String getServiceOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operatorString = telephonyManager.getSimOperator();
        if (operatorString == null) {
            return "未知";
        }
        if (operatorString.equals("46000") || operatorString.equals("46002")) {
            //中国移动
            return "移动";
        } else if (operatorString.equals("46001")) {
            //中国联通
            return "联通";
        } else if (operatorString.equals("46003")) {
            //中国电信
            return "电信";
        }
        //error
        return "未知";
    }

    /**
     * 获取手机品牌\
     *
     * @return
     */
    public static String getMobileBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getMobileModel() {
        return Build.MODEL;
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static long gettime() {
        return (long) (System.currentTimeMillis() / 1000);
    }


    /**
     * 获取手机cpu信息
     *
     * @return
     */
    public static List<String> getCpuInfo() {
        String str = null;
        StringBuilder stringBuilder = new StringBuilder();
        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;
        ArrayList<String> cpuInfo = new ArrayList<String>();
        try {
            localFileReader = new FileReader("/proc/cpuinfo");
            if (localFileReader != null) {
                try {
                    localBufferedReader = new BufferedReader(localFileReader, 1024);
                    String line;
                    while ((line = localBufferedReader.readLine()) != null) {
                        cpuInfo.add(line);
                    }
                    str = stringBuilder.toString();
                    localBufferedReader.close();
                    localFileReader.close();
                } catch (IOException localIOException) {
                    System.out.print("Could not read from file /proc/cpuinfo" + localIOException);
                }
            }
            if (str == null) {
                return null;
            }
        } catch (FileNotFoundException localFileNotFoundException) {
            System.out.print("Could not open file /proc/cpuinfo" + localFileNotFoundException);
        }
        return cpuInfo;
    }

    /**
     * 获取手机cpu指令型号
     *
     * @return
     */
    public static String getCpuInsCode() {
        List<String> cupInfo = getCpuInfo();
        if (cupInfo != null) {
            if (TextUtils.isEmpty(cupInfo.get(0))) {
                return "";
            } else {
                String str = cupInfo.get(0);
                int i = str.indexOf(":") + 1;
                return str.substring(i).trim();
            }

        } else {
            return "";
        }
    }

    /**
     * 获取手机cpu硬件型号
     *
     * @return
     */
    public static String getCpuHardCode() {
        List<String> cupInfo = getCpuInfo();
        if (cupInfo != null) {
            String hardCode = "";
            int size = cupInfo.size();
            for (int i = 0; i < size; i++) {
                if (i >= size) {
                    break;
                }
                if (TextUtils.isEmpty(cupInfo.get(i))) {
                    continue;
                } else {
                    int j = cupInfo.get(i).indexOf("Hardware");
                    if (j == -1) {
                        continue;
                    } else {
                        int n = cupInfo.get(i).substring(j).trim().indexOf(":");
                        if (n == -1) {
                            continue;
                        } else {
                            return cupInfo.get(i).substring(j).substring(n + 1).trim();
                        }
                    }
                }
            }
            return hardCode;
        } else {
            return "";
        }
    }

    /**
     * md5加密获取sign
     *
     * @return
     */
    public static String getSign(Context context) {
        String key = "744f95618927de20e56b60e41b0a44ef";
        return MD5Util.MD5(getServiceOperator(context) + getMobileBrand() + getMobileModel() + getCpuInsCode() +
                getCpuHardCode() + String.valueOf(gettime()) + key);
    }
}
