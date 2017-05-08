package com.baofeng.mj.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.dialog.ScanToPlayDialog;
import com.baofeng.mj.ui.dialog.ShowScanDialog;
import com.baofeng.mj.ui.listeners.GlassesScanResultListener;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.ViewfinderView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.SubApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.zxingutil.camera.CameraManager;
import com.baofeng.mj.util.zxingutil.decoding.CaptureActivityHandler;
import com.baofeng.mj.util.zxingutil.decoding.InactivityTimer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * 二维码扫描
 */
public class CodeScanActivity extends BaseActivity implements SurfaceHolder.Callback {
    private ViewfinderView viewfinder_view;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private MediaPlayer mediaPlayer;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private InactivityTimer inactivityTimer;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;
    private AppTitleBackView appTitleLayout;
    private String fromStr;
    private ShowScanDialog showScanDialog;
    private ScanToPlayDialog scanToPlayDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);
        checkPre();
    }

    /**
     * 6.0获取权限
     */
    private void checkPre() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.FLASHLIGHT}, 1
                    );
                }
            } else {
                CameraManager.init(getApplication());
                viewfinder_view = (ViewfinderView) findViewById(R.id.viewfinder_view);
                hasSurface = false;
                inactivityTimer = new InactivityTimer(this);
                appTitleLayout = (AppTitleBackView) findViewById(R.id.scan_title_layout);
                appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
                appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_RICH_SCAN"));
                fromPage();
            }
        } else {
            CameraManager.init(getApplication());
            viewfinder_view = (ViewfinderView) findViewById(R.id.viewfinder_view);
            hasSurface = false;
            inactivityTimer = new InactivityTimer(this);
            appTitleLayout = (AppTitleBackView) findViewById(R.id.scan_title_layout);
            appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
            appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_RICH_SCAN"));
            fromPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            CameraManager.init(getApplication());
            viewfinder_view = (ViewfinderView) findViewById(R.id.viewfinder_view);
            hasSurface = false;
            inactivityTimer = new InactivityTimer(this);
            appTitleLayout = (AppTitleBackView) findViewById(R.id.scan_title_layout);
            appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
            appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_RICH_SCAN"));
            fromPage();
        } else {
            finish();
        }
    }

    private void fromPage() {
        if (getIntent() != null && "GlassesView".equals(getIntent().getStringExtra("from"))) {
            fromStr = getIntent().getStringExtra("from");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager cameraManager = CameraManager.get();
        if (cameraManager != null) {
            cameraManager.closeDriver();
        }
    }

    @Override
    protected void onDestroy() {
        if(inactivityTimer!=null){
            inactivityTimer.shutdown();
        }
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    private boolean isGlassesCodeStr(String code) {
        if (TextUtils.isEmpty(code))
            return false;
        if (!code.contains("key=")) {
            return false;
        }
        return true;
//		Pattern pat = Pattern.compile("^[0-9A-Z-]{36,41}+$");
//		Matcher mat = pat.matcher(code);
//		return mat.find();
    }

    private boolean isURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return true;
        }

        return false;

    }

    /**
     * 扫描打开URL Scheme
     *
     * @param url
     * @return
     */
    private boolean isMojing(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.startsWith("mojing://?")) {
            return true;
        }
        return false;
    }

    private void openWebURl(String url) {
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(this, "当前无网络连接，请检查你的手机设置", Toast.LENGTH_SHORT).show();
            return;
        }
        final String resultString = result.getText();
        if ("GlassesView".equals(fromStr)) {
            if (resultString.equals("") || !isGlassesCodeStr(resultString)) {
                if (showScanDialog == null) {
                    showScanDialog = new ShowScanDialog(this, "无效的眼镜二维码信息");
                }
                showScanDialog.showDialog(CodeScanActivity.this, new ShowScanDialog.MyDialogInterface() {
                    @Override
                    public void dialogCallBack() {
                        continuePreview();
                    }
                });
            } else {
                String[] results = resultString.split("=");
                String key = results[1];
                GlassesScanResultListener.getInstance().notifyListener(key);
                CodeScanActivity.this.finish();
            }
//            CodeScanActivity.this.finish();
            return;
        }
        if (isMojing(resultString)) {
            final Uri uri = Uri.parse(resultString.trim());
            String schemeAction = uri.getQueryParameter("action");
            String text = null;
            if ("open".equals(schemeAction)) {
                text = "立即打开";
            } else if ("play".equals(schemeAction)) {
                text = "立即播放";
            } else if ("download".equals(schemeAction)) {
                text = "立即下载";
            }
            if (scanToPlayDialog == null) {
                scanToPlayDialog = new ScanToPlayDialog(this);
            }
            scanToPlayDialog.showDialog(new ScanToPlayDialog.MyDialogInterface() {
                @Override
                public void dialogCallBack() {//清除
                    Intent intent = new Intent(CodeScanActivity.this, MainActivityGroup.class);
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                    CodeScanActivity.this.finish();
                }
            }, new ScanToPlayDialog.MyDialogInterfaceCancel() {
                @Override
                public void dialogCallBack() {
                    continuePreview();
                }
            }, text);
        } else if (isURL(resultString)) {
            Intent intent = new Intent(CodeScanActivity.this, H5Activity.class);
            intent.putExtra("next_url", resultString.trim());
            intent.putExtra("next_title", "网页");
            startActivity(intent);
            CodeScanActivity.this.finish();
        } else {
            if (showScanDialog == null) {
                showScanDialog = new ShowScanDialog(this, resultString.trim());
            }
            showScanDialog.showDialog(CodeScanActivity.this, new ShowScanDialog.MyDialogInterface() {
                @Override
                public void dialogCallBack() {
                    continuePreview();
                }
            });
        }
        //扫描订阅本期不加
