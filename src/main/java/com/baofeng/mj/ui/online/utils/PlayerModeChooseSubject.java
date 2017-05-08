package com.baofeng.mj.ui.online.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.VideoDetailActivity;
import com.baofeng.mj.ui.online.view.PlayerTypeChoseDialog;
import com.baofeng.mj.ui.online.view.VideoPlayerPreView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by wanghongfang on 2016/11/25.
 * 在线普通2D3D视频详情播放模型选择通知监听，使在线播放View使用正确的播放模式
 */
public class PlayerModeChooseSubject {

    private static PlayerModeChooseSubject mInstance;
    private ArrayList<IPlayerChooseCallback> mCallBackList = new ArrayList<IPlayerChooseCallback>();
    private PlayerModeChooseSubject(){

    }

    public static PlayerModeChooseSubject getInstance(){
        if(mInstance==null)
            mInstance = new PlayerModeChooseSubject();
        return mInstance;
    }

    public void Bind(IPlayerChooseCallback chooseCallback){
        if(mCallBackList==null){
            mCallBackList = new ArrayList<IPlayerChooseCallback>();
        }
        mCallBackList.add(chooseCallback);
    }

    public void unBind( IPlayerChooseCallback chooseCallback){
         if(mCallBackList!=null&&mCallBackList.size()>0 && mCallBackList.contains(chooseCallback)){
             mCallBackList.remove(chooseCallback);
         }
    }

    public void notifyChooseCallBack(final Activity activity,final String SqlNo,final IPlayerChooseCallback chooseCallback){
        int playerMode = SettingSpBusiness.getInstance().getPlayerMode();
        if(playerMode==0){//极简模式

            if(mCallBackList!=null&&mCallBackList.size()>0) {

                if(!(activity instanceof VideoDetailActivity)){
                    activity.finish();
                }
                for (IPlayerChooseCallback callback : mCallBackList) {
                    callback.doNormalPlay(SqlNo);
                }

            }
        }else if(playerMode==1){//沉浸模式
            if(chooseCallback!=null)
                if(mCallBackList!=null&&mCallBackList.size()>0) {

                    for (IPlayerChooseCallback callback : mCallBackList) {
                        callback.doVRPlay(SqlNo);
                    }

                }
               chooseCallback.doVRPlay(SqlNo);

        }else {
            ChooseDialogManager.getInstance().showChooseDialog(activity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (R.id.player_choose_dialog_simple_layout == v.getId()){
                        if(mCallBackList!=null&&mCallBackList.size()>0) {
                            if(!(activity instanceof VideoDetailActivity)){
                                activity.finish();
                            }
                            for (IPlayerChooseCallback callback : mCallBackList) {
                                callback.doNormalPlay(SqlNo);
                            }

                        }
                    }else if(R.id.player_choose_dialog_vr_layout == v.getId()){
                        if(mCallBackList!=null&&mCallBackList.size()>0) {

                            for (IPlayerChooseCallback callback : mCallBackList) {
                                callback.doVRPlay(SqlNo);
                            }

                        }
                        if(chooseCallback!=null)
                            chooseCallback.doVRPlay(SqlNo);

                    }else  if(R.id.close_img==v.getId()){
                        for (IPlayerChooseCallback callback : mCallBackList) {
                            callback.onChooseViewClose();
                        }
                    }
                }
            });

        }

    }

    public interface IPlayerChooseCallback{
        void doVRPlay(String SqlNo);
        void doNormalPlay(String SqlNo); //sqlNo 播放第几集
        void onChooseViewClose();
    }

}
