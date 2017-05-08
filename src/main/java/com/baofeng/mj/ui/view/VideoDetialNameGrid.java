package com.baofeng.mj.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.VideoDetailNameAdapter;
import com.baofeng.mj.ui.online.view.VideoPlayerPreView;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

/**
 * 视频详情页面中的选集，当选集小于等于8集时，显示title，一排两列
 * Created by muyu on 2016/5/22.
 */
public class VideoDetialNameGrid extends FrameLayout implements AdapterView.OnItemClickListener {

    private Context mContext;
    private View rootView;
    private GridView gridView;
    private VideoDetailNameAdapter adapter;
    private VideoDetailBean videoDetailBean;
    private VideoDetailBean.AlbumsBean albumsBean;

    private String detailUrl;//父层Detail页面的Url，需要传给U3D

    public VideoDetialNameGrid(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public VideoDetialNameGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_video_detail_name_grid,null);
        this.addView(rootView);
        gridView = (GridView) rootView.findViewById(R.id.view_grid);
    }

    public void initData(VideoDetailBean detailBean, String detailUrl){
        this.videoDetailBean = detailBean;
        albumsBean = detailBean.getAlbums().get(0);
        this.detailUrl = detailUrl;
        adapter = new VideoDetailNameAdapter(mContext,albumsBean.getVideos());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        reportClick( albumsBean.getVideos().get(position));
        StartActivityHelper.startVideoGoUnity((Activity) mContext, detailUrl,
                videoDetailBean.getLandscape_url().getContents(),  videoDetailBean.getLandscape_url().getNav(), albumsBean.getVideos().get(position).getSeq()+"", "detail");
        adapter.setSelectedIndexSeq(albumsBean.getVideos().get(position).getSeq());

    }

    private void reportClick(VideoDetailBean.AlbumsBean.VideosBean videoBean){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("play");
        bean.setTpos("1");
        bean.setPagetype("detail");
        bean.setTitle(videoBean.getTitle());
        bean.setMovieid(String.valueOf(videoBean.getVid()));
        bean.setMovietypeid(String.valueOf(videoDetailBean.getCategory_type()));
        ReportBusiness.getInstance().reportClick(bean);
    }

    public void setCurrentIndex(int mIndex){
        if(adapter!=null){
                adapter.setSelectedIndexSeq(mIndex+1);
        }
    }
}
