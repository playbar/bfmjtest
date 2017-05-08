package com.bfmj.viewcore.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.distortion.Distortion;
import com.bfmj.viewcore.R;
import com.bfmj.viewcore.interfaces.IGLViewClickListener;
import com.bfmj.viewcore.render.GLColorRect;
import com.bfmj.viewcore.render.GLImageRect;
import com.bfmj.viewcore.render.GLScreenParams;
import com.bfmj.viewcore.render.GLVideoRect;
import com.bfmj.viewcore.util.GLFocusUtils;
import com.bfmj.viewcore.util.GLThreadUtil;
import com.bfmj.viewcore.util.Quaternion;
import com.bfmj.viewcore.util.Vector3;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;

public class GLRootView extends GvrView implements GvrView.StereoRenderer {
    private static final float DEGREE_TORADIAN  = 3.1415f / 180.0f;
    private ArrayList<GLView> mChild = new ArrayList<GLView>();
    private Context mContext;
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean isSurfaceCreated = false;
    private boolean isVisible = true;

    private Distortion mDistortion;
    private boolean mDistortionEnable = true;
    private boolean mGroyEnable = true;
    private float[] headView = new float[16];
    private float mRatio = 1;

    private float init_Pov_head = 0;//初始视角
    private Quaternion camera_quat;
    private Quaternion camera_quatx;
    private Quaternion camera_quaty;
    private Quaternion camera_quatz;
    private int currentDegreeMode = 0;//当前手机屏幕旋转角度，默认是向上
    private final int FLAG_DEGREE_TOP_MODE = 0;
    private final int FLAG_DEGREE_LEFT_MODE = 1;
    private final int FLAG_DEGREE_BOTTOM_MODE = 2;
    private final int FLAG_DEGREE_RIGHT_MODE = 3;
    private boolean mIsScreenTouch = false;
    private float mPreviousX;
    private float mPreviousY;
    public volatile float mDeltaX;
    public volatile float mDeltaY;
    public volatile float deltaX;
    public volatile float deltaY;

    private float downX=0.0f;
    private float downY=0.0f;
    private float upX=0.0f;
    private float upY=0.0f;

    static {
        System.loadLibrary("viewcore");
    }


