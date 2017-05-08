package com.bfmj.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class NetworkUtil {
	private Context mContext = null;
	public static final String NET_TYPE_WIFI = "WIFI";
	public static final String NET_TYPE_MOBILE = "MOBILE";
	public static final String NET_TYPE_NO_NETWORK = "no_network";
	public static final int NETWORK_TYPE_UNKNOWN = -1;
	// public static final int NETWORK_TYPE_MOBILE = -100;
	public static final int NETWORK_TYPE_WIFI = -101;
	public static final int NETWORK_UNKNOWN = 0;
	public static final int NETWORK_2_G = 1;
	public static final int NETWORK_3_G = 2;
	public static final int NETWORK_4_G = 3;
	public static final int NETWORK_WIFI = 4;

	public NetworkUtil(Context pContext) {
		this.mContext = pContext;
	}

	public static final String IP_DEFAULT = "0.0.0.0";

	public static boolean isConnectInternet(final Context pContext) {
		final ConnectivityManager conManager = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}

		return false;
	}

	/**
	 * @author liuchuanchi @Date 2015-10-10 下午12:00:07
	 * @description:{网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean IsNetWorkEnable(Context context) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public static int isConnectWifi(final Context pContext) {
		ConnectivityManager mConnectivity = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		// 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
		int netType = -1;
		if (info != null) {
			netType = info.getType();
		}
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected() ? 1 : 10;
		}
		if (netType == ConnectivityManager.TYPE_ETHERNET) {
			return info.isConnected() ? 2 : 20;
		} else {
			return 0;
		}
	}

	/**
	 * @author liuchuanchi @Date 2015-10-10 上午11:50:20
	 * @description:{WiFi是否可用
	 * @param context
	 * @return
	 */
	public static boolean isWifiAvailable(final Context context) {
		NetworkInfo networkInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected() && networkInfo
				.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * @author liuchuanchi  @Date 2015-10-14 下午9:48:08
	 * @description:{3G4G网络}
	 *@param netWork
	 *@return
	 */
	public static boolean is2G3G4G(int netWork){
		if(NetworkUtil.NETWORK_2_G == netWork || NetworkUtil.NETWORK_3_G == netWork || NetworkUtil.NETWORK_4_G == netWork){//3G4G可用
			return true;
		}
		return false;
	}
	
	public static String getNetTypeName(final int pNetType) {
		switch (pNetType) {
		case 0:
			return "unknown";
		case 1:
			return "GPRS";
		case 2:
			return "EDGE";
		case 3:
			return "UMTS";
		case 4:
			return "CDMA: Either IS95A or IS95B";
		case 5:
			return "EVDO revision 0";
		case 6:
			return "EVDO revision A";
		case 7:
			return "1xRTT";
		case 8:
			return "HSDPA";
		case 9:
			return "HSUPA";
		case 10:
			return "HSPA";
		case 11:
			return "iDen";
		case 12:
			return "EVDO revision B";
		case 13:
			return "LTE";
		case 14:
			return "eHRPD";
		case 15:
			return "HSPA+";
		default:
			return "unknown";
		}
	}

	public static String getIPAddress() {
		try {
			final Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
					.getNetworkInterfaces();

			while (networkInterfaceEnumeration.hasMoreElements()) {
				final NetworkInterface networkInterface = networkInterfaceEnumeration
						.nextElement();

				final Enumeration<InetAddress> inetAddressEnumeration = networkInterface
						.getInetAddresses();

				while (inetAddressEnumeration.hasMoreElements()) {
					final InetAddress inetAddress = inetAddressEnumeration
							.nextElement();

					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}

			return NetworkUtil.IP_DEFAULT;
		} catch (final SocketException e) {
			return NetworkUtil.IP_DEFAULT;
		}
	}

	public String getConnTypeName() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return NET_TYPE_NO_NETWORK;
		} else {
			return networkInfo.getTypeName();
		}
	}

	public static int getWifiLevle(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService("wifi");
		  // Wifi管理器
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		 // 获得的Wifi信息
			// 获得WifiManager

		// 获得信号强度值
		int level = wifiInfo.getRssi();
		// 根据获得的信号强度发送信息
		return level;

	}

	public static int getWifiCount(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService("wifi");
		; // Wifi管理器
		List<ScanResult> listScan = wifiManager.getScanResults();
		return listScan != null ? listScan.size() : 0;

	}

	public static void getWangKaInfo() {
		try {
			List<NetworkInterface> networkInterfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());

			for (NetworkInterface networkInterface : networkInterfaces) {
				String displayName = networkInterface.getDisplayName();

			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void getWangKaInfo1() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();

			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				printParameter(ni);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void printParameter(NetworkInterface ni) {

		try {
			System.out.println(" Name = " + ni.getName());

			System.out.println(" Display Name = " + ni.getDisplayName());

			System.out.println(" Is up = " + ni.isUp());

			System.out
					.println(" Support multicast = " + ni.supportsMulticast());

			System.out.println(" Is loopback = " + ni.isLoopback());

			System.out.println(" Is virtual = " + ni.isVirtual());

			System.out.println(" Is point to point = " + ni.isPointToPoint());

			System.out
					.println(" Hardware address = " + ni.getHardwareAddress());

			System.out.println(" MTU = " + ni.getMTU());

			System.out.println("\nList of Interface Addresses:");

			List<InterfaceAddress> list = ni.getInterfaceAddresses();

			Iterator<InterfaceAddress> it = list.iterator();

			while (it.hasNext()) {

				InterfaceAddress ia = it.next();

				System.out.println(" Address = " + ia.getAddress());

				System.out.println(" Broadcast = " + ia.getBroadcast());

				System.out.println(" Network prefix length = "
						+ ia.getNetworkPrefixLength());

				System.out.println("");

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static boolean isWangXian(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean getNetStatus(Context context) {
		NetworkInfo network = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return network != null && network.isConnected();
	}

	/**
	 * 判断是否连接上WIFI
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnected(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean flag = false;
		if (wifiManager.isWifiEnabled()) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (info != null) {
				flag = info.getNetworkId() != -1;
			}
		}
		return flag;
	}

	/**
	 * 获取本地ip
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		String ip = "";
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (info != null) {
				int intIp = info.getIpAddress();
				ip = int2ip(intIp);
			}
		}
		if (ip == null) {
			try {
				outer: for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ip = inetAddress.getHostAddress().toString();
							break outer;
						}
					}
				}
			} catch (SocketException ex) {
				Log.e("获取ip异常", ex.toString());
			}
		}

		return ip;
	}

	private static String int2ip(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	/**
	 * 获取当前网络类型
	 * @return 1为wifi 其他与 R.drawable.wifi_state 中level对应
	 *         8为2G 9为3g 10为4g
	 */

	public synchronized static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		// connectivityManager.get
		// System.out.println(networkInfo.getType() + "!"
		// + ConnectivityManager.TYPE_MOBILE);
		// Toast.makeText(context,
		// networkInfo + "!" + ConnectivityManager.TYPE_MOBILE,
		// Toast.LENGTH_SHORT).show();
		if (networkInfo == null) {
			return (((WifiManager) context
					.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled() ? 0
					: 6);
		}
		if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

			return 1 + getWifiLevel(context, 5);
		}

		if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

			int gprsType = networkInfo.getSubtype();
			return getNetworkClass(gprsType);
		}

		return netType;
	}

	/**
	 * 取得2,3,4G类型
	 * @param networkType
	 * @return 与 R.drawable.wifi_state 中level对应
	 */
	private static int getNetworkClass(int networkType) {
		switch (networkType) {

		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			// 2g
			return 8;
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			// TelephonyManager.NETWORK_TYPE_TD_SCDMA 17 被隐藏
		case 17:
			// 3g
			return 9;
		case TelephonyManager.NETWORK_TYPE_LTE:
			// 4g
			return 10;
		default:
			return 6;
		}
	}

	/**
	 * @author qiguolong @Date 2015-7-7 下午5:17:39
	 * @description:{判断wifi 还是 2,3,4g}
	 * @return
	 */
	public static String networknName(Context context) {
		int t = getNetworkType(context);
		switch (t) {
		case 1:
			return "WIFI";
		case 8:
			return "2G";
		case 9:
			return "3G";
		case 10:
			return "4G";

		}
		return "";
	}

	/**
	 * 获取wifi强度等级
	 * @param context
	 * @param levelMax 最高级数
	 * @return
	 */
	public static int getWifiLevel(Context context, int levelMax) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);

	}


	/**
	 * @author liuchuanchi @Date 2015-10-10 下午1:42:34
	 * @description:{获取网络
	 * @param networkType
	 * @return
	 */
	public static int getNetwork(Context context, NetworkInfo networkInfo) {
		int networkType = getNetworkType(context, networkInfo);
		return getNetwork(networkType);
	}

	/**
	 * @author liuchuanchi @Date 2015-10-10 下午1:42:34
	 * @description:{获取网络
	 * @param networkType
	 * @return
	 */
	public static int getNetwork(Context context) {
		int networkType = NETWORK_TYPE_UNKNOWN;
		try {
			NetworkInfo networkInfo = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			networkType = getNetworkType(context, networkInfo);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getNetwork(networkType);
	}

	/**
	 * @author liuchuanchi @Date 2015-10-10 下午1:42:34
	 * @description:{获取网络
	 * @param networkType
	 * @return
	 */
	public static int getNetwork(int networkType) {
		switch (networkType) {
		case NETWORK_TYPE_WIFI:
			return NETWORK_WIFI;
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return NETWORK_2_G;
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return NETWORK_3_G;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return NETWORK_4_G;
		default:
			return NETWORK_UNKNOWN;
		}
	}

	/**
	 * @author liuchuanchi @Date 2015-10-10 下午2:13:54
	 * @description:{获取网络类型
	 * @param context
	 * @param networkInfo
	 * @return
	 */
	public static int getNetworkType(Context context, NetworkInfo networkInfo) {
		int networkType = NETWORK_TYPE_UNKNOWN;
		try {
			if (networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected()) {
				int type = networkInfo.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					networkType = ((TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE))
							.getNetworkType();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return networkType;
	}

	/**
	 * @author liuchuanchi @Date 2015-10-10 下午1:06:29
	 * @description:{获取网络名称
	 * @param context
	 * @return
	 */
	public static String getNetworkName(Context context) {
		switch (getNetwork(context)) {
		case NETWORK_WIFI:
			return "Wi-Fi";
		case NETWORK_2_G:
			return "2G";
		case NETWORK_3_G:
			return "3G";
		case NETWORK_4_G:
			return "4G";
		case NETWORK_UNKNOWN:
			return "未知";
		default:
			return "未知";
		}
	}
}
