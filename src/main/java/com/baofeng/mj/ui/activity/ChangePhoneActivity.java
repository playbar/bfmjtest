package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.viewutil.SoftKeyBoardManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    private static final int SHORT_MESSAGE_WAIT_TIME = 60;
    private static final int MESSAGE_COUNT_DOWN = 1000;
    private Handler mHandler;
    private int mCurrentSeconds;

    private AppTitleBackView bind_title_layout;
    private EditText tel_num, verify_code;
    private TextView send_code, bind_sure;
    private String mPhoneNumber;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        initView();
    }

    /**
     * View初始化
     */
    private void initView() {
        bind_title_layout = (AppTitleBackView) findViewById(R.id.bind_title_layout);
        bind_title_layout.getNameTV().setText(getResources().getString(R.string.change_phone));
        bind_title_layout.getInvrImgBtn().setVisibility(View.GONE);
        tel_num = (EditText) findViewById(R.id.tel_num);
        verify_code = (EditText) findViewById(R.id.verify_code);
        send_code = (TextView) findViewById(R.id.send_code);
        send_code.setOnClickListener(this);
        bind_sure = (TextView) findViewById(R.id.bind_sure);
        bind_sure.setOnClickListener(this);
        addEditListener(tel_num);
        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.send_code == id) {
            send_code.setEnabled(false);
            getVerifyCode();
        } else if (R.id.bind_sure == id) {
            changePhone();
        }
    }

    /**
     * 绑定手机号获取验证码
     */
    private void getVerifyCode() {
        String telnum = tel_num.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            send_code.setEnabled(true);
            return;
        }
        if (!Common.isMobile(telnum)) {
            Toast.makeText(this, "手机号不合法！", Toast.LENGTH_SHORT).show();
            showToast("发送失败！");
            send_code.setEnabled(true);
            return;
        }
        new UserInfoApi().getVerifyCode(this, telnum, "tel_update", new ApiCallBack<String>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Toast.makeText(ChangePhoneActivity.this, content, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    resetSendBtnState();
                    showToast("发送失败！");
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
                        Toast.makeText(ChangePhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changePhone() {
        String telnum = tel_num.getText().toString().trim();
        String check = verify_code.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            showToast("请输入手机号！");
            return;
        }
        if (!Common.isMobile(telnum)) {
            showToast("手机号不合法！");
            return;
        }
        mPhoneNumber = telnum;
        if (TextUtils.isEmpty(check)) {
            showToast("请输入校验码！");
            return;
        }
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        new UserInfoApi().changePhone(this, userInfo.getUid(), telnum, check, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (result != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        if (jsonObj.getBoolean("status")) {
                            showToast("手机绑定成功");
                            UserSpBusiness.getInstance().setMobile(mPhoneNumber);
                            finish();
                        } else {
                            SoftKeyBoardManager.hideSoftKeyboard(ChangePhoneActivity.this);
                            if(!TextUtils.isEmpty(jsonObj.getString("msg"))){
                                showToast(jsonObj.getString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        resetSendBtnState();
                    }
                } else {
                    SoftKeyBoardManager.hideSoftKeyboard(ChangePhoneActivity.this);
                    showToast("服务器连接失败，请检查网络");
                    resetSendBtnState();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCache(String result) {
                super.onCache(result);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

    private void showToast(String str) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        mToast.show();
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
                send_code.setText(LanguageValue.getInstance().getValue(ChangePhoneActivity.this, "SID_GET_FREE_SMS"));
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
                send_code.setEnabled(true);
                mHandler.removeMessages(MESSAGE_COUNT_DOWN);
            }
            mCurrentSeconds--;
        }
        return false;
    }
    private void resetSendBtnState(){
        send_code.setText(getResources().getString(R.string.get_short_message_for_free));
        send_code.setEnabled(true);
    }
}
