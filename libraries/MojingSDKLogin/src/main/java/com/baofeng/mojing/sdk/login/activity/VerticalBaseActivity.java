package com.baofeng.mojing.sdk.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/**
 * ClassName: VerticalBaseActivity <br/>
 * @author yebo
 * @date: 2015年1月16日 下午4:02:32 <br/>
 *        description: 所有竖屏页面的基类
 */
public class VerticalBaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			this.getContentResolver()
					.registerContentObserver(
							Settings.System
									.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE),
							true, mBrightnessObserver);
			this.getContentResolver()
					.registerContentObserver(
							Settings.System
									.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
							true, mBrightnessObserver);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		 
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			this.getContentResolver().unregisterContentObserver(
					mBrightnessObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

//		ScreenBrightnessUtils.setModel(this, false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 竖屏禁止使用摇杆
	 */
	public boolean dispatchGenericMotionEvent(android.view.MotionEvent ev) {
		return true;
	};

	private ContentObserver mBrightnessObserver = new ContentObserver(
			new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
//			ScreenBrightnessUtils.initModel(VerticalBaseActivity.this);
		}
	};

	@Override
	public void startActivity(Intent intent) {
		if (intent.getComponent() == null) { // 启动系统组件
			try {
				super.startActivity(intent);
			} catch (Exception e) {
				/**
				 * 解决 java.lang.SecurityException: Permission Denial: starting
				 * Intent
				 */
				// toastShort("系统未安装相关应用，无法启动");
			}
			return;
		}
 

		super.startActivity(intent);
	}

	public void showToast(String mes) {
		Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 方便的 获取view的方法
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getViewByTag(View view, Object tag) {
		return (T) view.findViewWithTag(tag);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getViewById(int resid) {
		return (T) this.findViewById(resid);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getViewById(View view, int resid) {
		return (T) view.findViewById(resid);
	}

	/**
	 * 禁摇杆
	 */
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// return super.onGenericMotionEvent(event);
		return true;
	};
}
