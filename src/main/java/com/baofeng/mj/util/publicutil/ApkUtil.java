package com.baofeng.mj.util.publicutil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.baofeng.mj.bean.ApkInstallInfo;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.callback.PackageChangeCallback;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesSdkBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.unity.UnityGameBusiness;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.UnZipUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.systemutil.FileManager;
import com.bfmj.Unzip.MJUncompress;
import com.bfmj.Unzip.UncompressCallback;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by liuchuanchi
 * apk工具类
 */
public class ApkUtil {

    public static final int NEED_DOWNLOAD = 0;//下载apk
    public static final int NEED_UPDATE = 8;//升级apk
    public static final int NEED_INSTALL = 9;//安装apk
    public static final int CAN_PLAY = 10;//打开apk
    public static final int NEED_UNZIP = 11;//解压zip
    public static final int DOWNLOADING = 2;//下载中
    public static final int PAUSE = 6;//暂停中

    public static final int INSTALL_SUCCEEDED = 1;//安装成功

    public static  final  String APK_RES_PATH = "res/drawable/";
    public static  final  String PLAY_METHOD_FILE_NAME = "com_mojing_vr_playmethod.png";
    public static  final  String RECT_ICON_FILE_NAME = "com_mojing_vr_icon_rect.png";
    public static  final  String GAME_IMAGE_PATH = "/mojing/game_image/";

    /**
     * 验证apk
     *
     * @param apk            本地apk文件
     * @param packageName    包名
     * @param newVersioncode apk最新版本号
     * @return
     */
    public static int checkApk(File apk, String packageName, int newVersioncode) {
        if(apk.exists()){//本地有apk
            if (apk.getName().endsWith(".zip")) {
                return NEED_UNZIP;//解压apk
            }

            int installedVersioinCode = getVersionCodeByPackageName(packageName);//已安装apk的版本号
            if(installedVersioinCode > 0){//已安装apk

                int apkVersionCode = getVersionCodeByFilePath(apk.getAbsolutePath());//本地apk的版本号
                if (apkVersionCode < newVersioncode) {//本地apk的版本号，小于最新版本号
                    return NEED_UPDATE;//更新apk
                } else if (installedVersioinCode < apkVersionCode) {//已安装apk的版本号，小于本地apk的版本号
                    return NEED_INSTALL;//安装apk
                } else {
                    return CAN_PLAY;//打开apk
                }
            }
            else{//未安装apk
                return NEED_INSTALL;//安装apk
            }
        }else{//本地没有apk
            int installedVersioinCode = getVersionCodeByPackageName(packageName);//已安装apk的版本号
            if(installedVersioinCode > 0){//已安装apk
                if (installedVersioinCode < newVersioncode) {//已安装apk版本号，小于最新版本号
                    return NEED_UPDATE;//更新apk
                } else {
                    return CAN_PLAY;//打开apk
                }
            }else{//未安装apk
                return NEED_DOWNLOAD;//下载apk
            }
        }
    }

