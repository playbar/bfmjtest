package com.baofeng.mjgl.pubblico.layout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.input.MojingInputManager;
import com.baofeng.mojing.input.base.MojingInputCallback;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.baofeng.mojing.sdk.login.activity.GLBaseActivity;
import com.baofeng.mojing.sdk.login.utils.SdkUtils;
import com.baofeng.mojing.sdk.login.widget.SdkDoubleLoginViewPage;
import com.bfmj.viewcore.util.GLFocusUtils;
import com.mojing.sdk.pay.service.IAIDLService;

import java.util.Timer;

public class SdkDoubleLoginActivity extends GLBaseActivity implements MojingInputCallback {
    private SdkDoubleLoginViewPage mLoginViewPage;

    private boolean isBound = false;
    private IAIDLService boundService;
    private String mPackageName = "";
    private Timer timer;
    private MojingInputManager joystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            Intent intent = getIntent();
            if (intent != null) {
                mPackageName = intent.getStringExtra("packageName");
            }
        } catch (Exception e) {
        }
        Log.i("adjustSDK", "GetSDKVersion=" + MojingSDK.GetSDKVersion());
        // 关闭陀螺仪
        super.setGroyEnable(false);
        super.onCreate(savedInstanceState);
        // 关闭头控
        GLFocusUtils.closeHeadControl();

        mLoginViewPage = new SdkDoubleLoginViewPage(this);
        getPageManager().push(mLoginViewPage, null);
        joystick = MojingInputManager.getMojingInputManager();

        joystick.AddProtocal(MojingInputManager.Protocol_Bluetooth);
    }

    /**
     * 创建英文键盘
     *
     * @param isUpper 是否大写
     * @return void
     * @author linzanxian  @Date 2015-10-12 上午10:59:21
     */
    public void createEnglistKeyboard(boolean isUpper) {
        if (mLoginViewPage != null) {
            mLoginViewPage.createEnglistKeyboard(mLoginViewPage.getIsUpper());
        }
    }

    /**
     * 设置文本
     *
     * @param text 文本
     * @return void
     * @author linzanxian  @Date 2015-10-12 下午5:04:11
     */
    public void setText(String text) {
        if (mLoginViewPage != null) {
            mLoginViewPage.setText(text);
        }
    }

    @Override
    protected void onResume() {
        joystick.Connect(this, null);
        bindService();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        joystick.Disconnect();
        unbindService();
        super.onPause();

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 绑定服务
     *
     * @return void
     * @author linzanxian  @Date 2015-10-13 上午10:52:22
     */
    private void bindService() {
        Intent mIntent = new Intent();
        mIntent.setAction("android.intent.action.AIDLService");
        mIntent = SdkUtils.getExplicitIntent(getApplicationContext(), mIntent, mPackageName);
        if (mIntent == null) {
            return;
        }

        Intent eintent = new Intent(mIntent);

        bindService(eintent, connection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    /**
     * 解绑服务
     *
     * @return void
     * @author linzanxian  @Date 2015-10-13 上午10:52:40
     */
    private void unbindService() {
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    /**
     * 服务连接
     */
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = IAIDLService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    public void onLoginCallback(String uid) {
        if (boundService != null) {
            try {
                boundService.loginCallback(uid);

                this.finish();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBluetoothAdapterStateChanged(int i) {

    }

    @Override
    public boolean onMojingKeyDown(String s, int i) {
        if (i != MojingKeyCode.KEYCODE_DPAD_CENTER) {
            if (i == MojingKeyCode.KEYCODE_ENTER
                    || i == MojingKeyCode.KEYCODE_BUTTON_A/*轴模式确定*/
                    || i == MojingKeyCode.KEYCODE_BUTTON_L2) {//五代手柄确定
                i = MojingKeyCode.KEYCODE_DPAD_CENTER;
            } else if (i == MojingKeyCode.KEYCODE_BUTTON_B/*轴模式返回*/
                    || i == MojingKeyCode.KEYCODE_BUTTON_L1) {//五代手柄返回
                i = MojingKeyCode.KEYCODE_BACK;
            }
            onZKeyDown(i);
        }

        return false;
    }

    @Override
    public boolean onMojingKeyUp(String s, int i) {
        if (!mLoginViewPage.isKeyboard && (
                i == MojingKeyCode.KEYCODE_BUTTON_B/*轴模式返回*/
                        || i == MojingKeyCode.KEYCODE_BUTTON_L1//五代手柄返回
                        || i == MojingKeyCode.KEYCODE_BACK)) {
            //返回键监听
            finish();
        }
        if (i != MojingKeyCode.KEYCODE_DPAD_CENTER) {
            if (i == MojingKeyCode.KEYCODE_ENTER
                    || i == MojingKeyCode.KEYCODE_BUTTON_A/*轴模式确定*/
                    || i == MojingKeyCode.KEYCODE_BUTTON_L2) {//五代手柄确定
                i = MojingKeyCode.KEYCODE_DPAD_CENTER;
            } else if (i == MojingKeyCode.KEYCODE_BUTTON_B/*轴模式返回*/
                    || i == MojingKeyCode.KEYCODE_BUTTON_L1) {//五代手柄返回
                i = MojingKeyCode.KEYCODE_BACK;
            }
            onZKeyUp(i);
        }

        return false;
    }

    @Override
    public boolean onMojingKeyLongPress(String s, int i) {
        onZKeyLongPress(i);
        return false;
    }

    @Override
    public boolean onMojingMove(String deviceName, int axis, float x, float y, float z) {
        return false;
    }

    //适配手柄轴模式
    private boolean inputFlag = true;

    @Override
    public boolean onMojingMove(String deviceName, int axis, float value) {
        if (axis == 0) {//x轴
            if (inputFlag) {
                inputFlag = false;
                if (value > 0.5) {//右
                    onZKeyDown(MojingKeyCode.KEYCODE_DPAD_RIGHT);
                } else if (value < -0.5) {//左
                    onZKeyDown(MojingKeyCode.KEYCODE_DPAD_LEFT);
                } else if (Math.abs(value) < 0.5) {
                    inputFlag = true;
                }
            }
        } else if (axis == 1) {//y轴
            if (inputFlag) {
                inputFlag = false;
                if (value > 0.5) {//下
                    onZKeyDown(MojingKeyCode.KEYCODE_DPAD_DOWN);
                } else if (value < -0.5) {//上
                    onZKeyDown(MojingKeyCode.KEYCODE_DPAD_UP);
                } else if (Math.abs(value) < 0.5) {
                    inputFlag = true;
                }
            }
        }
        return false;
    }

    @Override
    public void onMojingDeviceAttached(String s) {
    }

    @Override
    public void onMojingDeviceDetached(String s) {
    }

    @Override
    public void onTouchPadStatusChange(String deviceName, boolean bisTouched) {

    }

    @Override
    public void onTouchPadPos(String deviceName, float x, float y) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (joystick.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (this.joystick.dispatchGenericMotionEvent(event)) {
            return true;
        }
        return super.dispatchGenericMotionEvent(event);
    }
}
