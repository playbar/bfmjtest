package com.baofeng.mj.business.localbusiness;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.bean.LocalVideoBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.sqlitebusiness.SqliteManager;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.StorageUtilsNew;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.fileutil.AssetUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.ComparatorLong;
import com.baofeng.mj.util.publicutil.ComparatorString;
import com.baofeng.mj.util.publicutil.ImageUtil;
import com.baofeng.mj.util.publicutil.VideoBlackUtil;
import com.baofeng.mj.util.publicutil.VideoExtensionUtil;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;
import com.baofeng.mj.util.threadutil.SqliteProxy;
import com.baofeng.mj.util.threadutil.ThreadPoolUtil;
import com.bn.mojingscaner.MJSCANLib;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author liuchuanchi
 * @description: 本地视频业务
 */
public class LocalVideoBusiness {
    private static LocalVideoBusiness instance;//单例
    private String fileFilter;//规则json
    private String fileTypes;//后缀名json
    private boolean isCreateVideoType;//true正在创建视频类型，false不是
    private boolean isCreateVideoThumbnail;//true正在创建视频缩略图，false不是
    private boolean isBreakThumbnail;
    private LocalVideoBusiness() {
    }

    public static LocalVideoBusiness getInstance(){
        if(instance == null){
            instance = new LocalVideoBusiness();
        }
        return instance;
    }

    /**
     * 查找本地视频
     */
    private void searchLocalVideo(File file, List<LocalVideoBean> videoList) {
        if(file == null || !file.exists()){
            return;
        }
        if(file.isFile()){//是文件
            long fileLength = file.length();
            if(fileLength > FileSizeUtil.MB){//文件大小大于1M
                if(VideoExtensionUtil.fileIsVideo(file)){//是视频文件
//                    if(LocalVideoSearchBusiness.getInstance().needSearch(file)){//需要检索
//
//                    }
                    LocalVideoBean bean = new LocalVideoBean();
                    bean.path = file.getAbsolutePath();//文件路径
                    bean.name = file.getName();//文件名称
                    bean.lastModify = file.lastModified();//文件上次修改时间
                    bean.length = fileLength;//文件大小
                    bean.size = FileSizeUtil.formatFileSize(fileLength);//文件大小
                    videoList.add(bean);
                }
            }
        }else if(file.isDirectory()){//是文件夹
            if(!VideoBlackUtil.fileIsBlack(file)){//文件夹在黑名单之外
                File[] fileList = file.listFiles();//文件夹下所有文件
                if (fileList != null && fileList.length > 0) {
                    for (File f : fileList) {
                        searchLocalVideo(f, videoList);//递归查找视频
                    }
                }
            }
        }
    }

    /**
     * 查找本地视频（通过C层去扫描）
     * @param sortRule 排序规则，0文件上次修改时间排序，1文件名排序，2文件大小排序
     */
    public void searchLocalVideo(final int sortRule, final LocalVideoDataCallback localVideoDataCallback,boolean isYTJ) {
        isBreakThumbnail = false;
//        ThreadPoolUtil.runThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        String[] filePaths = MJSCANLib.getAllMediaFiles(getAllRootPaths(), getFileTypes(), getFileFilter());
        TreeMap treeMap = null;
        if (FileCommonUtil.ruleFileLastModify == sortRule) {//文件上次修改时间排序
            treeMap = new TreeMap<Long, LocalVideoBean>(new ComparatorLong());
            for (String filePath : filePaths) {
                LocalVideoBean localVideoBean = createLocalVideoBean(filePath);//创建LocalVideoBean
                treeMap.put(localVideoBean.lastModify, localVideoBean);
            }
        } else if (FileCommonUtil.ruleFileName == sortRule) {//文件名排序
            treeMap = new TreeMap<String, LocalVideoBean>(new ComparatorString());
            for (String filePath : filePaths) {
                LocalVideoBean localVideoBean = createLocalVideoBean(filePath);//创建LocalVideoBean
                if (treeMap.containsKey(localVideoBean.name)) {//当前key（文件名）已存在
//                    treeMap.put(localVideoBean.name + System.currentTimeMillis(), localVideoBean);
                    treeMap.put(localVideoBean.name + localVideoBean.path, localVideoBean);
                } else {
                    treeMap.put(localVideoBean.name, localVideoBean);
                }
            }
        } else {//文件大小排序
            treeMap = new TreeMap<Long, LocalVideoBean>(new ComparatorLong());
            for (String filePath : filePaths) {
                LocalVideoBean localVideoBean = createLocalVideoBean(filePath);//创建LocalVideoBean
                treeMap.put(localVideoBean.length, localVideoBean);
            }
        }
        if (localVideoDataCallback != null) {
            localVideoDataCallback.callback(treeMap);
        }

        if(!isYTJ){
            createVideoType(treeMap);//创建视频类型
        }

        createVideoThumbnail(treeMap);//创建视频缩略图
    }

