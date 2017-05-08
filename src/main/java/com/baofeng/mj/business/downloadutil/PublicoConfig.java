package com.baofeng.mj.business.downloadutil;

/**
 * Created by qiguolong on 2016/7/20.
 * 全局 config
 */
public interface PublicoConfig {
    /**
     * 类型
     */
   interface ResType{
        int movie=1;                        //在线影视
        int pic=2;                          //全景图片
        int roam=3;                         //全景漫游
        int video=4;                        //全景视频
        int live=5;                         //全景直播
        int apps=6;                         //应用
        int game=7;                         //游戏
        int topic=8;                        //专题
        int fireware = 16;
    }
    /**
     * 剧集类型
     */
   interface CategoryType{
        int nofilm=0;                        // 非影视2D资源
        int film=1;                          // 电影
        int teleplay=2;                      // 电视剧
        int comic=3;                         // 动漫
        int variety=4;                       // 综艺
    }
    /**
     * 暂停原因
     */
    interface Reason{
        int DEFAULT_REASON=1;          //默认暂停
        int NO_PAUSE=2;                //没有暂停
        int LOCAL_PAUSE = 3;           //本地手动暂停或者等待
    }

    /**
     * 解压状态
     */
    interface UnzipState{
        int UNZIP_NOT_EXIS=0;              //不存在
        int DOWN_FINISH_NOT_UNZIP=1;       //下载完 未解压
        int UNZIP_FINISH=2;                //解压完成
        int UNZIPING=3;                    //解压中
    }
    /**
     * 电池电量状态
     */
    interface Battery{
        int BATTERY_STATUS_UNKNOWN=1;       //1 未知
        int BATTERY_STATUS_CHARGING=2;      //2 充电状态
        int BATTERY_STATUS_DISCHARGING=3;   //3 放电中
        int BATTERY_STATUS_NOT_CHARGING=4;  //4 没充电
        int BATTERY_STATUS_FULL=5;          //5 电池满
    }
    /**
     * 缓存类型
     */
    interface CacheType{
        int NETWORK_CACHE=3;                //3、一级缓存（网络）
        int LOCAL_NETWORK_CACHE=2;          //2、二级缓存（本地、网络）
        int MEMORY_LOACL_NETWORK_CACHE=1;   //1、三级缓存（内存、本地、网络）
    }
    /**
     * 直播状态
     */
    interface LiveStatus{
        int NO_BEGIN=1;         //未开始
        int LIVEING=2;          //直播中
        int FINISH=3;           //已结束
        int NOT_ERROR=4;        //未知错误
    }

    /**
     *本地类型
     */
    interface ResTypeLocal{
        int fireware = 16;                  //固件
        int downloading=-2;                 //下载中的文件
    }

}
