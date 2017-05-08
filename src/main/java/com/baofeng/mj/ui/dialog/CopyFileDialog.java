package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;

import java.io.File;

/**
 * 拷贝文件对话框
 * @author Administrator
 *
 */
public class CopyFileDialog {
	private Activity context;
	private Dialog dialog;//对话框
	private LinearLayout ll_warning;//警告视图
	private LinearLayout ll_waiting;//拷贝文件视图
	private TextView tv_progres;//拷贝进度视图
	private boolean isCopying;//true正在拷贝文件
	private Handler handler;
	private Runnable runnable;

	public CopyFileDialog(Activity context) {
		this.context = (Activity) context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.setting_copyfile,null);//生成布局文件
		dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
		dialog.setContentView(view);//设置布局文件
		dialog.setCancelable(false);//false不可以点击返回键取消对话框
		ll_warning = (LinearLayout) view.findViewById(R.id.ll_warning);
		ll_waiting = (LinearLayout) view.findViewById(R.id.ll_waiting);
		tv_progres = (TextView) view.findViewById(R.id.tv_progres);
	}
	
	/**
	 * 显示对话框
	 */
	public void showDialog(boolean showWarning, boolean showWaiting){
		if(dialog != null && !dialog.isShowing()){
			dialog.show();
		}
		if(showWarning){
			ll_warning.setVisibility(View.VISIBLE);
		}else{
			ll_warning.setVisibility(View.GONE);
		}
		if(showWaiting){
			ll_waiting.setVisibility(View.VISIBLE);
		}else{
			ll_waiting.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示对话框
	 * @param time 毫秒
	 */
	public void showDialog(boolean showWarning, boolean showWaiting, long time){
		showDialog(showWarning, showWaiting);
		dismissDialog(time);
	}
	/**
	 * 隐藏对话框
	 */
	public void dismissDialog(){
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	/**
	 * 隐藏对话框
	 * @param time 毫秒
	 */
	public void dismissDialog(long time){
		if(handler == null){
			handler = new Handler();
		}
		if(runnable == null){
			runnable = new Runnable() {
				@Override
				public void run() {
					dismissDialog();
				}
			};
		}
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, time);
	}

	/**
	 * 拷贝文件
	 * @param oldPath 老的路径
	 * @param newPath 新的路径
	 * @param storageMode 存储模式
	 * @param copyFileCallback 回调
	 */
	public void copyFiles(final String oldPath, final String newPath, final int storageMode, final CopyFileCallback copyFileCallback) {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				isCopying = true;
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean copyResult = FileCommonUtil.copyFileByShell(oldPath, newPath);
				if (copyResult) {//拷贝文件成功
					FileCommonUtil.deleteFile(new File(oldPath));//删除旧的文件
					FileStorageUtil.setStorageMode(storageMode);//保存新的路径模式
					FileStorageUtil.setStorageDir(newPath);//保存新的路径
					FileStorageUtil.resetDownloadDir();//重置路径
					DownloadResBusiness.resetPath();//重置路径
				}
				return copyResult;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				dismissDialog();
				isCopying = false;
				if(copyFileCallback != null){
					copyFileCallback.callback(result);
				}
			}
		}.execute();
		showProgress(oldPath, newPath);//显示进度
	}

	/**
	 * 显示拷贝文件进度
	 * @param oldPath
	 * @param newPath
	 */
	private void showProgress(final String oldPath, final String newPath) {
		final File newFile = new File(newPath);
		final long fileTotalSize = FileSizeUtil.getFileSize(oldPath);//文件总大小
		if(fileTotalSize == 0){
			return;
		}
		final long unitSize = fileTotalSize / 100;//单位大小
		new AsyncTask<File, Integer, Void>() {
			@Override
			protected Void doInBackground(File... params) {
				while (isCopying) {//正在拷贝
					long hasCopySize = FileSizeUtil.getFileSize(newFile);//已经拷贝的大小
					publishProgress((int)(hasCopySize / unitSize));//更新进度
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				tv_progres.setText("正在移动资源(" + values[0] + "%)");
			}
		}.execute(newFile);
	}

	public interface CopyFileCallback{
		void callback(boolean copyResult);
	}
}
