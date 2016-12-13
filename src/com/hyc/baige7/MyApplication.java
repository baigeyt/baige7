package com.hyc.baige7;

import java.io.File;
import java.util.TimerTask;

import android.app.Application;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public class MyApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		File mainFile = new File(getDir()+"/baige");
		if(!mainFile.exists()){
			Log.d("tt", "创建了baige文件夹");
			mainFile.mkdirs();
		}
		File picFile = new File(getDir()+"/baige/picFile");
		if(!picFile.exists()){
			picFile.mkdirs();
		}
		File dataFile = new File(getDir()+"/baige/dataFile");
		if(!dataFile.exists()){
			dataFile.mkdirs();
		}
		File campusPicFile = new File(getDir()+"/baige/campusPicFile");
		if(!campusPicFile.exists()){
			campusPicFile.mkdirs();
		}
		File advertpFile = new File(getDir()+"/baige/advertPicFile");
		if(!advertpFile.exists()){
			advertpFile.mkdirs();
		}
		File advertvFile = new File(getDir()+"/baige/advertVideoFile");
		if(!advertvFile.exists()){
			advertvFile.mkdirs();
		}
		File headportrait = new File(getDir() + "/baige/headportrait");
		if (!headportrait.exists()) {
			Log.d("tt", "创建了headportrait文件夹");

			headportrait.mkdirs();
		}
		Time time = new Time();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				
			}
		};
	}
	
	private File getDir() {
		// �õ�SD����Ŀ¼
		File dir = Environment.getExternalStorageDirectory();
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}
}
