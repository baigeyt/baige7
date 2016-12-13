package com.hyc.baige7;

import java.util.Timer;
import java.util.TimerTask;

import com.hyc.qidong.Reflesh;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {

	Login login = new Login();

	@Override
	public void onCreate() {
		super.onCreate();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				login.myFun(getApplicationContext());
				System.out.println("login.myFun()");
			}
		};
		timer.schedule(task, 0, 5400000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}