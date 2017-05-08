package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.util.List;

/**
 * Created by wanghongfang on 2017/1/3.
 * 视频清晰度view显示
 */
public class VideoSelectHdView {
    VideoDetailBean videoBean;
    PanoramaVideoBean mPanoramVideoBean;
    List<VideoDetailBean.AlbumsBean> mDataList;
    PlayDefinitionPopupWindow mPlayDefinitionView;
    BottomContrallerView bootomView;
    int[] Difinition_Window_Pos = new int[2]; //清晰度弹窗位置
    int Difinition_Window_Height = 0;  //清晰度弹窗高度
    Activity activity;
    String mCurDefinition;
    IChangeHDSelectIndexListener changeListener;
    public  VideoSelectHdView(Activity activity,IChangeHDSelectIndexListener changeListener,BottomContrallerView bootomView){
        this.bootomView = bootomView;
        this.activity = activity;
        this.changeListener = changeListener;
    }

    public void setVideoBean(VideoDetailBean videoBean){
        this.videoBean = videoBean;
    }

    public void setmPanoramVideoBean(PanoramaVideoBean mPanoramVideoBean){
        this.mPanoramVideoBean = mPanoramVideoBean;
    }

    public void setCurDefinition(String hdtype){
        mCurDefinition = hdtype;
        if(mPlayDefinitionView!=null) {
            mPlayDefinitionView.setCurDefinition(mCurDefinition);
        }
    }
    public String getCurDefinition(){
        return mCurDefinition;
    }

    public boolean showDdfinitionView(TextView textView){
        if(mPlayDefinitionView==null){
            initPopViewDefinition();
        }
        if(mPlayDefinitionView.isShowing()){
            mPlayDefinitionView.dismiss();
            return false;
        }
        textView.getLocationOnScreen(Difinition_Window_Pos);
        int totalHeight =
//                textView.getPaddingBottom() +
                        Difinition_Window_Height;
        int width = textView.getWidth();
        mPlayDefinitionView.setWidth(width);
        int[] bottomPos = new int[2];
        bootomView.getLocationOnScreen(bottomPos);
       int padding = (Difinition_Window_Pos[1]-bottomPos[1]);
        mPlayDefinitionView.showAtLocation(textView, Gravity.NO_GRAVITY, Difinition_Window_Pos[0], bottomPos[1] - totalHeight);
        if(bootomView!=null) {
            bootomView.setDefinitonBtnUP(false);
        }
        return true;
    }

    /**
     * 初始化清晰度选项view
     */
    private void initPopViewDefinition(){
        mPlayDefinitionView = new PlayDefinitionPopupWindow(activity);
        int size = 0;
        if(mPanoramVideoBean!=null) {
            mPlayDefinitionView.setPanoramDatas(mPanoramVideoBean);
            size =  mPanoramVideoBean.getVideo_attrs().size();
        }else if(videoBean!=null){
            mPlayDefinitionView.setMovieVideoDatas(videoBean);
            size = videoBean.getAlbums().size();
        }else {
            return;
        }
        mPlayDefinitionView.setCurDefinition(mCurDefinition);
        mPlayDefinitionView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(bootomView!=null) {
                    bootomView.setDefinitonBtnUP(true);
                }
            }
        });
        Difinition_Window_Height = size *  (PixelsUtil.dip2px(35f)) + (size-1)*(PixelsUtil.dip2px(1f));
        mPlayDefinitionView.setOnItemClickListener(changeListener);
    }




    public interface IChangeHDSelectIndexListener{
        void onChangeHd(String hdtype);
        void onChangeSelectIndex(int index);
    }

    public boolean isShowing(){
        if(mPlayDefinitionView!=null&&mPlayDefinitionView.isShowing()) {
            return true;
        }
       return false;

    }

    public void dismiss(){
        if(mPlayDefinitionView!=null&&mPlayDefinitionView.isShowing()){
            mPlayDefinitionView.dismiss();
        }
    }
}
