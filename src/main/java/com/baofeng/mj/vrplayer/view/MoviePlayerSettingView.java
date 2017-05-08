package com.baofeng.mj.vrplayer.view;

import android.content.Context;

import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.vrplayer.interfaces.IPlayerSettingCallBack;
import com.baofeng.mj.vrplayer.interfaces.IViewVisiableListener;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.SoundUtils;
import com.bfmj.viewcore.view.GLRelativeView;

/**
 * Created by wanghongfang on 2016/7/15.
 * 播放功能设置
 */
public class MoviePlayerSettingView extends GLRelativeView {

    public static final int MOVIE = 0;
    public static final int LOCAL_MOVIE = 1;
    public static final int PANO = 2;
    public static final int LOCAL_PANO = 3;
    private int mType = 0;

    private Context mContext;

    private MovieSettingView movieSettingView;

    private HDTypeView hdTypeView;

    private SoundBarView soundBarView;

    private SelectSourceView selectSourceView;

    public MoviePlayerSettingView(Context context) {
        super(context);
        mContext = context;
        setLayoutParams(1000,1000);
        setHandleFocus(false);
//        setBackground(new GLColor(0xff0000));
    }

    public void initView(int type) {
        mType = type;

        //创建在线影院选择资源菜单
        createSelectSourceView();
        //创建在线影院音量调节菜单
        createSoundBarView();
        //创建在线影院清晰度菜单
        createHDTypeView();
        //创建在线影院设置菜单
        createMovieSettingView();

//        setDepth(GLConst.Player_Settings_Depth,GLConst.Player_Settings_Scale);
    }

    private void createSelectSourceView() {
        selectSourceView = new SelectSourceView(mContext);
        selectSourceView.setVisible(false);
//        selectSourceView.setDepth(GLConst.Player_Settings_Depth,GLConst.Player_Settings_Scale);
        addViewBottom(selectSourceView);
    }

    public void setMovieVideoDatas(VideoDetailBean videosBean,int index) {
        selectSourceView.setMovieVideoDatas(videosBean,index);
    }

    public void setCurrentNum(int index) {
        selectSourceView.setCurrentNum(index);
    }

    public void setSelectSourceViewShow(boolean isShow) {
        if(isShow) {
            selectSourceView.setVisible(true);
        } else {
            selectSourceView.setVisible(false);
        }
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(isShow);
        }
    }

    private void createSoundBarView() {
        soundBarView = new SoundBarView(mContext);
        soundBarView.setVisible(false);
        addViewBottom(soundBarView);
    }

    public void setSoundBarViewShow(boolean isShow) {
        if(isShow) {
            soundBarView.setVisible(true);
        } else {
            soundBarView.setVisible(false);
        }
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(isShow);
        }
    }

    public void setSoundIcon(boolean flag) {
        soundBarView.setSoundIcon(flag);
        if(flag){
            soundBarView.setVolume(SettingSpBusiness.getInstance().getPlayerSoundValue());
        }else {
            soundBarView.setVolume(0);
        }
    }

    public void setVolume(int vm) {
        soundBarView.setVolume(vm);
        soundBarView.setSoundIcon(vm>0);
        SettingSpBusiness.getInstance().setPlayerSoundValue(vm);
        SettingSpBusiness.getInstance().setPlayerSoundMute(!(vm>0));
    }

    private void createHDTypeView() {
        hdTypeView = new HDTypeView(mContext);
        hdTypeView.setVisible(false);
        addViewBottom(hdTypeView);
    }
    public void setHDdata(String[] strs,String defaultHD) {
        hdTypeView.initView(strs);
        hdTypeView.setSelectedHD(defaultHD);
    }

    public void setSelectedHD(String hdtype){
        hdTypeView.setSelectedHD(hdtype);
    }

    public void setHDTypeViewShow(boolean isShow) {
        if(isShow) {
            hdTypeView.setVisible(true);
        } else {
            hdTypeView.setVisible(false);
        }
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(isShow);
        }
    }

    private void createMovieSettingView() {
        movieSettingView = new MovieSettingView(mContext);
        movieSettingView.initView(mType);
        movieSettingView.setVisible(false);
        addViewBottom(movieSettingView);
    }

    public void setMovieSettingViewShow(boolean isShow) {
        if(isShow) {
            movieSettingView.refreshView();
            movieSettingView.setVisible(true);
        } else {
            movieSettingView.setVisible(false);
        }
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(isShow);
        }
    }

    /**
     * 隐藏此view的所有子view
     */
    public void hideAllView() {
        //隐藏设置相关全部菜单
        movieSettingView.setVisible(false);
        hdTypeView.setVisible(false);
        soundBarView.setVisible(false);
        selectSourceView.setVisible(false);
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(false);
        }
    }

    private IPlayerSettingCallBack mCallBack;
    private IViewVisiableListener mVisiableCallBack;

    public void setIPlayerSettingCallBack(IPlayerSettingCallBack callBack) {

        this.mCallBack = callBack;
        movieSettingView.setIPlayerSettingCallBack(callBack);
        hdTypeView.setIPlayerSettingCallBack(callBack);
        soundBarView.setIPlayerSettingCallBack(callBack);
        selectSourceView.setIPlayerSettingCallBack(callBack);
    }
    public void setOnViewVisiableListener(IViewVisiableListener listener){
        mVisiableCallBack = listener;
    }
}
