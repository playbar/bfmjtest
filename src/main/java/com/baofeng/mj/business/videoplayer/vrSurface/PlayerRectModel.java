package com.baofeng.mj.business.videoplayer.vrSurface;


import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Rect模型
 */
public class PlayerRectModel extends VrModel
{

    private final String vertexShaderCode =
           // "uniform mat4 uMVPMatrix;"+
            "uniform vec4 uvRange;"
           + "attribute vec2 vPosition;"
            + "attribute vec2 inputTextureCoordinate;"
            + "varying vec2 textureCoordinate;"
            + "void main()" + "{"
            + "gl_Position =vec4(vPosition,0,1) ;" //uMVPMatrix *
            +"textureCoordinate.x = mix (uvRange.x,uvRange.z , inputTextureCoordinate.x);"
            +"textureCoordinate.y = mix (uvRange.y,uvRange.w,  inputTextureCoordinate.y);"
            + "}";

    private final String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;"
            + "varying vec2 textureCoordinate;\n"
            + "uniform samplerExternalOES s_texture;\n"
            + "void main() {"
            + "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n"
            + "}";

    private int mProgram = 0;
    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    private static final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
    private int mDegree = 0;                                                // vertex

    static float vertexPos[] = { -1.0f, -1.0f,   1.0f, -1.0f,   1.0f, 1.0f,  -1.0f, 1.0f, };

   	static float uvPos[] = {0f,1f,  1.0f,1.0f,  1.0f,0.0f,  0.0f,0.0f };

    float[] mtx;

    public PlayerRectModel()
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexPos.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(vertexPos);
        mVertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(drawOrder);
        indexBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(uvPos.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = bb2.asFloatBuffer();
        mTexCoorBuffer.put(uvPos);
        mTexCoorBuffer.position(0);

        setScreenType(ScreenType.TYPE_LR3D);
        initShader(vertexShaderCode,fragmentShaderCode);

    }

    @Override
    public void setRatio(float ratio)
    {
        float[]  tPos = new float[vertexPos.length];

        int tarPos = 0;
        if(ratio > 1)
        {
            tarPos = 1;
            ratio = 1/ratio;
        }

        for(int i = 0 ;i< vertexPos.length ;i++)
        {
            if(i % 2 == tarPos)
                tPos[i] = vertexPos[i]* ratio;
            else
                tPos[i] = vertexPos[i];
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(tPos.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(tPos);
        mVertexBuffer.position(0);
    }

    public void initShader(String vertexShader, String fragmentShader)
    {
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mUVRangeHandle = GLES20.glGetUniformLocation(mProgram, "uvRange");
       // muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void setUVOffset(float x,float y)
    {

    }
    public void drawSelf(int texId ,int eye)
    {

        GLES20.glUseProgram(mProgram);
       // GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId);




        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, 2,
                GLES20.GL_FLOAT, false, 2*4, mVertexBuffer);

        GLES20.glUniform4f(mUVRangeHandle,mUVRangeBuffer[eye][0],mUVRangeBuffer[eye][1],mUVRangeBuffer[eye][2],mUVRangeBuffer[eye][3]);

        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2,
                GLES20.GL_FLOAT, false, 2*4, mTexCoorBuffer);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(maPositionHandle);
        GLES20.glDisableVertexAttribArray(maTexCoorHandle);
        GLES20.glDisableVertexAttribArray(mUVRangeHandle);
    }

    public void setRotation(int degree) {
        switch (degree) {
            case 0:
                uvPos = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 0.0f};
                break;
            case 90:
                uvPos = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 0.0f};
                break;
            case 180:
                uvPos = new float[]{1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f};
                break;
            case 270:
                uvPos = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 1.0f};
                break;
            default:
                return;
        }
        mDegree = degree;
    }
    private float[] transformTextureCoordinates(float[] coords, float[] matrix) {
        float[] result = new float[coords.length];
        float[] vt = new float[4];

        for (int i = 0; i < coords.length; i += 2) {
            float[] v = {coords[i], coords[i + 1], 0, 1};
            Matrix.multiplyMV(vt, 0, matrix, 0, v, 0);
            result[i] = vt[0];
            result[i + 1] = vt[1];
        }
        return result;
    }


    public void release() {
        GLES20.glDeleteProgram(mProgram);
    }
}
