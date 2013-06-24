package yumeko.example.remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import yumeko.example.whiteboard.MainActivity;

public class Connection {
	
	public interface OnActionReceiveListener{
		public void onActionReceiveStart(float x,float y);
		public void onActionReceiveMove(float x,float y);
		public void onActionReceiveEnd(float x,float y);
	}
	
	public final static int PORT = 9023;
	
	OnActionReceiveListener mOnActionReceiveListener;
	ActionReceiver			mActionReceiver;
	RemoteInvocator			mRemoteInvocator;
	Socket					mClientSocket;
	
	boolean				isActived;
	
	public Connection(OnActionReceiveListener onActionReceiveListener){
		mOnActionReceiveListener = onActionReceiveListener;
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
	
	public void sendAction(int actionType,float x,float y){
		mRemoteInvocator.sendAction(actionType, x, y);
	}
	
	public void connect(String url){
		final String urlAddress = url;
		new Thread(){
			@Override
			public void run(){
				int count = 0;
				while(count<=50){
					System.out.println(">>>>>> try connection "+count);
					try {
						mClientSocket = new Socket(urlAddress, PORT);
						mActionReceiver = new ActionReceiver(Connection.this);
						mRemoteInvocator   = new RemoteInvocator(Connection.this);
						isActived = true;
						mActionReceiver.startListening();
						System.out.println(">>>>>> connection created"+count);
						break;
					} catch (UnknownHostException e) {
						System.out.println(">>>>>> UnknownHostException");
						//e.printStackTrace();
					} catch (IOException e) {
						System.out.println(">>>>>> IOException");
						//e.printStackTrace();
					}
					count++;
				}
			}
		}.start();
		
	}
	
	public void disConnect(){
		try {
			mClientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			mClientSocket = null;
			isActived = true;
			mActionReceiver = null;
			mRemoteInvocator = null;
		}
	}
	
	@Override
	public void finalize(){
		
	}
	
	public InputStream getInputStream(){
		try {
			return mClientSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public OutputStream getOutputStream(){
		try {
			return mClientSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public class RemoteInvocator{
		
		BufferedWriter mBufferedWriter;
		
		
		RemoteInvocator(Connection connection){
			mBufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		}
		
		public void sendAction(int actionType,float x, float y) {
			String action = actionType+"|"+x+"/"+y+"\n";
			new Thread(new Invocation(action)).start();
		} 
		
		class Invocation implements Runnable{
			
			String actionMessage;
			
			Invocation(String message){
				actionMessage = message;
			}
			
			@Override
			public void run() {		
				synchronized(mBufferedWriter){
					if(mBufferedWriter!=null&&isActived){
						try {
							mBufferedWriter.write(actionMessage);
							mBufferedWriter.flush();
						} catch (IOException e) {
							e.printStackTrace();
							isActived = false;
						}
					}
				}
			}
		}
		
	}
	
	public class ActionReceiver implements Runnable{

		private BufferedReader			mBufferedReader;
		
		ActionReceiver(Connection connection){
			InputStream inputStream= connection.getInputStream();
			if(inputStream!=null){
				isActived = true;
				mBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			}
		}
		
		@Override
		public void run() {		
			while(isActived){
				try {
					String action = mBufferedReader.readLine();
					receiveAction(action);
				} catch (IOException e) {
					System.out.println(">>>>>> actionReceiver run error");
					e.printStackTrace();
					isActived = false;
				}
			}
		}
		
		void startListening(){
			if(mBufferedReader!=null){
				new Thread(this).start();
			}
			
		}
		
	}
}
