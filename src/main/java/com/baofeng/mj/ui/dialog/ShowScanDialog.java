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
 * 二维码扫描弹窗
 */
public class ShowScanDialog {
    private Activity activity;
    private Dialog showScanDialog;//未更新对话框
    private String scanContent;
    private MyDialogInterface myDialogInterface;//回调接口

    public ShowScanDialog(Activity Activity, String content) {
        this.activity = Activity;
        this.scanContent = content;
    }

    /**
     * 创建未更新对话框
     *
     * @param context
     */
    private void createNoUpdateDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.scan_show_content, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        showScanDialog = new Dialog(context, R.style.alertdialog);
        showScanDialog.setContentView(view, params);
        showScanDialog.setCancelable(false);
        TextView update_dialog_ok = (TextView) view.findViewById(R.id.update_dialog_ok);
        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(scanContent);
        update_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDialogInterface != null) {
                    myDialogInterface.dialogCallBack();
                }
                showScanDialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(Activity context, MyDialogInterface myDialogInterface) {
        if (showScanDialog != null && !showScanDialog.isShowing()) {
            this.myDialogInterface = myDialogInterface;
            showScanDialog.show();
        } else {
            this.myDialogInterface = myDialogInterface;
            createNoUpdateDialog(context);
            showScanDialog.show();
        }
    }

    public interface MyDialogInterface {
        void dialogCallBack();
    }
}
