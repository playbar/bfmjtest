package com.baofeng.mj.ui.popwindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.fileutil.FileCommonUtil;

/**
 * Created by liuchuanchi on 2016/5/17.
 * 本地视频排序popWindow
 */
public class LocalVideoSortPop extends PopupWindow {
    private Context context;
    private LocalVideoSortCallback localVideoSortCallback;

    public LocalVideoSortPop(Activity context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mMenuView = inflater.inflate(R.layout.pop_local_video_sort, null);
        final LinearLayout pop_layout = (LinearLayout) mMenuView.findViewById(R.id.pop_layout);
        final LinearLayout ll_sort_time = (LinearLayout) mMenuView.findViewById(R.id.ll_sort_time);
        final LinearLayout ll_sort_name = (LinearLayout) mMenuView.findViewById(R.id.ll_sort_name);
        final LinearLayout ll_sort_size = (LinearLayout) mMenuView.findViewById(R.id.ll_sort_size);
        final TextView tv_sort_time = (TextView) mMenuView.findViewById(R.id.tv_sort_time);
        final TextView tv_sort_name = (TextView) mMenuView.findViewById(R.id.tv_sort_name);
        final TextView tv_sort_size = (TextView) mMenuView.findViewById(R.id.tv_sort_size);
        final ImageView iv_sort_time = (ImageView) mMenuView.findViewById(R.id.iv_sort_time);
        final ImageView iv_sort_name = (ImageView) mMenuView.findViewById(R.id.iv_sort_name);
        final ImageView iv_sort_size = (ImageView) mMenuView.findViewById(R.id.iv_sort_size);
        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        this.setFocusable(true); //设置弹出窗体可点击
        this.setAnimationStyle(R.style.LocalVideoPopAnimation);//设置弹出窗体动画效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);//实例化一个ColorDrawable颜色为半透明
        this.setBackgroundDrawable(dw);//设置SelectPicPopupWindow弹出窗体的背景
        mMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        ll_sort_time.setOnClickListener(new View.OnClickListener() { //添加时间排序
            public void onClick(View v) {
                if(localVideoSortCallback != null){
                    selectStyle(tv_sort_time, iv_sort_time);
                    normalStyle(tv_sort_name, iv_sort_name);
                    normalStyle(tv_sort_size, iv_sort_size);
                    SettingSpBusiness.getInstance().setLocalVideoSort(0);
                    localVideoSortCallback.select(0);
                    dismissPop();
                }
            }
        });
        ll_sort_name.setOnClickListener(new View.OnClickListener() { //文件名排序
            public void onClick(View v) {
                if(localVideoSortCallback != null){
                    normalStyle(tv_sort_time, iv_sort_time);
                    selectStyle(tv_sort_name, iv_sort_name);
                    normalStyle(tv_sort_size, iv_sort_size);
                    SettingSpBusiness.getInstance().setLocalVideoSort(1);
                    localVideoSortCallback.select(1);
                    dismissPop();
                }
            }
        });
        ll_sort_size.setOnClickListener(new View.OnClickListener() { //文件大小排序
            public void onClick(View v) {
                if(localVideoSortCallback != null){
                    normalStyle(tv_sort_time, iv_sort_time);
                    normalStyle(tv_sort_name, iv_sort_name);
                    selectStyle(tv_sort_size, iv_sort_size);
                    SettingSpBusiness.getInstance().setLocalVideoSort(2);
                    localVideoSortCallback.select(2);
                    dismissPop();
                }
            }
        });

        int sortRule = SettingSpBusiness.getInstance().getLocalVideoSort();
        if(FileCommonUtil.ruleFileLastModify == sortRule){//添加时间排序
            selectStyle(tv_sort_time, iv_sort_time);
        }else if(FileCommonUtil.ruleFileName == sortRule){//文件名排序
            selectStyle(tv_sort_name, iv_sort_name);
        }else{//文件大小排序
            selectStyle(tv_sort_size, iv_sort_size);
        }
    }

    /**
     * 选中样式
     */
    private void selectStyle(TextView tv, ImageView iv){
        tv.setTextColor(context.getResources().getColor(R.color.theme_main_color));
        iv.setVisibility(View.VISIBLE);
    }

    /**
     * 未选中样式
     */
    private void normalStyle(TextView tv, ImageView iv){
        tv.setTextColor(context.getResources().getColor(R.color.prompt_color));
        iv.setVisibility(View.GONE);
    }

    public void showPop(View parent, LocalVideoSortCallback localVideoSortCallback){
        if(!isShowing()){
            this.localVideoSortCallback = localVideoSortCallback;
            showAsDropDown(parent);
        }
    }

    public void dismissPop(){
        if(isShowing()){
            dismiss();
        }
    }

    public interface LocalVideoSortCallback{
        void select(int sortRule);
    }
}
