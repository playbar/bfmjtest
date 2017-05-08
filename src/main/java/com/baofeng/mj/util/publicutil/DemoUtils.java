package com.baofeng.mj.util.publicutil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.mojing.dl.domain.DownloadItem;
import com.mojing.dl.utils.DownloadConstant;
import com.storm.smart.domain.DramaBrowserItem;

import java.util.ArrayList;

/**
 * 下载操作类
 * ClassName: DemoUtils <br/>
 * @author linzanxian
 * @date: 2015年1月19日 上午9:57:41 <br/>
 *        description:下载操作类
 */
public class DemoUtils {
	public static boolean canGprsDownload = false;

	/**
	 * 启动下载
	 * @author linzanxian @Date 2015年1月19日 上午9:57:58
	 *         description:启动下载，点击下载后调用
	 * @param context Context
	 * @param item 下载资源项
	 * @return void
	 */
	public static void startDownload(Context context, DownloadItem item) {
		long downloadSize = (long)FileSizeUtil.getSizeFromString(item.getSite())+1;
		long sdEnoughSize = FileStorageUtil.getEnoughSDSize();
		if(downloadSize > sdEnoughSize){
			Toast.makeText(context,context.getResources().getString(R.string.sd_not_enough),Toast.LENGTH_SHORT).show();
			return;
		}
		if(NetworkUtil.canPlayAndDownload()){
			DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(item.getAid());//获取正在下载的DownloadItem
			if(downloadingItem == null){//没有下载过（是开始下载，不是继续下载）
				UserSpBusiness.getInstance().setDownloadNum(UserSpBusiness.getInstance().getDownloadNum() + 1);//本地下载次数加1
				if(ResTypeUtil.isGameOrApp(item.getDownloadType())){//游戏或者应用
					new ReportUtil().reportDownloadNum(item.getAid(), new ApiCallBack<String>() {
					});//上报下载量
				}
			}

			if (TextUtils.isEmpty(item.getSeq())) {
				item.setSeq("1");
			}
			DownloadUtils.getInstance().startDownload(context,item);

		}
	}

	/**
	 * 设置最大下载数
	 * @author linzanxian @Date 2015年1月19日 上午10:00:22
	 *         description:设置最大下载数
	 * @param context context
	 * @return void
	 */
	public static void changeMaxDownloadCount(Context context) {
//		Intent intent = new Intent(context, DownloadService.class);
//		intent.putExtra(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.CHANGE_DL_MAX_COUNT);
//		context.startService(intent);
	}

	/**
	 * 设置下载目录
	 * @author linzanxian @Date 2015年1月19日 上午10:02:56
	 *         description:设置下载目录
	 * @param context Context
	 * @param path 目录
	 * @return void
	 */
	public static void changeDownloadSdcardPath(Context context, String path) {
//		Intent intent = new Intent(context, DownloadService.class);
//		Bundle extras = new Bundle();
//		extras.putInt(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.CHANGE_DOWNLOAD_PATH);
//		extras.putString("downloadpath", path);
//		intent.putExtras(extras);
//		context.startService(intent);
	}

	/**
	 * 启动所有下载
	 * @author linzanxian @Date 2015年1月19日 上午10:04:25
	 *         description:启动下载池中的所有下载任务
	 * @param context Context
	 * @param reason 原因
	 * @return void
	 */
	public static void startAllDownload(Context context, int reason) {
		DownloadUtils.getInstance().startAllDownload(context);

	}

	/**
	 * 启动下载
	 * @author linzanxian @Date 2015年1月19日 上午10:05:31
	 *         description:启动下载，单独线程启动
	 * @param context Context
	 * @param drama 剧集相关信息
	 * @param fromTag tag
	 * @return {返回值说明}
	 */
	/*public static void startDownload(final Context context, final Drama drama, final String fromTag) {
		new Thread() {
			@Override
			public void run() {
				String downloadSite = getDownloadSite(drama);
				if (downloadSite == null) {
					downloadSite = drama.getCurSite();
				}
				DownloadItem item = new DownloadItem(
						DownloadItemType.ITEM_TYPE_VIDEO);
				if (downloadSite.startsWith("bf-")) {
					item.setItemType(DownloadItemType.ITEM_TYPE_P2P);
				}
				item.setAid(drama.getId());
				item.setSeq(drama.getSeq());
				item.setChannelType(drama.getChannelType());
				item.setTitle(drama.getTitle());
				item.setSite(downloadSite);
				item.setHas(drama.getHas());
				item.setTopicId(drama.getTopicId());
				item.setUlike(drama.isFromUlike());
				item.setAppFromType(fromTag);
				item.setThreeDVideoFlag(drama.getThreeD());

				startDownload(context, item);
			}

		}.start();

	}
*/
	/**
	 * 启动暴风APK下载
	 * @author linzanxian @Date 2015年1月19日 上午10:07:37
	 *         description:启动暴风APK下载
	 * @param context Context
	 * @param downloadItem
	 * @return void
	 */
	public static void startDownloadBfApk(Context context,
			DownloadItem downloadItem) {
		startDownload(context, downloadItem);
	}

