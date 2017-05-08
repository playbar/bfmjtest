package com.baofeng.mj.util.publicutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/5/22.
 * 视频黑名单工具类
 */
public class VideoBlackUtil {
    private static List<String> blackList;//黑名单list

    /**
     * 文件是不是黑名单
     */
    public static boolean fileIsBlack(File file){
        String filePath = file.getAbsolutePath();//文件路径
        if(blackList == null){
            initBlackList();
        }
        for(String black : blackList){
            if(filePath.contains(black)){
                return true;//文件是黑名单
            }
        }
        return false;
    }

    /**
     * 初始化黑名单list
     */
    private static void initBlackList(){
        blackList = new ArrayList<String>();
        blackList.add("tencent");//腾讯文件夹
        blackList.add("com.qiyi.video");//爱奇艺包名
        blackList.add("com.tencent.qqlive");//腾讯视频包名
        blackList.add("com.tencent.mm");//微信包名
        blackList.add("mojing");//mojing文件夹
    }
}
