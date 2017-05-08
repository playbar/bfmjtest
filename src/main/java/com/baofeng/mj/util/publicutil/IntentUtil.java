package com.baofeng.mj.util.publicutil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * @author GURR 2014-4-30
 *         返回一些intent
 */
public class IntentUtil {
	// android获取一个用于打开HTML文件的intent
	public static Intent getHtmlFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(Uri.fromFile(new File(param)), "text/html");
		return intent;
	}

	// android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	// android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(String paramString, boolean paramBoolean) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(paramString);
			intent.setDataAndType(uri1, "text/plain");
		}
		while (true) {
			Uri uri2 = Uri.fromFile(new File(paramString));
			intent.setDataAndType(uri2, "text/plain");
			return intent;
		}
	}

	// android获取一个用于打开音频文件的intent
	public static Intent getAudioFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// android获取一个用于打开视频文件的intent
	public static Intent getVideoFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	//打开浏览器
	public static Intent getWebBrowser(String url) {
		Uri uri = Uri.parse(url);
		Intent in = new Intent(Intent.ACTION_VIEW);
		in.setData(uri);
		// new Intent(Intent.ACTION_VIEW, uri)
		return in;
	}

	//打开浏览器
	public static Intent getWebBrowserForChoose(String url) {
		Uri uri = Uri.parse(url);
		// return new Intent(Intent.ACTION_VIEW, uri);
		return Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri),
				"请选择浏览器");
	}

	//打电话
	public static Intent getPhone(String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		return new Intent(Intent.ACTION_DIAL, uri);
	}

	//发短信
	public static Intent getSms(String phone, String content) {
		Uri uri = Uri.parse("smsto:" + phone);
		Intent intent = new Intent(Intent.ACTION_DIAL, uri);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", content);
		return intent;
	}

	/**
	 * 发邮件
	 * @param tos 发送人
	 * @param ccs 抄送 可null
	 * @param body 文本
	 * @param text 副标题
	 * @param file 附件 可null
	 */
	public static Intent getEmails(String[] tos, String[] ccs, String body,
			String text, String file) {
		//startActivity(Intent.createChooser(it,"Choose Email Client"));
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_EMAIL, tos);
		if (ccs != null) {
			it.putExtra(Intent.EXTRA_CC, ccs);
		}
		if (TextUtils.isEmpty(file)) {
			it.putExtra(Intent.EXTRA_STREAM, file);
		}

		it.putExtra(Intent.EXTRA_TEXT, body);
		it.putExtra(Intent.EXTRA_SUBJECT, text);
		it.setType("message/rfc822");
		return it;
	}

	public static Intent getUnistall(String strPackageName) {
		Uri uri = Uri.fromParts("package", strPackageName, null);
		return new Intent(Intent.ACTION_DELETE, uri);
	}

	/**
	 * @author qiguolong @Date 2015-3-24 下午2:46:04
	 * @description:{播放video
	 * @param file
	 * @return
	 */
	public static Intent getVideo(String file) {
		Uri uri = Uri.parse(file);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		it.setType("audio/mp3");
		return it;
	}

	/**
	 * @author qiguolong @Date 2015-7-16 下午6:34:58
	 * @description:{通过类名返回intent
	 * @param name
	 * @return
	 */
	public static Intent getIntentForName(Context context, String name) {
		return new Intent().setClassName(context, name);
	}
}
