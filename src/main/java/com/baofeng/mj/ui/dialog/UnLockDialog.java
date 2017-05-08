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
 * 解锁高清Dialog
 * Created by muyu on 2016/9/7.
 */
public class UnLockDialog extends Dialog implements View.OnClickListener {

    private TextView cancel;
    private TextView confirm;
    private TextView contentTv;
    private UnLockCallBack mUnLockCallBack;
    private int width;
    public UnLockDialog(Context context, UnLockCallBack unLockCallBack) {
        super(context, R.style.alertdialog);
        this.mUnLockCallBack = unLockCallBack;
        initView(PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40));
    }

    public UnLockDialog(Context context, UnLockCallBack unLockCallBack,int widht) {
        super(context, R.style.alertdialog);
        this.mUnLockCallBack = unLockCallBack;
        initView(widht);
    }

    private void initView(int width) {
         View v = LayoutInflater.from(getContext()).inflate(
                R.layout.unlock_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width =width;
        setContentView(v, params);
        setCancelable(false);
        contentTv = (TextView)v.findViewById(R.id.content_tv);
        cancel = (TextView) v.findViewById(R.id.unlock_cancel);
        confirm = (TextView) v.findViewById(R.id.unlock_confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }


    public void setContentText(String content){
        if(contentTv!=null) {
            contentTv.setText(content);
        }
    }

    public void setConfirmText(String confirmText){
        if(confirm!=null) {
            confirm.setText(confirmText);
        }
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.unlock_confirm) {
            if (mUnLockCallBack != null) {
                mUnLockCallBack.onConfirm();
            }
        }else if(vid == R.id.unlock_cancel){
            if (mUnLockCallBack != null) {
                mUnLockCallBack.onCancel();
            }
        }
        cancel();
    }

    public interface UnLockCallBack {
        public void onConfirm();
        public void onCancel();
    }
}