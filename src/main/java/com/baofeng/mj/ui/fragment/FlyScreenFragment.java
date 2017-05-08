package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.DeviceInfo;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.Resource.PageItem;
import com.baofeng.mj.business.localbusiness.flyscreen.FlyScreenBusiness;
import com.baofeng.mj.business.localbusiness.flyscreen.interfaces.FlyScreenListener;
import com.baofeng.mj.business.localbusiness.flyscreen.util.FlyScreenConstant;
import com.baofeng.mj.business.localbusiness.flyscreen.util.FlyScreenUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.FlyDeviceListAdapter;
import com.baofeng.mj.ui.adapter.FlyVideoListAdapter;
import com.baofeng.mj.ui.view.FlyScreenGuideView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhaominglei on 2016/5/6.
 */

public class FlyScreenFragment extends BaseViewPagerFragment implements
        View.OnClickListener
        , FlyScreenListener, Handler.Callback {

    ListView listview_device, listview_folder;
    private FlyDeviceListAdapter flyDeviceListAdapter;
    private FlyVideoListAdapter flyVideoListAdapter;
    private List<DeviceInfo> deviceInfos = new ArrayList<DeviceInfo>();
    private List<PageItem> pageItems = new ArrayList<PageItem>();
    private RelativeLayout rl_content;
    private TextView fly_screen_name;
    private TextView tv_rescan;
    private TextView tv_common_problem;
    private ImageView iv_common_problem;
    private TextView tv_fly_screen_parent_dir;
    private RelativeLayout rl_fly_screen_loading;
    private RelativeLayout rl_fly_screen_not_found;
    private FlyScreenGuideView rl_fly_screen_guide;
    private Handler handler;

    public FlyScreenFragment() {
        super();
        handler = new Handler(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case FlyScreenConstant.FIND_FLY_SCREEN:
//                if(FlyScreenBusiness.getInstance().getFileDirStack().getSize() == 0)
                    dealDeviceFindResult();
                break;
            case FlyScreenConstant.GET_FILES_FROM_FLY_SCREEN:
                updateVideoList();
                break;
            case FlyScreenConstant.SHOW_PROGRESS_BAR:
                showLoadingProgressBar();
                break;
            case FlyScreenConstant.FLY_SCREEN_NOT_FOUND:
                hideLoadingProgressBar();
                setDeviceNotFoundVisibility(View.VISIBLE);
                break;
            case FlyScreenConstant.BACK_TO_DEVICE_LIST:
                backToDevice();
                break;
            case FlyScreenConstant.FLY_SCREEN_LOGIN_PWD_ERROR:
                if((getActivity() != null) && (getActivity().getResources() != null))
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.fly_screen_password_error), Toast.LENGTH_SHORT).show();
                break;
            case FlyScreenConstant.FLY_SCREEN_RECONNECT:
                if((getActivity() != null) && (getActivity().getResources() != null))
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.fly_screen_reconnect), Toast.LENGTH_SHORT).show();
                break;

            case FlyScreenConstant.FLY_SCREEN_SERVERCLOSED:
                if((getActivity() != null) && (getActivity().getResources() != null))
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.fly_screen_serverclosed), Toast.LENGTH_SHORT).show();
                    guideViewShow();
                break;
            case FlyScreenConstant.FLY_SCREEN_RESOURCE_FAIL:
                if((getActivity() != null) && (getActivity().getResources() != null))
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.fly_screen_resource_fail), Toast.LENGTH_SHORT).show();
                break;
            case FlyScreenConstant.FLY_SCREEN_RESOURCE_FAIL_NOTLOGIN:
                if((getActivity() != null) && (getActivity().getResources() != null))
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.fly_screen_resource_fail_notlogin), Toast.LENGTH_SHORT).show();
                break;
