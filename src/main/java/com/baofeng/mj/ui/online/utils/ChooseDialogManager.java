package com.baofeng.mj.ui.online.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.baofeng.mj.ui.online.view.PlayerTypeChoseDialog;

import java.util.ArrayList;

/**
 * Created by wanghongfang on 2017/2/14.
 */
public class ChooseDialogManager {

    private static ChooseDialogManager mInstance;
    private ArrayList<IDetailActiviyFinishCallback> mCallBackList = new ArrayList<IDetailActiviyFinishCallback>();
    private ChooseDialogManager(){

    }

    public static ChooseDialogManager getInstance(){
        if(mInstance==null)
            mInstance = new ChooseDialogManager();
        return mInstance;
    }

    public void Bind(IDetailActiviyFinishCallback chooseCallback){
        if(mCallBackList==null){
            mCallBackList = new ArrayList<IDetailActiviyFinishCallback>();
        }
        mCallBackList.add(chooseCallback);
    }

    public void unBind( IDetailActiviyFinishCallback chooseCallback){
        if(mCallBackList!=null&&mCallBackList.size()>0 && mCallBackList.contains(chooseCallback)){
            mCallBackList.remove(chooseCallback);
        }
    }

    public void notifyFinish(){
        if(mCallBackList==null)
            return;
        for(IDetailActiviyFinishCallback callback:mCallBackList){
            callback.finishDetailActivity();
        }
    }
    private PlayerTypeChoseDialog dialog;
    private Dialog getDialog(){
        return dialog;
    }
    public boolean isShowing(){
        if(dialog!=null&&dialog.isShowing())
            return true;
        return false;
    }
    public void showChooseDialog(Activity activity,View.OnClickListener PlayAndBacklistener){
        if(activity!=null&&activity.isFinishing())
            return;
        dialog = new PlayerTypeChoseDialog(activity);
        dialog.setGoUnityParams(activity, PlayAndBacklistener);
        dialog.show();
    }

    public interface IDetailActiviyFinishCallback{
        void finishDetailActivity();//进入GOunity状态下当跳转unity播放时需要把详情页finish
    }
}
