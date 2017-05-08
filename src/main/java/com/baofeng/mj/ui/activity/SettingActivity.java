package com.baofeng.mj.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppUpdateBean;
import com.baofeng.mj.bean.DirFile;
import com.baofeng.mj.business.accountbusiness.AppUpdateBusiness;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.dialog.AppUpdateDialog;
import com.baofeng.mj.ui.dialog.ClearCacheDialog;
import com.baofeng.mj.ui.dialog.CopyFileDialog;
import com.baofeng.mj.ui.dialog.JoyStickCheckDialog;
import com.baofeng.mj.ui.dialog.LogoutDialog;
import com.baofeng.mj.ui.popwindows.DownloadPathSelectPop;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.updateutil.APKDownloadUtils;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.storm.smart.common.utils.LogHelper;

import java.util.HashMap;
import java.util.List;

/**
 * 设置页面
 * Created by muyu on 2016/5/11.
 */
public class SettingActivity extends BaseStickActivity implements View.OnClickListener {
    private LinearLayout parent;
    private AppTitleBackView appTitleLayout;
    private RelativeLayout profileLayout;
    private RelativeLayout wifiLayout;
    private RelativeLayout pathLayout;
    private RelativeLayout clearCacheLayout;
    private RelativeLayout updateLayout;
    private RelativeLayout aboutLayout;
    private RelativeLayout setting_contact_layout;//联系方式
    private TextView tv_download_path;
    private TextView update_warn;//小红点
    private TextView tv_cache_size, is_gprs, check_tag, custom_info_tag;
    private AppUpdateDialog appUpdateDialog;//app更新对话框
    private CopyFileDialog copyFileDialog;//拷贝文件对话框
    private ClearCacheDialog clearCacheDialog;//清除缓存对话框
    private DownloadPathSelectPop downloadPathSelectPop;//下载路径选择pop
    private LogoutDialog mLogoutDialog;
    private List<DirFile> dirFileList;
    private Button exit_btn;
    private View pro_line;
    private RelativeLayout setting_res_layout;//手机屏幕尺寸选择
    private TextView tv_res;

    private RelativeLayout modeLayout; //手柄控制模式
    private TextView modeTV;

