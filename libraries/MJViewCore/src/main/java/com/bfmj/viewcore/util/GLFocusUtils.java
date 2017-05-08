package com.bfmj.viewcore.util;

import java.security.acl.Group;
import java.util.ArrayList;

import android.annotation.SuppressLint;

import com.baofeng.mojing.MojingSDK;
import com.bfmj.viewcore.interfaces.ICursorView;
import com.bfmj.viewcore.render.GLRect;
import com.bfmj.viewcore.render.GLScreenParams;
import com.bfmj.viewcore.view.GLCursorView;
import com.bfmj.viewcore.view.GLGroupView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLView;

/**
 * 
 * ClassName: GLFocusUtils <br/>
 * @author lixianke    
 * @date: 2015-3-9 下午1:52:08 <br/>  
 * description:
 */
public class GLFocusUtils {
	public static final int TO_LEFT = 0;
	public static final int TO_RIGHT = 1;
	public static final int TO_UP = 2;
	public static final int TO_DOWN = 3;
	public static final int TO_UNKNOWN = 4;
	public static float[] headView;

	private static float[] mTouchPadHeadView = null;
	
	public static boolean isOpenHeadControl = true;

	private static OnCursorDepthChangeListener mCursorDepthChangeListener;
	
	private static GLRectView mFocusedView;
	private static int mComputeTimes = 0;

	public static float[] getTouchPadHeadView() {
		return mTouchPadHeadView;
	}

	public static void setTouchPadHeadView(float[] touchPadHeadView) {
		GLFocusUtils.mTouchPadHeadView = touchPadHeadView;
	}
	
	/**
	 * 开启头控
	 * @author lixianke  @Date 2015-3-20 上午11:43:48
	 * @param 
	 * @return
	 */
	public static void openHeadControl(){
		isOpenHeadControl = true;		
	}
	
	/**
	 * 关闭头控
	 * @author lixianke  @Date 2015-3-20 上午11:43:48
	 * @param 
	 * @return
	 */
	public static void closeHeadControl(){
		isOpenHeadControl = false;
	}
	
	/**
	 * 获取拥有焦点的view索引
	 * @author lixianke  @Date 2015-3-20 上午11:45:07
	 * @param 
	 * @return 索引
	 */
	public static GLRectView getFocusedView(){
		return mFocusedView;
	}

	public static void setFousedView(GLRectView view){
//		mFocusedView = view;
	}

	public static void getEulerAngles(float[] eulerAngles, int offset) {
		getEulerAngles(headView, eulerAngles, offset);
	}
	
	@SuppressLint("FloatMath")
	public static void getEulerAngles(float[] headView, float[] eulerAngles, int offset) {
		if (offset + 3 > eulerAngles.length) {
			throw new IllegalArgumentException(
					"Not enough space to write the result");
		}
		float pitch = (float) Math.asin(headView[6]);
		float roll;
		float yaw;
		if (Math.sqrt(1.0F - headView[6] * headView[6]) >= 0.01F) {
			yaw = (float) Math.atan2(-headView[2], headView[10]);
			roll = (float) Math.atan2(-headView[4], headView[5]);
		} else {
			yaw = 0.0F;
			roll = (float) Math.atan2(headView[1], headView[0]);
		}
		
//		Log.d("video", "yaw = " + Math.toDegrees(-yaw) + "; pitch = " + Math.toDegrees(-pitch) + "; roll = " + Math.toDegrees(-roll));
		
		eulerAngles[(offset + 0)] = (-yaw);
		eulerAngles[(offset + 1)] = (-pitch);
		eulerAngles[(offset + 2)] = (-roll);
	}

	/**
	 * 获取实际坐标x值
	 * @param x
	 * @return
     */
	public static float getX(float x){
		return (x - GLScreenParams.getXDpi() / 2) / GLScreenParams.getXDpi() * GLScreenParams.getScreenWidth();
	}

	/**
	 * 获取实际坐标y值
	 * @param y
	 * @return
     */
	public static float getY(float y){
		return (GLScreenParams.getYDpi() / 2 - y) / GLScreenParams.getYDpi() * GLScreenParams.getScreenHeight();
	}

	private static int[] mCurosrPosition = new int[]{-1, -1};

