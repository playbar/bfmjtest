package com.baofeng.mj.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.ui.activity.FeedbackActivity;
import com.baofeng.mj.ui.activity.HelpActivity;
import com.baofeng.mj.ui.popwindows.GuideNoGlasPopWindow;
import com.storm.smart.common.utils.LogHelper;

/**
 * Created by muyu on 2016/9/18.
 */
public class GuideFragment extends BaseFragment {

    private TextView mSelectTextView;
    private View rootView;
    private TextView mHaveGlassTextView;
    private String mGlassesName;
    private Button mNextBtn;
    private RelativeLayout mBottomLayout;
    private TextView mNoGlassTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_guide1, null);
        initViews();
        return rootView;
    }

    private void initViews(){
        mHaveGlassTextView = (TextView) rootView.findViewById(R.id.have_glass_tv);
        mSelectTextView = (TextView) rootView.findViewById(R.id.guide_select_tv);
        mNextBtn = (Button) rootView.findViewById(R.id.guide_next_btn);
        mBottomLayout = (RelativeLayout) rootView.findViewById(R.id.bottom_layout);
        mNoGlassTextView = (TextView) rootView.findViewById(R.id.no_glass_tv);

        mSelectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null) {
                   /* String ids = SettingSpBusiness.getInstance().getGlassesIds();
                    if(!TextUtils.isEmpty(ids)){ //如果已存在镜片值 跳过镜片引导
                        if(SettingSpBusiness.getInstance().isOtherGlasses()){ //第三方眼镜直接进入主页面
                            getActivity().startActivity(new Intent(getActivity(), MainActivityGroup.class));
                            getActivity().finish();
                        }else {
                            ((GuideActivity) getActivity()).changeFrag(2);
                        }
                    }else {
                        ((GuideActivity) getActivity()).changeFrag(1);
                    }*/

                    Intent intent = new Intent("com.main.intent.action.vr.DEVICE_SELECT");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.putExtra("from", ConstantKey.FROM_MAIN_APP_GUIDE);
                    startActivityForResult(intent, 100);

                }
            }
        });

        mHaveGlassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.main.intent.action.vr.DEVICE_SELECT");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra("from", ConstantKey.FROM_MAIN_APP_GUIDE);
                startActivityForResult(intent, 100);
            }
        });
        mNoGlassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportClick();
                GuideNoGlasPopWindow popWindow = new GuideNoGlasPopWindow(getActivity());
                popWindow.showAtLocation(mHaveGlassTextView, Gravity.CENTER, 0, 0);
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpActivity = new Intent(getActivity(),HelpActivity.class);
                helpActivity.putExtra(HelpActivity.SHOW,true);

                //5代镜片
                String manufactureid = ConstantKey.ManufactureID_MJ5;
                String productid = ConstantKey.ProductID_MJ5;
                String glassesid = ConstantKey.GlassesID_MJ5;
                GlassesNetBean bean = GlassesManager.getGlassesNetBean();
                if(bean != null && manufactureid.equals(bean.getCompany_id()) && productid.equals(bean.getProduct_id()) && glassesid.equals(bean.getLens_id())){
                    helpActivity.putExtra(HelpActivity.IS_MJ,true);
                }else{
                    helpActivity.putExtra(HelpActivity.IS_MJ,false);
                }

                startActivity(helpActivity);

            }
        });
        GlassesNetBean dataBean = GlassesManager.getGlassesNetBean();
        if(null != dataBean && dataBean.isSelected()){
            mGlassesName = dataBean.getGlass_name();
        }

        updateView();
    }

    private void updateView(){
        if(!TextUtils.isEmpty(mGlassesName)){
            mHaveGlassTextView.setVisibility(View.VISIBLE);
            mNextBtn.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.GONE);

            String text = getActivity().getResources().getString(R.string.current_device_name);
            String name = String.format(text,mGlassesName);
            mHaveGlassTextView.setText(name);
        }else{
            mHaveGlassTextView.setVisibility(View.GONE);
            mNextBtn.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    //click 报数
    private void reportClick(){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("cut");
        bean.setTpos("1");
        bean.setPagetype("choose_glasses");

        ReportBusiness.getInstance().reportClick(bean);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取sdk提供的眼镜名称
        if(data == null){
            return;
        }
        if(100 == requestCode){
            GlassesNetBean dataBean = GlassesManager.getGlassesNetBean();
            if(null != dataBean && dataBean.isSelected()){
                mGlassesName = dataBean.getGlass_name();
            }

            boolean isFeedback = data.getBooleanExtra("isFeedback", false);
            if(isFeedback){
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                intent.putExtra("from","glasses_view");
                ((Activity)(getContext())).startActivityForResult(intent,201);

            }


            updateView();

        }
    }
}
