package com.bfmj.sdk.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.baofeng.mojing.input.MojingInputManager;

import java.util.Set;

public class StickUtil {

	public static MojingInputManager joystick;
	private static StickUtil instance;
	public static boolean isConnected = false;

	public static StickUtil getInstance(Activity context){
		if (instance == null) {
			instance = new StickUtil(context);
		}
		return instance;
	}

	public StickUtil(Activity activity){
		joystick = MojingInputManager.getMojingInputManager();
		joystick.AddProtocal(MojingInputManager.Protocol_Bluetooth);
		joystick.Connect(activity, null);
	}

	public static void setCallback(Activity callback){
		isConnected = false;
		if (instance != null && instance.joystick != null) {
			instance.joystick.setCallback(callback);
		}
	}

	public static void disconnect(){
		if (instance != null && instance.joystick != null) {
			try {
				instance.joystick.Disconnect();
			} catch (Exception e) {}

			instance = null;
		}
		isConnected = false;
	}

	public static boolean dispatchKeyEvent(KeyEvent event){
		if (instance != null && instance.joystick != null) {
			return instance.joystick.dispatchKeyEvent(event);
		}
		return false;
	}

	public static boolean dispatchGenericMotionEvent(MotionEvent event){
		if (instance != null && instance.joystick != null) {
			return instance.joystick.dispatchGenericMotionEvent(event);
		}
		return false;
	}

	public static boolean blutoothEnble() {
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		return mAdapter != null && mAdapter.isEnabled();
	}

	/**
	 * @author qiguolong @Date 2015-9-14 下午2:41:24
	 * @description:{遥控器是否配对
	 * @return
	 */
	public static boolean isBondBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		String dname = "";
		for (BluetoothDevice device : devices) {
			if (device != null) {
				dname = device.getName();
				if (!TextUtils.isEmpty(dname)) {
					if (dname.startsWith("Mojing")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @author qiguolong @Date 2015-9-14 下午2:41:09
	 * @description:{遥控器是否连接
	 * @return
	 */
	public static boolean stickIsConnect() {
		Log.wtf("px","blutoothEnble()  "+blutoothEnble()+" isBondBluetooth  "+isBondBluetooth()+" isConnected  "+isConnected);
		if(blutoothEnble() && isBondBluetooth()
				&& isConnected){
			return true;
		}
		return false;
	}
}