	/**
	 * 获取当前的焦点位置
	 * @return 焦点位置
     */
	public static int[] getCursorPosition(){
		return mCurosrPosition;
	}

	public static int[] getCursorPosition(GLRectView view){
		float rate = GLScreenParams.getXDpi() / GLScreenParams.getScreenWidth();
		float[] outPos = new float[]{0, 0};
		float vx1 = getX(view.getLeft() + view.getX());
		float vy1 = getY(view.getTop() + view.getY());
		float vx2 = getX(view.getLeft() + view.getX() + view.getWidth());
		float vy2 = getY(view.getTop() + view.getY() + view.getHeight());
		float z = -view.getDepth();
		MojingSDK.DirectionalRadiaInRect(view.isCostomHeadView() ? view.getMatrixState().getVMatrix() : GLFocusUtils.headView, new float[]{vx1, vy1}, new float[]{vx2, vy2}, z, outPos);
		return new int[]{(int)(outPos[0] * rate), (int)(outPos[1] * rate)};
	}

	/**
	 * 处理焦点
	 * @author lixianke  @Date 2015-3-16 上午10:52:11
	 * @param headView 陀螺仪矩阵
	 * @param views View列表
	 * @return 
	 */
	public static void handleFocused(float[] headView, ArrayList<GLView> views){
		GLFocusUtils.headView = mTouchPadHeadView != null ? mTouchPadHeadView : headView;
		
		if (!isOpenHeadControl){
			return;
		}
		
		if (mComputeTimes < 10){
			mComputeTimes++;
			return;
		} else {
			mComputeTimes = 0;
		}

		float defualtDepth = GLScreenParams.getDefualtDepth();
		boolean hasFocused = false;

		float x1 = getX(0);
		float y1 = getY(0);

		float x2 = getX(GLScreenParams.getXDpi());
		float y2 = getY(GLScreenParams.getYDpi());

		float[] outPos = new float[]{0, 0};

		boolean isFoused = MojingSDK.DirectionalRadiaInRect(GLFocusUtils.headView, new float[]{x1, y1}, new float[]{x2, y2}, -defualtDepth, outPos);

		float rate = GLScreenParams.getXDpi() / GLScreenParams.getScreenWidth();

		mCurosrPosition[0] = (int)(outPos[0] * rate);
		mCurosrPosition[1] = (int)(outPos[1] * rate);

		for (int i = views.size() - 1; i >= 0; i--) {
			if (!(views.get(i) instanceof GLRectView)) {
				continue;
			}

			GLRectView v = (GLRectView) views.get(i);
			if (!v.isVisible() || !v.isHandleFocus() || v instanceof ICursorView || v.getParent() instanceof ICursorView) {
				continue;
			}

			float vx1 = getX(v.getLeft() + v.getX());
			float vy1 = getY(v.getTop() + v.getY());
			float vx2 = getX(v.getLeft() + v.getX() + v.getWidth());
			float vy2 = getY(v.getTop() + v.getY() + v.getHeight());
			float z = -v.getDepth();

			if (MojingSDK.DirectionalRadiaInRect(v.isCostomHeadView() ? v.getMatrixState().getVMatrix() : GLFocusUtils.headView, new float[]{vx1, vy1}, new float[]{vx2, vy2}, z, new float[2])){
				if (v.hasListeter()){
					hasFocused = true;
				} else if (getHasListenerParent(v) != null){
					v = getHasListenerParent(v);
					hasFocused = true;
				} else {
					hasFocused = false;
				}
				if (hasFocused && v != mFocusedView && v.isEnable()) {
					if (mFocusedView != null) {
						if (mFocusedView.isGrandParent(v)){ //  if v is mFocusedView's parent, first let v' child lost focus
							GLRectView child = ((GLGroupView)v).getFocusedChild();
							if (child != null){
								child.onFocusChange(TO_UNKNOWN, false);
							}
						} else if (!v.isGrandParent(mFocusedView)){ //  if mFocusedView is not v's parent
							mFocusedView.onFocusChange(TO_UNKNOWN, false);
						}
					}

					v.doRequestFocus();
					mFocusedView = v;
				}
				break;
			}
		}

		if (!hasFocused){
			lostAllViewFocus();
		}
	}

