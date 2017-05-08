package com.baofeng.mj.business.downloadutil;

import java.io.File;

/**
 * Created by zhangxiong on 2016/8/24.
 */
public class FileUtils {
    public static boolean exist(String path) {
        if(path==null||path.equals("")){
            return false;
        }
        File file = new File(path);
        return file.exists();
    }


}