	/**
	 * 启动下载
	 * @author linzanxian @Date 2015年1月19日 上午10:08:18
	 *         description:启动下载，单独线程启动
	 * @param context Context
	 * @param webItem 在线内容项
	 * @return void
	 */
	/*public static void startDownload(final Context context,
			final WebItem webItem) {
		new Thread() {

			@Override
			public void run() {
				String downloadSite = getAvalibleDownloadSite(
						webItem.getSites_mode(), webItem.getSite());
				if (downloadSite == null) {
					return;
				}

				DownloadItem item = new DownloadItem(
						DownloadItemType.ITEM_TYPE_VIDEO);
				if (downloadSite.startsWith("bf-")) {
					item.setItemType(DownloadItemType.ITEM_TYPE_P2P);
				}
				item.setAid(webItem.getAlbumId());
				item.setSeq(String.valueOf(webItem.getSeq()));
				item.setChannelType(webItem.getChannelType());
				item.setTitle(webItem.getAlbumTitle());
				item.setSite(downloadSite);
				item.setHas(webItem.getTvSeries());
				item.setTopicId(webItem.getTopicId());
				item.setUlike(false);
				item.setAppFromType(webItem.getFrom());
				item.setThreeDVideoFlag(webItem.getThreeD());

				startDownload(context, item);
			}

		}.start();
	}*/

	/**
	 * 暂停下载
	 * @author linzanxian @Date 2015年1月19日 上午10:09:33
	 *         description:暂停下载
	 * @param context Context
	 * @param item
	 * @return void
	 */
	public static void pauseDownload(Context context, DownloadItem item) {
		DownloadUtils.getInstance().pauseDownload(context,item);
	}

	public static void unityPauseDownload(Context context, DownloadItem item){
		DownloadUtils.getInstance().unityPauseDownload(context,item);
	}

	/**
	 * 删除下载
	 * @author linzanxian @Date 2015年1月19日 上午10:09:33
	 *         description:删除下载
	 * @param context Context
	 * @param item
	 * @return void
	 */
	public static void deleteDownload(Context context, DownloadItem item) {
		DownloadUtils.getInstance().deleteDownload(context,item);
	}

	/**
	 * 删除下载文件
	 * @author linzanxian @Date 2015年1月19日 上午10:09:33
	 *         description:删除下载文件
	 * @param context Context
	 * @param item
	 * @return void
	 */
	public static void deleteGameFile(Context context, DownloadItem item) {
//		Intent intent = new Intent(context, DownloadService.class);
//		Bundle extras = new Bundle();
//		extras.putSerializable(DownloadConstant.DOWNLOAD_ITEM, item);
//		extras.putInt(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.DL_DELETE_GAME_FILE);
//		intent.putExtras(extras);
//		context.startService(intent);
	}

	/**
	 * 创建绑定
	 * @author linzanxian @Date 2015年1月19日 上午10:15:37
	 *         description:创建绑定
	 * @param context Context
	 * @return void
	 */
	public static void createBindApkShortCut(Context context) {
//		Intent intent = new Intent(context, DownloadService.class);
//		intent.putExtra(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.DL_CREATE_BIND_APK_SHORCUT);
//		context.startService(intent);
	}

	/**
	 * 重置警告通知
	 * @author linzanxian @Date 2015年1月19日 上午10:11:29
	 *         description:重置警告通知
	 * @param context Context
	 * @return void
	 */
	public static void resetWarnNotification(Context context) {
//		Intent intent = new Intent(context, DownloadService.class);
//		intent.putExtra(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.CLEAR_NOTIFICATION);
//		context.startService(intent);
	}

