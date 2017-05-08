package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * @author liuchuanchi
 * @description: usb设备广播
 */
public class UsbDeviceReceiver extends BroadcastReceiver {
	public static final String TAG = "UsbDeviceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent == null){
			Log.d(TAG, "intent == null");
			return;
		}
		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED " + device.toString());
		}else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			Log.d(TAG, "ACTION_USB_DEVICE_DETACHED " + device.toString());

		}
	}

}
