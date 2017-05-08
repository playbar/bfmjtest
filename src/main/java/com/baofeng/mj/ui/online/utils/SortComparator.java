package com.baofeng.mj.ui.online.utils;


import com.baofeng.mj.bean.PanoramaVideoAttrs;

import java.util.Comparator;

/**
 * Created by wanghongfang on 2016/8/12.
 */

public class SortComparator implements Comparator<PanoramaVideoAttrs> {
    @Override
    public int compare(PanoramaVideoAttrs lhs, PanoramaVideoAttrs rhs) {
        if(lhs!=null&&rhs!=null){
          int lhdtype = lhs.getDefinition_id();
          int rhdtype = rhs.getDefinition_id();
            return  rhdtype-lhdtype;

        }
        return 0;
    }
}
