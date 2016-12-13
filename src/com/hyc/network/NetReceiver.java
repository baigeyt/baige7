package com.hyc.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetReceiver extends BroadcastReceiver {

	BRInteraction brInteraction;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			boolean isConnected = NetUtils.isNetworkConnected(context);
			System.out.println("网络状态：" + isConnected);
			System.out.println("wifi状态：" + NetUtils.isWifiConnected(context));
			System.out.println("移动网络状态：" + NetUtils.isMobileConnected(context));
			System.out.println("网络连接类型：" + NetUtils.getConnectedType(context));
			if (isConnected) {
				brInteraction.setText("已连接");
				Toast.makeText(context, "网络已连接！", Toast.LENGTH_LONG).show();
			} else {
				brInteraction.setText("已断开");
				Toast.makeText(context, "网络已断开！", Toast.LENGTH_LONG).show();
			}
		}
	}

	public interface BRInteraction {
		public void setText(String content);
	}

	public void setBRInteractionListener(BRInteraction brInteraction) {
		this.brInteraction = brInteraction;
	}
}
