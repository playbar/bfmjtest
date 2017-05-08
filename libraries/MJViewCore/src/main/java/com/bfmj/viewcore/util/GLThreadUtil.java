package com.bfmj.viewcore.util;

import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mac on 16/8/19.
 */
public class GLThreadUtil {

    public static native void onDrawFrame();

    public static native void onSurfaceChanged( int width, int height);

    public static native void onSurfaceCreated(EGLConfig config);

}
