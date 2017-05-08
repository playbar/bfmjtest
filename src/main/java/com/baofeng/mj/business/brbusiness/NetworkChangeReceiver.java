package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;

import com.baofeng.mj.ui.online.utils.PlayerNetworkSubject;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.storm.smart.common.utils.LogHelper;

/**
 * 监听网络连接
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
	private NetworkChangeListener networkChangeListener;

	public NetworkChangeReceiver(NetworkChangeListener networkChangeListener) {
		this.networkChangeListener = networkChangeListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		LogHelper.e("infosss","newworkIntent=="+intent);
		// 监听网络连接的设置，包括wifi和移动数据的打开和关闭
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			int currentNetwork = NetworkUtil.getNetwork(networkInfo);
			if (networkChangeListener != null) {
				networkChangeListener.networkChange(currentNetwork);
			}
			/**播放时网络变化监听 add by whf 20161123*/
			PlayerNetworkSubject.getInstance().notifyChanged(currentNetwork);
		}
		//监听WiFi信号的改变
		if (WifiManager.RSSI_CHANGED_ACTION.equals(intent.getAction())) {
			if (networkChangeListener != null) {
				networkChangeListener.wifiLevelChange(NetworkUtil.getWifiLevel());
			}
		}

		if(intent.getAction() != null){
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) { //*WIFI状态改变*//*
				int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				switch (state) {
					case WifiManager.WIFI_STATE_DISABLED: //*已关闭*//*
						if (networkChangeListener != null) {
							networkChangeListener.wifiEnabled(1);
						}
						break;
					case WifiManager.WIFI_STATE_ENABLED://*已打开*//*
						if (networkChangeListener != null) {
							networkChangeListener.wifiEnabled(3);
						}
						break;
					default:
						break;
				}
			}

		}

	}

	public interface NetworkChangeListener {
		void networkChange(int currentNetwork);//网络状态改变
		void wifiLevelChange(int wifiLevel);//WiFi信号强度改变
		void wifiEnabled(int enabled);//WiFi enabled disabled
	}
}
