package com.baofeng.mj.util.systemutil;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by liuchuanchi on 2016/6/13.
 * 语言工具类
 */
public class LanguageUtil {
    private static HashMap<String,Boolean> languageMap;//语言map

    /**
     * 获取系统语言
     */
    public static String getLanguage(){
        if(languageMap == null){
            initLanguageMap();
        }
        String language = Locale.getDefault().getLanguage();
        if(languageMap.containsKey(language)){
            return language;
        }
        return "zh";//返回默认值
    }

    private static void initLanguageMap(){
        languageMap = new HashMap<String,Boolean>();
        languageMap.put("zh", true);//中文
        languageMap.put("en", true);//英文
    }
}
