package com.baofeng.mj.business.downloadutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.diskutil.DiskLruCache;
import com.bfmj.sdk.util.SharedPreferencesUtil;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Common {
    private static DiskLruCache mDiskLruCache;
    public static String savePath = "";
    public static boolean isreadSavePath = false;

    /**
     * 视频时长格式化
     *
     * @param time 时长（秒数）
     * @return 格式化后的时长（HH:MM:SS）
     */
    public static String formatTime(int time, boolean isShowHour) {

        String strTime = "";

        long second = time % 60;

        strTime = (second < 10 ? "0" : "") + second;

        time = time / 60;

        if (time >= 1) {
            long minute = time % 60;
            strTime = (minute < 10 ? "0" : "") + minute + ":" + strTime;
        } else {
            strTime = "00:" + strTime;
        }

        if (isShowHour) {
            time = time / 60;

            if (time >= 1) {
                strTime = (time < 10 ? "0" : "") + time + ":" + strTime;
            } else {
                strTime = "00:" + strTime;
            }

        }

        return strTime;
    }

    /*
     * 字符串截取函数，中文字符按两个字符处理
     */
    public static String subStr(String str, int end) {
        if (str == null) {
            return "";
        }
        int len = str.length();
        String endStr = "";
        for (int i = 0; i < end && i < len; i++) {
            int n = (int) str.charAt(i);
            if (n > 256) {
                end -= 1;
            }
        }
        if (end > len) {
            end = len;
        } else {
            endStr = "...";
        }
        return str.substring(0, end) + endStr;
    }

    public static String geCurrtDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String day = format.format(new Date());
        return day;
    }

    public static String[] getMonthAndDay() {
        String[] day = new String[2];
        SimpleDateFormat format = new SimpleDateFormat("M", Locale.CHINA);
        day[0] = format.format(new Date());

        format = new SimpleDateFormat("d", Locale.CHINA);
        day[1] = format.format(new Date());

        return day;
    }

	/*
     * public static String getDayOfWeek() {
	 * final Calendar c = Calendar.getInstance();
	 * c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
	 * String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
	 * if ("1".equals(mWay)) {
	 * mWay = "天";
	 * } else if ("2".equals(mWay)) {
	 * mWay = "一";
	 * } else if ("3".equals(mWay)) {
	 * mWay = "二";
	 * } else if ("4".equals(mWay)) {
	 * mWay = "三";
	 * } else if ("5".equals(mWay)) {
	 * mWay = "四";
	 * } else if ("6".equals(mWay)) {
	 * mWay = "五";
	 * } else if ("7".equals(mWay)) {
	 * mWay = "六";
	 * }
	 * return mWay;
	 * }
	 */

    // 日期间隔
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

    // 日期间隔
    public static int getGapDayCount(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        Date date_start = null;
        Date date_end = null;
        try {
            date_start = sdf.parse(start);
            date_end = sdf.parse(end);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return getGapCount(date_start, date_end);
    }

    public static int getNetWorkState(Context context) {

        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // wifi
        NetworkInfo wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.getState() == State.CONNECTED) {
            return NetState.e_net_reachable_via_wifi;
        }

        NetworkInfo ethInfo = conMan
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (ethInfo != null && ethInfo.getState() == State.CONNECTED) {
            return NetState.e_eth;
        }

        return NetState.e_net_not_reachable;
    }

    @SuppressLint("NewApi")
    public static HashMap<String, Long> getAvailMemory(Context context) {
        HashMap<String, Long> hm = new HashMap<String, Long>();

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // hm.put("total", mi.totalMem);
        hm.put("avail", mi.availMem);

        return hm;
    }

    public static HashMap<String, Long> getSDCardSize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            HashMap<String, Long> hm = new HashMap<String, Long>();

            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            hm.put("total", blockSize * blockCount);
            hm.put("avail", availCount * blockSize);
            return hm;
        } else {
            return null;
        }
    }

    public static String getRandomString(int length) {
        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        char[] data = new char[length];

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length);
            data[i] = chars[index];
        }
        String s = new String(data);
        return s;
    }

    public static String genSDCacheFilePath() {
        String path = getSavePath();
        path = path + "cache";

        return path;
    }

    private interface NetState {
        int e_net_not_reachable = 0; //
        int e_net_reachable_via_wwan = 1; // Wireless Wide Area
        // Network
        int e_net_reachable_via_wifi = 2; // wifi
        int e_eth = 3; // 网线
    }

    public static void httpGet(String actionUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(actionUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.connect();

            int code = conn.getResponseCode();
            if (code != 200) {
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // 统一释放资源
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static byte[] ApacheHttpGet(String url) {
        byte[] result = null;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpGet request = new HttpGet(url);
        request.addHeader(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.11 (KHTML, like Gecko) Ubuntu/12" +
                        ".04 Chromium/20.0.1132.47 Chrome/20.0.1132.47 Safari/536.11");
        HttpResponse response;
        ByteArrayOutputStream os = null;
        try {
            response = client.execute(request);
            os = new ByteArrayOutputStream();
            response.getEntity().writeTo(os);
            result = os.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (os != null) {
                    os.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static String ApacheHttpPost(String url, String content) {
        String result = "";

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(url);
        post.addHeader(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.11 (KHTML, like Gecko) Ubuntu/12" +
                        ".04 Chromium/20.0.1132.47 Chrome/20.0.1132.47 Safari/536.11");

        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        BasicNameValuePair param = new BasicNameValuePair("content", content);
        paramList.add(param);

        try {
            post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
            HttpResponse httpResponse = client.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //	public static String getUUID(Context context) {
    //		String uniqueId = "";
    //		try {
    //			String macAddress = "", cpuSerial = "", androidId;
    //			WifiReceiver wifiMgr = (WifiReceiver) context
    //					.getSystemService(Context.WIFI_SERVICE);
    //			WifiInfo info = (null == wifiMgr ? null : wifiMgr
    //					.getConnectionInfo());
    //			if (null != info) {
    //				macAddress = info.getMacAddress();
    //			}

    //			androidId = ""
    //					+ android.provider.Settings.Secure.getString(
    //							context.getContentResolver(),
    //							android.provider.Settings.Secure.ANDROID_ID);
    //			UUID deviceUuid = new UUID(androidId.hashCode(),
    //					((long) macAddress.hashCode() << 32) | cpuSerial.hashCode());
    //			uniqueId = deviceUuid.toString();
    //		} catch (Exception e) {
    //			// TODO: handle exception
    //		}
    //
    //		return uniqueId;
    //	}

    public static String getUserVersionCode(Context context) {
        if (context == null)
            return null;

        try {
            String packageName = context.getPackageName();
            String versionCode = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
            if (versionCode.indexOf("-") == -1)
                return "";

            versionCode = versionCode.substring(0, versionCode.indexOf("-"));
            return versionCode;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDevelopVersionCode(Context context) {
        if (context == null)
            return null;

        try {
            String packageName = context.getPackageName();
            String versionCode = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
            if (versionCode.indexOf("-") == -1)
                return "";

            versionCode = versionCode.substring(versionCode.indexOf("-") + 1);
            return versionCode;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param versionCode
     * @return
     * @author qiguolong @Date 2015-5-5 下午4:57:15
     * @description:{判断
     */
    public static String getOtherVersionCode(String versionCode) {

        if (versionCode.indexOf("-") == -1)
            return versionCode;

        versionCode = versionCode.substring(versionCode.indexOf("-") + 1);
        return versionCode;

    }

    public static String getVersionName(Context context) {
        String version = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = null;

            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);

            version = packInfo.versionName;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static boolean isPublicVersion(String version) {
        return !TextUtils.isEmpty(version) && version.endsWith("1");
    }

    public static boolean isUpgradeVersion(Context context) {
        String version = getVersionName(context);
        if (!TextUtils.isEmpty(version)) {
            return version.endsWith("1131");
        }
        return false;
    }

    public static HashMap<String, String> getScreenInfo(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);

        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        int densityDpi = metric.densityDpi;

        HashMap<String, String> hs = new HashMap<String, String>();
        hs.put("w", width + "");
        hs.put("h", height + "");
        hs.put("den", density + "");
        hs.put("dendpi", densityDpi + "");

        return hs;
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return infos;
    }

    public static Bitmap transImageViewToBitmap(ImageView v) {
        ImageView iv = (ImageView) v;
        BitmapDrawable bd = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }

    public static boolean detect(Activity act) {
        ConnectivityManager manager = (ConnectivityManager) act
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    public static String getSavePath() {
        String path = "";
        if (!TextUtils.isEmpty(savePath)) {
            return savePath;
        }
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
                BaseApplication.INSTANCE);
        path = sharedPreferencesUtil.getString("savepath", "");
        if (!TextUtils.isEmpty(path)) {
            if (new File(path).exists()) {
                return savePath = path;
            }

        }
        // sdcard
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            path = sdDir + "/mojing/";

        } else {
            String romString = Environment.getDataDirectory().getAbsolutePath();
            path = romString + "/data/" + "com.baofeng.mj" + "/";
        }

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return savePath = path;
    }

    public static String getResourcePath() {
        String appPath = getSavePath();
        File dir = new File(appPath + "resource_download");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath().toString() + "/";
    }

    public static String getSaveFile(String filename, String folder) {
        String path = getSavePath() + folder + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path + filename;
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }

    public static void savePNG(Bitmap bitmap, String name) {
        if (bitmap == null)
            return;

        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 75, out)) {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getFileSuffix(String downUrl) {

        if (downUrl == null) {
            return "";
        }
        LogHelper.e("infos","downUrl=="+downUrl);
        String suffix = "";
        if (downUrl.indexOf(".apk") > 0) {
            suffix = ".apk";
        } else if (downUrl.indexOf(".mp4") > 0) {
            suffix = ".mp4";
        } else {
            URI uri = URI.create(downUrl);
            String urlPath = uri.getPath();
            if (urlPath == null || "".equals(urlPath.trim()))
                return "";
            String[] strs = urlPath.split("\\.");
            int len = strs.length;
            if (len == 2) {
                suffix = "." + strs[1];
            }
        }

        return suffix;
    }

    public static File getFile(DownloadItem downloadItem) {
        String suffix = getFileSuffix(downloadItem.getHttpUrl());
        return new File(downloadItem.getFileDir(), downloadItem.getTitle()
                + suffix);
    }

    public static boolean fileExist(DownloadItem item, String suffix) {
        return fileExist(item.getFileDir(), item.getTitle(), suffix);
    }

    public static boolean fileExist(DownloadItem item) {
        return fileExist(item.getFileDir(), item.getTitle(),
                getFileSuffix(item.getHttpUrl()));
    }

    public static boolean fileExist(String fileDir, String title, String suffix) {
        File file ;
//        File f = new File(fileDir,title);
        if(suffix.endsWith(".apk") || suffix.endsWith(".zip") || suffix.endsWith(".mp4")){
            file = new File(fileDir, title + suffix);
        }else {
            file = new File(fileDir,title);
        }
        LogHelper.e("infos","file.path==="+file.getAbsolutePath()+"==file=="+file.exists()+"==suffix=="+suffix);
        if (file.exists()) {
            return true;
        }

        return false;
    }



    public static int getByteSize(String sizes) {
		/*
		 * int size = 0;
		 * sizes = sizes.toLowerCase();
		 * sizes = sizes.replace("b", "");
		 * if (sizes.contains("m")) {
		 * sizes = sizes.replace("m", "");
		 * size = (int) (Double.parseDouble(sizes) * 1024 * 1024);
		 * } else if(sizes.contains("g")) {
		 * sizes = sizes.replace("g", "");
		 * size = (int) (Double.parseDouble(sizes) * 1024 * 1024 * 1024);
		 * } else if(sizes.contains("k")) {
		 * sizes = sizes.replace("k", "");
		 * size = (int) (Double.parseDouble(sizes) * 1024);
		 * } else {
		 * size = (int) Double.parseDouble(sizes);
		 * }
		 */

        return 1;
    }


    // 得到文件下的文件名称列表
    public static ArrayList<String> getDirFilesName(String path) {
        ArrayList<String> fileArr = new ArrayList<String>();

        File file = new File(path);
        File[] comeinFile = file.listFiles();
        if (comeinFile != null && comeinFile.length > 0) {
            for (int i = 0; i < comeinFile.length; i++) {
                // 图片限定格式 - 修改扫出未下载完成的
                if (path.contains("image")) {
                    if (comeinFile[i].getName().toLowerCase().endsWith(".jpg")
                            || comeinFile[i].getName().toLowerCase()
                            .endsWith(".jpeg")) {
                        fileArr.add(comeinFile[i].getName());
                    }
                } else {
                    fileArr.add(comeinFile[i].getName());
                }
            }
        }

        return fileArr;
    }

    public static ArrayList<String> getDirFilesNameForMp4(String path) {
        ArrayList<String> fileArr = new ArrayList<String>();

        File file = new File(path);
        File[] comeinFile = file.listFiles();
        if (comeinFile != null && comeinFile.length > 0) {
            for (int i = 0; i < comeinFile.length; i++) {

                File _file = comeinFile[i];
                String _fileName = _file.getName();

                if (_fileName.toLowerCase().endsWith(".mp4")) {
                    fileArr.add(comeinFile[i].getName());
                }
            }
        }

        return fileArr;
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
     * 计算魔豆
     *
     * @param rechargeModou 充值魔豆
     * @param giftModou     赠送魔豆
     * @return
     */
    public static float calculateModou(String rechargeModou, String giftModou) {
        float fl = Float.parseFloat(rechargeModou)
                + Float.parseFloat(giftModou);
        fl = (float) Math.round(fl * 10) / 10;

        return fl;
    }


    public static String getHeadImageName(String uid) {
        return "head_" + uid + ".png";
    }

    /**
     * 获取当前应用程序的版本号。
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 根据传入的uniqueName获取磁盘缓存的路径地址。
     * 磁盘缓存存入app包下，这样卸载能相应的删除
     */
    @SuppressLint("NewApi")
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            /**处理异常 java.lang.NullPointerException at com.baofeng.mj.pubblico.util.PlayerCommon
             * .getDiskCacheDir(Common.java:950)*/
            if (context.getExternalCacheDir() == null) {
                cachePath = getExternalCacheDir(context);
            } else {
                cachePath = context.getExternalCacheDir().getPath();
            }

        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private static String getExternalCacheDir(Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/")
                .append(context.getPackageName())
                .append("/cache").toString();
        return sb.toString();
    }


    /**
     * 获取缓存对象
     *
     * @return DiskLruCache缓存对象
     * @author lixianke @Date 2015-2-2 下午2:39:02
     * description: 获取缓存对象
     */
    public static synchronized DiskLruCache getDiskLruCache() {
        if (mDiskLruCache == null) {
            // 初始化磁盘缓存
            try {
                // 获取图片缓存路径
                File cacheDir = Common.getDiskCacheDir(BaseApplication.INSTANCE,
                        "files");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }

                // 创建DiskLruCache实例，初始化缓存数据
                mDiskLruCache = DiskLruCache.open(cacheDir,
                        Common.getAppVersion(BaseApplication.INSTANCE), 1,
                        10 * 1024 * 1024);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mDiskLruCache;
    }












    // ================game end===============================//

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
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return
     * @author qiguolong @Date 2015-10-30 下午7:25:20
     * @description:{获取 缩略图存储地址}
     */
    public static String getImageLoaderDisk() {
        return getSavePath() + "thumb";

    }

    public static String getDownloadDic() {
        return getSavePath() + "download";

    }



}
