package com.baofeng.mj.ui.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.business.pluginbusiness.PluginOperateBusiness;
import com.baofeng.mj.business.pluginbusiness.PluginUIBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.LiveVideoListActivity;
import com.baofeng.mj.ui.fragment.LiveVideoListFragment;
import com.baofeng.mj.ui.view.CircleImageView;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yushaochen on 2017/2/28.
 */

public class LiveVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private Fragment mFragement;

    private int selected_type;

    private ArrayList<ContentInfo> mContentListBeans = new ArrayList<ContentInfo>();//列表数据

    public LiveVideoListAdapter(Activity context, Fragment fragement) {
        mContext = context;
        mFragement = fragement;
    }

    public void setPageSelectedType(int type) {
        selected_type = type;
    }

    private void reportClick(String resId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("etype","click");
        map.put("clicktype","chooseitem");
        map.put("tpos","1");
        if(selected_type == LiveVideoListActivity.SELECTED_TYPE_LATEST) {
            map.put("pagetype","livepagelatest");
        } else if(selected_type == LiveVideoListActivity.SELECTED_TYPE_HOTTEST){
            map.put("pagetype","livepagehot");
        }
        map.put("resid",resId);
        ReportBusiness.getInstance().reportClick(map);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mContentListBeans.size() - 1) {
            return LiveVideoListFragment.LOAD_MORE_NUM;
        } else {
            return LiveVideoListFragment.CONTENT_LIST;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, int i1) {
        View view = null;
        switch (i) {
            case LiveVideoListFragment.CONTENT_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.frag_live_video_list_content_item, null);
                return new ContentViewHolder(view);
            case LiveVideoListFragment.LOAD_MORE_NUM:
                view = LayoutInflater.from(mContext).inflate(R.layout.load_more_item, null);
                LoadMoreViewHolder loadMoreViewHolder = new LoadMoreViewHolder(view);
                if(mFragement instanceof LiveVideoListFragment){
                    ((LiveVideoListFragment) mFragement).setLoadMoreViewHolder(loadMoreViewHolder);
                }
                return loadMoreViewHolder;
        }
        view = new View(mContext);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case LiveVideoListFragment.CONTENT_LIST:
                fillContentList((ContentViewHolder)viewHolder,mContentListBeans.get(position),position);
                break;
        }
    }

    private void fillContentList(ContentViewHolder viewHolder, final ContentInfo contentListBean, int position) {
        if(null != contentListBean.getApp_extra()) {
            GlideUtil.displayImage(mContext, new WeakReference<ImageView>(viewHolder.user_head),contentListBean.getApp_extra().getHeadImg(),R.drawable.user_default_head_portrait);
            viewHolder.user_name.setText(contentListBean.getApp_extra().getNickName());
            viewHolder.from_name.setText("来源:"+contentListBean.getApp_extra().getSource());
            viewHolder.look_num.setText(contentListBean.getApp_extra().getOnLineCount()+"");
            GlideUtil.displayImage(mContext, new WeakReference<ImageView>(viewHolder.live_img),contentListBean.getPic_url(),R.drawable.live_img_default);
            viewHolder.root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PluginUIBusiness.getmInstance().openPlugin((Activity)mContext,contentListBean.getApp_extra().getSource_id()+"",contentListBean.getApp_extra().getShowId(),"");
                    //报数
                    reportClick(contentListBean.getRes_id());
                }
            });
            if(contentListBean.getApp_extra().getStatus() == ResTypeUtil.res_live_video_status_stop) {
                viewHolder.live_status.setText("休息");
                viewHolder.live_icon.setVisibility(View.GONE);
            } else if(contentListBean.getApp_extra().getStatus() == ResTypeUtil.res_live_video_status_playing) {
                viewHolder.live_status.setText("直播中");
                viewHolder.live_icon.setVisibility(View.VISIBLE);
            } else if(contentListBean.getApp_extra().getStatus() == ResTypeUtil.res_live_video_status_replay) {
                viewHolder.live_status.setText("回放");
                viewHolder.live_icon.setVisibility(View.GONE);
            }
            if(position == mContentListBeans.size() - 2) {
                viewHolder.bottom_view.setVisibility(View.GONE);
            } else {
                viewHolder.bottom_view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mContentListBeans.size();
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView user_head;
        public TextView user_name;
        public TextView from_name;
        public TextView look_num;
        public ImageView live_img;
        public ImageView live_icon;
        public TextView live_status;
        public RelativeLayout root_layout;
        public View bottom_view;
        public ContentViewHolder(View itemView) {
            super(itemView);
            user_head = (CircleImageView) itemView.findViewById(R.id.user_head);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            from_name = (TextView) itemView.findViewById(R.id.from_name);
            look_num = (TextView) itemView.findViewById(R.id.look_num);
            live_img = (ImageView) itemView.findViewById(R.id.live_img);
            live_icon = (ImageView) itemView.findViewById(R.id.live_icon);
            live_status = (TextView) itemView.findViewById(R.id.live_status);
            root_layout = (RelativeLayout) itemView.findViewById(R.id.root_layout);
            bottom_view = itemView.findViewById(R.id.bottom_view);
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
        mContentListBeans.addAll(contentListBeans);
        addBottomViewBean();
    }

    private void addBottomViewBean() {
        ContentInfo loadMoreInfo = new ContentInfo();
        loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
        mContentListBeans.add(loadMoreInfo);
    }
}
