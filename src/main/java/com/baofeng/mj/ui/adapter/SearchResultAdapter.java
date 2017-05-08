package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.ui.activity.SearchActivity;
import com.baofeng.mj.ui.fragment.SearchResultFragment;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.NumFormatUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.ShowDetail;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by sunshine on 16/9/21.
 * 搜索结果Adapter
 */
public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Fragment fragment;
    private MainSubContentListBean<List<ContentInfo>> mData;
    private LayoutInflater layoutInflater;
    private List<ContentInfo> mContentInfos = new ArrayList<ContentInfo>();
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ReportFromBean reportBean;
    private String mKey;

    public SearchResultAdapter(Context context, Fragment fragment, MainSubContentListBean<List<ContentInfo>> data, List<ContentInfo> infos, DownLoadBusiness<ContentInfo> downLoadBusiness, String key) {
        this.mContext = context;
        this.fragment = fragment;
        this.mData = data;
        this.mContentInfos = infos;
        this.downLoadBusiness = downLoadBusiness;
        this.mKey = key;
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, int i1) {
        View view = null;
        switch (i) {
            case 0:
                view = layoutInflater.inflate(R.layout.search_result_empty_layout, null);
                return new EmptyViewHolder(view);
            case 1:
                view = layoutInflater.inflate(R.layout.search_result_list_video, null);
                return new VideoViewHolder(view);
            case 2:
                view = layoutInflater.inflate(R.layout.search_result_list_game, null);
                return new GameViewHolder(view);
            case 3:
                view = layoutInflater.inflate(R.layout.search_result_title_item, null);
                return new TitleViewHolder(view);
            case 4:
                view = layoutInflater.inflate(R.layout.load_more_item, null);
                LoadMoreViewHolder loadMoreViewHolder = new LoadMoreViewHolder(view);
                ((SearchResultFragment) fragment).setLoadMoreViewHolder(loadMoreViewHolder);
                return loadMoreViewHolder;
            default:
                view = layoutInflater.inflate(R.layout.search_result_title_item, null);
                return new TitleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case 0:
                fillEmptyData((EmptyViewHolder) viewHolder, mContentInfos.get(i));
                break;
            case 1:
                fillVideoData((VideoViewHolder) viewHolder, mContentInfos.get(i));
                break;
            case 2:
                fillGameData((GameViewHolder) viewHolder, mContentInfos.get(i));
                break;
            case 3:
                fillTitleData((TitleViewHolder) viewHolder, mContentInfos.get(i));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mContentInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mContentInfos.get(position).getIndex();
    }

    /**
     * 视频列表ViewHolder
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public WeakReference<ImageView> video_img;
        public TextView video_name;
        public ImageView video_label;
        public TextView video_des;

        public VideoViewHolder(View itemView) {
            super(itemView);
            video_img = new WeakReference<ImageView> ((ImageView) itemView.findViewById(R.id.video_img));
            video_name = (TextView) itemView.findViewById(R.id.video_name);
            video_label = (ImageView) itemView.findViewById(R.id.video_label);
            video_des = (TextView) itemView.findViewById(R.id.video_des);
        }
    }

    /**
     * 填充视频视图数据
     *
     * @param holder
     * @param videoInfo
     */
    private void fillVideoData(VideoViewHolder holder, final ContentInfo videoInfo) {
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(videoInfo.getPic_url(), holder.video_img, ImageLoaderUtils.getInstance().getImgOptionsThreeCross());
        GlideUtil.displayImage(fragment, holder.video_img, videoInfo.getPic_url(), R.drawable.img_default_3n_cross);
        holder.video_name.setText(highlight(videoInfo.getTitle(), mKey));
        holder.video_des.setText(highlight(videoInfo.getSubtitle(), mKey));
        ShowDetail.showVideoLabel(videoInfo.getHeadwear(), holder.video_label);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResTypeUtil.onClickToActivity(mContext, videoInfo);
                ((SearchActivity) mContext).reportVideoClick(videoInfo);
            }
        });
    }

    /**
     * 游戏搜索视图 ViewHolder
     */
    public class GameViewHolder extends RecyclerView.ViewHolder {
        public WeakReference<ImageView> list_one_image;
        public TextView list_one_name;
        public TextView list_one_type;
        public TextView list_one_downloadno;
        public TextView list_one_size;
        public TextView list_one_des;
        public Button list_one_btn;

        public GameViewHolder(View itemView) {
            super(itemView);
            list_one_image = new WeakReference<ImageView> ((ImageView) itemView.findViewById(R.id.list_one_image));
            list_one_name = (TextView) itemView.findViewById(R.id.list_one_name);
            list_one_type = (TextView) itemView.findViewById(R.id.list_one_type);
            list_one_downloadno = (TextView) itemView.findViewById(R.id.list_one_downloadno);
            list_one_size = (TextView) itemView.findViewById(R.id.list_one_size);
            list_one_des = (TextView) itemView.findViewById(R.id.list_one_des);
            list_one_btn = (Button) itemView.findViewById(R.id.list_one_btn);
        }
    }

    /**
     * 填充游戏搜索视图数据
     *
     * @param viewHolder
     * @param gameInfo
     */
    public void fillGameData(GameViewHolder viewHolder, final ContentInfo gameInfo) {
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(gameInfo.getPic_url(), viewHolder.list_one_image, ImageLoaderUtils.getInstance().getImgOptionsFour());
        GlideUtil.displayImage(fragment, viewHolder.list_one_image, gameInfo.getPic_url(), R.drawable.img_default_4n);
        viewHolder.list_one_name.setText(highlight(gameInfo.getTitle(), mKey));
        if (gameInfo.getApp_extra() != null) {
            viewHolder.list_one_downloadno.setText(NumFormatUtil.formatCount(gameInfo.getApp_extra().getDownload_count()) + "次");
            viewHolder.list_one_size.setText(gameInfo.getApp_extra().getFilesize());
            ShowDetail.showGameLabel(gameInfo.getApp_extra().getProduct_type(), viewHolder.list_one_type, mContext);
            viewHolder.list_one_des.setText(highlight(gameInfo.getApp_extra().getRecommend_desc(), mKey));
        }
        addDownloadButton(viewHolder.list_one_btn, gameInfo);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResTypeUtil.onClickToActivity(mContext, gameInfo);
                ((SearchActivity) mContext).reportGameClick(gameInfo.getRes_id(), gameInfo.getTitle(), "jump");
            }
        });
    }

    /**
     * 创建空视图ViewHolder
     */
    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        private TextView no_result_warn;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            no_result_warn = (TextView) itemView.findViewById(R.id.no_result_warn);
        }
    }

    /**
     * 填充空视图数据
     *
     * @param viewHolder
     * @param contentInfo
     */
    public void fillEmptyData(EmptyViewHolder viewHolder, ContentInfo contentInfo) {
        if (mData.getObject_type() == 1) {
            viewHolder.no_result_warn.setText("抱歉,没有找到相关视频");
        } else if (mData.getObject_type() == 2) {
            viewHolder.no_result_warn.setText("抱歉,没有找到相关应用");
        }
    }

    /**
     * 创建头部文件视图ViewHolder
     */
    public class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView view_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            view_title = (TextView) itemView.findViewById(R.id.view_title);
        }
    }

    /**
     * 填充头部文件视图数据
     *
     * @param viewHolder
     * @param titleInfo
     */
    public void fillTitleData(TitleViewHolder viewHolder, ContentInfo titleInfo) {
        viewHolder.view_title.setText(titleInfo.getTitle());
    }

    private void addDownloadButton(Button btn_download, ContentInfo contentInfo) {
        if (downLoadBusiness != null) {
            AppExtraBean appExtra = contentInfo.getApp_extra();
            if (appExtra != null) {
                //报数
                //ReportBusiness.getInstance().put(String.valueOf(contentInfo.getRes_id()),reportBean);
                btn_download.setTag(contentInfo);
                downLoadBusiness.addDownloadButton(btn_download, contentInfo, appExtra);
            }
        }
    }

    public void setReportBean(ReportFromBean reportBean) {
        this.reportBean = reportBean;
    }

    /**
     * 关键字高亮
     *
     * @param text
     * @param target
     * @return
     */
    public SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;
        if(!target.contains("*")&&!target.contains("\\")&&!target.contains("*\\")&&!target.contains("*")){
            Pattern p = null;
            try{
                p = Pattern.compile(target);
            }catch (Exception e){
                e.printStackTrace();
            }
            Matcher m = null;
            if(null != p){
                m = p.matcher(text);
            }

            while (m  != null && m.find()) {
                span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.theme_main_color));// 需要重复！
                spannable.setSpan(span, m.start(), m.end(),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }
}
