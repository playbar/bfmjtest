package com.baofeng.mj.util.publicutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.baofeng.mj.util.diskutil.DiskLruCache;
import com.baofeng.mj.util.diskutil.DiskLruCacheUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;

/**
 * push里的加载bitmap任务
 */
public class BitmapDownloaderTaskForPush extends AsyncTask<String, Void, Bitmap> {
	@Override
	protected Bitmap doInBackground(String... params) {
		DiskLruCache mDiskLruCache = DiskLruCacheUtil.getDiskLruCache("notifyimg", 3 * 1024 * 1024);
		Bitmap bitmap = DiskLruCacheUtil.getBitmapFromDisk(mDiskLruCache, params[0]);
		if (bitmap == null) {
			InputStream inputStream = null;
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			HttpGet httpGet = new HttpGet(params[0]);
			try {
				HttpResponse response = client.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							inputStream = entity.getContent();
							bitmap = BitmapFactory.decodeStream(inputStream);
						} finally {
							inputStream.close();
							entity.consumeContent();
						}
					}
				}
			} catch (Exception e) {
				httpGet.abort();
			} finally {
				if (client != null) {
					client.close();
				}
			}
		}
		if (bitmap != null) {
			DiskLruCacheUtil.saveBitmapToDisk(mDiskLruCache, bitmap, params[0]);
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
	}
}