    public static int createTexture(Bitmap bitmap) {
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        //上面是纹理贴图的取样方式，包括拉伸方式，取临近值和线性值
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);//让图片和纹理关联起来，加载到OpenGl空间中
        return textureId;
    }

    //////////

    public Queue<GLView> mCreateTextureQueue = new LinkedList<>();

    public GLRootView(Context context) {
        super(context);
        init(context);
    }

    public GLRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setEGLContextClientVersion(3);

        setRenderer(this);
        setTransitionViewEnabled(false);

        if (mContext instanceof  BaseViewActivity) {
            BaseViewActivity activity = (BaseViewActivity) mContext;
            mGroyEnable = activity.isGroyEnable();
            mDistortionEnable = activity.isDistortionEnable();
        }

        initHeadView();
    }

    @Override
    public void onResume() {
        for (GLView view : mChild) {
            view.onResume();
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        for (GLView view : mChild) {
            view.onPause();
        }

        super.onPause();

        if (mChild != null && mChild.size() > 0) {
            for (int i = 0; i < mChild.size(); i++) {
                mChild.get(i).release();
            }
        }
    }

    public void onDestroy() {
        if (mChild != null && mChild.size() > 0) {
            for (int i = 0; i < mChild.size(); i++) {
                mChild.get(i).release();
            }
        }
        GLColorRect.releaseInstance();
        GLImageRect.releaseInstance();
        GLVideoRect.releaseInstance();
        GLPanoView.releaseInstance();

    }

    //FPS测试 start//////
    private long lastFrame = System.currentTimeMillis();
    private int times = 0;

    public int getFPS() {
        long time = (System.currentTimeMillis() - lastFrame);
        int ts = times / 2;
        lastFrame = System.currentTimeMillis();
        times = 0;
        return time > 0 ? (int)(ts * 1000 / time) : 60;
    }
    //FPS测试 end//////

    public void setDistortionEnable(boolean enable) {
        if (mDistortionEnable != enable) {
            if (enable && mWidth > 0 && mHeight > 0) {
                mDistortion = Distortion.getInstance();
                mDistortion.setScreen(mWidth, mHeight);
            } else {
                mDistortion = null;
            }
            mDistortionEnable = enable;
//            setFov();
        }
    }

    public void initHeadView() {
        if (mGroyEnable) {
            if (isSurfaceCreated) {
                recenterHeadTracker();
            }
        } else {
            Matrix.setLookAtM(headView, 0, 0, 0, 0, 0, 0, -4, 0, 1, 0);
        }
    }

    public void addView(GLView view) {
        if (mChild.contains(view)){
            return;
        }

        if (isSurfaceCreated) {
            view.initDraw();
        }

        mChild.add(view);
    }

    public void addView(GLRectView view, boolean isFoucs) {
        addView(view);

        if (isFoucs) {
            if (GLFocusUtils.getFocusedView() != null) {
                GLFocusUtils.getFocusedView().onFocusChange(GLFocusUtils.TO_UNKNOWN, false);
            }
            GLFocusUtils.setFousedView(view);
            view.onFocusChange(GLFocusUtils.TO_UNKNOWN, true);
        }
    }

    public void removeView(GLView view) {
        if (view instanceof GLRectView) {
            GLRectView v1 = (GLRectView) view;
            GLRectView v2 = GLFocusUtils.getFocusedView();
            if (v1 == v2 || v1.isGrandChild(v2)) {
                GLFocusUtils.setFousedView(null);
            }
        }
        view.release();
        mChild.remove(view);
    }

    /**
     * 遍历GLGroupView下所有的view
     *
     * @param
     * @return view列表
     * @author lixianke  @Date 2015-3-18 下午4:45:01
     */
    private ArrayList<GLRectView> getViews(GLGroupView groupView) {
        ArrayList<GLRectView> views = new ArrayList<GLRectView>();
        views.add(groupView);

        if (groupView.isVisible()) {
            GLRectView view;
            int size = groupView.getView().size();
            for (int i = 0; i < size; i++) {
                view = groupView.getView(i);
                if (view == null || !view.isVisible()){
                    continue;
                }
                if (view instanceof GLGroupView) {
                    views.addAll(getViews((GLGroupView) view));
                } else {
                    views.add(view);
                }
            }
        }

        return views;
    }

    private static void viewSort( ArrayList<GLRectView>  views) {
        for (int i = 0; i < views.size(); i++) {
            GLRectView temp = views.get(i);
            int left = 0;
            int right = i-1;
            int mid = 0;
            while(left<=right){
                mid = (left+right)/2;
                GLRectView rhs = views.get(mid);
                if(temp.getDepth() - temp.getmIncrementDepth() - 0.000001f >rhs.getDepth() - rhs.getmIncrementDepth()){
                    right = mid-1;
                }else{
                    left = mid+1;
                }
            }
            for (int j = i-1; j >= left; j--) {
                views.set(j+1, views.get(j));
            }
            if(left != i){
                views.set(left, temp);
            }
        }
    }

    /**
     * 遍历所有的view
     *
     * @param
     * @return view列表
     * @author lixianke  @Date 2015-3-18 下午4:45:01
     */
    private ArrayList<GLView> getAllViews() {
        ArrayList<GLView> views1 = new ArrayList<GLView>();
        ArrayList<GLRectView> views2 = new ArrayList<GLRectView>();

        if (!isVisible) {
            return views1;
        }

//        try {
            for (GLView view : mChild) {
                if (!view.isVisible()) {
                    continue;
                } else if (view instanceof GLGroupView) {
                    views2.addAll(getViews((GLGroupView) view));
                } else if (view instanceof GLRectView) {
                    views2.add((GLRectView) view);
                } else {
                    views1.add(view);
                }
            }

            viewSort(views2);

            int zPosition = 0;
            for (GLRectView view : views2) {
                if (view != null) {
                    view.setZPosition(zPosition++);
                }
            }

        views1.addAll(views2);

        return views1;
    }

    private GLRectView getFocusedView() {
        for (int i = 0; i < mChild.size(); i++) {
            if (mChild.get(i) instanceof GLRectView) {
                GLRectView view = (GLRectView) mChild.get(i);
                if (view.isVisible() && view.isFocused()) {
                    return view;
                }
            }
        }
        return null;
    }

    private ArrayList<GLRectView> getRectViews() {
        ArrayList<GLRectView> views = new ArrayList<GLRectView>();
        for (GLView view : mChild) {

            if (view instanceof GLRectView) {
                views.add((GLRectView) view);
            }
        }
        return views;
    }

    public boolean onKeyDown(int keycode) {
//		if (getRenderMode() ==  RENDERMODE_WHEN_DIRTY){
//			mHeadViewNoChangeTimes = 0;
//			setRenderMode(RENDERMODE_CONTINUOUSLY);
//		}

        GLRectView view = getFocusedView();

        boolean flag = false;
        if (view != null) {
            flag = view.onKeyDown(keycode);
        }

        if (!flag) {
            ArrayList<GLRectView> views = getRectViews();
            switch (keycode) {
                case MojingKeyCode.KEYCODE_DPAD_LEFT:
                    GLFocusUtils.handleFocused(GLFocusUtils.TO_LEFT, view, views);
                    break;
                case MojingKeyCode.KEYCODE_DPAD_RIGHT:
                    GLFocusUtils.handleFocused(GLFocusUtils.TO_RIGHT, view, views);
                    break;
                case MojingKeyCode.KEYCODE_DPAD_UP:
                    GLFocusUtils.handleFocused(GLFocusUtils.TO_UP, view, views);
                    break;
                case MojingKeyCode.KEYCODE_DPAD_DOWN:
                    GLFocusUtils.handleFocused(GLFocusUtils.TO_DOWN, view, views);
                    break;
                default:
                    break;
            }
        }
        return flag;
    }

    public boolean onKeyUp(int keycode) {
        GLRectView view = getFocusedView();
        if (view != null) {
            return view.onKeyUp(keycode);
        }
        return false;
    }

    public boolean onKeyLongPress(int keycode) {
        GLRectView view = getFocusedView();
        if (view != null) {
            return view.onKeyLongPress(keycode);
        }
        return false;
    }

    public boolean isGroyEnable() {
        return mGroyEnable;
    }

    public void setGroyEnable(boolean groyEnable) {
        if (mGroyEnable == groyEnable) {
            return;
        }

        mGroyEnable = groyEnable;
        initHeadView();
    }

    //GvrView.StereoRenderer
    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(Eye eye) {
        GLThreadUtil.onDrawFrame();

        times ++;
        if (mChild == null || mChild.size() == 0) {
            return;
        }

        GLView v = mCreateTextureQueue.poll();
        if (v != null){
            v.createTexture();
        }

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        final ArrayList<GLView> allViews = getAllViews();
//        Log.e("onDrawFrame", "allViews size = >" + allViews.size());

        if (mGroyEnable) {
//            MojingSDK.getLastHeadView(headView);
            headView = eye.getEyeView();
        }

        float[] groyMatrix = new float[16];
        Matrix.setIdentityM(groyMatrix, 0);
        float roX = mDeltaX * DEGREE_TORADIAN;
        float roY = mDeltaY * DEGREE_TORADIAN;
        float[] out = new float[3];
        GLFocusUtils.getEulerAngles(headView, out, 0);

        float _x = out[1];
        float _y = out[0];
        float _z = out[2];

        double z = Math.toDegrees(_z);

        if(z>=-135&&z<-45){
            currentDegreeMode = FLAG_DEGREE_RIGHT_MODE;
        }else if(z>-45&&z<=45){
            currentDegreeMode = FLAG_DEGREE_TOP_MODE;
        }else if(z>45&&z<=135){
            currentDegreeMode = FLAG_DEGREE_LEFT_MODE;
        }else{
            currentDegreeMode = FLAG_DEGREE_BOTTOM_MODE;
        }
        camera_quatx.setRotation(new Vector3(0.f, 1.f, 0.f), roX + _y+init_Pov_head); // 绕Y轴旋转
        camera_quaty.setRotation(new Vector3(1.f, 0.f, 0.f), roY + _x); // 绕X轴旋转
        camera_quatz.setRotation(new Vector3(0.f, 0.f, 1.f), _z); // 绕Z轴旋转
        camera_quat = camera_quatx.mul(camera_quaty.mul(camera_quatz));

        float[] quatMatrix = new float[16];
        System.arraycopy(camera_quat.toMatrix().getAsArray(), 0, quatMatrix, 0, 16);
        Matrix.multiplyMM(groyMatrix, 0, quatMatrix, 0, groyMatrix, 0);

        float nearRight = GLScreenParams.getNear() * (float)Math.tan(GLScreenParams.getFOV() / 2);

        // 为了绘制中间的视频,把GLRectView分成两部分
        ArrayList<GLRectView> imageRectView1 = new ArrayList<>();
        ArrayList<GLRectView> imageRectView2 = new ArrayList<>();
        GLPlayerView playerView = null;
        for (int j = 0; j < allViews.size(); j++) {
            GLView view = allViews.get(j);
            if (view != null  && view.setBDraw( view.isVisible())) {
                view.getMatrixState().setVMatrix(groyMatrix);
                Matrix.frustumM(view.getMatrixState().getProjMatrix(), 0, -nearRight, nearRight, -nearRight * mRatio, nearRight * mRatio, GLScreenParams.getNear(), GLScreenParams.getFar());
                //					Matrix.orthoM(view.getMatrixState().getProjMatrix(), 0, -40, 40, -40, 40, GLScreenParams.getNear(), GLScreenParams.getFar());
                //			Matrix.setLookAtM(view.getMatrixState().getVMatrix(), 0, 0, 0, 0, headView[2], -headView[6], headView[10], 0, 1, 0);

                view.onBeforeDraw(eye.getType() == Eye.Type.RIGHT ? false : true);

                if (!(view instanceof GLRectView)){
                    view.draw();
                } else if (view instanceof GLPlayerView){
                    playerView = (GLPlayerView)view;
                } else if (playerView == null){
                    imageRectView1.add((GLRectView) view);
                } else {
                    imageRectView2.add((GLRectView) view);
                }
            }
        }
        if (imageRectView1.size() > 0){
            GLImageRect.getInstance().drawViews(imageRectView1);
        }
        if (playerView != null){
            GLVideoRect.getInstance().draw(playerView);
        }
        if (imageRectView2.size() > 0){
            GLImageRect.getInstance().drawViews(imageRectView2);
        }

        for (int j = 0; j < allViews.size(); j++) {
            GLView view = allViews.get(j);
            if (view != null  && view.isBDraw()) {
                view.onAfterDraw(eye.getType() == Eye.Type.RIGHT ? false : true);
            }
        }

        if (GLFocusUtils.getTouchPadHeadView() != null){
            GLFocusUtils.handleFocused(allViews);
        } else {
            GLFocusUtils.handleFocused(groyMatrix, allViews);
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLThreadUtil.onSurfaceChanged(width, height);

        mWidth = width;
        mHeight = height;

        if (mWidth > 0 && mHeight > 0){
            mRatio = (float) mHeight / (float)mWidth;
        }

        for (GLView view : mChild) {
            view.onSurfaceChanged(width, height);
        }

        camera_quatx = new Quaternion(); // This creates an identity quaternion
        camera_quaty = new Quaternion(); // This creates an identity quaternion
        camera_quatz = new Quaternion(); // This creates an identity quaternion
        camera_quat = new Quaternion(); // This creates an identity quaternion
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        GLThreadUtil.onSurfaceCreated(eglConfig);
        isSurfaceCreated = true;

        for (GLView view : mChild) {
            view.initDraw();
            view.onSurfaceCreated();
        }

        GLColorRect.initInstance();
        GLImageRect.initInstance();
        GLVideoRect.initInstance();
    }

    @Override
    public void onRendererShutdown() {

    }

    /**
     * 设置是否双屏
     */
    public void setDoubleScreen(boolean isDouble) {
        setStereoModeEnabled(isDouble);
    }

    public void setScreenTouch(boolean mIsScreenTouch){
        this.mIsScreenTouch = mIsScreenTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event != null){
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE && mIsScreenTouch){
                deltaX = (x - mPreviousX) / 4.0f / 2f;
                deltaY = (y - mPreviousY) / 4.0f / 2f;

                switch(currentDegreeMode){
                    case FLAG_DEGREE_TOP_MODE:
                        mDeltaX += deltaX;
                        mDeltaY += deltaY;
                        break;
                    case FLAG_DEGREE_LEFT_MODE:
                        mDeltaX += deltaY;
                        mDeltaY -= deltaX;
                        break;
                    case FLAG_DEGREE_BOTTOM_MODE:
                        mDeltaX -= deltaX;
                        mDeltaY -= deltaY;
                        break;
                    case FLAG_DEGREE_RIGHT_MODE:
                        mDeltaX -= deltaY;
                        mDeltaY += deltaX;
                        break;
                }

            }
            else if(event.getAction() == MotionEvent.ACTION_DOWN){
                downX = x;
                downY = y;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
//				if(mOnTouchUpCallback != null){
//					mOnTouchUpCallback.touchUp(x, y, downX, downY);
//				}
                upX = event.getX();
                upY = event.getY();
                if (Math.abs(downX - upX) < 12&&Math.abs(downY-upY)<12) {
                    if(mIGLViewClickListener!=null){
                        mIGLViewClickListener.click();
                    }
                }
            }
            mPreviousX = x;
            mPreviousY = y;

            return true;
        }
        else{
            return super.onTouchEvent(event);
        }
    }

    IGLViewClickListener mIGLViewClickListener;

    public void setOnGLClickListener(IGLViewClickListener mIGLViewClickListener){
        this.mIGLViewClickListener = mIGLViewClickListener;
    }

    /**
     * 回复画面到初始状态
     *    用于 用户手动通过触屏方式移动画面后 再次复原到初始状态
     */
    public void ResetRoteDegree(){
        currentDegreeMode = 0;
        mDeltaX=0;
        mDeltaY=0;
    }

    /**
     * 设置全景视频初始角度 （针对有些全局视频本身画面是有偏移的，通过设置该值，调整画面）
     * @param pov_head 角度
     */
    public void setInit_Pov_head(int pov_head){
        this.init_Pov_head =-90+pov_head ;
//		this.init_Pov_head = pov_head+70;

    }
}