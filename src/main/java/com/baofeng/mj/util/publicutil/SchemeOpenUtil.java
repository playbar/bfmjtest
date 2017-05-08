package com.baofeng.mj.util.publicutil;

/**
 * Created by liuchuanchi on 2016/6/23.
 * scheme协议打开魔镜app工具类
 * ##场景举例：
 1. 打开魔镜
 mojing://?action=open
 2. 打开魔镜并打开特定资源详情页
 mojing://?action=open&type=1&playurl=http://xxxx.js
 3. 打开魔镜并打开特定专题页
 mojing://?action=open&type=4&playurl=http://xxxx.js
 4. 打开魔镜并播放特定资源详情页
 mojing://?action=play&type=3&playurl=http://xxxx.js&resource=1 （播放官网2d视频）
 mojing://?action=play&type=3&playurl=http://xxxx.mp4&resource=2 （播放外链普通2d视频）
 mojing://?action=play&type=2&playurl=http://xxxx.jpg&resource=2 （播放外链全景图片）
 mojing://?action=play&type=1&playurl=http://xxxx.mp4&resource=2 (播放外链全景视频)
 5. 打开魔镜并下载特定的资源（游戏或视频）
 mojing://?action=download&type=5&playurl=http://xxxx.js&resource=1
 6. 返回第三方应用逻辑
 若调用参数中fromscheme和fromappname值不为空，则在资源播放结束或中断时，弹框提示用户，允许用户选择「留在魔镜」或「返回」
 */
public class SchemeOpenUtil {
    /**
     * 资源类型
     */
    public static final String type_panorama = "1";//全景视频
    public static final String type_image = "2";//全景图片
    public static final String type_video_2d_3d = "3";//普通2d,3d视频
    public static final String type_special = "4";//专题
    public static final String type_game = "5";//游戏
    public static final String type_app = "6";//应用
    /**
     * 打开方式
     */
    public static final String action_open = "open";//打开竖屏
    public static final String action_open_vr = "openvr";//打开横屏
    public static final String action_play = "play";//播放（默认值）
    public static final String action_download = "download";//下载
    /**
     * 来源
     */
    public static final String resource_official = "1";//官方资源
    public static final String resource_out = "2";//外部资源（默认值）

    /**
     * 获取资源类型
     * @param schemeType scheme类型
     * @return 资源类型
     */
    public static int getResType(String schemeType){
        if(SchemeOpenUtil.type_game.equals(schemeType)){//游戏
            return ResTypeUtil.res_type_game;
        }else if(SchemeOpenUtil.type_app.equals(schemeType)){//应用
            return ResTypeUtil.res_type_apply;
        }else if(SchemeOpenUtil.type_special.equals(schemeType)){//专题
            return ResTypeUtil.res_type_special;
        }else if(SchemeOpenUtil.type_panorama.equals(schemeType)){//全景视频
            return ResTypeUtil.res_type_video;
        }else if(SchemeOpenUtil.type_image.equals(schemeType)){//全景图片
            return ResTypeUtil.res_type_image;
        }else if(SchemeOpenUtil.type_video_2d_3d.equals(schemeType)){//普通2d,3d视频
            return ResTypeUtil.res_type_movie;
        }
        return 0;
    }
}