    /**
     * 获取已安装apk信息集合
     *
     * @return
     */
    public static List<ApkInstallInfo> getInstallApkInfos() {
        List<ApkInstallInfo> installGameInfos = new ArrayList<ApkInstallInfo>();//已安装apk信息集合
        PackageManager packageManager = BaseApplication.INSTANCE.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        if (packageInfos != null && packageInfos.size() > 0) {
            for (PackageInfo packageInfo : packageInfos) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) <= 0) {//是否为非系统预装的apk
                    ApkInstallInfo installGameInfo = new ApkInstallInfo();
                    installGameInfo.setPackageName(packageInfo.packageName);
                    installGameInfo.setVersionCode(packageInfo.versionCode);
                    installGameInfo.setAppName(applicationInfo.loadLabel(packageManager).toString());
                    installGameInfos.add(installGameInfo);
                }
            }
        }
        return installGameInfos;
    }

    /**
     * 安装apk
     *
     * @param apkPath apk路径
     */
    public static void installApk(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装apk
     * @param resType 资源类型
     * @param resId 资源id
     * @param resTitle 资源标题
     */
    public static void installApk(final Context context, int resType, String resId, String resTitle){
        File apkFile = DownloadResBusiness.getApkFile(resType, resId, resTitle);
        String apkPath = apkFile.getAbsolutePath();
        if(apkPath.endsWith(".apk")){//apk文件，直接安装
            ApkUtil.installApk(context, apkPath);
        }else if(apkPath.endsWith(".zip")){//zip文件，先解压再安装
            DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(resType, resId, resTitle, ".zip");
            downloadItem.setAppFromType(ConstantKey.OBB);
            UnZipUtil.unZip(downloadItem, new UnZipUtil.UnZipNotify() {
                @Override
                public void notify(DownloadItem downloadItem, int unZipResult) {
                    if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                        File file = DownloadResBusiness.getDownloadResFile(downloadItem);
                        ApkUtil.installApk(context, file.getAbsolutePath());//安装apk
                    }
                }
            });
        }
    }

    /**
     * 静默安装
     * @param packageName
     * @param resType 资源类型
     * @param resId 资源id
     * @param resTitle 资源标题
     */
    public static void installSilentApk(final String packageName, int resType, String resId, final String resTitle){
        File apkFile = DownloadResBusiness.getApkFile(resType, resId, resTitle);
        final String apkPath = apkFile.getAbsolutePath();
        LogHelper.e("installapp","apkPath=="+apkPath);
        if(apkPath.endsWith(".apk")){//apk文件，直接安装
//            ApkUtil.installApk(context, apkPath);
            install(apkPath, packageName, null);
        }else if(apkPath.endsWith(".zip")){//zip文件，先解压再安装
            UnityGameBusiness.deleteZipFile(resType,resId,resTitle);
            DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(resType, resId, resTitle, ".zip");
            downloadItem.setAppFromType(ConstantKey.OBB);
            mjUncompress(downloadItem, packageName);
        }
    }

    public static void mjUncompress(final DownloadItem downloadItem, final String packageName){
        String fileDir = DownloadResBusiness.getDownloadResFolder(downloadItem.getDownloadType()) + File.separator;
        final String obbDir = fileDir + downloadItem.getAid() + File.separator;
        String path = fileDir + downloadItem.getAid()+".zip";

        final HashMap<String, String> compressParam = new HashMap<>();
        compressParam.put("zip_path", path);
        compressParam.put("apk_filename", downloadItem.getAid());
        compressParam.put("apk_path", fileDir);
        compressParam.put("obb_path", obbDir);

        LogHelper.d("installapp", "111111111111");
        MJUncompress mjUncompress = new MJUncompress();
        mjUncompress.UncompressStart(path, downloadItem.getAid()+".apk", fileDir, obbDir, true, new UncompressCallback() {
            @Override
            public void UncompressOk() {
                LogHelper.d("installapp", "UncompressOk");
                File file = DownloadResBusiness.getDownloadResFile(downloadItem);
                install(file.getAbsolutePath(), packageName, compressParam);
            }

            @Override
            public void UncompressFailed(String s) {
                LogHelper.d("installapp", "UncompressFailed s = " + s);
                if(UnityActivity.INSTANCE != null) {
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//安装完成通知Unity
                        iAndroidCallback.sendApkInstallResult(packageName, -1);
                    }
                }
            }

            @Override
            public void UncompressingProgress(int i, int i1) {//第一个参数 包中文件个数 第二个参数 解压的第几个
                LogHelper.d("installapp", "UncompressingProgress i = " + i+"==progress=="+i1);
                if(i  < 0){
                    if(UnityActivity.INSTANCE != null) {
                        IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                        if (iAndroidCallback != null) {//安装完成通知Unity
                            iAndroidCallback.sendApkInstallResult(packageName, -1);
                        }
                    }
                }
            }
        });
    }

    private static void install(String apkPath, final String packageName, final HashMap<String, String> compressParam){
        saveGamePlayImage(apkPath, packageName);

        FileManager.getAndroidManager().installPackage(apkPath, new PackageChangeCallback() {
            @Override
            public void onPackageInstalled(int resultCode) {
                LogHelper.d("installapp", "onPackageInstalled  resultCode = " + resultCode);
                if (resultCode == INSTALL_SUCCEEDED){
                    LogHelper.d("installapp", "install call ");
                    String obbPath = compressParam != null ? compressParam.get("obb_path") : null;
                    if (obbPath != null && !obbPath.isEmpty()){
                        LogHelper.d("installapp", "moveFile start packageName = " + packageName);
//                        MJUncompress mjUncompress = new MJUncompress();
//                        mjUncompress.moveFile(compressParam.get("obb_path") + "Android/obb/" + packageName,
//                                Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/obb/");
                        Runtime runtime = Runtime.getRuntime();
                        String commandMv = "cp -rf " + compressParam.get("obb_path") + "Android/obb" + " " + Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/";
                        //String commandDel = "rm -rf " + compressParam.get("obb_path");
                        LogHelper.d("installapp", "commandMv = " + commandMv);
                        try {
                            runtime.exec(commandMv);
                          //  runtime.exec(commandDel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        LogHelper.d("installapp", "moveFile end ");
                    }
                }

                if(resultCode != INSTALL_SUCCEEDED && UnityActivity.INSTANCE != null) {
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//安装完成通知Unity
                        iAndroidCallback.sendApkInstallResult(packageName, resultCode);
                    }
                }
            }
        });
    }

    /**
     * 打开apk
     * @param packageName
     */
    public static void startPlayApk(Context context, String packageName) {
        StickUtil.disconnect();
        try {//休眠500毫秒
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String glasses_params = "" ;
        GlassesSdkBean bean = GlassesManager.getGlassesSdkBean();
        try {
            if(null != bean){
                JSONObject json = new JSONObject();
                json.put("manufactureid", bean.getManufactureID());
                json.put("productid", bean.getProductID());
                json.put("glassesid", bean.getGlassesID());
                glasses_params = json.toString();
            }

            Intent intent = getIntent(packageName);
            intent.putExtra("uid", UserSpBusiness.getInstance().getUid());
            intent.putExtra("unity", 1);
            intent.putExtra("glassversion", String.valueOf(SettingSpBusiness.getInstance().getStrongMode()));
            intent.putExtra("glasses_params", glasses_params);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取intent
     *
     * @param packName
     * @return
     */
    private static Intent getIntent(String packName) {
        if (packName.equals("com.BaoFeng.Elysium")
                || packName.equals("com.bfmj.argamedyzmhandle")
                || packName.equals("com.bfmj.argamedyzmhead")) {
            return new Intent(packName + ".LandScape");
        }
        return BaseApplication.INSTANCE.getPackageManager().getLaunchIntentForPackage(packName);
    }

    /**
     * 获取PackageInfo
     *
     * @param filePath
     * @return
     */
    public static PackageInfo getPackageArchiveInfo(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return BaseApplication.INSTANCE.getPackageManager().getPackageArchiveInfo(filePath,
                PackageManager.GET_ACTIVITIES);
    }

    /**
     * 获取PackageInfo
     *
     * @param packageName
     * @return
     */
    public static PackageInfo getPackageInfo(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return BaseApplication.INSTANCE.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取PackageInfo、
     *
     * @param packageManager
     * @param packageName
     * @return
     */
    public static PackageInfo getPackageInfo(PackageManager packageManager, String packageName) {
        if (packageManager == null || TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取apk版本号
     *
     * @param filePath
     * @return
     */
    public static int getVersionCodeByFilePath(String filePath) {
        PackageInfo packageInfo = getPackageArchiveInfo(filePath);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    /**
     * 获取apk版本号
     *
     * @param packageName
     * @return
     */
    public static int getVersionCodeByPackageName(String packageName) {
        PackageInfo packageInfo = getPackageInfo(packageName);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static int getVersionCode() {
        PackageInfo packageInfo = getPackageInfo(BaseApplication.INSTANCE.getPackageName());
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static String getVersionName() {
        PackageInfo packageInfo = getPackageInfo(BaseApplication.INSTANCE.getPackageName());
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    public static String getApkPackage(String path){
        PackageManager pm = BaseApplication.INSTANCE.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            return appInfo.packageName;
        }
       return null;

    }

    /**
     * 获取versionName的前缀  "4.0.0-4.00.0823.1112"
     */
    public static String getVersionNamePrefix() {
        String versionNamePrefix = null;
        PackageInfo packageInfo = getPackageInfo(BaseApplication.INSTANCE.getPackageName());
        if (packageInfo != null) {
            int index = packageInfo.versionName.indexOf("-");
            if (index >= 0) {
                versionNamePrefix = packageInfo.versionName.substring(0, index);
            }else{
                versionNamePrefix = packageInfo.versionName;
            }
        }
        return versionNamePrefix;
    }

    /**
     * 获取versionName的后缀  "4.0.0-4.00.0823.1112"
     */
    public static String getVersionNameSuffix() {
        String versionNameSuffix = null;
        PackageInfo packageInfo = getPackageInfo(BaseApplication.INSTANCE.getPackageName());
        if (packageInfo != null) {
            int index = packageInfo.versionName.indexOf("-");
            if (index >= 0) {
                versionNameSuffix = packageInfo.versionName.substring(index + 1);
            }else{
                versionNameSuffix = packageInfo.versionName;
            }
        }
        return versionNameSuffix;
    }
    /**
     * 获取versionName的中间部分（4.00.0823）  "4.0.0-4.00.0823.1112"
     */
    public static String getVersionNameMiddle(){
        String versionNameMiddle = null;
        PackageInfo packageInfo = getPackageInfo(BaseApplication.INSTANCE.getPackageName());
        if (packageInfo != null) {
            int index = packageInfo.versionName.indexOf("-");
            int lastIndex = packageInfo.versionName.lastIndexOf(".");
            if (index >= 0&&lastIndex>=0) {
                versionNameMiddle = packageInfo.versionName.substring(index + 1,lastIndex);
            }else{
                versionNameMiddle = packageInfo.versionName;
            }
        }
        return versionNameMiddle;
    }

    /**
     * 获取apk签名
     *
     * @param packageName
     * @return
     */
    public static String getApkSign(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager packageManager = BaseApplication.INSTANCE.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
        if (packageInfoList != null && packageInfoList.size() > 0) {
            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equals(packageName)) {
                    Signature[] signatures = packageInfo.signatures;
                    return signatures[0].toCharsString();
                }
            }
        }
        return null;
    }

    public static boolean checkAPKExist(String path, String version) {
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(path)) {
            return false;
        }
        File odlf = new File(path);
        if (odlf.exists()) {
            PackageInfo pa = BaseApplication.INSTANCE.getPackageManager().getPackageArchiveInfo(path,
                    PackageManager.GET_ACTIVITIES);
            if (null == pa) {
                return false;
            }
            return pa.packageName.equals(BaseApplication.INSTANCE.getPackageName())
                    && pa.versionName.contains(version);
        }
        return false;
    }

    /***
     * 判断apk是否安装
     * @param uri
     * @return
     */
    public static boolean isAppInstalled(String uri) {
        PackageManager pm = BaseApplication.INSTANCE.getPackageManager();
        boolean installed =false;
        try {
            pm.getPackageInfo(uri,PackageManager.GET_ACTIVITIES);
            installed =true;
        } catch(PackageManager.NameNotFoundException e) {
            installed =false;
        }
        return installed;
    }

    /**
     * apk是否安装
     * @param packageName 包名
     * @return true已安装，false未安装
     */
    public static boolean apkHasInstalled(String packageName){
        if(0 == getVersionCodeByPackageName(packageName)){
            return false;
        }
        return true;
    }

    public static String getGameMethodImagePath(String packageName){
        String path = getGameImagePath(packageName) + File.separator + PLAY_METHOD_FILE_NAME;
        File file = new File(path);
        return file.exists() ? path : "";
    }

    public static String getRectIconImagePath(String packageName){
        String path = getGameImagePath(packageName) + File.separator + RECT_ICON_FILE_NAME;
        LogHelper.e("RectIcon","path==="+path);
        File file = new File(path);
        return file.exists() ? path : "";
    }

    private static String getGameImagePath(String packageName){
        return Environment.getExternalStorageDirectory().getPath() + File.separator + GAME_IMAGE_PATH + File.separator + packageName;
    }

    private static void saveGamePlayImage(String apkPath, String packageName) {
        String outPath = getGameImagePath(packageName);

        File file = new File(outPath);
        if (!file.exists()){
            file.mkdirs();
        }
        LogHelper.d("saveGamePlayImage", "saveGamePlayImage");
        copyFileByApk(apkPath, APK_RES_PATH + PLAY_METHOD_FILE_NAME, outPath + File.separator + PLAY_METHOD_FILE_NAME);
        copyFileByApk(apkPath, APK_RES_PATH + RECT_ICON_FILE_NAME, outPath + File.separator + RECT_ICON_FILE_NAME);
    }

    private static void copyFileByApk(String apkPath, String fileName, String outPath){
        try {
            ZipFile zipFile = new ZipFile(apkPath);
            LogHelper.d("saveGamePlayImage", "copyFileByApk => " + zipFile);
            ZipEntry entry = zipFile.getEntry(fileName);
            LogHelper.d("saveGamePlayImage", "copyFileByApk => " + entry);
            if (entry != null && entry.getName().contains(fileName)){
                LogHelper.d("saveGamePlayImage", "copyFileByApk => " + entry.getName());
                InputStream inputStream = zipFile.getInputStream(entry);
                LogHelper.d("saveGamePlayImage", "copyFileByApk => " + inputStream);
                copyFile(inputStream, outPath);
                LogHelper.d("saveGamePlayImage", "copyFileByApk => copyFile end ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream inputStream, String outPath){
        //文件需要覆盖或者文件不存在，则解压文件
        try {
            File file = new File(outPath);
            LogHelper.d("saveGamePlayImage", "copyFile => " + file);
            if (!file.exists()) {
                file.createNewFile();
            }
                //使用1Mbuffer
                byte[] buffer = new byte[1024];
                //解压时字节计数
                int count = 0;
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                LogHelper.d("saveGamePlayImage", "copyFile => " + fileOutputStream);
                while ((count = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
                LogHelper.d("saveGamePlayImage", "copyFile => fileOutputStream close");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 比较版本号大小
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if(TextUtils.isEmpty(version1)&&TextUtils.isEmpty(version2)) {
            return 0;
        }
        if(TextUtils.isEmpty(version1)){
            return -1;
        }
        if(TextUtils.isEmpty(version2)){
            return 1;
        }
        if (version1.equals(version2)) {
            return 0;
        }

        if(!version1.contains(".")&&!version2.contains(".")){
            int v1 = Integer.parseInt(version1);
            int v2 = Integer.parseInt(version2);
            return v1-v2;
        }

        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;

        while (index < minLen && (diff = Integer.parseInt(version1Array[index].trim()) - Integer.parseInt(version2Array[index].trim())) == 0) {
            index ++;
        }

        if (diff == 0) {
            for (int i = index; i < version1Array.length; i ++) {
                if (Integer.parseInt(version1Array[i].trim()) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i ++) {
                if (Integer.parseInt(version2Array[i].trim()) > 0) {
                    return -1;
                }
            }

            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }
}
