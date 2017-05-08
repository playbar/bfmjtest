package com.baofeng.mj.ui.popwindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.adapter.NameSelectionAdapter;
import com.baofeng.mj.ui.online.utils.PlayerModeChooseSubject;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

/**
 *
 * 影片名字 更多 PopWindow
 * Created by muyu on 2016/5/31.
 */
public class NameSelectionPopWindow extends PopupWindow implements View.OnClickListener, AdapterView.OnItemClickListener,PlayerModeChooseSubject.IPlayerChooseCallback{

    private Activity activity;
    private View contentView;
    private  VideoDetailBean detailBean;
    private ListView videoListView;
    private NameSelectionAdapter adapter;
    private ImageView cancelTV;
    private String detailUrl;

    public NameSelectionPopWindow(Activity activity, VideoDetailBean detailBean, String detailUrl) {
        super(activity);
        this.activity = activity;
        this.detailBean = detailBean;
        this.detailUrl = detailUrl;
        initView();
        PlayerModeChooseSubject.getInstance().Bind(this);
    }

    public NameSelectionPopWindow(Activity activity, AttributeSet attrs) {
        super(activity, attrs);
        this.activity = activity;
        initView();
    }

    public NameSelectionPopWindow(Activity activity, AttributeSet attrs, int defStyle) {
        super(activity, attrs, defStyle);
        this.activity = activity;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popwindow_name_selection, null);
        setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        videoListView = (ListView) contentView.findViewById(R.id.pop_name_selection_listview);
        cancelTV = (ImageView) contentView.findViewById(R.id.pop_name_selection_back);
        cancelTV.setOnClickListener(this);
        adapter = new NameSelectionAdapter(activity,detailBean.getAlbums().get(0));
        videoListView.setAdapter(adapter);
        videoListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.pop_name_selection_back){
            dismiss();
        }
    }

    private int mPrePostion=0;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPrePostion = adapter.getSelectedIndex();
        adapter.setSelectedIndex(position);
        int seq = detailBean.getAlbums().get(0).getVideos().get(position).getSeq();
        StartActivityHelper.startVideoGoUnity(activity, detailUrl, detailBean.getLandscape_url().getContents(), detailBean.getLandscape_url().getNav(),seq+"","detail");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        PlayerModeChooseSubject.getInstance().unBind(this);
    }

    @Override
    public void doVRPlay(String SqlNo) {
    }

    @Override
    public void doNormalPlay(String SqlNo) {//如果用户选择了极简播放模式，需要手动关闭给View
        dismiss();
    }

    @Override
    public void onChooseViewClose() {
        adapter.setSelectedIndex(mPrePostion);
    }

    public void setCurrentIndex(int mIndex){
        if(adapter!=null&&adapter.getCount()>mIndex){
            adapter.setSelectedIndex(mIndex);
        }
    }
}