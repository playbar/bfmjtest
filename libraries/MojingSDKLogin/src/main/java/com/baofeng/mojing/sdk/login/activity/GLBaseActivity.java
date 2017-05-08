package com.baofeng.mojing.sdk.login.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.sdk.login.R;
import com.bfmj.viewcore.view.BaseViewActivity;
import org.json.JSONObject;

public class GLBaseActivity extends BaseViewActivity {
	private String getGlassesKey() {
		String key = "";
		try {
			String json = MojingSDK.GetLastMojingWorld("zh");
			JSONObject jsonObject = new JSONObject(json);
			JSONObject jobj = jsonObject.getJSONObject("Glass");
			Log.i("adjustKey",json);
			key = jobj.getString("Key");
		} catch (Exception e) {
			e.printStackTrace();
		}

        return key;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 是否锁定视角
		super.onCreate(savedInstanceState);

		ImageView baseLine = new ImageView(this);
		LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		baseLine.setLayoutParams(lp);
		baseLine.setBackgroundResource(R.drawable.baseline);

		getRootLayout().addView(baseLine);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

}
