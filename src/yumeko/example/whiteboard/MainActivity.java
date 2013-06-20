package yumeko.example.whiteboard;

import yumeko.example.whiteboard.Connection.OnActionReceiveListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class MainActivity extends Activity implements WhiteboardView.OnWhiteboardActionListener,OnActionReceiveListener{

	public static final int ACTION_START = 1;
	public static final int ACTION_MOVE = 2;
	public static final int ACTION_END =3;
	
	WhiteboardView mWhiteboardView;
	Connection mConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWhiteboardView = (WhiteboardView) findViewById(R.id.whiteboardView);
		mWhiteboardView.setOnWhiteboardActionListener(this);
		Connection.setConnection("192.168.16.103",this);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(mConnection!=null){
			mConnection.close();
			mConnection = null;
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Connection.setConnection("192.168.16.103",this);
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		Connection.setConnection("192.168.16.103",this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTouchStart(float x, float y) {
		if(mConnection!=null){
			mConnection.sendAction(ACTION_START,x,y);
			System.out.println(">>>>>> send action");
		}else{
			System.out.println(">>>>>> mconnection ==null");
		}
		mWhiteboardView.setPathStart(x,y);
	}

	@Override
	public void onTouchMove(float x, float y) {
		if(mConnection!=null){
			mConnection.sendAction(ACTION_MOVE,x,y);
			System.out.println(">>>>>> send action");
		}else{
			System.out.println(">>>>>> mconnection ==null");
		}
		mWhiteboardView.setPathMove(x,y);
		mWhiteboardView.invalidate();
	}

	@Override
	public void onTouchEnd(float x, float y) {
		if(mConnection!=null){
			mConnection.sendAction(ACTION_END,x,y);
			System.out.println(">>>>>> send action");
		}else{
			System.out.println(">>>>>> mconnection ==null");
		}
		mWhiteboardView.setPathEnd(x, y);
		mWhiteboardView.invalidate();
	}

	@Override
	public void onActionReceiveStart(float x, float y) {
		mWhiteboardView.setPathStart(x,y);
	}

	@Override
	public void onActionReceiveMove(float x, float y) {
		mWhiteboardView.setPathMove(x,y);
		hander.sendEmptyMessage(1);
		
	}

	@Override
	public void onActionReceiveEnd(float x, float y) {
		mWhiteboardView.setPathEnd(x, y);
		hander.sendEmptyMessage(1);
	}


	
	Handler hander = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg){
			mWhiteboardView.invalidate();
		}
		
	};

	@Override
	public void removeListener() {
		mConnection = null;
	}
}