	/**
	 * 暂停所有下载
	 * @author linzanxian @Date 2015年1月19日 上午10:12:31
	 *         description:暂停所有服务池中下载
	 * @param context
	 * @param downloadItemType
	 * @param reason
	 * @return void
	 */
	public static void pauseAll(Context context, int downloadItemType,
			int reason) {
		DownloadUtils.getInstance().pauseAllDownload(context);
	}

	/**
	 * 停止下载服务
	 * @author linzanxian @Date 2015年1月19日 上午10:17:04
	 *         description:停止下载服务
	 * @param context
	 * @return void
	 */
//	public static void stopDownlaodService(Activity context) {
//		pasueAllDownload(context, PauseReason.OTHER);
//		Intent intent = new Intent(context, DownloadService.class);
//		context.stopService(intent);
//	}

	/**
	 * 下载速度限制
	 * @author linzanxian @Date 2015年1月19日 上午10:17:04
	 *         description:下载速度限制
	 * @param context
	 * @param webItem
	 * @param isSpeedLimit
	 * @return void
	 */
	/*public static void setDlSpeedLimit(Context context, WebItem webItem,
			boolean isSpeedLimit) {
		if (webItem == null || webItem.getSite() == null) {
			return;
		}

		String downloadSite = getAvalibleDownloadSite(webItem.getSites_mode(),
				webItem.getSite());
		if (downloadSite == null) {
			downloadSite = webItem.getSite();
		}
		if (!downloadSite.startsWith("bf-")) {
			return;
		}
		DownloadItem item = new DownloadItem(DownloadItemType.ITEM_TYPE_P2P);
		item.setAid(webItem.getAlbumId());
		item.setSeq(String.valueOf(webItem.getSeq()));
		if (TextUtils.isEmpty(item.getSeq())) {
			item.setSeq("1");
		}
		Intent intent = new Intent(context, DownloadService.class);
		Bundle extras = new Bundle();
		extras.putSerializable(DownloadConstant.DOWNLOAD_ITEM, item);
		extras.putInt(DownloadCommand.DL_COMMAND,
				isSpeedLimit ? DownloadCommand.DL_SPEED_LIMIT_OPEN
						: DownloadCommand.DL_SPEED_LIMIT_CLOSE);
		intent.putExtras(extras);
		context.startService(intent);
	}
*/
	/**
	 * 启动下载服务
	 * @author linzanxian @Date 2015年1月19日 上午10:19:42
	 *         description:启动下载服务
	 * @param context Context
	 * @return void
	 */
	public static void startDownloadService(Context context) {
//		context.startService(new Intent(context, DownloadService.class));
	}

	/**
	 * 检查游戏更新
	 * @author linzanxian @Date 2015年1月19日 上午10:20:29
	 *         description:检查游戏更新
	 * @param context Context
	 * @return void
	 */
	public static void checkGameUpdates(Context context) {
//		Intent intent = new Intent(context, DownloadService.class);
//		intent.putExtra(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.DL_CHECK_GAME_UPDATE);
//		context.startService(intent);
	}

	/**
	 * 启动己下载列表
	 * @author linzanxian @Date 2015年1月19日 上午10:26:02
	 *         description:启动己下载列表中的下载项
	 * @param context Context
	 * @param isForceDown 是否强制下载
	 * @return void
	 */
	public static void startDownloadPrelist(Context context, boolean isForceDown) {
//		Intent intent = new Intent(context, DownloadService.class);
//		intent.putExtra(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.DL_START_DOWNLOAD_PRELIST);
//		intent.putExtra("isForceDown", isForceDown);
//		context.startService(intent);
	}

	/**
	 * 清除下载通知
	 * @author linzanxian @Date 2015年1月19日 上午10:27:12
	 *         description:清除下载通知
	 * @param context Context
	 * @return void
	 */
	public static void clearDownloadItemNotification(Context context,
			DownloadItem item) {
//		Intent intent = new Intent(context, DownloadService.class);
//		Bundle extras = new Bundle();
//		extras.putSerializable(DownloadConstant.DOWNLOAD_ITEM, item);
//		extras.putInt(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.DL_CLEAR_DOWNLOAD_NOTIFICATION);
//		intent.putExtras(extras);
//		context.startService(intent);
	}

