package com.baofeng.mj.ui.online.utils;


import android.text.TextUtils;

import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.VideoDetailBean;

import java.util.Comparator;

/**
 * Created by wanghongfang on 2016/8/12.
 */

public class SortComparatorVideo implements Comparator<VideoDetailBean.AlbumsBean> {
    @Override
    public int compare(VideoDetailBean.AlbumsBean lhs, VideoDetailBean.AlbumsBean rhs) {
        if(lhs!=null&&rhs!=null){
            int lhdtype = lhs.getHdtype();
            int rhdtype = rhs.getHdtype();
            return rhdtype -lhdtype;

        }
        return 0;
    }
}
