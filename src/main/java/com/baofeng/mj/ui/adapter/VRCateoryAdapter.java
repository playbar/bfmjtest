package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.HomeSubTabVRBean;

import java.util.List;

/**
 * Created by muyu on 2017/3/3.
 */
public class VRCateoryAdapter extends BaseAdapter {
    private  List<HomeSubTabVRBean> mHomeSubTabVRBeans;
    private Context context;
    private int selectedIndex = 0;

    public VRCateoryAdapter(Context context, List<HomeSubTabVRBean> homeSubTabVRBeans) {
        this.context = context;
        this.mHomeSubTabVRBeans = homeSubTabVRBeans;
    }

    public void setSelectedIndex(int position) {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }
    @Override
    public int getCount() {
        return mHomeSubTabVRBeans == null ? 0 : mHomeSubTabVRBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mHomeSubTabVRBeans == null ? null : mHomeSubTabVRBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mHomeSubTabVRBeans == null || mHomeSubTabVRBeans.isEmpty()) ? 0 : mHomeSubTabVRBeans.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        final MyGridViewItemViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.vr_category_item, null);
            viewHolder = new MyGridViewItemViewHolder();
            viewHolder.ItemText = (RadioButton) view.findViewById(R.id.cube_icon_radiobtn);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (MyGridViewItemViewHolder) view.getTag();
        }

        viewHolder.ItemText.setClickable(false);

        HomeSubTabVRBean videosBean = mHomeSubTabVRBeans.get(position);

        if(selectedIndex == position) {
            viewHolder.ItemText.setChecked(true);
//            videosBean.setSeletedStatus(true);
        } else{
            viewHolder.ItemText.setChecked(false);
//            videosBean.setSeletedStatus(false);
        }
        //update view holder
        updateViewHolder(viewHolder, (HomeSubTabVRBean) getItem(position));
        return view;
    }

    private void updateViewHolder(final MyGridViewItemViewHolder viewHolder, HomeSubTabVRBean data) {
        viewHolder.ItemText.setText(data.getTitle()+"");
    }

    public void update(List<HomeSubTabVRBean> homeSubTabVRBeans) {
        this.mHomeSubTabVRBeans = homeSubTabVRBeans;
        notifyDataSetChanged();
    }

    public void add(List<HomeSubTabVRBean> homeSubTabVRBeans) {
        if (this.mHomeSubTabVRBeans == null) {
            this.mHomeSubTabVRBeans = homeSubTabVRBeans;
        } else {
            mHomeSubTabVRBeans.clear();
            this.mHomeSubTabVRBeans.addAll(homeSubTabVRBeans);
        }
        notifyDataSetChanged();
    }

    public void update() {
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return mHomeSubTabVRBeans != null;
    }

    class MyGridViewItemViewHolder {
        private RadioButton ItemText;
    }
}