	public static void lostAllViewFocus(){
		if (mFocusedView != null){
			mFocusedView.onFocusChange(TO_UNKNOWN, false);
			GLRectView parent = mFocusedView.getParent();
			if (parent != null && parent instanceof GLGroupView){
				((GLGroupView)parent).lostParentFocus();
			}
			mFocusedView = null;
		}
	}

	private static GLGroupView getHasListenerParent(GLRectView view){
		GLGroupView parent = view.getParent();
		while (parent != null){
			if (parent.hasListeter()){
				return  parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	public static float[] getPosition(float[] headView, float depth){
		float x1 = getX(0);
		float y1 = getY(0);

		float x2 = getX(GLScreenParams.getXDpi());
		float y2 = getY(GLScreenParams.getYDpi());

		float[] outPos = new float[]{0, 0};

		boolean isFoused = MojingSDK.DirectionalRadiaInRect(headView, new float[]{x1, y1}, new float[]{x2, y2}, -depth, outPos);

		float rate = GLScreenParams.getXDpi() / GLScreenParams.getScreenWidth();
		outPos[0] = (int)(outPos[0] * rate);
		outPos[1] = (int)(outPos[1] * rate);
		return outPos;
	}
	
	/**
	 * 处理焦点
	 * @author lixianke  @Date 2017-3-21 上午9:52:11
	 * @param views View列表
	 * @return 
	 */
	public static void handleFocused(ArrayList<GLView> views){
		if (!isOpenHeadControl){
			return;
		}

		if (mComputeTimes < 2){
			mComputeTimes++;
			return;
		} else {
			mComputeTimes = 0;
		}

		float defualtDepth = GLScreenParams.getDefualtDepth();
		boolean hasFocused = false;

		float x1 = getX(0);
		float y1 = getY(0);

		float x2 = getX(GLScreenParams.getXDpi());
		float y2 = getY(GLScreenParams.getYDpi());

		float[] outPos = new float[]{0, 0};

		boolean isFoused = MojingSDK.DirectionalRadiaInRect(mTouchPadHeadView, new float[]{x1, y1}, new float[]{x2, y2}, -defualtDepth, outPos);

		float rate = GLScreenParams.getXDpi() / GLScreenParams.getScreenWidth();

		mCurosrPosition[0] = (int)(outPos[0] * rate);
		mCurosrPosition[1] = (int)(outPos[1] * rate);

		for (int i = views.size() - 1; i >= 0; i--) {
			if (!(views.get(i) instanceof GLRectView)) {
				continue;
			}

			GLRectView v = (GLRectView) views.get(i);
				if (!v.isVisible() || v instanceof ICursorView || v.getParent() instanceof ICursorView) {
				continue;
			}

			float vx1 = getX(v.getLeft() + v.getX()) * 3.0f / v.getDepth();
			float vy1 = getY(v.getTop() + v.getY()) * 3.0f / v.getDepth();
			float vx2 = getX(v.getLeft() + v.getX() + v.getWidth()) * 3.0f / v.getDepth();
			float vy2 = getY(v.getTop() + v.getY() + v.getHeight()) * 3.0f / v.getDepth();

			float px = (x1 + outPos[0]) * 3.0f / v.getDepth(), py = (y1 - outPos[1]) * 3.0f / v.getDepth();

			if (px > vx1 && px < vx2 && py < vy1 && py > vy2){
				if (v.hasListeter()){
					hasFocused = true;
				} else if (getHasListenerParent(v) != null){
					v = getHasListenerParent(v);
					hasFocused = true;
				} else {
					hasFocused = false;
				}
				if (hasFocused && v != mFocusedView && v.isEnable()) {
					if (mFocusedView != null) {
						if (mFocusedView.isGrandParent(v)){ //  if v is mFocusedView's parent, first let v' child lost focus
							GLRectView child = ((GLGroupView)v).getFocusedChild();
							if (child != null){
								child.onFocusChange(TO_UNKNOWN, false);
							}
						} else if (!v.isGrandParent(mFocusedView)){ //  if mFocusedView is not v's parent
							mFocusedView.onFocusChange(TO_UNKNOWN, false);
						}
					}

					v.doRequestFocus();
					mFocusedView = v;
				}
				break;
			}
		}

		if (!hasFocused){
			lostAllViewFocus();
		}
	}
	
	/**
	 * 处理焦点
	 * @author lixianke  @Date 2015-3-16 上午10:52:11
	 * @param views View列表
	 * @return 
	 */
	public static boolean handleFocused(int direction, GLRectView view, ArrayList<GLRectView> views){
		if (isOpenHeadControl){
			return false;
		}
		
		mFocusedView = view;
		
		GLRectView focusedView = null;
		float x = -10000;
		float y = -10000;
		float vx11 = 0;
		float vx12 = 0;
		float vy11 = 0;
		float vy12 = 0;
		float vx21 = 0;
		float vx22 = 0;
		float vy21 = 0;
		float vy22 = 0;
		
		for (int i = 0; i < views.size(); i++) {
			GLRectView v = views.get(i);
			
			if (!v.isFocusable() || !v.isVisible() || !v.isEnable()){
				continue;
			}
			
			vx11 = v.getLeft() + v.getX();
			vx12 = v.getLeft() + v.getX() + v.getWidth();
			vy11 = v.getTop() + v.getY();
			vy12 = v.getTop() + v.getY() + v.getHeight();
			
			switch (direction) {
				case TO_LEFT:
					if (view != null){
						vx21 = view.getLeft() + view.getX();
						vy21 = view.getTop() + view.getY();
						vy22 = view.getTop() + view.getY() + view.getHeight();
						if (vx12 <= vx21 && (vx12 > x || (vx12 == x && Math.abs(vy21 + vy22 - vy11 - vy12) < Math.abs(vy21 + vy22 - y)))){
							x = vx12;
							y = vy11 + vy12;
							focusedView = v;
						}
					} else {
						if (vx12 > x){
							x = vx12;
							focusedView = v;
						}
					}
					break;
				case TO_RIGHT:
					if (view != null){
						vx22 = view.getLeft() + view.getX() + view.getWidth();
						vy21 = view.getTop() + view.getY();
						vy22 = view.getTop() + view.getY() + view.getHeight();
						if (vx11 >= vx22 && ((vx11 < x || x == -10000) || (vx11 == x && Math.abs(vy21 + vy22 - vy11 - vy12) < Math.abs(vy21 + vy22 - y)))){
							x = vx11;
							y = vy11 + vy12;
							focusedView = v;
						}
					} else {
						if (vx11 < x || x == -10000){
							x = vx11;
							focusedView = v;
						}
					}
					break;
				case TO_UP:
					if (view != null){
						vx21 = view.getLeft() + view.getX();
						vx22 = view.getLeft() + view.getX() + view.getWidth();
						vy21 = view.getTop() + view.getY();
						if (vy12 <= vy21 && (vy12 > y || (vy12 == y && Math.abs(vx21 + vx22 - vx11 - vx12) < Math.abs(vx21 + vx22 - x)))){
							x = vx11 + vx12;
							y = vy12;
							focusedView = v;
						}
					} else {
						if (vy12 > y){
							y = vy12;
							focusedView = v;
						}
					}
					break;
				case TO_DOWN:
					if (view != null){
						vx21 = view.getLeft() + view.getX();
						vx22 = view.getLeft() + view.getX() + view.getWidth();
						vy22 = view.getTop() + view.getY() + view.getHeight();
						if (vy11 >= vy22 && ((vy11 < y || y == -10000) || (vy11 == y && Math.abs(vx21 + vx22 - vx11 - vx12) < Math.abs(vx21 + vx22 - x)))){
							x = vx11 + vx12;
							y = vy11;
							focusedView = v;
						}
					} else {
						if (vy11 > y || y == -10000){
							y = vy11;
							focusedView = v;
						}
					}
					break;
				default:
					break;
			}
		}
		
		if (focusedView != null && mFocusedView != focusedView){
			if (mFocusedView != null){
				mFocusedView.onFocusChange(direction, false);
			}
			focusedView.onFocusChange(direction, true);
			mFocusedView = focusedView;
			
			return true;
		}
		
		return false;
	}
	
	public static void setOnCursorDepthChangeListener(OnCursorDepthChangeListener listener){
		mCursorDepthChangeListener = listener;
	}
	
	public interface OnCursorDepthChangeListener {
		void onCursorDepthChange(float depth);
	}
}
