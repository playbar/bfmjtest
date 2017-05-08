package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.AppListActivity;
import com.baofeng.mj.ui.view.BannerView;
import com.baofeng.mj.ui.view.BaseView;
import com.baofeng.mj.ui.view.CircleIconScrollView;
import com.baofeng.mj.ui.view.CircleIconView;
import com.baofeng.mj.ui.view.PicBannerView;
import com.baofeng.mj.ui.view.SelectItemView;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.NumFormatUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.viewutil.ShowDetail;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hanyang on 2016/7/8.
 * 列表页、筛选页、更多页合并Adapter
 */
public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SelectItemView.SelectChange {
    private Context mContext;
    private List<ContentBaseBean> beanList;
    private boolean hasShadow; //首页的banner有上半部分阴影
    private LayoutInflater layoutInflater;
    private boolean hasTag;
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private boolean isRank;
    private SelectChanged selectChanged;
    private int screenWidth;
    private int px40, px20, px15, px13, px10, px7, px5, px3;
    //报数
    private ReportFromBean reportBean;

    public AppListAdapter(Context context, List<ContentBaseBean> beans, SelectChanged selectChanged) {
        this.mContext = context;
        this.beanList = beans;
        this.selectChanged = selectChanged;
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
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
    }

    @Override
    public int getItemCount() {
        if (beanList == null || beanList.size() <= 0) {
            return 0;
        }
        return beanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String typeName = beanList.get(position).getLayout_type();
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
        } else if (typeName.equals(FindViewGroup.SELECT_TYPE)) {
            return FindViewGroup.SELECT_TYPE_NUM;
        } else if (typeName.equals(FindViewGroup.LINE)) {
            return FindViewGroup.LINE_NUM;
        } else if (typeName.equals(FindViewGroup.NO_DATA)) {
            return FindViewGroup.NO_DATA_NUM;
        } else if (typeName.equals(FindViewGroup.LOAD_MORE)) {
            return FindViewGroup.LOAD_MORE_NUM;
        } else if(typeName.equals(FindViewGroup.APP_VIDEO)){
            return FindViewGroup.APP_VIDEO_NUM;
        }
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
                view = new CircleIconScrollView(mContext, hasTag);
                //报数
                ((BaseView) view).setReportBean(reportBean);
                return new ViewHolder(view);
            case FindViewGroup.NAV_MULT_NUM:
                view = new CircleIconView(mContext, hasTag);
                //报数
                ((BaseView) view).setReportBean(reportBean);
                return new ViewHolder(view);
            case FindViewGroup.GLOBAL_BANNNER_NUM:
                view = new BannerView(mContext, hasTag, hasShadow);
                //报数
                ((BaseView) view).setReportBean(reportBean);
                return new ViewHolder(view);
            case FindViewGroup.GLOBAL_TOPIC_NUM:
                view = layoutInflater.inflate(R.layout.global_topic_item, null);
                return new GlobleTopicHolder(view);
            case FindViewGroup.APP_CATEGORY_NUM:
                view = layoutInflater.inflate(R.layout.two_class_item, null);
                return new TwoClassHolder(view);
            case FindViewGroup.LAYOUR_TITLE_NUM:
                view = layoutInflater.inflate(R.layout.title_item, null);
                return new TitleHolder(view);
            case FindViewGroup.SELECT_TYPE_NUM://筛选选项
                view = new SelectItemView(mContext, (SelectListBean<SelectDetailBean>) beanList.get(i1), this);
                ViewHolder holder = new ViewHolder(view);
                holder.setIsRecyclable(false);
                return holder;
            case FindViewGroup.LINE_NUM://横线
                view = layoutInflater.inflate(R.layout.view_line_divider_nomargin, null);
                View dividerView = view.findViewById(R.id.line_divider);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerView.setLayoutParams(lineParams);
                ViewHolder lineViewHolder = new ViewHolder(view);
                return lineViewHolder;
            case FindViewGroup.NO_DATA_NUM://空页面
                view = layoutInflater.inflate(R.layout.view_empty_without_btn, null);
                ViewGroup.LayoutParams emptyParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(emptyParams);
                return new ViewHolder(view);
            case FindViewGroup.LOAD_MORE_NUM://加载更多
                view = layoutInflater.inflate(R.layout.load_more_item, null);
                LoadMoreViewHolder loadMoreViewHolder = new LoadMoreViewHolder(view);
                if(mContext instanceof AppListActivity){
                    ((AppListActivity) mContext).setLoadMoreViewHolder(loadMoreViewHolder);
                }
                return loadMoreViewHolder;
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
            ((BaseView) viewHolder.itemView).initView((MainSubContentListBean<List<ContentInfo>>) beanList.get(position));
        } else {
            ContentBaseBean itemDara = beanList.get(position);
            viewHolder.itemView.setTag(itemDara);
            switch (getItemViewType(position)) {
                case FindViewGroup.VIDEO_H1_NUM:
                    fillOneListView((HighOneHolder) viewHolder, (ContentInfo) itemDara, position);
                    break;
                case FindViewGroup.VIDEO_H2_NUM:
                    fillViewTwoHor((ViewTwoHorizontalHolder) viewHolder, (ContentInfo) itemDara, position);
                    break;
                case FindViewGroup.VIDEO_H3_NUM:
                    fillViewThreeHor((ViewThreeHorizontalHolder) viewHolder, (ContentInfo) itemDara, position);
                    break;
                case FindViewGroup.VIDEO_V2_NUM:
                    fillViewTwoVertical((ViewTwoVerticalHolder) viewHolder, (ContentInfo) itemDara, position);
                    break;
                case FindViewGroup.VIDEO_V3_NUM:
                    fillViewThreeVertical((ViewThreeVerticalHolder) viewHolder, (ContentInfo) itemDara, position);
                    break;
                case FindViewGroup.APP_H_NUM:
                    fillViewRectFourColumn((RectFourHolder) viewHolder, (ContentInfo) itemDara);
                    break;
                case FindViewGroup.APP_V_NUM:
                    fillViewListOne((ListOneHolder) viewHolder, (ContentInfo) itemDara, position);
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
                case 15://筛选选项
                    ((SelectItemView) viewHolder.itemView).setCheck(((SelectListBean) itemDara).getSelectPos());
                    break;
                //TODO 新增类型
                default:
                    break;
            }
        }
    }

    private void fillOneListView(HighOneHolder viewHolder, final ContentInfo data, int pos) {
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
        GlideUtil.displayImage(mContext, viewHolder.hight_one_image, data.getPic_url(), R.drawable.img_default_1n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
        viewHolder.hight_one_image.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
            }
        });
    }

    //ViewTwoHorView
    private void fillViewTwoHor(ViewTwoHorizontalHolder viewHolder, final ContentInfo data, int pos) {
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
        ShowDetail.showVideoLabel(data.getHeadwear(), viewHolder.shorty_two_headwear);
        viewHolder.ItemImage.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
            }
        });
        if (data.getIndex() % 2 == 0) { //左边
            viewHolder.item_parent.setPadding(px10, 0, px5, px13);
        }else{//右边
            viewHolder.item_parent.setPadding(px5, 0, px10, px13);
        }
        GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_2n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
    }

    //ViewTwoHorView
    private void fillViewTwoVertical(ViewTwoVerticalHolder viewHolder, final ContentInfo data, int pos) {
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
            }
        });
        if (data.getIndex() % 2 == 0) { //左边
            viewHolder.item_parent.setPadding(px10, 0, px5, px13);
        }else{//右边
            viewHolder.item_parent.setPadding(px5, 0, px10, px13);
        }
        GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_2n_vertical, viewHolder.imgWidth, viewHolder.imgHeight);
    }

    //ViewThreeHor
    private void fillViewThreeHor(ViewThreeHorizontalHolder viewHolder, final ContentInfo data, int pos) {
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
        GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_3n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
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
        GlideUtil.displayImage(mContext, viewHolder.ItemImage, data.getPic_url(), R.drawable.img_default_3n_vertical, viewHolder.imgWidth, viewHolder.imgHeight);
    }

    //RectFourColumn
    private void fillViewRectFourColumn(RectFourHolder viewHolder, final ContentInfo data) {
        viewHolder.rect_four_name.setText(data.getTitle());
        GlideUtil.displayImage(mContext, viewHolder.rect_four_image, data.getPic_url(), R.drawable.img_default_4n);
        viewHolder.rect_four_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
            }
        });
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = data.getApp_extra();
            if (appExtra != null) {
                viewHolder.rect_four_btn.setTag(data);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(data.getRes_id()), reportBean);
                downLoadBusiness.addDownloadButton(viewHolder.rect_four_btn, data, appExtra);
            }
        }
    }

    //ViewListOne
    private void fillViewListOne(ListOneHolder viewHolder, final ContentInfo data, int position) {
        GlideUtil.displayImage(mContext, viewHolder.list_one_image, data.getPic_url(), R.drawable.img_default_4n);
        if (isRank) {
            viewHolder.list_one_no.setVisibility(View.VISIBLE);
            viewHolder.list_one_no.setText((position + 1) + "");
        } else {
            viewHolder.list_one_no.setVisibility(View.GONE);
        }
        viewHolder.list_one_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
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
            System.out.println("testtest playmodeList:"+playmodeList);
            for(String str: playmodeList){
                if(str.equals("6")){ //体感游戏
                    viewHolder.list_one_stickgame.setVisibility(View.VISIBLE);
                }
            }
        }
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = data.getApp_extra();
            if (appExtra != null) {
                viewHolder.list_one_btn.setTag(data);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(data.getRes_id()), reportBean);
                downLoadBusiness.addDownloadButton(viewHolder.list_one_btn, data, appExtra);
            }
        }
    }

    //TopicItem
    private void fillTopicItem(GlobleTopicHolder viewHolder, final ContentInfo data) {
        viewHolder.special_name.setText(data.getTitle());
        viewHolder.special_des.setText(data.getSubtitle());
        PicBannerView picBannerView = new PicBannerView(mContext, data.getBanner(), false);
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
            }
        });
        if (data.getIndex() % 2 == 0) { //左边
            viewHolder.img_parent.setPadding(px10, 0, px5, px10);
        }else{//右边
            viewHolder.img_parent.setPadding(px5, 0, px10, px10);
        }
        GlideUtil.displayImage(mContext, viewHolder.class_item_image, data.getPic_url(), R.drawable.img_default_2n_cross, viewHolder.imgWidth, viewHolder.imgHeight);
    }

    //ViewTitle
    private void fillViewTitle(TitleHolder viewHolder, final ContentInfo data) {
        viewHolder.view_title.setText(data.getTitle());
        if (data.getHas_more() == 0) {
            viewHolder.view_title_more.setVisibility(View.GONE);
        } else {
            viewHolder.view_title_more.setText(LanguageValue.getInstance().getValue(mContext, "SID_MORE"));
            viewHolder.view_title_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportBusiness.getInstance().putHeader(data, reportBean);
                    ResTypeUtil.moreClick(mContext, data);
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

            //视图一填充数据
            viewHolder.game_rank_1_name.setText(contentInfo1.getTitle());
            viewHolder.game_rank_1_down_no.setText(NumFormatUtil.formatCount(contentInfo1.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(contentInfo1.getPic_url(), viewHolder.game_rank_1_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            GlideUtil.displayImage(mContext, viewHolder.game_rank_1_icon, contentInfo1.getPic_url(), R.drawable.img_default_4n);
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
                }
            });

            //视图二填充数据
            viewHolder.game_rank_2_name.setText(contentInfo2.getTitle());
            viewHolder.game_rank_2_down_no.setText(NumFormatUtil.formatCount(contentInfo2.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(contentInfo2.getPic_url(), viewHolder.game_rank_2_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            GlideUtil.displayImage(mContext, viewHolder.game_rank_2_icon, contentInfo2.getPic_url(), R.drawable.img_default_4n);

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
                }
            });
            //视图三填充数据
            viewHolder.game_rank_3_name.setText(contentInfo3.getTitle());
            viewHolder.game_rank_3_down_no.setText(NumFormatUtil.formatCount(contentInfo3.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(contentInfo3.getPic_url(), viewHolder.game_rank_3_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            GlideUtil.displayImage(mContext, viewHolder.game_rank_3_icon, contentInfo3.getPic_url(), R.drawable.img_default_4n);

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
                }
            });
            //视图四填充数据
            viewHolder.game_rank_4_name.setText(contentInfo4.getTitle());
            viewHolder.game_rank_4_size.setText(contentInfo4.getApp_extra().getFilesize());
            viewHolder.game_rank_4_download_no.setText(NumFormatUtil.formatCount(contentInfo4.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(contentInfo4.getPic_url(), viewHolder.game_rank_4_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            GlideUtil.displayImage(mContext, viewHolder.game_rank_4_icon, contentInfo4.getPic_url(), R.drawable.img_default_4n);

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
                }
            });
            //视图五填充数据
            viewHolder.game_rank_5_name.setText(contentInfo5.getTitle());
            viewHolder.game_rank_5_size.setText(contentInfo5.getApp_extra().getFilesize());
            viewHolder.game_rank_5_download_no.setText(NumFormatUtil.formatCount(contentInfo5.getApp_extra().getDownload_count()) + LanguageValue.getInstance().getValue(mContext, "SID_TIMES"));
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(contentInfo5.getPic_url(), viewHolder.game_rank_5_icon, ImageLoaderUtils.getInstance().getImgOptionsFour());
            GlideUtil.displayImage(mContext, viewHolder.game_rank_5_icon, contentInfo5.getPic_url(), R.drawable.img_default_4n);

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
                    ReportBusiness.getInstance().putHeader(contentInfo5, reportBean);
                    ResTypeUtil.onClickToActivity(mContext, contentInfo5);
                }
            });
            viewHolder.rank_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportBusiness.getInstance().put(data.getUrl(), reportBean);
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
                }
            });
        }
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
        private WeakReference<ImageView> rect_four_image;
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
        private int pos = 0;

        public ViewHolder(View itemView) {
            super(itemView);
        }
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

    public void setIsRank(boolean isRank) {
        this.isRank = isRank;
    }

    public boolean isRank() {
        return isRank;
    }

    public void setDownLoadBusiness(DownLoadBusiness<ContentInfo> downLoadBusiness) {
        this.downLoadBusiness = downLoadBusiness;
    }

    /**
     * 下载
     *
     * @param btn_download
     * @param contentInfo
     */
    private void addDownloadButton(Button btn_download, ContentInfo contentInfo) {
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = contentInfo.getApp_extra();
            if (appExtra != null) {
                btn_download.setTag(contentInfo);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(contentInfo.getRes_id()), reportBean);
                downLoadBusiness.addDownloadButton(btn_download, contentInfo, appExtra);
            }
        }
    }

    @Override
    public void select(RadioGroup group, int checkedId) {
        if (selectChanged != null) {
            Log.d("check", checkedId + "");
            selectChanged.change(checkedId);
        }
    }

    public interface SelectChanged {
        public void change(int checkId);
    }

    public SelectItemView.SelectChange getSelectChange() {
        return this;
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
