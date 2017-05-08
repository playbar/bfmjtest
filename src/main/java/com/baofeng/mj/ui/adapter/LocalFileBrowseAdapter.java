package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.NewFile;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.publicutil.VideoExtensionUtil;

import java.io.File;
import java.util.List;

/**
 * 本地文件浏览器
 */
public class LocalFileBrowseAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<File> fileList;
    private boolean isRoot;//是不是根目录，true是，false不是

    public LocalFileBrowseAdapter(Context context, List<File> fileList, boolean isRoot) {
        this.fileList = fileList;
        this.isRoot = isRoot;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList == null ? 0 : fileList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_local_file_browse, null);
            viewHolder = new MViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MViewHolder) convertView.getTag();
        }

        File file = fileList.get(position);
        if(isRoot){//是根目录，根目录的实体类是NewFile
            viewHolder.tv_name.setText(((NewFile)file).getNewName());//名称
        }else{
            viewHolder.tv_name.setText(file.getName());//名称
        }
        if(file.isFile()){//文件
            if(VideoExtensionUtil.fileIsVideo(file)){//视频文件
                viewHolder.iv_image.setImageResource(R.drawable.local_video_icon);
            }else{//其他文件
                viewHolder.iv_image.setImageResource(R.drawable.local_file_icon);
            }
            String fileSize = FileSizeUtil.formatFileSize(FileSizeUtil.getFileSize(file));
            viewHolder.tv_size.setText(fileSize);//文件大小
        }else if(file.isDirectory()){//文件夹
            viewHolder.iv_image.setImageResource(R.drawable.local_folder_icon);
            String[] fileArr = file.list();
            if(fileArr == null){
                viewHolder.tv_size.setText("0个文件");
            }else{
                viewHolder.tv_size.setText(fileArr.length + "个文件");
            }
        }
        if(fileList.size() == 1 || position < fileList.size() - 1){
            viewHolder.view_divider.setVisibility(View.VISIBLE);
        }else{
            viewHolder.view_divider.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class MViewHolder {
        private ImageView iv_image;
        private TextView tv_name;
        private TextView tv_size;
        private View view_divider;

        public MViewHolder(View view) {
            iv_image = (ImageView) view.findViewById(R.id.iv_image);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            view_divider = view.findViewById(R.id.view_divider);
        }
    }
}