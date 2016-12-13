package com.hyc.qidong;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.extend.DeleteFilePic;
import com.extend.GetFileNum;
import com.hyc.baige7.BroadCastReceiver;
import com.hyc.baige7.Login;
import com.hyc.baige7.MainActivity;
import com.hyc.baige7.MyService;
import com.hyc.baige7.R;
import com.hyc.bean.Company;
import com.hyc.bean.InterWeb;
import com.hyc.bean.MacEntity;
import com.hyc.db.DBMacAddress;
import com.hyc.db.DBManagerCompany;
import com.hyc.db.DBManagerPicture;
import com.hyc.db.DBManagerStu;
import com.hyc.network.GetDeviceID;
import com.hyc.network.IsNetWork;
import com.hyc.rec.RecSchoolInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Reflesh extends Activity{
	private static final String netACTION="android.net.conn.CONNECTIVITY_CHANGE";
	private ProgressDialog progressDialog;
	public static int logoId=0;
	public static String schoolName=null;
	public static int qq=0;
	public static String mobile=null;
	public static String email=null;
	public static String province=null;
	public static String city=null;
	public static String district=null;
	public static String address=null;
	public static String content=null;
	public static int Id=0;
	public static String accesstoken;


	private final static String LOGO_PATH = Environment
			.getExternalStorageDirectory() + "/baige/LOGOFile/0.jpg";
	InterWeb interWeb = new InterWeb();
	private Company com = new Company();
	 DBMacAddress db;
	private Broad broad;
//	private NetworkStateReceiver nsr;
//	private Thread thread;
//	private Thread isNetWork;
	private boolean noDate = false;
	private DBManagerStu dbstu;
	private String in= null;
	private DBManagerCompany dbCompany;
	private DBManagerPicture dbMaPicture;
	Toast toast;
	Timer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.firstscreen);
		
		DeleteFilePic.deletelistFiles(new File(getDir() + "/baige/picFile"));
		DeleteFilePic.deletelistFiles(new File(getDir() + "/baige/twoFile"));
		
		dbCompany = new DBManagerCompany();
		dbMaPicture = new DBManagerPicture();
		dbCompany.creatDB();
		dbMaPicture.creatDB();
		
			String finishUp = new DBManagerStu(Reflesh.this).queryIntent();
			if(finishUp!=null){
				if(finishUp.equals("0")){
					new DBManagerStu(Reflesh.this).delete();
					dbMaPicture.deleteAll();
//				       DeleteFilePic.deleteHe(new File(Environment
//								.getExternalStorageDirectory()
//								+ "/baige/headportrait/"));
				}
			}
		
		
		RelativeLayout relat = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		relat.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);              
		toast  = Toast.makeText(Reflesh.this, "正在连接服务器，请耐心等待 ^_^！",Toast.LENGTH_LONG);
		timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
		progressDialog = new ProgressDialog(this);
		
		db = new DBMacAddress();
		db.creatDB();
		MacEntity macEntity = new MacEntity();
		macEntity = db.query();
		if (macEntity.getMac() != null) {
			System.out.println("mac:  " + macEntity.getMac()); 
			accesstoken = macEntity.getMac();
			System.out.println("从数据库获取MAC");
		}
		if (accesstoken == null) {
			GetDeviceID getDeviceID = new GetDeviceID();
			accesstoken = getDeviceID.getMacAddress();
			System.out.println("accesstoken  " + accesstoken);
			if(accesstoken!=null){
				db.insert(accesstoken);	
			}
		}

		System.out.println("Login 获取到的mac：  " + accesstoken);  
		
		//监听网络状态广播
