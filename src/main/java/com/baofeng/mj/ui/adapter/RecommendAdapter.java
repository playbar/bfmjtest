package com.baofeng.mj.ui.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadutil.PublicoConfig;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.fragment.RecommendFragement;
import com.baofeng.mj.ui.view.RecommendTopListView;
import com.baofeng.mj.ui.viewholder.NewLoadMoreViewHolder;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by yushaochen on 2017/1/18.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private Fragment mFragement;
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ReportFromBean reportBean;//报数

    private int screenWidth;

    private ArrayList<ContentInfo> mContentListBeans = new ArrayList<ContentInfo>();//中部列表数据

    private boolean mIsRefreshTop  = true;

    public RecommendAdapter(Activity context, Fragment fragement) {
        mContext = context;
        mFragement = fragement;
        screenWidth = PixelsUtil.getWidthPixels();
        mContentListBeans.add(new ContentInfo());
    }

    public void setDownLoadBusiness(DownLoadBusiness<ContentInfo> downLoadBusiness) {
        this.downLoadBusiness = downLoadBusiness;
    }

    public void setReportBean(ReportFromBean reportBean) {
        this.reportBean = reportBean;
    }

    @Override
    public int getItemViewType(int position) {
        int type = mContentListBeans.get(position).getType();
        if(position == 0) {
            return RecommendFragement.TOP_LIST;
        }else if(position == mContentListBeans.size() - 1) {
            return RecommendFragement.LOAD_MORE_LIST;
        }else if(type == ResTypeUtil.res_type_apply || type == ResTypeUtil.res_type_game){ //应用 或者游戏
            return RecommendFragement.APP_OR_GAME;
        } else {
            return RecommendFragement.CONTENT_LIST;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, int i1) {
        View view = null;
        switch (i) {
            case RecommendFragement.TOP_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.recommend_top, null);
                return new TopViewHolder(view);
            case RecommendFragement.CONTENT_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.recommend_content_item, null);
                return new ContentViewHolder(view);
            case RecommendFragement.APP_OR_GAME:
                view = LayoutInflater.from(mContext).inflate(R.layout.recommend_game_item,null);
                return new GameViewHolder(view);
            case RecommendFragement.LOAD_MORE_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.new_load_more_item, null);
                NewLoadMoreViewHolder loadMoreViewHolder = new NewLoadMoreViewHolder(view);
                if(mFragement instanceof RecommendFragement){
                    ((RecommendFragement) mFragement).setLoadMoreViewHolder(loadMoreViewHolder);
                }
                return loadMoreViewHolder;
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case RecommendFragement.TOP_LIST:
                fillTopList((TopViewHolder)viewHolder);
                break;
            case RecommendFragement.CONTENT_LIST:
                fillContentList((ContentViewHolder)viewHolder,mContentListBeans.get(position),position);
                break;
            case RecommendFragement.APP_OR_GAME:
                fillGameList((GameViewHolder)viewHolder,mContentListBeans.get(position));
                break;
        }
    }

    private void fillContentList(ContentViewHolder viewHolder, final ContentInfo contentListBean, final int position) {
        viewHolder.content_text.setText(contentListBean.getTitle());
        if(!TextUtils.isEmpty(contentListBean.getCate())) {
            viewHolder.video_type1.setText("#"+contentListBean.getCate());
            viewHolder.video_type1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.video_type1.setVisibility(View.GONE);
        }
        if(contentListBean.getType() == ResTypeUtil.res_type_video) {
            viewHolder.video_type2.setText("#VR");
        } else {
            viewHolder.video_type2.setText("#影视");
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.mainClickPosition = position;
                //ResTypeUtil.onClickToActivity(mContext, contentListBean);
                if (contentListBean.getType() == ResTypeUtil.res_type_video) {//全景视频
                    StartActivityHelper.startPanoramaGoUnity(mContext, contentListBean.getType(), contentListBean.getUrl(), contentListBean.getUrl(), "", ReportBusiness.PAGE_TYPE_DETAIL, StartActivityHelper.online_resource_from_default);
                } else {//非全景视频
                    StartActivityHelper.goVrPlay(mContext, contentListBean.getUrl(), contentListBean.getUrl(), "", 0 + "", ReportBusiness.PAGE_TYPE_DETAIL);
                }
            }
        });
        if(contentListBean.getFavor_num() <= 0) {
            viewHolder.collection_count.setVisibility(View.GONE);
            viewHolder.icon_star.setVisibility(View.GONE);
        } else {
            viewHolder.collection_count.setText(formatCount(contentListBean.getFavor_num()));
            viewHolder.collection_count.setVisibility(View.VISIBLE);
            viewHolder.icon_star.setVisibility(View.VISIBLE);
        }
        GlideUtil.displayImage(mContext, new WeakReference<ImageView>(viewHolder.content_image),contentListBean.getPic_url(),R.drawable.recommend_content_default_bg);
    }

    private void fillGameList(GameViewHolder viewHolder, final ContentInfo contentListBean) {
        viewHolder.recommend_game_name.setText(contentListBean.getTitle());
        viewHolder.recommend_game_size.setText(contentListBean.getApp_extra().getFilesize());
        viewHolder.recommend_game_type.setText("#"+contentListBean.getCate());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = contentListBean.getType();
                if (type == ResTypeUtil.res_type_apply || type == ResTypeUtil.res_type_game) {
                    ResTypeUtil.onClickToActivity(mContext, contentListBean);
                }
            }
        });
        viewHolder.collection_count.setText(formatCount(Integer.parseInt(contentListBean.getApp_extra().getDownload_count())));
        GlideUtil.displayImage(mContext, new WeakReference<ImageView>(viewHolder.recommend_game_image), contentListBean.getPic_url(), R.drawable.recommend_content_default_bg);
        GlideUtil.displayImage(mContext, new WeakReference<ImageView>(viewHolder.recommend_game_icon), contentListBean.getApp_extra().getIcon_url(), R.drawable.img_default_4n);

        if (downLoadBusiness != null) {
            AppExtraBean appExtra = contentListBean.getApp_extra();
            if (appExtra != null) {
                viewHolder.recommend_game_download_btn.setTag(contentListBean);
                //报数
                ReportBusiness.getInstance().put(String.valueOf(contentListBean.getRes_id()),reportBean);
                downLoadBusiness.addDownloadButton(viewHolder.recommend_game_download_btn, contentListBean, appExtra);
            }
        }

    }

    private String formatCount(int count) {
        if(count < 1000 && count > 0) {
            return "<1000";
        } else if(count >= 1000 && count <10000) {
            return count+"";
        } else if(count >= 10000 && count <100000) {
            return new BigDecimal(count/10000d).setScale(1, BigDecimal.ROUND_FLOOR).doubleValue() + "万";
        } else {
            return ((int) (count/10000d))+"万";
        }
    }

    private void fillTopList(TopViewHolder viewHolder) {
        viewHolder.recommend_top_list.requestData(mIsRefreshTop);
        mIsRefreshTop = false;
    }

    @Override
    public int getItemCount() {
        return mContentListBeans.size();
    }

    private class TopViewHolder extends RecyclerView.ViewHolder {

        private RecommendTopListView recommend_top_list;

        public TopViewHolder(View itemView) {
            super(itemView);
            recommend_top_list = (RecommendTopListView) itemView.findViewById(R.id.recommend_top_list);

        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {

        private ImageView content_image;
        private ImageView alpha_bg;
        private TextView content_text;
        private TextView collection_count;
        private TextView video_type1;
        private TextView video_type2;
        private ImageView icon_star;
        private int imgWidth;
        private int imgHeight;

        public ContentViewHolder(View itemView) {
            super(itemView);
            imgWidth = screenWidth;
            imgHeight = (int)(screenWidth/1.774f);
            content_text = (TextView) itemView.findViewById(R.id.content_text);
            video_type1 = (TextView) itemView.findViewById(R.id.video_type1);
            video_type2 = (TextView) itemView.findViewById(R.id.video_type2);
            content_image = (ImageView) itemView.findViewById(R.id.content_image);
            content_image.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
            alpha_bg = (ImageView) itemView.findViewById(R.id.alpha_bg);
            //alpha_bg.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
            icon_star = (ImageView) itemView.findViewById(R.id.icon_star);
            collection_count = (TextView) itemView.findViewById(R.id.collection_count);
        }
    }

    private class GameViewHolder extends RecyclerView.ViewHolder {

        private ImageView recommend_game_image;
        private ImageView alpha_bg;

        private ImageView recommend_game_icon;
        private TextView recommend_game_name;
        private TextView recommend_game_size;
        private TextView recommend_game_type;
        private Button recommend_game_download_btn;

        private TextView collection_count;

        private int imgWidth;
        private int imgHeight;

        public GameViewHolder(View itemView) {
            super(itemView);
            imgWidth = screenWidth;
            imgHeight = (int)(screenWidth/1.774f);

            recommend_game_image = (ImageView) itemView.findViewById(R.id.recommend_game_image);
            recommend_game_image.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
            alpha_bg = (ImageView) itemView.findViewById(R.id.alpha_bg);
            alpha_bg.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
            collection_count = (TextView) itemView.findViewById(R.id.collection_count);

            recommend_game_icon = (ImageView)itemView.findViewById(R.id.recommend_game_icon);
            recommend_game_name = (TextView) itemView.findViewById(R.id.recommend_game_name);
            recommend_game_size = (TextView) itemView.findViewById(R.id.recommend_game_size);
            recommend_game_type = (TextView)itemView.findViewById(R.id.recommend_game_type);
            recommend_game_download_btn = (Button)itemView.findViewById(R.id.recommend_game_download_btn);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public void setContentData(ArrayList<ContentInfo> contentListBeans) {
        if(null == contentListBeans) {
            return;
        }
        mContentListBeans.clear();
        mContentListBeans.add(new ContentInfo());
        mContentListBeans.addAll(contentListBeans);
        addBottomViewBean();
    }

    private void addBottomViewBean() {
        ContentInfo loadMoreInfo = new ContentInfo();
        loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
        mContentListBeans.add(loadMoreInfo);
    }

    public void isRefreshTop(boolean isRefreshTop) {
        mIsRefreshTop = isRefreshTop;
    }
}