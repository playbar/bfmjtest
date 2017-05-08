package com.baofeng.mj.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.fragment.AllSelectionContentFragment;
import com.baofeng.mj.ui.online.utils.ThreadProxy;

import java.util.ArrayList;

/**
 * 视频全集适配器
 * Created by muyu on 2016/5/6.
 */
public class AllSelectionAdapter extends FragmentStatePagerAdapter {
    private static final float PAGE_SIZE = 30;

    private String detailUrl;
    private String contents;
    private String nav;
    private int videoNum;
    private ArrayList<VideoDetailBean.AlbumsBean.VideosBean> mjList;
    public AllSelectionAdapter(FragmentManager fm,VideoDetailBean.AlbumsBean detailBean, String detailUrl, String contents, String nav, int videoNum) {
        super(fm);
        this.detailUrl = detailUrl;
        this.contents = contents;
        this.nav = nav;
        this.videoNum = videoNum;
        this.mjList = (ArrayList<VideoDetailBean.AlbumsBean.VideosBean>)detailBean.getVideos();
    }

    @Override
    public Fragment getItem(int position) {
        AllSelectionContentFragment fragment = new AllSelectionContentFragment();
        int tabNum = (int) Math.ceil(mjList.size()/PAGE_SIZE);

        int begin = (int) (position * PAGE_SIZE);
        int end = (int) ((position + 1) * PAGE_SIZE );

        if(position == tabNum -1 ){
            end = mjList.size() ;
        }
        ArrayList<VideoDetailBean.AlbumsBean.VideosBean> arrayList  = new ArrayList(mjList.subList(begin, end));
        Bundle bundle = new Bundle();
        bundle.putString("detailUrl",detailUrl);
        bundle.putString("contents", contents);
        bundle.putString("nav", nav);
        bundle.putSerializable("selectionList", arrayList);
        if (videoNum >= begin && videoNum <= end) {
            bundle.putInt("select_index", videoNum);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return (int) Math.ceil(mjList.size()/PAGE_SIZE);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setMjList(ArrayList<VideoDetailBean.AlbumsBean.VideosBean> list){
        mjList = list;
        notifyDataSetChanged();
    }

    public void setCurrentIndex(int page, int mIndex, final Activity activity){
        final int newIndex = mIndex%((int)PAGE_SIZE);
        final AllSelectionContentFragment fragment = (AllSelectionContentFragment)getItem(page);

        ThreadProxy.getInstance().addRunDelay(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        fragment.setPosition(newIndex);

                    }
                });
            }
        },500);

    }

}
