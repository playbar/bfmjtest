package com.baofeng.mj.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.publicutil.GlassesUtils;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.MojingSDK;

/**
 * 从外部打开播放本地视频  魔镜-视频
 */
public class OpenMovieActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		initData();
	}

	private void initData(){
		Intent intent = getIntent();
		if(intent != null){
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {

				GlassesUtils.setDefaultGlasses(true);

				if (PermissionUtil.isOverMarshmallow() && !PermissionUtil.hasSelfPermissions(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					Toast.makeText(getApplicationContext(), R.string.storage_permission_denied, Toast.LENGTH_LONG).show();
					finish();
					return;
				}
				goToPlay();
			}
		}
	}

	public void goToPlay(){
		Uri uri = getIntent().getData();
		String mVideoScheme =  uri.getScheme();
		if ("http".equals(mVideoScheme)){//从网络打开
			String mVideoPath = Uri.decode(uri.toString());
			String mVideoTitle = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
			StartActivityHelper.playVideoWithNetDisk(this,mVideoTitle,mVideoPath);
		}else{//从文件管理器打开
			String mVideoPath = FileCommonUtil.getPathByUri(uri);
			if(TextUtils.isEmpty(mVideoPath)){
				return;
			}
			String mVideoTitle = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
			if(mVideoPath.endsWith(".jpeg") || mVideoPath.endsWith(".png")){//播放图片
				StartActivityHelper.playImageWithExplorer(this, mVideoTitle);
			}else{//播放视频
				StartActivityHelper.playVideoWithExplorer(this, mVideoTitle, mVideoPath);
			}
		}
		finish();
	}
}