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
 * Created by wanghongfang on 2017/2/23.
 * 手柄校验未连接时对话框
 */
public class JoyStickCheckDialog  extends Dialog implements View.OnClickListener {
      DialogCallBack mCallBack;
    public JoyStickCheckDialog(Context context,DialogCallBack callBack) {
        super(context, R.style.alertdialog);
        this.mCallBack = callBack;
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.logout_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        setContentView(v, params);
        setCancelable(false);
        TextView cancel = (TextView) v.findViewById(R.id.logout_cancel);
        TextView confirm = (TextView) v.findViewById(R.id.logout_confirm);
        TextView content = (TextView) v.findViewById(R.id.content_tv);
        content.setText(getContext().getResources().getString(R.string.joystick_check_dialog));
        confirm.setText(getContext().getResources().getString(R.string.joystick_connect_instruction));
        confirm.setTextColor(getContext().getResources().getColor(R.color.theme_main_color));
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.logout_confirm) {
            if (mCallBack != null) {
                mCallBack.onConfirm();
            }
        }
        cancel();
    }

    public interface DialogCallBack {
        public void onConfirm();
    }
}
