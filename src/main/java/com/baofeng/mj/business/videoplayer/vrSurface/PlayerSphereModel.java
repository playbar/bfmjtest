package com.baofeng.mj.business.videoplayer.vrSurface;


import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * 球模型
 */
public class PlayerSphereModel extends VrModel
{
    boolean mS180;//180度半球模
    int mInitialAngle = 0; //初始角
    public PlayerSphereModel(float radius,boolean s180)
    {
        mS180 = s180;
    	creatSphere(radius,mS180);
    	
    	String vertexShader =
                "uniform vec4 uvRange;" +
    			"uniform mat4 uMVPMatrix;" +
    			"attribute vec3 aPosition;" +
    			"attribute vec2 aTexCoor;" +
    			"varying vec2 vTextureCoord;" +
    			"void main()" +     
    			"{" +  			                          	
    			"   gl_Position = uMVPMatrix * vec4(aPosition,1);" +
                    "vTextureCoord.x = mix (uvRange.x,uvRange.z , aTexCoor.x);"+
                    "vTextureCoord.y = mix (uvRange.y,uvRange.w,  aTexCoor.y);"+
    			"}";
    			
    			String fragmentShader = "#extension GL_OES_EGL_image_external : require\n"+
    			"precision mediump float;" +
    			"uniform samplerExternalOES sTexture;" +
    			"varying vec2 vTextureCoord;" +
    			"void main()" +                         
    			"{" +
    			"   vec4 finalColor=texture2D(sTexture, vTextureCoord);" +
    			"   gl_FragColor = finalColor;" +
    			"}";
    			
    	initShader(vertexShader,fragmentShader);
    }
    
    public void creatSphere(float radius ,boolean s180) {
    	int segmentCount = 15;
        int hozSegmentCount = segmentCount * 4;
        int verSegmentCount = segmentCount * 2;
        
        // cos(theta) and sin(theta) in z-x plane
        ArrayList<Float> cosTheta = new ArrayList<Float>();
        ArrayList<Float> sinTheta = new ArrayList<Float>();
        
        double theta = NV_PI / 2;
        double thetaStep = NV_PI / (segmentCount * 2);

        if(s180)
            thetaStep = thetaStep/2;
        for (int i = 0; i < hozSegmentCount; i++, theta += thetaStep) {
            cosTheta.add((float)Math.cos(theta));
            sinTheta.add((float)Math.sin(theta));
        }

        if(s180)
        {
            cosTheta.add(cosTheta.get(hozSegmentCount -1));
            sinTheta.add(sinTheta.get(hozSegmentCount -1));
        }
        else
        {
            cosTheta.add(cosTheta.get(0));
            sinTheta.add(sinTheta.get(0));
        }

        // Angle in x-y plane
        double angle = (NV_PI / 2);
        double angleStep = NV_PI / verSegmentCount;
        
        // Save vertex data
        ArrayList<Float> vertexPos = new ArrayList<Float>();        
        // Save texture data
        ArrayList<Float> vertexTexCoord = new ArrayList<Float>();
        // Save normal data
        ArrayList<Float> vertexNormal = new ArrayList<Float>();
        
        for (int i = 0; i <= verSegmentCount; i++, angle -= angleStep) {
            float t = (float)i / verSegmentCount;
            double radiusInCrossSection;
            float y;

            if (i == 0) {
                radiusInCrossSection = 0;
                y = (float)radius;
            } else if (i == verSegmentCount) {
                radiusInCrossSection = 0;
                y = (float)-radius;
            } else {
                radiusInCrossSection = radius * Math.cos(angle);
                y = (float)(radius * Math.sin(angle));
            }

            for (int j = 0; j <= hozSegmentCount; j++) {
                float s = (float)(hozSegmentCount - j) / hozSegmentCount;
                vertexPos.add((float)(radiusInCrossSection * sinTheta.get(j)));
                vertexPos.add(y);
                vertexPos.add((float)(radiusInCrossSection * cosTheta.get(j)));

                vertexTexCoord.add(s);
                vertexTexCoord.add(t);
            }
        }
        
        float vertices [] = new float[vertexPos.size()];  
        for (int i = 0; i < vertexPos.size(); i++) {  
            vertices[i]=vertexPos.get(i);  
        }            
        
        float textures [] = new float[vertexTexCoord.size()];  
        for (int i = 0; i < vertexTexCoord.size(); i++) {  
        	textures[i]=vertexTexCoord.get(i);  
        }
     
      	ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
		        
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(textures);
        mTexCoorBuffer.position(0);

        ArrayList<Integer> alIndex = new ArrayList<Integer>();
        
        for (int row = 0; row < verSegmentCount; row++) {
            for (int col = 0; col < hozSegmentCount; col++) {
                int N10 = (int)((row + 1) * (hozSegmentCount + 1) + col);
                int N00 = (int)(row * (hozSegmentCount + 1) + col);
                
				alIndex.add(N00);
                alIndex.add(N10 + 1);
				alIndex.add(N10);
                

				alIndex.add(N00);
                alIndex.add(N00 + 1);
				alIndex.add(N10 + 1);
				
            }
        }
        
        iCount=alIndex.size();  
        short indices []=new short[iCount];  
        for (int i = 0; i < iCount; i++) {  
            indices[i]=alIndex.get(i).shortValue()  ;
        }  
        
        ByteBuffer ibb =  ByteBuffer.allocateDirect(iCount*2);
        
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer=ibb .asShortBuffer();
        indexBuffer.put(indices);  
        indexBuffer.position(0); 
    }   

    public void initShader(String vertexShader, String fragmentShader)
    {
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mUVRangeHandle = GLES20.glGetUniformLocation(mProgram, "uvRange");
    }
    
    public void drawSelf(int texId, int eye)
    {
    	 MatrixState.rotate(xAngle, 1, 0, 0);
    	 MatrixState.rotate(yAngle+mInitialAngle, 0, 1, 0);
    	 MatrixState.rotate(zAngle, 0, 0, 1);
    	
    	 GLES20.glUseProgram(mProgram);
         
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform4f(mUVRangeHandle,mUVRangeBuffer[eye][0],mUVRangeBuffer[eye][1],mUVRangeBuffer[eye][2],mUVRangeBuffer[eye][3]);
         
         GLES20.glVertexAttribPointer
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         GLES20.glVertexAttribPointer
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         
         GLES20.glEnableVertexAttribArray(maPositionHandle);
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);
         
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        /*绑定句柄*/
         GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId);
         
         GLES20.glDrawElements(
        		 GLES20.GL_TRIANGLES,
                 iCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }

    public void release() {
        GLES20.glDeleteProgram(mProgram);
    }

    /**
     * 设置初始角
     * @param angle
     */
    public void setInitialAngle(int angle){
        this.mInitialAngle = angle;
    }
}
