package com.npu.carsecretary.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

/**
 * 震动帮助类
 * androidManifest.xml中加入 以下权限
 * <uses-permission android:name="android.permission.VIBRATE" />
 */
public class VibrateHelp {
	private static Vibrator vibrator;
	private static MediaPlayer mPlayer;
	
	/*
	 * 简单提示声
	 */
	public static void playSound(Context contex,int flag){
		AssetManager am = contex.getAssets();//获得该应用的AssetManager  
		String file = "";
		if(flag == 0){
			file = "recognition_start.mp3";
		}else if(flag == 1){
			file = "confirm.mp3";
		}else if(flag == 2){
			file = "cancel.mp3";
		}
	     try{  
	            AssetFileDescriptor fileDescriptor = am.openFd(file);  
	            mPlayer = new MediaPlayer();  
	            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                       fileDescriptor.getStartOffset(),
                       fileDescriptor.getLength());  
	            mPlayer.prepare(); //准备  
	        }  
	        catch(IOException e){  
	            e.printStackTrace();  
	        } 
	     mPlayer.start();
	}
	
	
	/**
	 * 简单震动
	 * @param context     调用震动的Context
	 * @param millisecond 震动的时间，毫秒
	 */
	@SuppressWarnings("static-access")
	public static void vSimple(Context context, int millisecond) {
		vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
		vibrator.vibrate(millisecond);
	}
	
	/**
	 * 复杂的震动
	 * @param context 调用震动的Context
	 * @param pattern 震动形式
	 * @param repeate 震动的次数，-1不重复，非-1为从pattern的指定下标开始重复
	 */
	@SuppressWarnings("static-access")
	public static void vComplicated(Context context, long[] pattern, int repeate) {
		vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, repeate);
	}
	
	/**
	 * 停止震动
	 */
	public static void stop() {
		if (vibrator != null) {
			vibrator.cancel();
		}
	}
}
