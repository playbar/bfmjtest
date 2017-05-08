package com.baofeng.mj.ui.listeners;

import java.util.ArrayList;
import java.util.List;

public class GlassesScanResultListener {
	public static GlassesScanResultListener mInstance;
	private List<ScanResultListener> mlist = new ArrayList<ScanResultListener>();
	private GlassesScanResultListener(){
		
	}
	public static GlassesScanResultListener getInstance(){
		if(mInstance == null){
			mInstance = new GlassesScanResultListener();
		}
		return mInstance;
	}
	public void onBind(ScanResultListener listener){
		if(mlist==null){
			mlist = new ArrayList<ScanResultListener>();
		}
		mlist.add(listener);
	}
	public void unBind(ScanResultListener listener){
		if(mlist!=null && mlist.contains(listener)){
			mlist.remove(listener);
		}
	}
	
	public void notifyListener(String code){
		if(mlist==null||mlist.size()<=0)
			return;
		for(ScanResultListener listener : mlist){
			listener.onScanResult(code);
		}
	}
	
public interface ScanResultListener{
	void onScanResult(String code);
}
}
