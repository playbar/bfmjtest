package com.baofeng.mj.util.viewutil;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘管理
 * @author muyu
 *
 */
public class SoftKeyBoardManager {
	/**
	 * 收起软键盘
	 */
	public static void hideSoftKeyboard(Context context){
		try{
			InputMethodManager imm =  (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);   
			if(imm != null) {  
				imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
