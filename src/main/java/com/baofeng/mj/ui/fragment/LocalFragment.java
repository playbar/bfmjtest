package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.adapter.LocalAdapter;
import com.baofeng.mj.ui.view.AppTitleView;
import com.baofeng.mj.util.publicutil.SubTypeUtil;
import com.baofeng.mj.util.viewutil.MainTabUtil;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

import java.util.ArrayList;
import java.util.List;

/**本地页面Fragment
 * Created by muyu on 2016/3/28.
 */
public class LocalFragment extends BaseViewPagerFragment implements View.OnClickListener, ViewPager.OnPageChangeListener{
    private FrameLayout local_mobile;
    private FrameLayout local_download;
    private FrameLayout local_flyscreen;
    private TextView local_mobile_txt;
    private TextView local_download_txt;
    private TextView local_fly_txt;
    private ImageView local_mobile_line;
    private ImageView local_download_line;
    private ImageView local_download_red_point;
    private ImageView local_fly_line;
    private ImageView local_fly_red_point;
    private ViewPager viewPager;
    private LocalAdapter adapter;
    private List<BaseViewPagerFragment> mAbsBaseFragments;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAbsBaseFragments = new ArrayList<BaseViewPagerFragment>();
        mAbsBaseFragments.add(new LocalVideoFragment());
        mAbsBaseFragments.add(new LocalDownloadFragment());
        mAbsBaseFragments.add(new FlyScreenFragment());
        adapter = new LocalAdapter(getChildFragmentManager(), mAbsBaseFragments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frag_local, container, false);
            initViews();
        }else{
            removeRootView();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        //removeRootView();
        super.onDestroyView();
    }

    private void initViews() {
        local_mobile = (FrameLayout) rootView.findViewById(R.id.local_mobile);
        local_download = (FrameLayout) rootView.findViewById(R.id.local_download);
        local_flyscreen = (FrameLayout) rootView.findViewById(R.id.local_fly);
        local_mobile_txt = (TextView) rootView.findViewById(R.id.local_mobile_txt);
        local_download_txt = (TextView) rootView.findViewById(R.id.local_download_txt);
        local_fly_txt = (TextView) rootView.findViewById(R.id.local_fly_txt);
        local_mobile_line = (ImageView) rootView.findViewById(R.id.local_mobile_line);
        local_download_line = (ImageView) rootView.findViewById(R.id.local_download_line);
        local_download_red_point = (ImageView) rootView.findViewById(R.id.local_download_red_point);
        local_fly_line = (ImageView) rootView.findViewById(R.id.local_fly_line);
        local_fly_red_point = (ImageView) rootView.findViewById(R.id.local_fly_red_point);
        viewPager = (ViewPager) rootView.findViewById(R.id.local_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

        local_mobile.setOnClickListener(this);
        local_download.setOnClickListener(this);
        local_flyscreen.setOnClickListener(this);

        setCurrentItem(0);
        changeStyle(0);//改变样式
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.local_mobile) {
            setCurrentItem(0);
        } else if (i == R.id.local_download) {
            setCurrentItem(1);
        } else if (i == R.id.local_fly) {
            setCurrentItem(2);
        }
    }

    public void setCurrentItem(int position){
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeStyle(position);//改变样式
        SettingSpBusiness.getInstance().setSubTabPosition(MainTabUtil.LOCAL, position); //存储当前Tab，SubTab位置
        ReportBusiness.getInstance().reportPV(MainTabUtil.LOCAL, position + 1);
        if(position == 2){
            ((FlyScreenFragment)mAbsBaseFragments.get(2)).setCurrentPage(true);
            ((FlyScreenFragment)mAbsBaseFragments.get(2)).guideViewShow();
        }else{
            ((FlyScreenFragment)mAbsBaseFragments.get(2)).setCurrentPage(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 改变样式
     */
    private void changeStyle(int position){
        if(position == 0){
            local_mobile_txt.setTextColor(getResources().getColor(R.color.tab_highlight_text_color));
            local_download_txt.setTextColor(getResources().getColor(R.color.prompt_color));
            local_fly_txt.setTextColor(getResources().getColor(R.color.prompt_color));

            local_mobile_line.setVisibility(View.VISIBLE);
            local_download_line.setVisibility(View.GONE);
            local_fly_line.setVisibility(View.GONE);
        }else if(position == 1){
            local_fly_txt.setTextColor(getResources().getColor(R.color.prompt_color));
            local_mobile_txt.setTextColor(getResources().getColor(R.color.prompt_color));
            local_download_txt.setTextColor(getResources().getColor(R.color.tab_highlight_text_color));

            local_fly_line.setVisibility(View.GONE);
            local_mobile_line.setVisibility(View.GONE);
            local_download_line.setVisibility(View.VISIBLE);
        }else if(position == 2){
            local_mobile_txt.setTextColor(getResources().getColor(R.color.prompt_color));
            local_download_txt.setTextColor(getResources().getColor(R.color.prompt_color));
            local_fly_txt.setTextColor(getResources().getColor(R.color.tab_highlight_text_color));

            local_fly_line.setVisibility(View.VISIBLE);
            local_mobile_line.setVisibility(View.GONE);
            local_download_line.setVisibility(View.GONE);
        }
    }

    /**
     * 显示小红点
     * @param downloadingSize 正在下载的个数
     */
    public void showRedPoint(final int downloadingSize){
        if(local_download_red_point != null){
            if(downloadingSize == 0){
                local_download_red_point.setVisibility(View.GONE);
            }else{
                local_download_red_point.setVisibility(View.VISIBLE);
            }
        }
    }



}