//        if (resultString.equals("")) {
//        } else {
//            if (isURL(resultString)) {
//                String[] split = resultString.split("\\?");
//                if (split.length > 1) {
//                    String[] split1 = split[1].split("\\&");
//                    if (split1.length > 1) {
//                        if ("qrcodetype=1".equals(split1[0])) {
//                            String[] split2 = split1[1].split("\\=");
//                            String[] split3 = split2[1].split("\\|");
//                            getAlbum(split3[1]);
//                        }
//                    }
//                }
//                if (resultString.contains("album_id")) {
//                }
//            }
//        }
    }

    private void continuePreview() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    /**
     * 初始化声音
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public ViewfinderView getViewfinderView() {
        return viewfinder_view;
    }

    public void drawViewfinder() {
        viewfinder_view.drawViewfinder();
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public Handler getHandler() {
        return handler;
    }

    /**
     * 扫码订阅
     *
     * @param albumId
     */
    private void getAlbum(String albumId) {
        HashMap<String, String> params = new HashMap<String, String>();
        String time = System.currentTimeMillis() + "";
        String uid = UserSpBusiness.getInstance().getUid();
        params.put("sub_time", time);
        params.put("uid", uid);
        params.put("album_id", albumId);
        String paramsUrl = Common.getSortString(params);
        String sign = Common.getCodeStr(paramsUrl).trim();
        RequestParams requestParams = new RequestParams();
        requestParams.put("sub_time", time);
        requestParams.put("uid", uid);
        requestParams.put("album_id", albumId);
        requestParams.put("sign", sign);
        new SubApi().Subscribe(this, ConfigUrl.getSubUrl(), requestParams, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(CodeScanActivity.this, "订阅失败!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("status")) {
                            if (jsonObject.getInt("status") == 0) {
                                Intent intent = new Intent(CodeScanActivity.this, SubscribeActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CodeScanActivity.this, "订阅失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CodeScanActivity.this, "订阅失败!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }
        });
    }
}
