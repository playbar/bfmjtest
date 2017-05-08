package com.baofeng.mj.ui.popwindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.DirFile;
import com.baofeng.mj.business.brbusiness.ExternalStorageReceiver;
import com.baofeng.mj.ui.dialog.CopyFileDialog;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchuanchi on 2016/5/17.
 * 下载路径选择popWindow
 */
public class DownloadPathSelectPop extends PopupWindow {
    private Activity context;
    private List<DirFile> dirFileList;
    private LayoutInflater inflater;
    private CopyFileDialog copyFileDialog;
    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    private CopyFileDialog.CopyFileCallback copyFileCallback;

    public DownloadPathSelectPop(Activity context, List<DirFile> dirFileList) {
        super(context);
        this.context = context;
        this.dirFileList = dirFileList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mMenuView = inflater.inflate(R.layout.pop_download_path_select, null);
        final LinearLayout ll_path = (LinearLayout) mMenuView.findViewById(R.id.ll_path);
        final TextView tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true); //设置弹出窗体可点击
        this.setAnimationStyle(R.style.PopupAnimation);//设置弹出窗体动画效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);//实例化一个ColorDrawable颜色为半透明
        this.setBackgroundDrawable(dw);//设置SelectPicPopupWindow弹出窗体的背景

        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int y = (int) event.getY();
                    int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                    if (y < height) { //获取触屏位置，如果在选择框外面则销毁弹出框
                        dismissPop();
                    }
                }
                return true;
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() { //取消按钮
            public void onClick(View v) {
                dismissPop();
            }
        });
        for (int i = 0; i < dirFileList.size(); i++) {
            ll_path.addView(createDirFileView(i));
        }
    }

    private View createDirFileView(final int position) {
        final DirFile dirFile = dirFileList.get(position);
        View downloadPathLayout =  inflater.inflate(R.layout.pop_download_path_select_item, null);//生成布局文件
        TextView tv_path = (TextView) downloadPathLayout.findViewById(R.id.tv_path);
        ImageView iv_path_selected = (ImageView) downloadPathLayout.findViewById(R.id.iv_path_selected);
        tv_path.setText(dirFile.getDirName());
        tv_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
                String newStorageDir = dirFile.getDirFile().getAbsolutePath() + "/";
                String oldStorageDir = FileStorageUtil.getDownloadDir();
                if(!new File(newStorageDir).exists()){//新的存储路径不存在
                    Toast.makeText(context, "存储空间不存在！", Toast.LENGTH_SHORT).show();
                }else if(!new File(oldStorageDir).exists()){//当前存储路径不存在（比如当前存储路径是sdcard，用户把sdcard移除了）
                    ExternalStorageReceiver.changeDownloadDir();//此时无法拷贝文件，直接切换存储路径
                    if(copyFileCallback != null){
                        copyFileCallback.callback(true);
                    }
                }else if (!oldStorageDir.equals(newStorageDir)) {
                    long fileSize = FileSizeUtil.getFileSize(oldStorageDir);
                    if (dirFile.getAvilableSize() < fileSize) {
                        Toast.makeText(context, "剩余空间不足！", Toast.LENGTH_SHORT).show();
                    } else {
//                        DemoUtils.pasueAllDownload(context, DownloadConstant.PauseReason.USER_CLICK);//暂停资源下载
//                        APKDownloadUtils.getInstance().pauseAll();//暂停魔镜app下载
                        copyFileDialog.showDialog(false, true);//显示拷贝文件对话框
                        copyFileDialog.copyFiles(oldStorageDir, newStorageDir, position, copyFileCallback);//开始拷贝文件
                    }
                }
            }
        });
        imageViewList.add(iv_path_selected);
        return downloadPathLayout;
    }

    public void showPop(View parent, int gravity, int x, int y, CopyFileDialog.CopyFileCallback copyFileCallback) {
        if (!isShowing()) {
            showAtLocation(parent, gravity, x, y);
        }
        this.copyFileCallback = copyFileCallback;
        int storageMode = FileStorageUtil.getStorageMode();
        for(int i = 0; i < imageViewList.size(); i++){
            ImageView imageView = imageViewList.get(i);
            if(i == storageMode){
                imageView.setVisibility(View.VISIBLE);
            }else{
                imageView.setVisibility(View.GONE);
            }
        }
    }

    public void dismissPop() {
        if (isShowing()) {
            dismiss();
        }
    }

    public void setCopyFileDialog(CopyFileDialog copyFileDialog) {
        this.copyFileDialog = copyFileDialog;
    }
}