//				nsr =new NetworkStateReceiver();
//				 IntentFilter filter = new IntentFilter();
//			     filter.addAction(netACTION);
//			     this.registerReceiver(nsr, filter);
		         super.onCreate(savedInstanceState);
		         
		        new AsyncTask<Void, Void, Boolean>(){

					@Override
					protected Boolean doInBackground(Void... params) {
						URL timeUrl;
						boolean netWork = false;
						try {
							timeUrl = new URL("http://api.360baige.cn/equipment/geteqcurrentdate?accesstoken="+accesstoken); 
							HttpURLConnection time_urlConnection = (HttpURLConnection) timeUrl  
									.openConnection();
							time_urlConnection.setRequestMethod("GET");
							time_urlConnection.setConnectTimeout(20000);
							time_urlConnection.setReadTimeout(20000);
							if(time_urlConnection.getResponseCode()==200){
								netWork = true;
							}

						} catch (Exception e) {
							netWork = false;
						}
						return netWork;
					}
					protected void onPostExecute(Boolean netWork) {

						Cursor cursor = new DBManagerStu(Reflesh.this).getReadableDatabase().query("stu", null, null, null, null, null,
				 				null);
				 		int i = 0;
				 		while (cursor.moveToNext()) {
				 			i++;
				 		}
				 		if (i==0) {
				 			noDate = true;
				 			if(netWork){
				 				new AsyncTask<Void, Void, Void>() {

									@Override
									protected Void doInBackground(
											Void... params) {
										RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
										recSchoolInfo.receiveSchInfo(mHandler,noDate);
										return null;
									}}.execute();
				 			}else{
				 				noDate = false;
				 				Log.e("yy", "network is inter!");
				 				if(toast!=null&&timer!=null){
				 			    	toast.cancel();
				 			    	timer.cancel();
				 			    	toast=null;
				 			    	timer=null;
				 			    }
				 		    	progressDialog.dismiss();
				 		    	AlertDialog.Builder builder = new Builder(Reflesh.this);
				 				builder.setTitle("提示");
				 				builder.setMessage("连接异常，请检查后重试！");
				 				builder.setPositiveButton("确定",
				 						new DialogInterface.OnClickListener() {

				 							@Override
				 							public void onClick(DialogInterface dialog, int which) {
				 								// TODO Auto-generated method stub
				 								
				 								dialog.dismiss();
				 								finish();
				 						}
				 						});
				 				builder.show();	
				 			}		
				 		}else{
				 			noDate = false;
				 			if(netWork){       
				            	   new AsyncTask<Void, Void, Void>() {

				   					@Override
				   					protected Void doInBackground(
				   							Void... params) {
				   						RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
				   						recSchoolInfo.receiveSchInfo(mHandler,noDate);
				   						return null;
				   					}}.execute();   
//								}
							
				 			}else{

									Intent mainIntent = new Intent(
											Reflesh.this, MainActivity.class);
									mainIntent.putExtra("checkNet", "1");
									Reflesh.this.startActivity(mainIntent);
									Reflesh.this.finish();	
//								}
							
				 			}
				 		}
					};
                	  
                  }.execute();
	}	
	@Override
	protected void onDestroy() {
	    if(toast!=null&&timer!=null){
	    	toast.cancel();
	    	timer.cancel();
	    	toast=null;
	    	timer=null;
	    }
		if(progressDialog!=null){
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}else{
				progressDialog=null;
			}
		}
	if(broad!=null){
		unregisterReceiver(broad);
	}
	if(dbCompany!=null){
		dbCompany.closeDBManagerCompany();	
	}
	if(db!=null){
		db.closeDBMac();
	}
	if(dbMaPicture!=null){
		dbMaPicture.closeDB();
	}
