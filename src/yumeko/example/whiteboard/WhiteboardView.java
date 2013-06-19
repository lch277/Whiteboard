package yumeko.example.whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WhiteboardView extends View{

	public interface OnWhiteboardActionListener{
		
	}
	
	public Bitmap mBackground;
	public Bitmap mContent;
	public Bitmap mScriptCache;
	
	public Paint mPaint;
	
	
	
	public WhiteboardView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public WhiteboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WhiteboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDraw(Canvas canvas){
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		
	}
	
	private void drawPath(){
		
	}

}
