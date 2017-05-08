package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mojing.dl.domain.DownloadItem;

import java.util.ArrayList;

/**
 * Created by liuchuanchi on 2016/7/12.
 * 删除资源广播
 */
public class DeleteDownloadingReceiver extends BroadcastReceiver {
    public static final String ACTION_DELETE_DOWNLOADING = "action.DELETE_DOWNLOADING";
    private static ArrayList<DeleteDownloadingNotify> list = new ArrayList<DeleteDownloadingNotify>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null){
            return;
        }
        if(ACTION_DELETE_DOWNLOADING.equals(intent.getAction())){
            DownloadItem downloadItem = (DownloadItem) intent.getSerializableExtra("downloadItem");
            if(downloadItem == null){
                return;
            }
            for(int i = list.size() - 1; i >= 0; i--){
                DeleteDownloadingNotify deleteDownloadingNotify = list.get(i);
                if(deleteDownloadingNotify == null){
                    list.remove(i);
                }else{
                    deleteDownloadingNotify.deleteNotify(downloadItem);
                }
            }
        }
    }

    public static void addDeleteDownloadingNotify(DeleteDownloadingNotify deleteDownloadingNotify){
        if(deleteDownloadingNotify == null || list.contains(deleteDownloadingNotify)){
            return;
        }
        list.add(deleteDownloadingNotify);
    }

    public static void removeDeleteDownloadingNotify(DeleteDownloadingNotify deleteDownloadingNotify){
        if(deleteDownloadingNotify == null){
            return;
        }
        list.remove(deleteDownloadingNotify);
    }

    public interface DeleteDownloadingNotify{
        void deleteNotify(DownloadItem downloadItem);
    }
}
