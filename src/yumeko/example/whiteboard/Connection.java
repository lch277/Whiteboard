package yumeko.example.whiteboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class Connection{

	public interface OnActionReceiveListener{
		public void onActionReceiveStart(float x,float y);
		public void onActionReceiveMove(float x,float y);
		public void onActionReceiveEnd(float x,float y);
		public void removeListener();
	}
	
	protected class ActionReceiver implements Runnable{

		BufferedReader			mBufferedReader;
		Connection				mConnection;
		
		ActionReceiver(InputStream inputStream,Connection connection){
			if(mBufferedReader!=null&&connection!=null){
				mBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				mConnection = connection;
			}
		}
		
		@Override
		public void run() {
			System.out.println(">>>>>> RUN"+new Throwable().getStackTrace()[0].getMethodName()+","+new Throwable().getStackTrace()[1].getMethodName()+" end");
			boolean closed = false;
			
			while(!closed){
				try {
					String action = mBufferedReader.readLine();
					mConnection.receiveAction(action);
				} catch (IOException e) {
					e.printStackTrace();
					closed = true;
					System.out.println(">>>>>> actionReceiver run error");
					mConnection.close();
				}
			}
		}
		
		void startListening(){
			if(mBufferedReader!=null){
				new Thread(this).start();
			}
			
		}
		
	}
	
	public final static int PORT = 9023;
	
	OnActionReceiveListener mOnActionReceiveListener;
	ServerSocket			mServerSocket;
	Socket  	  			mClientSocket;
	OutputStream			mOutputStream;
	InputStream				mInputStream;
	ActionReceiver			mActionReceiver;
	
	public void sendAction(int actionType,float x, float y) {
		if(mClientSocket!=null&&mOutputStream!=null){
			try {
				String action = actionType+"|"+x+"/"+y+"\n";
				mOutputStream.write(action.getBytes());
				mOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(">>>>>> sendAction IOException");
				close();
			}
		}
	} 
	
	public void receiveAction(String action){
		int firstIndexOfDivider = action.indexOf("|");
		int secondIndexOfDivider = action.indexOf("/");
		
		if(firstIndexOfDivider==-1||secondIndexOfDivider==-1||firstIndexOfDivider>=secondIndexOfDivider){
			return;
		}
		
		int actionType = Integer.parseInt(action.substring(0, firstIndexOfDivider));
		float x = Float.parseFloat(action.substring(firstIndexOfDivider+1,secondIndexOfDivider ));
		float y = Float.parseFloat(action.substring(secondIndexOfDivider+1,action.length() ));
		switch(actionType){
		case MainActivity.ACTION_START:
			mOnActionReceiveListener.onActionReceiveStart(x, y);
			break;
		case MainActivity.ACTION_MOVE:
			mOnActionReceiveListener.onActionReceiveMove(x, y);
			break;
		case MainActivity.ACTION_END:
			mOnActionReceiveListener.onActionReceiveEnd(x, y);
			break;
		}
	}
	
	public void close(){
		StackTraceElement[] elements = new Throwable().getStackTrace();
		System.out.println(">>>>>> close"+elements[1].getClassName()+"::"+elements[1].getMethodName()+elements[2].getClassName()+"::"+elements[2].getMethodName()+elements[3].getClassName()+"::"+elements[3].getMethodName());
		if(mClientSocket!=null){
			try {
				mClientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(mServerSocket!=null){
			try {
				mServerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mOnActionReceiveListener.removeListener();
	}

	
	public void setOnActionReceiveListener(OnActionReceiveListener listener){
		mOnActionReceiveListener = listener;
	}
	
	public static void setConnection(String IPAddress,MainActivity activity){
		new ConnectionFactory(IPAddress,activity).start();
	}
	
	public static Connection waitForConnection(){
		return new ConnectionAsServer();
	}
}

class ConnectionFactory implements Runnable{

	Connection mConnection;
	String mIPAddress;
	MainActivity mActivity;
	
	ConnectionFactory(String IPAddress,MainActivity activity){
		mIPAddress=IPAddress;
		mActivity = activity;
	}
	
	@Override
	public void run() {
		System.out.println(">>>>>> ConnectionFactory");
		mConnection = new ConnectionToServer(mIPAddress);
		mConnection.setOnActionReceiveListener(mActivity);
		mActivity.mConnection = mConnection;
		System.out.println(">>>>>> ConnectionFactory end"+mConnection);
	}
	
	void start(){
		new Thread(this).start();
	}
	
}


class ConnectionToServer extends Connection{

	ConnectionToServer(String serverURL){
		
		int count = 0;
		while(count<=50){
			System.out.println(">>>>>>try connection "+count);
			try {
				mClientSocket = new Socket(serverURL, PORT);
				mOutputStream = mClientSocket.getOutputStream();
				mInputStream = mClientSocket.getInputStream();
				mActionReceiver = new ActionReceiver(mInputStream,this);
				mActionReceiver.startListening();
				break;
			} catch (UnknownHostException e) {
				System.out.println("UnknownHostException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}

	}

	
}
	
class ConnectionAsServer extends Connection{

	ConnectionAsServer(){
		
	}
	 
}
