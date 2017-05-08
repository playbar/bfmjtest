package com.bfmj.sdk.util;


import android.util.Log;

/**
 * 日志打印工具类
 * @author yanzw    
 * @date: 2015-1-16 上午11:04:57 
 */
public class Logger {
	
	private static String TAG = "com.bfmj.sdk";
	private static String MSG = "";
	protected static boolean debugMode = false;
	
	public static String getDebugTag() {
		return TAG;
	}
	
	/**
	 * Sets the tag to be used in LogCat debug messages
	 * 
	 * @param tag any valid String for LogCat tags
	 */
	public static void setDebugTag(String tag) {
		Logger.TAG = tag;
	}
	
	/**
	 * set debugMode
	 * @param debug
	 */
	public static void setDebugMode(boolean debug) {
		Logger.debugMode = debug;
	}
	
	/**
	 * Prints a warning to LogCat with information about the engine warning
	 * 
	 * @param message The message to be passed on
	 */
	public static void warning(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.w(TAG, MSG);
	}
	
	/**
	 * Prints a warning to LogCat with information about the engine warning
	 * 
	 * @param message The message to be passed on
	 */
	public static void w(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.w(TAG, MSG);
	}
	
	/**
	 * Prints to the verbose stream of LogCat with information from the engine
	 * 
	 * @param message The message to be passed on
	 */
	public static void print(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.v(TAG, MSG);
	}
	
	/**
	 * 打印 error 消息 到 LogCat
	 * @param message 消息
	 */
	public static void error(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.e(TAG, MSG);
	}
	
	/**
	 * 打印 error 消息 到 LogCat
	 * @param message 消息
	 */
	public static void e(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.e(TAG, MSG);
	}
	
	/**
	 * 打印 error 消息 到 LogCat
	 * @param message	消息
	 * @param t		异常对象
	 */
	public static void error(String message, Throwable t) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.e(TAG, MSG, t);
	}
	
	/**
	 * 打印 error 消息 到 LogCat
	 * @param message	消息
	 * @param t		异常对象
	 */
	public static void e(String message, Throwable t) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.e(TAG, MSG, t);
	}
	
	/**
	 * 打印 verbose 消息 到 LogCat
	 * @param message
	 */
	public static void verbose(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.v(TAG, MSG);
	}
	
	/**
	 * 打印 verbose 消息 到 LogCat
	 * @param message
	 */
	public static void v(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.v(TAG, MSG);
	}
	
	/**
	 * Forces the application to exit, this is messy, unsure if it should be used. For debugging purposes while testing, it will be.
	 */
	public static void forceExit() {
		System.exit(0);
	}
	
	/**
	 * 打印 info 消息 到 LogCat
	 * @param message	打印的消息
	 */
	public static void i(String message){
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
		Log.i(TAG, MSG);
	}
	
	/**
	 * 打印 info 消息 到 LogCat
	 * @param tag		
	 * @param message
	 */
	public static void i(String tag, String message){
		if(!debugMode) return;
		if(message == null) return ;
		if(StringUtil.valid(tag, true)){
			Log.i(tag, message);
			return ;
		}
		setCallerInfo(message);
		Log.i(TAG, MSG);
	}
	
	/**
	 * 设置日志设备调用者信息
	 * @param message 打印的消息
	 */
	private static void setCallerInfo(String message) {
		CallerInfo callerInfo = inferCaller();
		if(callerInfo != null){
			String fileName = callerInfo.getFileName();
			TAG = fileName.substring(0,fileName.lastIndexOf("."));
			MSG = callerInfo.getFileName() + 
								":" +  callerInfo.getLineNumber() + "行" + 
								"->" + callerInfo.getMethodName() +
								" 日志: " + message;
			return ;
		}
	}

	/**
	 * 打印 debug 消息 到 LogCat
	 * @param message
	 */
	public static void d(String message) {
		if(!debugMode) return;
		if(message == null) return ;
		setCallerInfo(message);
	}
	
	/**
	 * 打印 debug 消息 到 LogCat
	 * @param tag
	 * @param message
	 */
	public static void d(String tag, String message){
		if(!debugMode) return;
		if(message == null) return ;
		if(StringUtil.valid(tag, true)){
			return ;
		}
		setCallerInfo(message);
	}
	
	/**
	 * 推断调用类和方法名
	 * @return
	 */
	 public static CallerInfo inferCaller(){
		 // Get the stack trace.
		 StackTraceElement stack[] = (new Throwable()).getStackTrace();
		 // First, search back to a method in the Logger class.
		 int ix = 0;
		 while (ix < stack.length) {
			 StackTraceElement frame = stack[ix];
			 String cname = frame.getClassName();
			 if (cname.equals("com.baofeng.mj.pubblico.util.Logger")) {
				 break;
			 }
			 ix++;
		 }
		 // Now search for the first frame before the "Logger" class.
		 while (ix < stack.length) {
			 StackTraceElement frame = stack[ix];
			 String cname = frame.getClassName();
			 if (!cname.equals("com.baofeng.mj.pubblico.util.Logger")) {
				 // We've found the relevant frame.
				 CallerInfo callerInfo = new CallerInfo();
				 callerInfo.setFileName(frame.getFileName());
				 callerInfo.setMethodName(frame.getMethodName());
				 callerInfo.setLineNumber(frame.getLineNumber()+"");
				 return callerInfo;
			 }
		 	ix++;
		 }
		 // We haven't found a suitable frame, so just punt. This is
		 // OK as we are only commited to making a "best effort" here.
		 return null;
	 }
	 
	 /**
	  * Logger类的调用者信息类
 	  * @author yanzw
	  * @date 2014-5-9 上午11:56:14
	  */
	 private static class CallerInfo{
		 private String fileName = "";
		 private String methodName = "";
		 private String lineNumber = "";
		 
		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getMethodName() {
			return methodName;
		}
		
		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public String getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(String lineNumber) {
			this.lineNumber = lineNumber;
		}
		 
	 }
}
