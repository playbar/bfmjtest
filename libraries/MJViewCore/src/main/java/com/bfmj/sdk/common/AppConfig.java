package com.bfmj.sdk.common;

import android.os.Environment;

/**
 * 应用配置文件
 * @author yanzw
 * @date 2014-5-4 下午7:02:09
 */
public class AppConfig {
	
	public static final String SD_DIR =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	public static final String SAVE_DIR = SD_DIR + "sdkdemo/";
	public static final String SAVE_IMAGE_FILE =  SAVE_DIR + "image.jpg";

	public static final String SAVE_VIDEO_FILE =  SAVE_DIR + "video.mp4";
	public static final String SAVE_ROAM_DIR =  SAVE_DIR + "roma/";

	public static final String PACKAGE_NAME =  "cn.mojing.playsdk";

	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	public static final int STOP = 2;

	public static final String SER_KEY_LOCAL = "ser_key_local";  //本地模式
	public static final String SER_KEY_ONLINE = "ser_key_online"; //在线模式
}
