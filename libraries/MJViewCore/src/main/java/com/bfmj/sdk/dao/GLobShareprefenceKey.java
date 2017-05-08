package com.bfmj.sdk.dao;

/**
 * ClassName: GLobShareprefenceKey <br/>
 * 
 * @author qiguolong
 * @date: 2015-7-15 下午7:41:46 <br/>
 * @description: 将通用的ShareprefenceKey 放在统一
 */
public interface GLobShareprefenceKey {
	// 头控开启
	public static final String HEADCONTROL = "head_control";

	/** 魔镜市场-本地-设置-解码器 key , 解码方式：软件解码、硬件解码、魔镜智能解码 */
	public static final String RES_LOCAL_SETTING_DECODER = "res_local_setting_decoder";
	/** 魔镜市场-本地-设置-解码器 value：软件解码 */
	public static final String RES_LOCAL_SETTING_DECODER_SOFT = "0";
	/** 魔镜市场-本地-设置-解码器 value：硬件解码 */
	public static final String RES_LOCAL_SETTING_DECODER_HARD = "1";
	/** 魔镜市场-本地-设置-解码器 value：魔镜智能解码 */
	public static final String RES_LOCAL_SETTING_DECODER_MJ = "2";
	/** 魔镜市场-本地-设置-字幕规则 key：默认加载字幕 */
	public static final String RES_LOCAL_SETTING_LOAD_SUBTITLES = "res_local_setting_load_subtitles";
	/**当前选择的眼镜*/
	public static final String PREF_PUBLIC_GLASSES_MODO_KEY ="pref_public_glasses_mode_key";
	/** 境内锁屏角度 */
	public static final String PREF_PUBLIC_LOCKSCREEN_ANGLE = "pref_public_lockscreen_angle";
	/** 陀罗仪开关 **/
	public static final String PREF_PUBLIC_VIEWCORE_GROY_ENABLE = "pref_public_viewcore_Groy_enable";
	/** 陀罗仪开关 手动设置 **/
	public static final String PREF_PUBLIC_VIEWCORE_GROY_CHANGE = "pref_public_viewcore_Groy_Change";
	/** 播放场景开关灯状态 */
	public static final String PREF_PUBLIC_PLAYSENCE_LIGHT_STATE = "pref_public_playsence_light_state";

	public static final String PLAY_LIGHT_MODEL = "light_model";
	public static final String PLAY_PAGE_LIGHT_VALUE = "play_page_light_value";
	public static final String SCENCE_PAGE_LIGHT_VALUE = "scence_page_light_value";

	/** 保存的增强模式 */
	public static final String PREF_PUBLIC_SETTING_STRONG_MODE = "pref_public_setting_strong_mode";

	/** 加载so开关**/
	public static final String STORM_IS_RELOAD_LIB_SO = "storm_is_reload_lib_so";

	public static final String PUBLIC_GLASSES_IDS_PREFENCE = "public_glasses_ids_prefence";
}
