package com.baofeng.mj.ui.online.view;

/**
 * Created by wanghongfang on 2016/12/30.
 * 在线播放选集view
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.online.adapter.PlaySelectionAdapter;
import java.util.ArrayList;
import java.util.List;


public class PlaySelectPopupWindow extends RelativeLayout {

    private Context mContext;
    private VideoSelectHdView.IChangeHDSelectIndexListener mOnChangeListener;
    private int mCur_Index_num; //当前选择的集数
    private ListView mSelectListView;
    private PlaySelectionAdapter mSelectionAdapter;
    private PlaySelectionAdapter mGridViewAdapter;
    private LinearLayout mIndexSelectView;
    private TextView mIndexTv;
    private RelativeLayout mIndexLayout;
    private GridView mIndexGridView;
    private VideoDetailBean mVieoDetailBean;
    private ImageView mIndex_img;
    private final  int PAGE_NUM = 50;
    ArrayList<String> mVideoDatas = new ArrayList<>();
    public PlaySelectPopupWindow(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public PlaySelectPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindow_player_select, null);
        this.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
       mSelectListView = (ListView) view.findViewById(R.id.video_select_list);
       mIndexSelectView = (LinearLayout) view.findViewById(R.id.video_index_select_view);
       mIndexTv = (TextView) view.findViewById(R.id.index_tv);
        mIndexLayout = (RelativeLayout) view.findViewById(R.id.index_layout);
        mIndexGridView = (GridView)view.findViewById(R.id.index_select_gridView);
        mIndex_img = (ImageView)view.findViewById(R.id.index_img);

        this.setFocusable(true);

        mSelectionAdapter = new PlaySelectionAdapter(mContext);
        mSelectionAdapter.setTextLeft(true);
        mGridViewAdapter = new PlaySelectionAdapter(mContext);
        mSelectListView.setAdapter(mSelectionAdapter);
        mIndexGridView.setAdapter(mGridViewAdapter);
        setListener();
    }

    private void setListener(){
        mSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectionAdapter.setCurrentSelect(position);
                if(mVieoDetailBean.getCategory_type()==1||mVieoDetailBean.getCategory_type()==4){
                    if(mOnChangeListener!=null){
                        List<VideoDetailBean.AlbumsBean.VideosBean> videoBean = mVieoDetailBean.getAlbums().get(0).getVideos();
                        if(videoBean!=null&&videoBean.size()>position) {
                            mOnChangeListener.onChangeSelectIndex(videoBean.get(position).getSeq()-1);
                        }else {
                            mOnChangeListener.onChangeSelectIndex(position);
                        }
                    }
                }else {
                    String content = (String) mSelectionAdapter.getItem(position);
                    changeGridView(content);
                }

            }
        });
        mIndexGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGridViewAdapter.setCurrentSelect(position);
                if(mOnChangeListener!=null) {
                    String index = gridDatas.get(position);
                    if(!TextUtils.isEmpty(index)){
                        int indexNum = Integer.parseInt(index);
                        if(indexNum>0){
                            indexNum = indexNum-1;
                        }
                        mOnChangeListener.onChangeSelectIndex(indexNum);
                    }

                }
            }
        });
        mIndexLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoDatas==null||mVideoDatas.size()<=0)
                    return;
                if(mIndexSelectView!=null){
                    mIndexSelectView.setVisibility(GONE);
                }
                if(mSelectListView!=null) {
                    mSelectListView.setVisibility(VISIBLE);
                }
            }
        });
    }
    ArrayList<String> gridDatas = new ArrayList<>();
    private void changeGridView(String content){
        if(mIndexSelectView!=null){
            mIndexSelectView.setVisibility(VISIBLE);
        }
        mIndexTv.setText(content);
        String[] num = content.split("-");
        int start = Integer.parseInt(num[0]);
        int end = Integer.parseInt(num[1]);
        gridDatas.clear();
        for(int i=start;i<=end;i++){
            gridDatas.add(i+"");
        }
        if(mCur_Index_num>=start&&mCur_Index_num<=end){
            mGridViewAdapter.setCurrentSelect(mCur_Index_num-1);
        }else {
            mGridViewAdapter.setCurrentSelect(-1);
        }
        mGridViewAdapter.setData(gridDatas);
        setCurSelectGridView(start);

        mSelectListView.setVisibility(GONE);
    }

    /**
     * 当前选项 一级选项（例如：0-50,51-100 ）
     * @param start
     */
    private void setCurSelectGridView(int start ){
        if(gridDatas!=null&&mGridViewAdapter!=null) {
            if (mCur_Index_num - start >= 0 && mCur_Index_num <= gridDatas.size()) {
                mGridViewAdapter.setCurrentSelect(mCur_Index_num - start);
            }
        }
    }

    public void setMovieVideoDatas(VideoDetailBean videosBean){
        if(videosBean==null)
            return;
        mVieoDetailBean = videosBean;
        int  categoryType = mVieoDetailBean.getCategory_type();
        if(categoryType==1||categoryType==4){//综艺和电影 直接显示标题
            initMovieDatas();
        }else {
            initListData();
        }

    }

    private void initMovieDatas(){
        VideoDetailBean.AlbumsBean albumsBean = mVieoDetailBean.getAlbums().get(0);
        if(albumsBean!=null) {
            List<VideoDetailBean.AlbumsBean.VideosBean> videos = albumsBean.getVideos();
            mVideoDatas.clear();
            for(VideoDetailBean.AlbumsBean.VideosBean video:videos){
                mVideoDatas.add(video.getTitle());
            }
            mSelectionAdapter.setData(mVideoDatas);
        }

    }

    /**
     * 除了综艺和电影外其他视频选集展示数据
     */
    private void initListData(){
        String totalStr = mVieoDetailBean.getTotal();
        if(!TextUtils.isEmpty(totalStr)){
            int size = Integer.parseInt(totalStr);
            mVideoDatas.clear();
            if(size<=50){ //小于50集时直接显示gridView
                changeGridView("1-"+size);
                mIndex_img.setVisibility(GONE);
                return;
            }

            int page = (size % PAGE_NUM == 0) ? size / PAGE_NUM
                    : (size / PAGE_NUM + 1);
            int start = 0;
            int end = 0;
            for (int i = 0; i < page; i++) {
                start = (i * PAGE_NUM) + 1;
                if (i == (page - 1)) {
                    end = size;
                } else {
                    end = (i + 1) * PAGE_NUM;
                }
                mVideoDatas.add(start + "-" + end);
            }
        }
        mSelectionAdapter.setData(mVideoDatas);

    }

    /**
     * 设置当前选中的索引
     * @param index
     */
    public void setCurIndex(int index){
        mCur_Index_num = index+1;

        if(mVieoDetailBean==null)
            return;
        if(mVieoDetailBean.getCategory_type()==1||mVieoDetailBean.getCategory_type()==4){
            int pos =getPosBySeq(index+1);
            if(pos>=0&&pos<mVideoDatas.size()) {
                mSelectionAdapter.setCurrentSelect(pos);
            }
        }else {

            String totalStr = mVieoDetailBean.getTotal();
            if(!TextUtils.isEmpty(totalStr)) {
                int size = Integer.parseInt(totalStr);
                if(size<=50){
                    if (gridDatas != null && mGridViewAdapter != null) {
                        setCurSelectGridView(1);
                    }
                    return;
                }
            }

            for (int i = 0; i < mVideoDatas.size(); i++) {
                String hdstr = mVideoDatas.get(i);
                String[] num = hdstr.split("-");
                int start = Integer.parseInt(num[0]);
                int end = Integer.parseInt(num[1]);
                if (mCur_Index_num >= start && mCur_Index_num <= end) {
                    mSelectionAdapter.setCurrentSelect(i);
                    if (gridDatas != null && mGridViewAdapter != null) {
                        setCurSelectGridView(start);
                    }

                    break;
                }

            }
        }

    }

    private int getPosBySeq(int seq){
        if(mVieoDetailBean==null)
            return -1;
        List<VideoDetailBean.AlbumsBean.VideosBean> videos = mVieoDetailBean.getAlbums().get(0).getVideos();
        if(videos!=null&&videos.size()>0){
            for(int i=0;i<videos.size();i++){
                VideoDetailBean.AlbumsBean.VideosBean bean = videos.get(i);
                if(bean.getSeq()==seq){
                    return i;
                }
            }
        }
        return -1;
    }


    public void setOnItemClickListener(VideoSelectHdView.IChangeHDSelectIndexListener changeListener) {
        this.mOnChangeListener = changeListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(String position);
    }
}

