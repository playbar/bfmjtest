package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 绑定手机号界面
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private static final int SHORT_MESSAGE_WAIT_TIME = 60;
    private static final int MESSAGE_COUNT_DOWN = 1000;
    private Handler mHandler;
    private int mCurrentSeconds;
    private AppTitleBackView bind_title_layout;
    private EditText tel_num, verify_code;
    private TextView send_code, bind_sure;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        initView();
    }

    /**
     * View初始化
     */
    private void initView() {
        bind_title_layout = (AppTitleBackView) findViewById(R.id.bind_title_layout);
        bind_title_layout.getNameTV().setText("绑定手机号");
        tel_num = (EditText) findViewById(R.id.tel_num);
        verify_code = (EditText) findViewById(R.id.verify_code);
        send_code = (TextView) findViewById(R.id.send_code);
        send_code.setOnClickListener(this);
        bind_sure = (TextView) findViewById(R.id.bind_sure);
        bind_sure.setOnClickListener(this);
        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.send_code == id) {
            send_code.setEnabled(false);
            getVerifyCode();
        } else if (R.id.bind_sure == id) {
            bindPhone();
        }
    }

    /**
     * 绑定手机号获取验证码
     */
    private void getVerifyCode() {
        String telnum = tel_num.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            showToast("请输入手机号！");
            resetSendBtnState();
            return;
        }
        if (!Common.isMobile(telnum)) {
            showToast("手机号不合法！");
            resetSendBtnState();
            return;
        }
        new UserInfoApi().getVerifyCode(this, telnum, "tel_bind", new ApiCallBack<String>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Toast.makeText(BindPhoneActivity.this, content, Toast.LENGTH_SHORT).show();
                resetSendBtnState();
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    showToast("发送失败！");
                    resetSendBtnState();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    //send_code.setText(msg);
                    boolean isSend = jsonObject.getBoolean("status");
                    if (isSend) {
                        mCurrentSeconds = SHORT_MESSAGE_WAIT_TIME;
                        mHandler.sendEmptyMessageDelayed(MESSAGE_COUNT_DOWN, 1000);
                    }else{
                        resetSendBtnState();
                    }
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(BindPhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    resetSendBtnState();
                }
            }
        });
    }

    /**
     * 绑定手机号
     */
    private void bindPhone() {
        String telNum = tel_num.getText().toString().trim();
        if (TextUtils.isEmpty(telNum)) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        if (!Common.isMobile(telNum)) {
            Toast.makeText(this, "手机号不合法！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }

        String verifyCode = verify_code.getText().toString().trim();
        String loginType = getIntent().getStringExtra("loginType");
        String openId = getIntent().getStringExtra("openId");
        new UserInfoApi().bindPhone(this, loginType, openId, telNum, verifyCode, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    showToast("绑定失败！");
                    resetSendBtnState();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean status = jsonObject.getBoolean("status");//true:绑定成功，false:绑定失败
                    String msg = jsonObject.getString("msg");//绑定结果信息
                    if (status) {
                        //jumpToLogin();
                        finish();
                    }
                    if(!TextUtils.isEmpty(msg)){
                        showToast(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    resetSendBtnState();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                showToast(content);
                resetSendBtnState();
            }
        });
    }

    private void showToast(String str) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_COUNT_DOWN) {
            if (mCurrentSeconds > 0) {
                send_code.setText("重新发送(" + mCurrentSeconds + ")");
                send_code.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(MESSAGE_COUNT_DOWN, 1000);
            } else {
                send_code.setText("重发短信");
                send_code.setEnabled(true);
                mHandler.removeMessages(MESSAGE_COUNT_DOWN);
            }
            mCurrentSeconds--;
        }
        return false;
    }

    private void resetSendBtnState() {
        send_code.setEnabled(true);
    }
}