//            case FlyScreenConstant.GOTO_DEVICE:
//                FlyScreenBusiness.getInstance().requestLoginData(deviceInfos.get(msg.arg1));
//                break;
//            case FlyScreenConstant.GOTO_FOLDER:
//                FlyScreenBusiness.getInstance().handlePageItem(pageItems.get(msg.arg1), getActivity());
//                break;
            case FlyScreenConstant.FLY_SCREEN_NETWORK_ERROR:
                if((getActivity() != null) && (getActivity().getResources() != null)) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.fly_screen_network_error), Toast.LENGTH_SHORT).show();
                    hideLoadingProgressBar();
                    setDeviceNotFoundVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void clearMessages()
    {
//        handler.removeMessages(FlyScreenConstant.GOTO_DEVICE);
//        handler.removeMessages(FlyScreenConstant.GOTO_FOLDER);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frag_local_fly_screen, container, false);
            findViewByIds();
            init();
        }else{
            removeRootView();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        //removeRootView();
        super.onDestroyView();
    }

    private void findViewByIds() {
        rl_content = (RelativeLayout) rootView.findViewById(R.id.rl_content);
        fly_screen_name = (TextView) rootView.findViewById(R.id.tv_fly_screen_name);
        listview_device = (ListView) rootView.findViewById(R.id.listview_device);
        listview_folder = (ListView) rootView.findViewById(R.id.listview_folder);
        rl_fly_screen_guide = (FlyScreenGuideView) rootView.findViewById(R.id.rl_fly_screen_guide);
        rl_fly_screen_loading = (RelativeLayout) rootView.findViewById(R.id.rl_fly_screen_loading);
        rl_fly_screen_not_found = (RelativeLayout) rootView.findViewById(R.id.rl_fly_screen_not_found);
        tv_rescan = (TextView) rootView.findViewById(R.id.tv_rescan);
        tv_common_problem = (TextView) rootView.findViewById(R.id.tv_common_problem);
        iv_common_problem = (ImageView) rootView.findViewById(R.id.iv_common_problem);
        tv_fly_screen_parent_dir = (TextView) rootView.findViewById(R.id.tv_fly_screen_parent_dir);
    }

    private void init() {
        tv_rescan.setOnClickListener(this);
        tv_common_problem.setOnClickListener(this);
        iv_common_problem.setOnClickListener(this);
        tv_fly_screen_parent_dir.setOnClickListener(this);
        listview_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                handler.removeMessages(FlyScreenConstant.GOTO_DEVICE);
//                Message msg = Message.obtain();
//                msg.what = FlyScreenConstant.GOTO_DEVICE;
//                msg.arg1 = position;
//                handler.sendMessageDelayed(msg, 100);
                if (deviceInfos.size() > position)
                    FlyScreenBusiness.getInstance().requestLoginData(deviceInfos.get(position));
            }
        });
        listview_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                handler.removeMessages(FlyScreenConstant.GOTO_FOLDER);
