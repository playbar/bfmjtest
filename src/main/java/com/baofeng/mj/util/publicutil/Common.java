package com.baofeng.mj.util.publicutil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ParamsInfo;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.utils.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Common {
    /**
     * 验证字符串是否有效
     *
     * @param str
     * @param checkNull 为true,会检测null字符串
     * @return
     */
    public static boolean valid(String str, boolean checkNull) {
        if (str == null) {
            return false;
        }
        if ("".equals(str.trim())) {
            return false;
        }
        if (checkNull && "null".equalsIgnoreCase(str.trim())) {
            return false;
        }
        return true;
    }

    public static HashMap<String, String> getHardwareInfo() {
        HashMap<String, String> infos = new HashMap<String, String>();

        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                String fname = field.getName();
                Object fval = field.get(null);
                if (fname != null && fval != null) {
                    infos.put(fname, fval.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return infos;
    }

    /***
     * 去除重复元素
     *
     * @param arlList
     */
    public static void removeDuplicateWithOrder(List arlList) {
        Set set = new HashSet();
        List newList = new ArrayList();
        synchronized (arlList) {
            for (Iterator iter = arlList.iterator(); iter.hasNext(); ) {
                Object element = iter.next();
                if (set.add(element))
                    newList.add(element);
            }
            arlList.clear();
            arlList.addAll(newList);
        }
    }

    public static void hideSoftInput(Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成用户中心请求参数的openId 的json函数 （由于生成的openid的顺序要与参数加密顺序完全一致 所以才有手动拼写字符串的形式生成json）
     *
     * @param params 参数列表
     * @return josn String
     */
    public static String getUsercenterJSONParams(List<ParamsInfo> params) {
        if (params == null || params.size() <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < params.size(); i++) {
            ParamsInfo info = params.get(i);
            if (info.getValue() instanceof String) {
                sb.append("\"").append(info.getKey()).append("\"").append(":").append("\"").append(info.getValue()).append("\"").append(",");
            } else {
                sb.append("\"").append(info.getKey()).append("\"").append(":").append(info.getValue()).append(",");
            }
        }
        String sbStr = sb.substring(0, sb.length() - 1);
        sbStr += "}";
        return sbStr;
    }

    /* * description:截取指定字节长度的字符串 （中文==3个字节长度）
     * @param text 字符串
     * @param allcount 截取的字节长度
     * @return String 截取后的字符串
     */
    public static String getStringBycharCount(String text, int allcount) {
        if (StringUtils.isEmpty(text) || allcount <= 0)
            return "";
        String subStr = "";
        try {
            byte[] bytearry = text.getBytes("UTF-8");
            byte[] temp = new byte[allcount];
            if (bytearry.length < allcount)
                return text;
            for (int i = 0; i < temp.length; i++) {
                if (i < bytearry.length) {
                    temp[i] = bytearry[i];
                }
            }
            String temStr = new String(temp, "UTF-8");
            int index = temStr.length();
            for (int i = 0; i < temStr.length(); i++) {
                if (text.length() > i) {
                    if (text.charAt(i) != temStr.charAt(i)) {
                        index = i;
                        break;
                    }
                }
            }
            subStr = temStr.substring(0, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subStr;
    }

    /***
     * @param text
     * @param count     字符长度
     * @param showCount 要显示的长度length
     * @return 截取后的字符串
     */
    public static String getEllipsizeStr(String text, int count, int showCount) {
        if (StringUtils.isEmpty(text))
            return text;
        String str = getStringBycharCount(text, count);
        if (str.length() > showCount) {
            str = str.substring(0, showCount) + "...";
        } else if (text.length() > str.length()) {
            str = str + "...";
        }
        return str;
    }

    public static float str2float(String str) {
        float f = 0f;
        try {
            if (!StringUtils.isEmpty(str)) {
                f = Float.parseFloat(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }


    /**
     * 获取浮点类型字符串
     *
     * @param data
     * @param pattern
     * @return
     */
    public static String getDecimalFormatStr(float data, String pattern) {
        DecimalFormat format = new DecimalFormat(pattern);
        String amountstr = format.format(data);
        return amountstr;
    }

    /***
     * 验证电话号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
        String telRegex = "[1][34578]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * @return int 字符串字节长度
     * @author wanghongfang  @Date 2015-1-20 上午9:51:33
     * description:获取字符串字节长度 （中文=2个字节长度）
     * @param str字符串
     */
    public static int getCharCount(String str) {
        if (TextUtils.isEmpty(str))
            return 0;
        int count = 0;
        try {
            byte[] bytearry = str.getBytes("UTF-8");
            count = bytearry.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 保存图片文件
     *
     * @param bm       图片的bitmap
     * @param filePath 保存的文件路径
     * @param fileName 保存的文件名称
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, String filePath, String fileName)
            throws IOException {
        if (bm == null) {
            return;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File file = new File(dirFile, fileName);
        if (file.exists()) { // 如果存在此图片则返回
            return;
        }
        File myCaptureFile = new File(filePath + "/" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    /**
     * true当前app是正式版，false是开发板
     *
     * @param version
     * @return
     */
    public static boolean isPublicVersion(Context context, String version) {
        if (!TextUtils.isEmpty(version)) {
            int lastIndex = version.lastIndexOf(".");
            if (lastIndex > 0) {
                String versionPart = version.substring(lastIndex + 1, version.length());
                if ("1111".equals(versionPart)) {
                    return true;//官网正式版
                }
                String[] channelArry = context.getResources().getStringArray(R.array.app_market_channel_ids);
                for (String channel : channelArry) {
                    if (channel.equals(versionPart)) {
                        return true;//应用市场正式版
                    }
                }
            }
        }
        return false;//是开发板
    }

    public static String getMainnVersion(Context context) {
        try {
            String packageName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
            if (versionName.indexOf("-") == -1)
                return "";
            versionName = versionName.substring(0, versionName.indexOf("-"));
            return versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间戳转化为yyyy-MM-dd HH:mm:ss 定义格式类型
     *
     * @param time
     * @param fromate
     * @return
     */
    public static String getFormatDate(long time, String fromate) {
        SimpleDateFormat format = new SimpleDateFormat(fromate);
        Date date = new Date(time);
        String formattime = format.format(date);
        return formattime;
    }

    /**
     * 获取url头部
     *
     * @param url
     * @return
     */
    public static String getUrlHead(String url) {
        String urlHead;
        if (url.indexOf(".") == -1)
            return "";
        urlHead = url.substring(0, url.indexOf("."));
        return urlHead;
    }

    /**
     * 获取url尾部
     *
     * @param url
     * @return
     */
    public static String getUrlTail(String url) {
        String urlTail;
        if (url.indexOf(".") == -1)
            return "";
        urlTail = url.substring(url.indexOf("."));
        return urlTail;
    }

    /***
     * 检查输入昵称是否合法
     * @param str
     * @return
     */
    public static boolean checkUserName(String str){
        String regEx = "^[\\u4e00-\\u9fa5_A-Za-z0-9_-]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        return rs;
    }

    /**
     * 返回日期的时间戳
     *
     * @param time "2015-08-19 19:06:19";
     * @return String 时间戳
     */
    public static String getTimestamps(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(time);
            return date.getTime() + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
/**
     * 对map排序 传回key=value&key=value样式用于服务器接口拼接sign
     *
     * @param map
     * @return
     */

    public static String getSortString(Map map) {
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> stringStringEntry, Map.Entry<String, String> t1) {
                return (stringStringEntry.getKey().toString().compareTo(t1.getKey()));
            }
        });
        int count = infoIds.size();
        String str = "";
        for (int i = 0; i < count; i++) {
            String id = infoIds.get(i).toString();
            if (i == count - 1) {
                str += id;
            } else {
                str += id + "&";
            }
        }
        return str.trim();
    }

    public static String getCodeStr(String str) {
        String code = encodeBase64(hmac_sha1(encodeURL(str))).trim();
        return code;
    }

    //URL编码
    public static String encodeURL(String str) {
        String urlEncodeStr = "";
        try {
            urlEncodeStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlEncodeStr;
    }

    //hmac_sha1算法加密
    public static String hmac_sha1(String value) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = ConfigUrl.MJ_SUBS_KEY.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            String hexBytes = byte2hex(rawHmac);
            return hexBytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String byte2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1) hs = hs + "0" + stmp;
            else hs = hs + stmp;
        }
        return hs;
    }

    public static String encodeBase64(String str) {
        return new String(Base64.encode(str.getBytes(), Base64.DEFAULT));
    }



    /***
     * 日期间隔
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
                .getTime()) / (1000 * 60 * 60 * 24));
    }

    /***
     * 获取mac
     * @return
     */
    public static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return macSerial;
    }

    /***
     * 判断是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
        return pattern.matcher(str).matches();
    }


}
