package com.baofeng.mj.vrplayer.utils;

/**
 * 应用的 公共常量
 * @author wanghongfang
 * @date 2017-4-17 下午7:02:22
 */
public class GLConst {
	//沉浸模式是否调整Unity
    public static boolean GoUnity = true;

	/**
	 * 深度值
	 */
	public static float Movie_Player_Depth = 18; //影片层深度
	public static float Player_Controler_Depth = 1.6f;//基础控制层
	public static float Player_Settings_Depth = 1.6f; //高级设置层
	public static float Bottom_Menu_Depth = 2f; //底部菜单层
	public static float LockScreen_Depth = 2f;//锁屏
	public static float Dialog_Depth = 1.8f; // 弹窗层深度
	public static float Cursor_Depth = 1.2f;  //瞄点深度

	/**
	 * 放大倍数
	 */
	public static float Movie_Player_Scale = 4.5f;
	public static float Player_Controler_Scale =0.4f;

	public static float Player_Settings_Scale = 0.4f-0.02f; //高级设置层
	public static float Bottom_Menu_Scale =0.6f; //底部菜单层
	public static float LockScreen_Scale = 0.5f;//锁屏
	public static float Dialog_Scale = 0.5f; // 弹窗层深度
	public static float Cursor_Scale = 0.3f;  //瞄点深度

}
