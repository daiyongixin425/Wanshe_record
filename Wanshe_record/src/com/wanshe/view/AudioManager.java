package com.wanshe.view;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.R.integer;
import android.graphics.Path.Direction;
import android.media.MediaRecorder;
import android.text.GetChars;

public class AudioManager {
	private MediaRecorder mMediaRecorder;
	private String mDir;
	private String mCurrentFilePath;

	private boolean isPrepared; //

	private static AudioManager mInstance;

	/**
	 * 初始化接口完毕
	 * 
	 * @author daixiansen
	 * 
	 */
	public interface AudioStateListener {
		void wellPrepared();
	}

	public AudioStateListener mListener;

	public void setOnAudioStateListener(AudioStateListener stateListener) {
		mListener = stateListener;
	}

	private AudioManager(String dir) {
		mDir = dir;
	}

	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			mInstance = new AudioManager(dir);
		}
		return mInstance;
	}

	public void prepareAudio() {
		try {
			isPrepared = false;
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String fileName = generateFileName();
			File file = new File(dir, fileName);
			//獲取到音頻的絕對路勁
			mCurrentFilePath = file.getAbsolutePath();
			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			// 设置mRecorder音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频编码
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mMediaRecorder.prepare();
			mMediaRecorder.start();
			// 准备结束
			isPrepared = true;
			// 通知button可以录音了
			if (mListener != null) {
				mListener.wellPrepared();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String generateFileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepared) {
			try {
				// mRecorder.getMaxAmplitude() 1-32767
				return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {

			}
		}
		return 1;
	}

	/**
	 * 释放资源
	 */
	public void release() {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder = null;
	}

	
	public void cancel() {
		release();
		if(mMediaRecorder != null){
			File file = new File(mCurrentFilePath);
			file.delete();
			mMediaRecorder = null;
		}
	}
}
