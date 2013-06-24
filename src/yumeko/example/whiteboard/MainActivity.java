package yumeko.example.whiteboard;

import yumeko.example.remote.Connection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity implements WhiteboardView.OnWhiteboardActionListener,Connection.OnActionReceiveListener{

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
		mConnection = new Connection(this);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mConnection.disConnect();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mConnection.connect("192.168.16.103");
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTouchStart(float x, float y) {
		mConnection.sendAction(ACTION_START,x,y);
		mWhiteboardView.setPathStart(x,y);
	}

	@Override
	public void onTouchMove(float x, float y) {
		mConnection.sendAction(ACTION_MOVE,x,y);
		mWhiteboardView.setPathMove(x,y);
		mWhiteboardView.invalidate();
	}

	@Override
	public void onTouchEnd(float x, float y) {
		mWhiteboardView.setPathEnd(x, y);
		mWhiteboardView.invalidate();
	}

	@Override
	public void onActionReceiveStart(float x, float y) {
		mWhiteboardView.setPathStart(x,y);
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onActionReceiveMove(float x, float y) {
		mWhiteboardView.setPathMove(x,y);
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onActionReceiveEnd(float x, float y) {
		mWhiteboardView.setPathEnd(x, y);
		mHandler.sendEmptyMessage(1);
	}
	
	Handler mHandler = new Handler(Looper.getMainLooper()){
		
		@Override
		public void handleMessage(Message msg){
			mWhiteboardView.invalidate();
		}
	};
}
