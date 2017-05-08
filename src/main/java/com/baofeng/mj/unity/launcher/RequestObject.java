package com.baofeng.mj.unity.launcher;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RequestObject {

    //the manager's calss name 
    public String className;
    //the method needed to invoke
    public String method;
    //the method's arguments except the callback
    public ArrayList<Object> arg;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ArrayList<Object> getArg() {
        return arg;
    }

    public void setArg(ArrayList<Object> arg) {
        this.arg = arg;
    }

    /**
     * depends on the reflect to invoke the specified method
     * @param callback
     * @return
     */
    public Object invoke(AndroidCallback callback) {
        if (callback != null) {
            arg.add(callback);
        }
        Object obj = null;
        try {
            Class<AndroidManager> clazz = (Class<AndroidManager>) Class
                    .forName(className);
            Method[] methods = clazz.getMethods();
            AndroidManager manager = null;
            for (Method mtd : methods) {
                if (mtd.getName().equals("getAndroidManager")) {
                    manager = (AndroidManager) mtd.invoke(null,
                            (Object[])null);
                }
            }

            for (Method mtd : methods) {
                if (mtd.getName().equalsIgnoreCase(separateMethodName(method))) {
                    if (arg != null && arg.size() != 0) {
                        obj = mtd.invoke(manager,arg.toArray(new Object[arg.size()]));
                    } else{
                        obj = mtd.invoke(manager, (Object[])null);
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            Log.i("RequestObject", e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.i("RequestObject", e.toString());
        }
        return obj;

    }
    private String separateMethodName(String str){
        String[] split = str.split("_");
        return split[1];
    }

}
