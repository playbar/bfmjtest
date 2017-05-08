package com.baofeng.mj.business.downloadutil;

import android.content.Context;
import android.util.SparseArray;

import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhangxiong on 2016/8/26.
 */
public class DownLoadItemUtils implements PublicoConfig.ResType {
    public static SparseArray resDownloadPathMap = new SparseArray<String>();

    static {
        resDownloadPathMap.put(video, "video");
        resDownloadPathMap.put(roam, "roam");
        resDownloadPathMap.put(pic, "image");
        resDownloadPathMap.put(apps, "app");
        resDownloadPathMap.put(game, "game");
        resDownloadPathMap.put(movie, "movie");
        resDownloadPathMap.put(fireware,"fireware");
    }




    /**
     * 根据resId判断该资源是否已经在下载队列中
     *
     * @param resId
     * @return
     */
    public static DownloadItem getDownLoadItem(Context context, String resId) {
        ArrayList<DownloadItem> downloadItems = DownloadUtils.getInstance().getAllDownLoadings(context);
        if (downloadItems != null && downloadItems.size() > 0) {
            for (DownloadItem item : downloadItems) {
                if (item.getAid().equals(resId)) {
                    return item;
                }
            }
        }
        return null;
    }



    public static String getDowloadDicFromType(int type, String aid, String url) {
        LogHelper.e("infos","downloadDic=="+Common.getDownloadDic() + File.separator + resDownloadPathMap.get(type, "others") + "/" + aid + Common.getFileSuffix(url)+"==suffix=="+Common.getFileSuffix(url));
        return Common.getDownloadDic() + File.separator + resDownloadPathMap.get(type, "others") + "/" + aid + Common.getFileSuffix(url);
    }

    /**
     * 判断存在
     *
     * @param downloadItem
     * @return
     */
    public static boolean isFileExist(DownloadItem downloadItem) {
        if (downloadItem == null) {
            return false;
        }
        return isFileExist(downloadItem.getFileDir(), downloadItem.getTitle(),
                FileCommonUtil.getFileSuffix(downloadItem.getHttpUrl()),
                downloadItem.getAid(), downloadItem.getDownloadType(), downloadItem.getDownloadState());
    }


    public static boolean isDownLoadingFileExist(DownloadItem downloadItem) {
        if (downloadItem == null) {
            return false;
        }
        return isDownLoadingFileExist(downloadItem.getFileDir(), downloadItem.getTitle(),
                Common.getFileSuffix(downloadItem.getHttpUrl()),
                downloadItem.getAid(), downloadItem.getApkInstallType());
    }

    public static boolean isDownLoadingFileExist(String fileDir, String title,
                                                 String suffix, String id, int type) {
        boolean isFileExist = false;
        if (type == roam) {// 漫游
            isFileExist = new File(fileDir, title + suffix).exists();
        } else if (type == video) { // 现场

            if (Common.fileExist(fileDir, title, suffix)) {
                isFileExist = true;
            } else {
                isFileExist = false;
            }


        } else if (type == pic) {// 全景图片

            if (Common.fileExist(fileDir, title, suffix)) {
                isFileExist = true;
            } else {
                isFileExist = false;
            }

        }
        return isFileExist;
    }


    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean isFileExist(String fileDir, String title,
                                      String suffix, String id, int type, int downLoadStatus) {
        boolean isFileExist = false;
        if (downLoadStatus == MjDownloadStatus.COMPLETE) {
            LogHelper.e("infosss","type=="+type+"==title=="+title);
            if (type == roam) {// 漫游
                String _fileDir = fileDir + "/" + id;
                File _file = new File(_fileDir);

                if (_file.exists()) {
                    isFileExist = true;
                } else {
                    // isFileExist = false;
                    isFileExist = new File(fileDir, title + suffix).exists();
                }


            } else if (type == video || type == movie) { // 现场

                if (Common.fileExist(fileDir, title, suffix)) {
                    isFileExist = true;
                } else {
                    isFileExist = false;
                }


            } else if (type == pic) {// 全景图片

                if (Common.fileExist(fileDir, title, suffix)) {
                    isFileExist = true;
                } else {
                    isFileExist = false;
                }

            } else if(type == game){
                if (Common.fileExist(fileDir, title, suffix)) {
                    isFileExist = true;
                } else {
                    isFileExist = false;
                }
            }
        } else {
            isFileExist = false;
        }
        return isFileExist;
    }


    /**
     * 查看 漫游文件状态
     *
     * @param
     * @param
     * @param
     * @param
     * @return 0不存在 1下载完 未解压 2解压完毕 3 解压中
     */
//    public static int checkRoamFIle(String fileDir, String title,
//                                    String suffix, String id, String url, int downLoadStatus) {
//        if (downLoadStatus == MjDownloadStatus.COMPLETE) {
//            if (UnZipUtil.isUnziping(url))
//                return 3;
//            String _fileDir = fileDir + "/" + id;
//
//            File _file = new File(_fileDir);
//            if (_file.exists() && _file.isDirectory()) {
//                return 2;
//            } else {
//                // isFileExist = false;
//
//                return new File(fileDir, title + suffix).exists() ? 1 : 0;
//
//            }
//
//        } else {
//            return 0;
//        }
//    }

//    public static int checkRoamFIle(DownloadItem downloadItem) {
//        return checkRoamFIle(downloadItem.getFileDir(), downloadItem.getTitle(),
//                downloadItem.getSite(), downloadItem.getAid(), downloadItem.getHttpUrl()
//                , downloadItem.getDownLoaderStatus()
//        );
//    }

    public static String getDowloadDicFromType(int type) {
        return Common.getDownloadDic() + File.separator + resDownloadPathMap.get(type, "others");
    }

    /**
     * 获取解压之后的位置
     *
     * @param item
     * @return
     */
    public static String getUnzipPath(DownloadItem item) {
        return item.getFileDir() + "/" + item.getAid();
    }

    public static boolean getdownloadPath(int type, String title) {
        File rangefile = new File(getDowloadDicFromType(type) + File.separator
                + title);
        return rangefile.exists();
    }

    /**
     * 获取下载路径 漫游解压之前
     *
     * @param item
     * @return
     */
    public static String getDownloadPath(DownloadItem item) {
        return new File(item.getFileDir(), item.getTitle() + item.getSite()).getAbsolutePath();
    }

    /**
     * 转换字节
     *
     * @param size
     * @return
     */
    public static String convertFileSize(long size) {
        final long kb = 1024;
        final long mb = kb * 1024;
        final long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            final float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            final float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

}
