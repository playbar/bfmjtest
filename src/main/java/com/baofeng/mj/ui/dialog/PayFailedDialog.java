package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.baofeng.mj.R;
import com.baofeng.mj.business.accountbusiness.deposit.UserPayBusiness;

/**
 * Created by zhaominglei on 2016/5/21.
 * 支付失败提示框
 */
public class PayFailedDialog extends Dialog implements View.OnClickListener {

    private Button cancel_pay;

    private Button continue_pay;

    public PayFailedDialog(Context context) {
        super(context, R.style.pay_alert_style);
        initView();
    }


    protected PayFailedDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_user_pay_failed, null);
        setContentView(v);
        setCancelable(false);
        cancel_pay = (Button) v.findViewById(R.id.cancel_pay);
        continue_pay = (Button) v.findViewById(R.id.continue_pay);
        cancel_pay.setOnClickListener(this);
        continue_pay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        cancel();
        if (vid == R.id.cancel_pay) {
            UserPayBusiness.getInstance().clear();
        } else if (vid == R.id.continue_pay) {
            UserPayBusiness.getInstance().continuePay();
        }
    }
}
