package com.baofeng.mojing.sdk.glhelper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuxiang on 2017/4/6.
 */
public class TextureLoader {

    public enum TextureType{
        TEXTURE_TYPE_BITMAP,
        TEXTURE_TYPE_KTX,
        TEXTURE_TYPE_PKM,
        TEXTURE_TYPE_ASTC
    }

    public static void LoadTextureFromResource(Resources res, int id, Texture texture, TextureType type) throws IOException {
        long startTime = System.nanoTime();
        if (texture.textureID == 0) {
            int[] texNames = new int[1];
            GLES20.glGenTextures(1, texNames, 0);
            texture.textureID = texNames[0];
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureID);

        InputStream is = res.openRawResource(id);
        LoadTextureFromStream(is, texture, type);
        texture.loadTime = (System.nanoTime() - startTime) / 1000000.0f;
    }

    public static void LoadTextureFromFile(String fileName, Texture texture) throws IOException {
        long startTime = System.nanoTime();
        if (texture.textureID == 0) {
            int[] texNames = new int[1];
            GLES20.glGenTextures(1, texNames, 0);
            texture.textureID = texNames[0];
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureID);

        String tmpStr = fileName.toLowerCase();
        TextureType type = TextureType.TEXTURE_TYPE_BITMAP;
        if (tmpStr.endsWith(".pkm"))
        {
            type = TextureType.TEXTURE_TYPE_PKM;
        }
        if (tmpStr.endsWith(".ktx"))
        {
            type = TextureType.TEXTURE_TYPE_KTX;
        }
        if (tmpStr.endsWith(".astc"))
        {
            type = TextureType.TEXTURE_TYPE_ASTC;
        }
        InputStream is = new FileInputStream(fileName);
        LoadTextureFromStream(is, texture, type);
        texture.loadTime = (System.nanoTime() - startTime) / 1000000.0f;
    }

    public static void LoadTextureFromStream(InputStream is, Texture texture, TextureType type) throws IOException {
        switch (type)
        {
            case TEXTURE_TYPE_BITMAP: {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                texture.lastglerror = GLES30.glGetError();
                texture.width = bitmap.getWidth();
                texture.height = bitmap.getHeight();
                bitmap.recycle();
                if (texture.lastglerror != GLES30.GL_NO_ERROR)
                {
                    throw new IOException("Create texture failed: " + GLU.gluErrorString(texture.lastglerror));
                }
                break;
            }

            case TEXTURE_TYPE_KTX:
                new KtxLoader().LoadTextureFromStream(is, texture);
                break;

            case  TEXTURE_TYPE_PKM:
                PkmLoader.LoadTextureFromStream(is, texture);
                break;

            case  TEXTURE_TYPE_ASTC:
                AstcLoader.LoadTextureFromStream(is, texture);
                break;
        }
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }
}