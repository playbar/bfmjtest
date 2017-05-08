package com.baofeng.mojing.sdk.glhelper;

import android.opengl.GLES10;
import android.opengl.GLES20;

/**
 * Created by xuxiang on 2017/3/31.
 */
public class Texture {
    public int textureID = 0;
    public int width = 0;
    public int height =0;
    public int depth = 0;
    public int level = 0;
    public int border = 0;
    public boolean isMipmapped = false;
    long glType = 0;
    long glTypeSize = 1;
    long glFormat;
    int glInternalFormat;
    int glBaseInternalFormat;
    public int lastglerror = GLES20.GL_NO_ERROR;
    public float loadTime = 0.0f;

    /**
     * Check if a internal format is supported by the active OpenGL ES context.
     * @return true if the active OpenGL ES context supports the internal format.
     */
    public static boolean isCompressedTextureFormatSupported(int internalFormat)
    {
        int[] results = new int[20];
        GLES10.glGetIntegerv(GLES10.GL_NUM_COMPRESSED_TEXTURE_FORMATS, results, 0);
        int numFormats = results[0];
        if (numFormats > results.length)
        {
            results = new int[numFormats];
            GLES10.glGetIntegerv(GLES10.GL_COMPRESSED_TEXTURE_FORMATS, results, 0);
        }
        for (int i = 1; i < numFormats; i++)
        {
            if (results[i] == internalFormat)
            {
                return true;
            }
        }
        return false;
    }
}
