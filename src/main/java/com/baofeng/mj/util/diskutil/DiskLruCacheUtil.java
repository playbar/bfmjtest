package com.baofeng.mj.util.diskutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ImageUtil;
import com.baofeng.mj.util.publicutil.MD5Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存工具类
 */
public class DiskLruCacheUtil {
	/**
	 * 获取磁盘缓存
	 */
	public static DiskLruCache getDiskLruCache() {
		String className = DiskLruCacheUtil.class.getCanonicalName();
		return getDiskLruCache(String.valueOf(className.hashCode()));
	}

	/**
	 * 获取磁盘缓存
	 * @param cacheName 缓存文件名
	 */
	public static DiskLruCache getDiskLruCache(String cacheName) {
		return getDiskLruCache(cacheName, 30 * 1024 * 1024);
	}

	/**
	 * 获取磁盘缓存
	 * @param cacheName 缓存文件名
	 * @param maxSize 缓存最大空间，例如10MB（值为10 * 1024 * 1024）
	 */
	public static DiskLruCache getDiskLruCache(String cacheName, long maxSize) {
		try {
			File cacheDir = getDiskCacheDir(cacheName);
			if(cacheDir != null){
				return DiskLruCache.open(cacheDir, ApkUtil.getVersionCode(), 1, maxSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取磁盘缓存目录
	 */
	public static File getDiskCacheDir(String cacheName) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			File externalCacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
			if (externalCacheDir == null) {
				cachePath = getExternalCacheDir(BaseApplication.INSTANCE.getPackageName());
			} else {
				cachePath = externalCacheDir.getPath();
			}
		} else {
			cachePath = BaseApplication.INSTANCE.getCacheDir().getPath();
		}
		File cacheDir = new File(cachePath, cacheName);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	/**
	 * 获取外部缓存目录
	 */
	public static String getExternalCacheDir(String packageName) {
		String path = Environment.getExternalStorageDirectory().getPath();
		StringBuilder sb = new StringBuilder();
		sb.append(path).append("/Android/data/").append(packageName).append("/cache");
		return sb.toString();
	}

	/**
	 * 保存数据到磁盘
	 */
	public static void saveDataToDisk(DiskLruCache mDiskLruCache, String cacheData, String url, boolean delete) {
		if (mDiskLruCache == null || TextUtils.isEmpty(cacheData) || TextUtils.isEmpty(url)){
			return;
		}
		try {
			String key = MD5Util.hashKeyForDisk(url);
			if(delete){
				mDiskLruCache.remove(key);// 根据key，删除原来缓存的数据
			}
			DiskLruCache.Editor editor = mDiskLruCache.edit(key);
			if (editor != null) {
				editor.set(0, cacheData);
				editor.commit();
				mDiskLruCache.flush();//刷入磁盘
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从磁盘获取数据
	 */
	public static String getDataFromDisk(DiskLruCache mDiskLruCache, String url) {
		if (mDiskLruCache == null || TextUtils.isEmpty(url)){
			return null;
		}
		try {
			String key = MD5Util.hashKeyForDisk(url);
			DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
			if (snapShot != null) {
				return snapShot.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保存图片到磁盘
	 */
	public static void saveBitmapToDisk(final DiskLruCache mDiskLruCache, final Bitmap bitmap, final String url) {
		if (mDiskLruCache == null || bitmap == null || TextUtils.isEmpty(url)){
			return;
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
			byte[] mContent = baos.toByteArray();
			baos.close();

			DiskLruCache.Editor editor = mDiskLruCache.edit(url);
			if (editor != null) {
				OutputStream outputStream = editor.newOutputStream(0);
				outputStream.write(mContent);
				outputStream.close();
				editor.commit();
				mDiskLruCache.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从磁盘获取Bitmap
	 */
	public static Bitmap getBitmapFromDisk(DiskLruCache mDiskLruCache, String url) {
		if (mDiskLruCache == null || TextUtils.isEmpty(url))
			return null;
		try {
			DiskLruCache.Snapshot snapShot = mDiskLruCache.get(url);
			if (snapShot != null) {
				FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(0);
				FileDescriptor fileDescriptor = fileInputStream.getFD();

				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
				opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, 30 * 1024);
				opts.inJustDecodeBounds = false;
				Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);

				fileInputStream.close();
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
