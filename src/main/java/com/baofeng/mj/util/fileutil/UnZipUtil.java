package com.baofeng.mj.util.fileutil;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.ui.dialog.UnZipDialog;
import com.baofeng.mj.unity.UnityActivity;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * 解压zip工具类
 */
public class UnZipUtil {
	public static final int BUFF_SIZE = 1024; // 1K Byte
	public static final int NOT_FOUND = 0; // 未找到解压包
	public static final int FORMAT_ERROR = 1; // 格式错误
	public static final int UNZIP_SUCCESS = 2; // 解压成功
	public static final int UNZIP_ERROR = 3; // 解压出错
	public static final int STROGE_LESS = 4; // 磁盘空间不足
	public static final int NOT_NEED_UNZIP = 5; // 不需要解压

	/**
	 * 解压
	 */
	public synchronized static void unZip(final DownloadItem downloadItem, final UnZipNotify unZipNotify) {
		final Activity curActivity = BaseApplication.INSTANCE.getCurrentActivity();
		if(curActivity == null || curActivity instanceof UnityActivity){//直接解压，不显示解压进度对话框
			startUnZip(downloadItem, new UnZipCallBack() {
				@Override
				public void unZipResult(int unZipResult) {
					if(unZipNotify != null){
						unZipNotify.notify(downloadItem, unZipResult);
					}
				}
			});
		}else{//边解压，边显示解压进度对话框
			final Handler handler = new Handler(curActivity.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					final UnZipDialog unZipDialog = new UnZipDialog(curActivity);//解压对话框
					unZipDialog.showUnZipDialog(UnZipDialog.START_UNZIP);//显示解压对话框
					startUnZip(downloadItem, new UnZipCallBack() {//开始解压
						@Override
						public void unZipResult(final int unZipResult) {
							switch (unZipResult) {//解压结果回调
								case NOT_FOUND:
									unZipDialog.showUnZipDialog(UnZipDialog.NOT_FOUND, handler);
									break;
								case FORMAT_ERROR:
									unZipDialog.showUnZipDialog(UnZipDialog.FORMAT_ERROR, handler);
									break;
								case UNZIP_ERROR:
									unZipDialog.showUnZipDialog(UnZipDialog.UNZIP_ERROR, handler);
									break;
								case STROGE_LESS:
									unZipDialog.showUnZipDialog(UnZipDialog.STROGE_LESS, handler);
									break;
								case NOT_NEED_UNZIP:
									unZipDialog.dismissUnZipDialog();//隐藏解压对话框
									break;
								default:
									unZipDialog.dismissUnZipDialog();//隐藏解压对话框
									break;
							}
							if(unZipNotify != null){
								unZipNotify.notify(downloadItem, unZipResult);
							}
						}
					});
				}
			});
		}
	}

	/**
	 * 开始压
	 */
	private static void startUnZip(final DownloadItem downloadItem, final UnZipCallBack mcallback) {
		//给解压加锁
		final String fileDir = DownloadResBusiness.getDownloadResFolder(downloadItem.getDownloadType());
		final File lockFile = new File(fileDir, downloadItem.getAid() + ".lock");
		if (lockFile.exists()) {
			if (mcallback != null) {
				mcallback.unZipResult(NOT_NEED_UNZIP);//不需要解压
			}
			return;//有锁，不解压
		}
		try {
			lockFile.createNewFile();//创建锁
		} catch (IOException e) {
			if (mcallback != null) {
				mcallback.unZipResult(UNZIP_ERROR);//解压出错
			}
			return;
		}
		final File fromFile = DownloadResBusiness.getDownloadResFile(downloadItem);
		new AsyncTask<Void, Void, Integer> (){
			@Override
			protected Integer doInBackground(Void... params) {
				if (!fromFile.exists()) {
					return NOT_FOUND;//文件不存在
				}
				if (!fromFile.getName().endsWith(ConstantKey.ZIP)) {
					return FORMAT_ERROR;//不是zip包
				}
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(fromFile);
				} catch (Exception e) {
					return UNZIP_ERROR;
				}
				try {//开始解压
					if(ConstantKey.OBB.equals(downloadItem.getAppFromType())){//解压OBB
						unZipObb(zipFile, fileDir, downloadItem);
					} else {//解压漫游
						String fileName = unZipRoaming(zipFile, fileDir);
						if(!TextUtils.isEmpty(fileName)){// 重命名目录名
							int index = fileName.indexOf("/");
							if (index > 0) {
								File oldFile = new File(fileDir, fileName.substring(0, index) + "/");
								File newFile = new File(fileDir, downloadItem.getAid() + "/");
								if (oldFile.exists()) {
									oldFile.renameTo(newFile);
								}
							}
						}
					}
					return UNZIP_SUCCESS;
				} catch (Exception e) {
					return STROGE_LESS;
				}
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				FileCommonUtil.deleteFile(fromFile);//删除zip压缩包
				FileCommonUtil.deleteFile(lockFile);//删除解压锁
				if (mcallback != null) {
					mcallback.unZipResult(result);
				}
			}
		}.execute();
	}

	/**
	 * 解压漫游
	 */
	private static String unZipRoaming(ZipFile zipFile, String unZipRoot) throws Exception {
		String fileName = null;
		Enumeration e = zipFile.getEntries();
		ZipEntry zipEntry;
		while (e.hasMoreElements()) {
			zipEntry = (ZipEntry) e.nextElement();
			if (zipEntry.isDirectory()) {
				if(TextUtils.isEmpty(fileName)){
					fileName = zipEntry.getName();
				}
			} else {
				File file = new File(unZipRoot, zipEntry.getName());
				file.getParentFile().mkdirs();

				InputStream fis = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] b = new byte[BUFF_SIZE];
				int len;
				while ((len = fis.read(b, 0, b.length)) != -1) {
					fos.write(b, 0, len);
				}
				fis.close();
				fos.close();
			}
		}
		return fileName;
	}

	/**
	 * 解压obb
	 */
	private static void unZipObb(ZipFile zipFile, String unZipRoot, DownloadItem mItem) throws Exception {
		Enumeration e = zipFile.getEntries();
		ZipEntry zipEntry;
		while (e.hasMoreElements()) {
			zipEntry = (ZipEntry) e.nextElement();
			if (zipEntry.isDirectory()) {
				LogHelper.e("infos","========zipEntry.isDirectory()==========");
			} else {
				File file = null;
				if(zipEntry.getName().endsWith(".apk")){//apk文件
					file = new File(unZipRoot, mItem.getAid()+".apk");
					LogHelper.e("infos","========apk=========="+file.getAbsolutePath());
				}else if(zipEntry.getName().endsWith(".obb")){  //obb文件
					file = new File(Environment.getExternalStorageDirectory(), zipEntry.getName());
					LogHelper.e("infos","========obb========="+file.getAbsolutePath());
				} else {
					file = new File(unZipRoot, zipEntry.getName());
					LogHelper.e("infos","========else========="+file.getAbsolutePath());
				}
				file.getParentFile().mkdirs();
				if(file.exists()){
					file.delete();
				}

				InputStream fis = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] b = new byte[BUFF_SIZE];
				int len;
				while ((len = fis.read(b, 0, b.length)) != -1) {
					fos.write(b, 0, len);
				}
				fis.close();
				fos.close();
			}
		}
	}

	/**
	 * 获取文件
	 */
	public static File getFile(DownloadItem downloadItem){
		String fileDir = DownloadResBusiness.getDownloadResFolder(downloadItem.getDownloadType());
		String title = downloadItem.getTitle();
		String fileSuffix = FileCommonUtil.getFileSuffix(downloadItem.getHttpUrl());
		return new File(fileDir, title + fileSuffix);
	}

	/**
	 * 获取锁文件
	 * @return
	 */
	private static File getLockFile(DownloadItem downloadItem){
		String fileDir = DownloadResBusiness.getDownloadResFolder(downloadItem.getDownloadType());
		return new File(fileDir, downloadItem.getAid() + ".lock");
	}

	/**
	 * 解压回调函数
	 */
	public interface UnZipCallBack {
		void unZipResult(int unZipResult);//解压结果
	}

	/**
	 * 解压通知
	 */
	public interface UnZipNotify{
		void notify(DownloadItem downloadItem, int unZipResult);
	}
}
