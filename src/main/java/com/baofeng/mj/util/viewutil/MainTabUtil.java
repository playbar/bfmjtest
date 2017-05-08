package com.baofeng.mj.util.viewutil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.fragment.AccountFragment;
import com.baofeng.mj.ui.fragment.HomeFragment;
import com.baofeng.mj.ui.fragment.LocalFragment;
import com.baofeng.mj.ui.fragment.RecommendFragement;
import com.baofeng.mj.ui.fragment.VideoFragment;
import com.baofeng.mj.ui.view.MyFragmentTabHost;

/**
 * 主tab工具类
 */
public class MainTabUtil {
//    public static final int RECOMMEND = 0;//推荐页
//    public static final int VIDEO = 1;//视频页
//    public static final int APPGAME = 2;//应用市场页
//    public static final int LOCAL = 3;//本地页

    //添加推荐页面，接口数据从首页开始，故数字不调整
    public static final int RECOMMEND = -1;//推荐页
    public static final int HOME = 0;//首页
    public static final int APPGAME = 1;//应用市场页
    public static final int LOCAL = 2;//本地页
    public static final int ACCOUNT = 3;//我的

    /**
     * 初始化主tab
     * @param mContext 上下文
     * @param myFragmentTabHost FragmentTabHost
     */
    public static void initMainTab(Context mContext, MyFragmentTabHost myFragmentTabHost) {

        if(BaseApplication.INSTANCE.channelCheckState == 1) {
            //推荐页面
            Bundle recommendBundle0 = new Bundle();
            recommendBundle0.putInt("currentTab", RECOMMEND);
            recommendBundle0.putInt("subCurrentTab", 0);
            recommendBundle0.putBoolean("show_title", true);
            TabHost.TabSpec recomTabSpec = newTabSpec(mContext, myFragmentTabHost, "推荐", R.drawable.tab_choice, RECOMMEND);
            myFragmentTabHost.addTab(recomTabSpec, RecommendFragement.class, recommendBundle0);
        }

        //首页页面--视频页面
        Bundle bundle0 = new Bundle();
        bundle0.putInt("currentTab", HOME);
        bundle0.putInt("subCurrentTab", 0);
        bundle0.putBoolean("show_title", true);
        TabHost.TabSpec tabSpec0 = newTabSpec(mContext, myFragmentTabHost, "视频", R.drawable.tab_video, HOME);

        //应用市场页面
        Bundle bundle1 = new Bundle();
        bundle1.putInt("currentTab", APPGAME);
        TabHost.TabSpec tabSpec1 = newTabSpec(mContext, myFragmentTabHost, "应用市场", R.drawable.tab_market, APPGAME);

        int tabOrder = SettingSpBusiness.getInstance().getTabOrder();
        if(tabOrder == 0 || tabOrder == 1) {
//            myFragmentTabHost.addTab(tabSpec0, HomeFragment.class, bundle0);
            myFragmentTabHost.addTab(tabSpec0, VideoFragment.class, bundle0);
            myFragmentTabHost.addTab(tabSpec1, VideoFragment.class, bundle1);
        } else {
            myFragmentTabHost.addTab(tabSpec1, VideoFragment.class, bundle1);
//            myFragmentTabHost.addTab(tabSpec0, HomeFragment.class, bundle0);
            myFragmentTabHost.addTab(tabSpec0, VideoFragment.class, bundle0);
        }

        //本地页面
        TabHost.TabSpec tabSpec2 = newTabSpec(mContext, myFragmentTabHost, "本地", R.drawable.tab_local, LOCAL);
        myFragmentTabHost.addTab(tabSpec2, LocalFragment.class, null);

        //我的页面
        TabHost.TabSpec tabSpec3 = newTabSpec(mContext, myFragmentTabHost, "我的", R.drawable.tab_account, ACCOUNT);
        myFragmentTabHost.addTab(tabSpec3, AccountFragment.class, null);

    }

    /**
     * 创建TabHost.TabSpec
     * @param mContext 上下文
     * @param myFragmentTabHost FragmentTabHost
     * @param resName 名称
     * @param resIcon 图标
     */
    private static TabHost.TabSpec newTabSpec(Context mContext, MyFragmentTabHost myFragmentTabHost, String resName, int resIcon, int position){
        View indicator = View.inflate(mContext, R.layout.layout_main_tab_item, null);
        TextView textView = (TextView) indicator.findViewById(R.id.textView);
        ImageView imageView = (ImageView) indicator.findViewById(R.id.imageView);
        TextView tv_red_point = (TextView) indicator.findViewById(R.id.tv_red_point);

        textView.setText(resName);
//        textView.setTextColor(mContext.getResources().getColor(R.color.main_blue_green_bg_color));
        imageView.setImageDrawable(mContext.getResources().getDrawable(resIcon));

        TabHost.TabSpec tabSpec = myFragmentTabHost.newTabSpec(getFragmentTabTag(mContext, position));
        tabSpec.setIndicator(indicator);

        if(position == APPGAME){//应用市场
            ((MainActivityGroup) mContext).setAppStoreTabView(indicator);
        } else if(position == LOCAL){//本地
            ((MainActivityGroup) mContext).setLocalTabView(indicator);
        }

        return tabSpec;
    }

    public static String getFragmentTabTag(Context mContext, int position){
        return mContext.toString() + position;
    }
}
