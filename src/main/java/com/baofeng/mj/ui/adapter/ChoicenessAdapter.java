package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.AppListActivity;
import com.baofeng.mj.ui.activity.TopicActivity;
import com.baofeng.mj.ui.view.BaseView;
import com.baofeng.mj.ui.view.CircleIconScrollView;
import com.baofeng.mj.ui.view.CircleIconView;
import com.baofeng.mj.ui.view.HorizontalAppListView;
import com.baofeng.mj.ui.view.MediaPlayerView;
import com.baofeng.mj.ui.view.MyViewPager;
import com.baofeng.mj.ui.view.PicBannerView;
import com.baofeng.mj.ui.view.TopicListView;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.NumFormatUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.viewutil.ShowDetail;
import com.storm.smart.common.utils.LogHelper;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表页面Adapter
 * Created by muyu on 2016/4/1.
 */
public class ChoicenessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context mContext;
    private Fragment fragment;
    private List<ContentBaseBean> infos;
    private int px40, px20, px15, px13, px10, px7, px5, px3;
    private int screenWidth;
    private int height;
    private ReportFromBean reportBean;//报数
    private boolean hasTag;
    private boolean hasShadow; //首页的banner有上半部分阴影
    private boolean isRank;
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private RelativeLayout.LayoutParams layoutParams;
    private VideoPlayerManager<MetaData> mVideoPlayerManager;

    public ChoicenessAdapter(Context context, List<ContentBaseBean> beans) {
        super();
        this.mContext = context;
        this.infos = beans;
        init();
    }

    public ChoicenessAdapter(Context context, Fragment fragment, List<ContentBaseBean> beans) {
        super();
        this.mContext = context;
        this.fragment = fragment;
        this.infos = beans;
        init();
    }

    public ChoicenessAdapter(Context context, Fragment fragment, List<ContentBaseBean> beans,VideoPlayerManager<MetaData> videoPlayerManager) {
        super();
        this.mContext = context;
        this.fragment = fragment;
        this.infos = beans;
        this.mVideoPlayerManager = videoPlayerManager;
        init();
    }

    private void init(){
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(mContext);
        }
        screenWidth = PixelsUtil.getWidthPixels();
        px40 = PixelsUtil.dip2px(40);
        px20 = PixelsUtil.dip2px(20);
        px15 = PixelsUtil.dip2px(15);
        px13 = PixelsUtil.dip2px(13);
        px10 = PixelsUtil.dip2px(10);
        px7 = PixelsUtil.dip2px(7);
        px5 = PixelsUtil.dip2px(5);
        px3 = PixelsUtil.dip2px(3);
        height = (int) (screenWidth / 1.77f);
        layoutParams = new RelativeLayout.LayoutParams(screenWidth, height);
    }

    public boolean isHasTag() {
        return hasTag;
    }

    public void setHasTag(boolean hasTag) {
        this.hasTag = hasTag;
    }

    public boolean isHasShadow() {
        return hasShadow;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    public void setDownLoadBusiness(DownLoadBusiness<ContentInfo> downLoadBusiness) {
        this.downLoadBusiness = downLoadBusiness;
    }

    public void setIsRank(boolean isRank) {
        this.isRank = isRank;
    }

    public boolean isRank() {
        return isRank;
    }

    @Override
    public int getItemCount() {
        if (infos == null || infos.size() <= 0) {
            return 0;
        }
        return infos.size();
    }

    @Override
    public int getItemViewType(int position) {
        String typeName = infos.get(position).getLayout_type();
        if(typeName == null){
            return 0;
        }
        if (typeName.equals(FindViewGroup.VIDEO_H1)) {
            return FindViewGroup.VIDEO_H1_NUM;
        } else if (typeName.equals(FindViewGroup.VIDEO_H2)) {
            return FindViewGroup.VIDEO_H2_NUM;
        } else if (typeName.equals(FindViewGroup.VIDEO_H3)) {
            return FindViewGroup.VIDEO_H3_NUM;
        } else if (typeName.equals(FindViewGroup.VIDEO_V2)) {
            return FindViewGroup.VIDEO_V2_NUM;
        } else if (typeName.equals(FindViewGroup.VIDEO_V3)) {
            return FindViewGroup.VIDEO_V3_NUM;
        } else if (typeName.equals(FindViewGroup.APP_H)) {
            return FindViewGroup.APP_H_NUM;
        } else if (typeName.equals(FindViewGroup.APP_V)) {
            return FindViewGroup.APP_V_NUM;
        } else if (typeName.equals(FindViewGroup.APP_TOP)) {
            return FindViewGroup.APP_TOP_NUM;
        } else if (typeName.equals(FindViewGroup.NAV_SINGLE)) {
            return FindViewGroup.NAV_SINGLE_NUM;
        } else if (typeName.equals(FindViewGroup.NAV_MULT)) {
            return FindViewGroup.NAV_MULT_NUM;
        } else if (typeName.equals(FindViewGroup.GLOBAL_BANNNER)) {
            return FindViewGroup.GLOBAL_BANNNER_NUM;
        } else if (typeName.equals(FindViewGroup.GLOBAL_TOPIC)) {
            return FindViewGroup.GLOBAL_TOPIC_NUM;
        } else if (typeName.equals(FindViewGroup.APP_CATEGORY)) {
            return FindViewGroup.APP_CATEGORY_NUM;
        } else if (typeName.equals(FindViewGroup.LAYOUR_TITLE)) {
            return FindViewGroup.LAYOUR_TITLE_NUM;
        } else if (typeName.equals(FindViewGroup.LOAD_MORE)) {
            return FindViewGroup.LOAD_MORE_NUM;
        } else if(typeName.equals(FindViewGroup.PIC_HSV)) {
            return FindViewGroup.PIC_HSV_NUM;
        } else if(typeName.equals(FindViewGroup.APP_HSV)){
            return FindViewGroup.APP_HSV_NUM;
        } else if(typeName.equals(FindViewGroup.APP_VIDEO)){
            return FindViewGroup.APP_VIDEO_NUM;
        }
//        System.out.println("testtest typeName:"+typeName+"----");
        //TODO 添加新类型
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, int i1) {
        View view = null;
        switch (i) {
            case FindViewGroup.VIDEO_H1_NUM:
                view = layoutInflater.inflate(R.layout.high_one_item, null);
                return new HighOneHolder(view);
            case FindViewGroup.VIDEO_H2_NUM:
                view = layoutInflater.inflate(R.layout.horizontal_two_item, null);
                return new ViewTwoHorizontalHolder(view);
            case FindViewGroup.VIDEO_H3_NUM:
                view = layoutInflater.inflate(R.layout.horizontal_three_item, null);
                return new ViewThreeHorizontalHolder(view);
            case FindViewGroup.VIDEO_V2_NUM:
                view = layoutInflater.inflate(R.layout.vertical_two_item, null);
                return new ViewTwoVerticalHolder(view);
            case FindViewGroup.VIDEO_V3_NUM:
                view = layoutInflater.inflate(R.layout.vertical_three_item, null);
                return new ViewThreeVerticalHolder(view);
            case FindViewGroup.APP_H_NUM:
                view = layoutInflater.inflate(R.layout.rect_four_item, null);
                return new RectFourHolder(view);
            case FindViewGroup.APP_V_NUM:
                view = layoutInflater.inflate(R.layout.list_one_item, null);
                return new ListOneHolder(view);
            case FindViewGroup.APP_TOP_NUM:
                view = layoutInflater.inflate(R.layout.rank_main_item, null);
                return new RankMainHolder(view);
            case FindViewGroup.NAV_SINGLE_NUM:
                view = new CircleIconScrollView(mContext,fragment, hasTag);
                //报数
                ((BaseView)view).setReportBean(reportBean);
                return new ViewHolder(view);
            case FindViewGroup.NAV_MULT_NUM:
                view = new CircleIconView(mContext, fragment, hasTag);
                //报数
                ((BaseView)view).setReportBean(reportBean);
                return new ViewHolder(view);
            case FindViewGroup.GLOBAL_BANNNER_NUM:
                view = layoutInflater.inflate(R.layout.view_banner2, null);
                return new BannerViewHolder(view);
//                view = new BannerView(mContext, hasTag, hasShadow);
//                //报数
//                ((BaseView)view).setReportBean(reportBean);
//                break;
            case FindViewGroup.GLOBAL_TOPIC_NUM:
                view = layoutInflater.inflate(R.layout.global_topic_item, null);
                return new GlobleTopicHolder(view);
            case FindViewGroup.APP_CATEGORY_NUM:
                view = layoutInflater.inflate(R.layout.two_class_item, null);
                return new TwoClassHolder(view);
            case FindViewGroup.LAYOUR_TITLE_NUM:
                view = layoutInflater.inflate(R.layout.title_item, null);
                return new TitleHolder(view);
            case FindViewGroup.LOAD_MORE_NUM:
                view = layoutInflater.inflate(R.layout.load_more_item, null);
                LoadMoreViewHolder loadMoreViewHolder = new LoadMoreViewHolder(view);
                if(fragment == null){
                    if(mContext instanceof TopicActivity){
                        ((TopicActivity) mContext).setLoadMoreViewHolder(loadMoreViewHolder);
                    }
                }else{
                }
                return loadMoreViewHolder;
            case FindViewGroup.PIC_HSV_NUM:
                view = new TopicListView(mContext);
                //报数
                ((TopicListView)view).setReportBean(reportBean);
                return new ViewHolder(view);
            case FindViewGroup.APP_HSV_NUM:
                view = new HorizontalAppListView(mContext);
                //报数
                ((HorizontalAppListView)view).setReportBean(reportBean,downLoadBusiness);
                return new ViewHolder(view);
            case FindViewGroup.APP_VIDEO_NUM:
                view = layoutInflater.inflate(R.layout.view_game_item, null);
                return new AppVideoHolder(view);
            default:
                //TODO 添加新类型
                break;
        }
        view = new TextView(mContext);
//        ((TextView)view).setText("this is a new type of view");
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // 绑定数据到ViewHolder上
        if (viewHolder.itemView instanceof BaseView) {
            ((BaseView) viewHolder.itemView).initView((MainSubContentListBean<List<ContentInfo>>) infos.get(position));
        } else if(viewHolder.itemView instanceof TopicListView){
            ((TopicListView) viewHolder.itemView).setData((MainSubContentListBean<List<ContentInfo>>) infos.get(position));
        }else if(viewHolder.itemView instanceof HorizontalAppListView){
            ((HorizontalAppListView)viewHolder.itemView).setData((MainSubContentListBean<List<ContentInfo>>) infos.get(position));
        }
        else {
            ContentBaseBean itemDara = infos.get(position);
            viewHolder.itemView.setTag(itemDara);
            switch (getItemViewType(position)) {
                case FindViewGroup.VIDEO_H1_NUM:
                    fillOneListView((HighOneHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.VIDEO_H2_NUM:
                    fillViewTwoHor((ViewTwoHorizontalHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.VIDEO_H3_NUM:
                    fillViewThreeHor((ViewThreeHorizontalHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.VIDEO_V2_NUM:
                    fillViewTwoVertical((ViewTwoVerticalHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.VIDEO_V3_NUM:
                    fillViewThreeVertical((ViewThreeVerticalHolder) viewHolder, (ContentInfo) itemDara, position);
                    break;
                case FindViewGroup.APP_H_NUM:
                    fillViewRectFourColumn((RectFourHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.APP_V_NUM:
                    fillViewListOne((ListOneHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.GLOBAL_TOPIC_NUM:
                    fillTopicItem((GlobleTopicHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.APP_CATEGORY_NUM:
                    fillTwoClassItem((TwoClassHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.LAYOUR_TITLE_NUM:
                    fillViewTitle((TitleHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.APP_TOP_NUM:
                    fillRankMain((RankMainHolder) viewHolder, (MainSubContentListBean<List<ContentInfo>>) itemDara);
                    break;
                case FindViewGroup.GLOBAL_BANNNER_NUM:
                    fillBannerView((BannerViewHolder) viewHolder, (MainSubContentListBean<List<ContentInfo>>) infos.get(position));
                    break;
                case FindViewGroup.APP_VIDEO_NUM:
                    fillViewAppVideo((AppVideoHolder) viewHolder, (ContentInfo) itemDara);
                default:
//                    fillDefaultView();
                    break;
            }
        }
    }

    private void fillOneListView(HighOneHolder viewHolder, final ContentInfo data) {
        if(isEmpty(data.getTitle())){
            viewHolder.high_one_name.setVisibility(View.GONE);
        }else{
            viewHolder.high_one_name.setVisibility(View.VISIBLE);
            viewHolder.high_one_name.setText(data.getTitle());
        }
        if(isEmpty(data.getSubtitle())){
            viewHolder.high_one_des.setVisibility(View.GONE);
        }else{
            viewHolder.high_one_des.setVisibility(View.VISIBLE);
            viewHolder.high_one_des.setText(data.getSubtitle());
        }
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.hight_one_image, data.getPic_url(), R.drawable.img_default_1n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.hight_one_image, data.getPic_url(), R.drawable.img_default_1n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }
        viewHolder.hight_one_image.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos","=====fillOneListView=======");
            }
        });
    }

    //ViewTwoHorView
    private void fillViewTwoHor(ViewTwoHorizontalHolder viewHolder, final ContentInfo data) {
        if(isEmpty(data.getTitle())){
            viewHolder.ItemText.setVisibility(View.GONE);
        }else{
            viewHolder.ItemText.setVisibility(View.VISIBLE);
            viewHolder.ItemText.setText(data.getTitle());
        }
        if (isEmpty(data.getSubtitle())) {
            viewHolder.shorty_two_subname.setVisibility(View.GONE);
        } else {
            viewHolder.shorty_two_subname.setVisibility(View.VISIBLE);
            viewHolder.shorty_two_subname.setText(data.getSubtitle());
        }
        ShowDetail.showPayCount(viewHolder.shorty_two_pay_count, data);
        if(null != data.getTitleType() && data.getTitleType().equals(mContext.getResources().getString(R.string.new_recommend))){
            ShowDetail.showVideoLabel(data.getHeadwear(), viewHolder.shorty_two_headwear);
        }else {
            ShowDetail.hideVideoLabel(data.getHeadwear(),viewHolder.shorty_two_headwear);
        }
        viewHolder.ItemImage.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos","=====fillViewTwoHor=======");
            }
        });
        if (data.getIndex() % 2 == 0) { //左边
            viewHolder.item_parent.setPadding(px10, 0, px5, px13);
        }else{//右边
            viewHolder.item_parent.setPadding(px5, 0, px10, px13);
        }
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_2n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_2n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }
    }

    //ViewTwoHorView
    private void fillViewTwoVertical(ViewTwoVerticalHolder viewHolder, final ContentInfo data) {
        if(isEmpty(data.getTitle())){
            viewHolder.ItemText.setVisibility(View.GONE);
        }else{
            viewHolder.ItemText.setVisibility(View.VISIBLE);
            viewHolder.ItemText.setText(data.getTitle());
        }
        if (isEmpty(data.getSubtitle())) {
            viewHolder.shorty_two_subname.setVisibility(View.GONE);
        } else {
            viewHolder.shorty_two_subname.setVisibility(View.VISIBLE);
            viewHolder.shorty_two_subname.setText(data.getSubtitle());
        }
        ShowDetail.showPayCount(viewHolder.shorty_two_pay_count, data);
        viewHolder.ItemImage.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);

                LogHelper.e("infos","=====fillViewTwoVertical=======");
            }
        });
        if (data.getIndex() % 2 == 0) { //左边
            viewHolder.item_parent.setPadding(px10, 0, px5, px13);
        }else{//右边
            viewHolder.item_parent.setPadding(px5, 0, px10, px13);
        }
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_2n_vertical, viewHolder.imgWidth, viewHolder.imgHeight);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_2n_vertical, viewHolder.imgWidth, viewHolder.imgHeight);
        }
    }

    //ViewThreeHor
    private void fillViewThreeHor(ViewThreeHorizontalHolder viewHolder, final ContentInfo data) {
        if(isEmpty(data.getTitle())){
            viewHolder.ItemText.setVisibility(View.GONE);
        }else{
            viewHolder.ItemText.setVisibility(View.VISIBLE);
            viewHolder.ItemText.setText(data.getTitle());
        }
        viewHolder.ItemImage.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos","=====fillViewThreeHor=======");
            }
        });
        switch (data.getIndex() % 3) {
            case 0: //左边
                viewHolder.item_parent.setPadding(px10, 0, px3, px13);
                break;
            case 1: //中间
                viewHolder.item_parent.setPadding(px7, 0, px7, px13);
                break;
            case 2: //右边
                viewHolder.item_parent.setPadding(px3, 0, px10, px13);
                break;
        }
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_3n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_3n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }
    }

    //ViewThreeVertical
    private void fillViewThreeVertical(ViewThreeVerticalHolder viewHolder, final ContentInfo data, int pos) {
        if(isEmpty(data.getTitle())){
            viewHolder.ItemText.setVisibility(View.GONE);
        }else{
            viewHolder.ItemText.setVisibility(View.VISIBLE);
            viewHolder.ItemText.setText(data.getTitle());
        }
        viewHolder.ItemImage.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos", "=====fillViewThreeVertical=======");
            }
        });
        switch (data.getIndex() % 3) {
            case 0: //左边
                viewHolder.item_parent.setPadding(px10, 0, px3, px13);
                break;
            case 1: //中间
                viewHolder.item_parent.setPadding(px7, 0, px7, px13);
                break;
            case 2: //右边
                viewHolder.item_parent.setPadding(px3, 0, px10, px13);
                break;
        }
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_3n_vertical, viewHolder.imgWidth, viewHolder.imgHeight);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_3n_vertical, viewHolder.imgWidth, viewHolder.imgHeight);
        }
    }

    //RectFourColumn

    private void fillViewRectFourColumn(RectFourHolder viewHolder, final ContentInfo data) {
        viewHolder.rect_four_name.setText(data.getTitle());
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.rect_four_image, data.getPic_url(), R.drawable.img_default_4n);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.rect_four_image, data.getPic_url(), R.drawable.img_default_4n);
        }
        viewHolder.rect_four_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos", "=====fillViewRectFourColumn=======");
            }
        });
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = data.getApp_extra();
            if (appExtra != null) {
                viewHolder.rect_four_btn.setTag(data);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(data.getRes_id()),reportBean);
                downLoadBusiness.addDownloadButton(viewHolder.rect_four_btn, data, appExtra);
            }
        }
    }

    //ViewListOne
    private void fillViewListOne(final ListOneHolder viewHolder, final ContentInfo data) {
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.list_one_image, data.getPic_url(), R.drawable.img_default_4n);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.list_one_image, data.getPic_url(), R.drawable.img_default_4n);
        }
        if (isRank) {
            viewHolder.list_one_no.setVisibility(View.VISIBLE);
        } else {
            viewHolder.list_one_no.setVisibility(View.GONE);
        }
        viewHolder.list_one_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos", "=====fillViewListOne=======");
            }
        });
        viewHolder.list_one_name.setText(data.getTitle());
        if (data.getApp_extra() != null) {
            viewHolder.list_one_des.setText(data.getApp_extra().getRecommend_desc());
            ShowDetail.showGameLabel(data.getApp_extra().getProduct_type(), viewHolder.list_one_type, mContext);
            viewHolder.list_one_size.setText(data.getApp_extra().getFilesize());
            viewHolder.list_one_downloadno.setText(NumFormatUtil.formatCount(data.getApp_extra().getDownload_count()) + "次");
            viewHolder.list_one_stickgame.setVisibility(View.GONE);
            List<String> playmodeList = data.getApp_extra().getPlay_mode();
            for(String str: playmodeList){
                if(str.equals("6")){ //体感游戏
                    viewHolder.list_one_stickgame.setVisibility(View.VISIBLE);
                }
            }
        }
        if (downLoadBusiness != null) {
            final AppExtraBean appExtra = data.getApp_extra();
            if (appExtra != null) {
                viewHolder.list_one_btn.setTag(data);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(data.getRes_id()),reportBean);
                downLoadBusiness.addDownloadButton(viewHolder.list_one_btn, data, appExtra);
            }
        }
    }

    //201704XX版本新增 app-video类型
    private void fillViewAppVideo(final AppVideoHolder viewHolder, final ContentInfo data) {
        if(fragment == null){
            GlideUtil.displayImage(mContext, viewHolder.video_game_icon, data.getPic_url(), R.drawable.img_default_4n);
        }else{
            GlideUtil.displayImage(fragment, viewHolder.video_game_icon, data.getPic_url(), R.drawable.img_default_4n);
        }
        viewHolder.video_game_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
            }
        });
        viewHolder.video_game_name.setText(data.getTitle());
        if (data.getApp_extra() != null) {
            viewHolder.video_game_des.setText(data.getApp_extra().getRecommend_desc());
            viewHolder.video_game_size.setText(data.getApp_extra().getFilesize());
            viewHolder.video_game_download_count.setText(NumFormatUtil.formatCount(data.getApp_extra().getDownload_count()) + "次");

            viewHolder.video_game_stickgame.setVisibility(View.GONE);
            if(data.getApp_extra().getPlay_mode() != null){
                List<String> playmodeList = data.getApp_extra().getPlay_mode();
                for(String str: playmodeList){
                    if(str.equals("6")){ //体感游戏
                        viewHolder.video_game_stickgame.setVisibility(View.VISIBLE);
                    }
                }
            }
//            final String path = "http://zxsp.mojing.cn//meizivideo//170411//89614bcf978240a796b7909ba6f31d41.mp4";//data.getApp_extra().getVideo_url()
            viewHolder.video_game_mediaplay_view.setVideoPath(data.getApp_extra().getVideo_url());
//            viewHolder.video_game_mediaplay_view.setVideoPath(path);
            viewHolder.video_game_mediaplay_view.setIsAutoPlay(false);
            viewHolder.video_game_mediaplay_view.setMaximize(true);
            viewHolder.video_game_mediaplay_view.setVideoPlayerManager(mVideoPlayerManager);
            //设置播放器封面图片
//            System.out.println("!!!!!!!!!!---------------url:"+data.getApp_extra().getCover_image());
            if(fragment == null){
                GlideUtil.displayImage(mContext, new WeakReference<ImageView>(viewHolder.cover_image), data.getApp_extra().getCover_image(), R.drawable.img_default_banner);
            } else {
                GlideUtil.displayImage(fragment, new WeakReference<ImageView>(viewHolder.cover_image), data.getApp_extra().getCover_image(), R.drawable.img_default_banner);
            }

        }
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = data.getApp_extra();
            if (appExtra != null) {
                viewHolder.video_game_download_btn.setTag(data);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(data.getRes_id()),reportBean);
                downLoadBusiness.addDownloadButton(viewHolder.video_game_download_btn, data, appExtra);
            }
        }
    }

    //TopicItem
    private void fillTopicItem(GlobleTopicHolder viewHolder, final ContentInfo data) {
        viewHolder.special_name.setText(data.getTitle());
        String subtitle = data.getSubtitle();
        if(TextUtils.isEmpty(subtitle)) {
            viewHolder.special_des.setVisibility(View.GONE);
        }else {
            viewHolder.special_des.setVisibility(View.VISIBLE);
            viewHolder.special_des.setText(subtitle.trim());
        }
        PicBannerView picBannerView = new PicBannerView(mContext, fragment, data.getBanner(), false);
        picBannerView.setLLPointGravity(Gravity.CENTER);
        viewHolder.global_topic_banner_layout.removeAllViews();
        viewHolder.global_topic_banner_layout.addView(picBannerView);
    }

    private void fillTwoClassItem(TwoClassHolder viewHolder, final ContentInfo data) {
        viewHolder.class_item_text.setText(data.getTitle());
        viewHolder.class_item_image.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
                LogHelper.e("infos","=====fillTwoClassItem=======");
            }
        });
        if (data.getIndex() % 2 == 0) { //左边
            viewHolder.img_parent.setPadding(px10, 0, px5, px10);
        }else{//右边
            viewHolder.img_parent.setPadding(px5, 0, px10, px10);
        }
        if (fragment == null) {
            GlideUtil.displayImage(mContext, viewHolder.class_item_image, data.getPic_url(), R.drawable.img_default_2n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }else{
            GlideUtil.displayImage(fragment , viewHolder.class_item_image, data.getPic_url(), R.drawable.img_default_2n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        }
    }

    //ViewTitle
    private void fillViewTitle(TitleHolder viewHolder, final ContentInfo data) {
        viewHolder.view_title.setText(data.getTitle());
        if (data.getHas_more() == 0) {
            viewHolder.view_title_more.setVisibility(View.GONE);
        } else {
            viewHolder.view_title_more.setVisibility(View.VISIBLE);
            viewHolder.view_title_more.setText(LanguageValue.getInstance().getValue(mContext, "SID_MORE"));
            viewHolder.view_title_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportBusiness.getInstance().putHeader(data,reportBean);
                    ResTypeUtil.moreClick(mContext, data);
                    LogHelper.e("infos","=====fillViewTitle=======");
                }
            });
        }
    }

    /**
     * 填充排行榜布局
     *
     * @param viewHolder
     * @param data
     */
    private void fillRankMain(RankMainHolder viewHolder, final MainSubContentListBean<List<ContentInfo>> data) {
        if (data.getList().size() < 4) {
            viewHolder.rank_main_layout.setVisibility(View.GONE);
            return;
        } else {
            final ContentInfo contentInfo1 = data.getList().get(0);
            final ContentInfo contentInfo2 = data.getList().get(1);
            final ContentInfo contentInfo3 = data.getList().get(2);
            final ContentInfo contentInfo4 = data.getList().get(3);
            final ContentInfo contentInfo5 = data.getList().get(4);

            addDownloadButton(viewHolder.game_rank_1_btn, contentInfo1);
            addDownloadButton(viewHolder.game_rank_2_btn, contentInfo2);
            addDownloadButton(viewHolder.game_rank_3_btn, contentInfo3);
            addDownloadButton(viewHolder.game_rank_4_btn, contentInfo4);
            addDownloadButton(viewHolder.game_rank_5_btn, contentInfo5);

            if(fragment == null){
                GlideUtil.displayImage(mContext, viewHolder.imageview_rank_bg, data.getBg_url(), R.color.mj_color_rank_blue);
            }else{
                GlideUtil.displayImage(fragment, viewHolder.imageview_rank_bg, data.getBg_url(), R.color.mj_color_rank_blue);
            }

            //视图一填充数据
            viewHolder.game_rank_1_name.setText(contentInfo1.getTitle());
            viewHolder.game_rank_1_down_no.setText(NumFormatUtil.formatCount(contentInfo1.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoader.getInstance().displayImage(contentInfo1.getPic_url(), viewHolder.game_rank_1_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            if(fragment == null){
                GlideUtil.displayImage(mContext, viewHolder.game_rank_1_icon, contentInfo1.getPic_url(), R.drawable.img_default_4n);
            }else{
                GlideUtil.displayImage(fragment, viewHolder.game_rank_1_icon, contentInfo1.getPic_url(), R.drawable.img_default_4n);
            }
            viewHolder.game_rank_1_icon_stickgame.setVisibility(View.GONE);
            if(contentInfo1.getApp_extra() != null && contentInfo1.getApp_extra().getPlay_mode() != null){
                List<String> playmodeList = contentInfo1.getApp_extra().getPlay_mode();
                for(String str: playmodeList){
                    if(str.equals("6")){ //体感游戏
                        viewHolder.game_rank_1_icon_stickgame.setVisibility(View.VISIBLE);
                    }
                }
            }

            viewHolder.layout_center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //报数
                    contentInfo1.setParentResId(data.getRes_id());
                    contentInfo1.setLayout_type(data.getLayout_type());
                    ReportBusiness.getInstance().putHeader(contentInfo1, reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo1);
                    LogHelper.e("infos","=====fillRankMain111111111111=======");
                }
            });

            //视图二填充数据
            viewHolder.game_rank_2_name.setText(contentInfo2.getTitle());
            viewHolder.game_rank_2_down_no.setText(NumFormatUtil.formatCount(contentInfo2.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoader.getInstance().displayImage(contentInfo2.getPic_url(), viewHolder.game_rank_2_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            if(fragment == null){
                GlideUtil.displayImage(mContext, viewHolder.game_rank_2_icon, contentInfo2.getPic_url(), R.drawable.img_default_4n);
            }else{
                GlideUtil.displayImage(fragment, viewHolder.game_rank_2_icon, contentInfo2.getPic_url(), R.drawable.img_default_4n);
            }

            viewHolder.game_rank_2_icon_stickgame.setVisibility(View.GONE);
            if(contentInfo2.getApp_extra() != null && contentInfo2.getApp_extra().getPlay_mode() != null){
                List<String> playmodeList = contentInfo2.getApp_extra().getPlay_mode();
                for(String str: playmodeList){
                    if(str.equals("6")){ //体感游戏
                        viewHolder.game_rank_2_icon_stickgame.setVisibility(View.VISIBLE);
                    }
                }
            }

            viewHolder.layout_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //报数
                    contentInfo2.setParentResId(data.getRes_id());
                    contentInfo2.setLayout_type(data.getLayout_type());
                    ReportBusiness.getInstance().putHeader(contentInfo2, reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo2);
                    LogHelper.e("infos","=====fillRankMain222222222222=======");
                }
            });
            //视图三填充数据
            viewHolder.game_rank_3_name.setText(contentInfo3.getTitle());
            viewHolder.game_rank_3_down_no.setText(NumFormatUtil.formatCount(contentInfo3.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoader.getInstance().displayImage(contentInfo3.getPic_url(), viewHolder.game_rank_3_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            if(fragment == null){
                GlideUtil.displayImage(mContext, viewHolder.game_rank_3_icon, contentInfo3.getPic_url(), R.drawable.img_default_4n);
            }else{
                GlideUtil.displayImage(fragment, viewHolder.game_rank_3_icon, contentInfo3.getPic_url(), R.drawable.img_default_4n);
            }

            viewHolder.game_rank_3_icon_stickgame.setVisibility(View.GONE);
            if(contentInfo3.getApp_extra() != null && contentInfo3.getApp_extra().getPlay_mode() != null){
                List<String> playmodeList = contentInfo3.getApp_extra().getPlay_mode();
                for(String str: playmodeList){
                    if(str.equals("6")){ //体感游戏
                        viewHolder.game_rank_3_icon_stickgame.setVisibility(View.VISIBLE);
                    }
                }
            }

            viewHolder.layout_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //报数
                    contentInfo3.setParentResId(data.getRes_id());
                    contentInfo3.setLayout_type(data.getLayout_type());
                    ReportBusiness.getInstance().putHeader(contentInfo3, reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo3);
                    LogHelper.e("infos","=====fillRankMain33333333333=======");
                }
            });
            //视图四填充数据
            viewHolder.game_rank_4_name.setText(contentInfo4.getTitle());
            viewHolder.game_rank_4_size.setText(contentInfo4.getApp_extra().getFilesize());
            viewHolder.game_rank_4_download_no.setText(NumFormatUtil.formatCount(contentInfo4.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoader.getInstance().displayImage(contentInfo4.getPic_url(), viewHolder.game_rank_4_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            if(fragment == null){
                GlideUtil.displayImage(mContext, viewHolder.game_rank_4_icon, contentInfo4.getPic_url(), R.drawable.img_default_4n);
            }else{
                GlideUtil.displayImage(fragment, viewHolder.game_rank_4_icon, contentInfo4.getPic_url(), R.drawable.img_default_4n);
            }

            viewHolder.game_rank_4_icon_stickgame.setVisibility(View.GONE);
            if(contentInfo4.getApp_extra() != null && contentInfo4.getApp_extra().getPlay_mode() != null){
                List<String> playmodeList = contentInfo4.getApp_extra().getPlay_mode();
                for(String str: playmodeList){
                    if(str.equals("6")){ //体感游戏
                        viewHolder.game_rank_4_icon_stickgame.setVisibility(View.VISIBLE);
                    }
                }
            }

            viewHolder.rank_layout_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //报数
                    contentInfo4.setParentResId(data.getRes_id());
                    contentInfo4.setLayout_type(data.getLayout_type());
                    ReportBusiness.getInstance().putHeader(contentInfo4, reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo4);
                    LogHelper.e("infos","=====fillRankMain44444444444=======");
                }
            });
            //视图五填充数据
            viewHolder.game_rank_5_name.setText(contentInfo5.getTitle());
            viewHolder.game_rank_5_size.setText(contentInfo5.getApp_extra().getFilesize());
            viewHolder.game_rank_5_download_no.setText(NumFormatUtil.formatCount(contentInfo5.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoader.getInstance().displayImage(contentInfo5.getPic_url(), viewHolder.game_rank_5_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            if(fragment == null){
                GlideUtil.displayImage(mContext, viewHolder.game_rank_5_icon, contentInfo5.getPic_url(), R.drawable.img_default_4n);
            }else{
                GlideUtil.displayImage(fragment, viewHolder.game_rank_5_icon, contentInfo5.getPic_url(), R.drawable.img_default_4n);
            }

            viewHolder.game_rank_5_icon_stickgame.setVisibility(View.GONE);
            if(contentInfo5.getApp_extra() != null && contentInfo5.getApp_extra().getPlay_mode() != null){
                List<String> playmodeList = contentInfo5.getApp_extra().getPlay_mode();
                for(String str: playmodeList){
                    if(str.equals("6")){ //体感游戏
                        viewHolder.game_rank_5_icon_stickgame.setVisibility(View.VISIBLE);
                    }
                }
            }
            viewHolder.rank_layout_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //报数
                    contentInfo5.setParentResId(data.getRes_id());
                    contentInfo5.setLayout_type(data.getLayout_type());
                    ReportBusiness.getInstance().putHeader(contentInfo5,reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo5);
                    LogHelper.e("infos","=====fillRankMain55555555=======");
                }
            });
            viewHolder.rank_name.setText(data.getTitle());
            viewHolder.rank_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportBean.setCompid(data.getRes_id());
                    reportBean.setComponenttype(data.getLayout_type());
                    reportBean.setCompsubtitle(data.getTitle());
                    reportBean.setCurpage(data.getUrl());
                    ReportBusiness.getInstance().put(data.getUrl(),reportBean);
                    Intent intent = new Intent(mContext, AppListActivity.class);
                    boolean isRank;
                    if (!isEmpty(data.getLayout_type()) && data.getLayout_type().equals("app-top")) {
                        isRank = true;
                    } else {
                        isRank = false;
                    }
                    intent.putExtra("isRank", isRank);
                    intent.putExtra("next_title", data.getTitle());
                    intent.putExtra("next_url", data.getUrl());
                    mContext.startActivity(intent);
                    LogHelper.e("infos","=====fillRankMain666666666=======");
                }
            });
        }
    }

    //填充广告位视图
    private void fillBannerView(final BannerViewHolder viewHolder, final MainSubContentListBean<List<ContentInfo>> data) {
        viewHolder.reset();

        List<ContentInfo> contentInfoList = data.getList();
        final int itemSize = contentInfoList.size();
        for (int i = 0; i < itemSize; i++) {//创建大图
            createBannerItem(viewHolder, contentInfoList.get(i), data);
        }
        if (itemSize == 2) {//个数为2，重新添加一遍数据，解决viewpager翻页的bug
            for (int i = 0; i < itemSize; i++) {//创建大图
                createBannerItem(viewHolder, contentInfoList.get(i), data);
            }
        }
        if (itemSize > 1) {//大于1，创建小圆点
            viewHolder.ll_point_parent.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(12, 12);
            pointParams.setMargins(0, 0, 12, 0);
            for (int i = 0; i < itemSize; i++) {
                ImageView iv_point = new ImageView(mContext);
                iv_point.setLayoutParams(pointParams);
                if (i == 0) {
                    iv_point.setImageResource(R.drawable.public_page_lighlight);
                } else {
                    iv_point.setImageResource(R.drawable.public_page_normal);
                }
                viewHolder.getPointViewList().add(iv_point);//加入集合
                viewHolder.ll_point_parent.addView(iv_point);//加入圆点容器
            }
            viewHolder.sendMessage();//发送消息
        } else {
            viewHolder.ll_point_parent.setVisibility(View.GONE);
        }
        viewHolder.viewPager.setAdapter(new MyViewpagerAdapter(viewHolder.getBannerViewList()));// 设置适配器
        //viewHolder.viewPager.setCurrentItem(itemSize * 80);
    }

    private void createBannerItem(BannerViewHolder viewHolder, final ContentInfo contentInfo, final MainSubContentListBean<List<ContentInfo>> data) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.view_banner_item, null);
        FrameLayout banner_parent = (FrameLayout) itemView.findViewById(R.id.banner_parent);
        TextView title = (TextView) itemView.findViewById(R.id.banner_title);
        WeakReference<ImageView> bannerBg = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.banner_header_img));
        ImageView headwear = (ImageView) itemView.findViewById(R.id.banner_headware);
        banner_parent.setLayoutParams(viewHolder.bannerParams);
        title.setText(contentInfo.getTitle());
        if(fragment == null){
            GlideUtil.displayImage(mContext, bannerBg, contentInfo.getPic_url(), R.drawable.img_default_banner, screenWidth, viewHolder.bannerHeight);
        }else{
            GlideUtil.displayImage(fragment, bannerBg, contentInfo.getPic_url(), R.drawable.img_default_banner, screenWidth, viewHolder.bannerHeight);
        }

        ShowDetail.showVideoLabel(contentInfo.getHeadwear(), headwear);

        if (contentInfo.getUrl() != null && !"".equals(contentInfo.getUrl())) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentInfo.setLayout_type(data.getLayout_type());
                    contentInfo.setParentResId(data.getRes_id());
                    ReportBusiness.getInstance().putHeader(contentInfo,reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo);
                    LogHelper.e("infos","=====createBannerItem=======");
                }
            });
        }
        viewHolder.getBannerViewList().add(itemView);//加入集合
    }

    public ReportFromBean getReportBean() {
        return reportBean;
    }

    public void setReportBean(ReportFromBean reportBean) {
        this.reportBean = reportBean;
    }

    //HighOneColumn
    public class HighOneHolder extends RecyclerView.ViewHolder {
        private FrameLayout img_parent;
        private WeakReference<ImageView> hight_one_image;
        private TextView high_one_name, high_one_des;
        public int imgWidth;
        public int imgHeight;

        public HighOneHolder(View itemView) {
            super(itemView);
            img_parent = (FrameLayout) itemView.findViewById(R.id.img_parent);
            high_one_name = (TextView) itemView.findViewById(R.id.high_one_name);
            high_one_des = (TextView) itemView.findViewById(R.id.high_one_des);
            hight_one_image = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.hight_one_image));

            imgWidth = screenWidth - px20;
            imgHeight = (int) (imgWidth / 1.672f);
            img_parent.setLayoutParams(new LinearLayout.LayoutParams(imgWidth, imgHeight));
        }
    }

    //ViewTwoHorizontal
    public class ViewTwoHorizontalHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_parent;
        private FrameLayout img_parent;
        private WeakReference<ImageView> ItemImage;
        private ImageView shorty_two_headwear;
        private TextView ItemText;
        private TextView shorty_two_subname;
        private TextView shorty_two_pay_count;
        public int imgWidth;
        public int imgHeight;

        public ViewTwoHorizontalHolder(View itemView) {
            super(itemView);
            item_parent = (LinearLayout) itemView.findViewById(R.id.item_parent);
            img_parent = (FrameLayout) itemView.findViewById(R.id.img_parent);
            ItemText = (TextView) itemView.findViewById(R.id.shorty_two_name_tv);
            ItemImage = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.shorty_two_image));
            shorty_two_headwear = (ImageView) itemView.findViewById(R.id.shorty_two_headwear);
            shorty_two_subname = (TextView) itemView.findViewById(R.id.shorty_two_subname);
            shorty_two_pay_count = (TextView) itemView.findViewById(R.id.shorty_two_pay_count);

            imgWidth = screenWidth / 2 - px15;
            imgHeight = (int) (imgWidth / 1.78f);
            item_parent.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            img_parent.setLayoutParams(new LinearLayout.LayoutParams(imgWidth, imgHeight));
        }
    }

    //ViewTwoVertical
    public class ViewTwoVerticalHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_parent;
        private FrameLayout img_parent;
        private WeakReference<ImageView> ItemImage;
        private TextView ItemText;
        private TextView shorty_two_subname;
        private TextView shorty_two_pay_count;
        public int imgWidth;
        public int imgHeight;

        public ViewTwoVerticalHolder(View itemView) {
            super(itemView);
            item_parent = (LinearLayout) itemView.findViewById(R.id.item_parent);
            img_parent = (FrameLayout) itemView.findViewById(R.id.img_parent);
            ItemText = (TextView) itemView.findViewById(R.id.shorty_two_name_tv);
            ItemImage = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.shorty_two_image));
            shorty_two_subname = (TextView) itemView.findViewById(R.id.shorty_two_subname);
            shorty_two_pay_count = (TextView) itemView.findViewById(R.id.shorty_two_pay_count);

            imgWidth = screenWidth / 2 - px15;
            imgHeight = (int) (imgWidth / 0.75f);
            item_parent.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            img_parent.setLayoutParams(new LinearLayout.LayoutParams(imgWidth, imgHeight));
        }
    }

    //ViewThreeHorizontal
    public class ViewThreeHorizontalHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_parent;
        private FrameLayout img_parent;
        private WeakReference<ImageView> ItemImage;
        private TextView ItemText;
        public int imgWidth;
        public int imgHeight;

        public ViewThreeHorizontalHolder(View itemView) {
            super(itemView);
            item_parent = (LinearLayout) itemView.findViewById(R.id.item_parent);
            img_parent = (FrameLayout) itemView.findViewById(R.id.img_parent);
            ItemText = (TextView) itemView.findViewById(R.id.high_three_name_tv);
            ItemImage = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.high_three_image));

            imgWidth = (screenWidth - px40) / 3;
            imgHeight = (int) (imgWidth / 1.333f);
            item_parent.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 3, LinearLayout.LayoutParams.WRAP_CONTENT));
            img_parent.setLayoutParams(new LinearLayout.LayoutParams(imgWidth, imgHeight));
        }
    }

    //ViewThreeVertical
    public class ViewThreeVerticalHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_parent;
        private FrameLayout img_parent;
        private WeakReference<ImageView> ItemImage;
        private TextView ItemText;
        public int imgWidth;
        public int imgHeight;

        public ViewThreeVerticalHolder(View itemView) {
            super(itemView);
            item_parent = (LinearLayout) itemView.findViewById(R.id.item_parent);
            img_parent = (FrameLayout) itemView.findViewById(R.id.img_parent);
            ItemText = (TextView) itemView.findViewById(R.id.high_three_name_tv);
            ItemImage = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.high_three_image));

            imgWidth = (screenWidth - px40) / 3;
            imgHeight = (int) (imgWidth / 0.753f);
            item_parent.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 3, LinearLayout.LayoutParams.WRAP_CONTENT));
            img_parent.setLayoutParams(new LinearLayout.LayoutParams(imgWidth, imgHeight));
        }
    }

    //RectFour
    public class RectFourHolder extends RecyclerView.ViewHolder {
        private WeakReference<ImageView>  rect_four_image;
        private TextView rect_four_name;
        private Button rect_four_btn;
        private LinearLayout rect_four_item_layout;

        public RectFourHolder(View itemView) {
            super(itemView);
            rect_four_image = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.rect_four_image));
            rect_four_name = (TextView) itemView.findViewById(R.id.rect_four_name);
            rect_four_btn = (Button) itemView.findViewById(R.id.rect_four_btn);
            rect_four_item_layout = (LinearLayout) itemView.findViewById(R.id.rect_four_item_layout);
        }
    }

    //ListOne
    public class ListOneHolder extends RecyclerView.ViewHolder {
        private WeakReference<ImageView> list_one_image;
        private TextView list_one_name, list_one_type, list_one_downloadno, list_one_size, list_one_des, list_one_no;
        private Button list_one_btn;
        private RelativeLayout list_one_layout;
        private ImageView list_one_stickgame;

        public ListOneHolder(View itemView) {
            super(itemView);
            list_one_image = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.list_one_image));
            list_one_name = (TextView) itemView.findViewById(R.id.list_one_name);
            list_one_type = (TextView) itemView.findViewById(R.id.list_one_type);
            list_one_downloadno = (TextView) itemView.findViewById(R.id.list_one_downloadno);
            list_one_size = (TextView) itemView.findViewById(R.id.list_one_size);
            list_one_des = (TextView) itemView.findViewById(R.id.list_one_des);
            list_one_btn = (Button) itemView.findViewById(R.id.list_one_btn);
            list_one_layout = (RelativeLayout) itemView.findViewById(R.id.list_one_layout);
            list_one_no = (TextView) itemView.findViewById(R.id.list_one_no);
            list_one_stickgame = (ImageView)itemView.findViewById(R.id.list_one_stickgame);
        }
    }

    //201704XX版本新增 app-video类型
    public class AppVideoHolder extends RecyclerView.ViewHolder {
        private MediaPlayerView video_game_mediaplay_view;
        private WeakReference<ImageView> video_game_icon;
        private TextView video_game_name, video_game_size, video_game_download_count, video_game_des;
        private Button video_game_download_btn;
        private RelativeLayout video_game_layout;
        private ImageView video_game_stickgame;
        private ImageView cover_image;

        public AppVideoHolder(View itemView) {
            super(itemView);
            video_game_mediaplay_view = (MediaPlayerView) itemView.findViewById(R.id.video_game_mediaplay_view);
            video_game_icon = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.video_game_icon));
            cover_image = video_game_mediaplay_view.getCoverImage();
            video_game_name = (TextView) itemView.findViewById(R.id.video_game_name);
            video_game_size = (TextView) itemView.findViewById(R.id.video_game_size);
            video_game_download_count = (TextView) itemView.findViewById(R.id.video_game_download_count);
            video_game_des = (TextView) itemView.findViewById(R.id.video_game_des);

            video_game_download_btn = (Button) itemView.findViewById(R.id.video_game_download_btn);
            video_game_layout = (RelativeLayout) itemView.findViewById(R.id.video_game_layout);
            video_game_mediaplay_view.setLayoutParams(layoutParams);
            video_game_stickgame = (ImageView) itemView.findViewById(R.id.video_game_stickgame);
        }
    }

    //RankMain
    public class RankMainHolder extends RecyclerView.ViewHolder {
        private TextView game_rank_1_name, rank_more, rank_name, game_rank_2_name, game_rank_2_down_no,
                game_rank_1_down_no, game_rank_3_name, game_rank_3_down_no, game_rank_4_name, game_rank_4_no,
                game_rank_4_download_no, game_rank_4_size, game_rank_5_no, game_rank_5_name,
                game_rank_5_download_no, game_rank_5_size;
        private RelativeLayout layout_left, layout_right,layout_center;
        private RelativeLayout rank_layout_4, rank_layout_5, rank_main_layout;
        private Button game_rank_1_btn, game_rank_2_btn, game_rank_3_btn, game_rank_4_btn, game_rank_5_btn, btn_download3;
        private WeakReference<ImageView> game_rank_2_icon, game_rank_1_icon, game_rank_3_icon, game_rank_4_icon, game_rank_5_icon;
        private WeakReference<ImageView> imageview_rank_bg;

        private ImageView game_rank_1_icon_stickgame, game_rank_2_icon_stickgame,
                game_rank_3_icon_stickgame, game_rank_4_icon_stickgame, game_rank_5_icon_stickgame;

        public RankMainHolder(View view) {
            super(view);
            rank_name = (TextView) view.findViewById(R.id.rank_name);
            rank_main_layout = (RelativeLayout) view.findViewById(R.id.rank_main_layout);
            //第一名视图
            game_rank_1_name = (TextView) view.findViewById(R.id.game_rank_1_name);
            game_rank_1_icon = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.game_rank_1_icon));
            game_rank_1_down_no = (TextView) view.findViewById(R.id.game_rank_1_down_no);
            game_rank_1_btn = (Button) view.findViewById(R.id.game_rank_1_btn);
            game_rank_1_icon_stickgame = (ImageView) view.findViewById(R.id.game_rank_1_icon_stickgame);
            //第二名视图
            game_rank_2_name = (TextView) view.findViewById(R.id.game_rank_2_name);
            game_rank_2_icon = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.game_rank_2_icon));
            game_rank_2_down_no = (TextView) view.findViewById(R.id.game_rank_2_down_no);
            game_rank_2_btn = (Button) view.findViewById(R.id.game_rank_2_btn);
            game_rank_2_icon_stickgame = (ImageView) view.findViewById(R.id.game_rank_2_icon_stickgame);
            //第三名视图
            game_rank_3_name = (TextView) view.findViewById(R.id.game_rank_3_name);
            game_rank_3_icon = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.game_rank_3_icon));
            game_rank_3_down_no = (TextView) view.findViewById(R.id.game_rank_3_down_no);
            game_rank_3_btn = (Button) view.findViewById(R.id.game_rank_3_btn);
            game_rank_3_icon_stickgame = (ImageView) view.findViewById(R.id.game_rank_3_icon_stickgame);
            //第四名视图
            game_rank_4_name = (TextView) view.findViewById(R.id.game_rank_4_name);
            game_rank_4_no = (TextView) view.findViewById(R.id.game_rank_4_no);
            game_rank_4_icon = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.game_rank_4_icon));
            game_rank_4_download_no = (TextView) view.findViewById(R.id.game_rank_4_download_no);
            game_rank_4_size = (TextView) view.findViewById(R.id.game_rank_4_size);
            game_rank_4_btn = (Button) view.findViewById(R.id.game_rank_4_btn);
            game_rank_4_icon_stickgame = (ImageView) view.findViewById(R.id.game_rank_4_icon_stickgame);
            //第五名视图
            game_rank_5_no = (TextView) view.findViewById(R.id.game_rank_5_no);
            game_rank_5_icon = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.game_rank_5_icon));
            game_rank_5_name = (TextView) view.findViewById(R.id.game_rank_5_name);
            game_rank_5_download_no = (TextView) view.findViewById(R.id.game_rank_5_download_no);
            game_rank_5_size = (TextView) view.findViewById(R.id.game_rank_5_size);
            game_rank_5_btn = (Button) view.findViewById(R.id.game_rank_5_btn);
            game_rank_5_icon_stickgame = (ImageView) view.findViewById(R.id.game_rank_5_icon_stickgame);
            imageview_rank_bg = new WeakReference<ImageView>((ImageView)view.findViewById(R.id.imageview_rank_bg));

            rank_more = (TextView) view.findViewById(R.id.rank_more);
            layout_left = (RelativeLayout) view.findViewById(R.id.layout_left);

            layout_center = (RelativeLayout) view.findViewById(R.id.layout_center);

            layout_right = (RelativeLayout) view.findViewById(R.id.layout_right);

            rank_layout_4 = (RelativeLayout) view.findViewById(R.id.rank_layout_4);

            rank_layout_5 = (RelativeLayout) view.findViewById(R.id.rank_layout_5);
            rank_more.setText(LanguageValue.getInstance().getValue(mContext, "SID_CHECK_MORE"));
        }
    }

    //GlobleTopic
    public class GlobleTopicHolder extends RecyclerView.ViewHolder {
        private LinearLayout global_topic_banner_layout;
        private TextView special_name, special_des;

        public GlobleTopicHolder(View itemView) {
            super(itemView);
            global_topic_banner_layout = (LinearLayout) itemView.findViewById(R.id.global_topic_banner_layout);
            special_name = (TextView) itemView.findViewById(R.id.special_name);
            special_des = (TextView) itemView.findViewById(R.id.special_des);
        }
    }

    public class TwoClassHolder extends RecyclerView.ViewHolder {
        private FrameLayout img_parent;
        private WeakReference<ImageView> class_item_image;
        private TextView class_item_text;
        public int imgWidth;
        public int imgHeight;

        public TwoClassHolder(View itemView) {
            super(itemView);
            img_parent = (FrameLayout) itemView.findViewById(R.id.img_parent);
            class_item_text = (TextView) itemView.findViewById(R.id.class_item_text);
            class_item_image = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.class_item_image));

            imgWidth = (screenWidth / 2 - px15);
            imgHeight = (int) (imgWidth / 1.65f);
            img_parent.setLayoutParams(new FrameLayout.LayoutParams(imgWidth, imgHeight));
        }
    }

    public class TitleHolder extends RecyclerView.ViewHolder {
        private TextView view_title;
        private TextView view_title_more;

        public TitleHolder(View itemView) {
            super(itemView);
            view_title = (TextView) itemView.findViewById(R.id.view_title);
            view_title_more = (TextView) itemView.findViewById(R.id.view_title_more);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // public TextView mTextView;
        private int pos = 0;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * @return the pos
         */
        public int getPos() {
            return pos;
        }

        /**
         * @param pos the pos to set
         */
        public void setPos(int pos) {
            this.pos = pos;
        }

    }

    private void addDownloadButton(Button btn_download, ContentInfo contentInfo) {
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = contentInfo.getApp_extra();
            if (appExtra != null) {
                //报数
                ReportBusiness.getInstance().put(String.valueOf(contentInfo.getRes_id()),reportBean);
                btn_download.setTag(contentInfo);
                downLoadBusiness.addDownloadButton(btn_download, contentInfo, appExtra);
            }
        }
    }

    /**
     * 广告位ViewHolder
     */
    private class BannerViewHolder extends RecyclerView.ViewHolder {
        private MyViewPager viewPager;
        private ImageView shorty_two_shadow_top;
        private LinearLayout ll_point_parent;
        private List<View> bannerViewList = new ArrayList<View>();
        private List<ImageView> pointViewList = new ArrayList<ImageView>();
        private int lastPageSelected;
        public int bannerHeight = 0;
        public FrameLayout.LayoutParams bannerParams;
        private Handler mHandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (viewPager != null) {//显示下一个
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        };

        public BannerViewHolder(final View view) {
            super(view);
            bannerHeight = (int) (screenWidth / 1.774f);
            bannerParams = new FrameLayout.LayoutParams(screenWidth, bannerHeight);
            ll_point_parent = (LinearLayout) view.findViewById(R.id.ll_points);
            shorty_two_shadow_top = (ImageView) view.findViewById(R.id.shorty_two_shadow_top);
            viewPager = (MyViewPager) view.findViewById(R.id.viewPager);
            viewPager.setLayoutParams(bannerParams);
            viewPager.setSpeedScroller(150);//设置滑动的间隔时间
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    if (pointViewList.size() > 1) {
                        sendMessage();//发送消息
                        int position = arg0 % pointViewList.size();
                        pointViewList.get(position).setImageResource(R.drawable.public_page_lighlight);//亮圆点
                        pointViewList.get(lastPageSelected).setImageResource(R.drawable.public_page_normal);//暗圆点
                        lastPageSelected = position;//记录位置
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            if (hasShadow) {//显示阴影
                shorty_two_shadow_top.setVisibility(View.VISIBLE);
            } else {//隐藏阴影
                shorty_two_shadow_top.setVisibility(View.GONE);
            }
        }

        private void sendMessage() {
            if (mHandler != null) {
                mHandler.removeMessages(0);//移除消息
                mHandler.sendEmptyMessageDelayed(0, 5000);//延迟三秒发送消息
            }
        }

        public void reset() {
            ll_point_parent.removeAllViews();
            bannerViewList.clear();
            pointViewList.clear();
            lastPageSelected = 0;
        }

        public List<View> getBannerViewList() {
            return bannerViewList;
        }

        public List<ImageView> getPointViewList() {
            return pointViewList;
        }
    }

    /**
     * 是否为空
     * @param str 字符串
     * @return true为空，false不为空
     */
    private boolean isEmpty(String str){
        if (str == null || str.trim().length() == 0){
            return true;
        }
        return false;
    }
}