package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by zhaominglei on 2016/5/21.
 * 支付成功提示框
 */
public class LogoutDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextView cancel;

    private TextView confirm;

    private LogoutCallBack mLogoutCallBack;

    public LogoutDialog(Context context, LogoutCallBack logoutCallBack) {
        super(context, R.style.alertdialog);
        mContext = context;
        this.mLogoutCallBack = logoutCallBack;
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.logout_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        setContentView(v, params);
        setCancelable(false);
        cancel = (TextView) v.findViewById(R.id.logout_cancel);
        confirm = (TextView) v.findViewById(R.id.logout_confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.logout_confirm) {
            if (mLogoutCallBack != null) {
                mLogoutCallBack.onConfirm();
            }
        }
        cancel();
    }

    public interface LogoutCallBack {
        public void onConfirm();
    }
}
