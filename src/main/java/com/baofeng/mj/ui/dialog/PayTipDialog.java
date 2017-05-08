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
 * Created by liuchuanchi on 2016/6/14.
 * 支付提示对话框
 */
public class PayTipDialog {
    private Dialog dialog;//对话框
    private TextView tv_description;
    private TextView tv_left;
    private TextView tv_right;
    private MyDialogInterface myDialogInterface;//回调接口

    public PayTipDialog(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_pay_tip,null);//生成布局文件
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
        dialog.setContentView(view, params);//设置布局文件
        dialog.setCancelable(true);//true可以点击返回键取消对话框
        tv_description = (TextView) view.findViewById(R.id.tv_description);//描述
        tv_left = (TextView) view.findViewById(R.id.tv_left);//左面按钮
        tv_right = (TextView) view.findViewById(R.id.tv_right);//右面按钮
        //左面按钮点击事件
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //右面按钮点击事件
        tv_right.setOnClickListener(new View.OnClickListener() {
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
    public void showDialog(String description, String leftText, String rightText, MyDialogInterface myDialogInterface){
        if(dialog != null && !dialog.isShowing()){
            this.myDialogInterface = myDialogInterface;
            tv_description.setText(description);
            tv_left.setText(leftText);
            tv_right.setText(rightText);
            dialog.show();
        }
    }

    public interface MyDialogInterface {
        void dialogCallBack();
    }
}
