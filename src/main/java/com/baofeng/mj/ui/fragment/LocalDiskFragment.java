package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.baofeng.mj.R;

/**
 * 本地网盘页面
 * Created by muyu on 2016/4/6.
 */
public class LocalDiskFragment extends BaseFragment {
    private final String url = "http://pan.baidu.com/disk/home";
    private boolean isFirstVisble = true;

    private View rootView;
    private WebView diskWebView;

    public static LocalDiskFragment getInstance() {
        return new LocalDiskFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_local_disk, null);
        initViews();
        return rootView;
    }

    protected void initViews() {
        diskWebView = (WebView) rootView.findViewById(R.id.local_disk_webview);
        //WebViewUtil.webViewInit(diskWebView, getActivity());

        diskWebView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && diskWebView.canGoBack()) {
                    diskWebView.goBackOrForward(-1);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onPause() {
        if (diskWebView != null) {
            diskWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // webView.loadData("", "text/html; charset=UTF-8", null);
        diskWebView.destroy();
        isFirstVisble = true;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        if (diskWebView != null) {
            diskWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (diskWebView != null) {
            if (isVisibleToUser) {
                // if (webView.isPaused()) {
                if (isFirstVisble) {
                    diskWebView.loadUrl(url);
                    isFirstVisble = false;
                }
                diskWebView.requestFocus();

                // }
            } else {

            }
        }

        super.setUserVisibleHint(isVisibleToUser);
    }
}
