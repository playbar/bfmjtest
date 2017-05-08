package com.baofeng.mj.unity.launcher;

/**
 * Created by qiguolong on 2016/5/5.
 * 各类缓存文件配置
 */
public class FileConfig {
    public  final  static  String userCache="userCache";
    // 路径
    private  String userCacheePath;
    public static FileConfig instance;
    public static FileConfig getInstance(){
        if (instance!=null)
            return  instance;
        return  instance=new FileConfig();
    }

    public String getUserCacheePath() {
        return userCacheePath;
    }

    public void setUserCacheePath(String userCacheePath) {
        this.userCacheePath = userCacheePath;
    }
}