    private TextView playerModelDes; //选择的播放模式描述
    private TextView joyStickName; //手柄校验栏的 手柄名称
    private AppUpdateBusiness mBussiness;
    private RelativeLayout mVrSettingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mBussiness = new AppUpdateBusiness();
        mBussiness.checkUpdate(this, false, false, false);
        initView();
    }

    private void initView() {
        parent = (LinearLayout) findViewById(R.id.parent);
        appTitleLayout = (AppTitleBackView) findViewById(R.id.setting_title_layout);
        appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_SETTING"));
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        profileLayout = (RelativeLayout) findViewById(R.id.setting_profile_layout);
        wifiLayout = (RelativeLayout) findViewById(R.id.setting_wifi_layout);
        pathLayout = (RelativeLayout) findViewById(R.id.setting_path_layout);
        clearCacheLayout = (RelativeLayout) findViewById(R.id.setting_clear_cache_layout);
        updateLayout = (RelativeLayout) findViewById(R.id.setting_update_layout);
        aboutLayout = (RelativeLayout) findViewById(R.id.setting_about_layout);
        aboutLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(SettingSpBusiness.getInstance().getPhoneInfo());
                return true;
            }
        });
        setting_contact_layout = (RelativeLayout) findViewById(R.id.setting_contact_layout);
        setting_contact_layout.setOnClickListener(this);
        setting_res_layout = (RelativeLayout) findViewById(R.id.setting_res_layout);
        tv_res = (TextView) findViewById(R.id.tv_res);
        tv_res.setText(getSize() + "英寸");
        tv_download_path = (TextView) findViewById(R.id.tv_download_path);
        tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
        check_tag = (TextView) findViewById(R.id.check_tag);
        check_tag.setText(LanguageValue.getInstance().getValue(this, "SID_CHECK_UPGRADE"));
        pro_line = (View) findViewById(R.id.pro_line);
        is_gprs = (TextView) findViewById(R.id.is_gprs);
        exit_btn = (Button) findViewById(R.id.exit_btn);
        custom_info_tag = (TextView) findViewById(R.id.custom_info_tag);
        custom_info_tag.setText(LanguageValue.getInstance().getValue(this, "SID_PROFILE"));
        update_warn = (TextView) findViewById(R.id.update_warn);
        if (UserSpBusiness.getInstance().isUserLogin()) {
            exit_btn.setVisibility(View.VISIBLE);
            exit_btn.setOnClickListener(this);
            profileLayout.setOnClickListener(this);
            profileLayout.setVisibility(View.VISIBLE);
            pro_line.setVisibility(View.VISIBLE);
        } else {
            exit_btn.setVisibility(View.GONE);
            profileLayout.setVisibility(View.GONE);
            pro_line.setVisibility(View.GONE);
        }
        wifiLayout.setOnClickListener(this);
        pathLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        updateLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        setting_res_layout.setOnClickListener(this);
        is_gprs.setOnClickListener(this);

        if (SettingSpBusiness.getInstance().getCanGPRSDownload()) {
            is_gprs.setBackgroundResource(R.drawable.my_setting_switchon);
        } else {
            is_gprs.setBackgroundResource(R.drawable.my_setting_switchoff);
        }
        tv_cache_size.setText(FileSizeUtil.getAppCacheSize());

        modeLayout = (RelativeLayout) findViewById(R.id.setting_mode_layout);
        modeLayout.setOnClickListener(this);

        modeTV = (TextView) findViewById(R.id.tv_mode);

        RelativeLayout palyerTypeLayout = (RelativeLayout) findViewById(R.id.setting_player_type);
        palyerTypeLayout.setOnClickListener(this);
        playerModelDes = (TextView)findViewById(R.id.player_model_des);

        RelativeLayout joystickCheckLayout = (RelativeLayout) findViewById(R.id.setting_joystick_check);
        joystickCheckLayout.setOnClickListener(this);
        joyStickName = (TextView) findViewById(R.id.joystick_name);

        mVrSettingLayout = (RelativeLayout) findViewById(R.id.vr_setting);
        mVrSettingLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.setting_profile_layout) {
            startActivity(new Intent(this, CustomInfoActivity.class));
        } else if (i == R.id.setting_wifi_layout) {

        } else if (i == R.id.setting_path_layout) {//显示下载路径选择pop
            showDownloadPathSelectPop();
        } else if (i == R.id.setting_clear_cache_layout) {//清除app缓存
            cleanAppCache();
        } else if (i == R.id.setting_update_layout) {//检查更新
            AppUpdateBean bean = mBussiness.getAppUpdateBean();
            LogHelper.e("infosss","bean=="+bean+"====appupdatBean=="+BaseApplication.INSTANCE.getAppUpdateBusiness().getAppUpdateBean());
            if(null == bean){
                mBussiness.setAppUpdateBean(BaseApplication.INSTANCE.getAppUpdateBusiness().getAppUpdateBean());
            }
            mBussiness.getAppUpdateDialog(this).showDialog(this,SettingSpBusiness.getInstance().getNeedUpdate());
        } else if (i == R.id.setting_about_layout) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (i == R.id.is_gprs) {
            if (SettingSpBusiness.getInstance().getCanGPRSDownload()) {
                is_gprs.setBackgroundResource(R.drawable.my_setting_switchoff);
                SettingSpBusiness.getInstance().setCanGPRSDownload(false);
            } else {
                is_gprs.setBackgroundResource(R.drawable.my_setting_switchon);
                SettingSpBusiness.getInstance().setCanGPRSDownload(true);
            }
        } else if (i == R.id.exit_btn) {
            if (mLogoutDialog == null) {
                mLogoutDialog = new LogoutDialog(this, new LogoutDialog.LogoutCallBack() {
                    @Override
                    public void onConfirm() {
                        if (UserSpBusiness.getInstance().isUserLogin()) {
                            UserSpBusiness.getInstance().clearUserInfo();
                            exit_btn.setVisibility(View.GONE);
                            profileLayout.setVisibility(View.GONE);
                            pro_line.setVisibility(View.GONE);
                            //退出登录，删除在线临时播放记录
                            HistoryBusiness.deleteAllCinemaTempHistory();
                        }
                    }
                });
            }
            mLogoutDialog.show();

        } else if (R.id.setting_contact_layout == i) {
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
        } else if (R.id.setting_res_layout == i) {
            Intent intent = new Intent(this, ResolSettingActivity.class);
            if (TextUtils.isEmpty(getSize())) {
                intent.putExtra("size", "");
            } else {
                intent.putExtra("size", getSize());
            }
            startActivity(intent);
        } else if(R.id.setting_mode_layout == i){
            startActivity(new Intent(this, ControlModeActivity.class));
        } else if(R.id.setting_player_type == i){ //播放操控模式选择  add by whf 20161122
            startActivity(new Intent(this,PlayerTypeSelectionActivity.class));
        }else if(R.id.setting_joystick_check==i){ //手柄按键校验 add by whf 20170223
            reportClick();
            if (!StickUtil.blutoothEnble()||!StickUtil.isBondBluetooth()||!StickUtil.isConnected){ //未连接状态
                  new JoyStickCheckDialog(this, new JoyStickCheckDialog.DialogCallBack() {
                      @Override
                      public void onConfirm() {//确认
                          startActivity(new Intent(SettingActivity.this, HelpActivity.class));
                      }
                  }).show();
            }else{//连接状态
               startActivity(new Intent(this,JoystickCheckActivity.class));
            }
        }else if(R.id.vr_setting == i){//VR设置
            startActivity(new Intent(this,VrSettingActivity.class));
        }
    }

    /**
     * 清除app缓存
     */
    private void cleanAppCache() {
        if (clearCacheDialog == null) {
            clearCacheDialog = new ClearCacheDialog(this);
        }
        clearCacheDialog.showDialog(new ClearCacheDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {//清除
                FileCommonUtil.cleanAppCacheFromSetting(tv_cache_size);
            }
        });
        SettingSpBusiness.getInstance().clearTabInfo();
    }

    /**
     * 显示下载路径选择pop
     */
    private void showDownloadPathSelectPop() {
        //每次从新获取当前读到的所有路径，保持路径列表是最新的
//        if (dirFileList == null) {
            dirFileList = FileStorageUtil.getAllDownloadDirFile();
//        }
//        if(dirFileList.size() == 0){
//            return;//暂无外部存储卡
//        }else if(dirFileList.size() == 1){
//            Toast.makeText(SettingActivity.this, "暂无外部SD存储卡", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (copyFileDialog == null) {
            copyFileDialog = new CopyFileDialog(this);
        }
        if (BaseApplication.INSTANCE.getDownloadingList().size() > 0 || APKDownloadUtils.isDownloading) {
            copyFileDialog.showDialog(true, false, 3000);//提示资源正在下载，或者魔镜app正在下载
            return;
        }
        if (downloadPathSelectPop == null) {
            downloadPathSelectPop = new DownloadPathSelectPop(this, dirFileList);
            downloadPathSelectPop.setCopyFileDialog(copyFileDialog);
        }
        downloadPathSelectPop.showPop(parent, Gravity.BOTTOM, 0, 0, new CopyFileDialog.CopyFileCallback() {
            @Override
            public void callback(boolean copyResult) {
                if (copyResult) {//拷贝文件成功
                    refreshDownloadPath();//刷新存储路径
                } else {
                    Toast.makeText(SettingActivity.this, "复制失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tv_res == null) {
            tv_res = (TextView) findViewById(R.id.tv_res);
        }
        if (TextUtils.isEmpty(getSize())) {
            tv_res.setText("点击设置");
        } else {
            tv_res.setText(getSize() + "英寸");
        }
        if (SettingSpBusiness.getInstance().getNeedUpdate()) {
            update_warn.setVisibility(View.VISIBLE);
        } else {
            update_warn.setVisibility(View.GONE);
        }
        refreshDownloadPath();//刷新存储路径

        if(SettingSpBusiness.getInstance().getControlMode() == 1 ){
            modeTV.setText(getResources().getString(R.string.guide_control_mode_pure));
        } else {
            modeTV.setText(getResources().getString(R.string.guide_control_mode_mix));
        }

        int type = SettingSpBusiness.getInstance().getPlayerMode();
        if(type==0){
            playerModelDes.setText(getString(R.string.player_mode_normal));
        }else if(type==1){
            playerModelDes.setText(getString(R.string.player_mode_vr));
        }else {
            playerModelDes.setText("");
        }
      updateJoystickConnect();
    }

    /**
     * 刷新存储路径
     */
    private void refreshDownloadPath() {
        int storageMode = FileStorageUtil.getStorageMode();
        if (storageMode == 0) {
            tv_download_path.setText("内部存储卡");
        } else if (storageMode == 1) {
            tv_download_path.setText("外部SD存储卡");
        } else {
            tv_download_path.setText("外部SD存储卡" + storageMode);
        }
    }

    /**
     * 获取用户选择的尺寸
     *
     * @return
     */
    private String getSize() {
        return getSharedPreferences("size", MODE_PRIVATE).getString("size", "");
    }

    @Override
    public void startCheck() {
        updateJoystickConnect();
    }


    private void updateJoystickConnect(){
        if(joyStickName==null)
            return;
        if (!StickUtil.blutoothEnble()||!StickUtil.isBondBluetooth()||!StickUtil.isConnected) {// 未连接
            joyStickName.setText(getString(R.string.josytick_unconnect));
        } else {// 已连接
            String name = BaseApplication.INSTANCE.getJoystickName();
            if(!TextUtils.isEmpty(name)&&name.contains("_")){
                name = name.substring(0,name.lastIndexOf("_"));
            }
            joyStickName.setText(name);
        }
    }
    //点击手柄校验报数
    private void reportClick(){
        HashMap<String,String> map = new HashMap<>();
        map.put("etype","click");
        map.put("clicktype","chooseitem");
        map.put("tpos","1");
        map.put("pagetype","controlcheck");
        ReportBusiness.getInstance().reportClick(map);
    }
}
