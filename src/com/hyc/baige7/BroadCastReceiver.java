package com.hyc.baige7;

import java.util.Timer;
import java.util.TimerTask;

import com.hyc.qidong.Reflesh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class BroadCastReceiver extends BroadcastReceiver{
	static final String action_boot="android.intent.action.BOOT_COMPLETED";

	
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				if (intent.getAction().equals(action_boot)){
					Intent ootStartIntent=new Intent(context,Reflesh.class);
					ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(ootStartIntent);
				}
				
			}
		};
		timer.schedule(task, 20000);
	}

}
