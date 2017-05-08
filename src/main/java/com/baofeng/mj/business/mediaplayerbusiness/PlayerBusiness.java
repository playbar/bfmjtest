package com.baofeng.mj.business.mediaplayerbusiness;

import com.baofeng.mj.business.videoplayer.VideoPlayerManager;
import com.baofeng.mj.util.systemutil.AudioManagerUtil;

/**
 * Created by zhaominglei on 2016/8/22.
 */
public class PlayerBusiness {

    //遥控器ok
    public int KEY_BLUETOOTH_OK = 66;
    //游戏手柄
    public int KEY_JOYSTICK_OK = 96;
    //遥控器长按
    public int KEY_BLUETOOTH_LONG_PRESS = 23;
    //遥控器长按左键
    public int KEY_BLUETOOTH_LEFT = 21;
    //遥控器长按右键
    public int KEY_BLUETOOTH_RIGHT = 22;
    //遥控器上键
    public int KEY_BLUETOOTH_UP = 19;
    //遥控器下键
    public int KEY_BLUETOOTH_DOWN = 20;
    //遥控器返回键
    public int KEY_BLUETOOTH_BACK = 4;
    //手柄返回键
    public int KEY_JOYSTICK_BACK = 97;

    private int STEP = 5;

    private static PlayerBusiness instance;


    private PlayerBusiness() {

    }

    public static PlayerBusiness getInstance() {
        if (instance == null) {
            instance = new PlayerBusiness();
        }
        return instance;
    }


    /***
     * 快进
     *
     * @param manager
     */
    public void fastForward(VideoPlayerManager manager) {
        if (manager == null) {
            return;
        }
        int curpos = manager.getCurPos();
        if (curpos < 0) {
            return;
        }
        int totalTime = manager.getDuration();
        if (curpos < totalTime - 5000) {
            curpos += 5000;//
        } else {
            curpos = totalTime - 5000;
        }
        manager.playSeek(curpos);
    }

    /***
     * 快退
     *
     * @param manager
     */
    public void rewind(VideoPlayerManager manager) {
        if (manager == null) {
            return;
        }
        int curpos = manager.getCurPos();
        if (curpos < 0) {
            return;
        }
        if (curpos > 5000) {
            curpos -= 5000;
        } else {
            curpos = 0;
        }
        manager.playSeek(curpos);
    }

    /***
     * 增大音量
     */
    public void increaseVolume() {
        int curVolume = AudioManagerUtil.getInstance().getStreamCurrentVolume();
        int maxVolume = AudioManagerUtil.getInstance().getStreamMaxVolume();
        curVolume += STEP;
        if (curVolume >= maxVolume) {
            curVolume = maxVolume;
        }
        AudioManagerUtil.getInstance().setStreamCurrentVolume(curVolume);
    }

    /***
     * 减小音量
     */
    public void decreaseVolume() {
        int curVolume = AudioManagerUtil.getInstance().getStreamCurrentVolume();
        if (curVolume > STEP) {
            curVolume -= STEP;
        } else {
            curVolume = 0;
        }
        AudioManagerUtil.getInstance().setStreamCurrentVolume(curVolume);
    }

    public void setPlayerTime(VideoPlayerManager manager, int downKeyValue) {
        if (downKeyValue == KEY_BLUETOOTH_LEFT) {
            rewind(manager);
        } else if (downKeyValue == KEY_BLUETOOTH_RIGHT) {
            fastForward(manager);
        }
    }

}