	/**
	 * 发送安装游戏数
	 * @author linzanxian @Date 2015年1月19日 上午10:28:12
	 *         description:发送安装游戏数
	 * @param context Context
	 * @param item
	 * @return void
	 */
	/*public static void sendInstallGameCount(Context context, DownloadItem item) {
		if (item.getItemType() == DownloadConstant.DownloadItemType.ITEM_TYPE_GAME) {
			Intent intent = new Intent(context, DownloadService.class);
			Bundle extras = new Bundle();
			extras.putSerializable(DownloadConstant.DOWNLOAD_ITEM, item);
			extras.putInt(DownloadConstant.DownloadCommand.DL_COMMAND,
					DownloadConstant.DownloadCommand.DL_SEND_GAME_INSTALL_COUNT);
			intent.putExtras(extras);
			context.startService(intent);
		}
	}*/

	/**
	 * 启动己下载的服戏APP
	 * @author linzanxian @Date 2015年1月19日 上午10:29:11
	 *         description:{这里用一句话描述这个方法的作用}
	 * @param context Context
	 * @param appId
	 * @return void
	 */
	public static void startDownloadPreGameApp(Context context, int appId) {
//		Intent intent = new Intent(context, DownloadService.class);
//		Bundle extras = new Bundle();
//		extras.putInt("appId", appId);
//		extras.putInt(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.DL_DOWNLOAD_PRE_GAME_APP);
//		intent.putExtras(extras);
//		context.startService(intent);
	}

	/**
	 * 暂停所有下载
	 * @author linzanxian @Date 2015年1月19日 上午10:41:51
	 *         description:暂停下载池中的所有下载
	 * @param context Context
	 * @param reason 原因
	 * @return void
	 */
	public static void pasueAllDownload(Context context, int reason) {
		pauseAll(context, DownloadConstant.DownloadCommand.PAUSE_ALL_DOWNLOADS, reason);
	}

	/**
	 * 启动所有视频下载
	 * @author linzanxian @Date 2015年1月19日 上午10:42:33
	 *         description:启动所有视频下载
	 * @param context Context
	 * @param reason 原因
	 * @return void
	 */
	public static void startAllVideoDownload(Context context, int reason) {
//		Intent intent = new Intent(context, DownloadService.class);
//		Bundle extras = new Bundle();
//		extras.putInt(DownloadConstant.DownloadCommand.DL_COMMAND,
//				DownloadConstant.DownloadCommand.RESUME_ALL_VIDEO_DOWNLOADS);
//		extras.putInt("reason", reason);
//		intent.putExtras(extras);
//		context.startService(intent);
	}

	/**
	 * 暂停所有视频下载
	 * @author linzanxian @Date 2015年1月19日 上午10:42:33
	 *         description:暂停所有视频下载
	 * @param context Context
	 * @param reason 原因
	 * @return void
	 */
	public static void pasueAllVideoDownload(Context context, int reason) {
		pauseAll(context, DownloadConstant.DownloadCommand.PAUSE_ALL_VIDEO_DOWNLOADS, reason);
	}

	/**
	 * 获取可视的下载网址
	 * @author linzanxian @Date 2015年1月19日 上午10:43:31
	 *         description:获取可视的下载网址
	 * @param siteModes 剧集列表资源项
	 * @param curSite
	 * @return String
	 */
	private static String getAvalibleDownloadSite(
			ArrayList<DramaBrowserItem> siteModes, String curSite) {

		if (siteModes == null || curSite == null) {
			return curSite;
		}

		String tempSite = null;
		String downloadSite = null;
		for (DramaBrowserItem siteMode : siteModes) {
			if (siteMode.isSaveAble()) {
				if (curSite.equals(siteMode.getSite())) {
					// 当前站点可以下载
					downloadSite = siteMode.getSite();
					break;
				} else {
					if (tempSite == null) {
						tempSite = siteMode.getSite();
					}
				}
			}
		}

		if (downloadSite == null) {
			downloadSite = tempSite;
		}
		return downloadSite;
	}

	/**
	 * 获取下载地址
	 * @author linzanxian @Date 2015年1月19日 上午10:45:30
	 *         description:{这里用一句话描述这个方法的作用}
	 * @param drama 剧集相关信息
	 * @return String
	 */
	/*private static String getDownloadSite(Drama drama) {
		ArrayList<DramaBrowserItem> siteModes = drama.getSites_mode();
		String downloadSite = drama.getCurSite();
		if (downloadSite == null) {
			try {
				if (drama.getSites() != null) {
					JSONArray jsonArray = new JSONArray(drama.getSites());
					downloadSite = (String) jsonArray.get(0);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return getAvalibleDownloadSite(siteModes, downloadSite);
	}*/
}
