package com.baofeng.mj.unity.launcher;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class AndroidManager {
    /**
     * Global manager controller,use this method can call any Manager which inherit the AndroidManager.
     * @param json
     *          the JSON data that contains the calssName，method and arg.
     * @param mCallback
     *          the corresponding CallBack .If there is no callback ,you can use null instead.
     * @return
     *          return the value from the method invoked, value null represents the method have no return value.
     */
    public static String request(String json, AndroidCallback mCallback) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<RequestObject>() {
        }.getType();
        RequestObject bean = null;
        try {
            bean = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return (String)bean.invoke(mCallback);
    }
}
