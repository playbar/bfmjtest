package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.localbusiness.flyscreen.FlyScreenBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;

/**
 * Created by zhaominglei on 2016/5/10.
 * 飞屏帮助页
 */
public class FlyScreenGuideView extends RelativeLayout implements OnClickListener {

    private static final int GUIDE_STEP_NONE = 0;
    private static final int GUIDE_STEP_1 = 1;
    private static final int GUIDE_STEP_2 = 2;
    private static final int GUIDE_STEP_3 = 3;
    private static final int GUIDE_STEP_END = 4;
    private static final int GUIDE_STEP_COUNT = 4;
    private View mView;
    private Context mContext;
    private Button btn_learn_use_fly_screen;
    private RelativeLayout rl_fly_screen_brife_introduction;
    private ImageView iv_fly_screen_guide;
    private RelativeLayout ll_fly_screen_guide_step;
    private TextView tv_fly_screen_download_url;
    private TextView tv_fly_screen_download_address;
    private TextView tv_fly_screen_step;
    private TextView tv_fly_screen_step_desc;
    private TextView tv_skip_guide;
    private Button btn_fly_screen_step;
    private int mGuideStep;

    public FlyScreenGuideView(Context context) {
        this(context, null);
    }

    public FlyScreenGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_fly_screen_guide, this);
        btn_learn_use_fly_screen = (Button) mView.findViewById(R.id.btn_learn_use_fly_screen);
        rl_fly_screen_brife_introduction = (RelativeLayout) mView.findViewById(R.id.rl_fly_screen_brife_introduction);
        iv_fly_screen_guide = (ImageView) mView.findViewById(R.id.iv_fly_screen_guide);
        ll_fly_screen_guide_step = (RelativeLayout) mView.findViewById(R.id.ll_fly_screen_guide_step);
        tv_fly_screen_step = (TextView) mView.findViewById(R.id.tv_fly_screen_step);
        tv_fly_screen_step_desc = (TextView) mView.findViewById(R.id.tv_fly_screen_step_desc);
        tv_fly_screen_download_address = (TextView) mView.findViewById(R.id.tv_fly_screen_download_address);
        tv_fly_screen_download_url = (TextView) mView.findViewById(R.id.tv_fly_screen_download_url);
        tv_skip_guide = (TextView) mView.findViewById(R.id.tv_skip_guide);
        btn_fly_screen_step = (Button) ll_fly_screen_guide_step.findViewById(R.id.btn_fly_screen_step);
        btn_fly_screen_step.setOnClickListener(this);
        btn_learn_use_fly_screen.setOnClickListener(this);
        tv_skip_guide.setOnClickListener(this);
    }

    public void showGuide() {
        setVisibility(View.VISIBLE);
        if(mGuideStep == GUIDE_STEP_NONE)
        {
            rl_fly_screen_brife_introduction.setVisibility(View.VISIBLE);
            ll_fly_screen_guide_step.setVisibility(View.GONE);
        }
        else{
            ll_fly_screen_guide_step.setVisibility(View.VISIBLE);
            rl_fly_screen_brife_introduction.setVisibility(View.GONE);
        }
        showGuideSteps();
    }

    public void setStepGuidEnd(){
        mGuideStep = GUIDE_STEP_END;
    }

    public void checkBeginStepGuid(){
        if(FlyScreenBusiness.getInstance().isBeginStepGuide() == true)
        {
            mGuideStep = GUIDE_STEP_NONE;
            FlyScreenBusiness.getInstance().resetBeginStepGuide();
        }
    }

    public void hideGuide() {
        setVisibility(View.GONE);
        FlyScreenBusiness.getInstance().setSkipGuide(true);
        resetStepState();
    }

    public void showGuideSteps() {
        if (mGuideStep == GUIDE_STEP_1) {
            iv_fly_screen_guide.setImageResource(R.drawable.fly_screen_guide_img_1);
            tv_fly_screen_step.setText(getResources().getString(R.string.guide_step_1));
            tv_fly_screen_step_desc.setText(getResources().getString(R.string.download_fly_sreen_app));
            tv_fly_screen_download_address.setText(getResources().getString(R.string.download_address));
            tv_fly_screen_download_url.setText(getResources().getString(R.string.fly_screen_download_url));
            btn_fly_screen_step.setText(getResources().getString(R.string.next_step));
        } else if (mGuideStep == GUIDE_STEP_2) {
            iv_fly_screen_guide.setImageResource(R.drawable.fly_screen_guide_img_2);
            tv_fly_screen_download_address.setVisibility(View.INVISIBLE);
            tv_fly_screen_download_url.setVisibility(View.INVISIBLE);
            tv_fly_screen_step.setText(getResources().getString(R.string.guide_step_2));
            tv_fly_screen_step_desc.setText("把电脑的视频添加到飞屏应用");
        } else if (mGuideStep == GUIDE_STEP_3) {
            iv_fly_screen_guide.setImageResource(R.drawable.fly_screen_guide_img_3);
            tv_fly_screen_step.setText(getResources().getString(R.string.guide_step_3));
            btn_fly_screen_step.setText("马上使用飞屏");
            tv_fly_screen_step_desc.setText("用魔镜飞屏功能播放视频");
        } else if (mGuideStep == GUIDE_STEP_END) {
            hideGuide();
            FlyScreenBusiness.getInstance().startScan();
        }
    }

    private void startShowGuide() {
        mGuideStep = GUIDE_STEP_1;
        rl_fly_screen_brife_introduction.setVisibility(View.GONE);
        ll_fly_screen_guide_step.setVisibility(View.VISIBLE);
        showGuideSteps();
    }

    private void resetStepState() {
        mGuideStep = 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_learn_use_fly_screen == id) {
            startShowGuide();
            reportClick("learn");
        } else if (R.id.btn_fly_screen_step == id) {
            if(mGuideStep+1 >= GUIDE_STEP_COUNT)
                mGuideStep = GUIDE_STEP_END;
            else{
                mGuideStep ++;
            }
            showGuideSteps();
        } else if (R.id.tv_skip_guide == id) {
            hideGuide();
            FlyScreenBusiness.getInstance().startScan();
            reportClick("jumplearn");
        }
    }



    //click 报数
    private void reportClick(String airvideohelp){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("chooseitem");
        bean.setTpos("1");
        bean.setPagetype("airvideo");
        bean.setLocal_menu_id("3");
        bean.setAirevideohelp(airvideohelp);


        ReportBusiness.getInstance().reportClick(bean);
    }

}