//	if(nsr!=null){
//		unregisterReceiver(nsr);
//	}
//	if(!thread.isInterrupted()){
//		thread.interrupt();
//		thread=null;
//	}
		super.onDestroy();
	}
	
	Handler  mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				//换学校或者新上学校时，更新数据和头像
			       new DBManagerStu(Reflesh.this).delete();
			       dbMaPicture.deleteAll();
			       DeleteFilePic.deleteHe(new File(Environment
							.getExternalStorageDirectory()
							+ "/baige/headportrait/"));
			       MainActivity.intent = new Intent(Reflesh.this, MyService.class);
			       startService(MainActivity.intent);
				break;
			case 2:
				Intent mainIntent = new Intent(
						Reflesh.this, MainActivity.class);
				Reflesh.this.startActivity(mainIntent);
				
				Reflesh.this.finish();
				break;
			case 3:
				if(toast!=null&&timer!=null){
			    	toast.cancel();
			    	timer.cancel();
			    	toast=null;
			    	timer=null;
			    }
				AlertDialog.Builder builder = new Builder(Reflesh.this);
				builder.setTitle("提示");
				builder.setMessage("连接异常，请检查后重试！");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								dialog.dismiss();
								finish();
						}
						});
				builder.show();
				break;
			}
		};
	};
	
	
	@Override
	protected void onStart() {
		
		broad = new Broad();
		IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
	    filter.addAction("com.baige.ui.service");
		registerReceiver(broad, filter);// 注册Broadcast Receiver
		super.onStart();
	}
	
	private class Broad extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getStringExtra("miss")!=null){
				if(intent.getStringExtra("miss").equals("2")){
					Intent mainIntent = new Intent(
							Reflesh.this, MainActivity.class);
					Reflesh.this.startActivity(mainIntent);
					
					Reflesh.this.finish();
				}
			}
			
			if(intent.getStringExtra("reflush")!=null){
				if(intent.getStringExtra("reflush").equals("1")){
					if(toast!=null&&timer!=null){
				    	toast.cancel();
				    	timer.cancel();
				    	toast=null;
				    	timer=null;
				    }
					progressDialog.dismiss();
		    		progressDialog = new ProgressDialog(Reflesh.this);
		    		progressDialog.setCanceledOnTouchOutside(false);
		            progressDialog.setCancelable(true);
	                progressDialog.setTitle("正在更新数据，请不要退出！");
	                progressDialog.show();		
				}
			}
			
			if(intent.getStringExtra("Fail")!=null){
				if(intent.getStringExtra("Fail").equals("3")){
					if(toast!=null&&timer!=null){
				    	toast.cancel();
				    	timer.cancel();
				    	toast=null;
				    	timer=null;
				    }
					MainActivity.intent = new Intent(Reflesh.this, MyService.class);
					stopService(intent);
					progressDialog.dismiss();
					AlertDialog.Builder builder = new Builder(Reflesh.this);
					builder.setTitle("提示");
					builder.setMessage("连接异常，请检查后重试！");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									dialog.dismiss();
									finish();
							}
							});
					builder.show();		
				}
			}
			
			if(intent.getStringExtra("progress")!=null){
				if(progressDialog.isShowing()){
					progressDialog.setMessage(intent.getStringExtra("progress"));
				}
			}
			
			if(intent.getStringExtra("uphead")!=null){
				if(intent.getStringExtra("uphead").equals("4")){
					progressDialog.dismiss();
		    		progressDialog = new ProgressDialog(Reflesh.this);
		    		progressDialog.setCanceledOnTouchOutside(false);
		            progressDialog.setCancelable(true);
	                progressDialog.setTitle("正在更新图片，请不要退出！");
	                progressDialog.show();	
				}
			}
			
			if(intent.getStringExtra("picFail")!=null){
				if(intent.getStringExtra("picFail").equals("5")){
					 MainActivity.intent = new Intent(Reflesh.this, MyService.class);
						stopService(intent);
					progressDialog.dismiss();
					Toast.makeText(Reflesh.this,"更新失败，您可能没有头像，联网后重试！", Toast.LENGTH_LONG).show();
					new DBManagerStu(Reflesh.this).insertIntent("1");
					Intent mainIntent = new Intent(
							Reflesh.this, MainActivity.class);
					Reflesh.this.startActivity(mainIntent);
					
					Reflesh.this.finish();	
				}
			}
		}
		
	}
	
//	private void updateData(){
//		Cursor cursor = new DBManagerStu(Reflesh.this).getReadableDatabase().query("stu", null, null, null, null, null,
// 				null);
// 		int i = 0;
// 		while (cursor.moveToNext()) {
// 			i++;
// 		}
// 		if (i==0) {
// 			noDate = true;
// 			if(netWork){
// 				new AsyncTask<Void, Void, Void>() {
//
//					@Override
//					protected Void doInBackground(
//							Void... params) {
//						RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
//						recSchoolInfo.receiveSchInfo(mHandler,noDate);
//						return null;
//					}}.execute();
// 			}else{
// 				noDate = false;
// 				Log.e("yy", "network is inter!");
// 				if(toast!=null&&timer!=null){
// 			    	toast.cancel();
// 			    	timer.cancel();
// 			    	toast=null;
// 			    	timer=null;
// 			    }
// 		    	progressDialog.dismiss();
// 		    	AlertDialog.Builder builder = new Builder(Reflesh.this);
// 				builder.setTitle("提示");
// 				builder.setMessage("连接异常，请检查后重试！");
// 				builder.setPositiveButton("确定",
// 						new DialogInterface.OnClickListener() {
//
// 							@Override
// 							public void onClick(DialogInterface dialog, int which) {
// 								// TODO Auto-generated method stub
// 								
// 								dialog.dismiss();
// 								finish();
// 						}
// 						});
// 				builder.show();	
// 			}		
// 		}else{
// 			noDate = false;
// 			if(netWork){       
//            	   new AsyncTask<Void, Void, Void>() {
//
//   					@Override
//   					protected Void doInBackground(
//   							Void... params) {
//   						RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
//   						recSchoolInfo.receiveSchInfo(mHandler,noDate);
//   						return null;
//   					}}.execute();   
//				}
//			
// 			}else{
//
//					Intent mainIntent = new Intent(
//							Reflesh.this, MainActivity.class);
//					mainIntent.putExtra("checkNet", "1");
//					Reflesh.this.startActivity(mainIntent);
//					Reflesh.this.finish();	
//				}
//			
// 			}
// 		}
//	
//		
//		
//	}
	
	private File getDir() {

		File dir = Environment.getExternalStorageDirectory();
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		} else {

			return super.onKeyDown(keyCode, event);
		}
			
	
	}
    

}

