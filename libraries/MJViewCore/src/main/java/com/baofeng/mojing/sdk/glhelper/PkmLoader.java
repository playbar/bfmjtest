package com.baofeng.mojing.sdk.glhelper;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLU;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by xuxiang on 2017/3/31.
 */
public class PkmLoader {

    private static final int[] etc2CompressedInternalFormats = {
            GLES11Ext.GL_ETC1_RGB8_OES,                         // 00
            GLES30.GL_COMPRESSED_RGB8_ETC2,                     // 01
            0,                                                  // 02
            GLES30.GL_COMPRESSED_RGBA8_ETC2_EAC,                // 03
            GLES30.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2, // 04
            GLES30.GL_COMPRESSED_R11_EAC,                       // 05
            GLES30.GL_COMPRESSED_RG11_EAC,                      // 06
            GLES30.GL_COMPRESSED_SIGNED_R11_EAC,                // 07
            GLES30.GL_COMPRESSED_SIGNED_RG11_EAC                // 08
    };

    static final int[] etc2BaseInternalFormats = {
            GLES20.GL_RGB,                                      // 00
            GLES20.GL_RGB,                                      // 01
            0,                                                  // 02
            GLES30.GL_COMPRESSED_RGBA8_ETC2_EAC,                // 03
            GLES30.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2, // 04
            GLES30.GL_COMPRESSED_R11_EAC,                       // 05
            GLES30.GL_COMPRESSED_RG11_EAC,                      // 06
            GLES30.GL_COMPRESSED_SIGNED_R11_EAC,                // 07
            GLES30.GL_COMPRESSED_SIGNED_RG11_EAC                // 08
    };

    private static final String[] etc2CompressedInternalFormatsName = {
            "GL_ETC1_RGB8_OES",                             // 00
            "GL_COMPRESSED_RGB8_ETC2",                      // 01
            "UNKNOWN",                                      // 02
            "GL_COMPRESSED_RGBA8_ETC2_EAC",                 // 03
            "GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2",  // 04
            "GL_COMPRESSED_R11_EAC",                        // 05
            "GL_COMPRESSED_RG11_EAC",                       // 06
            "GL_COMPRESSED_SIGNED_R11_EAC",                 // 07
            "GL_COMPRESSED_SIGNED_RG11_EAC"                 // 08
    };

    public static final int ETC_PKM_HEADER_SIZE = 16;

    public static void LoadTextureFromStream(InputStream is, Texture texture) throws IOException {
        byte[] headerBuffer = new byte[ETC_PKM_HEADER_SIZE];
        if (is.read(headerBuffer, 0, ETC_PKM_HEADER_SIZE) != ETC_PKM_HEADER_SIZE) {
            throw new IOException("Unable to read PKM file header.");
        }
        String headerId = new String(headerBuffer, 0, 4);
        if (headerId.compareTo("PKM ") != 0) {
            throw new IOException("Not a PKM file.");
        }

        String version = new String(headerBuffer, 4, 2);
        texture.width = (headerBuffer[12] & 0xFF) * 256 + (headerBuffer[13] & 0xFF);
        texture.height = (headerBuffer[14] & 0xFF) * 256 + (headerBuffer[15] & 0xFF);
        if (version.startsWith("10")) {
            LoadEtc1FromStream(is, texture);
        } else if (version.startsWith("20")) {
            LoadEtc2FromStream(is, texture, headerBuffer[7] & 0xFF);
        }
        else {
            throw new IOException("Not supported PKM version.");
        }
    }

    private static void LoadEtc1FromStream(InputStream is, Texture texture) throws IOException
    {
        texture.glInternalFormat = GLES11Ext.GL_ETC1_RGB8_OES;
        texture.glBaseInternalFormat = GLES20.GL_RGB;
        if (!Texture.isCompressedTextureFormatSupported(GLES11Ext.GL_ETC1_RGB8_OES))
        {
            throw new IOException("ETC1 is not supported by this device.");
        }

        // ETC1把一个4x4的像素单元组压成一个64位的数据块
        ByteBuffer data = ReadData(is);
        int imageSize = data.remaining();
        GLES20.glCompressedTexImage2D(GLES30.GL_TEXTURE_2D, texture.level, GLES11Ext.GL_ETC1_RGB8_OES, texture.width, texture.height, texture.border, imageSize, data);
        texture.lastglerror = GLES20.glGetError();
        if (texture.lastglerror != GLES20.GL_NO_ERROR)
        {
            throw new IOException("Create texture failed: " + GLU.gluErrorString(texture.lastglerror));
        }
    }

    private static void LoadEtc2FromStream(InputStream is, Texture texture, int internalFormat) throws IOException
    {
        texture.glInternalFormat = etc2CompressedInternalFormats[internalFormat];
        if (texture.glInternalFormat == 0)
        {
            throw new IOException("Not supported PKM 20 format: " + internalFormat);
        }
        if (!Texture.isCompressedTextureFormatSupported(texture.glInternalFormat))
        {
            throw new IOException(etc2CompressedInternalFormatsName[internalFormat] + "ETC1 is not supported by this device.");
        }

        ByteBuffer data = ReadData(is);
        int imageSize = data.remaining();
        GLES30.glCompressedTexImage2D(GLES30.GL_TEXTURE_2D, texture.level, texture.glInternalFormat, texture.width, texture.height, texture.border, imageSize, data);
        texture.lastglerror = GLES30.glGetError();
        if (texture.lastglerror != GLES30.GL_NO_ERROR)
        {
            throw new IOException("Create texture failed: " + GLU.gluErrorString(texture.lastglerror));
        }
    }

    private static ByteBuffer ReadData(InputStream is) throws IOException
    {
        try {
            byte[] ioBuffer = new byte[4096];
            ByteBuffer dataBuffer = ByteBuffer.allocateDirect(is.available()).order(ByteOrder.nativeOrder());
            int len;
            while ((len = is.read(ioBuffer)) != -1) {
                dataBuffer.put(ioBuffer, 0, len);
            }
            dataBuffer.position(0);
            return dataBuffer;
        }
        catch (IOException e)
        {
            throw new IOException("Read PKM file error:" + e.getMessage());
        }
    }
}
