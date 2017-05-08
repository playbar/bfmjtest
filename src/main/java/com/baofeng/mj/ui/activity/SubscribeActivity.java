package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.SubAlbumBean;
import com.baofeng.mj.bean.SubBean;
import com.baofeng.mj.business.publicbusiness.CancleAlbumBusiness;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.AlbumListView;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.SubApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 订阅页面
 * Created by muyu on 2016/5/11.
 */
public class SubscribeActivity extends BaseActivity implements View.OnClickListener, CancleAlbumBusiness {

    private AppTitleBackView appTitleLayout;
    private Button sub_scan_btn;
    private LinearLayout album_layout, activity_noSub_layout;
    private ScrollView album_list_layout;
    private List<SubBean> subBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        initView();
    }

    private void initView() {
        appTitleLayout = (AppTitleBackView) findViewById(R.id.subscribe_title_layout);
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_SUBSCRIBE"));
        sub_scan_btn = (Button) findViewById(R.id.sub_scan_btn);
        sub_scan_btn.setOnClickListener(this);
        album_layout = (LinearLayout) findViewById(R.id.album_layout);
        album_list_layout = (ScrollView) findViewById(R.id.album_list_layout);
        activity_noSub_layout = (LinearLayout) findViewById(R.id.activity_noSub_layout);
        getAlbumList();
    }

    /**
     * 获取用户订阅专辑列表
     */
    private void getAlbumList() {
        RequestParams requestParams = getRequestParams(null);
        new SubApi().getAlbumList(this, ConfigUrl.getAlbumUrl(), requestParams, new ApiCallBack<SubAlbumBean>() {
            @Override
            public void onSuccess(SubAlbumBean result) {
                super.onSuccess(result);
                if (null != result) {
                    if (result.getStatus() == 0) {
                        if (null != result.getData()) {
                            if (result.getData().size() > 0) {
                                album_list_layout.setVisibility(View.VISIBLE);
                                activity_noSub_layout.setVisibility(View.GONE);
                                bindView(result.getData());
                            } else {
                                album_list_layout.setVisibility(View.GONE);
                                activity_noSub_layout.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }
        });
    }

    /**
     * 绑定数据，添加视图
     */
    private void bindView(List<SubBean> subBeanList) {
        subBeans = subBeanList;
        album_layout.removeAllViews();
        AlbumListView albumListView = new AlbumListView(this, subBeanList, this);
        album_layout.addView(albumListView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sub_scan_btn) {
            startActivity(new Intent(this, CodeScanActivity.class));
        }
    }

    @Override
    public void cancleCallBack(final int position, String albumId) {
        RequestParams requestParams = getRequestParams(albumId);
        requestParams.put("album_id", albumId);
        new SubApi().cancleAlbum(this, ConfigUrl.getCancleAlbumUrl(), requestParams, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(SubscribeActivity.this, "取消订阅失败", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("status") && jsonObject.getInt("status") == 0) {
                            refreshView(position);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Toast.makeText(SubscribeActivity.this, "取消订阅失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在取消订阅");
            }
        });
    }

    /**
     * 刷新View
     */
    private void refreshView(int position) {
        if (position < subBeans.size()) {
            subBeans.remove(position);
        } else {
            return;
        }
        if (null == subBeans || subBeans.size() == 0) {
            album_list_layout.setVisibility(View.GONE);
            activity_noSub_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            album_list_layout.setVisibility(View.VISIBLE);
            activity_noSub_layout.setVisibility(View.GONE);
            album_layout.removeAllViews();
            AlbumListView albumListView = new AlbumListView(this, subBeans, this);
            album_layout.addView(albumListView);
        }
    }

    /**
     * 获取RequestParams
     *
     * @return
     */
    private RequestParams getRequestParams(String albumId) {
        HashMap<String, String> params = new HashMap<String, String>();
        String time = System.currentTimeMillis() + "";
        String uid = UserSpBusiness.getInstance().getUid();
        params.put("sub_time", time);
        params.put("uid", uid);
        if (!TextUtils.isEmpty(albumId)) {
            params.put("album_id", albumId);
        }
        String paramsUrl = Common.getSortString(params);
        String sign = Common.getCodeStr(paramsUrl).trim();
        RequestParams requestParams = new RequestParams();
        requestParams.put("sub_time", time);
        requestParams.put("uid", uid);
        requestParams.put("sign", sign);
        return requestParams;
    }
}
