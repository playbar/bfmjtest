package com.baofeng.mj.ui.online.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.dialog.AppUpdateDialog;


/**
 * Created by wanghongfang on 2016/11/23.
 * 播放异常提示框
 */
public class ExceptionDialogView extends LinearLayout  {
    private Context context;
    LinearLayout layout_l;
    TextView noNetWork_l;
    TextView reloadTv_l;
    Button reloadBtn_l;

    LinearLayout layout_r;
    TextView noNetWork_r;
    TextView reloadTv_r;
    Button reloadBtn_r;

    View leftview;
    View rightview;
    private boolean isContinuBtn = false;
    public ExceptionDialogView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        setOrientation(LinearLayout.HORIZONTAL);
        initView();
    }
    private void initView(){
          leftview = LayoutInflater.from(context).inflate(R.layout.player_exception_dialog_layout,null);
          rightview = LayoutInflater.from(context).inflate(R.layout.player_exception_dialog_layout,null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.weight = 1;
        this.addView(leftview,params);
        this.addView(rightview,params);
        layout_l = (LinearLayout) leftview.findViewById(R.id.player_exception_layout);
        noNetWork_l = (TextView) leftview.findViewById(R.id.player_no_network_tv);

        reloadTv_l = (TextView) leftview.findViewById(R.id.player_reload_tv);
        reloadBtn_l = (Button) leftview.findViewById(R.id.player_relaod_btn);

        layout_r = (LinearLayout) rightview.findViewById(R.id.player_exception_layout);
        noNetWork_r = (TextView) rightview.findViewById(R.id.player_no_network_tv);

        reloadTv_r = (TextView) rightview.findViewById(R.id.player_reload_tv);
        reloadBtn_r = (Button) rightview.findViewById(R.id.player_relaod_btn);

    }

    public void setTipText(String text){
        if(reloadTv_l!=null){
            reloadTv_l.setText(text);
        }
        if(reloadTv_r!=null){
            reloadTv_r.setText(text);
        }
    }

    public void setTipBtnText(String text){
        if(reloadBtn_l!=null){
            reloadBtn_l.setText(text);
        }
        if(reloadBtn_r!=null){
            reloadBtn_r.setText(text);
        }
    }
    public void setBtnOnClickListener(OnClickListener listener){
        if(reloadBtn_l!=null) {
            reloadBtn_l.setOnClickListener(listener);
        }
        if(reloadBtn_r!=null) {
            reloadBtn_r.setOnClickListener(listener);
        }
    }
    public boolean getIsContinueBtn(){
        return isContinuBtn;
    }

    public void showNoNetwork(){
        showNoNetwork(getResources().getString(R.string.player_network_exception));
    }
    public void showNoNetwork(String content){
        if(layout_l!=null){
            layout_l.setVisibility(View.GONE);
        }
        if(layout_r!=null){
            layout_r.setVisibility(View.GONE);
        }
        if(noNetWork_l!=null){
            noNetWork_l.setText(content);
            noNetWork_l.setVisibility(View.VISIBLE);
        }
        if(noNetWork_r!=null){
            noNetWork_r.setText(content);
            noNetWork_r.setVisibility(View.VISIBLE);
        }
        ExceptionDialogView.this.setVisibility(VISIBLE);
    }
    //播放loading超时 提示框
    public void showNetworkTips(int duration, final DismissListener listener){
        showNoNetwork(getResources().getString(R.string.player_exception_tips));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ExceptionDialogView.this.setVisibility(GONE);
                listener.dismiss();
            }
        },duration);

    }

    public void showExceptionDialog(String msg,String BtnName,boolean isContinuBtn){
        this.isContinuBtn = isContinuBtn;
        if(noNetWork_l!=null){
            noNetWork_l.setVisibility(View.GONE);
        }
        if(noNetWork_r!=null){
            noNetWork_r.setVisibility(View.GONE);
        }
        if(layout_l!=null){
            layout_l.setVisibility(View.VISIBLE);
        }
        if(layout_r!=null){
            layout_r.setVisibility(View.VISIBLE);
        }
        setTipBtnText(BtnName);
        setTipText(msg);
    }

    public void setLayoutScreen(boolean isfull){
        if(isfull){
            leftview.setVisibility(VISIBLE);
            rightview.setVisibility(VISIBLE);
        }else {
            leftview.setVisibility(VISIBLE);
            rightview.setVisibility(GONE);
        }
    }
    public interface DismissListener {
        void dismiss();
    }
}
