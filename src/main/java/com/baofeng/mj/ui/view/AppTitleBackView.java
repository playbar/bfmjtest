package com.baofeng.mj.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.GoUnity;

/**整个应用Title,左侧返回
 * Created by muyu on 2016/5/26.
 */
public class AppTitleBackView extends FrameLayout implements View.OnClickListener{
    public Context mContext;
    public View rootView;
    private ImageButton backImgBtn;
    private ImageButton invrImgBtn;
    private TextView nameTV;
    private TextView app_title_right;
    private String pageType;//页面类型
    private View view_top_line;

    public AppTitleBackView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public AppTitleBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_app_back_title, this);
        backImgBtn = (ImageButton) rootView.findViewById(R.id.app_title_back_imagebtn);
        invrImgBtn = (ImageButton) rootView.findViewById(R.id.app_title_in_vr_imagebtn);
        nameTV = (TextView) rootView.findViewById(R.id.app_title_name);
        app_title_right = (TextView) rootView.findViewById(R.id.app_title_right);
        view_top_line = rootView.findViewById(R.id.view_top_line);
        backImgBtn.setOnClickListener(this);
        invrImgBtn.setOnClickListener(this);
        initSubView();
    }

    public void initSubView(){};

    public void hideTopLine(){
        view_top_line.setVisibility(View.GONE);
    }

    public void setVRResWhite(){
        backImgBtn.setImageResource(R.drawable.nav_icon_back_white);
        invrImgBtn.setImageResource(R.drawable.nav_icon_invr_white);
    }

    public void setVRResBlack(){
        backImgBtn.setImageResource(R.drawable.nav_icon_back);
        invrImgBtn.setImageResource(R.drawable.nav_icon_invr);
    }

    public void setTextLeftLayout(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_LEFT);
        params.leftMargin = 160;
        params.topMargin = 46;
        nameTV.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.app_title_in_vr_imagebtn) {
            mContext.startActivity(new Intent(mContext, GoUnity.class));
            reportVRKeyClick();
        } else if (i == R.id.app_title_back_imagebtn) {
            ((Activity)mContext).finish();
        }
    }

    public TextView getNameTV() {
        return nameTV;
    }

    public void setNameTV(TextView nameTV) {
        this.nameTV = nameTV;
    }

    public ImageButton getInvrImgBtn() {
        return invrImgBtn;
    }

    public void setInvrImgBtn(ImageButton invrImgBtn) {
        this.invrImgBtn = invrImgBtn;
    }

    public ImageButton getBackImgBtn(){
        return backImgBtn;
    }

    public TextView getAppTitleRight(){
        return app_title_right;
    }

    /**
     * 设置页面类型
     */
    public void setPageType(String pageType){
        this.pageType = pageType;
    }

    private void reportVRKeyClick(){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("jump");
        bean.setPagetype("VR_key");
        bean.setFrompage(pageType);
        ReportBusiness.getInstance().reportClick(bean);
    }
}
