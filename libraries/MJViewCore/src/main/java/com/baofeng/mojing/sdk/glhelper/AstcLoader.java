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
public class AstcLoader {

    // ASTC texture compression internal formats. */
    public static final int GL_COMPRESSED_RGBA_ASTC_4x4_KHR          = 0x93B0;
    public static final int GL_COMPRESSED_RGBA_ASTC_5x4_KHR          = 0x93B1;
    public static final int GL_COMPRESSED_RGBA_ASTC_5x5_KHR          = 0x93B2;
    public static final int GL_COMPRESSED_RGBA_ASTC_6x5_KHR          = 0x93B3;
    public static final int GL_COMPRESSED_RGBA_ASTC_6x6_KHR          = 0x93B4;
    public static final int GL_COMPRESSED_RGBA_ASTC_8x5_KHR          = 0x93B5;
    public static final int GL_COMPRESSED_RGBA_ASTC_8x6_KHR          = 0x93B6;
    public static final int GL_COMPRESSED_RGBA_ASTC_8x8_KHR          = 0x93B7;
    public static final int GL_COMPRESSED_RGBA_ASTC_10x5_KHR         = 0x93B8;
    public static final int GL_COMPRESSED_RGBA_ASTC_10x6_KHR         = 0x93B9;
    public static final int GL_COMPRESSED_RGBA_ASTC_10x8_KHR         = 0x93BA;
    public static final int GL_COMPRESSED_RGBA_ASTC_10x10_KHR        = 0x93BB;
    public static final int GL_COMPRESSED_RGBA_ASTC_12x10_KHR        = 0x93BC;
    public static final int GL_COMPRESSED_RGBA_ASTC_12x12_KHR        = 0x93BD;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR  = 0x93D0;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x4_KHR  = 0x93D1;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x5_KHR  = 0x93D2;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x5_KHR  = 0x93D3;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x6_KHR  = 0x93D4;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x5_KHR  = 0x93D5;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x6_KHR  = 0x93D6;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x8_KHR  = 0x93D7;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x5_KHR = 0x93D8;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x6_KHR = 0x93D9;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x8_KHR = 0x93DA;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x10_KHR = 0x93DB;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x10_KHR = 0x93DC;
    public static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR = 0x93DD;

    public static boolean isAstcInternalFormat(int internalFormat)
    {
        return (((internalFormat >= GL_COMPRESSED_RGBA_ASTC_4x4_KHR) && (internalFormat <= GL_COMPRESSED_RGBA_ASTC_12x12_KHR)) ||
                ((internalFormat >= GL_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR) && (internalFormat <= GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR)));
    }

    private static final String[] guessStr = {
            "4x4.",
            "5x4.", "5x5.",
            "6x5.", "6x6.",
            "8x5.", "8x6.", "8x8.",
            "10x5.", "10x6.", "10x8.", "10x10.",
            "12x10.", "12x12."
    };

    private static final int[] guessFormat = {
            GL_COMPRESSED_RGBA_ASTC_4x4_KHR,
            GL_COMPRESSED_RGBA_ASTC_5x4_KHR, GL_COMPRESSED_RGBA_ASTC_5x5_KHR,
            GL_COMPRESSED_RGBA_ASTC_6x5_KHR, GL_COMPRESSED_RGBA_ASTC_6x6_KHR,
            GL_COMPRESSED_RGBA_ASTC_8x5_KHR, GL_COMPRESSED_RGBA_ASTC_8x6_KHR, GL_COMPRESSED_RGBA_ASTC_8x8_KHR,
            GL_COMPRESSED_RGBA_ASTC_10x5_KHR, GL_COMPRESSED_RGBA_ASTC_10x6_KHR, GL_COMPRESSED_RGBA_ASTC_10x8_KHR, GL_COMPRESSED_RGBA_ASTC_10x10_KHR,
            GL_COMPRESSED_RGBA_ASTC_12x10_KHR, GL_COMPRESSED_RGBA_ASTC_12x12_KHR
    };

    public static int GuessInternalformatFromName(String name)
    {
        for (int i=0; i<guessStr.length; i++)
        {
            if (name.contains(guessStr[i])){
                return guessFormat[i];
            }
        }
        return 0;
    }

    public static final int ASTC_HEADER_SIZE = 16;

    public static void LoadTextureFromStream(InputStream is, Texture texture) throws IOException {
        if (isAstcInternalFormat(texture.glInternalFormat) && Texture.isCompressedTextureFormatSupported(texture.glInternalFormat)) {
            byte[] headerBuffer = new byte[ASTC_HEADER_SIZE];
            if (is.read(headerBuffer, 0, ASTC_HEADER_SIZE) != ASTC_HEADER_SIZE) {
                throw new IOException("Unable to read ASTC file header.");
            }
            String headerId = new String(headerBuffer, 0, 4);
            if ((headerBuffer[0] != 0x13) || ((headerBuffer[1]&0xFF) != 0xAB) || ((headerBuffer[2]&0xFF) != 0xA1) || (headerBuffer[3] != 0x5C)) {
                throw new IOException("Not a ASTC file.");
            }

            // Merge x,y,z-sizes from 3 chars into one integer value.
            int xsize = (headerBuffer[7] & 0xff) + ((headerBuffer[8] & 0xff) << 8) + ((headerBuffer[9] & 0xff) << 16);
            int ysize = (headerBuffer[10] & 0xff) + ((headerBuffer[11] & 0xff) << 8) + ((headerBuffer[12] & 0xff) << 16);
            int zsize = (headerBuffer[13] & 0xff) + ((headerBuffer[14] & 0xff) << 8) + ((headerBuffer[15] & 0xff) << 16);

            // Compute number of blocks in each direction.
            int blockdim_x = headerBuffer[4] & 0xff;
            int blockdim_y = headerBuffer[5] & 0xff;
            int blockdim_z = headerBuffer[6] & 0xff;
            int xblocks = (xsize + blockdim_x - 1) / blockdim_x;
            int yblocks = (ysize + blockdim_y - 1) / blockdim_y;
            int zblocks = (zsize + blockdim_z - 1) / blockdim_z;

            // Each block is encoded on 16 bytes, so calculate total compressed image data size.
            int n_bytes_to_read = (xblocks * yblocks * zblocks) << 4;
            texture.width = xsize;
            texture.height = ysize;

            // Upload texture data to ES.
            if (is.available() < n_bytes_to_read) {
                throw new IOException("Broken ASTC file: not enough data.");
            }
            ByteBuffer data = ReadData(is);
            GLES20.glCompressedTexImage2D(GLES30.GL_TEXTURE_2D, texture.level, texture.glInternalFormat, xsize, ysize, texture.border, n_bytes_to_read, data);
            texture.lastglerror = GLES20.glGetError();
            if (texture.lastglerror != GLES20.GL_NO_ERROR) {
                throw new IOException("Create texture failed: " + GLU.gluErrorString(texture.lastglerror));
            }
        } else {
            throw new IOException("Not supported ASTC internal format: " + texture.glInternalFormat);
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
            throw new IOException("Read ASTC file error:" + e.getMessage());
        }
    }
}
