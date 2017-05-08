package com.bfmj.sdk.util;

import com.bfmj.sdk.common.App;
import com.bfmj.viewcore.view.GLAdapterView;
import com.bfmj.viewcore.view.GLListView;
import com.bfmj.viewcore.view.GLRectView;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: GLListViewManager <br/>
 * @author liuchuanchi    
 * @date: 2015-9-28 下午6:05:20 <br/>  
 * @description: 通知GLListView，刷新头控状态
 */
public class GLListViewManager {
	private static GLListViewManager instance;//静态实例
	private List<GLListViewCallback> list;//回调集合

	private GLListViewManager(){
		list = new ArrayList<GLListViewCallback>();//实例化
	}
	
	public static GLListViewManager getInstance(){
		if(instance == null){
			instance = new GLListViewManager();
		}
		return instance;
	}
	
	/**
	 * @author liuchuanchi  @Date 2015-9-28 下午5:58:48
	 * @description:{通知GLListView，刷新头控状态}
	 *@param headControl true头控开启，false头控关闭
	 */
	public void notify(boolean headControl){
		if(list != null && list.size() > 0){
			for (GLListViewCallback callback : list) {
				if(callback != null){
					callback.notify(headControl);
				}
			}
		}
	}
	
	/**
	 * @author liuchuanchi  @Date 2015-9-28 下午5:57:00
	 * @description:{添加回调}
	 *@param callback
	 */
	public void addGLListViewCallback(GLListViewCallback callback){
		if(list != null && callback != null && !list.contains(callback)){
			list.add(callback);
		}
	}
	
	/**
	 * @author liuchuanchi  @Date 2015-9-28 下午5:57:11
	 * @description:{移除回调}
	 *@param callback
	 */
	public void removeGLListViewCallback(GLListViewCallback callback){
		if(list != null && callback != null){
			list.remove(callback);
		}
	}
	
	/**
	 * ClassName: GLListViewCallback <br/>
	 * @author liuchuanchi    
	 * @date: 2015-9-28 下午5:58:35 <br/>  
	 * @description: 回调接口
	 */
	public interface GLListViewCallback{
		/**
		 * @author liuchuanchi  @Date 2015-9-28 下午5:59:31
		 * @description:{通知GLListView，刷新头控状态}
		 *@param headControl true头控开启，false头控关闭
		 */
		public void notify(boolean headControl);
	}
	
	/**
	 * @author liuchuanchi  @Date 2015-9-29 下午1:45:04
	 * @description:{如果头控开启，mListView需要失去焦点}
	 *@param mListView
	 *@param onItemSelectedListener
	 */
	public static void onNothingSelected(GLListView mListView, GLAdapterView.OnItemSelectedListener onItemSelectedListener){
		if(DefaultSharedPreferenceManager.getInstance(App.getInstance()).getHeadControl()){//头控开启
			int mFocusIndex = mListView.getFocusIndex();
			int mTotalCount = mListView.getTotalCount();
			if (mFocusIndex >= 0 && mFocusIndex < mTotalCount && onItemSelectedListener!=null) {
				GLRectView _view = mListView.getView(mFocusIndex);
				if(_view != null) {
					onItemSelectedListener.onNothingSelected(null, _view, mFocusIndex, mListView.getStartIndex()+mFocusIndex);
				}
			}
		}
	}
}

