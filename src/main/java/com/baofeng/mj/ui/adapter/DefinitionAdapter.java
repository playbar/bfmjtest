package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;

import java.util.ArrayList;
import java.util.List;

/**选择视频下载类型
 * Created by muyu on 2016/5/21.
 */
public class DefinitionAdapter extends BaseAdapter {
    private Context mContext;
    private List<PanoramaVideoAttrs> mList = new ArrayList<PanoramaVideoAttrs>();
    private int result;

    public DefinitionAdapter(Context context, List<PanoramaVideoAttrs> list) {
        this.mContext = context;
        this.mList = list;
        result = SettingSpBusiness.getInstance().getHigh();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mList == null || mList.isEmpty()) ? 0 : mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.definition_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.definition_item_textview = (TextView) view.findViewById(R.id.definition_item_textview);
            viewHolder.definition_item_unlock = (ImageView) view.findViewById(R.id.definition_item_unlock);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, (PanoramaVideoAttrs) getItem(position));
        return view;
    }
    public void fillData(ViewHolder viewHolder, PanoramaVideoAttrs data) {
        viewHolder.definition_item_textview.setText(data.getDefinition_name() + "(" + data.getSize() + ")");
        if (2 != result && "4k".equals(data.getDefinition_name())) {
            viewHolder.definition_item_unlock.setVisibility(View.VISIBLE);
            viewHolder.definition_item_textview.setTextColor(mContext.getResources().getColor(R.color.bg_color));
        }else {
            viewHolder.definition_item_unlock.setVisibility(View.GONE);
            viewHolder.definition_item_textview.setTextColor(mContext.getResources().getColor(R.color.theme_main_color));
        }
    }
    class ViewHolder {
        private TextView definition_item_textview;
        private ImageView definition_item_unlock;
    }
}
