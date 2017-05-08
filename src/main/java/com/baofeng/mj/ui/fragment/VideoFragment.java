package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.GameDialogBean;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.bean.MainTabBean;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.VideoTitleActivity;
import com.baofeng.mj.ui.adapter.VideoAdapter;
import com.baofeng.mj.ui.dialog.GameTipDialog;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.ui.view.TabContainer;
import com.baofeng.mj.ui.view.UnderLineRadioButton;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.MainAPI;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.MainTabUtil;

import java.util.List;

/**
 * 视频页面Fragment
 * Created by muyu on 2016/3/28.
 */
public class VideoFragment extends BaseViewPagerFragment implements ViewPager.OnPageChangeListener,View.OnClickListener {
    private EmptyView emptyView;
    private TabContainer liveGroup;
    private ViewPager viewPager;
    private VideoAdapter adapter;
    private MainTabBean<List<MainSubTabBean>> mainTabBean;
    private List<MainSubTabBean> mainSubTabBeanList;
    private int currentTab; //一级Tab position
    private int screenWidth;
    private LinearLayout.LayoutParams params;
    private RelativeLayout.LayoutParams scrollParams;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayout;
    private View video_line;
    private ImageView moreIV;
    private ImageView moreBgIV;
    private int px40;
    private static final int REQUEST_CODE_TITLE =  100;
    private GameTipDialog gameTipDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTab = getArguments().getInt("currentTab");
        }
        screenWidth = PixelsUtil.getWidthPixels();
        px40 = PixelsUtil.dip2px(40);
//        params = new LinearLayout.LayoutParams((screenWidth - px40) / 5, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        if(currentTab == MainTabUtil.APPGAME && !SettingSpBusiness.getInstance().getGameTips()){
            //请求是否开启显示下述引导弹窗
            getGameDialog();
        }
    }

    private void getGameDialog(){
        new MainAPI().getGameDialogInfo(new ApiCallBack<Response<GameDialogBean>>() {
            @Override
            public void onSuccess(Response<GameDialogBean> result) {
                super.onSuccess(result);
                if(result != null && result.data != null && "1".equals(result.data.getStatus())){
                    showDialog(result.data);//显示Dialog
                }
            }
        });
    }

    private void showDialog(GameDialogBean data){
        if (gameTipDialog == null) {
            gameTipDialog = new GameTipDialog(getActivity());
        }
        gameTipDialog.showDialog(data.getImage_info(), data.getUrl());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frag_video, container, false);
            initView();
            getData();//请求数据
        }else{
            removeRootView();
        }
        return rootView;
    }

    private void initView(){
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.video_title_scroll);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.video_title_linear_layout);
        emptyView = (EmptyView) rootView.findViewById(R.id.empty_view);
        emptyView.getRefreshView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();//请求数据
            }
        });
        viewPager = (ViewPager) rootView.findViewById(R.id.video_viewpager);
        //viewPager.setOnPageChangeListener(VideoFragment.this);
        //viewPager.setOffscreenPageLimit(3);
        liveGroup = (TabContainer) rootView.findViewById(R.id.video_radiogroup);
        liveGroup.setOnPageChangeListener(VideoFragment.this);
        liveGroup.setViewPager(viewPager);

        video_line = rootView.findViewById(R.id.video_line);

        moreIV = (ImageView) rootView.findViewById(R.id.video_more_imageview);
        moreIV.setOnClickListener(this);

        moreBgIV = (ImageView) rootView.findViewById(R.id.video_more_imageview_bg);
    }

    /**
     * 请求数据
     */
    private void getData() {
        ((MainActivityGroup) getActivity()).getMainTabBeanList(new MainActivityGroup.MainTabBeanListCallback() {
            @Override
            public void callback(List<MainTabBean<List<MainSubTabBean>>> mainTabBeanList) {
                if (mainTabBeanList == null) {
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                emptyView.setVisibility(View.GONE);
                mainTabBean = mainTabBeanList.get(currentTab);
                mainSubTabBeanList = mainTabBean.getPages();
                int subTabCount = mainSubTabBeanList.size();
                scrollParams = (RelativeLayout.LayoutParams) horizontalScrollView.getLayoutParams();
                if(subTabCount <= 5){
                    moreIV.setVisibility(View.GONE);
                    moreBgIV.setVisibility(View.GONE);
//                    horizontalScrollView.setSmoothScrollingEnabled(false);
                    scrollParams.setMargins(0,0,0,0);
                    horizontalScrollView.setLayoutParams(scrollParams);
                    params = new LinearLayout.LayoutParams((screenWidth) / 5, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                }else {
                    moreIV.setVisibility(View.VISIBLE);
                    moreBgIV.setVisibility(View.VISIBLE);
                    scrollParams.setMargins(0, 0, px40, 0);
                    horizontalScrollView.setLayoutParams(scrollParams);
                    params = new LinearLayout.LayoutParams((screenWidth - px40) / 5, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                }
                UnderLineRadioButton itemView;
                for (int i = 0; i < subTabCount; i++) {
                    itemView = new UnderLineRadioButton(getActivity());
                    itemView.setLayoutParams(params);
                    itemView.getTextView().setText(mainSubTabBeanList.get(i).getTitle());
                    liveGroup.addView(itemView);
                }
                adapter = new VideoAdapter(getChildFragmentManager(), mainSubTabBeanList, currentTab);
                viewPager.setAdapter(adapter);
                onPageSelectedNoReport(0);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onPageSelectedNoReport(int position){
        SettingSpBusiness.getInstance().setSubTabPosition(currentTab, position); //存储当前Tab，SubTab位置
        viewPager.setCurrentItem(position);
        liveGroup.setCurrentTab(position);
    }

    @Override
    public void onPageSelected(int position) {
        onPageSelectedNoReport(position);
        onCheckedPageSelected(position);
        reportPV(position);
    }

    private void onCheckedPageSelected(int position){
        if(position == 0){
            video_line.setVisibility(View.GONE);
        }else {
            video_line.setVisibility(View.VISIBLE);
        }
        viewPager.setCurrentItem(position);
        liveGroup.setCurrentTab(position);

        //Tab居中效果
        if(position >= 2) {
            int distance = (position - 2) * (screenWidth / 5);
            horizontalScrollView.smoothScrollTo(distance, 0);
        } else {
            horizontalScrollView.smoothScrollTo(0, 0);
        }
//        horizontalScrollView.smoothScrollTo(position * screenWidth / 5, 0);
    }

    public void reportPV(int position){
        int resId = mainSubTabBeanList.get(position).getRes_id();
        String currentMenuName = mainSubTabBeanList.get(position).getTitle();
        ReportBusiness.getInstance().reportPV(currentTab, resId, currentMenuName);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.refreshView) {
            getData();
        } else if(id == R.id.video_more_imageview){
            Bundle bundle = new Bundle();
            bundle.putSerializable("pages", mainTabBean);
            Intent intent = new Intent(getActivity(), VideoTitleActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE_TITLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_TITLE && resultCode ==1 && data != null){
            int position = data.getIntExtra("position",0);
            onCheckedPageSelected(position);
        }
    }

}
