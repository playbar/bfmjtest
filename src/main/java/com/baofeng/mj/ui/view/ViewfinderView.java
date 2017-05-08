package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.zxingutil.camera.CameraManager;
import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by hanyang on 2016/5/12.
 * 二维码扫描框
 */
public class ViewfinderView extends View {
    //刷新时间
    private final long ANIMATION_DELAY = 10L;
    //透明度
    private final int OPAQUE = 0xFF;
    //扫描框对应的边角长度
    private int screenLenth;
    //扫描框对应的边角宽度
    private final int CORNER_WIDTH = 10;
    //扫描线的高度
    private final int MIDDLE_LINE_WIDTH = 6;
    //扫描线距离边框的间距
    private final int MIDDLE_LINE_PADDING = 5;
    //扫描线扫描移动距离
    private final int SCAN_LENGTH = 5;
    //手机屏幕密度
    private float density;
    //画笔
    private Paint paint;
    //扫描线滑动的最顶端位置
    private int slideTop;
    //扫描线滑动的最底端位置
    private int slideBottom;
    //扫描的二维码
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private final int resultPointColor;
    private String tagText;
    boolean isFirst;//是否第一次

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        //像素转换dp
        screenLenth = (int) (20 * density);
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);
        tagText = LanguageValue.getInstance().getValue(context, "SID_WHEN_SCAN_2D_CODE");
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CameraManager cameraManager = CameraManager.get();
        if (cameraManager == null) {
            return;
        }
        Rect frame = cameraManager.getFramingRect();
        if (frame == null) {
            return;
        }
        //初始化扫描线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }
        //获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        //绘制扫描框外面的阴影部分，上、下、左、右
        canvas.drawRect(0, 0, width, frame.top, paint);//上
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);//左
        canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);//右
        canvas.drawRect(0, frame.bottom, width, height, paint);// 下
        if (resultBitmap != null) {
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            //扫描框边角
            paint.setColor(getResources().getColor(R.color.theme_main_color));
            int lineWid = CORNER_WIDTH / 2;
            canvas.drawRect(frame.left - lineWid, frame.top - lineWid, frame.left + screenLenth, frame.top + lineWid, paint);//左上横
            canvas.drawRect(frame.left - lineWid, frame.top - lineWid, frame.left + lineWid, frame.top + screenLenth, paint);//左上竖
            canvas.drawRect(frame.right - screenLenth, frame.top - lineWid, frame.right + lineWid, frame.top + lineWid, paint);//右上横
            canvas.drawRect(frame.right - lineWid, frame.top - lineWid, frame.right + lineWid, frame.top + screenLenth, paint);//右上竖
            canvas.drawRect(frame.left - lineWid, frame.bottom - lineWid, frame.left + screenLenth, frame.bottom + lineWid, paint);//左下横
            canvas.drawRect(frame.left - lineWid, frame.bottom - screenLenth, frame.left + lineWid, frame.bottom + lineWid, paint);//左下竖
            canvas.drawRect(frame.right - screenLenth, frame.bottom - lineWid, frame.right + lineWid, frame.bottom + lineWid, paint);//右下横
            canvas.drawRect(frame.right - lineWid, frame.bottom - screenLenth, frame.right + lineWid, frame.bottom + lineWid, paint);//右下竖
        }
        slideTop += SCAN_LENGTH;
        //滑动到底部重新顶部开始
        if (slideTop >= frame.bottom) {
            slideTop = frame.top;
        }
        Rect lineRect = new Rect();
        lineRect.left = frame.left - 20;
        lineRect.right = frame.right + 20;
        lineRect.top = slideTop - 60;
        lineRect.bottom = slideTop + 60;
        canvas.drawBitmap(((BitmapDrawable) (getResources().getDrawable(R.drawable.my_scan_lightline))).getBitmap(), null, lineRect, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(13 * density);
        paint.setAlpha(0x40);
        paint.setTypeface(Typeface.create("System", Typeface.BOLD));
        float textWidth = paint.measureText(tagText);
        canvas.drawText(tagText, (width - textWidth) / 2, (float) (frame.bottom + (float) 22 * density), paint);
        //只刷新扫描框内容
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }

    /**
     * 添加二维码特征点
     *
     * @param point
     */
    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }
}
