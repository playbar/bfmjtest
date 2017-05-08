package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * 体感游戏下载提示Dialog
 * Created by muyu on 2017/4/11.
 */
public class StickGameDownloadDialog extends Dialog implements View.OnClickListener {

    private TextView cancel;
    private TextView confirm;
    private CheckedTextView checkedTextView;
    private DownloadCallBack mDownloadCallBack;
    private int width;
    public StickGameDownloadDialog(Context context, DownloadCallBack downloadCallBack) {
        super(context, R.style.alertdialog);
        this.mDownloadCallBack = downloadCallBack;
        initView(PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40));
    }

    public StickGameDownloadDialog(Context context, DownloadCallBack downloadCallBack, int width) {
        super(context, R.style.alertdialog);
        this.mDownloadCallBack = downloadCallBack;
        initView(width);
    }

    private void initView(int width) {
         View v = LayoutInflater.from(getContext()).inflate(
                R.layout.stickgame_download_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width =width;
        setContentView(v, params);
        setCancelable(false);
        checkedTextView = (CheckedTextView) v.findViewById(R.id.stick_game_download_check);
        cancel = (TextView) v.findViewById(R.id.stick_game_download_cancel);
        confirm = (TextView) v.findViewById(R.id.stick_game_download_confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedTextView.isChecked()){ //选中
                    checkedTextView.setChecked(false);
                } else { //没选中
                    checkedTextView.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.stick_game_download_confirm) {
            if (mDownloadCallBack != null) {
                mDownloadCallBack.onConfirm(checkedTextView.isChecked());
            }
        }else if(vid == R.id.stick_game_download_cancel){
            if (mDownloadCallBack != null) {
                mDownloadCallBack.onCancel();
            }
        }
        cancel();
    }

    public interface DownloadCallBack {
        public void onConfirm(boolean isChecked);
        public void onCancel();
    }
}