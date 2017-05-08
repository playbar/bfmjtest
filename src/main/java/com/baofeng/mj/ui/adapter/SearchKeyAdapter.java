package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.KeyWordListBean;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by sunshine on 16/9/20.
 * 热搜词Adapter
 */
public class SearchKeyAdapter extends BaseAdapter {
    private Context mContext;
    private KeyWordListBean keyWordListBean;
    private LayoutInflater mLayoutInflater;
    private itemClickInterface mItemClickInterface;

    public SearchKeyAdapter(Context context, KeyWordListBean keyWordListBean, itemClickInterface itemClickInterface) {
        this.mContext = context;
        this.keyWordListBean = keyWordListBean;
        this.mItemClickInterface = itemClickInterface;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return keyWordListBean.getList().size();
    }

    @Override
    public Object getItem(int position) {
        return keyWordListBean.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.keyword_item, null);
            viewHolder.key_no = (TextView) convertView.findViewById(R.id.key_no);
            viewHolder.key_name = (TextView) convertView.findViewById(R.id.key_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            viewHolder.key_no.setTextColor(mContext.getResources().getColor(R.color.auxiliary_red_color));
        } else if (position == 1) {
            viewHolder.key_no.setTextColor(mContext.getResources().getColor(R.color.auxiliary_yellow_color));
        } else if (position == 2) {
            viewHolder.key_no.setTextColor(mContext.getResources().getColor(R.color.auxiliary_blue_color));
        } else {
            viewHolder.key_no.setTextColor(mContext.getResources().getColor(R.color.content_color));
        }
        viewHolder.key_no.setText(position + 1 + "");
        final String keyWord = keyWordListBean.getList().get(position).getTitle();
        viewHolder.key_name.setText(keyWord);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickInterface != null) {
                    mItemClickInterface.itemClick(keyWord);
                }
            }
        });
        return convertView;
    }

    public final class ViewHolder {
        public TextView key_no;
        public TextView key_name;
    }

    public interface itemClickInterface {
        public void itemClick(String keyWord);
    }
}
