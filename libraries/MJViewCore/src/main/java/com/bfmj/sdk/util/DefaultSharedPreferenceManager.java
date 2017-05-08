package com.bfmj.sdk.util;


import android.content.Context;
import android.text.TextUtils;

import com.bfmj.sdk.common.App;
import com.bfmj.sdk.dao.GLobShareprefenceKey;

public class DefaultSharedPreferenceManager implements GLobShareprefenceKey{

	public SharedPreferencesUtil mySharedPreferences;
	public static DefaultSharedPreferenceManager instance;
	private Context mContext;
	private DefaultSharedPreferenceManager(Context context) {
		mContext = context;
		mySharedPreferences = new SharedPreferencesUtil(mContext);
	}

	public static DefaultSharedPreferenceManager getInstance(Context context) {
		if (instance == null) {
			instance = new DefaultSharedPreferenceManager(context);
		}
		return instance;
	}
    /**
     * 获取保存的当前眼镜的key
     * @return {type:key} //type:1 手动选择 type:2 扫码选择 key:进入魔镜的key
     */
    public String getGlassesModeKey() {
        String key =  mySharedPreferences.getString(
				GLobShareprefenceKey.PREF_PUBLIC_GLASSES_MODO_KEY,getDefaultGlassesKey(mContext) );
        return key;
    }

    /**
     * 保存当前选择的眼镜key
     * @param key 进入魔镜的key
     */
    public void setGlassesModeKey(String key) {
		String ids = GlassesManager.getInstance(App.getInstance()).getGameGlassesParams(key);
		setGlassesIds(ids);
        mySharedPreferences.setString(GLobShareprefenceKey.PREF_PUBLIC_GLASSES_MODO_KEY,
                key);
    }
	public static String getDefaultGlassesKey(Context context){
		String glassesType = Common.getChannelCode(context, "GLASSES_TYPE");
		if("0".equals(glassesType)){

		}else if("1".equals(glassesType)){
			return GlassesManager.MJ3_A_96_KEY;
		}else if("2".equals(glassesType)){
			return GlassesManager.MJ3_B_60_KEY;
		}else if("3".equals(glassesType)){
			return GlassesManager.MJ4_KEY;
		}
		return GlassesManager.MJ2_KEY;
	}

	public void setHeadContol(boolean isopen) {
		mySharedPreferences.setBoolean(HEADCONTROL, isopen);
	}

	public boolean getHeadControl() {
		return mySharedPreferences.getBoolean(HEADCONTROL, false);
	}

	/**
	 * @author wanghongfang @Date 2015-7-9 上午10:35:53 description:镜内锁屏角度
	 * @param {引入参数名  {引入参数说明}
	 * @return 设定的锁屏角度
	 */
	public int getLockScreenAngle() {
		return mySharedPreferences.getInt(PREF_PUBLIC_LOCKSCREEN_ANGLE, 0);
	}

	/**
	 * @author wanghongfang @Date 2015-7-9 上午10:40:38 description:设定锁屏角度
	 * @param angle 角度
	 * @return {返回值说明}
	 */
	public void setLockScreenAngle(int angle) {
		mySharedPreferences.setInt(PREF_PUBLIC_LOCKSCREEN_ANGLE, angle);
	}

	/**
	 * false 认为半沉浸
	 *
	 * @author wanghongfang @Date 2015-6-24 下午6:50:03
	 *         description:{这里用一句话描述这个方法的作用}
	 * @param {引入参数名  {引入参数说明}
	 * @return {返回值说明}
	 */
	public boolean getGroyEnable() {
		return mySharedPreferences.getBoolean(PREF_PUBLIC_VIEWCORE_GROY_ENABLE, false);
	}

	/**
	 * 陀罗仪开关
	 *
	 * @author linzanxian @Date 2015-6-10 下午2:16:23 description:陀罗仪开关
	 * @param enable 是否打开
	 * @return void
	 */
	public void setGroyEnable(boolean enable) {
		mySharedPreferences.setBoolean(PREF_PUBLIC_VIEWCORE_GROY_ENABLE, enable);
		if (mGroylistener != null) {
			mGroylistener.groyEnableCallback(enable);
		}
	}

	public interface onGroyEnableListener {
		void groyEnableCallback(boolean isenable);
	}

	private onGroyEnableListener mGroylistener;

	public void setCheckGroyEnableListener(onGroyEnableListener mGroylistener) {
		this.mGroylistener = mGroylistener;

	}

	public void setString(String key,String value) {
		mySharedPreferences.setString(key, value);
	}

	public String getString(String key){
		return mySharedPreferences.getString(key, "");
	}


	/**
	 *
	 * @方法名：setDecoderRule
	 * @功能说明：：解码器规则：软件解码、硬件解码、智能解码
	 * @author liumansong
	 * @date 2015年9月7日 下午8:25:24
	 * @param rule
	 */
	public void setDecoderRule(String rule) {
		if (TextUtils.isEmpty(rule)) {
			mySharedPreferences.remove(GLobShareprefenceKey.RES_LOCAL_SETTING_DECODER);
			return;
		}
		mySharedPreferences.setString(GLobShareprefenceKey.RES_LOCAL_SETTING_DECODER, rule);
	}

	/**
	 *
	 * @方法名：getDecoderRule
	 * @功能说明：解码器规则：软件解码、硬件解码、智能解码
	 * @author liumansong
	 * @date 2015年9月7日 下午8:22:20
	 * @return 未设置默认返回空
	 */
	public String getDecoderRule() {
		return mySharedPreferences.getString(GLobShareprefenceKey.RES_LOCAL_SETTING_DECODER, GLobShareprefenceKey.RES_LOCAL_SETTING_DECODER_HARD);
	}

