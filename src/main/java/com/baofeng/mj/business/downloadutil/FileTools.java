package com.baofeng.mj.business.downloadutil;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.mojing.dl.domain.DownloadItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by yushaochen on 2016/8/2.
 */
public class FileTools {
    /**
     * 获取真正的视频地址
     *
     * @param activity
     * @param path     content://media/external/video/media/1711
     * @return 返回 /storage/emulated/0/阿波罗11号3D.mp4
     */
    public static String getRealVideoPath(Activity activity, String path) {
        if (activity == null || TextUtils.isEmpty(path)) {
            return path;
        } else if (path.startsWith("content://")) {
            String[] proj = {MediaStore.Video.Media.DATA};
            Cursor cursor = activity.managedQuery(Uri.parse(path), proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                String realPath = cursor.getString(column_index);
                cursor.close();
                return realPath;
            } else {
                return path;
            }
        } else {
            return path;
        }
    }

    /**
     * 获取文件 直接为java对象
     *
     * @param path 路径
     * @description:{序列化读文件
     */
    public static Serializable readSerFile(String path) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(path));
            ois = new ObjectInputStream(fis);
            final Serializable o = (Serializable) ois.readObject();
            ois.close();
            fis.close();
            return o;
        } catch (final Exception e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param o    转化的对象
     * @param path
     * @description:{序列化写文件
     */
    public static void writeSerFile(Serializable o, String path) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(new File(path));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.close();
            fos.close();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != oos) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /***
     * 读取下载中的文件
     *
     * @return
     */
    public static ArrayList<DownloadItem> readDownLoadingFile(boolean isStartApp) {
        ArrayList<DownloadItem> downLoading = null;
        Object obj = readSerFile(Common.getDownloadDic() + "/.downloading.info");
        if (obj instanceof ArrayList) {
            downLoading = (ArrayList<DownloadItem>) obj;
        } else if (obj instanceof HashMap) {
            HashMap<Long, DownloadItem> hashMap = (HashMap<Long, DownloadItem>) obj;
            Set<Long> Longs = hashMap.keySet();
            for(Long keys:Longs){
                for(DownloadItem downItem:downLoading){
                    if(!downItem.getAid().equals(hashMap.get(keys).getAid())){
                        hashMap.get(keys).setId(keys);
                        downLoading.add(hashMap.get(keys));
                    }
                }
            }
            File f = new File(Common.getDownloadDic() + "/.downloading.info");
            f.delete();
        }
        if (isStartApp) {
            DownloadUtils.getInstance().setDownLoadDate(downLoading);
        }
        return downLoading;
    }

    /**
     * 读取等待中的文件
     *
     * @return
     */
    public static ArrayList<DownloadItem> readWaittingFile(boolean isSatrtApp) {
        ArrayList<DownloadItem> waitInfos = (ArrayList<DownloadItem>) readSerFile(Common.getDownloadDic() + "/.waitting.info");
        if (isSatrtApp) {
            DownloadUtils.getInstance().setWaittingDate(waitInfos);
        }
        return waitInfos;
    }

    /**
     * 写入下载中的文件
     *
     * @param mDownLoadings
     */
    public static void writeDownLoadingFile(ArrayList<DownloadItem> mDownLoadings) {
        if (mDownLoadings != null) {
            writeSerFile(mDownLoadings, Common.getDownloadDic() + "/.downloading.info");
        }
    }

    /**
     * 写入等待中的文件
     *
     * @param waittingInfos
     */
    public static void writeWaittingFile(ArrayList<DownloadItem> waittingInfos) {
        if (waittingInfos != null) {
            writeSerFile(waittingInfos, Common.getDownloadDic() + "/.waitting.info");
        }
    }

    public static void readDownLoadWaitingFile() {
        readDownLoadingFile(true);
        readWaittingFile(true);
    }
}
