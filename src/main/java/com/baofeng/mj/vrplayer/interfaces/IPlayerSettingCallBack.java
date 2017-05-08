package com.baofeng.mj.vrplayer.interfaces;

/**
 * Created by wanghongfang on 2016/7/20.
 */
public interface IPlayerSettingCallBack {
    void onSettingShowChange(String id,boolean isShow);
    void onHideControlAndSettingView(boolean isHide);//true 隐藏，false 停止隐藏
    void onHDChange(String hd);//清晰度改变
    void onSoundChange(int vm);//0-100
    void isOpenSound(boolean isOpen);//true 关闭静音，false 打开静音
    void onSelected(int num);//当前选中第几集
}
