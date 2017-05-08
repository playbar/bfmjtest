package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;


/**
 * Created by wanghongfang on 2016/11/22.
 * 播放操作控制选项
 *           极简模式  沉浸模式
 */
public class PlayerTypeSelectionActivity extends BaseActivity implements View.OnClickListener{
    private ImageView normalTypeImg;
    private ImageView VRTypeImg;
    public static final  int PLAYER_TYPE_NORMAL = 0;//极简模式
    public static final int PLAYER_TYPE_VR = 1;//沉浸模式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playertype_select);
        initView();

    }

    private void initView(){
        RelativeLayout normalType = (RelativeLayout)findViewById(R.id.player_type_normal);
        normalType.setOnClickListener(this);
        RelativeLayout VRType = (RelativeLayout)findViewById(R.id.player_type_vr);
        VRType.setOnClickListener(this);
        normalTypeImg = (ImageView)findViewById(R.id.player_type_normal_img);
        VRTypeImg = (ImageView)findViewById(R.id.player_type_vr_img);
        AppTitleBackView titleBackView = (AppTitleBackView)findViewById(R.id.player_type_title_layout);
        titleBackView.getInvrImgBtn().setVisibility(View.GONE);
        int type = SettingSpBusiness.getInstance().getPlayerMode();
        if(type==PLAYER_TYPE_NORMAL){
            normalTypeImg.setVisibility(View.VISIBLE);
            VRTypeImg.setVisibility(View.GONE);
        }else if(type==PLAYER_TYPE_VR){
            VRTypeImg.setVisibility(View.VISIBLE);
            normalTypeImg.setVisibility(View.GONE);
        }
        reportPV(type);
    }

    @Override
    public void onClick(View v) {
        if(R.id.player_type_normal == v.getId()){//极简模式
            normalTypeImg.setVisibility(View.VISIBLE);
            VRTypeImg.setVisibility(View.GONE);
            SettingSpBusiness.getInstance().setPlayerMode(PLAYER_TYPE_NORMAL);
            reportClick("simple");
        }else if(R.id.player_type_vr == v.getId()){//沉浸模式
            normalTypeImg.setVisibility(View.GONE);
            VRTypeImg.setVisibility(View.VISIBLE);
            SettingSpBusiness.getInstance().setPlayerMode(PLAYER_TYPE_VR);
            reportClick("u3d");
        }
        finish();
    }

    private void reportClick(String mode){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("cut");
        bean.setTpos("1");
        bean.setPagetype("set_playmode");
        bean.setMode(mode);
        ReportBusiness.getInstance().reportClick(bean);
    }


    private void reportPV(int type){
        ReportPVBean bean = new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("set_playmode");
        if(type==PLAYER_TYPE_NORMAL){
          bean.setMode("simple");
        }else if(type==PLAYER_TYPE_VR) {
            bean.setMode("u3d");
        }
        ReportBusiness.getInstance().reportPV(bean);
    }
}
