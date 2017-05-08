package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.accountbusiness.ExperienceReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;

/**
 * Created by zhaominglei on 2016/5/21.
 * 体验报告
 */
public class ExperienceReportDialog extends Dialog implements View.OnClickListener {

    private TextView mCancel;
    private TextView mConfirm;
    private Context mContext;

    public ExperienceReportDialog(Context context) {
        super(context, R.style.pay_alert_style);
        mContext = context;
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_experience_report, null);
        setContentView(v);
        setCancelable(false);

        mConfirm = (TextView) v.findViewById(R.id.tv_ok);
        mCancel = (TextView) v.findViewById(R.id.tv_cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.tv_ok) {
            ExperienceReportBusiness.getInstance().startWebExperienceReportActivity(mContext);
        } else if (vid == R.id.tv_cancel) {
            int count = SettingSpBusiness.getInstance().getReportDialogCancelCount();
            SettingSpBusiness.getInstance().setReportDialogCancelCount(count++);
        }
        cancel();
    }
}
