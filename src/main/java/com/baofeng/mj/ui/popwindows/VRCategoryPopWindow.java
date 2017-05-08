package com.baofeng.mj.ui.popwindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HomeSubTabVRBean;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.ui.adapter.VRCateoryAdapter;

import java.util.List;

/** 首页面VR分类PopWindow
 * Created by muyu on 2017/3/3.
 */
public class VRCategoryPopWindow extends PopupWindow implements View.OnClickListener{
    private Context mContext;
    private View contentView;
    private List<HomeSubTabVRBean> mHomeSubTabVRBeans;
    private GridView categoryGridView;
    private VRCateoryAdapter adapter;
    private ImageView closeIV;
    private OnItemClickCallback onItemClickCallback;

    public VRCategoryPopWindow(Context context, List<HomeSubTabVRBean> homeSubTabVRBeans) {
        super(context);
        this.mContext = context;
        this.mHomeSubTabVRBeans = homeSubTabVRBeans;
        initView();
    }

    public VRCategoryPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public VRCategoryPopWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popwindow_vr_category, null);
        setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        categoryGridView = (GridView) contentView.findViewById(R.id.vr_category_gridview);
        closeIV = (ImageView) contentView.findViewById(R.id.vr_category_close_imageview);
        closeIV.setOnClickListener(this);
//        System.out.println("testtest VRCategory mHomeSubTabVRBeans:"+mHomeSubTabVRBeans.toString());
        adapter = new VRCateoryAdapter(mContext, mHomeSubTabVRBeans);
        categoryGridView.setAdapter(adapter);
        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                if (onItemClickCallback != null) {
                    onItemClickCallback.onItemClick(position);
                }
                dismiss();
            }
        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setSelectedIndex(int position){
        adapter.setSelectedIndex(position);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.vr_category_close_imageview) {
            dismiss();
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(int position);
    }
}
