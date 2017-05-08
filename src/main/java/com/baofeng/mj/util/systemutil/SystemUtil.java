package com.baofeng.mj.util.systemutil;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.baofeng.mj.business.publicbusiness.BaseApplication;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liuchuanchi on 2016/5/21.
 * 系统工具类
 */
public class SystemUtil {
    /**
     * 获取cpu型号
     * @return
     */
    public static String getCpuType(){
        String strInfo = getCpuInfo();
        String strType = null;
        if (strInfo.contains("ARMv5")) {
            strType = "armv5";
        } else if (strInfo.contains("ARMv6")) {
            strType = "armv6";
        } else if (strInfo.contains("ARMv7")) {
            strType = "armv7";
        } else if (strInfo.contains("Intel")){
            strType = "x86";
        }else{
            strType = "unknown";
            return strType;
        }
        if (strInfo.contains("neon")) {
            strType += "_neon";
        }else if (strInfo.contains("vfpv3")) {
            strType += "_vfpv3";
        }else if (strInfo.contains(" vfp")) {
            strType += "_vfp";
        }else{
            strType += "_none";
        }
        return strType;
    }

    /**
     * 获取cpu信息
     * @return
     */
    public static String getCpuInfo(){
        if(Build.CPU_ABI.equalsIgnoreCase("x86")){
            return "Intel";
        }
        try {
            byte[] bs = new byte[1024];
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            reader.read(bs);
            String ret = new String(bs);
            int index = ret.indexOf(0);
            if(index != -1) {
                return ret.substring(0, index);
            } else {
                return ret;
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获取运行平台
     * @return
     */
    public static String getRunningPlatform() {
        try {
            PackageManager pm = BaseApplication.INSTANCE.getPackageManager();
            String packageName = BaseApplication.INSTANCE.getPackageName();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Object value = ai.metaData.get("running_platform");
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }
}
