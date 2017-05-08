package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.adapter.AllSelectionContentAdapter;
import com.baofeng.mj.ui.online.utils.PlayerModeChooseSubject;
import com.baofeng.mj.ui.view.BaseGridView;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

import java.util.ArrayList;

/**
 * 视频选集Content内容 Fragment
 * Created by muyu on 2016/5/6.
 */
public class AllSelectionContentFragment extends BaseFragment implements AdapterView.OnItemClickListener,PlayerModeChooseSubject.IPlayerChooseCallback{

    private View rootView;
    private BaseGridView allSelectionGridView;
    private AllSelectionContentAdapter allSelectionContentAdapter;
    private String detailUrl;
    private String contents;
    private String nav;
    private int select_index;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_video_all_selection, null);
        initViews();
        return rootView;
    }

    private void initViews(){
        PlayerModeChooseSubject.getInstance().Bind(this);
        allSelectionGridView = (BaseGridView) rootView.findViewById(R.id.all_selection_view_grid);
        detailUrl = getArguments().getString("detailUrl");
        contents = getArguments().getString("contents");
        nav = getArguments().getString("nav");
        select_index = getArguments().getInt("select_index");
        ArrayList<VideoDetailBean.AlbumsBean.VideosBean> list = (ArrayList) getArguments().get("selectionList");
        allSelectionContentAdapter = new AllSelectionContentAdapter(getActivity(), list);
        allSelectionGridView.setAdapter(allSelectionContentAdapter);
        allSelectionGridView.setOnItemClickListener(this);
        if(select_index != 0) {
            int newIndex = select_index%((int)30) - 1;
            allSelectionContentAdapter.setSelectedIndex(newIndex);
        }
    }

    private int mPreSeletPosion = 0;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPreSeletPosion = allSelectionContentAdapter.getSelectedIndex();
        allSelectionContentAdapter.setSelectedIndex(position);
        RadioButton radioButton = (RadioButton)((LinearLayout) view).getChildAt(0);
        StartActivityHelper.startVideoGoUnity(getActivity(), detailUrl, contents, nav, (String) radioButton.getText(),"detail");
    }

    public void setPosition(int mIndex){
        if(allSelectionContentAdapter!=null&&allSelectionContentAdapter.getCount()>mIndex){
            allSelectionContentAdapter.setSelectedIndex(mIndex);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayerModeChooseSubject.getInstance().unBind(this);
    }

    @Override
    public void doVRPlay(String SqlNo) {

    }

    @Override
    public void doNormalPlay(String SqlNo) {

    }

    @Override
    public void onChooseViewClose() {
        allSelectionContentAdapter.setSelectedIndex(mPreSeletPosion);
    }
}


