package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.GyroscopeActivity;
import com.baofeng.mj.ui.activity.HelpActivity;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mojing.MojingSDK;

/**
 * 控制方式fragment
 * Created by muyu on 2016/5/19.
 */
public class ControlModeFragment extends BaseFragment implements View.OnClickListener {

    private ImageView headStatusLight;
    private ImageView stickStatusLight;

    private TextView headStatusTV;
    private TextView stickStatusTV;

    private TextView gyroscopeCheckTV;
    private TextView stickCheckTV;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_control_mode, null);
        initView();
        return rootView;
    }

    //连接绿色，未连接红色
    private void unConnectZkey() {
//        headStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_green_dot));
//        headStatusTV.setText("已连接");

        stickStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_red_dot));
        stickStatusTV.setText("未连接");
        stickCheckTV.setVisibility(View.VISIBLE);
    }

    private void connectZkey() {
        stickStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_green_dot));
        stickStatusTV.setText("已连接");
        stickCheckTV.setVisibility(View.INVISIBLE);

//        headStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_red_dot));
//        headStatusTV.setText("未连接");
    }

    private void initView() {
        headStatusLight = (ImageView) rootView.findViewById(R.id.control_head_status_light);
        stickStatusLight = (ImageView) rootView.findViewById(R.id.control_stick_status_light);
        headStatusTV = (TextView) rootView.findViewById(R.id.control_head_status);
        stickStatusTV = (TextView) rootView.findViewById(R.id.control_stick_status);

        gyroscopeCheckTV = (TextView) rootView.findViewById(R.id.control_gyroscope_check);
        gyroscopeCheckTV.setText(LanguageValue.getInstance().getValue(getContext(), "SID_SENSITIVITY_VERIFICATION"));
        stickCheckTV = (TextView) rootView.findViewById(R.id.control_stick_check);

        rootView.findViewById(R.id.control_setting_save).setOnClickListener(this);

        gyroscopeCheckTV.setOnClickListener(this);
        stickCheckTV.setOnClickListener(this);
        startCheck();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.control_gyroscope_check) {
            reportClick("jump","ajust");
            startActivity(new Intent(getActivity(), GyroscopeActivity.class));
            getActivity().overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
        } else if (i == R.id.control_stick_check) {
            reportClick("jump","connectspec");
            startActivity(new Intent(getActivity(), HelpActivity.class));
        } else if (i == R.id.control_setting_save) {
            reportClick("chooseitem","savesetting");
            Toast.makeText(getActivity(), "设置已保存", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        }
    }

    public synchronized void startCheck() {
        if(BaseApplication.INSTANCE.isBFMJ5Connection() && BaseApplication.INSTANCE.getJoystickConnect() ){
            connectZkey();
        }else if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            unConnectZkey();
        } else if (!StickUtil.isBondBluetooth()) {// 蓝牙与魔镜设备未配对
            unConnectZkey();
        } else if (!StickUtil.isConnected) {// 设备未开启或者设备休眠
            unConnectZkey();
        } else {// 已连接
            connectZkey();
        }
    }


    //click 报数
    private void reportClick(String clickType,String devicesetting){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(clickType);
        bean.setTpos("1");
        bean.setPagetype("mydevice");
        bean.setDevicesetting(devicesetting);

        ReportBusiness.getInstance().reportClick(bean);
    }
}
