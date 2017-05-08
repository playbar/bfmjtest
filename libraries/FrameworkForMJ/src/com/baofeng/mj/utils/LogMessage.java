
package com.baofeng.mj.utils;

import android.text.TextUtils;
import android.util.Log;

import com.anjoyo.framework.BuildConfig;

/**
 * 日志打印类
 */
public class LogMessage {

    // 是否打印日志
    private static boolean isDebug = BuildConfig.DEBUG;
    // 日志标签
    public static String LOG_TAG = "frame";

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg != null ? msg : "");
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg != null ? msg : "");
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg != null ? msg : "");
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg != null ? msg : "");
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg != null ? msg : "");
        }
    }

    public static void print(String tag, String msg) {
        // System.out.println("tag=="+msg);
    }

    public static void v(String msg) {
        v(getLocation(), msg);
    }

    public static void i(String msg) {
        i(getLocation(), msg);
    }

    public static void d(String msg) {
        d(getLocation(), msg);
    }

    public static void w(String msg) {
        w(getLocation(), msg);
    }

    public static void e(String msg) {
        e(getLocation(), msg);
    }


    /**
     * 设置debug 模式
     *
     * @param isDebug true 打印日志 false：不打印
     */
    public static void setiSDebug(boolean isDebug) {
        LogMessage.isDebug = isDebug;
    }

    private static String getLocation() {
        final String className = LogMessage.class.getName();
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();
        boolean found = false;


        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":"
                                + trace.getMethodName() + ":"
                                + trace.getLineNumber() + "]";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }


        return "[]: ";
    }


    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }


            return getClassName(clazz.getEnclosingClass());
        }


        return "";
    }

    public static boolean isDebug() {
        return isDebug;
    }
}
