package com.baofeng.mj.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import com.storm.smart.play.baseplayer.BaseSurfacePlayer;
import com.storm.smart.play.call.BaofengPlayerListener;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.call.PlayerWithoutSurfaceFactory;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MediaGLSurfaceView extends GLSurfaceView implements Renderer,
        SurfaceTexture.OnFrameAvailableListener {
    //    private  final String TAG =getClass().getSimpleName();
    private final String TAG = "U3DSYSPlusPlayer";
    Context mContext;
    SurfaceTexture surfaceTexture;
    int mTextureID = -1;
    DirectDrawer mDirectDrawer;
    int w, h;
    //    U3DSYSPlusPlayer mediaPlayer;
    BaseSurfacePlayer mediaPlayer;
    private String path = "";
    private Surface surface;
    private int decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
    private boolean autoCreateMedia = true;
    private BaofengPlayerListener listener;
    private boolean isDouble = false;
    ChangePlayer changePlayer;
    private int mVideoHeight, mVideoWidth;
    private boolean is3d = false;
    private int rote = 0;
    private boolean isPause = false;

    public MediaGLSurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);

    }

    private void init(Context context) {
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public MediaGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

        //		CameraInterface.getInstance().doOpenCamera(null);
        //        surface = new Surface(surfaceTexture);
        //        w = getWidth();
        //        h = getHeight();
        //        doMedia(surface);
        //        mDirectDrawer.setRotation(180);
        initTexture();
        if (isAutoCreateMedia() && mediaPlayer == null)
            createPlayer();
        if (!TextUtils.isEmpty(path)&&mediaPlayer!=null)
            doSDKMedia();
        //        mDirectDrawer.setRotation(90);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        w = width;
        h = height;

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        //        Log.i(TAG, "onDrawFrame...");

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        // 是否双屏
        if (!is3d && isDouble) {
            drawDoubleScreen(mtx);
        } else {
            drawSingleScreen(mtx);
        }

        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void showTextTure() {
        //    GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //		CameraInterface.getInstance().doStopCamera();
        //        mediaPlayer.pause();
    }

    /**
     * @return gl初始化的值
     * @author qiguolong @Date 2015-1-26 下午3:51:30
     * @description:{进行TextureID构造
     */
    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    public SurfaceTexture _getSurfaceTexture() {
        return surfaceTexture;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // TODO Auto-generated method stub
        //                Log.i(TAG, "onFrameAvailable...");
        if (!isPause) {

            this.requestRender();
        }
    }


    public void doSDKMedia() {
        bindSurface();
        try {
            if (!mediaPlayer.setVideoPath(path)) {
                errorToChangeSoft();
            } else {

            }
            //            mediaPlayer.
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setPath(String path) {
        this.path = path;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    /**
     * 结束时调用
     */
    public void finish() {
        isPause = true;
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDirectDrawer.release();
            }
        });


    }

    public int getRote() {
        return rote;
    }

    public void setRote(int rote) {
        this.rote = rote;
        if (mDirectDrawer != null)
            mDirectDrawer.setRotation(rote);
    }

    public void testRotated() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                rote++;
                mDirectDrawer.setRotation(rote % 4 * 90);
            }
        }, 1000, 5000);
    }

    public BaseSurfacePlayer getMediaPlayer() {
        return mediaPlayer;
    }


    public void setMediaPlayer(BaseSurfacePlayer mediaPlayer) {

        this.mediaPlayer = mediaPlayer;
        if (mTextureID != -1) {
            bindSurface();
        }
    }

    private void bindSurface() {
        surface = new Surface(surfaceTexture);
        mediaPlayer.setSurfaceTexture(surfaceTexture);
        mediaPlayer.setSurface(surface);
    }

    public int getDecodeType() {
        return decodeType;
    }

    /**
     * @param decodeType IBfPlayerConstant.IBasePlayerType
     */
    public void setDecodeType(int decodeType) {
        this.decodeType = decodeType;
    }

    /**
     * 播放失败切换到软解
     */
    public void errorToChangeSoft() {
        if (decodeType != IBfPlayerConstant
                .IBasePlayerType.TYPE_SOFT) {
            setDecodeType(IBfPlayerConstant.IBasePlayerType.TYPE_SOFT);
            //            surface.release();

//            Toast.makeText(getContext(),"播放失败切换至软解",Toast.LENGTH_LONG).show();
            mediaPlayer.release();
            createPlayer();
            doSDKMedia();
        }
    }

    public String getPath() {
        return path;
    }

    public boolean isAutoCreateMedia() {
        return autoCreateMedia;
    }

    /**
     * 自动创建player
     *
     * @param autoCreateMedia
     */
    public void setAutoCreateMedia(boolean autoCreateMedia) {
        this.autoCreateMedia = autoCreateMedia;
        if (autoCreateMedia && mediaPlayer == null) {
            createPlayer();

        }
    }


    public void setMediaListener(BaofengPlayerListener listener) {
        this.listener = listener;
        //        mediaPlayer.setListener(listener);
    }

    public BaseSurfacePlayer createPlayer() {
        mediaPlayer = PlayerWithoutSurfaceFactory.createPlayer((Activity) mContext, decodeType,false);
        mediaPlayer.setListener(listener);
        if (changePlayer != null)
            changePlayer.changed();
        return mediaPlayer;
    }

    public boolean isDouble() {
        return isDouble;
    }

    /**
     * 是否分屏
     *
     * @param isDouble
     */
    public void setIsDouble(boolean isDouble) {
        this.isDouble = isDouble;
    }


    public ChangePlayer getChangePlayer() {
        return changePlayer;
    }

    public void setChangePlayer(ChangePlayer changePlayer) {
        this.changePlayer = changePlayer;
    }


    /**
     * @param mtx
     * @author qiguolong @Date 2015-2-11 下午3:24:09
     * @description:{绘制单屏画面
     */
    private void drawSingleScreen(float[] mtx) {
        int width = w, height = h;
        float rate = (float) mVideoHeight / (float) mVideoWidth;

        // 是否横屏
        if (w > h) {
            float screenRate = (float) h / (float) w;
            // 视频流高宽比大于屏幕高宽比
            if (rate <= screenRate) {
                height = (int) (width * rate);
            } else {
                width = (int) (height / rate);
            }

        } else {
            float screenRate = (float) w / (float) h;
            if (rate <= screenRate) {
                width = (int) (height * rate);
            } else {
                height = (int) (width / rate);
            }
        }

        // 如果旋转为垂直方向
//        if (rote == 90 || rote == 270) {
//
//            height = h;
//            width = (int) (height * rate);
//
//            if (width > w) {
//                width = w;
//                height = (int) (height / rate);
//            }
//        }
        if (rote == 0 || rote == 180) {

            height = h;
            width = (int) (height * rate);

            if (width > w) {
                width = w;
                height = (int) (height / rate);
            }
        }

        drawScreen(false, width, height, mtx);
    }

    /**
     * @param isDouble 是否双屏
     * @param width    绘制区域宽
     * @param height   绘制区域高
     * @param mtx      绘制数据矩阵
     * @author qiguolong @Date 2015-2-11 下午3:24:32
     * @description:{绘制屏幕
     */
    private void drawScreen(boolean isDouble, int width, int height, float[] mtx) {
        int centerX1, centerY1, centerX2, centerY2;

        if (isDouble) {

            if (w > h) {
                centerX1 = w / 4;
                centerX2 = w / 4 * 3;
                centerY2 = centerY1 = h / 2;
            } else {
                centerX2 = centerX1 = w / 2;
                centerY1 = h / 4;
                centerY2 = h / 4 * 3;
            }

            GLES20.glViewport(centerX1 - width / 2, centerY1 - height / 2,
                    width, height);
            mDirectDrawer.draw(mtx);

            GLES20.glViewport(centerX2 - width / 2, centerY2 - height / 2,
                    width, height);
            GLES20.glScissor(0, 0, w, h);
            mDirectDrawer.draw(mtx);
        } else {

            centerX1 = w / 2;
            centerY1 = h / 2;
            GLES20.glViewport(centerX1 - width / 2, centerY1 - height / 2,
                    width, height);
            //			GLES20.glViewport(centerX1*2 - width, centerY1- height / 2,
            //					5*mVideoWidth, height);
            GLES20.glScissor(0, 0, w, h);
            mDirectDrawer.draw(mtx);
        }
    }

    /**
     * @param mtx
     * @author qiguolong @Date 2015-2-11 下午3:24:19
     * @description:{绘制双屏画面
     */
    private void drawDoubleScreen(float[] mtx) {
        int width = w, height = h;
        float rate = (float) mVideoHeight / (float) mVideoWidth;

        // 是否横屏
        if (w > h) {
            float screenRate = (float) h / (float) w;
            // 视频流高宽比大于2倍屏幕高宽比

            if (rate <= screenRate * 2) {
                width = width / 2;
                height = (int) (width * rate);
            } else {
                width = (int) (height / rate);
            }
        } else {
            float screenRate = (float) w / (float) h;
            if (rate <= screenRate * 2) {
                height = height / 2;
                width = (int) (height * rate);
            } else {
                height = (int) (width / rate);
            }
        }

        // 如果旋转为垂直方向
        if (rote == 90 || rote == 270) {

            height = h;
            width = (int) (height * rate);

            if (width > w / 2) {
                width = w / 2;
                height = (int) (width / rate);
            }
        }

        drawScreen(true, width, height, mtx);
    }

    /**
     * @param width  视频宽
     * @param height 视频高
     * @author qiguolong @Date 2015-2-11 下午3:24:58
     * @description:{ 设置视频流宽高
     */
    public void setVideoSize(int width, int height) {
        mVideoWidth = width;

        mVideoHeight = height / (is3d ? 2 : 1);
    }

    /**
     * 当glview自动切换或创建plaerer成功时
     */
    public interface ChangePlayer {
        public void changed();
    }

    public boolean is3d() {
        return is3d;
    }

    public void setIs3d(boolean is3d) {
        if (this.is3d != is3d) {
            if (mVideoHeight != 0) {
                mVideoHeight = (int) (mVideoHeight * (is3d ? 0.5 : 2));
            }
        }
        this.is3d = is3d;
    }

    /**
     * 切换 解码方式
     * @param decodeType
     */
    public void switchViewType(int decodeType) {
        if (this.decodeType != decodeType) {
            setDecodeType(decodeType);
            if (mediaPlayer != null) {
                surface.release();
                mediaPlayer.release();
                createPlayer();
                doSDKMedia();
            }
        }
    }

    private void initTexture() {

        mTextureID = createTextureID();
        surfaceTexture = new SurfaceTexture(mTextureID);
        surfaceTexture.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mTextureID);
        mDirectDrawer.setRotation(rote);
    }
}
