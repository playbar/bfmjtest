package com.baofeng.mj.ui.online.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghongfang on 2016/11/23.
 * 播放中网络监听
 */
public class PlayerNetworkSubject {
    private static PlayerNetworkSubject mInstance;
    private List<PlayerNetWorkChangeListener> listeners = new ArrayList<PlayerNetWorkChangeListener>() ;
    private PlayerNetworkSubject(){

    }
    public static PlayerNetworkSubject getInstance(){
        if(mInstance==null)
            mInstance = new PlayerNetworkSubject();
        return mInstance;
    }

    public void Bind(PlayerNetWorkChangeListener listener){
       listeners.add(listener);
    }
    public void UnBind(PlayerNetWorkChangeListener listener){
        if(listeners!=null&&listeners.size()>0&&listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    public void notifyChanged(int curState){
        if(listeners==null||listeners.size()<=0)
            return;
        for(PlayerNetWorkChangeListener listener:listeners){
            listener.networkChange(curState);
        }
    }

    public interface PlayerNetWorkChangeListener{
        void networkChange(int currentNetwork);//网络状态改变
    }
}
