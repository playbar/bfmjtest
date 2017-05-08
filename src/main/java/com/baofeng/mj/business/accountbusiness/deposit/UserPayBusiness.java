package com.baofeng.mj.business.accountbusiness.deposit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.dialog.PayFailedDialog;
import com.baofeng.mj.ui.dialog.PaySuccessDialog;
import com.baofeng.mj.utils.StringUtils;

/**
 * Created by zhaominglei on 2016/5/19.
 */
public class UserPayBusiness {

    private static UserPayBusiness instance;

    public int PAYMENT_ZFB = 1;

    public int PAYMENT_WX = 2;

    public int PAY_SUCCESS = 0x1000;

    public int PAY_FAIL = 0x1001;

    public int PAY_EXCEPTION = 0x1002;

    public int PAY_USER_CANCEL = 0x1003;

    public int ORDER_GENERATE_SUCCESS = 0x1004;

    public int ORDER_GENERATE_FAIL = 0x1005;

    public int NET_EXCEPTION = 0x1006;

    public int NOT_INSTALL = 0x1007;

    private IUserPayCallback userPayCallback;

    private ProgressDialog mProgressDialog;

    private Activity mContext;
    //支付类型
    private int mPayType = 0;

    private String phoneNum = "";

    private String modouCount = "";

    private String money = "";


    private UserPayBusiness() {
    }

    public static UserPayBusiness getInstance() {
        synchronized (UserPayBusiness.class) {
            if (instance == null) {
                instance = new UserPayBusiness();
            }
            return instance;
        }
    }

    /***
     * 微信支付
     *
     * @param context
     */
    private void startWXPay(Activity context, String phoneNum, String modouCount, String money) {
        WXPayUtil.getInstance().startPay(context, phoneNum, modouCount);
    }

    private void startZFBPay(Activity context, String phoneNum, String modouCount, String money) {
        ZFBPayUtil.getInstance().startPay(context, phoneNum, modouCount);
    }

    public void startPay(Activity context, String phoneNum, String modouCount, String money, int type) {
        this.mPayType = type;
        this.mContext = context;
        this.phoneNum = phoneNum;
        this.modouCount = modouCount;
        this.money = money;
        if (type == PAYMENT_ZFB) {
            startZFBPay(context, phoneNum, modouCount, money);
        } else if (type == PAYMENT_WX) {
            startWXPay(context, phoneNum, modouCount, money);
        }
    }

    public void continuePay() {
        if (!StringUtils.isEmpty(phoneNum)
                && !StringUtils.isEmpty(modouCount)
                && !StringUtils.isEmpty(money)) {
            startPay(mContext, phoneNum, modouCount, money, mPayType);
        }
    }

    public void clear() {
        mPayType = 0;
        this.phoneNum = "";
        this.money = "";
        this.modouCount = "";
    }

    public void setUserPayCallback(IUserPayCallback callback) {
        this.userPayCallback = callback;
    }

    /***
     * 用户取消支付
     */
    public void userCancel(Activity context) {
        showPayFailedDialog(context);
    }

    public IUserPayCallback getUserPayCallback() {
        return this.userPayCallback;
    }

    public void showProgressDialog(Activity context) {
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.mProgressDialog.cancel();
            this.mProgressDialog = null;
        }
        if (this.mProgressDialog == null) {
            this.mProgressDialog = new ProgressDialog(context);
        }

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("订单生成中");
        mProgressDialog.show();
    }

    public void cancelProgress() {
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.mProgressDialog.cancel();
            this.mProgressDialog = null;
        }
    }

    /***
     * 支付成功
     */
    public void paySuccess(Activity context) {
        PaySuccessDialog mPaySuccessDialog = new PaySuccessDialog(context, modouCount);
        mPaySuccessDialog.show();
        clear();
    }

    public void showPayFailedDialog(Context context) {
        PayFailedDialog mPayFailedDialog = new PayFailedDialog(context);
        mPayFailedDialog.show();
    }

    public int getPaymentType() {
        return mPayType;
    }

    public void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivityGroup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public interface IUserPayCallback {

        public void onPayResult(int code, Object obj);
    }
}
