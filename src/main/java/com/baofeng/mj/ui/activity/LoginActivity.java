package com.baofeng.mj.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.RequestResponseCode;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.GameApi;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.NoDoubleClickUtils;
import com.baofeng.mj.util.threadutil.HistoryProxy;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.baofeng.mj.util.viewutil.LanguageValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 登录页面
 * Created by muyu on 2016/5/30.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private AppTitleBackView backView;
    private TextView loginTV;
    private TextView forgotTV, login_with_info;
    private TextView registerTV, login_password_eye;

    private ImageView sinaIV;
    private ImageView qqIV;
    private ImageView weixinIV;
    private ImageView stormIV;

    private EditText et_phonenum;
    private EditText et_password;
    private boolean isLogined = false;
    private TextView login_mobile_pre, login_password_pre;//标签

    public static Activity loginActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        if (null != getIntent() && getIntent().hasExtra("uid")) {
            queryUserInfoById(getIntent().getStringExtra("uid"));
        }
        et_phonenum = (EditText) findViewById(R.id.et_phonenum);
        et_phonenum.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_PHONE_NUM"));
        et_password = (EditText) findViewById(R.id.et_password);
        et_password.setHint(LanguageValue.getInstance().getValue(this, "SID_INPUT_PASSWORD"));
        login_password_eye = (TextView) findViewById(R.id.login_password_eye);
        login_password_eye.setOnClickListener(this);

        backView = (AppTitleBackView) findViewById(R.id.login_title_layout);
        backView.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_LOGIN"));
        backView.getInvrImgBtn().setVisibility(View.GONE);
        loginTV = (TextView) findViewById(R.id.login_sure);
        loginTV.setText(LanguageValue.getInstance().getValue(this, "SID_LOGIN"));
        loginTV.setOnClickListener(this);
        login_with_info = (TextView) findViewById(R.id.login_with_info);
        login_with_info.setText(LanguageValue.getInstance().getValue(this, "SID_ACCOUNT_ONE_KEY_LOGIN"));
        login_mobile_pre = (TextView) findViewById(R.id.login_mobile_pre);
        login_password_pre = (TextView) findViewById(R.id.login_password_pre);
        login_mobile_pre.setText(LanguageValue.getInstance().getValue(this, "SID_PHONE_NUM"));
        login_password_pre.setText(LanguageValue.getInstance().getValue(this, "SID_PASSWORD"));

        forgotTV = (TextView) findViewById(R.id.login_forgot_password);
        forgotTV.setText(LanguageValue.getInstance().getValue(this, "SID_FORGOT_PASSWORD"));
        registerTV = (TextView) findViewById(R.id.login_register);
        registerTV.setText(LanguageValue.getInstance().getValue(this, "SID_NEW_ACCOUNT_REGISTER"));
        forgotTV.setOnClickListener(this);
        registerTV.setOnClickListener(this);

        sinaIV = (ImageView) findViewById(R.id.login_sina);
        qqIV = (ImageView) findViewById(R.id.login_qq);
        weixinIV = (ImageView) findViewById(R.id.login_weixin);
        stormIV = (ImageView) findViewById(R.id.login_storm);

        sinaIV.setOnClickListener(this);
        qqIV.setOnClickListener(this);
        weixinIV.setOnClickListener(this);
        stormIV.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != loginActivity) {
            loginActivity = null;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_sure) {//登录
            login();//登录
        } else if (i == R.id.login_forgot_password) {//忘记密码
            Intent forgotIntent = new Intent(this, ResetPwdActivity.class);
            forgotIntent.putExtra("toPage", "forgot");
            startActivity(forgotIntent);

        } else if (i == R.id.login_register) {//注册
            Intent registerIntent = new Intent(this, RegisterActivity.class);
            registerIntent.putExtra("toPage", "register");
            startActivity(registerIntent);
            finish();

        } else if (i == R.id.login_sina) {//新浪登录
            startNativeActivity(1);
        } else if (i == R.id.login_qq) {//qq登录
            startNativeActivity(2);
        } else if (i == R.id.login_weixin) {//微信登录
            startNativeActivity(3);
        } else if (i == R.id.login_storm) {//暴风登录
            LoginActivity.loginActivity = this;
            Intent registerIntent = new Intent(this, StormLoginActivity.class);
            startActivity(registerIntent);
        } else if (i == R.id.login_password_eye) {
            int type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            int length = et_password.getText().length();
            if (type == et_password.getInputType()) {
                login_password_eye.setBackground(getResources().getDrawable(R.drawable.register_icon_eye_sel));
                et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                et_password.setSelection(length);
            } else {
                login_password_eye.setBackground(getResources().getDrawable(R.drawable.register_icon_eye));
                et_password.setInputType(type);
                et_password.setSelection(length);
            }
        }
    }

    private void startNativeActivity(int type) {
        if (!NoDoubleClickUtils.isDoubleClick()) {
            LoginActivity.loginActivity = this;
            Intent intent = new Intent(this, NativeLoginActivity.class);
            intent.putExtra("type", "login");
            intent.putExtra("loginType", type);
            startActivity(intent);
        }
    }

    /**
     * 登录
     */
    private void login() {
        String phoneNum = et_phonenum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Common.isMobile(phoneNum)) {
            Toast.makeText(this, "手机号不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = et_password.getText().toString().trim();
        if (password.toString().length() < 6) {
            Toast.makeText(this, "密码长度为6-32位！", Toast.LENGTH_SHORT).show();
            return;
        }
        new UserInfoApi().login(this, phoneNum, password, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject loginJo = new JSONObject(result);
                        if (loginJo.getBoolean("status")) {//登录成功
                            sendComment();
                            JSONObject data = loginJo.getJSONObject("data");
                            if (!data.isNull("user_no")) {
                                queryUserInfoById(data.getString("user_no"));//查询用户信息
                            }
                        } else {//登录失败
                            Toast.makeText(LoginActivity.this, loginJo.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!NetworkUtil.isNetworkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.load_data_failure), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (isLogined) {
            new UserInfoApi().updateModouCount(this, new ApiCallBack<String>() {
                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                }

                @Override
                public void onSuccess(String result) {
                    super.onSuccess(result);
                    try {
                        JSONObject json = new JSONObject(new String(result));
                        if (json.getBoolean("status")) {
                            JSONObject joData = json.getJSONObject("data");
                            String recharge_modou = joData
                                    .getString("recharge_modou");
                            String gift_modou = joData.getString("gift_modou");
                            UserSpBusiness.getInstance().updateModouCount(recharge_modou, gift_modou);
                        } else {
                            if ("100030".equals(json.getString("code"))) {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            return;
        }
    }

    /**
     * 查询用户信息
     */
    private void queryUserInfoById(String uid) {
        new UserInfoApi().queryUserInfoById(this, uid, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
            @Override
            public void onSuccess(Response<List<SimpleUserInfo>> result) {
                super.onSuccess(result);
                isLogined = true;
                //上报在线播放历史
                HistoryProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                    @Override
                    public void run() {
                        HistoryBusiness.reportAllCinemaTempHistory();
                    }
                });
                setResult(RequestResponseCode.RESPONSE_CODE_LOGIN, new Intent());
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Toast.makeText(LoginActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 未登录发表评论，登陆之后直接发布
     */
    private void sendComment() {
        if (getIntent() != null && getIntent().hasExtra("content") && getIntent().hasExtra("score")) {
            String uid = UserSpBusiness.getInstance().getUid();
            String res_id = getIntent().getStringExtra("id");
            String nickName = UserSpBusiness.getInstance().getNickName();
            String res_name = getIntent().getStringExtra("title");
            int score = getIntent().getIntExtra("score", 0);
            String content = getIntent().getStringExtra("content");
            if (checkComment(content, score)) {

                if (nickName == null || "".equals(nickName)) {
                    nickName = "0";
                }
                new GameApi().sendComment(this, uid, nickName, res_id, 100, res_name, score, content, new ApiCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            if (jsonObject.getInt("status") == 1) {
                                Toast.makeText(LoginActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        Toast.makeText(LoginActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    /**
     * 检测评论内容和评分是否符合规范
     *
     * @return
     */
    private boolean checkComment(String content, int score) {
        if (content.length() < 5) {
            return false;
        }
        if (score == 0) {
            return false;
        }
        return true;
    }
}
