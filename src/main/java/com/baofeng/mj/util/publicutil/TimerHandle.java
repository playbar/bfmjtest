package com.baofeng.mj.util.publicutil;


import android.os.Handler;
import android.os.Message;

/**
 * Created by qiguolong on 2016/3/7.
 * 主线程timer 效果
 */
public class TimerHandle extends Handler {
    private final int msgid = 1989;
    private  int times = 1000;
    private Runnable timerWork;

    public TimerHandle(Runnable timerWork){
        super();
        setTimerWork(timerWork);
    }
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == msgid) {
            if (timerWork != null)
                post(timerWork);
               send();
            }
        super.handleMessage(msg);

    }

    private void send() {
        sendEmptyMessageDelayed(msgid, times);
    }

    public void  cancel(){
        removeMessages(msgid);
    }
    public void  start(){
        send();
    }
    public void  release(){
        cancel();
        timerWork=null;
        removeCallbacksAndMessages(null);
    }


    public Runnable getTimerWork() {
        return timerWork;
    }

    public void setTimerWork(Runnable timerWork) {
        this.timerWork = timerWork;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
