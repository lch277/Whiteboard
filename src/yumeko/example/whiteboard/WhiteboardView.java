package yumeko.example.whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class WhiteboardView extends View{

	public interface OnWhiteboardActionListener{
		public void onTouchStart(float x,float y);
		public void onTouchMove(float x,float y);
		public void onTouchEnd(float x,float y);
	}
	
	private OnWhiteboardActionListener mOnWhiteboardActionListener;
	
	private Bitmap mBackground;
	private Bitmap mContent;
	private Bitmap mScriptCache;
	
	private Canvas mScriptCanvas; 
	
	private Path  	mScriptPath;
	private float 	mScriptStartX;
	private float	mScriptStartY;
	
	private boolean mScriptDrawPending;
	private boolean mScriptPageChanged;
	
	private Paint mScriptPaint;
	
	
	public WhiteboardView(Context context) {
		super(context);
		
		mScriptPaint = new Paint();
		mScriptPaint.setAntiAlias(true);
		mScriptPaint.setDither(true);
		mScriptPaint.setColor(Color.BLACK);
		mScriptPaint.setStyle(Paint.Style.STROKE);
		mScriptPaint.setStrokeJoin(Paint.Join.BEVEL);
		mScriptPaint.setStrokeCap(Paint.Cap.SQUARE);
		mScriptPaint.setStrokeWidth(12);
		
		mScriptPath = new Path();
	}
	
	public WhiteboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WhiteboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void init(){
		
		mScriptPaint = new Paint();
		mScriptPaint.setAntiAlias(true);
		mScriptPaint.setDither(true);
		mScriptPaint.setColor(Color.BLACK);
		mScriptPaint.setStyle(Paint.Style.STROKE);
		mScriptPaint.setStrokeJoin(Paint.Join.BEVEL);
		mScriptPaint.setStrokeCap(Paint.Cap.SQUARE);
		mScriptPaint.setStrokeWidth(12);
		
		mScriptPath = new Path();
	}
	
	@Override
	public void onDraw(Canvas canvas){
		if(mScriptPaint==null){
			init();
		}
		canvas.drawColor(0xFFFFFFFF);
		drawBackground();
		drawContent();
		if(mScriptCache==null||mScriptDrawPending||mScriptPageChanged){
			drawPath();
		}
		canvas.drawBitmap(mScriptCache, 0, 0, mScriptPaint);
	}
	
	private void drawBackground(){
		
	}
	
	private void drawContent(){
		
	}
	
	private void drawPath(){
		if(mScriptCache==null){
            final int width = Math.max(1, getWidth());
            final int height = Math.max(1, getHeight());
            mScriptCache = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mScriptCanvas = new Canvas(mScriptCache);
		}
		mScriptCanvas.drawPath(mScriptPath, mScriptPaint);
		mScriptDrawPending = false;
	}
	
/*	@Override
	public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		
	}*/
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		float x = event.getX();
		float y = event.getY();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			mOnWhiteboardActionListener.onTouchStart(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			mOnWhiteboardActionListener.onTouchMove(x, y);
			break;
		case MotionEvent.ACTION_UP:
			mOnWhiteboardActionListener.onTouchMove(x, y);
			break;
		}
		
		return true;
	}
	
	public void setPathStart(float x,float y){
		mScriptPath.reset();
		mScriptPath.moveTo(x, y);
		mScriptStartX = x;
		mScriptStartY = y;
	}
	
	public void setPathMove(float x,float y){
		float distanceFromStartX = Math.abs(x - mScriptStartX);
		float distanceFromStartY = Math.abs(y - mScriptStartY);
		if (distanceFromStartX >= 4 || distanceFromStartY >= 4) {
			mScriptPath.quadTo(mScriptStartX, mScriptStartY, (x + mScriptStartX) / 2, (y + mScriptStartY) / 2);
			mScriptStartX = x;
			mScriptStartY = y;
		}

		mScriptDrawPending = true;
	}
	
	public void setPathEnd(float x,float y){
		mScriptPath.lineTo(mScriptStartX, mScriptStartY);
		mScriptPath.reset();

		mScriptDrawPending = true;
	}
	
	public void setOnWhiteboardActionListener(OnWhiteboardActionListener listener){
		mOnWhiteboardActionListener = listener;
	}

}
