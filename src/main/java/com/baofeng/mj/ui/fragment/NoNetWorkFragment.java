package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baofeng.mj.R;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.view.EmptyView;

/**
 * 首页面4个Tab，没网络时，显示这个fragment
 * Created by muyu on 2016/7/16.
 */
public class NoNetWorkFragment extends BaseFragment implements View.OnClickListener{

    private View rootView;
    private EmptyView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frag_no_network, null);
        }
        initView();
        return rootView;
    }

    public void initView(){
        //空页面
        emptyView = (EmptyView) rootView.findViewById(R.id.no_network_empty_view);
        emptyView.getRefreshView().setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.refreshView){
            //((MainActivityGroup)getActivity()).requestData(null);
        }
    }
}
