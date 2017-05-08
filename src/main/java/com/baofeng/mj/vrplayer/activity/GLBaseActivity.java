package com.baofeng.mj.vrplayer.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.vrplayer.interfaces.IResetLayerCallBack;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.HeadControlUtil;
import com.baofeng.mj.vrplayer.view.ResetLayer;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.input.base.MojingInputCallback;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.baofeng.mojing.sdk.glhelper.TextureLoader;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.view.BaseViewActivity;
import com.bfmj.viewcore.view.GLPanoView;
import com.bfmj.viewcore.view.GLTextView;
import com.mojing.sdk.pay.widget.mudoles.Glass;
import com.mojing.sdk.pay.widget.mudoles.Manufacturer;
import com.mojing.sdk.pay.widget.mudoles.ManufacturerList;
import com.mojing.sdk.pay.widget.mudoles.Product;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GLBaseActivity extends BaseViewActivity implements MojingInputCallback {
	private boolean setBrightness = false;

	public static final int SCENE_TYPE_CINEMA = 0;
	public static final int SCENE_TYPE_HOME = 1;
	public static final int SCENE_TYPE_OUTCINEMA = 2;

	private int mSceneType = -1;

	private HeadControlUtil mHeadControlCursor;

	private GLPanoView mSkyboxView;

	private ResetLayer mResetView;
	private Timer timer ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		//
//		getRootView().setMultiThread(true);
//		getRootView().setTimeWarp(true);

		//显示场景
		mSkyboxView = GLPanoView.getSharedPanoView(this);
		showSkyBox(SettingSpBusiness.getInstance().getSkyboxIndex());

		//显示头控
		mHeadControlCursor = new HeadControlUtil(this);
		mHeadControlCursor.setX( 1160);
		mHeadControlCursor.setY( 1160);
//			mHeadControlCursor.setLayoutParams(80, 80);
//			mHeadControlCursor.setImage(R.drawable.cursor_normal);
		mHeadControlCursor.setDepth(GLConst.Cursor_Depth,GLConst.Cursor_Scale);
		getRootView().addView(mHeadControlCursor);
//		showCursorView();
		hideCursorView();

		//显示复位菜单
		mResetView = new ResetLayer(this);
		mResetView.setDepth(GLConst.Bottom_Menu_Depth,GLConst.Bottom_Menu_Scale);
		getRootView().addView(mResetView);
		showResetView();

//		StickUtil.getInstance(this);
//		StickUtil.setCallback(this);
//		initLog();

	}

	/**
	 * 显示复位图标
	 */
	public void showResetView() {
		if (mResetView != null){
			mResetView.setVisible(true);
		}
	}

	/**
	 * 隐藏复位图标
	 */
	public void hideResetView() {
		if (mResetView != null ) {
			mResetView.setVisible(false);
		}
	}

	public void fixedResetView(boolean isFixed){
		if (mResetView!=null){
			mResetView.setFixed(isFixed);
			mResetView.setResetFixed(isFixed);

		}
	}

	/**
	 * 重新初始化视角
	 */
	public void initHeadView() {
		getRootView().initHeadView();
	}

	/**
	 * 显示天空盒场景
	 * @param
     */
	public synchronized void showSkyBox(int type){

		mSkyboxView.reset();

        mSkyboxView.setSceneType(GLPanoView.SCENE_TYPE_SPHERE);//3D场景

		if(type == SCENE_TYPE_CINEMA){  //18m
			//设置右边场景
			mSkyboxView.setLeftImage(R.raw.play_cinema_left, TextureLoader.TextureType.TEXTURE_TYPE_KTX);
			//设置左边场景
			mSkyboxView.setImage(R.raw.play_cinema_right, TextureLoader.TextureType.TEXTURE_TYPE_KTX);
		} else if(type == SCENE_TYPE_HOME) { //6m
			//设置右边场景
			mSkyboxView.setLeftImage(R.raw.play_home_left, TextureLoader.TextureType.TEXTURE_TYPE_KTX);
			//设置左边场景
			mSkyboxView.setImage(R.raw.play_home_right, TextureLoader.TextureType.TEXTURE_TYPE_KTX);
		} else if(type == SCENE_TYPE_OUTCINEMA) { //6m
			//设置右边场景
			mSkyboxView.setLeftImage(R.raw.play_outcinema_left, TextureLoader.TextureType.TEXTURE_TYPE_KTX);
			//设置左边场景
			mSkyboxView.setImage(R.raw.play_outcinema_right, TextureLoader.TextureType.TEXTURE_TYPE_KTX);
		}

		SettingSpBusiness.getInstance().setSkyboxIndex(type);

		getRootView().queueEvent(new Runnable() {
			@Override
			public void run() {
				if(null != mSkyboxView) {
					mSkyboxView.setVisible(true);
				}
			}
		});
	}

	public void setSkyboxFixed(boolean fixed){
		GLPanoView.getSharedPanoView(this).setFixed(fixed);
	}

	/**
	 * 隐藏天空盒场景
	 */
	public void hideSkyBox(){

		getRootView().queueEvent(new Runnable() {
			@Override
			public void run() {
				if(null != mSkyboxView) {
					mSkyboxView.setVisible(false);
				}
			}
		});
	}

	@Override
	public void showCursorView() {
		if (mHeadControlCursor != null){
			cancelHideCursorViewTimer();
			mHeadControlCursor.setVisible(true);
		}
	}

	public void setCursorFixed(boolean fixed){
		if (mHeadControlCursor != null ) {
			mHeadControlCursor.setFixed(fixed);
		}
	}

    /**
     * 立刻隐藏头控焦点图标
     */
	@Override
	public void hideCursorView() {
		if (mHeadControlCursor != null ) {
			mHeadControlCursor.setVisible(false);
		}
	}

	/**
	 * 隐藏头控焦点图标(3S后消失)
	 */
	public void hideCursorView2() {
		if (mHeadControlCursor != null ) {
			setDelayVisiable(2*1000);
		}
	}

	public void setDelayVisiable(int duration){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
//				MJGLUtils.exeGLQueueEvent(this, new Runnable() {
//					@Override
//					public void run() {
				mHeadControlCursor.setVisible(false);
//					}
//				});
			}
		};
		timer.schedule(task,duration);
	}

	public void cancelHideCursorViewTimer(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
	}



	@Override
	protected void onDestroy() {
//		GLPanoView.finish();
		super.onDestroy();

		try {
			Runtime.getRuntime().exec("setprop debug.sf.vr 1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		StickUtil.setCallback(null);
		MobclickAgent.onPause(getApplicationContext());
		setBrightness = false;

		/*if (mStatusBarView != null){
			mStatusBarView.onPause();
		}*/
	}

	@Override
	protected void onResume() {
		super.onResume();
//		StickUtil.getInstance(this);
//		StickUtil.setCallback(this);
		MobclickAgent.onResume(getApplicationContext());
	/*	if (mStatusBarView != null){
			mStatusBarView.onResume();
		}*/
		if (getRootView() != null){
			getRootView().initHeadView();
		}
	}


	@Override
	protected void onStart() {
//		ScreenBrightnessUtils.setModel(this, true);
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		setBrightness = true;
	}

	/**
	 * @author qiguolong @Date 2015-6-24 下午6:13:47
	 * @description:{gl事件 延迟
	 * @param runnable
	 */
	public void queenGlRunableDelay(final Runnable runnable, final int times) {
		new Thread() {
			public void run() {
				try {
					sleep(times);
					queenGlRunable(runnable);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			};
		}.start();

	}

	/**
	 * @author qiguolong @Date 2015-6-24 下午6:13:47
	 * @description:{gl事件
	 * @param runnable
	 */
	public void queenGlRunable(final Runnable runnable) {

		if (getRootView() != null)
			getRootView().queueEvent(runnable);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (StickUtil.dispatchKeyEvent(event) && event.getKeyCode() != 4)
			return true;
		else
			return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		if (StickUtil.dispatchGenericMotionEvent(event))
			return true;
		else
			return super.dispatchGenericMotionEvent(event);
	}

	@Override
	public void onBluetoothAdapterStateChanged(int arg0) {
		StickUtil.onBluetoothAdapterStateChanged(arg0);
	}

	@Override
	public void onMojingDeviceAttached(String arg0) {
		StickUtil.isConnected = true;
		StickUtil.onMojingDeviceAttached(arg0);

	}

	@Override
	public void onMojingDeviceDetached(String arg0) {
		StickUtil.isConnected = false;
		StickUtil.onMojingDeviceDetached(arg0);

	}

	@Override
	public void onTouchPadStatusChange(String s, boolean b) {

	}

	@Override
	public void onTouchPadPos(String s, float v, float v1) {

	}

	@Override
	public boolean onMojingKeyDown(String deviceName, final int keyCode) {
		StickUtil.onMojingKeyDown(deviceName, keyCode);
		onZKeyDown(keyCode);
		return false;
	}

	@Override
	public boolean onMojingKeyLongPress(String deviceName, final int keyCode) {
		StickUtil.onMojingKeyLongPress(deviceName, keyCode);
		onZKeyLongPress(keyCode);
		return false;
	}

	@Override
	public boolean onMojingKeyUp(String deviceName, final int keyCode) {
		StickUtil.onMojingKeyUp(deviceName, keyCode);
		onZKeyUp(keyCode);
		return false;
	}

	@Override
	public boolean onMojingMove(String deviceName, int axis, float x, float y, float z) {
		StickUtil.onMojingMove(deviceName, axis, x, y, z);
		return false;
	}

	@Override
	public boolean onMojingMove(String deviceName, int axis, float value) {
		StickUtil.onMojingMove(deviceName, axis, value);
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (StickUtil.isConnected){
			return true;
		}

		 if(keyCode==KeyEvent.KEYCODE_VOLUME_MUTE||keyCode==KeyEvent.KEYCODE_VOLUME_DOWN|| keyCode==KeyEvent.KEYCODE_VOLUME_UP){
			 return super.onKeyDown(keyCode, event);
		 }
		if (keyCode != KeyEvent.KEYCODE_BACK || getPageManager().hasMorePage()){
			if (keyCode == 23){
				keyCode = MojingKeyCode.KEYCODE_ENTER;
			}
			onZKeyDown(keyCode);
			return  true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (StickUtil.isConnected){
			return true;
		}

		if (keyCode != KeyEvent.KEYCODE_BACK || getPageManager().hasMorePage()){
			if (keyCode == 23){
				keyCode = MojingKeyCode.KEYCODE_ENTER;
			}
			onZKeyUp(keyCode);
			return  true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			onZKeyDown(MojingKeyCode.KEYCODE_ENTER);
		} else if (event.getAction() == MotionEvent.ACTION_UP){
			onZKeyUp(MojingKeyCode.KEYCODE_ENTER);
		}

		return super.onTouchEvent(event);
	}

	private String getGlassKey(){
		if (!MojingSDK.GetInitSDK()) {
			MojingSDK.Init(this.getApplicationContext());
		}

		ManufacturerList m_ManufacturerList = ManufacturerList.getInstance("zh");

		List<Manufacturer> manufacturers = m_ManufacturerList.mManufaturerList;
		List<Product> products = manufacturers.get(0).mProductList;
		List<Glass> glasses = products.get(0).mGlassList;
		String key = glasses.get(0).mKey;
		return key;
	}

	private IResetLayerCallBack mCallBack;

	public void setIResetLayerCallBack(IResetLayerCallBack callBack){
		mCallBack = callBack;
		if (null != mResetView) {
			mResetView.setIResetLayerCallBack(mCallBack);
		}
	}

	public void showOpen() {
		mResetView.showOpen();
	}

	public void showClose() {
		mResetView.showClose();
	}

	private void initLog(){
		final GLTextView fps = new GLTextView(this);
		fps.setX(900);
		fps.setY(2200);
		fps.setLayoutParams(600, 100);
		fps.setFixed(true);
		fps.setBackground(new GLColor(0x000000, 0.5f));
		fps.setTextColor(new GLColor(0xffffff));
		fps.setTextSize(80);

		getRootView().addView(fps);

		new Thread(new Runnable() {
			long times = 0;
			int max = 0;
			int min = 60;

			@Override
			public void run() {
				getRootView().getFPS();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (true){
					final int f = getRootView().getFPS();
					if (f > 0 && f < 70){
						times++;
						if (times > 2) {
							max = Math.max(f, max);
							min = Math.min(f, min);
							getRootView().queueEvent(new Runnable() {
								@Override
								public void run() {
									String msg = "FPS : " + f;
									if (max > 0){
										msg +=  " [" + min + "~" + max + "]";
									}
									fps.setText(msg);
								}
							});
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

}