    /**
     * 创建LocalVideoBean
     * @param filePath 文件路径
     */
    private LocalVideoBean createLocalVideoBean(String filePath){
        File file = new File(filePath);
        LocalVideoBean bean = new LocalVideoBean();
        bean.path = file.getAbsolutePath();//文件路径
        bean.name = file.getName();//文件名称
        bean.lastModify = file.lastModified();//文件上次修改时间
        bean.length = file.length();//文件大小
        bean.size = FileSizeUtil.formatFileSize(bean.length);//文件大小
        String thumbPath = LocalVideoPathBusiness.getLocalVideoImg(bean.path);//缩略图地址
        bean.thumbPath = FileCommonUtil.filePrefix + thumbPath;
        LogHelper.e("infossss","path=="+bean.path+"==name=="+bean.name+"==lastModify=="+bean.lastModify+"==length=="+bean.length+"==size=="+bean.size+"==thum=="+bean.thumbPath);
        return bean;
    }

    /**
     * 获取所有根路径
     */
    public String getAllRootPaths(){
        JSONObject joPath = new JSONObject();
        try {
            JSONArray jaPath = new JSONArray();
            String[] allRootPaths = FileStorageUtil.getAllStorageDir();//所有根路径
            if(null != allRootPaths){
                for (String rootPath : allRootPaths) {//遍历所有根路径
                    jaPath.put(rootPath);
                }
            }

            for(String rootPath : getAllSdPath()){
                boolean have = false;
                if(null != allRootPaths){
                    for(String path : allRootPaths){
                        if(path.equals(rootPath)){
                            have = true;
                        }
                    }
                }
                if(!have){
                    jaPath.put(rootPath);
                }

            }
            joPath.put("rootDir",jaPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return joPath.toString();
    }

    public List<String> getAllPath(){
        List<StorageUtilsNew.StorageInfo> ss = StorageUtilsNew.getStorageList();
        List<String> list = new ArrayList<>();
        LogHelper.e("infos","ssss=="+ss.size());
        for(StorageUtilsNew.StorageInfo s: ss)
        {
            if(s.path.equals("/storage/emulated/0")){
                continue;
            }
            String  temp = s.path.substring(s.path.lastIndexOf("/"),s.path.length());
            LogHelper.e("infos","temp=="+temp);
            String finalPath = "/storage"+temp;
            list.add(finalPath);

            LogHelper.e("infos","path======"+s.path+"===name==="+finalPath);
        }

        return list;
    }




    /**
     * 获取手机系统中所有被挂载的TF卡，包括OTG等
     *
     * @return
     */
    public static String[] getAllExterSdcardPath()
    {
        List<String> SdList = new ArrayList<String>();

//        String firstPath  ;

        try
        {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null)
            {
                Log.d("", line);
                // 将常见的linux分区过滤掉

                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb"))
                {
                    continue;
                }

                // 下面这些分区是我们需要的
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs")))
                {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1)
                    {
                        String path = items[1].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
//                        LogHelper.e("infos","path=="+path);
                        SdList.add(path);
//                        if (path != null && !SdList.contains(path) && path.contains("sd"))
//                            SdList.add(items[1]);
                    }
                }
            }
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        if (!SdList.contains(firstPath))
//        {
//            SdList.add(firstPath);
//        }
//        LogHelper.e("infos","list.size=="+SdList.size());
        for(String in : SdList){
//            LogHelper.e("infos","in.path==="+in);
        }

        return (String [])SdList.toArray();
    }


    /**
     * 获取规则json
     */
    private String getFileFilter(){
        if(TextUtils.isEmpty(fileFilter)){
            fileFilter = AssetUtil.loadAssetFileAsString("file_filter.json");
        }
        return fileFilter;
    }

    /**
     * 获取后缀名json
     */
    private String getFileTypes() {
        if(TextUtils.isEmpty(fileTypes)){
            fileTypes = AssetUtil.loadAssetFileAsString("fileTypes.json");
        }
        return fileTypes;
    }

    /**
     * 创建视频类型
     */
    private void createVideoType(final TreeMap<String, LocalVideoBean> localVideoMap){
        Log.i("LocalVideoBusiness","createVideoType");
        if(isCreateVideoType){
            Log.i("LocalVideoBusiness","createVideoType直接返回");
            return;//直接返回
        }
        isCreateVideoType = true;//正在创建视频类型
        ThreadPoolUtil.runThread(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Iterator iter = localVideoMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    final LocalVideoBean localVideoBean = (LocalVideoBean) entry.getValue();
                    int videoType = SqliteProxy.getInstance().addProxyExecute(new SqliteProxy.ProxyExecute<Integer>() {
                        @Override
                        public Integer execute() {
                            return SqliteManager.getInstance().getFromLocalVideoType(localVideoBean.path);
                        }
                    });
                    if (VideoTypeUtil.MJVideoPictureTypeUnCreate == videoType) {//当前视频没有创建过视频类型
                        ImageUtil.createVideoType(retriever, localVideoBean.path);//创建视频类型
                    }
                }
                SqliteProxy.getInstance().addProxyRunnable(new SqliteProxy.ProxyRunnable() {
                    @Override
                    public void run() {
                        SqliteManager.getInstance().closeSQLiteDatabase();
                    }
                });
                try {
                    retriever.release();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                isCreateVideoType = false;//创建视频类型结束
            }
        });
    }

    /**
     * 创建视频缩略图
     */
    private void createVideoThumbnail(final TreeMap<String, LocalVideoBean> localVideoMap){
        LogHelper.i("LocalVideoBusiness","createVideoThumbnail");
        LogHelper.e("infos","=======createVideoThumbnail111111111============"+isCreateVideoThumbnail);
        if(isCreateVideoThumbnail){
            LogHelper.i("LocalVideoBusiness","createVideoThumbnail直接返回");
            LogHelper.e("infos","=======createVideoThumbnail22222222============");
            return;//直接返回
        }
        isCreateVideoThumbnail = true;//正在创建视频缩略图
        ThreadPoolUtil.runThread(new Runnable() {
            @Override
            public void run() {
                LogHelper.e("infos","=======createVideoThumbnai233333333============");
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Iterator iter = localVideoMap.entrySet().iterator();
                while (iter.hasNext()) {
                    LogHelper.e("infos","=======isBreakThumbnail============"+isBreakThumbnail);
                    if(isBreakThumbnail){
                        break;
                    }
                    Map.Entry entry = (Map.Entry) iter.next();
                    final LocalVideoBean localVideoBean = (LocalVideoBean) entry.getValue();
                    LogHelper.e("infos","=======localVideoBean============"+localVideoBean.name);
                    ImageUtil.createVideoThumbnail(retriever, localVideoBean.path, 360, 270);//创建视频缩略图
                }
                try {
                    retriever.release();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                LogHelper.e("infos","=======createVideoThumbnai4444444============");
                isCreateVideoThumbnail = false;//创建视频缩略图结束
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if(null != iAndroidCallback){
                        LogHelper.e("infos","=======createVideoThumbnai55555555555============");
                        iAndroidCallback.sendLocalVideoThumbnailEnd();
                    }
                }
            }
        });
    }

    public interface LocalVideoDataCallback{
        void callback(TreeMap localVideoMap);
    }


    public List<String> getAllSdPath(){
        List<String> finalList = new ArrayList<>();
        StorageManager manager = (StorageManager) BaseApplication.INSTANCE.getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method mMethodGetPaths =  manager.getClass().getMethod("getVolumes");
            Object obj = mMethodGetPaths.invoke(manager);
            List<Object> list = (List<Object>) obj;
            for(Object in : list){
                Method type = in.getClass().getMethod("getType");
                Method methodPath = in.getClass().getMethod("getPath");
                if((int)type.invoke(in) == 0){
                   File file = (File) methodPath.invoke(in);
                    String finalPath = file.getAbsolutePath();
                    finalList.add(finalPath);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return  finalList;
    }

    public void setBreakThumbnail(boolean isBreak){
        isBreakThumbnail = isBreak;
    }

    public void searchLocalVideo(final int sortRule) {
        String[] filePaths = MJSCANLib.getAllMediaFiles(getAllRootPaths(), getFileTypes(), getFileFilter());
        LogHelper.e("rootPaths","paths---------"+getAllRootPaths());
        TreeMap treeMap = null;
        if (FileCommonUtil.ruleFileLastModify == sortRule) {//文件上次修改时间排序
            treeMap = new TreeMap<Long, LocalVideoBean>(new ComparatorLong());
            for (String filePath : filePaths) {
                LocalVideoBean localVideoBean = createLocalVideoBean(filePath);//创建LocalVideoBean
                treeMap.put(localVideoBean.lastModify, localVideoBean);
            }
        } else if (FileCommonUtil.ruleFileName == sortRule) {//文件名排序
            treeMap = new TreeMap<String, LocalVideoBean>(new ComparatorString());
            for (String filePath : filePaths) {
                LocalVideoBean localVideoBean = createLocalVideoBean(filePath);//创建LocalVideoBean
                if (treeMap.containsKey(localVideoBean.name)) {//当前key（文件名）已存在
                    treeMap.put(localVideoBean.name + System.currentTimeMillis(), localVideoBean);
                } else {
                    treeMap.put(localVideoBean.name, localVideoBean);
                }
            }
        } else {//文件大小排序
            treeMap = new TreeMap<Long, LocalVideoBean>(new ComparatorLong());
            for (String filePath : filePaths) {
                LocalVideoBean localVideoBean = createLocalVideoBean(filePath);//创建LocalVideoBean
                treeMap.put(localVideoBean.length, localVideoBean);
            }
        }
        createVideoThumbnailNoCall(treeMap);//创建视频缩略图
    }

    private void createVideoThumbnailNoCall(final TreeMap<String, LocalVideoBean> localVideoMap){
        LogHelper.e("infossss","--------createVideoThumbnail111111111--------------");
        ThreadPoolUtil.runThread(new Runnable() {
            @Override
            public void run() {
                LogHelper.e("infossss","-------------createVideoThumbnai233333333----------------");
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Iterator iter = localVideoMap.entrySet().iterator();
                while (iter.hasNext()) {
                    if(isCreateVideoThumbnail){
                        break;
                    }
                    Map.Entry entry = (Map.Entry) iter.next();
                    final LocalVideoBean localVideoBean = (LocalVideoBean) entry.getValue();
                    ImageUtil.createVideoThumbnailNoCall(retriever, localVideoBean.path, 360, 270);//创建视频缩略图
                }
                try {
                    retriever.release();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                LogHelper.e("infossss","---------createVideoThumbnai4444444-----------");
            }
        });
    }


}
