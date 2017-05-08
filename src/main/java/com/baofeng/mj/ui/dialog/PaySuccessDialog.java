package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.activity.RechargeRecordActivity;

/**
 * Created by zhaominglei on 2016/5/21.
 * 支付成功提示框
 */
public class PaySuccessDialog extends Dialog implements View.OnClickListener {

    private Button view_recharge_record;

    private Button confirm;

    private TextView modouCount;

    private String sModouCount;

    private Context mContext;

    public PaySuccessDialog(Context context, String modouCount) {
        super(context, R.style.pay_alert_style);
        mContext = context;
        sModouCount = modouCount;
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_user_pay_success, null);
        setContentView(v);
        setCancelable(false);
        modouCount = (TextView) v.findViewById(R.id.modou_count);
        if (!TextUtils.isEmpty(sModouCount)) {
            modouCount.setText(sModouCount + "魔币已成功存入你的帐号");
        }
        view_recharge_record = (Button) v.findViewById(R.id.btn_view_recharge_record);
        confirm = (Button) v.findViewById(R.id.ok);
        view_recharge_record.setOnClickListener(this);
        confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.ok) {
        } else if (vid == R.id.btn_view_recharge_record) {
            Intent intent = new Intent(mContext, RechargeRecordActivity.class);
            mContext.startActivity(intent);
        }
        cancel();
    }
}
