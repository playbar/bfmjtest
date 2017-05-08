package com.baofeng.mj.business.videoplayer.vrSurface;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public  class VrModel
{
 public enum ScreenType {
  TYPE_2D(1), TYPE_LR3D(3), TYPE_UD3D(2), TYPE_LR3DS(4);

  private int nCode;
  private ScreenType(int _nCode) {
   this.nCode = _nCode;
  }

  @Override
  public String toString() {
   return String.valueOf(this.nCode);
  }
  public static ScreenType valueOf(int value) {

   switch (value) {
    case 1:
     return TYPE_2D;
    case 2:
     return TYPE_UD3D;
    case 3:
     return TYPE_LR3D;
    case 4:
     return TYPE_LR3DS;
    default:
     return TYPE_2D;
   }
  }
  public int value() {
   return this.nCode;
  }
 }

 public enum ModelType {
  MODEL_RECT(1), MODEL_SPHERE(2), MODEL_BOX(3), MODEL_SPHERE180(4);
  private int nCode;

  private ModelType(int _nCode) {
   this.nCode = _nCode;
  }

  @Override
  public String toString() {
   return String.valueOf(this.nCode);
  }

  public static ModelType valueOf(int value) {

   switch (value) {
    case 1:
     return MODEL_RECT;
    case 2:
     return MODEL_SPHERE;
    case 3:
     return MODEL_BOX;
    case 4:
     return MODEL_SPHERE180;
    default:
     return MODEL_RECT;
   }
  }
  public int value() {
   return this.nCode;
  }
 }
   protected int mProgram;
    protected int muMVPMatrixHandle;
    protected int maPositionHandle;
    protected int maTexCoorHandle;
    protected int mUVRangeHandle;

    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer   mTexCoorBuffer;
   protected  float[][] mUVRangeBuffer = new float[2][4];
    protected ShortBuffer indexBuffer;

    protected float xAngle=0;
    protected float yAngle=0;
    protected float zAngle=0;
    protected int iCount=0;
    protected static double NV_PI = 3.14159265358979323846;
    protected ScreenType mScreenType;
    public  void initShader(String vertexShader, String fragmentShader){};
    public void setScreenType(ScreenType type)
    {
     mScreenType = type;
     if(type == ScreenType.TYPE_LR3D)
    {
     mUVRangeBuffer[0] = new float[]{0, 0, 0.5f, 1f};
     mUVRangeBuffer[1] = new float[]{0.5f ,0 ,1f ,1f};
     }else if(type == ScreenType.TYPE_UD3D)
      {
       mUVRangeBuffer[0] = new float[]{0,0,1f,0.5f};
       mUVRangeBuffer[1] = new float[]{0,0.5f,1f,1f};
      }
     else
     {
      mUVRangeBuffer[0] = new float[]{0,0,1f,1f};
      mUVRangeBuffer[1] = new float[]{0,0,1f,1f};
     }
    }

    public void setRatio(float ratio){}
    public  void drawSelf(int texId ,int eye){};
    public void setInitialAngle(int angle){};
    public  void release(){};

}
