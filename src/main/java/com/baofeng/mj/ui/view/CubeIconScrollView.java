package com.baofeng.mj.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

/**
 * 方钮 布局 1行横向滚动
 * Created by muyu on 2016/5/9.
 */
public class CubeIconScrollView extends FrameLayout {

    private Context mContext;
    private View rootView;
    private RadioGroup radioGroup;
    private String detailUrl;
    private VideoDetailBean videoDetailBean;
    public CubeIconScrollView(Context context) {
        this(context, null);
    }

    public CubeIconScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void initData(final VideoDetailBean detailBean,final String detailUrl){
        this.videoDetailBean = detailBean;
        int count = detailBean.getAlbums().get(0).getVideos().size();
        this.detailUrl = detailUrl;
        if(count > 10){
            count = 10;
        }
        for(int i= 0;i < count ; i++){
            RadioButton radioButton = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.cube_icon_item, null);
            final int seq = detailBean.getAlbums().get(0).getVideos().get(i).getSeq();
            radioButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //报数
                    reportClick(detailBean);
                    StartActivityHelper.startVideoGoUnity((Activity) mContext, detailUrl,
                            detailBean.getLandscape_url().getContents(), detailBean.getLandscape_url().getNav(), seq + "", "detail");
                }
            });
            radioButton.setText(seq+"");
            radioGroup.addView(radioButton);
        }
        radioGroup.check(radioGroup.getChildAt(0).getId());
    }

    protected void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_cube_icon_scroll,null);
        this.addView(rootView);
        radioGroup = (RadioGroup) findViewById(R.id.cube_icon_radiogroup);
    }

    private void reportClick(VideoDetailBean videoBean){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("play");
        bean.setTpos("1");
        bean.setPagetype("detail");
        bean.setTitle(videoBean.getTitle());
        bean.setMovieid(String.valueOf(videoBean.getId()));
        bean.setMovietypeid(String.valueOf(videoDetailBean.getCategory_type()));
        ReportBusiness.getInstance().reportClick(bean);
    }

    public void setCurrentIndex(int mIndex){
        if(radioGroup!=null){
            if(radioGroup.getChildCount()>mIndex){
                radioGroup.check(radioGroup.getChildAt(mIndex).getId());
            }

        }
    }
}
