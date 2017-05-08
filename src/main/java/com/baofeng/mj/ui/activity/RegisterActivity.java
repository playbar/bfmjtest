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
 * 注册页面
 * Created by muyu on 2016/5/30.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private static final int SHORT_MESSAGE_WAIT_TIME = 60;
    private static final int MESSAGE_COUNT_DOWN = 1000;
    private Handler mHandler;
    private int mCurrentSeconds;
    private AppTitleBackView register_title_layout;
    private TextView send_code, registe_password_eye, register_sure, register_mojing_login, login_mobile_pre,
            registe_password_tag, login_code_pre, login_one_tag;
    private EditText tel_num, registe_password, verify_code;
    private ImageView register_sina, register_qq, register_weixin, register_storm;
    private boolean isSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        register_title_layout = (AppTitleBackView) findViewById(R.id.register_title_layout);
        register_title_layout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_REGISTER"));
        register_title_layout.getInvrImgBtn().setVisibility(View.GONE);
        send_code = (TextView) findViewById(R.id.send_code);
        send_code.setText(LanguageValue.getInstance().getValue(this, "SID_GET_FREE_SMS"));
        tel_num = (EditText) findViewById(R.id.tel_num);
        tel_num.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_PHONE_NUM"));
        registe_password = (EditText) findViewById(R.id.registe_password);
        registe_password.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_PASSWORD"));
        registe_password_eye = (TextView) findViewById(R.id.registe_password_eye);
        register_sure = (TextView) findViewById(R.id.register_sure);
        register_sure.setText(LanguageValue.getInstance().getValue(this, "SID_REGISTER"));
        verify_code = (EditText) findViewById(R.id.verify_code);
        verify_code.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_SMS_CODE"));
        register_mojing_login = (TextView) findViewById(R.id.register_mojing_login);
        register_mojing_login.setText(LanguageValue.getInstance().getValue(this, "SID_MOJING_ACCOUNT_LOGIN"));
        register_sina = (ImageView) findViewById(R.id.register_sina);
        register_qq = (ImageView) findViewById(R.id.register_qq);
        register_weixin = (ImageView) findViewById(R.id.register_weixin);
        register_storm = (ImageView) findViewById(R.id.register_storm);
        login_mobile_pre = (TextView) findViewById(R.id.login_mobile_pre);
        login_mobile_pre.setText(LanguageValue.getInstance().getValue(this, "SID_PHONE_NUM"));
        registe_password_tag = (TextView) findViewById(R.id.registe_password_tag);
        registe_password_tag.setText(LanguageValue.getInstance().getValue(this, "SID_PASSWORD"));
        login_code_pre = (TextView) findViewById(R.id.login_code_pre);
        login_code_pre.setText(LanguageValue.getInstance().getValue(this, "SID_Verification_code"));
        login_one_tag = (TextView) findViewById(R.id.login_one_tag);
        login_one_tag.setText(LanguageValue.getInstance().getValue(this, "SID_ACCOUNT_ONE_KEY_LOGIN"));
        addEditListener(tel_num);
        addEditListener(registe_password);
        register_mojing_login.setOnClickListener(this);
        register_sure.setOnClickListener(this);
        registe_password_eye.setOnClickListener(this);
        send_code.setOnClickListener(this);
        //第三方登陆
        register_sina.setOnClickListener(this);
        register_qq.setOnClickListener(this);
        register_weixin.setOnClickListener(this);
        register_storm.setOnClickListener(this);
        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.register_mojing_login) {
            jumpToLogin();
        } else if (R.id.registe_password_eye == i) {
            int type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            int length = registe_password.getText().length();
            if (type == registe_password.getInputType()) {
                registe_password_eye.setBackground(getResources().getDrawable(R.drawable.register_icon_eye_sel));
                registe_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                registe_password.setSelection(length);
            } else {
                registe_password_eye.setBackground(getResources().getDrawable(R.drawable.register_icon_eye));
                registe_password.setInputType(type);
                registe_password.setSelection(length);
            }
        } else if (R.id.send_code == i) {
            send_code.setEnabled(false);
            getVerifyCode();
        } else if (R.id.register_sure == i) {
            regist();
        } else if (i == R.id.register_sina) {//新浪注册
            startNativeLoginActivity(1);
            finish();
        } else if (i == R.id.register_qq) {//qq注册
            startNativeLoginActivity(2);
            finish();
        } else if (i == R.id.register_weixin) {//微信注册
            startNativeLoginActivity(3);
        } else if (i == R.id.register_storm) {//暴风注册
            Intent registerIntent = new Intent(this, StormLoginActivity.class);
            startActivity(registerIntent);
        }
    }

    /**
     * 发送验证码
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
        new UserInfoApi().getVerifyCode(this, telnum, "tel_regist", new ApiCallBack<String>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                isSend = false;
                if (!NetworkUtil.isNetworkConnected(RegisterActivity.this)) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.load_data_failure), Toast.LENGTH_SHORT).show();
                }
                resetSendBtnState();
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    isSend = false;
                    resetSendBtnState();
                    Toast.makeText(RegisterActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    isSend = jsonObject.getBoolean("status");
                    if(isSend){
                        mCurrentSeconds = SHORT_MESSAGE_WAIT_TIME;
                        mHandler.sendEmptyMessageDelayed(MESSAGE_COUNT_DOWN, 1000);
                    }else{
                        resetSendBtnState();
                    }
                    if(!TextUtils.isEmpty(msg)){
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    //send_code.setText(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                    resetSendBtnState();
                }
            }
        });
    }

    private void regist() {
        String telnum = tel_num.getText().toString().trim();
        String password = registe_password.getText().toString().trim();
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
        if (!isSend) {
            Toast.makeText(this, "请首先获取验证码！", Toast.LENGTH_SHORT).show();
            resetSendBtnState();
            return;
        }
        new UserInfoApi().regist(this, telnum, password, verifyCode, new ApiCallBack<String>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!NetworkUtil.isNetworkConnected(RegisterActivity.this)) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.load_data_failure), Toast.LENGTH_SHORT).show();
                }
                resetSendBtnState();
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                    resetSendBtnState();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean status = jsonObject.getBoolean("status");
                    String uid = jsonObject.getString("msg");
                    if (status) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                        finish();
                    }
                    if(!TextUtils.isEmpty(uid)){
                        Toast.makeText(RegisterActivity.this, uid, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void startNativeLoginActivity(int loginType) {
        if (!NoDoubleClickUtils.isDoubleClick()) {
            Intent intent = new Intent(this, NativeLoginActivity.class);
            intent.putExtra("type", "reg");
            intent.putExtra("loginType", loginType);
            startActivity(intent);
        }
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
                send_code.setText(LanguageValue.getInstance().getValue(RegisterActivity.this, "SID_GET_FREE_SMS"));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_COUNT_DOWN) {
            if (mCurrentSeconds > 0) {
                send_code.setText("重新发送(" + mCurrentSeconds + ")");
                mHandler.sendEmptyMessageDelayed(MESSAGE_COUNT_DOWN, 1000);
            } else {
                send_code.setText("重发短信");
                resetSendBtnState();
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
