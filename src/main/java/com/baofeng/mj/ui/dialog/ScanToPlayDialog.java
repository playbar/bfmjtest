package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by hanyang on 2016/7/6.
 */
public class ScanToPlayDialog {
    private Dialog dialog;//对话框
    private MyDialogInterface myDialogInterface;//回调接口
    private MyDialogInterfaceCancel myDialogInterfaceCancel;//回调接口
    private TextView tv_clear;

    public ScanToPlayDialog(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_scan_toplay, null);//生成布局文件
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
        dialog.setContentView(view, params);//设置布局文件
        dialog.setCancelable(true);//true可以点击返回键取消对话框
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);//左面按钮
        tv_clear = (TextView) view.findViewById(R.id.tv_clear);//右面按钮
        //左面按钮点击事件
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDialogInterfaceCancel != null) {
                    myDialogInterfaceCancel.dialogCallBack();
                }
                dialog.dismiss();
            }
        });
        //右面按钮点击事件
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDialogInterface != null) {
                    myDialogInterface.dialogCallBack();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(MyDialogInterface myDialogInterface) {
        if (dialog != null && !dialog.isShowing()) {
            this.myDialogInterface = myDialogInterface;
            dialog.show();
        }
    }

    /**
     * 显示对话框
     */
    public void showDialog(MyDialogInterface myDialogInterface, MyDialogInterfaceCancel myDialogInterfaceCancel, String text) {
        if (dialog != null && !dialog.isShowing()) {
            this.myDialogInterface = myDialogInterface;
            this.myDialogInterfaceCancel = myDialogInterfaceCancel;
            tv_clear.setText(text);
            dialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public interface MyDialogInterface {
        void dialogCallBack();
    }

    public interface MyDialogInterfaceCancel {
        void dialogCallBack();
    }
}
