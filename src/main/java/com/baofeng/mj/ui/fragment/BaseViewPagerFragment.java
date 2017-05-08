package com.baofeng.mj.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baofeng.mj.ui.activity.BaseActivity;
import com.mojing.dl.domain.DownloadItem;

import java.util.List;

/**
 * fragment基类
 * Created by muyu on 2016/3/28.
 */
public abstract class BaseViewPagerFragment extends Fragment {
    protected View rootView;

    /**
     * 判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
     */
    public void removeRootView(){
        if(rootView != null){
            ViewGroup parent = (ViewGroup)rootView.getParent();
            if(parent != null) {
                parent.removeView(rootView);
            }
        }
    }

    /**
     * @description 显示加载Dialog
     */
    public void showProgressDialog(String message) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showProgressDialog(message);
        }
    }

    public void showProgressDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showProgressDialog();
        }
    }

    public void showProgressDialog(int resId) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showProgressDialog(resId);
        }
    }

    /**
     * 隐藏加载Dialog
     */
    public void dismissProgressDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).dismissProgressDialog();
        }
    }

    /**
     * 当Fragment嵌套时直接startActivityForResult无法收到onActivityResult的回调
     *
     * @param intent
     * @param requestCode
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Fragment parent = getParentFragment();
        if (parent != null) {
            parent.startActivityForResult(intent, requestCode);
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * fragment在 onDestroyView时 释放图片资源
     * @param view
     */
    public void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(rootView != null) {
            unbindDrawables(rootView);
        }
    }

    /**
     * 用于处理fragment嵌套时子Fragment无法收到onActivityResult
     * XXX 如果Google某天修复了这个问题，直接去掉这个方法
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // notifying nested fragments (support library bug fix)
        final FragmentManager childFragmentManager = getChildFragmentManager();

        if (childFragmentManager != null) {
            final List<Fragment> nestedFragments = childFragmentManager.getFragments();

            if (nestedFragments == null || nestedFragments.size() == 0) return;

            for (Fragment childFragment : nestedFragments) {
                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
                    childFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(DownloadItem downloadItem){
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(int downloadingSize,List<DownloadItem> downloadingList){
    }

}
