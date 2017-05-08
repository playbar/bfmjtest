package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.KeyWordBean;
import com.baofeng.mj.bean.KeyWordListBean;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.bean.SearchResultBean;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SearchSpBusiness;
import com.baofeng.mj.ui.adapter.SearchKeyAdapter;
import com.baofeng.mj.ui.adapter.SearchViewpagerAdapter;
import com.baofeng.mj.ui.dialog.DeleteSearchHisDialog;
import com.baofeng.mj.ui.fragment.BaseFragment;
import com.baofeng.mj.ui.fragment.SearchResultFragment;
import com.baofeng.mj.ui.view.CustomViewGroup;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.SearchApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyang on 2016/9/18.
 * 全局搜索界面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener, SearchKeyAdapter.itemClickInterface, DeleteSearchHisDialog.CancleBusiness {
    private CustomViewGroup customViewGroup;//搜索历史
    private SearchKeyAdapter searchKeyAdapter;//热搜adapter
    private ListView hot_list;//热搜列表
    private TextView cancel;//取消按钮
    private EditText key_word;//搜索输入框
    private TextView his_title, his_clear, divider_line1, divider_view;//搜索历史隐藏相关
    private RadioButton video_tab, game_tab;//选项
    private LinearLayout search_title;//搜索选项布局
    private RadioGroup search_radiogroup;//搜索结果选项卡
    private ViewPager search_viewpager;//搜索切换viewpager
    private RelativeLayout key_layout;//搜索关键字布局
    private SearchViewpagerAdapter searchViewpagerAdapter;//Viewpager Adapter
    private ArrayList<BaseFragment> fragmentArrayList = new ArrayList<BaseFragment>();
//    private ImageView clear_edit;
    private String channelId;
    private String frompage;//来源哪个页面
    private String searchType;//搜索类型
    private String searchKeywords;//搜索关键词
    private String search_rst_sp;//视频的，0无搜索结果，1相反
    private String search_rst_yy;//游戏的，0无搜索结果，1相反

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        if (intent != null) {
            channelId = intent.getStringExtra("res_id");
            frompage = intent.getStringExtra("frompage");
        }
        find();
        init();
        hideorShowHIs();
        loadKeyWordData();
        reportSearchPv();//上报搜索页pv
    }

    /**
     * 控件寻址
     */
    private void find() {
        hot_list = (ListView) findViewById(R.id.hot_list);
        cancel = (TextView) findViewById(R.id.cancel);
        key_word = (EditText) findViewById(R.id.key_word);
        key_layout = (RelativeLayout) findViewById(R.id.key_layout);
        his_title = (TextView) findViewById(R.id.his_title);
        his_clear = (TextView) findViewById(R.id.his_clear);
        divider_line1 = (TextView) findViewById(R.id.divider_line1);
        customViewGroup = (CustomViewGroup) findViewById(R.id.his_group);
        divider_view = (TextView) findViewById(R.id.divider_view);
        video_tab = (RadioButton) findViewById(R.id.video_tab);
        game_tab = (RadioButton) findViewById(R.id.game_tab);
        search_title = (LinearLayout) findViewById(R.id.search_title);
        search_radiogroup = (RadioGroup) findViewById(R.id.search_radiogroup);
        search_viewpager = (ViewPager) findViewById(R.id.search_viewpager);
//        clear_edit = (ImageView) findViewById(R.id.clear_edit);
//        clear_edit.setOnClickListener(this);
    }

    /**
     * 初始化以及点击事件
     */
    private void init() {
        customViewGroup.setSpacing(PixelsUtil.dip2px(6.67f), PixelsUtil.dip2px(10));
        cancel.setOnClickListener(this);
        his_clear.setOnClickListener(this);
        key_layout.setVisibility(View.VISIBLE);
        search_title.setVisibility(View.VISIBLE);
        search_radiogroup.setVisibility(View.GONE);
        search_viewpager.setVisibility(View.GONE);
        key_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = key_word.getText().toString().trim().length();
                if (len > 0) {
                    cancel.setText("搜索");
                } else {
                    cancel.setText("取消");
                    if (len == 0) {
                        initHis();
                    }
                    search_radiogroup.clearCheck();
                    search_radiogroup.setVisibility(View.GONE);
                    search_viewpager.setVisibility(View.GONE);
                    key_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        key_word.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String his_key = key_word.getText().toString().replace("\n", "").trim();
                key_word.setText(his_key);
                if (!TextUtils.isEmpty(his_key)) {
                    searchType = "search_button";
                    sendKeyForSearch(his_key.replaceAll("\\s{2,}", " "));
                }
                return false;
            }
        });
        search_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    video_tab.setChecked(true);
                } else if (i == 1) {
                    game_tab.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        video_tab.setOnClickListener(this);
        game_tab.setOnClickListener(this);
    }

    /**
     * 请求网络数据,获取热搜词
     */
    private void loadKeyWordData() {
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(this, "当前网络已断开，请检查网络设置!", Toast.LENGTH_SHORT).show();
            return;
        }
        new SearchApi().getKeyWords(this, ConfigUrl.getSearchKeyWord(), new ApiCallBack<KeyWordBean>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onSuccess(KeyWordBean result) {
                if (result != null) {
                    resolveKeyWords(result);
                }
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
     * 处理热搜词数据
     *
     * @param result
     */
    private void resolveKeyWords(KeyWordBean result) {
        if (result.getStatus() == 0) {
            bindView(result.getData());
        }
    }

    /**
     * 填充热搜词
     *
     * @param keyWordListBean
     */
    private void bindView(KeyWordListBean keyWordListBean) {
        if (keyWordListBean != null) {
            if (keyWordListBean.getTotal() == 0) {

            } else {
                searchKeyAdapter = new SearchKeyAdapter(this, keyWordListBean, this);
                hot_list.setAdapter(searchKeyAdapter);
            }
        }
    }

    /**
     * 填充搜索历史
     */
    private void initHis() {
        String hisStr = SearchSpBusiness.getInstance().getSearchHis();
        customViewGroup.removeAllViews();
        if (!TextUtils.isEmpty(hisStr.trim())) {
            try {
                org.json.JSONArray hisArray = new org.json.JSONArray(hisStr);
                int hisLen = hisArray.length();
                while (hisLen > 0) {
                    TextView textView = null;
                    textView = new TextView(this);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.3f);
                    textView.setTextColor(getResources().getColor(R.color.content_color));
                    textView.setPadding(PixelsUtil.dip2px(10), 0, PixelsUtil.dip2px(10), 0);
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PixelsUtil.dip2px(26.7f)));
                    textView.setGravity(Gravity.CENTER_VERTICAL);
                    textView.setBackground(getResources().getDrawable(R.drawable.corner_search_his));
                    textView.setText(hisArray.get(hisLen - 1).toString());
                    final String hisKey = hisArray.get(hisLen - 1).toString().trim();
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            key_word.setText(hisKey);
                            searchType = "search_history";
                            sendKeyForSearch(hisKey);
                        }
                    });
                    customViewGroup.addView(textView);
                    hisLen--;
                }
                his_title.setVisibility(View.VISIBLE);
                his_clear.setVisibility(View.VISIBLE);
                customViewGroup.setVisibility(View.VISIBLE);
                divider_line1.setVisibility(View.VISIBLE);
                divider_view.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求搜索结果列表
     */
    private void sendKeyForSearch(String keyWord) {
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(this, "当前网络已断开，请检查网络设置后重新搜索!", Toast.LENGTH_SHORT).show();
            return;
        }
        saveHis();
        String keyStr=keyWord.replace("%", " ");
        searchKeywords = keyStr.replace("#", " ").trim();
        new SearchApi().getSearchResult(this, ConfigUrl.getSearchListUrl(getUrlTail(keyStr, channelId, 0, ConfigConstant.pageCount12)), new ApiCallBack<SearchResultBean>() {
            @Override
            public void onSuccess(SearchResultBean result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result.toString().trim())) {

                } else {
                    resolveResult(result);
                }
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

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
        reportClick();
    }

    /**
     * 处理搜索列表结果
     *
     * @param searchResultBean
     */
    private void resolveResult(SearchResultBean searchResultBean) {
        key_layout.setVisibility(View.GONE);
        search_radiogroup.setVisibility(View.VISIBLE);
        search_viewpager.setVisibility(View.VISIBLE);
        fragmentArrayList.clear();
        if (searchResultBean.getData() == null || searchResultBean.getData().getList() == null) {
            return;
        }
        List<MainSubContentListBean<List<ContentInfo>>> mainSubContentBeanList = searchResultBean.getData().getList();
        int count = mainSubContentBeanList.size();
        for (int i = 0; i < count; i++) {
            MainSubContentListBean mainSubContentListBean = mainSubContentBeanList.get(i);
            fragmentArrayList.add(new SearchResultFragment().initThis(this, key_word.getText().toString().replace("\n", "").trim(), searchResultBean.getChannel(), mainSubContentListBean));

            List<ContentInfo> contentInfoList = (List<ContentInfo>) mainSubContentListBean.getList();
            if(contentInfoList != null && contentInfoList.size() > 0){
                int object_type = mainSubContentListBean.getObject_type();
                int has_more = mainSubContentListBean.getHas_more();
                if(has_more == 0){//没有搜索结果
                    if(object_type == 1){//视频
                        search_rst_sp = "0";
                    }else if(object_type == 2){//应用
                        search_rst_yy = "0";
                    }
                }else{//有搜索结果
                    if(object_type == 1){//视频
                        search_rst_sp = "1";
                    }else if(object_type == 2){//应用
                        search_rst_yy = "1";
                    }
                }
            }
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        searchViewpagerAdapter = new SearchViewpagerAdapter(fragmentManager, fragmentArrayList);
        search_viewpager.setAdapter(searchViewpagerAdapter);
        searchViewpagerAdapter.notifyDataSetChanged();
        if (mainSubContentBeanList.size() >= 2) {
            if (mainSubContentBeanList.get(0).getHas_more() == 0) {
                video_tab.setText(mainSubContentBeanList.get(0).getTitle() + "(" + 0 + ")");
            } else {
                video_tab.setText(mainSubContentBeanList.get(0).getTitle() + "(" + mainSubContentBeanList.get(0).getTotal() + ")");
            }
            if (mainSubContentBeanList.get(1).getHas_more() == 0) {
                game_tab.setText(mainSubContentBeanList.get(1).getTitle() + "(" + 0 + ")");
            } else {
                game_tab.setText(mainSubContentBeanList.get(1).getTitle() + "(" + mainSubContentBeanList.get(1).getTotal() + ")");
            }
            if (mainSubContentBeanList.get(0).getLightshow() == 1) {
                video_tab.setChecked(true);
                search_viewpager.setCurrentItem(0);
            }
            if (mainSubContentBeanList.get(1).getLightshow() == 1) {
                game_tab.setChecked(true);
                search_viewpager.setCurrentItem(1);
            }
        }
        searchViewpagerAdapter.notifyDataSetChanged();
        reportSearchResultPv();//上报搜索结果页pv
    }

    /**
     * 是否隐藏搜索历史
     */

    private void hideorShowHIs() {
        if (TextUtils.isEmpty(SearchSpBusiness.getInstance().getSearchHis().trim())) {
            his_title.setVisibility(View.GONE);
            his_clear.setVisibility(View.GONE);
            customViewGroup.setVisibility(View.GONE);
            divider_line1.setVisibility(View.GONE);
            divider_view.setVisibility(View.GONE);
        } else {
            initHis();
            his_title.setVisibility(View.VISIBLE);
            his_clear.setVisibility(View.VISIBLE);
            customViewGroup.setVisibility(View.VISIBLE);
            divider_line1.setVisibility(View.VISIBLE);
            divider_view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 保存搜索历史
     */
    private void saveHis() {
        String hisStr = SearchSpBusiness.getInstance().getSearchHis();
        try {
            org.json.JSONArray hisArray;
            if (!TextUtils.isEmpty(hisStr)) {
                hisArray = new org.json.JSONArray(hisStr);
                if (hisArray.length() == 10) {
                    boolean hasSame = false;
                    for (int i = 0; i < hisArray.length(); i++) {
                        if (key_word.getText().toString().trim().equals(hisArray.get(i))) {
                            hasSame = true;
                            hisArray.remove(i);
                            hisArray.put(hisArray.length(), key_word.getText().toString().trim().replaceAll("\\s{2,}", " "));
                            break;
                        }
                    }
                    if (!hasSame) {
                        hisArray.remove(0);
                        hisArray.put(hisArray.length(),key_word.getText().toString().trim().replaceAll("\\s{2,}", " "));
                    }
                } else {
                    for (int i = 0; i < hisArray.length(); i++) {
                        if (key_word.getText().toString().trim().replaceAll("\\s{2,}", " ").equals(hisArray.get(i))) {
                            hisArray.remove(i);
                            break;
                        }
                    }
                    hisArray.put(hisArray.length(), key_word.getText().toString().trim().replaceAll("\\s{2,}", " "));
                }
            } else {
                hisArray = new org.json.JSONArray();
                hisArray.put(0, key_word.getText().toString().trim().replaceAll("\\s{2,}", " "));
            }
            SearchSpBusiness.getInstance().saveSearchHis(hisArray.toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            if (((TextView) v).getText().equals("取消")) {//如果是取消,则结束当前界面
                key_word.setText("");
                finish();
            } else {//如果是搜索则请求网络数据
                searchType = "search_button";
                sendKeyForSearch(key_word.getText().toString().trim().replaceAll("\\s{2,}", " "));
            }
        } else if (id == R.id.video_tab) {
            search_viewpager.setCurrentItem(0);
        } else if (id == R.id.game_tab) {
            search_viewpager.setCurrentItem(1);
//        }else if (id == R.id.clear_edit) {
//            search_radiogroup.clearCheck();
//            key_word.setText("");
//            initHis();
        } else if (id == R.id.his_clear) {
            new DeleteSearchHisDialog().showDialog(this, this);
        }
    }

    /**
     * 获取搜索列表接口
     *
     * @param key
     * @param channel
     * @param startNum
     * @param pageNum
     * @return
     */
    private String getUrlTail(String key, String channel, int startNum, int pageNum) {
        return key + "-" + "channel" + channel + "-" + "start" + startNum + "-" + "num" + pageNum + ".js";
    }

    @Override
    public void itemClick(String keyWord) {
        key_word.setText(keyWord);
        searchType = "search_hot";
        sendKeyForSearch(keyWord);
    }

    @Override
    public void cancleCallBack() {
        SearchSpBusiness.getInstance().saveSearchHis("");
        his_title.setVisibility(View.GONE);
        his_clear.setVisibility(View.GONE);
        customViewGroup.setVisibility(View.GONE);
        divider_line1.setVisibility(View.GONE);
        divider_view.setVisibility(View.GONE);
    }

    /**
     * 上报click
     */
    private void reportClick(){
        ReportClickBean reportClickBean = new ReportClickBean();
        reportClickBean.setEtype("click");
        reportClickBean.setTpos("1");
        reportClickBean.setPagetype("search");
        reportClickBean.setClicktype("search");
        reportClickBean.setSearch_type(searchType);
        reportClickBean.setSearch_keywords(searchKeywords);
        ReportBusiness.getInstance().reportClick(reportClickBean);
    }

    /**
     * 上报搜索页pv
     */
    private void reportSearchPv(){
        ReportPVBean reportPVBean = new ReportPVBean();
        reportPVBean.setEtype("pv");
        reportPVBean.setTpos("1");
        reportPVBean.setPagetype("search");
        reportPVBean.setFrompage(frompage);
        ReportBusiness.getInstance().reportPV(reportPVBean);
    }

    /**
     * 上报搜索结果页pv
     */
    private void reportSearchResultPv(){
        ReportPVBean reportPVBean = new ReportPVBean();
        reportPVBean.setEtype("pv");
        reportPVBean.setTpos("1");
        reportPVBean.setPagetype("search_result");
        reportPVBean.setSearch_type(searchType);
        reportPVBean.setSearch_keywords(searchKeywords);
        reportPVBean.setSearch_rst_sp(search_rst_sp);
        reportPVBean.setSearch_rst_yy(search_rst_yy);
        ReportBusiness.getInstance().reportPV(reportPVBean);
    }

    /**
     * 上报游戏点击
     */
    public void reportGameClick(String resId, String title, String clicktype){
        ReportClickBean reportClickBean = new ReportClickBean();
        reportClickBean.setEtype("click");
        reportClickBean.setTpos("1");
        reportClickBean.setPagetype("search_result");
        reportClickBean.setClicktype(clicktype);
        reportClickBean.setTitle(title);
        reportClickBean.setSearch_type(searchType);
        reportClickBean.setSearch_keywords(searchKeywords);
        reportClickBean.setSearch_rst_sp(search_rst_sp);
        reportClickBean.setSearch_rst_yy(search_rst_yy);
        reportClickBean.setGameid(resId);
        ReportBusiness.getInstance().reportClick(reportClickBean);
    }

    /**
     * 上报视频点击
     */
    public void reportVideoClick(ContentInfo contentInfo){
        ReportClickBean reportClickBean = new ReportClickBean();
        reportClickBean.setEtype("click");
        reportClickBean.setTpos("1");
        reportClickBean.setPagetype("search_result");
        reportClickBean.setClicktype("jump");
        reportClickBean.setTitle(contentInfo.getTitle());
        reportClickBean.setSearch_type(searchType);
        reportClickBean.setSearch_keywords(searchKeywords);
        reportClickBean.setSearch_rst_sp(search_rst_sp);
        reportClickBean.setSearch_rst_yy(search_rst_yy);
        int resType = contentInfo.getType();//资源类型
        if(ResTypeUtil.res_type_video == resType){//全景视频
            reportClickBean.setVideoid(contentInfo.getRes_id());//资源id
            reportClickBean.setTypeid(String.valueOf(resType));//资源类型
        }else if(ResTypeUtil.res_type_movie == resType){//影视
            reportClickBean.setMovieid(contentInfo.getRes_id());//资源id
            reportClickBean.setMovietypeid(String.valueOf(contentInfo.getCategory_type()));//影视分类
        }
        ReportBusiness.getInstance().reportClick(reportClickBean);
    }
}
