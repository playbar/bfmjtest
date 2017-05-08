package com.baofeng.mj.util.updateutil;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: APKDownloadUtils <br/>
 * @author yebo
 * @date: 2015年1月16日 下午3:10:57 <br/>
 *        description: 安装包下载的工具类
 */
public class APKDownloadUtils {

	private ExecutorService services;

	private Map<String, DownloadListener> map;

	private Map<String, String> savePaths;

	private List<String> pausesList;

	private static APKDownloadUtils downloadUtils;

	public static boolean isDownloading = false;
    public static boolean isPauseAll = false;
	@SuppressLint("NewApi")
	private APKDownloadUtils() {
		services = Executors.newSingleThreadExecutor();
		map = new ConcurrentSkipListMap<String, DownloadListener>();
		pausesList = Collections.synchronizedList(new ArrayList<String>());
		savePaths = new HashMap<String, String>();
	}

	public static APKDownloadUtils getInstance() {
		if (downloadUtils == null) {
			downloadUtils = new APKDownloadUtils();
		}

		return downloadUtils;
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:11:33
	 *         description:{需要下载APK的时候添加下载的任务}
	 * @param {url  {需要下载的APK的路径} {listener} {下载监听}
	 * @return {无}
	 */
	public void addTask(String url, DownloadListener listener) {
		if (!TextUtils.isEmpty(url) && listener != null) {
			map.put(url, listener);
		}
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:13:49
	 *         description:添加APK包的保存路径
	 * @param {url  {需要下载的APK的路径} {savePath} {APK保存的路径} {listener} {下载监听}
	 * @return {无}
	 */
	public void addTask(String url, String savePath, DownloadListener listener) {
		if (!TextUtils.isEmpty(url) && listener != null) {
			map.put(url, listener);
			if (!TextUtils.isEmpty(savePath)) {
				savePaths.put(url, savePath);
			}
		}
        isPauseAll=false;
    }

	/**
	 * @author yebo @Date 2015年1月16日 下午3:18:05
	 *         description:{删除下载任务}
	 * @param {url  {APK包的下载路径}
	 * @return {无}
	 */
	public void removeTask(String url) {
		if (!TextUtils.isEmpty(url)) {
			map.remove(url);
			pausesList.remove(url);
		}
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:18:48
	 *         description:{判断是否还有下载任务}
	 * @param {无
	 * @return {true：还有下载任务}
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:20:34
	 *         description:{执行下载的任务，下载过程中回调接口}
	 * @param {无
	 * @return {无}
	 */
	public void execute() {
		if (isDownloading)
			return;
		if (!NetworkUtil.canPlayAndDownload())
			return;
		services.execute(new Runnable() {

			@Override
			public void run() {
				while (!map.isEmpty()) {
					final Iterator<String> iterator = map.keySet().iterator();
					while (iterator.hasNext()) {
						String url = iterator.next();
						if (isPause(url))
							continue;
						isDownloading = true;
						String savePath = getAPKDownloadPath(url);
						download(url, savePath);
					}
				}
				isDownloading = false;
			}
		});
	}

	private int appearErrorTime = 0;

	/**
	 * @author yebo @Date 2015年1月16日 下午3:21:21
	 *         description:{真正执行下载的方法}
	 * @param {url  {需要下载的APK的路径} {savePath} {APK保存的路径}
	 * @return {无}
	 */
	private void download(String urlPath, String savePath) {
		if (isPause(urlPath))
			return;

		HttpURLConnection conn = null;
		InputStream in = null;
		BufferedInputStream bis = null;
		RandomAccessFile raf = null;

		try {
			map.get(urlPath).onStart();
			long position = getAPPDownloadSize(savePath);

			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(40 * 1000);
			conn.setReadTimeout(40 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Charser", "GBK,utf-8;q=0.7,*;q=0.3");
			if (position > 0)
				conn.setRequestProperty("Range", "bytes=" + position + "-");
			int responseCode = conn.getResponseCode();
			if (416 == responseCode) {
				map.get(urlPath).onSuccess(new File(savePath));
				map.remove(urlPath);
				return;
			} else if (200 == responseCode || 206 == responseCode) {
				in = conn.getInputStream();
				bis = new BufferedInputStream(in);
				int contentLength = conn.getContentLength();

				raf = new RandomAccessFile(new File(savePath), "rw");
				raf.seek(position);

				long count = position;
				byte buffer[] = new byte[1024 * 1024 * 2];
				do {
					int read = bis.read(buffer);
					count += read;

					if (read <= 0) {
						break;
					}
					raf.write(buffer, 0, read);
					map.get(urlPath).onLoading(contentLength, count);
					if (isPause(urlPath)) {
						map.get(urlPath).onPause(count);
						in.close();
						raf.close();
						bis.close();
						return;
					}
				} while (!isPause(urlPath));

				buffer = null;

				File file = new File(savePath);
				map.get(urlPath).onSuccess(file);
			}
			removeTask(urlPath);
			appearErrorTime = 0;
		} catch (IOException e) {
			e.printStackTrace();
			appearErrorTime++;
			if (appearErrorTime < 10) {
				download(urlPath, savePath);
			} else {
				DownloadListener listener = map.get(urlPath);
				if (listener != null)
					listener.onFailure(e, e.hashCode(), e.getMessage());
				setCancel(urlPath);
			}
		}
		// catch (Exception e) {
		// e.printStackTrace();
		// DownloadListener listener = map.get(urlPath);
		// if (listener != null)
		// listener.onFailure(e, e.hashCode(), e.getMessage());
		// setCancel(urlPath);
		// }
		finally {
			try {
				if (raf != null) {
					raf.close();
					raf = null;
				}
				if (bis != null) {
					bis.close();
					bis = null;
				}
				if (in != null) {
					in.close();
					in = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:22:05
	 *         description:{获取APK保存的路径}
	 * @param {url  {下载地址}
	 * @return {APK保存的路径}
	 */
	public String getAPKDownloadPath(String url) {
		if (savePaths != null && savePaths.containsKey(url)) {
			return savePaths.get(url);
		}
		String result = FileStorageUtil.getDownloadDir();
		if (!TextUtils.isEmpty(url)) {
			String apkName = url.substring(url.lastIndexOf("/") + 1);
			result = result + apkName;
		} else {
			result = result + "unknow.apk";
		}

		return result;
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:23:30
	 *         description:{根据APK保存的路径获取APK包的大小}
	 * @param {savePath  {APK保存的路径}
	 * @return {APK包的大小}
	 */
	public long getAPPDownloadSize(String savePath) {
		long result = 0;
		if (!TextUtils.isEmpty(savePath)) {
			File file = new File(savePath);
			if (file.isFile() && file.exists())
				result = file.length();
		}

		return result;
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:24:24
	 *         description:{暂停下载任务}
	 * @param {urlPath  {下载路径}
	 * @return {无}
	 */
	public void setPause(String urlPath) {
		if (!TextUtils.isEmpty(urlPath) && map.containsKey(urlPath)
				&& !pausesList.contains(urlPath)) {
			pausesList.add(urlPath);
		}
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:25:02
	 *         description:{继续下载APK}
	 * @param {urlPath  {下载路径}
	 * @return {无}
	 */
	public void setContinue(String urlPath) {
		if (!TextUtils.isEmpty(urlPath) && map.containsKey(urlPath)
				&& pausesList.contains(urlPath)) {
			pausesList.remove(urlPath);
		}
		execute();
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:25:29
	 *         description:{取消下载任务}
	 * @param {urlPath  {下载路径}
	 * @return {无}
	 */
	public void setCancel(String urlPath) {
		if (!TextUtils.isEmpty(urlPath)) {
			map.remove(urlPath);
			pausesList.remove(urlPath);
			savePaths.remove(urlPath);
			// deleteDownloadFile(urlPath);
		}
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:25:56
	 *         description:{判断下载任务是否暂停}
	 * @param {urlPath  {下载路径}
	 * @return {true：暂停}
	 */
	public boolean isPause(String urlPath) {
		if (!TextUtils.isEmpty(urlPath)) {
			return pausesList.contains(urlPath);
		}
		return false;
	}

	/**
	 * @author yebo @Date 2015年1月16日 下午3:28:23
	 *         description:{删除下载文件}
	 * @param {urlPath  {下载路径}
	 * @return {无}
	 */
	public void deleteDownloadFile(String urlPath) {
		if (TextUtils.isEmpty(urlPath))
			return;

		String apkDownloadPath = getAPKDownloadPath(urlPath);
		if (!TextUtils.isEmpty(apkDownloadPath)) {
			File apk = new File(apkDownloadPath);
			if (apk.exists() && apk.isFile()) {
				apk.delete();
			}
		}
	}

	/**
	 * ClassName: DownloadListener <br/>
	 * @author yebo
	 * @date: 2015年1月16日 下午3:27:21 <br/>
	 *        description: 下载回调的接口
	 */
	public interface DownloadListener {

		void onStart();

		void onLoading(long count, long current);

		void onPause(long current);

		void onRestart(long position);

		void onSuccess(File file);

		void onFailure(Throwable t, int errorNo, String strMsg);

	}
    public  static class simpleDownLoadListener implements  DownloadListener {


        @Override
        public void onStart() {

        }

        @Override
        public void onLoading(long count, long current) {

        }

        @Override
        public void onPause(long current) {

        }

        @Override
        public void onRestart(long position) {

        }

        @Override
        public void onSuccess(File file) {

        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {

        }
    }

	/**
	 * @author qiguolong @Date 2015-5-7 下午4:42:27
	 * @description:{暂停全部
	 */
	public void pauseAll() {
		final Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String url = iterator.next();
			// if (isPause(url))
			// continue;
			// isDownloading = true;
			// String savePath = getAPKDownloadPath(url);
			// download(url, savePath);
			setPause(url);
		}
	}

    /**
     * 关闭u3d下载
     */
    public void pauseAllU3d() {
        if (isPauseAll)
            return;
        final Iterator<String> iterator = map.keySet().iterator();
        for (Map.Entry<String, DownloadListener> entry : map.entrySet()) {
            if (entry.getValue() instanceof  simpleDownLoadListener){
                setPause(entry.getKey());
            }
        }
        isPauseAll=true;
    }
	/**
	 * @author qiguolong @Date 2015-5-7 下午4:43:47
	 * @description:{继续全部
	 */
	public synchronized void continueAll() {
//        final Iterator<String> it=pausesList.iterator();
//        while(it.hasNext()) {
//            setContinue(it.next());
//        }
		//一边迭代一边修改会报ConcurrentModificationException，改为如下
		for(int i = 0; i < pausesList.size(); i++){
			setContinue(pausesList.get(i));
		}
        isPauseAll=false;
	}

	/**
	 * ClassName: DownloadStatus <br/>
	 * @author yebo
	 * @date: 2015年1月16日 下午3:27:06 <br/>
	 *        description: 下载的状态
	 */
	public enum DownloadStatus {
		DOWNLOAD_NOTHING, DOWNLOADING, DOWNLOAD_PAUSE, DOWNLOAD_WAIT, DOWNLOAD_FINISHED, DOWNLOAD_ERROR;

		public static DownloadStatus valueOf(int i) {
			DownloadStatus status = DOWNLOAD_NOTHING;
			if (1 == i)
				status = DOWNLOADING;
			if (2 == i)
				status = DOWNLOAD_PAUSE;
			if (3 == i)
				status = DOWNLOAD_WAIT;
			if (4 == i)
				status = DOWNLOAD_FINISHED;
			if (5 == i)
				status = DOWNLOAD_ERROR;

			return status;
		}

	}

}
