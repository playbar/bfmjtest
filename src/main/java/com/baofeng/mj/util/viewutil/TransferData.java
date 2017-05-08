package com.baofeng.mj.util.viewutil;

import android.text.TextUtils;

import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentBean;
import com.baofeng.mj.bean.MainSubContentListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 转换数据
 * Created by muyu on 2016/7/4.
 */
public class TransferData {
    
    private static TransferData instance;

    private TransferData() {

    }

    public static TransferData getInstance() {
        if (instance == null) {
            instance = new TransferData();
        }
        return instance;
    }

    //global-banner图不处理，nav-mult快捷入口不处理
    //video-h2,
    public List<ContentBaseBean> transToSmallModule(MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> data) {
        //ContentBaseBean为ContentInfo与MainSubContentListBean的公共父类，为了兼容不同布局的数据结构
        List<ContentBaseBean> contentBaseBeans = new ArrayList<ContentBaseBean>();
        List<MainSubContentListBean<List<ContentInfo>>> bean = data.getList();
        for (MainSubContentListBean<List<ContentInfo>> item : bean) {
            String layoutType = item.getLayout_type();
            //暂时屏蔽品牌专区，等待需求开发时，再打开
            if(FindViewGroup.CP_H2.equals(layoutType)) {
                continue;
            }
            int count = 0;
            if(item.getList() != null){
                count = item.getList().size();
            }
            //若为排行榜、圆形、banner、专题栏目则直接传进去MainSubContentBean,其他布局取出list内容，传进去ContentInfo
            if (FindViewGroup.APP_TOP.equals(layoutType)
                    || FindViewGroup.NAV_SINGLE.equals(layoutType)
                    || FindViewGroup.NAV_MULT.equals(layoutType)
                    || FindViewGroup.GLOBAL_BANNNER.equals(layoutType)) {

                contentBaseBeans.add(item);
            } else if(FindViewGroup.PIC_HSV.equals(layoutType)||FindViewGroup.APP_HSV.equals(layoutType)) {
                //根据title和hasMore判断是否创建title布局
                if (item.getHas_more() != 0 || !TextUtils.isEmpty(item.getTitle())) {
                    ContentInfo titleInfo = new ContentInfo();
                    titleInfo.setLayout_type("layout_title");
                    titleInfo.setTitle(item.getTitle());
                    titleInfo.setParentResId(item.getRes_id());
                    titleInfo.setHas_more(item.getHas_more());
                    titleInfo.setUrl(item.getUrl());
                    contentBaseBeans.add(titleInfo);
                }
                contentBaseBeans.add(item);
            } else {
                //根据title和hasMore判断是否创建title布局
                if (item.getHas_more() != 0 || !TextUtils.isEmpty(item.getTitle())) {
                    ContentInfo titleInfo = new ContentInfo();
                    titleInfo.setLayout_type("layout_title");
                    titleInfo.setTitle(item.getTitle());
                    titleInfo.setParentResId(item.getRes_id());
                    titleInfo.setHas_more(item.getHas_more());
                    titleInfo.setUrl(item.getUrl());
                    contentBaseBeans.add(titleInfo);
                }
                ContentInfo contentInfo = null;
                for (int i = 0; i < count; i++) {
                    contentInfo = item.getList().get(i);
                    contentInfo.setLayout_type(item.getLayout_type());
                    contentInfo.setIndex(i);
                    contentInfo.setParentResId(item.getRes_id());
                    contentInfo.setTitleType(item.getTitle());//在每一个资源上添加tilte，判断每一个资源的title，来添加最新推荐角标
                    contentBaseBeans.add(contentInfo);
                }
            }
        }
        return contentBaseBeans;
    }

    //global-banner图不处理，nav-mult快捷入口不处理
    //video-h2,
    public List<ContentBaseBean> transToListModule(MainSubContentListBean<List<ContentInfo>> data) {
        //ContentBaseBean为ContentInfo与MainSubContentListBean的公共父类，为了兼容不同布局的数据结构
        List<ContentBaseBean> contentBaseBeans = new ArrayList<ContentBaseBean>();
        String layoutType = data.getLayout_type();
        int count = data.getList().size();
        //若为排行榜、圆形、banner、专题栏目则直接传进去MainSubContentBean,其他布局取出list内容，传进去ContentInfo
        if (FindViewGroup.APP_TOP.equals(layoutType)
                || FindViewGroup.NAV_SINGLE.equals(layoutType)
                || FindViewGroup.NAV_MULT.equals(layoutType)
                || FindViewGroup.GLOBAL_BANNNER.equals(layoutType)
                || FindViewGroup.PIC_HSV.equals(layoutType)) {
            contentBaseBeans.add(data);
        } else {
//            //根据title和hasMore判断是否创建title布局
//            if (data.getHas_more() != 0 || !TextUtils.isEmpty(data.getTitle())) {
//                ContentInfo titleInfo = new ContentInfo();
//                titleInfo.setLayout_type("layout_title");
//                titleInfo.setTitle(data.getTitle());
//                titleInfo.setHas_more(data.getHas_more());
//                titleInfo.setUrl(data.getUrl());
//                contentBaseBeans.add(titleInfo);
//            }
            ContentInfo contentInfo = null;
            for (int i = 0; i < count; i++) {
                contentInfo = data.getList().get(i);
                contentInfo.setLayout_type(data.getLayout_type());
                contentInfo.setIndex(i);
                contentInfo.setParentResId(data.getRes_id());
                contentBaseBeans.add(contentInfo);
            }
        }
        return contentBaseBeans;
    }
}
