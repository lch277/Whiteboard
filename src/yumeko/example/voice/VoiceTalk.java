package yumeko.example.voice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

public class VoiceTalk {

	private static int numAudioSource 		= MediaRecorder.AudioSource.MIC;
	private static int numSampleRateInHz 		= 44100;
	private static int numChannelConfig 		= AudioFormat.CHANNEL_IN_MONO;
	private static int numAudioFormat 		= AudioFormat.ENCODING_PCM_16BIT;
	
	private int 			numBufferSizeInBytes;
	private AudioRecord 	mAudioRecode;
	private boolean 		isRecording;
	
	public VoiceTalk(){
		 
	}
	 
	public void test(){
		File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/test.pcm");
		
		if(file.exists()){
			file.delete();
		}
		
		try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create "
                    + file.toString());
        }
		
		try{
			FileOutputStream fos = new FileOutputStream(file);
			
            numBufferSizeInBytes = AudioRecord.getMinBufferSize(
                    numSampleRateInHz, numChannelConfig, numAudioFormat);
            mAudioRecode = new AudioRecord(numAudioSource, numSampleRateInHz,
                    numChannelConfig, numAudioFormat, numBufferSizeInBytes);
            
            byte[] buffer = new byte[numBufferSizeInBytes];
            
            mAudioRecode.startRecording();
            isRecording = true;
            
            while (isRecording) {
            	mAudioRecode.read(buffer, 0, numBufferSizeInBytes);
                fos.write(buffer);
            }
            
            mAudioRecode.stop();
            mAudioRecode.stop();
            mAudioRecode.release();// ÊÍ·Å×ÊÔ´
            mAudioRecode = null;
            fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
