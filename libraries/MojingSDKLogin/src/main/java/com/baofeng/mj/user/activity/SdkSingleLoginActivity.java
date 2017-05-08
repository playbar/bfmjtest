package com.baofeng.mj.user.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baofeng.mojing.sdk.login.activity.VerticalBaseActivity;
import com.baofeng.mojing.sdk.login.R;
import com.baofeng.mojing.sdk.login.utils.SdkUtils;
import com.mojing.sdk.pay.service.IAIDLService;

/**
 * sdk单屏登录页
 * ClassName: SdkSingleLoginActivity <br/>
 * @author linzanxian    
 * @date: 2015-10-9 上午10:58:05 <br/>  
 * description:
 */
public class SdkSingleLoginActivity extends VerticalBaseActivity {
	private WebView mWebView;
	private String mUrl = "http://sso.mojing.cn/static/app/sdk/login.html";
	
	private boolean isBound = false;
	private IAIDLService boundService;
	private String mPackageName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//设置当前布局
        setContentView(R.layout.activty_webview);
        
        try {
        	Intent intent = getIntent();
        	if (intent != null) {
        		mPackageName = intent.getStringExtra("packageName"); 
        	}
		} catch (Exception e) {}
        
        
        //获取WebView
        mWebView=(WebView)findViewById(R.id.webView);
        
        //配置
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //加载网页
        mWebView.setSaveEnabled(false);
        mWebView.setWebChromeClient(new WebChromeClient());
        
        mWebView.addJavascriptInterface(new javaScriptInterface(), "login");  
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            	view.loadUrl(url);
            	return true;
            }
        });
	}
	
	@Override
	protected void onResume() {
		bindService();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unbindService();
		super.onPause();
	}

	/**
	 * 绑定服务
	 * @author linzanxian  @Date 2015-10-13 上午10:52:22
	 * @return void
	 */
	private void bindService() {
		Intent mIntent = new Intent();
		mIntent.setAction("android.intent.action.AIDLService");
		mIntent = SdkUtils.getExplicitIntent(getApplicationContext(), mIntent, mPackageName);
		if (mIntent == null) {
			return;
		}
		
		Intent eintent = new Intent(mIntent);
		
        bindService(eintent, connection, Context.BIND_AUTO_CREATE);
        isBound = true;  
    }  
  
	/**
	 * 解绑服务
	 * @author linzanxian  @Date 2015-10-13 上午10:52:40
	 * @return void
	 */
    private void unbindService() {
        if (isBound) {  
            unbindService(connection);  
            isBound = false;  
        }  
    }  
    
    /**
     * 服务连接
     */
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = IAIDLService.Stub.asInterface(service);  
        }  
  
        public void onServiceDisconnected(ComponentName className) {
            boundService = null;  
        }  
    };

	/**
	 * 网页js交互接口
	 * ClassName: javaScriptInterface <br/>
	 * @author linzanxian    
	 * @date: 2015-10-13 上午10:48:29 <br/>  
	 * description:
	 */
	public class javaScriptInterface {
		
		@JavascriptInterface
		public void success(String uid) {
			SdkSingleLoginActivity.this.finish();
			if (boundService != null) {
				try {
					boundService.loginCallback(uid);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		
		@JavascriptInterface
		public boolean isNetConnect() {
			ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) { 
				return false; 
			} else { 
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) { 
					for (int i = 0; i < info.length; i++) { 
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true; 
						} 
					} 
				} 
			} 
			
			return false; 
		}
		
		@JavascriptInterface
		public void doBack() {
			SdkSingleLoginActivity.this.finish();
		}
	}
	
}
