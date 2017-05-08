package com.baofeng.mj.util.publicutil;

import android.content.ContentResolver;
import android.util.Log;

import com.baofeng.mj.business.publicbusiness.BaseApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhaominglei on 2016/5/23.
 */
public class DateUtil {
    private static SimpleDateFormat minDateFormat;
    private static SimpleDateFormat hourDateFormat;

    /**
     * 格式化日期
     */
    public static String min2String(long time) {
        if(minDateFormat == null){
            minDateFormat = new SimpleDateFormat("mm:ss");
        }
        return minDateFormat.format(new Date(time));
    }

    /**
     * 格式化日期
     */
    public static String hour2String(long time) {
        if(hourDateFormat == null){
            hourDateFormat = new SimpleDateFormat("HH:mm:ss");
        }
        return hourDateFormat.format(new Date(time));
    }

    /**
     * 格式化日期
     */
    public static String date2String(long time, String match) {
        return new SimpleDateFormat(match).format(new Date(time));
    }

    /**
     * 格式化日期
     */
    public static String date2String(Date date, String match) {
        return new SimpleDateFormat(match).format(date);
    }

    /**
     * 格式化日期
     */
    public static String date2StringForSecond(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化日期
     */
    public static String date2StringForMills(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * string型日期比较
     */
    public static boolean compareDate(String match, String date1, String date2) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(match, Locale.getDefault());
            return df.parse(date1).compareTo(df.parse(date2)) >= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean compareDate(String date1, String date2) {
        return compareDate("yyyy-MM-dd HH:mm:ss", date1, date2);
    }

    /**
     * 获取当前时间
     */
    public static String getCurTime(){
        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        int second = mCalendar.get(Calendar.SECOND);
        if(mCalendar.get(Calendar.AM_PM) == Calendar.AM){//上午

        }else{//下午
            if(hour < 12){//12小时制
                hour = hour + 12;//转成24小时制
            }
        }
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    /**
     * true：12小时制，false：24小时制
     */
    public static boolean hourIs12(){
        ContentResolver mResolver= BaseApplication.INSTANCE.getContentResolver();
        String key = android.provider.Settings.System.TIME_12_24;
        if("12".equals(android.provider.Settings.System.getString(mResolver,key))) {//12小时制
            Log.i("hourIs12","12小时制");
            return true;
        }else {//24小时制
            Log.i("hourIs12","24小时制");
            return false;
        }
    }
}
