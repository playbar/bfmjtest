package com.baofeng.mj.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.io.File;

/**
 * @author wanghongfang
 * 首页开屏图显示的view
 */
public class FlashView extends View {

	private int w = 0;
	private int h = 0;
	private int x = 0;
	private int y = 0;
	private Bitmap bitmap;
	private Context mContext;
	private Paint paint;
	public FlashView(Context context) {
		super(context);		
		mContext = context;
		init();
	}
	
	public FlashView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		DisplayMetrics metric = new DisplayMetrics();
		((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
		w = metric.widthPixels;
		h = metric.heightPixels;
		h = h-PixelsUtil.getStatusHeight(getContext());
	}
    public void setBitmapRes(int res){
		bitmap = BitmapFactory.decodeResource(getContext().getResources(),res);
		invalidate();
	}
	public void setBitmapFilePath(String path){
		File imgfile= new File(path);
		bitmap = BitmapFactory.decodeFile(imgfile.getPath());
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(paint==null){
			paint = new Paint();
			paint.setAntiAlias(true);
		}
		if(bitmap==null)
			return;
		Rect dst = new Rect(x,y, w, h);
		Rect ret = new Rect(0,0, bitmap.getWidth(), bitmap.getHeight());
		canvas.drawBitmap(bitmap, ret, dst, paint);
//		recyleBitmap();
	}

	/**
	 * 资源回收
	 */
	public void recyleBitmap() {
		if(bitmap!=null) {
			bitmap.recycle();
		}
	}
}
