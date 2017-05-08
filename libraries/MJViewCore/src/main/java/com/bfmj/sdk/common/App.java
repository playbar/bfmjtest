package com.bfmj.sdk.common;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.bfmj.sdk.util.Common;
import com.bfmj.sdk.util.PreferenceUtil;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * 应用程序类
 * @author yanzw
 * @date 2014-9-3 下午4:40:06
 */
public class App extends Application {
	private static final String TYPE_ID = "000";// 暴风官网
	private boolean isBaofengAppCrash = false;// 是本应用奔溃还是图片、现场崩溃

	private static Application appInstance = null;
	private boolean isHeadControl = true;
	private List<Activity> activityList = new LinkedList<Activity>();

	// 记录播放现场后导致返回定位不准的数据
	private boolean isPlayComein = false;// 是否播放了现场
	private int comeinIndex = 0; // 记录现场的索引
	private int indexIndex = 1; // 记录入口页的索引
	private String brand = "";

	private boolean isVerticalScreen = true;// 是否在竖屏页
	private boolean isChargeView = false; // 是否在支付页面
	private SharedPreferences mPreference;
	private boolean isScan = false;

	public static boolean isReport = true; // 启动应用报数

	@Override
	public void onCreate() {
		super.onCreate();
		appInstance = this;
		File file = new File(AppConfig.SAVE_DIR);
		if(!file.exists()){
			file.mkdirs();
		}
		new Thread() {
			public void run() {
				init();
				mPreference = PreferenceUtil.getPressSharedPreferences(
						getApplicationContext(), PreferenceUtil.DEFAULT_NAME,
						Activity.MODE_PRIVATE);
				// 迁移到蓝牙页
				// Intent intent = new Intent(App.this, AppNotifyService.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startService(intent);
			};
		}.start();

//		ResApp.init(this);
	}

	private void init() {
		setBrand();
//		if (!AppConfig.DEBUG) {
//			// 收集崩溃
//			CrashHandler crashHandler = CrashHandler
//					.getInstance(getApplicationContext());
//			Thread.setDefaultUncaughtExceptionHandler(crashHandler);
//		}

	}

	// 手机品牌，头控中有些手机磁控兼容性
	private void setBrand() {
		HashMap<String, String> hsbrand = Common.getHardwareInfo();
		if (hsbrand != null) {
			brand = hsbrand.get("BRAND");

			if (brand == null) {
				brand = "";
			}
		}
	}

//	public static void loadLocalData() {
//		if (appInstance != null) {
//			SearchLocalVideo.startSearch(appInstance, "jsonCallBack");
//		}
//	}
//
//	public static void jsonCallBack(String url, JSONArray jsonArray,
//			AjaxStatus status) throws JSONException {
//		SearchLocalVideo.doCallBack(url, jsonArray, status);
//	}
//
//	@Override
//	public void onTerminate() {
//		super.onTerminate();
//		// 退出系统
//		System.exit(0);
//		RunningTaskMonitor.setMojingAPPRunning(false);
//		com.baofeng.mj.resource.pubblico.service.RunningTaskMonitor.setMojingAPPRunning(false);
//		stopService(new Intent(this, RunningForwardMonitor.class));
//	}

	public static Application getInstance() {
		return appInstance;
	}

	public boolean isHeadControl() {
		return isHeadControl;
	}

	public void setHeadControl(boolean isHeadControl) {
		this.isHeadControl = isHeadControl;
	}

	public void setComeinIndex(int index) {
		this.comeinIndex = index;
	}

	public int getComeinIndex() {
		return this.comeinIndex;
	}

	public void setIndexIndex(int index) {
		this.indexIndex = index;
	}

	public int getIndexIndex() {
		return this.indexIndex;
	}

	public void setIsPlayComein(boolean isPlay) {
		this.isPlayComein = isPlay;
	}

	public boolean getIsPlayComein() {
		return this.isPlayComein;
	}

	public String getBrand() {
		return this.brand;
	}

	public void addActivity(Activity activity) {
		if (null != activity)
			activityList.add(activity);
	}

	public void removeActivty(Activity activity) {
		if (null != activity)
			activityList.remove(activity);
	}

	public void exitAllActivity() {
		for (Activity activity : activityList) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	public String getSid() {
		return TYPE_ID;
	}

//	/**
//	 * app激活报数
//	 */
//	public void reportActive() {
//		Report report = Report.getSingleReport(this);
//		// 每天首次启动报活
//		String currDate = Common.geCurrtDate();
//		SharedPreferencesUtil sp = new SharedPreferencesUtil(this);
//		String last_version = sp.getString("last_version", "");
//		if (!last_version.equals(Common.getVersionName(this))) {//
//			HashMap<String, String> hsactive = new HashMap<String, String>();
//			hsactive.put("t", "".equals(last_version) ? 1 + "" : 5 + "");
//			hsactive.put("idate", Common.geCurrtDate());
//			hsactive.put("activeinterval", 0 + "");
//			hsactive.put("result", 1 + "");
//			report.reportActive(hsactive);
//			UserManager.getInstance().setIsNicktip(true);
//			sp.setString("last_version", Common.getVersionName(this));
//		}
//		if (!sp.getString("day_firstuse", "").equals(currDate)) // 每天第一次启动应用
//		{
//			// 距上次报活的天数
//			int interval;
//			String last = sp.getString("last_time_active", "");
//			if (!last.equals("")) {
//				interval = Common.getGapDayCount(last, Common.geCurrtDate());
//			} else {
//				interval = 0;
//			}
//
//			HashMap<String, String> hsactive = new HashMap<String, String>();
//			hsactive.put("t", 3 + "");
//			hsactive.put("activeinterval", interval + "");
//			report.reportActive(hsactive);
//			sp.setString("day_firstuse", Common.geCurrtDate());// 是否是每天第一次启动应用
//			sp.setString("last_time_active", Common.geCurrtDate());// 上次报活时间
//		}
//
//		HashMap<String, String> hsactive = new HashMap<String, String>();
//		hsactive.put("t", 4 + "");
//		report.reportActive(hsactive);
//
//	}

	public void setBaofengAppCrash(boolean isBaofengAppCrash) {
		this.isBaofengAppCrash = isBaofengAppCrash;
	}

	public boolean getBaofengAppCrash() {
		return isBaofengAppCrash;
	}

	public boolean isVerticalScreen() {
		return isVerticalScreen;
	}

	public void setVerticalScreen(boolean isVerticalScreen) {
		this.isVerticalScreen = isVerticalScreen;
	}

	/**
	 * @author wanghongfang @Date 2015-2-9 上午11:45:31
	 *         description:判断是否在支付页面，如果在支付页不弹强制升级的提示
	 */
	public boolean isChargeViewScreen() {
		return isChargeView;
	}

	/**
	 * @author wanghongfang @Date 2015-2-9 上午11:45:21
	 *         description:记录当前是否在支付页面的状态
	 * @param ischargeView true:在支付页 false:不在支付页
	 */
	public void setIsChargeViewScreen(boolean ischargeView) {
		this.isChargeView = ischargeView;
	}

	// 是否从扫描页回来
	public void setScan(boolean scan) {
		isScan = scan;
	}

	public boolean getScan() {
		return isScan;
	}
}
