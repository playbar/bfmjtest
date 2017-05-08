package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.SubBean;
import com.baofeng.mj.business.publicbusiness.CancleAlbumBusiness;
import com.baofeng.mj.ui.dialog.CancleAlbumDialog;

import java.util.List;

/**
 * Created by hanyang on 2016/6/21.
 */
public class AlbumListView extends LinearLayout {
    private View rootView;
    private GridView gridView;
    private AlbumListAdapter adapter;
    private List<SubBean> data;
    private Context mContext;
    private CancleAlbumBusiness cancleAlbumBusiness;

    public AlbumListView(Context context, List<SubBean> data, CancleAlbumBusiness cancleAlbumBusiness) {
        super(context);
        this.mContext = context;
        this.data = data;
        this.cancleAlbumBusiness = cancleAlbumBusiness;
        initView();
    }

    public AlbumListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_ablum_list, null);
        this.addView(rootView);
        gridView = (GridView) rootView.findViewById(R.id.view_album_grid_view);
        adapter = new AlbumListAdapter(mContext, data);
        gridView.setAdapter(adapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new CancleAlbumDialog().showDialog(mContext, cancleAlbumBusiness, position, data.get(position).getAlbum_id());
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
