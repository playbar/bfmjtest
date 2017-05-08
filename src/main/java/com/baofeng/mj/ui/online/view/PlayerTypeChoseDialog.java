package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.PlayerTypeSelectionActivity;
import com.baofeng.mj.util.publicutil.PixelsUtil;



/**
 * Created by wanghongfang on 2016/11/23.
 * 在线播放模式选择对话框
 *     极简模式  沉浸模式
 */
public class PlayerTypeChoseDialog extends Dialog implements View.OnClickListener{
    LinearLayout simple;
    LinearLayout vrmodel;
    PanoramaVideoBean videoBean;
    LinearLayout remeberLayout;
    ImageView remeberImg;
    boolean isRemeber = false;
    Activity activity;
    private ImageView closeImg;
    public PlayerTypeChoseDialog(Context context) {
        super(context,R.style.alertdialog);
        initView();
        reportPV();
    }

    private void initView(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_chose_dialog_layout,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(50);
        setContentView(view,params);
        setCancelable(true);
        TextView rememberTv = (TextView) view.findViewById(R.id.player_choose_dialog_remember);
        simple = (LinearLayout)view.findViewById(R.id.player_choose_dialog_simple_layout);
        vrmodel = (LinearLayout)view.findViewById(R.id.player_choose_dialog_vr_layout);
        closeImg = (ImageView) view.findViewById(R.id.close_img);
        remeberLayout = (LinearLayout)findViewById(R.id.remember_layout);
        remeberImg = (ImageView) findViewById(R.id.remember_img);
        simple.setOnClickListener(this);
        vrmodel.setOnClickListener(this);
        closeImg.setOnClickListener(this);
        remeberLayout.setOnClickListener(this);

        rememberTv.setText(Html.fromHtml(getContext().getString(R.string.player_chose_dialog_remember1)));


    }

    private View.OnClickListener mListener;
    private onBackPressListener mOnbackPressListener;

    public void setGoUnityParams(Activity activity,View.OnClickListener listener){
        this.activity = activity;
        this.mListener = listener;
    }

    public void setGoUnityParams(Activity activity,View.OnClickListener listener, onBackPressListener onBackPressListener){
        this.activity = activity;
        this.mListener = listener;
        this.mOnbackPressListener = onBackPressListener;
    }

    @Override
    public void onClick(View v) {
        if(mListener!=null) {
            mListener.onClick(v);
        }
        if(R.id.close_img==v.getId()){
            dismiss();
            reportClick("close","");
        } else if (R.id.player_choose_dialog_simple_layout == v.getId()){
            dismiss();
            if(isRemeber){
                SettingSpBusiness.getInstance().setPlayerMode(PlayerTypeSelectionActivity.PLAYER_TYPE_NORMAL);
            }
            reportClick("cut","simple");

        }else if(R.id.player_choose_dialog_vr_layout== v.getId()){
            dismiss();
            if(isRemeber){
                SettingSpBusiness.getInstance().setPlayerMode(PlayerTypeSelectionActivity.PLAYER_TYPE_VR);
            }
            reportClick("cut","u3d");
        }else if(R.id.remember_layout == v.getId()){
            isRemeber = !isRemeber;
            if(isRemeber){
                remeberImg.setImageResource(R.drawable.choose_icon_remember_highlight);
            }else {
                remeberImg.setImageResource(R.drawable.choose_icon_remember);
            }

        }
    }

    private void reportClick(String clickType,String mode){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(clickType);
        bean.setTpos("1");
        bean.setPagetype("playmode");
        bean.setMode(mode);
        bean.setIs_rem(isRemeber?"1":"0");
        ReportBusiness.getInstance().reportClick(bean);
    }


    private void reportPV(){
        ReportPVBean bean = new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("playmode");
        ReportBusiness.getInstance().reportPV(bean);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mOnbackPressListener != null) {
            mOnbackPressListener.back();
        }
    }

    public interface onBackPressListener{
        void back();
    }
}
