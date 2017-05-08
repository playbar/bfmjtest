package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.GuideActivity;

/**
 * Created by muyu on 2016/9/19.
 */
public class GuideControlFragment extends BaseFragment implements View.OnClickListener{

    private View rootView;
    private TextView noStickTV;
    private TextView nextTV;

    private RelativeLayout mixLayout;
    private RelativeLayout pureLayout;

    private RadioButton mixRadioBtn;
    private RadioButton pureRadioBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_guide_control, null);
        initViews();
        return rootView;
    }

    private void initViews(){
        //控制颜色
        mixLayout = (RelativeLayout) rootView.findViewById(R.id.guide_control_mode_mix_layout);
        pureLayout = (RelativeLayout) rootView.findViewById(R.id.guide_control_mode_pure_layout);

        mixRadioBtn = (RadioButton) rootView.findViewById(R.id.guide_control_mix_radiobtn);
        pureRadioBtn = (RadioButton) rootView.findViewById(R.id.guide_control_pure_radiobtn);

        mixRadioBtn.setOnClickListener(this);
        pureRadioBtn.setOnClickListener(this);

        noStickTV = (TextView) rootView.findViewById(R.id.guide_no_stick);
        noStickTV.setOnClickListener(this);
        nextTV = (TextView) rootView.findViewById(R.id.guide_mode_next);
        nextTV.setOnClickListener(this);

        if(SettingSpBusiness.getInstance().getControlMode() == 1){
            setLayoutStatus(false, true);
        } else {
            setLayoutStatus(true,false);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.guide_no_stick){
            getActivity().startActivity(new Intent(getActivity(), MainActivityGroup.class));
            getActivity().finish();
        } else if(id == R.id.guide_mode_next){
//            if(getActivity() != null) {
//                ((GuideActivity) getActivity()).changeFrag(3);
//            }
//            if(listeners != null) {
//                listeners.changeFrag(3);
//            }
        } else if(id == R.id.guide_control_mix_radiobtn){

            setLayoutStatus(true, false);
            SettingSpBusiness.getInstance().setControlMode(0);
        } else if(id == R.id.guide_control_pure_radiobtn){

            setLayoutStatus(false, true);
            SettingSpBusiness.getInstance().setControlMode(1);
        }

    }

    private void setLayoutStatus(boolean mixStatus,boolean pureStatus){
        mixLayout.setSelected(mixStatus);
        mixRadioBtn.setChecked(mixStatus);
        pureLayout.setSelected(pureStatus);
        pureRadioBtn.setChecked(pureStatus);
    }
}
