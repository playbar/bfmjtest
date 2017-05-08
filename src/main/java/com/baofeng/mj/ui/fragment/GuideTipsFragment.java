package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.GlassesItemMode;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesSdkBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;

/**
 * 非五代魔镜 手柄说明页面
 * Created by muyu on 2016/9/19.
 */
public class GuideTipsFragment extends BaseFragment{

    private View rootView;
    private TextView titleTV;

    private View normalLayout;
    private View fiveGenerLayout;
    private Button beginBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_guide_help, null);
        initViews();
        return rootView;
    }

    public void initTips(){
        GlassesSdkBean bean = GlassesManager.getGlassesSdkBean();
        if(bean != null && "1".equals(bean.getManufactureID()) && "8".equals(bean.getProductID()) && "16".equals(bean.getGlassesID())){ //魔镜5代
            fiveGenerLayout.setVisibility(View.VISIBLE);
            normalLayout.setVisibility(View.GONE);
            titleTV.setText("魔镜5代手柄使用说明");
        } else {
            fiveGenerLayout.setVisibility(View.GONE);
            normalLayout.setVisibility(View.VISIBLE);
            titleTV.setText("魔镜手柄使用说明");
        }
    }

    private void initViews(){
        titleTV = (TextView) rootView.findViewById(R.id.help_list_title);
        beginBtn = (Button) rootView.findViewById(R.id.guide_begin_btn);

        normalLayout =  rootView.findViewById(R.id.guide_help_normal_content);
        fiveGenerLayout =  rootView.findViewById(R.id.guide_help_fivegener_content);
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), MainActivityGroup.class));
                getActivity().finish();
            }
        });
    }
}
