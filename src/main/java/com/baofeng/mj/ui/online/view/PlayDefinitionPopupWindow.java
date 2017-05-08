package com.baofeng.mj.ui.online.view;

/**
 * Created by wanghongfang on 2016/12/30.
 * 在线播放清晰度选择view
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.online.adapter.PlayDefinitionAdapter;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


/**
 *
 */
public class PlayDefinitionPopupWindow extends PopupWindow {

    private Context mContext;

    private VideoSelectHdView.IChangeHDSelectIndexListener mOnChangeListener;
    private String mCur_Definition; //当前选择的清晰度
    private ListView playerHdView;
    private PanoramaVideoBean mPanoramVideoBean;  //全景视频数据
    private VideoDetailBean mVideoDetailBean;  //普通在线视频数据
    private PlayDefinitionAdapter mDefinitionAdapter;

    ArrayList<String> mVideoDdatas = new ArrayList<>();
    public PlayDefinitionPopupWindow(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindow_player_hd, null);
        setContentView(view);
        playerHdView = (ListView) view.findViewById(R.id.video_hd_list);

        setWidth(PixelsUtil.dip2px(66.6f));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.mj_player_popview_bg));


         mDefinitionAdapter = new PlayDefinitionAdapter(mContext);
        playerHdView.setAdapter(mDefinitionAdapter);

        setListener();
    }

    private void setListener(){
        playerHdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDefinitionAdapter.setCurrentSelect(position);
                mCur_Definition = mVideoDdatas.get(position);
                if(mOnChangeListener!=null){
                    mOnChangeListener.onChangeHd(mCur_Definition);
                }
            }
        });
    }

    public void setPanoramDatas(PanoramaVideoBean videoBean){
        this.mPanoramVideoBean = videoBean;
        List<PanoramaVideoAttrs> video_attrs = mPanoramVideoBean.getVideo_attrs();
        ArrayList<String> datas = new ArrayList<>();
        for(PanoramaVideoAttrs video:video_attrs){
            datas.add(video.getDefinition_name());
        }
        //去重复
        LinkedHashSet<String> set = new LinkedHashSet<String>(datas);
        mVideoDdatas.clear();
        mVideoDdatas = new ArrayList<String>(set);
        mDefinitionAdapter.setData(mVideoDdatas);
    }

    public void setMovieVideoDatas(VideoDetailBean videosBean){
        this.mVideoDetailBean = videosBean;
       List<VideoDetailBean.AlbumsBean> albumsBeans = mVideoDetailBean.getAlbums();
        ArrayList<String> datas = new ArrayList<>();
        for(VideoDetailBean.AlbumsBean albumsBean:albumsBeans){
            datas.add(albumsBean.getHdtype()+"");
        }
        //去重复
        LinkedHashSet<String> set = new LinkedHashSet<String>(datas);
        mVideoDdatas.clear();
        mVideoDdatas = new ArrayList<String>(set);
        mDefinitionAdapter.setData(mVideoDdatas);
    }

    public void setCurDefinition(String hdtype){
        mCur_Definition = hdtype;
        if(mVideoDdatas==null)
            return;
        for(int i=0;i<mVideoDdatas.size();i++){
            String hdstr = mVideoDdatas.get(i);
            if(hdstr.equals(hdtype)){
                if(mDefinitionAdapter!=null){
                    mDefinitionAdapter.setCurrentSelect(i);
                }
                break;
            }
        }

    }


    public void setOnItemClickListener(VideoSelectHdView.IChangeHDSelectIndexListener changeListener) {
        this.mOnChangeListener = changeListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(String position);
    }
}

