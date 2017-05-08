package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.text.NumberFormat;

/**
 * Created by zhaominglei on 2016/6/24.
 */
public class CustomProgressDialog extends Dialog {

    private Context mContext;
    private boolean mHasStarted;
    private ProgressBar mProgress;
    private TextView mProgressPercent;
    private TextView mCancel;
    private Handler mViewUpdateHandler;

    private int mProgressVal;
    private int mMax;
    private NumberFormat mProgressPercentFormat;
    private View.OnClickListener onClickListener;

    protected CustomProgressDialog(Context context) {
        super(context);
        this.mContext = context;
        initFormats();
    }

    protected CustomProgressDialog(Context context, int theme) {
        super(context, R.style.alertdialog);
        this.mContext = context;
        initFormats();
    }

    protected CustomProgressDialog(Context context, View.OnClickListener onClickListener) {
        super(context, R.style.alertdialog);
        this.mContext = context;
        this.onClickListener = onClickListener;
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int progress = mProgress.getProgress();
                int max = mProgress.getMax();
                if (mProgressPercentFormat != null) {
                    double percent = (double) progress / (double) max;
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    mProgressPercent.setText(tmp);
                } else {
                    mProgressPercent.setText("");
                }
            }
        };
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.app_update_progress_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCancel = (TextView) view.findViewById(R.id.cancel);
        mProgress = (ProgressBar) view.findViewById(R.id.update_download_progress);
        mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        setContentView(view, params);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    private void onProgressChanged() {
        if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
            mViewUpdateHandler.sendEmptyMessage(0);
        }
    }

    private void initFormats() {
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }
}
