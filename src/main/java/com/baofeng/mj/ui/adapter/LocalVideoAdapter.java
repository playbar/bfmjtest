package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.LocalVideoBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.viewutil.ShowUi;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by muyu on 2016/4/7.
 * 本地视频
 */
public class LocalVideoAdapter extends BaseAdapter {
    private Context context;
    private Fragment fragment;
    private List<LocalVideoBean> videoBeanList;
    private LayoutInflater layoutInflater;

    public LocalVideoAdapter(Context context, Fragment fragment, List<LocalVideoBean> videoBeanList) {
        this.context = context;
        this.fragment = fragment;
        this.videoBeanList = videoBeanList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return videoBeanList == null ? 0 : videoBeanList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return videoBeanList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductListItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_local_video, null);
            viewHolder = new ProductListItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ProductListItemViewHolder) convertView.getTag();
        }

        final LocalVideoBean videoBean = videoBeanList.get(position);
        ShowUi.showTitle(viewHolder.local_file_title, videoBean.name, 15);//显示文件名
        String describe = videoBean.size;//文件大小
        int fileSuffixStartIndex = videoBean.name.lastIndexOf(".") + 1;//文件后缀名开始位置
        if(fileSuffixStartIndex >= 0){
            String fileSuffix = videoBean.name.substring(fileSuffixStartIndex,videoBean.name.length());//文件后缀名
            describe += "   " + fileSuffix.toUpperCase();//文件后缀名
        }
        viewHolder.local_file_describe.setText(describe);//显示描述
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(videoBean.thumbPath, viewHolder.local_file_image, ImageLoaderUtils.getInstance().getImgOptionsThreeCross());//显示缩略图
        GlideUtil.displayImage(fragment, viewHolder.local_file_image, videoBean.thumbPath, R.drawable.img_default_3n_cross);
//        viewHolder.local_file_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//预览
//                String localFormat = videoBean.path.substring(videoBean.path.lastIndexOf(".")+1);
//                reportClick(videoBean.name, localFormat, videoBean.name);
////                Intent intent = new Intent(context, LocalVideoPreviewActivity.class);
//                Intent intent = new Intent(context, MediaGlActivity.class);
//                intent.putExtra("videoPath",videoBean.path);
//                intent.putExtra("videoName",videoBean.name);
//                context.startActivity(intent);
//            }
//        });
        if(videoBeanList.size() == 1 || position < videoBeanList.size() - 1){
            viewHolder.view_divider.setVisibility(View.VISIBLE);
        }else{
            viewHolder.view_divider.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ProductListItemViewHolder {
        private WeakReference<ImageView> local_file_image;
        private TextView local_file_title;
        private TextView local_file_describe;
        private Button local_file_btn;
        private View view_divider;

        public ProductListItemViewHolder(View view) {
            local_file_image = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.local_file_image));
            local_file_title = (TextView) view.findViewById(R.id.local_file_title);
            local_file_describe = (TextView) view.findViewById(R.id.local_file_describe);
            local_file_btn = (Button) view.findViewById(R.id.local_file_btn);
            view_divider = view.findViewById(R.id.view_divider);
        }
    }

    /**
     * 点击预览报数
     */
    private void reportClick(String title, String localFormat, String filePath){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("prev");
        bean.setTpos("1");
        bean.setPagetype("local");
        bean.setTitle(title);
        bean.setLocal_menu_id("1");
        bean.setLocal_format(localFormat);
        bean.setFilepath(filePath);
        ReportBusiness.getInstance().reportClick(bean);
    }


}