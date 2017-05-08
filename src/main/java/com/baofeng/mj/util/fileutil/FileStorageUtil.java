package com.baofeng.mj.util.fileutil;

import android.app.Activity;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.baofeng.mj.bean.DirFile;
import com.baofeng.mj.business.brbusiness.ExternalStorageReceiver;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.util.diskutil.DiskStatFs;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/5/5.
 * 文件存储工具类
 */
public class FileStorageUtil {
    private static String mojingDir;//mojing存储路径
    private static String downloadDir;//下载存储路径
    private final static  String PACKAGENAME = "com.baofeng.mj";
    /**
     * 重置下载路径
     */
    public static void resetDownloadDir(){
        downloadDir = null;
    }

    /**
     * 获取mojing存储路径
     */
    public static String getMojingDir() {
        if (!TextUtils.isEmpty(mojingDir)) {
            mkdir(mojingDir);//创建mojing存储路径
            return mojingDir;
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            mojingDir = getExternalMojingDir();//外部mojing存储路径
        } else {
            mojingDir = getInternalMojingCacheDir();//内部mojing缓存路径
        }
        mkdir(mojingDir);//创建mojing存储路径
        return mojingDir;
    }

    /**
     * 获取下载存储路径
     */
    public static String getDownloadDir() {
        if (!TextUtils.isEmpty(downloadDir)) {
            mkdir(downloadDir);//创建下载存储路径
            return downloadDir;
        }
        downloadDir = getStorageDir();
        if(TextUtils.isEmpty(downloadDir)){
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                downloadDir = getExternalMojingDir() + "download/";//外部下载存储路径
            } else {
                downloadDir = getInternalMojingCacheDir() + "download/";//内部下载缓存路径
            }
            setStorageDir(downloadDir);
        }
        mkdir(downloadDir);//创建下载存储路径
        return downloadDir;
    }

    /**
     * 获取内部mojing缓存路径
     */
    public static String getInternalMojingCacheDir(){
        String dataDirectory = Environment.getDataDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(dataDirectory).append("/data/com.baofeng.mj/").append(ConfigConstant.STORAGE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }

    /**
     * 获取外部mojing存储路径
     */
    public static String getExternalMojingDir(){
        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(externalStorageDir).append("/").append(ConfigConstant.STORAGE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }

    /**
     * 获取外部mojing缓存路径
     */
    public static String getExternalMojingCacheDir() {
        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return getExternalMojingCacheDir(externalStorageDir);
    }

    /**
     * 获取外部mojing缓存路径
     */
    public static String getExternalMojingCacheDir(String cacheDir) {
        String packageName = BaseApplication.INSTANCE.getPackageName();//包名
        StringBuilder sb = new StringBuilder();
        sb.append(cacheDir).append("/Android/data/").append(packageName).append("/")
          .append(ConfigConstant.STORAGE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }

    /**
     * 获取外部mojing缓存路径,放ota资源供U3D使用
     * ///storage/emulated/0/Android/data/com.baofeng.mj/files/res/
     */
    public static String getExternalMojingFileDir(){
        String packageName = BaseApplication.INSTANCE.getPackageName();//包名
        StringBuilder sb = new StringBuilder();
        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        sb.append(externalStorageDir).append("/Android/data/").append(packageName).append("/")
                .append(ConfigConstant.STORAGE_FILE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }

    /**
     * 获取手机内置，外置的存储根路径集合
     */
    public static String[] getAllStorageDir() {
        try {
            StorageManager mStorageManager = (StorageManager) BaseApplication.INSTANCE.getSystemService(Activity.STORAGE_SERVICE);
            Method mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            return (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取手机内置，外置的下载根路径file集合
     */
    public static List<DirFile> getAllDownloadDirFile() {
        List<DirFile> dirFileList = new ArrayList<DirFile>();
        String externalStorageDir = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File externalStorageFile = Environment.getExternalStorageDirectory();
            externalStorageDir = externalStorageFile.getAbsolutePath();
            long[] fileSizeArr = DiskStatFs.getStatFsAll(externalStorageDir);
            DirFile dirFile = new DirFile();
            dirFile.setDirName("内部存储卡");
            dirFile.setTotalSize(fileSizeArr[0]);
            dirFile.setAvilableSize(fileSizeArr[1]);
            dirFile.setUsedSize(fileSizeArr[2]);
            String dirPath = getExternalMojingDir() + "download";
            mkdir(dirPath);
            dirFile.setDirFile(new File(dirPath));
            dirFileList.add(dirFile);
        }
        String[] storageDirArr = getAllStorageDir();//获取手机内置，外置的存储根路径
        if(storageDirArr != null && storageDirArr.length > 0){
            int sdCount = 0;//sd卡计数
            for(int i = 0; i < storageDirArr.length; i++){
                if (!TextUtils.isEmpty(externalStorageDir) && externalStorageDir.equals(storageDirArr[i])) {
                    continue;//内部存储卡
                }
                File tempFile = new File(storageDirArr[i]);
                if(tempFile != null && tempFile.exists() && tempFile.isDirectory()){
                    long[] fileSizeArr = DiskStatFs.getStatFsAll(storageDirArr[i]);
                    if(fileSizeArr[0] > FileSizeUtil.MB * 100){//文件总大小大于100MB
                        DirFile dirFile = new DirFile();
                        sdCount++;
                        if(sdCount >= 2){
                            dirFile.setDirName("外部SD存储卡" + sdCount);
                        }else{
                            dirFile.setDirName("外部SD存储卡");
                        }
                        dirFile.setTotalSize(fileSizeArr[0]);
                        dirFile.setAvilableSize(fileSizeArr[1]);
                        dirFile.setUsedSize(fileSizeArr[2]);
                        String dirPath = getExternalMojingCacheDir(storageDirArr[i]) + "download";
                        mkdir(dirPath);
                        dirFile.setDirFile(new File(dirPath));
                        dirFileList.add(dirFile);
                    }
                }
            }
        }
        return dirFileList;
    }

    /**
     * 创建根目录
     */
    public static void mkdir(String filePath){
        File dirFile = new File(filePath);
        if(!dirFile.exists()){
            dirFile.mkdirs();//mkdir()不能创建多个目录，所以要用mkdirs()
        }
    }

    /**
     * 设置下载存储模式 0:内部存储，1:外部存储1,2:外部存储2,3:外部存储3.......
     */
    public static void setStorageMode(int mode) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".downloadMode");
        FileCommonUtil.writeFileString(String.valueOf(mode), file);
    }

    /**
     * 获取下载存储模式 0:内部存储，1:外部存储1,2:外部存储2,3:外部存储3.......
     */
    public static int getStorageMode() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".downloadMode");
        String storageMode = FileCommonUtil.readFileString(file);
        if(TextUtils.isEmpty(storageMode)){
            return 0;
        }
        return Integer.valueOf(storageMode);
    }

    /**
     * 设置下载存储根路径
     * @param storageDir
     */
    public static void setStorageDir(String storageDir) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".downloadDir");
        FileCommonUtil.writeFileString(storageDir, file);
    }

    /**
     * 获取下载存储根路径
     * @return
     */
    public static String getStorageDir() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".downloadDir");
        return FileCommonUtil.readFileString(file);
    }

    /**
     * 设置保存的版本号
     */
    public static void setSavedVersionCode(int savedVersionCode, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
        FileCommonUtil.writeFileString(String.valueOf(savedVersionCode), file);
    }

    /**
     * 获取保存的版本号
     */
    public static int getSavedVersionCode(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
        String savedVersionCode = FileCommonUtil.readFileString(file);
        if(TextUtils.isEmpty(savedVersionCode)){
            return 0;
        }
        return Integer.valueOf(savedVersionCode);
    }

    /**
     * 检查下载路径是否有效
     * @return true有效，false无效
     */
    public static void checkDownloadDir(){
        int storageMode = getStorageMode();
        if(storageMode > 0){//外部存储
            try{
                String storageDir = getStorageDir();
                mkdir(storageDir);
                if(!new File(storageDir).exists()){//当前下载路径不存在
                    ExternalStorageReceiver.changeDownloadDir();//改变下载存储路径
                }
            }catch (Exception exception){
            }
        }
    }

    /**
     * 获取飞屏字幕缓存路径
     * @return
     */
    public static String getMJFlyScreenSubFile(){
        return getExternalMojingDir()+"FlyScreenSubtitle/";
    }

    /**
     * 判断是否有足够的空间供下载
     *
     * @return
     */
    public static long getEnoughSDSize()
    {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
                .getAbsolutePath());

        //sd卡分区数
        int blockCounts = statFs.getBlockCount();

        //sd卡可用分区数
        int avCounts = statFs.getAvailableBlocks();

        //一个分区数的大小
        long blockSize = statFs.getBlockSize();

        //sd卡可用空间
        long spaceLeft = avCounts * blockSize;

        return spaceLeft;
    }

    public static String getInternalMojingdownloadDir(){
        String dataDirectory = Environment.getDataDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(dataDirectory).append("/data/com.baofeng.mj/").append(ConfigConstant.STORAGE_DIR);
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }






}