//                Message msg = Message.obtain();
//                msg.what = FlyScreenConstant.GOTO_FOLDER;
//                msg.arg1 = position;
//                handler.sendMessageDelayed(msg, 100);
                if (pageItems.size() > position)
                    FlyScreenBusiness.getInstance().handlePageItem(pageItems.get(position), getActivity());
            }
        });
        listview_device.setAdapter(flyDeviceListAdapter = new FlyDeviceListAdapter(
                deviceInfos, getActivity(),
                R.layout.local_fragment_device_item));
        listview_folder.setAdapter(flyVideoListAdapter = new FlyVideoListAdapter(
                pageItems, getActivity(),this,
                R.layout.local_fragment_listview_item));
        //FlyScreenBusiness.getInstance().init(getActivity());
        FlyScreenBusiness.getInstance().init(BaseApplication.INSTANCE);
        FlyScreenBusiness.getInstance().setTcpReceiver(true);
        FlyScreenBusiness.getInstance().setFlyScreenListener(this);
    }

    @Override
    public void onClick(View v) {
        int resid = v.getId();
        if (resid == R.id.tv_rescan) {
            setDeviceNotFoundVisibility(View.GONE);
            FlyScreenBusiness.getInstance().startScan();
        } else if (resid == R.id.tv_common_problem
                || resid == R.id.iv_common_problem) {
            reportClick("airwiki");
            FlyScreenBusiness.getInstance().startFlyScreenHelpActivity(getActivity());
        } else if (resid == R.id.tv_fly_screen_parent_dir) {

            FlyScreenBusiness.getInstance().backToParentDir();
        }
    }

    /**
     * @author qiguolong @Date 2015-9-2 上午11:17:27
     * @description:{返回设备列表
     */

    private void backToDevice() {
        //设备列表显示
        listview_device.setVisibility(View.VISIBLE);
        //设备详细列表隐藏
        listview_folder.setVisibility(View.GONE);
        //返回上级隐藏
        tv_fly_screen_parent_dir.setVisibility(View.GONE);
        fly_screen_name.setText(BaseApplication.INSTANCE.getResources().getString(R.string.device_list));
    }


    private void refreshComplete() {
        hideLoadingProgressBar();
        rl_content.setVisibility(View.VISIBLE);
    }

    private void setDeviceNotFoundVisibility(int visibility) {
        if (rl_fly_screen_not_found.getVisibility() == View.GONE && visibility == View.VISIBLE) {
            rl_fly_screen_not_found.setVisibility(View.VISIBLE);
            rl_content.setVisibility(View.GONE);
        } else if (rl_fly_screen_not_found.getVisibility() == View.VISIBLE && visibility == View.GONE) {
            rl_fly_screen_not_found.setVisibility(View.GONE);
        }
    }

    /**
     * @author qiguolong @Date 2015-9-1 上午11:42:22
     * @description:{这里用一句话描述这个方法的作用
     */

    private void addDevices() {
        //List<DeviceInfo> udpdevices = FlyScreenBusiness.getInstance().getDeviceList();
        List<DeviceInfo> udpdevices = FlyScreenBusiness.getInstance().getmDeviceInfos();
        deviceInfos.clear();
        deviceInfos.addAll(udpdevices);
        //Common.removeDuplicateWithOrder(deviceInfos);
    }

    /**
     * @author qiguolong @Date 2015-9-1 上午11:24:44
     * @description:{这里用一句话描述这个方法的作用
     */

    private void dealDeviceFindResult() {
        addDevices();
        if (deviceInfos.size() > 0) {
        //if(FlyScreenBusiness.getInstance().getmDeviceInfos().size()>0){
            refreshComplete();
            setDeviceNotFoundVisibility(View.GONE);
            tv_fly_screen_parent_dir.setVisibility(View.GONE);
            listview_folder.setVisibility(View.GONE);
            listview_device.setVisibility(View.VISIBLE);
            fly_screen_name.setText(BaseApplication.INSTANCE.getResources().getString(R.string.device_list));
            flyDeviceListAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(isCurrentPage) {
            guideViewShow();
        }
    }
    private boolean isCurrentPage;
    public void setCurrentPage(boolean isCurrentPage){
        this.isCurrentPage = isCurrentPage;
    }

    public void guideViewShow() {
        resetView();
        if (FlyScreenBusiness.getInstance().isSkipGuide()) {
            rl_fly_screen_guide.setStepGuidEnd();
            FlyScreenBusiness.getInstance().startScan();
        } else {
            rl_fly_screen_guide.checkBeginStepGuid();
            rl_fly_screen_guide.showGuide();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /**
     * @author qiguolong @Date 2015-9-2 上午10:35:21
     * @description:{这里用一句话描述这个方法的作用
     */

    private void updateVideoList() {
        listview_folder.setVisibility(View.VISIBLE);
        tv_fly_screen_parent_dir.setVisibility(View.VISIBLE);
        listview_device.setVisibility(View.GONE);
        fly_screen_name.setText(FlyScreenBusiness.getInstance().getCurrentDevice().getName());
        flyVideoListAdapter.setDataList(pageItems);
        flyVideoListAdapter.notifyDataSetInvalidated();

        //((MainActivityGroup) getActivity()).detailUrl = "";
        //initHierarchy();
        FlyScreenUtil.saveSubtitleFile(pageItems);
    }


    //恢复到最开始页面
    private void resetView() {
        rl_content.setVisibility(View.GONE);
        listview_folder.setVisibility(View.GONE);
        tv_fly_screen_parent_dir.setVisibility(View.GONE);
        setDeviceNotFoundVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        FlyScreenBusiness.getInstance().onDestroy();
        clearMessages();
        deviceInfos.clear();
        super.onDestroy();
    }

    public boolean onKeyDown(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                FlyScreenBusiness.getInstance().backToParentDir();
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(int type) {
        handler.sendEmptyMessage(type);
    }

    @Override
    public void onDataReceived(int type, Object obj) {
        if (type == FlyScreenConstant.GET_FILES_FROM_FLY_SCREEN) {
            if (obj instanceof Collection) {
                pageItems = (List<PageItem>) obj;
                handler.sendEmptyMessage(FlyScreenConstant.GET_FILES_FROM_FLY_SCREEN);
            }
        }
    }

    private void showLoadingProgressBar() {
        rl_fly_screen_loading.setVisibility(View.VISIBLE);
    }

    private void hideLoadingProgressBar() {
        rl_fly_screen_loading.setVisibility(View.GONE);
    }



    //click 报数
    private void reportClick(String airvideohelp){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("chooseitem");
        bean.setTpos("1");
        bean.setPagetype("airvideo");
        bean.setLocal_menu_id("3");
        bean.setAirevideohelp(airvideohelp);
        ReportBusiness.getInstance().reportClick(bean);
    }

}