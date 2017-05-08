package com.baofeng.mj.util.viewutil;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** 加载多Assets下的配置表
 * Created by muyu on 2016/6/22.
 */
public class LoadAssetsConfig {

    //加载服务器地址线上
    public static String loadServerUrl(Context context){
        String serverstr = "";
        try {
            serverstr = getStringFromConfigAssets(context, "url_config.json");
        } catch (Exception e) {
            Log.e("LoadAssetsConfig", "url_config.json文件出错了", e);
        }
        return serverstr;
    }
    //加载LocalizationStrings.json文件
    public static String loadLanguageConfig(Context context){
        String languageconfigstr = "";
        try {
            languageconfigstr = getStringFromConfigAssets(context, "LocalizationStrings.json");
        } catch (Exception e) {
            Log.e("LoadAssetsConfig", "加载LocalizationStrings.json文件出错了", e);
        }
        return languageconfigstr;
    }

    //加载glasses.json文件
    public static String loadGlassesConfig(Context context){
        String glassesConfigstr = "";
        try {
            glassesConfigstr = getStringFromConfigAssets(context, "glasses.json");
        } catch (Exception e) {
            Log.e("LoadAssetsConfig", "加载glasses.json文件出错了", e);
        }
        return glassesConfigstr;
    }

    public static String getStringFromConfigAssets(Context context, String path) throws Exception{
        InputStream is = context.getResources().getAssets().open(path);
        String str = null;
        try {
            str = convertStreamToString(is);
        } catch (IOException e) {
            throw e;
        }
        return str;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}
