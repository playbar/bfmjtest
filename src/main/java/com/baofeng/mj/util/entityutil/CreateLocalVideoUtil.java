package com.baofeng.mj.util.entityutil;

import com.baofeng.mj.bean.LocalVideoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by liuchuanchi on 2016/5/21.
 * 创建本地视频(实体类)工具类
 */
public class CreateLocalVideoUtil {
    /**
     * 创建本地视频JSONArray
     */
    public static JSONArray createLocalVideoJSONArray(TreeMap localVideoMap){
        JSONArray localVideoJSONArray = new JSONArray();
        Iterator iter = localVideoMap.values().iterator();
        while (iter.hasNext()) {
            LocalVideoBean localVideoBean = (LocalVideoBean) iter.next();
            localVideoJSONArray.put(createLocalVideoJSONObject(localVideoBean));
        }
        return localVideoJSONArray;
    }

    /**
     * 创建本地视频JSONObject
     */
    public static JSONObject createLocalVideoJSONObject(LocalVideoBean localVideoBean){
        JSONObject localVideoJson = new JSONObject();
        try {
            localVideoJson.put("name",localVideoBean.name);
            localVideoJson.put("length",localVideoBean.length);
            localVideoJson.put("size",localVideoBean.size);
            localVideoJson.put("path",localVideoBean.path);
            localVideoJson.put("lastModify",localVideoBean.lastModify);
            localVideoJson.put("thumbPath",localVideoBean.thumbPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return localVideoJson;
    }
}
