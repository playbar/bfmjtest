package com.baofeng.mj.business.publicbusiness;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/7/8.
 * 文件检索业务（基类）
 */
public abstract class FileSearchBusiness {
    public HashMap<String,Long> fileSearchMap;//文件检索map

    /**
     * true需要检索，false不需要检索
     */
    public boolean needSearch(File file){
        String filePath = file.getAbsolutePath();//文件路径
        long lastModified = file.lastModified();//文件上次修改时间
        if(getFileSearchMap().containsKey(filePath)){
            if(getFileSearchMap().get(filePath) == lastModified){
                return false;//文件上次修改时间相等，不用检索
            }
        }
        getFileSearchMap().put(filePath, lastModified);
        return true;
    }

    /**
     * 读取fileSearchMap
     */
    public HashMap<String,Long> getFileSearchMap(){
        if(fileSearchMap == null){
            Serializable serializable = readFileSearchMap();
            if(serializable == null){
                fileSearchMap = new HashMap<String,Long>();
            }else{
                fileSearchMap = (HashMap<String,Long>)serializable;
            }
        }
        return fileSearchMap;
    }

    /**
     * 检查fileSearchMap
     */
    public void checkFileSearchMap(){
        List<String> filePathDeleteList = new ArrayList<String>();
        Iterator iter = getFileSearchMap().keySet().iterator();
        while (iter.hasNext()) {
            String filePath = (String)iter.next();
            if(!new File(filePath).exists()){//文件不存在
                filePathDeleteList.add(filePath);
            }
        }
        for (String filePath : filePathDeleteList){
            getFileSearchMap().remove(filePath);
        }
        writeFileSearchMap();//写入fileSearchMap
    }

    /**
     * 重置fileSearchMap
     */
    public void resetFileSearchMap(){
        writeFileSearchMap();//写入fileSearchMap
        if(fileSearchMap != null){
            fileSearchMap.clear();
            fileSearchMap = null;
        }
    }

    /**
     * 读取fileSearchMap
     */
    public abstract Serializable readFileSearchMap();

    /**
     * 写入fileSearchMap
     */
    public abstract void writeFileSearchMap();
}
