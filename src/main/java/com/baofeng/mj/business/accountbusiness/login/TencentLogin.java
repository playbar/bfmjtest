package com.baofeng.mj.business.accountbusiness.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.ui.activity.BindPhoneActivity;
import com.baofeng.mj.ui.activity.LoginActivity;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhaominglei on 2016/5/16.
 * QQ登录
 */
public class TencentLogin extends BaseLoginManager {

    private static boolean isServerSideLogin = false;
    public static Tencent mTencent;
    private static String mAppid;
    private UserInfo mInfo;
    private static String mOpenId;
    private static String mOpenType;
    private Handler mHandler = new Handler();
    private BaseUiListener mLoginListener = new BaseUiListener();
    private Context mContext;
    public TencentLogin(Context context, String type) {
        super(context, type);
        mContext = context;
        mAppid = "1104291863";
        mOpenType = "login_qq";
        if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, context);
        }
    }

    @Override
    public void onClickLogin() {
       /* if(!ApkUtil.isAppInstalled("com.tencent.mobileqq")){
            Toast.makeText(mContext, R.string.not_install_qq, Toast.LENGTH_SHORT).show();
            ((Activity)mContext).finish();
            return;
        }
*/
        if (!mTencent.isSessionValid()) {
            mTencent.login((Activity) mContext, "mojingbaofeng", mLoginListener);
            isServerSideLogin = false;
        } else {
            mTencent.logout(mContext);
            mTencent.login((Activity) mContext, "mojingbaofeng", mLoginListener);
            isServerSideLogin = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResultData(requestCode, resultCode, data, mLoginListener);
    }

    /***
     * 获取token
     *
     * @param jsonObject
     */
    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            mOpenId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(mOpenId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(mOpenId);
            }
            //通过openid 获取用户id
            queryUserIdByOpenId();

        } catch (Exception e) {
        }
    }

    /***
     * 通过openid 查询用户信息
     */
    private void queryUserIdByOpenId() {
        new UserInfoApi().queryUserIdByOpenId(mContext, mOpenType, mOpenId, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        //找到用户信息的
                        queryUserInfoById(jsonObject.getString("msg"));
                    } else {
                        //没有找到用户信息
                        startPhoneBindActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
    }

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     */
    private void queryUserInfoById(final String id) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new UserInfoApi().queryUserInfoById(mContext, id, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
                    @Override
                    public void onSuccess(Response<List<SimpleUserInfo>> result) {
                        super.onSuccess(result);
                        if (result.data != null && result.data.size() > 0) {
                            SimpleUserInfo userInfo = result.data.get(0);
                            if (TextUtils.isEmpty(userInfo.user_tel)) {
                                startPhoneBindActivity();
                            } else {
                                startAccountActivity();
                            }
                        }else{
                            startPhoneBindActivity();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        startLoginActivity();
                    }
                });
            }
        });
    }

    /***
     * QQ登陆listener
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                return;
            }
            initOpenidAndToken((JSONObject) response);
        }

        @Override
        public void onError(UiError e) {
            startLoginActivity();
        }

        @Override
        public void onCancel() {
            startLoginActivity();
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
        }
    }

    //登陆过程异常 返回登陆页
    private void startLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

    //手机号码为空时
    private void startPhoneBindActivity() {
        Intent intent = new Intent(mContext, BindPhoneActivity.class);
        intent.putExtra("loginType", mOpenType);
        intent.putExtra("openId", mOpenId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

    //登陆成功
    private void startAccountActivity() {
//        Intent intent = new Intent(mContext, AccountActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        mContext.startActivity(intent);

        if(null != LoginActivity.loginActivity) {
            LoginActivity.loginActivity.finish();
        }
        ((Activity)mContext).finish();
    }
}
