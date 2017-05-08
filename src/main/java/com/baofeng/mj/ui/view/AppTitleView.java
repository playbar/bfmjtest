package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.GoUnity;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

/**
 * 整个应用Title
 * Created by muyu on 2016/5/26.
 */
public class AppTitleView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private View rootView;
//    private ImageButton accountImgBtn;
    private ImageButton invrImgBtn;
    private ImageButton app_title_search;

    public AppTitleView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public AppTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_app_title, this);
//        accountImgBtn = (ImageButton) rootView.findViewById(R.id.app_title_account);
        invrImgBtn = (ImageButton) rootView.findViewById(R.id.app_title_in_vr);
        app_title_search = (ImageButton) rootView.findViewById(R.id.app_title_search);
//        accountImgBtn.setOnClickListener(this);
        invrImgBtn.setOnClickListener(this);
        app_title_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.app_title_in_vr) {
            mContext.startActivity(new Intent(mContext, GoUnity.class));
            reportVRKeyClick();
        } else if(i == R.id.app_title_search){
            if (mContext instanceof MainActivityGroup) {
                StartActivityHelper.gotoSearchActivity((MainActivityGroup) mContext);
            }
        }
    }

    private void reportVRKeyClick() {
        if (mContext instanceof MainActivityGroup) {
            ReportBusiness.getInstance().reportVRKeyClick(((MainActivityGroup) mContext).currentNavId,
                    ((MainActivityGroup) mContext).currentMenuName,
                    ((MainActivityGroup) mContext).subType, ((MainActivityGroup) mContext).getCurrentTab());
        }
    }

    //click 报数
    private void reportClick(String vrmode){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("chooseitem");
        bean.setTpos("1");
        bean.setVrmode(vrmode);

        ReportBusiness.getInstance().reportClick(bean);
    }

}
