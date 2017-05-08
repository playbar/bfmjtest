package com.baofeng.mj.util.systemutil;


import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baofeng.mj.business.publicbusiness.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by wanghongfang on 2015/12/30.
 */
public class UUidUtil {

    private static final String INSTALLATION = ".SYSTEM.ID";
    private String uuid = null;
    private String path = "";
    private static UUidUtil mInstance;

    private String initUUid() {
        final TelephonyManager tm = (TelephonyManager) BaseApplication.INSTANCE
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId, androidId, cpuSerial;

        String tmDevice = tm.getDeviceId();
        deviceId = tmDevice != null && !TextUtils.isEmpty(tmDevice) && !(tmDevice.contains("*")) ? UUID
                .nameUUIDFromBytes(tmDevice.getBytes(Charset.forName("utf-8"))).toString() : UUID
                .randomUUID().toString();

        androidId = ""
                + android.provider.Settings.Secure.getString(
                BaseApplication.INSTANCE.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        if ("9774d56d682e549c".equals(androidId) || TextUtils.isEmpty(androidId)) {
            androidId = UUID.randomUUID().toString();
        }
        cpuSerial = "" + getCPUSerial();
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) deviceId.hashCode() << 32) | cpuSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    private UUidUtil() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            path = sdDir;

        } else {
            String romString = Environment.getDataDirectory().getAbsolutePath();
            path = romString + "/data/";
        }
        File installation = new File(path, INSTALLATION);
        writeUUIDFile(installation);

    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位)
     * 读取失败为"0000000000000000"
     */
    private  String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1,
                                str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    /**
     * 读取存储的uuid
     * @param installation
     * @return
     * @throws IOException
     */
    private String readUUIDFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    /**
     * 将生成的uuid存储 下次使用时直接从文件中取
     * @param installation
     */
    private void writeUUIDFile(File installation) {
        try {
            FileOutputStream out = new FileOutputStream(installation);
            uuid = initUUid();
            out.write(uuid.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UUidUtil getInstance() {
        if (mInstance == null) {
            mInstance = new UUidUtil();
        }
        return mInstance;
    }

    /**
     * 对外提供的获取uuid的方法
     * @return
     */
    public synchronized String getUUID() {
        if (uuid == null) {
            File installation = new File(path, INSTALLATION);
            try {
                if (!installation.exists())
                    writeUUIDFile(installation);
                uuid = readUUIDFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return uuid;
    }

}

