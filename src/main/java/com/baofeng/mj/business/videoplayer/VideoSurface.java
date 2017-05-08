package com.baofeng.mj.business.videoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.baofeng.mojing.MojingSDK;
import com.baofeng.mj.business.videoplayer.vrSurface.MatrixState;
import com.baofeng.mj.business.videoplayer.vrSurface.PlayerRectModel;
import com.baofeng.mj.business.videoplayer.vrSurface.PlayerSkyBoxModel;
import com.baofeng.mj.business.videoplayer.vrSurface.PlayerSphereModel;
import com.baofeng.mj.business.videoplayer.vrSurface.VrModel;
import com.baofeng.mojing.EyeTextureParameter;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by liuyunlong on 2016/6/22.
 */
public class VideoSurface  implements GvrView.StereoRenderer {

    protected VrModel m_vModel;
    protected Context m_context;
    protected  int g_textureImgWidth;
    protected  int g_textureImgHeight;
    protected int g_TexId;
    protected SurfaceTexture surfaceTexture;
    protected int g_frameBufferObject;
    protected VrModel.ModelType m_modelType = VrModel.ModelType.MODEL_RECT;
    protected VrModel.ScreenType m_screenType = VrModel.ScreenType.TYPE_2D;
    protected Surface surface;
    private int  mInitAngle = 0;
    protected  VrModel[] m_modelCache = new VrModel[4];

    private int g_select;
    boolean m_ChangeScene = false;
    boolean changed = false;
    public boolean g_ShowList = true;

    Handler m_surfaceHandler;
    int m_videoWidth,m_videoHeight;
    boolean m_init = false;
    public VideoSurface(Context context) {
        //super(context);
        m_init = false;
        m_context = context;
    }

    public void setHandler(Handler h){m_surfaceHandler = h;}

    public SurfaceTexture getSurfaceTexture(){
        return surfaceTexture;
    }

    void initModel()
    {
        m_modelCache[VrModel.ModelType.MODEL_RECT.ordinal()] =new PlayerRectModel();
        m_modelCache[VrModel.ModelType.MODEL_SPHERE.ordinal()] =new PlayerSphereModel(100,false);
        m_modelCache[VrModel.ModelType.MODEL_SPHERE180.ordinal()] =new PlayerSphereModel(100,true);
        m_modelCache[VrModel.ModelType.MODEL_BOX.ordinal()] =new PlayerSkyBoxModel(100f,2.0f/g_textureImgWidth);;
    }

    public void initSurface(VrModel.ScreenType screenType, VrModel.ModelType modelType)
    {
        if(m_init)
        {
            setModel(modelType);
            set3DType( screenType);
            setInitAngle(mInitAngle);
        }
        else
        {
            m_screenType = screenType;
            m_modelType = modelType;
        }


    }
    public void setVideoSize(int width ,int height)
    {
        m_videoWidth = width;
        m_videoHeight = height;

        float ratio = (float) width/height;
        if(m_vModel != null)
            m_vModel.setRatio(ratio);
    }


    public void setInitAngle(int angle){
        mInitAngle = angle;
        if(m_vModel!=null){
            m_vModel.setInitialAngle(angle);
        }
    }

    public void set3DType(VrModel.ScreenType type)
    {
        m_screenType = type;

        if(m_vModel != null)
        {
            m_vModel.setScreenType(m_screenType);
            setVideoSize(m_videoWidth,m_videoHeight);
        }

    }
    void setModel(VrModel.ModelType type )
    {
        if(!m_init)
        {
            m_modelType = type;
            return;
        }
        if(m_vModel == null || type != m_modelType)
        {
            int index = type.ordinal();
            if(m_modelCache[index] != null )
                m_vModel = m_modelCache[index];
            else if(type == VrModel.ModelType.MODEL_BOX){
                m_vModel = new PlayerSkyBoxModel(100f,2.0f/g_textureImgWidth);
                m_modelCache[index] = m_vModel;
            }else if(type == VrModel.ModelType.MODEL_SPHERE){
                m_vModel = new PlayerSphereModel(100,false);
                m_modelCache[index] = m_vModel;
            } else if(type == VrModel.ModelType.MODEL_SPHERE180){
                m_vModel = new PlayerSphereModel(100,true);
                m_modelCache[index] = m_vModel;
            }
            else
            {
                m_vModel = new PlayerRectModel();
                m_modelCache[index] = m_vModel;
            }
        }
        m_modelType = type;
    }

    public void renderToTexture(Eye eye) {
        float Camera[] = { -0.1f, 0.1f };
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        /**刷新流*/
        surfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        MatrixState.pushMatrix();

        float[] fM = eye.getEyeView();

        int e = eye.getType() == Eye.Type.RIGHT ? 1 : 0;
        MatrixState.setCamera(Camera[e], 0, 0, 0f, 0.0f, -0.1f, 0f, 1.0f, 0.0f);
        MatrixState.setViewMatrix(fM);

        if (m_vModel != null)
            m_vModel.drawSelf(g_TexId, e);
        MatrixState.popMatrix();
    }

    protected  int generateFrameBufferObject() {
        IntBuffer framebuffer = IntBuffer.allocate(1);
        GLES20.glGenFramebuffers(1, framebuffer);

        return framebuffer.get(0);
    }
    protected int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture,0);

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

    public void Release()
    {
        if (GLES20.glIsTexture(g_TexId)){
            GLES20.glDeleteBuffers(1, new int[]{g_TexId}, 0);
        }
        for(int i = 0 ; i< m_modelCache.length ;i++)
        {
            if(m_modelCache[i] != null)
                m_modelCache[i].release();
        }
        m_context = null;
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(Eye eye) {
        renderToTexture(eye);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        float fov = 96;
        float ratio = (float) Math.tan(Math.toRadians(fov / 2)) * 1.0f;
        Log.e("Mojing", String.format("radio is %f", ratio));
        MatrixState.setProjectFrustum(-ratio, ratio, -ratio, ratio, 1.f, 800);
        MatrixState.setCamera(0, 0, 0, 0f, 0.0f, -0.1f, 0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        android.util.DisplayMetrics display = m_context.getResources().getDisplayMetrics();
        if (display.widthPixels > display.heightPixels) {
            g_textureImgWidth = display.widthPixels;
            g_textureImgHeight = display.heightPixels;
        } else {
            g_textureImgWidth = display.heightPixels;
            g_textureImgHeight = display.widthPixels;
        }
        MatrixState.setInitStack();
        g_TexId = createTextureID();
        surfaceTexture = new SurfaceTexture(g_TexId);
        g_frameBufferObject = generateFrameBufferObject();

        initModel();
        m_init = true;
        initSurface(m_screenType,m_modelType);


        //surface = new Surface(surfaceTexture);
        Message message = new Message();
        message.what = 1;
        if(m_surfaceHandler != null)
            m_surfaceHandler.sendMessage(message);
    }

    @Override
    public void onRendererShutdown() {

    }
}
