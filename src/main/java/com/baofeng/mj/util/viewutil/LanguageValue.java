package com.baofeng.mj.util.viewutil;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.business.publicbusiness.BaseApplication;

import java.util.HashMap;

/**获取多语言Key对应Value,
 * Created by muyu on 2016/6/22.
 */
public class LanguageValue {

    private String defaultLang = "zh_CN";
    private static LanguageValue instance;
    private HashMap <String, HashMap<String, String>> LanguageMap;
    private HashMap <String, HashMap<String, String>> serverUrlMap;
    public static LanguageValue getInstance(){
        if(instance == null){
            instance = new LanguageValue();
        }
        return instance;
    }

    public void initLanguageMap(Context context){ //Context为BaseApplication.INSTANCE
        String languageConfig = LoadAssetsConfig.loadLanguageConfig(context);
        JSONObject jsonObject = JSON.parseObject(languageConfig);
        JSONObject rootStr = (JSONObject) jsonObject.get("root");
        LanguageMap = JSON.parseObject(rootStr.toString(), new TypeReference<HashMap<String, HashMap<String, String>>>() {
        });
        BaseApplication.INSTANCE.setLanguageMap(LanguageMap);
    }

    public String getValue(Context context, String key){
        this.LanguageMap = BaseApplication.INSTANCE.getLanguageMap();
        if(LanguageMap == null){
            initLanguageMap(BaseApplication.INSTANCE);
        }

        if(LanguageMap != null){
            if(LanguageMap.containsKey(defaultLang) && LanguageMap.get(defaultLang).containsKey(key))
            {
                return LanguageMap.get(defaultLang).get(key);
            }
        }
        return "@_" + key;
    }

    //初始化server url
    public void initServerUrl(Context context){
        String languageConfig = LoadAssetsConfig.loadServerUrl(context);
        serverUrlMap = JSON.parseObject(languageConfig, new TypeReference<HashMap<String, HashMap<String, String>>>() {
        });
        BaseApplication.INSTANCE.setServerUrlMap(serverUrlMap);
    }

    public String getServerValue(Context context, String key){
        this.serverUrlMap = BaseApplication.INSTANCE.getServerUrlMap();
        if(serverUrlMap == null){
            initServerUrl(BaseApplication.INSTANCE);
        }
        if(serverUrlMap != null){
            return serverUrlMap.get("vertical").get(key);
        }
        return "@_" + key;
    }

    //返回横屏的Url
    public String getServerhorizonValue(Context context){
        this.serverUrlMap = BaseApplication.INSTANCE.getServerUrlMap();

        if(serverUrlMap == null){
            initServerUrl(context);
        }

        if(serverUrlMap != null){
            JSONObject jsonObject = (JSONObject) JSON.toJSON(serverUrlMap.get("horizon"));
            return jsonObject.toString();
        }
        return "";
    }
}
