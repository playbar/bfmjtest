package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.accountbusiness.deposit.UserPayBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.TitleBar;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;

import org.json.JSONObject;

public class UserPayActivity extends BaseActivity implements View.OnClickListener, UserPayBusiness.IUserPayCallback, Handler.Callback {

    private TitleBar ib_pay_back;
    //支付宝
    private LinearLayout ll_payment_zfb;
    //微信
    private LinearLayout ll_payment_wechat;

    private String modouCount;

    private String money;

    private String phoneNumber;

    private Handler mHandler;

    private Toast mToast;

    private boolean mClickLimit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pay);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            modouCount = bundle.getString("modouNum");
            money = bundle.getString("money");
            phoneNumber = bundle.getString("mobile");
        }
        findViewByIds();
        init();
    }

    private void findViewByIds() {
        ib_pay_back = (TitleBar) findViewById(R.id.rl_pay_title);
        ll_payment_zfb = (LinearLayout) findViewById(R.id.ll_payment_zfb);
        ll_payment_wechat = (LinearLayout) findViewById(R.id.ll_payment_wx);
    }

    private void init() {
        ib_pay_back.setTitleBarTitle(getResources().getString(R.string.select_payment));
        ib_pay_back.setOnClickListener(this);
        ib_pay_back.getRightBtn().setVisibility(View.GONE);
        ll_payment_zfb.setOnClickListener(this);
        ll_payment_wechat.setOnClickListener(this);
        UserPayBusiness.getInstance().setUserPayCallback(this);
        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.ll_payment_wx) {
            if (!mClickLimit) {
                mClickLimit = true;
                UserPayBusiness.getInstance().showProgressDialog(this);
                UserPayBusiness.getInstance().startPay(this,
                        phoneNumber, modouCount, money, UserPayBusiness.getInstance().PAYMENT_WX);
            }
        } else if (vid == R.id.ll_payment_zfb) {
            if (!mClickLimit) {
                mClickLimit = true;
                UserPayBusiness.getInstance().startPay(this,
                        phoneNumber, modouCount, money, UserPayBusiness.getInstance().PAYMENT_ZFB);
            }

        } else if (vid == R.id.back) {
            finish();
        }
    }

    @Override
    public void onPayResult(int code, Object obj) {
        Message msg = Message.obtain();
        msg.what = code;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        mClickLimit = false;
        UserPayBusiness.getInstance().cancelProgress();
        if (what == UserPayBusiness.getInstance().PAY_SUCCESS) {
            UserPayBusiness.getInstance().paySuccess(this);
            updateModouCount();
        } else if (what == UserPayBusiness.getInstance().PAY_EXCEPTION
                || what == UserPayBusiness.getInstance().PAY_FAIL) {
            if (msg.obj != null) {
                showToast(String.valueOf(String.valueOf(msg.obj)));
            }
        } else if (what == UserPayBusiness.getInstance().PAY_USER_CANCEL) {
            UserPayBusiness.getInstance().userCancel(this);
        } else if (what == UserPayBusiness.getInstance().ORDER_GENERATE_SUCCESS) {

        } else if (what == UserPayBusiness.getInstance().ORDER_GENERATE_FAIL) {
            if (msg.obj != null) {
                showToast(String.valueOf(msg.obj));
            }
        } else if (what == UserPayBusiness.getInstance().NET_EXCEPTION) {
            showToast(getResources().getString(R.string.network_exception));
        }
        return false;
    }

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mClickLimit = false;
    }

    private void updateModouCount(){
        new UserInfoApi().updateModouCount(this,new ApiCallBack<String>() {
            @Override
            public void onSuccess(String responseBody) {
                super.onSuccess(responseBody);
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getBoolean("status")) {
                        JSONObject joData = json.getJSONObject("data");
                        String recharge_modou = joData
                                .getString("recharge_modou");
                        String gift_modou = joData.getString("gift_modou");
                        UserSpBusiness.getInstance().updateModouCount(recharge_modou, gift_modou);
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
}
