package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.NoDoubleClickUtils;
import com.baofeng.mj.util.viewutil.LanguageValue;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 重置密码界面
 */
public class ResetPwdActivity extends BaseActivity implements View.OnClickListener ,Handler.Callback{
    private static final int SHORT_MESSAGE_WAIT_TIME = 60;
    private static final int MESSAGE_COUNT_DOWN = 1000;
    private Handler mHandler;
    private int mCurrentSeconds;
    private AppTitleBackView appTitleBackView;
    private TextView reset_sure, send_code, reset_password_eye, reset_mojing_login, login_mobile_pre,
            registe_password_tag, login_code_pre, login_one_key;
    private EditText tel_num, reset_password, verify_code;

    private ImageView sinaIV;
    private ImageView qqIV;
    private ImageView weixinIV;
    private ImageView stormIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        initView();
    }

    /**
     * 初始化view
     */
    public void initView() {
        appTitleBackView = (AppTitleBackView) findViewById(R.id.reset_title_layout);
        appTitleBackView.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_RETAKE_PASSWORD"));
        appTitleBackView.getInvrImgBtn().setVisibility(View.GONE);
        reset_sure = (TextView) findViewById(R.id.reset_sure);
        reset_sure.setText(LanguageValue.getInstance().getValue(this, "SID_SAVE"));
        send_code = (TextView) findViewById(R.id.send_code);
        tel_num = (EditText) findViewById(R.id.tel_num);
        tel_num.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_PHONE_NUM"));
        reset_password = (EditText) findViewById(R.id.reset_password);
        verify_code = (EditText) findViewById(R.id.verify_code);
        verify_code.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_SMS_CODE"));
        reset_password_eye = (TextView) findViewById(R.id.reset_password_eye);
        reset_mojing_login = (TextView) findViewById(R.id.reset_mojing_login);
        reset_mojing_login.setText(LanguageValue.getInstance().getValue(this, "SID_MOJING_ACCOUNT_LOGIN"));
        reset_mojing_login.setOnClickListener(this);
        reset_password_eye.setOnClickListener(this);
        send_code.setOnClickListener(this);
        send_code.setText(LanguageValue.getInstance().getValue(this, "SID_GET_FREE_SMS"));
        reset_sure.setOnClickListener(this);

        sinaIV = (ImageView) findViewById(R.id.login_sina);
        qqIV = (ImageView) findViewById(R.id.login_qq);
        weixinIV = (ImageView) findViewById(R.id.login_weixin);
        stormIV = (ImageView) findViewById(R.id.login_storm);

        sinaIV.setOnClickListener(this);
        qqIV.setOnClickListener(this);
        weixinIV.setOnClickListener(this);
        stormIV.setOnClickListener(this);

        login_mobile_pre = (TextView) findViewById(R.id.login_mobile_pre);
        login_mobile_pre.setText(LanguageValue.getInstance().getValue(this, "SID_PHONE_NUM"));
        registe_password_tag = (TextView) findViewById(R.id.registe_password_tag);
        registe_password_tag.setText(LanguageValue.getInstance().getValue(this, "SID_PASSWORD"));
        login_code_pre = (TextView) findViewById(R.id.login_code_pre);
        login_code_pre.setText(LanguageValue.getInstance().getValue(this, "SID_Verification_code"));
        login_one_key = (TextView) findViewById(R.id.login_one_key);
        login_one_key.setText(LanguageValue.getInstance().getValue(this, "SID_ACCOUNT_ONE_KEY_LOGIN"));
        addEditListener(tel_num);
        addEditListener(reset_password);
        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.reset_sure == id) {
            resetPwd();
        } else if (R.id.send_code == id) {
            send_code.setEnabled(false);
            getVerifyCode();
        } else if (R.id.reset_password_eye == id) {
            hideOrShowPwd();
        } else if (R.id.reset_mojing_login == id) {
            jumpToLogin();
        } else if (id == R.id.login_sina) {//新浪登录
            startNativeActivity(1);
        } else if (id == R.id.login_qq) {//qq登录
            startNativeActivity(2);
        } else if (id == R.id.login_weixin) {//微信登录
            startNativeActivity(3);
        } else if (id == R.id.login_storm) {//暴风登录
            Intent registerIntent = new Intent(this, StormLoginActivity.class);
            startActivity(registerIntent);
        }
    }

    /**
     * 获取重设密码的验证码
     */
    private void getVerifyCode() {
        String telnum = tel_num.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        if (!Common.isMobile(telnum)) {
            Toast.makeText(this, "手机号不合法！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        new UserInfoApi().getVerifyCode(this, telnum, "sec_update", new ApiCallBack<String>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!NetworkUtil.isNetworkConnected(ResetPwdActivity.this)) {
                    Toast.makeText(ResetPwdActivity.this, getResources().getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPwdActivity.this, getResources().getString(R.string.load_data_failure), Toast.LENGTH_SHORT).show();
                }
                resetSendBtnState();
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(ResetPwdActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                    resetSendBtnState();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean isSend = jsonObject.getBoolean("status");
                    if(isSend){
                        mCurrentSeconds = SHORT_MESSAGE_WAIT_TIME;
                        mHandler.sendEmptyMessageDelayed(MESSAGE_COUNT_DOWN, 1000);
                    }else{
                        resetSendBtnState();
                    }
                    if(!TextUtils.isEmpty(msg)){
                        Toast.makeText(ResetPwdActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    resetSendBtnState();
                }
            }
        });
    }

    /**
     * 重置密码
     */
    private void resetPwd() {
        String telnum = tel_num.getText().toString().trim();
        String password = reset_password.getText().toString().trim();
        String verifyCode = verify_code.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        if (!Common.isMobile(telnum)) {
            Toast.makeText(this, "手机号不合法！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        if (password.toString().length() < 6) {
            Toast.makeText(this, "密码长度为6-32位！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(this, "请输入校验码！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        new UserInfoApi().resetPwd(this, telnum, password, verifyCode, new ApiCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        if (TextUtils.isEmpty(result)) {
                            Toast.makeText(ResetPwdActivity.this, "重新设置失败！", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            boolean status = jsonObject.getBoolean("status");//判断结果，true：成功，false：失败
                            String msg = jsonObject.getString("msg");//结果信息
                            if (status) {
                                jumpToLogin();
                            }
                            if(!TextUtils.isEmpty(msg)){
                                Toast.makeText(ResetPwdActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resetSendBtnState();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        if (!NetworkUtil.isNetworkConnected(ResetPwdActivity.this)) {
                            Toast.makeText(ResetPwdActivity.this, getResources().getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPwdActivity.this, getResources().getString(R.string.load_data_failure), Toast.LENGTH_SHORT).show();
                        }
                        resetSendBtnState();
                    }
                }

        );
    }

    /***
     * 显示或隐藏密码
     */
    private void hideOrShowPwd() {
        int type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        int length = reset_password.getText().length();
        if (length == 0) {
            return;
        }
        if (type == reset_password.getInputType()) {
            reset_password_eye.setBackground(getResources().getDrawable(R.drawable.register_icon_eye_sel));
            reset_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            reset_password.setSelection(length);
        } else {
            reset_password_eye.setBackground(getResources().getDrawable(R.drawable.register_icon_eye));
            reset_password.setInputType(type);
            reset_password.setSelection(length);
        }
    }

    /**
     * 跳转到登录界面
     */
    private void jumpToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * 监听editText
     *
     * @param editText
     */
    private void addEditListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                send_code.setText(LanguageValue.getInstance().getValue(ResetPwdActivity.this, "SID_GET_FREE_SMS"));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void startNativeActivity(int type) {
        if (!NoDoubleClickUtils.isDoubleClick()) {
            Intent intent = new Intent(this, NativeLoginActivity.class);
            intent.putExtra("type", "login");
            intent.putExtra("loginType", type);
            startActivity(intent);
        }
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

    private void resetSendBtnState(){
        send_code.setEnabled(true);
    }
}
