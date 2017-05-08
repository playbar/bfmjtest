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

/** H5页面Title,左侧返回
 * Created by muyu on 2016/5/26.
 */
public class AppH5TitleBackView extends AppTitleBackView implements View.OnClickListener{
    private TextView closeTV;

    public AppH5TitleBackView(Context context) {
        super(context);
    }

    public AppH5TitleBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initSubView(){
        closeTV = (TextView) rootView.findViewById(R.id.app_title_close);
        closeTV.setVisibility(View.VISIBLE);
        closeTV.setOnClickListener(this);
        getInvrImgBtn().setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.app_title_close){
            ((Activity)mContext).finish();
        }
    }
}