	/**
	 *
	 * @方法名：getSubtitlesRule
	 * @功能说明：字幕加载规则
	 * @author liumansong
	 * @date 2015年9月7日 下午8:22:20
	 * @return
	 */
	public void setSubtitlesRule(boolean b) {
		mySharedPreferences.setBoolean(GLobShareprefenceKey.RES_LOCAL_SETTING_LOAD_SUBTITLES, b);
	}

	/**
	 *
	 * @方法名：getSubtitlesRule
	 * @功能说明：字幕加载规则
	 * @author liumansong
	 * @date 2015年9月7日 下午8:22:20
	 * @return 默认加载字幕
	 */
	public boolean getSubtitlesRule() {
		return mySharedPreferences.getBoolean(GLobShareprefenceKey.RES_LOCAL_SETTING_LOAD_SUBTITLES, true);
	}

	/**
	 * @author wanghongfang @Date 2015-6-29 下午5:42:42 description:设置播放场景开关等状态
	 * @param type
	 *            1:开灯 0:关灯
	 * @return {返回值说明}
	 */
	public void setPlaySenceLightState(int type) {
		mySharedPreferences.setInt(PREF_PUBLIC_PLAYSENCE_LIGHT_STATE, type);
	}

	/**
	 * @author wanghongfang @Date 2015-6-29 下午5:46:58 description:获取保守的是否开灯状态
	 * @return 1：开灯 0：关灯
	 */
	public int getPlaySenceLightState() {
		return mySharedPreferences.getInt(PREF_PUBLIC_PLAYSENCE_LIGHT_STATE, 1);
	}

	/**
	 * 通用设置 获取保存的亮度设置模式
	 * @return 0:自动 1：手动
	 */
	public int getLightModel() {
		return mySharedPreferences.getInt(PLAY_LIGHT_MODEL, 0);
	}

	/**
	 * 通用设置 自动亮度模式
	 * @param lightmodel 0：自动 1：手动
	 */
	public void setLightModel(int lightmodel) {
		mySharedPreferences.setInt(PLAY_LIGHT_MODEL, lightmodel);
	}

	/**
	 * 设置播放页亮度
	 * @param lightValue 亮度值
	 * @author wanghongfang
	 * @data 2015-04-08 17:00
	 */
	public void setPlayPageLightValue(int lightValue) {
		mySharedPreferences.setInt(PLAY_PAGE_LIGHT_VALUE, lightValue);
	}

	/**
	 * 获取保存的播放页亮度值
	 * @author wanghongfang
	 * @data 2015-04-08 17:00
	 * @return
	 */
	public int getPlayPageLightValue() {
		return mySharedPreferences.getInt(PLAY_PAGE_LIGHT_VALUE, ScreenBrightnessUtils.getScreenBrightness());
	}

	/**
	 * 设置场景页亮度
	 * @param lightValue 亮度值
	 * @author wanghongfang
	 * @data 2015-04-08 17:00
	 */
	public void setScencePageLightValue(int lightValue) {
		mySharedPreferences.setInt(SCENCE_PAGE_LIGHT_VALUE, lightValue);
	}

	/**
	 * 获取保存的场景页亮度值
	 * @return
	 */
	public int getScencePageLightValue() {
		return mySharedPreferences.getInt(SCENCE_PAGE_LIGHT_VALUE, ScreenBrightnessUtils.getScreenBrightness());
	}

	/**
	 * 陀罗仪开关
	 *
	 * @author linzanxian @Date 2015-6-10 下午2:16:23 description:陀罗仪开关 不触发回调
	 * @param enable 是否打开
	 * @return void
	 */
	public void setGroyEnableWithouCallback(boolean enable) {
		mySharedPreferences.setBoolean(PREF_PUBLIC_VIEWCORE_GROY_ENABLE, enable);
		// 手动设置过 则黑白名单无效
		mySharedPreferences.setBoolean(PREF_PUBLIC_VIEWCORE_GROY_CHANGE, true);
		if (mGroylistener != null) {
			mGroylistener.groyEnableCallback(enable);
		}
	}

	/**
	 * 增强模式二、三代
	 *
	 * @author linzanxian @Date 2015-5-19 上午10:48:21 description:省电模式
	 * @param id 值（2 为二代，3为三代 1 为720p）
	 * @return void
	 */
	public void setStrongMode(int id) {
		mySharedPreferences.setInt(PREF_PUBLIC_SETTING_STRONG_MODE, id);
	}

	/**
	 * @author qiguolong @Date 2015-7-27 上午11:15:42
	 * @description:{默认二代
	 * @return
	 */
	public int getStrongMode() {
		return mySharedPreferences.getInt(PREF_PUBLIC_SETTING_STRONG_MODE, 2);
	}

	/**
	 * 是否加载so库
	 * @return
	 */
	public boolean getSoIsLoad() {
		return mySharedPreferences.getBoolean(STORM_IS_RELOAD_LIB_SO, false);
	}

	public void setSoIsLoad(boolean loadSuccess) {
		mySharedPreferences.setBoolean(STORM_IS_RELOAD_LIB_SO, loadSuccess);
	}


	public synchronized void setGlassesIds(String ids){
		mySharedPreferences.setString(PUBLIC_GLASSES_IDS_PREFENCE,ids);
	}

	public synchronized String getGlassesIds(){
		return mySharedPreferences.getString(PUBLIC_GLASSES_IDS_PREFENCE,"");
	}
}

