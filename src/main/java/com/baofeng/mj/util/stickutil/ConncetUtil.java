package com.baofeng.mj.util.stickutil;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 连接工具类
 */
@SuppressLint("NewApi")
public class ConncetUtil {
	/**
	 * 配对设备
	 */
	public static void pairDevice(BluetoothDevice bluetoothDevice) {
		try {
			Method method = BluetoothDevice.class.getMethod("createBond");
			method.invoke(bluetoothDevice);
		} catch (Exception e) {
		}
	}

	/**
	 * 取消配对设备
	 */
	public static void unPairDevice(BluetoothDevice bluetoothDevice) {
		try {
			Method method = BluetoothDevice.class.getMethod("removeBond");
			method.invoke(bluetoothDevice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消配对设备
	 */
	public static void unPairDevice(String deviceAddress) {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		unPairDevice(bluetoothDevice);
	}

	/**
	 * 重命名设备
	 * */
	public static Boolean renameDevice(BluetoothDevice bluetoothDevice, String deviceName) {
		try {
			Method method = BluetoothDevice.class.getMethod("setAlias", String.class);
			return (Boolean) method.invoke(bluetoothDevice, deviceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取BluetoothProfile中hid的profile，"INPUT_DEVICE"类型隐藏，需反射获取
     */
	@SuppressLint("NewApi")
	private static int getInputDeviceConstant() {
		Class<BluetoothProfile> clazz = BluetoothProfile.class;
		for (Field f : clazz.getFields()) {
			int mod = f.getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
				try {
					if ("INPUT_DEVICE".equals(f.getName())) {
						return f.getInt(null);
					}
				} catch (Exception e) {
				}
			}
		}
		return -1;
	}

	/**
	 * hid连接
     */
	public static void connectHid(final Context context, final BluetoothDevice bluetoothDevice, final HidConnectListener hidConnectListener) {
		final int inputDeviceConstant = getInputDeviceConstant();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
			@Override
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				try {
					if (profile == inputDeviceConstant) {
						Method method = proxy.getClass().getMethod("connect", new Class[]{BluetoothDevice.class});
						method.invoke(proxy, bluetoothDevice);
						if(hidConnectListener != null){
							hidConnectListener.hasConnected();
						}
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onServiceDisconnected(int profile) {
			}
		}, inputDeviceConstant);
	}

	/**
	 * 断开hid连接
     */
	public static void disconnectHid(final Context context, final BluetoothDevice bluetoothDevice, final HidDisconnectListener hidDisconnectListener) {
		final int inputDeviceConstant = getInputDeviceConstant();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
			@Override
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				try {
					if (profile == inputDeviceConstant) {
						Method method = proxy.getClass().getMethod("disconnect", new Class[]{BluetoothDevice.class});
						method.invoke(proxy, bluetoothDevice);
						if (hidDisconnectListener != null) {
							hidDisconnectListener.hasDisconnected();
						}
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onServiceDisconnected(int profile) {
			}
		}, inputDeviceConstant);
	}

	/**
	 * 获取hid连接的设备列表
     */
	public static void getHidConncetList(final Context context, final HidConncetListListener hidConncetListListener) {
		final int inputDeviceConstant = getInputDeviceConstant();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
			@Override
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				List<BluetoothDevice> hidConncetList = null;
				try {
					if (profile == inputDeviceConstant) {
						hidConncetList = proxy.getConnectedDevices();
					}
				} catch (Exception e) {
				}
				if(hidConncetListListener != null){
					hidConncetListListener.onResult(hidConncetList);
				}
			}

			@Override
			public void onServiceDisconnected(int profile) {
			}
		}, inputDeviceConstant);
	}

	public interface HidConncetListListener {
		void onResult(List<BluetoothDevice> list);
	}

	public interface HidConnectListener{
		void hasConnected();
	}

	public interface HidDisconnectListener{
		void hasDisconnected();
	}
}
