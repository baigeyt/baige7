package com.hyc.baige7;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.SerialPort;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.baidu.mapapi.SDKInitializer;
import com.extend.CardMd5;
import com.extend.DataString;
import com.extend.DeleteFilePic;
import com.extend.GetFileNum;
import com.hyc.bean.Company;
import com.hyc.bean.ImgInfo;
import com.hyc.bean.MacEntity;
import com.hyc.bean.PictureId;
import com.hyc.bean.Stu;
import com.hyc.db.DBMacAddress;
import com.hyc.db.DBManagerCard;
import com.hyc.db.DBManagerCompany;
import com.hyc.db.DBManagerPicture;
import com.hyc.db.DBManagerStu;
import com.hyc.db.Db;
import com.hyc.network.GetDeviceID;
import com.hyc.network.IsNetWork;
import com.hyc.network.NetReceiver;
import com.hyc.network.NetReceiver.BRInteraction;
import com.hyc.qidong.Reflesh;
import com.hyc.rec.RecOneCard;
import com.hyc.rec.RecSchoolInfo;
import com.hyc.rec.ReceiveDeviceId;
import com.hyc.rec.ReceiveICCard;
import com.hyc.rec.RequestDataOSS;
import com.hyc.up.OSSSample;
import com.hyc.up.ReUpFile;
import com.hyc.up.UploadFile;
import com.hyc.up.UploadLocation;
import com.hyc.up.UploadRecord;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

public class MainActivity extends Activity implements 
		SurfaceHolder.Callback, android.view.View.OnClickListener {
	private static final int FLAG_HOMEKEY_DISPATCHED = 0;
	private Stu stu;
	private String name = null;
	private String classname = null;
	private String sex = "无";
	private Uri uri, uri1;
	private Bitmap mBitmap;
	private boolean isPreview = false;
	private boolean isInputStream = true;
	private OutputStream outputStreamSp;
	public  static String accesstoken;
	private Broad broad; 
	private boolean camera = false;
	private boolean school = true;
	private boolean hasdown = true;
	
	private Context context;
	private Db db;
	private SQLiteDatabase dbWriter, dbReader; 

	public static int type;
	public static long cardno;
	public static long physicsno;
	private int cardT = 0;
	private long tt = 4294967295L;
	private int upcount = 1;
	// ------------------------------------------
	public static double latitude = 39.9;
	public static double longitude = 116.3;

	private Timer timerAd, Screen_timer;
	private Bitmap two_bitmap;
	
	private ScreenBroadcastReceiver mScreenReceiver;

	// ------------------------------------------
	private SurfaceHolder holder = null;
	private SurfaceView mySurfaceView;
	private Camera myCamera;
	private InputStream inputStream = null;
	private File pictureFile;
	private File uploadfile;
	public static Intent intent;
	private View main;
	private ProgressDialog progressDialog;
	private ProgressDialog cameraDialog;

	private String sPort = "/dev/ttymxc2", sPortsp = "/dev/ttymxc3";
	private static final String netACTION="android.net.conn.CONNECTIVITY_CHANGE";
	private ReadThread readThread = null;
	private SerialPort serialPort, serialPortSp;
	private SerialHelper serialHelper;
	private StringBuilder sMsg = new StringBuilder();
	private DBManagerStu dbManagerstu;
	private DBManagerCard dbManagercard = new DBManagerCard();
	private DBManagerPicture dbpicture = new DBManagerPicture();
	private DBMacAddress dbMac;
	private TextView textConnect, textname, textcardno, textgrade, textsex,
			textschool;
	private ImageView imageView1, imageView2;
	private LinearLayout lvSurface;
	private View view;

	private float maxLight;
	private float currentLight;
	private Handler screen_handler;
	long denytime = 5 * 1000L;
	// private Window window;

	private WifiManager wm; // WifiManager在包 android.net.wifi.WifiManager中
	private WifiInfo wi; // WifiInfo在包android.net.wifi.WifiInfo中
	private int strength; // 信号强度
	private TextView textRSSI;
	public static final int FLAG_HOMEKEY_DISPATCHED1 = 0x80000000;

	DBManagerCompany dbCompany = new DBManagerCompany();
	Company company = new Company();

	// -----百度地图------
	UploadLocation up;

	// -------
	public static final String action = "jason.broadcast.action"; 
	private OSS oss;
	private int SchoolID = 0;
	private List<String> list = new ArrayList<String>();
	private IsNetWork work = new IsNetWork();
	private String parment;
	private ImgInfo mImginfo;
	public static TimerTask timerTask;
	private String serverAddress;
	public String picFilepath;
	
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
    
	//语音
	private SpeechSynthesizer mTts;
	private SharedPreferences mSharedPreferences;
	
	private static String TAG = "Iat_TtsDemo";
	
	private int shuaka = 0;
	public static int shangchuan = 0;
	private int flag =0;
	
	private Login login = new Login();
	private Timer timer,timeLoc;
	
	private OSSSample mOssSample;
	

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取SDK 版本
		SDKInitializer.initialize(getApplicationContext());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Toast.makeText(MainActivity.this, "锁屏期间请不要刷卡！", Toast.LENGTH_LONG).show();

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		
		main = getLayoutInflater().from(this).inflate(R.layout.activity_main,
				null);
		main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN);
		main.setOnClickListener(this);

		SpeechUtility.createUtility(this, "appid=54859360");
		
		getMac();
		setContentView(main);
		mScreenReceiver = new ScreenBroadcastReceiver();
		
		mOssSample = new OSSSample();
		
		up = new UploadLocation();
		String checkNetInt = getIntent().getStringExtra("checkNet");
		progressDialog = new ProgressDialog(MainActivity.this);
		cameraDialog = new ProgressDialog(MainActivity.this);
		//屏幕广播
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);
	    //检测网络
		if(checkNetInt!=null){
			if(checkNetInt.equals("1")){
				checkNetWork();
				checkNetInt=null;
			}
		}
		
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

		mSharedPreferences = getSharedPreferences("com.iflytek.setting",
				Activity.MODE_PRIVATE);

		textConnect = (TextView) this.findViewById(R.id.textConnect);
		textname = (TextView) this.findViewById(R.id.textname);
		textcardno = (TextView) this.findViewById(R.id.textcardno);
		textgrade = (TextView) this.findViewById(R.id.textgrade);
		textsex = (TextView) this.findViewById(R.id.textsex);
		textschool = (TextView) this.findViewById(R.id.textcname);
		textRSSI = (TextView) this.findViewById(R.id.textRSSI);

		lvSurface = (LinearLayout) findViewById(R.id.lvSurface);
		imageView1 = (ImageView) this.findViewById(R.id.imageView1);
		imageView2 = (ImageView) this.findViewById(R.id.imageView2);

		mySurfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);
		holder = mySurfaceView.getHolder();
		holder.setFormat(PixelFormat.TRANSLUCENT);

		holder.addCallback(MainActivity.this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		db = new Db(MainActivity.this);
		dbWriter = db.getWritableDatabase();
		dbReader = db.getReadableDatabase();
		dbWriter.delete("filepaths", null, null);
		dbWriter.delete("allpaths", null, null);
		dbManagerstu = new DBManagerStu(this);
		dbManagerstu.insertTwo("0");
		dbManagercard.creatDB();
		dbpicture.creatDB();
		
		new TimeThread().start();
//		Vector reup = GetFileName(getDir() + "/baige/picFile/", ".jpg");
//		if (reup.size() != 0) {
//			for (int i = 0; i < reup.size(); i++) {
//				File file = new File(getDir() + "/baige/picFile/"
//						+ reup.get(i));
//				DeleteFilePic.delete(file);
//			}
//		}

		// TODO
		try {
			serialPortSp = new SerialPort(new File(sPortsp), 115200, 0);
			outputStreamSp = serialPortSp.getOutputStream();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace(); 
		}

		/*
		 * intent = new Intent(MainActivity.this, MyService.class);
		 * startService(intent);
		 */

		initTimer();
		InitData();
		screen_Timer();
		setControls();

		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				dbCompany.creatDB();
				company = dbCompany.query();
				if(company.getName()!=null){
					Message wthInfomsg = new Message();
					wthInfomsg.what = 1;
					MainActivity.this.schoolHandler.sendMessage(wthInfomsg);
				}
			}
		};
		timer.schedule(task, 3000,1800000);
		
		timeLoc = new Timer();
		TimerTask taskLoc = new TimerTask() {
			@Override
			public void run() {
				if(dbMac.query_ID().getDevices()!=null){
					Log.e("准备上船", "gogogo");
					Message msg = new Message();
					msg.what = 3;
					schoolHandler.sendMessage(msg);
				}else{
					if(accesstoken!=null){
						new ReceiveDeviceId().mUploadData(accesstoken,dbMac);	
					}
				}
			}
		};
		timeLoc.schedule(taskLoc, 5000,5000);  

	}
	
	
	
	Handler schooleInfo = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				textschool.setText(RecSchoolInfo.schoolName); 
				break;
			case 2:
				//换学校后，更新数据，删除图片，请加入
				new DBManagerStu(MainActivity.this).delete();
				new DBManagerPicture().deleteAll();
				DeleteFilePic.deleteHe(new File(Environment
						.getExternalStorageDirectory()
						+ "/baige/headportrait/"));
				Log.e("在这下载", "下载数据");
			       MainActivity.intent = new Intent(MainActivity.this, MyService.class);
			       startService(MainActivity.intent); 
				break;
			case 3:
				MainActivity.intent = new Intent(MainActivity.this, MyService.class);
			       startService(MainActivity.intent);
				break;
			case 4:
				openControls();
//				cameraDialog.dismiss();
//				main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//						| View.SYSTEM_UI_FLAG_FULLSCREEN);
				Toast.makeText(MainActivity.this,"准备完毕，欢迎使用！", Toast.LENGTH_SHORT).show();
				break;
			case 5:
				//没换学校，日常更新数据
				new DBManagerStu(MainActivity.this).delete();
				 MainActivity.intent = new Intent(MainActivity.this, MyService.class);
			       startService(MainActivity.intent); 
				 break;
			}
		
		};
	};

	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector GetFileName1(String fileAbsolutePath, String form) {
		Vector vecFile = new Vector();
		File file = new File(fileAbsolutePath);
		if (file.exists() && file.isDirectory()) {
			if (file.listFiles().length > 0) {
				File[] subFile = file.listFiles();
				for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
					// 判断是否为文件夹
					if (!subFile[iFileLength].isDirectory()) {
						String filename = subFile[iFileLength].getName();
						// 判断是否为MP4结尾
						if (filename.trim().toLowerCase().endsWith(form)) {
							vecFile.add(filename);
						}
					}
				}
			}
		}
		return vecFile;
	}


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				textConnect.setText("已连接网络");
				break;
			case 2:
				textConnect.setText("未连接网络");
				break;
			case 66:
				try {
//					inputStream.notify();
    				lvSurface.setVisibility(View.VISIBLE);
    				myCamera = Camera.open();
    				myCamera.setPreviewDisplay(holder);
    				initCamera();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
				break;
			}

		}
	};
	
	Handler timeLimit = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				closeControls();
				break;
			case 2:
				openControls();
				break;
			}
		};
	};

	@SuppressLint("HandlerLeak")
	Handler schoolHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				textschool.setText(company.getName());

				SchoolID = company.getSchoolid();
				if (SchoolID != 0) {
					if (String.valueOf(SchoolID) != null) {
						getOSSCertificate();
					}
				}
				break;
			case 2:
				company = dbCompany.query();
				textschool.setText(company.getName());
				break;
			case 3:
				if(dbCompany.queryLocation()==1&&oss!=null&&SchoolID!=0){
					up.upLocation(MainActivity.this, oss, SchoolID);	 
				}
				break;
			}
		}
	};
	
	// --------------获取访问OSS的凭证----------------
	public void getOSSCertificate() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				list = new RequestDataOSS().uploadFileOss(MainActivity.this,
						String.valueOf(SchoolID));
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (work.isNetworkAvailable(MainActivity.this) && list.size() == 3) {

					OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
							list.get(0), list.get(1), list.get(2));

					oss = new OSSClient(MainActivity.this,
							"http://oss-bj.360baige.cn",
							credentialProvider);

//					Timer timer = new Timer();
//					TimerTask task = new TimerTask() {
//						@Override
//						public void run() {
//							new AsyncTask<Void, Void, Void>() {
//
//								@Override
//								protected Void doInBackground(
//										Void... params) {
//									list = new RequestDataOSS()
//											.uploadFileOss(
//													MainActivity.this,
//													String.valueOf(SchoolID));
//									return null;
//								}
//
//								@Override
//								protected void onPostExecute(Void result) {
//									new Thread() {
//										public void run() {
//											if (work.isNetWork()
//													&& list.size() == 3) {
//
//												OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
//														list.get(0),
//														list.get(1),
//														list.get(2));
//
//												oss = new OSSClient(
//														MainActivity.this,
//														"http://oss-bj.360baige.cn",
//														credentialProvider);
//
//											}
//										};
//									}.start();
//									super.onPostExecute(result);
//								}
//
//							}.execute();
//						}
//					};
//					timer.schedule(task, 120000, 1800000);
					try {
						String url = oss.presignConstrainedObjectURL(
								"sdk-baige", "logo.jpg", 3600);
						System.out
								.println(url
										+ "oooooooooooooooooooooookkkkkkkkkkkkk");
					} catch (ClientException e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
					}
				}
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		int i = main.getSystemUiVisibility();
		if (i == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
			main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		} else if (i == View.SYSTEM_UI_FLAG_VISIBLE) {
			main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		} else if (i == View.SYSTEM_UI_FLAG_LOW_PROFILE) {
			main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
	}

	private void setControls() {
		try {
			serialPort = new SerialPort(new File(sPort), 9600, 0);  
			System.out.println(serialPort);
			inputStream = serialPort.getInputStream();
			readThread = new ReadThread();
			readThread.start();	
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeControls(){
		
		if(readThread!=null){
			if(!readThread.isInterrupted()){
				readThread.interrupt();
				readThread=null;
			}
		}
	}
	
	private void openControls(){
		if(readThread==null){
			readThread = new ReadThread();
			readThread.start();	
		}
		}
	
	
	

	private void initTimer() {
		if (timerAd != null) {
			timerAd.cancel();
			timerTask.cancel();
			timerAd.purge();
			timerAd = null;
			timerTask = null;
		}
		timerAd = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if(work.isNetworkAvailable(MainActivity.this)){
					if(oss!=null){
						mHandler.sendEmptyMessage(1);
						if(school){
							RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
							recSchoolInfo.receiveSchoolInfoMain(schooleInfo,getMac(),MainActivity.this);
							school = false;
						}
						Log.e("二次", "定时器启动了");
						if(dbManagerstu.queryTwo()!=null){
							if(dbManagerstu.queryTwo().equals("0")){
								Log.e("二次", "二次上传启动了");
								two_compressPic();	
							}
						}	
					}
				}else{
					mHandler.sendEmptyMessage(2);
				}
			}

		};

		timerAd.schedule(timerTask, 0, 30000);
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("你好,你是谁？");
		builder.setTitle("检查拍照设备");
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.create().show();
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
			int h) {
		if(camera){
			
		}else{
			initCamera();
			System.out.println("surfaceChanged");	
		}
	}

	
	public void surfaceCreated(SurfaceHolder surfaceholder) {
       if(camera){
			
		}else{
			try {
				// setControls();
	        	   myCamera = Camera.open();
	   			System.out.println("aaaaaaaaaaaaaaaaa");   
	           
			} catch (Exception e) {
//				dialog();
			}	
		}
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		 if(camera){
				
			}else{
				if (null != myCamera) {
					myCamera.setPreviewCallback(null);

					CloseComPort(serialHelper);
					stopCamera();
					isPreview = false;
					myCamera.release();
					myCamera = null;
				}	
			}
	}


	private void initCamera() {
		if (isPreview) {
			stopCamera();
		}
		if (myCamera != null) {
			try {
				System.out.println("initCamera");
				Camera.Parameters parameters = myCamera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				parameters.setPictureSize(640, 480);
				parameters.setPreviewSize(640, 480);
				parameters.set("jpeg-quality",85);
				myCamera.setParameters(parameters);
				myCamera.setPreviewDisplay(holder);
				myCamera.startPreview();
				myCamera.autoFocus(myAutoFocusCallback);
				isPreview = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	ShutterCallback myShutterCallback = new ShutterCallback()

	{

		public void onShutter() {
			// TODO Auto-generated method stub
			System.out.println("myShutterCallback:onShutter...");

		}
	};

	@SuppressWarnings("deprecation")
	private void stopCamera() {
		if (myCamera != null) {
			try {
				myCamera.stopPreview();
				System.out.println("stopCamera");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// --------------------------------
	private void takePicture() {
		System.out.println("00000000000000000");
		System.out.println("takePicture");
		System.out.println(getSysNowTime());
		System.out.println(getSysNowTime());
		if (myCamera == null) {
			System.out.println("myCamera==null");
			try {
				myCamera = Camera.open();
				myCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			initCamera();
			takePicture();
		}
		if (myCamera != null) {
			myCamera.startPreview();
			myCamera.autoFocus(myAutoFocusCallback);
			myCamera.takePicture(myShutterCallback, null, myPicCallback);
			System.out.println("this is takePicture()");
		}
		/*
		 * if(stu.getName() == null){ stopCamera(); }
		 */
	}
	
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
        
        public void onAutoFocus(boolean success, Camera camera) {
            // TODO Auto-generated method stub
            if(success)//success表示对焦成功
            {
                Log.i("FOCUS", "myAutoFocusCallback: success...");
                //myCamera.setOneShotPreviewCallback(null);
                
            }
            else
            {
                //未对焦成功
                Log.i("NOFOCUS", "myAutoFocusCallback: 失败了...");

            }
                
            
        }
    };

	private PictureCallback myPicCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			System.out.println("myPicCallback");
			if (null != data) {
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				stopCamera();
				isPreview = false;
			}
			Matrix matrix = new Matrix();
			// matrix.postRotate((float) 180.0);
			matrix.postScale(-1, 1);
			Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0, 465, 465,
					matrix, false);

			String picFilename = CardMd5.GetMD5Code(String.valueOf(System
							.currentTimeMillis()))+System.currentTimeMillis();

			pictureFile = new File(Environment.getExternalStorageDirectory()
					+ "/baige/picFile/" + picFilename + ".jpg");
			picFilepath = pictureFile.getPath();
			BufferedOutputStream bos;
			try {
				bos = new BufferedOutputStream(
						new FileOutputStream(pictureFile));
				rotaBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
				bos.flush();
				bos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			isPreview = true;
			uri = Uri.fromFile(pictureFile);
			stopCamera();
//			initCamera();
			if(rotaBitmap!=null){
				rotaBitmap.recycle();
				rotaBitmap = null;
			}
			cardT = 0;
			System.out.println("8888888888888888888888888888888");
			if (name != null) {
				Message message = new Message();
				message.what = 1;
				MainActivity.this.handler.sendMessage(message);
				// speech();
				dbManagercard.insert(Long.toString(cardno));
			}
			Message message = new Message();
			message.what = 1;

			Bundle bundle = new Bundle();
			// bundle.putInt("type", nameClass.getType());
			bundle.putLong("cardno", cardno);
			bundle.putString("File", picFilepath);
			message.setData(bundle);

			MainActivity.this.upHandler.sendMessage(message);
			Log.e("刷卡数", ""+shuaka++);
		}
	};

	// TODO
	@SuppressLint("HandlerLeak")
	Handler upHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				textToSpeech(classname +","+ name);
				File file = new File(msg.getData().getString("File"));
				Uri uri = Uri.fromFile(file);
				ImgInfo imginfo = new ImgInfo();
				imginfo.setCardno(msg.getData().getLong("cardno"));
				imginfo.setType(msg.getData().getInt("type"));
				imginfo.setFile(file);
				mImginfo = imginfo;
				imageView2.setImageURI(uri);
				System.out.println("77777777777777777777777777777");
				System.out.println("imageView2");
				compressPic(imginfo);
				upcount = 0;
			}
		}
	};

	@SuppressWarnings({ "resource" })
	private void compressPic(final ImgInfo imginfo) {

		Log.e("sssss", "一次上传启动了");
//		Bitmap bitmap = null;
//		FileInputStream fis;
//		BufferedOutputStream stream;
		try {
//			fis = new FileInputStream(imginfo.getFile());
//			bitmap = BitmapFactory.decodeStream(fis);
//			stream = new BufferedOutputStream(new FileOutputStream(
//					imginfo.getFile()));
//			if (null == bitmap) {
//				// deleteFile(imginfo.getFile().getPath());
//				new OSSSample().copyFile(getDir() + "/baige/LOGOFile/0.jpg",
//						imginfo.getFile().getPath());
//				fis = new FileInputStream(imginfo.getFile());
//				bitmap = BitmapFactory.decodeStream(fis);
//				stream = new BufferedOutputStream(new FileOutputStream(
//						imginfo.getFile()));
//			}
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

			// TODO Auto-generated method stub
			try {
				if (oss != null) {
					String testObject = imginfo
							.getFile()
							.getPath()
							.substring(
									imginfo.getFile().getPath()
											.lastIndexOf("/") + 1,
									imginfo.getFile().getPath()
											.lastIndexOf("."));

					Date nowTime = new Date(System.currentTimeMillis());
					SimpleDateFormat sdFormatter = new SimpleDateFormat(
							"yyyyMMdd");
					String retStrFormatNowDate = sdFormatter.format(nowTime);

					String uploadFilePath = imginfo.getFile().getPath();

					parment = "baige2"
							+ "/"
							+ RecSchoolInfo.Id
							+ "/"
							+ retStrFormatNowDate
							+ "/"
							+ new CardMd5().GetMD5Code(testObject
									+ System.currentTimeMillis()) + ".jpg";

					System.out.println(parment + "wfqf");

					
					new OSSSample(MainActivity.this, uploadFilePath, oss,
							mHandler, imginfo, parment,dbWriter).upload();
				}else{
					mHandler.sendEmptyMessage(2);
					Log.e("断网启动", ""+flag++);
					ContentValues values = new ContentValues();
					values.put("type", imginfo.getType());
					values.put("cardno", imginfo.getCardno());
					values.put("alluploadpaths", imginfo.getFile()
							.getPath().substring(imginfo.getFile()
									.getPath().lastIndexOf("/")+1,imginfo.getFile()
									.getPath().length()));
					values.put("timecode",System.currentTimeMillis() / 1000 );
					dbWriter.insert("allpaths", null, values);
					File dir = new File(Environment.getExternalStorageDirectory()+"/baige/twoFile");
					if (dir.exists()) {
					} else {
						dir.mkdirs();
					}
					new UploadRecord().copyFile(imginfo.getFile()
							.getPath(), Environment.getExternalStorageDirectory()+"/baige/twoFile/"+imginfo.getFile()
							.getPath().substring(imginfo.getFile()
									.getPath().lastIndexOf("/")+1,imginfo.getFile()
									.getPath().length()));
					DeleteFilePic.delete(new File(imginfo.getFile()
							.getPath()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("tt", "http://" + "sdk-baige."
					+ "oss-cn-beijing.aliyuncs.com/" + serverAddress);

//			stream.flush();
//			stream.close();
//			if (bitmap != null) {
//				if (!bitmap.isRecycled()) {
//					bitmap.recycle();// 记得释放资源，否则会内存溢出
//				}
//			}
		} 
//		catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} 
		catch (NullPointerException e) {
			e.printStackTrace();
		} 
//		catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	//语音合成
	private void textToSpeech(String txt) {
		// TODO Auto-generated method stub
		// 设置合成
		mTts.setParameter(SpeechConstant.ENGINE_TYPE,
				SpeechConstant.TYPE_LOCAL);
		// 设置发音人 voicer为空默认通过语音+界面指定发音人。
		mTts.setParameter(SpeechConstant.VOICE_NAME, "");

		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE,
				mSharedPreferences.getString("stream_preference", "1"));// 0则为听筒
		int code = mTts.startSpeaking(txt, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
			Toast.makeText(MainActivity.this, "未安装离线包",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void two_compressPic() {
		dbManagerstu.insertTwo("1");
		ArrayList<String> reup = GetFileName1();
		System.out.println("待传人数" + reup.size());
		int i = 0;
		try {
			context = MainActivity.this;
//			Cursor c2 = dbReader.query("allpaths", new String[] {
//					"alluploadpaths", "type", "cardno" }, null, null, null,
//					null, null);
//			for (c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext()) {
           for(int j=0;j<reup.size();j++){
        	   i++;
        	ArrayList<String> listCode =  queryRecode(reup.get(j).substring(reup.get(j).lastIndexOf("/")+1, reup.get(j).length()));
        	String content = reup.get(j);
			int type = Integer.parseInt(listCode.get(1));
			long cardno = Long.parseLong(listCode.get(0));
			long time = Long.parseLong(listCode.get(2));
			
			ImgInfo info = new ImgInfo();
			info.setType(type);
			info.setCardno(cardno);
			System.out.println(content + "    .........dddddd.");
//			if (!content.equals(query(content))) {
				System.out.println(".....dddddd  22" + content);

//				ContentValues values = new ContentValues();
//				values.put("uploadpaths", content);
//				dbWriter.insert("filepaths", null, values);

				String testObject = content.substring(
						content.lastIndexOf("/") + 1,
						content.lastIndexOf("."));

				Date nowTime = new Date(System.currentTimeMillis());
				SimpleDateFormat sdFormatter = new SimpleDateFormat(
						"yyyyMMdd");
				String retStrFormatNowDate = sdFormatter.format(nowTime);

				Log.e("fffffff", String.valueOf(reup.size()));
				System.out.println("reup.get(i)==>" + content);

				String filePath = content;
				System.out.println("filePath==>" + filePath);

				parment = "baige2"
						+ "/"
						+ RecSchoolInfo.Id
						+ "/"
						+ retStrFormatNowDate
						+ "/"
						+ new CardMd5().GetMD5Code(testObject
								+ System.currentTimeMillis()) + ".jpg";

				if (oss != null) {
					Log.e("ddd11", "6666666666666666666");
					Log.e("ddd22", info.getCardno() + "");
					Log.e("ddd33", parment);
					Log.e("ddd44", filePath);
					new OSSSample(MainActivity.this, filePath, oss,
							mHandler, info, parment,dbWriter).sycupload(time);
				}
				if(j==reup.size()-1){
					dbManagerstu.insertTwo("0");
				}
           }
           if(i==0){
        	   dbManagerstu.insertTwo("0");
           }
			


//				}

			
//			}
//			}
//			if(c2.isAfterLast()||i==0){
//				dbManagerstu.insertTwo("0");
//			}
//			c2.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		reup.clear();

	}

	// ----------------------------------
	private void CloseComPort(SerialHelper ComPort) {
		if (ComPort != null) {
			ComPort.stopSend();
			ComPort.close();
			System.out.println("close");
		}
	}

	private class ReadThread extends Thread {
		// private Handler handler;
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (inputStream == null)
						return;
					size = inputStream.read(buffer);

					/*
					 * if (size > 0) { byte[] bRec = null; bRec = new
					 * byte[size]; for (int i = 0; i < size; i++) { bRec[i] =
					 * buffer[i]; } sMsg.append(MyFunc.ByteArrToHex(bRec));
					 * Log.e("--sMsg--", sMsg.toString()); if (sMsg.length() >
					 * 10) { String frcardno = MainActivity.fromCardno(sMsg
					 * .toString()); System.out.println(frcardno);
					 */
					if (size > 0) {
						byte[] bRec = null;
						bRec = new byte[size];
						for (int i = 0; i < size; i++) {
							bRec[i] = buffer[i];
						}
						sMsg.append(MyFunc.ByteArrToHex(bRec));
						Log.e("--sMsg--", sMsg.toString());
						if (sMsg.length() == 30) {
							String frcardno = MainActivity.fromCardno(sMsg
									.toString().substring(12,
											sMsg.toString().length() - 6));
							System.out.println(frcardno);
							read(frcardno);

							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat sdFormatter = new SimpleDateFormat(
									"yyyy-MM-dd");

							String cardTime1 = sdFormatter.format(nowTime);
							CardMd5 cardMd5 = new CardMd5();

							if (frcardno != null) {
								serverAddress = SchoolID
										+ "/"
										+ cardTime1
										+ "/"
										+ cardMd5.GetMD5Code(frcardno
												+ nowTime.toString());
							}

							sMsg = new StringBuilder();
						} else if (sMsg.length() > 30) {

							String frcardno = MainActivity.fromCardno(sMsg
									.toString().substring(
											sMsg.toString().length() - 30));
							String frcardno1 = frcardno.substring(12,
									frcardno.length() - 6);
							System.out.println(frcardno);
							System.out.println(frcardno1);
							// take = 2;
							read(frcardno1);

							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat sdFormatter = new SimpleDateFormat(
									"yyyy-MM-dd");

							String cardTime1 = sdFormatter.format(nowTime);
							CardMd5 cardMd5 = new CardMd5();

							if (frcardno1 != null) {
								serverAddress = SchoolID
										+ "/"
										+ cardTime1
										+ "/"
										+ cardMd5.GetMD5Code(frcardno1
												+ nowTime.toString());
							}

							sMsg = new StringBuilder();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private void read(String sMsg) {

//		timerAd.cancel();
//		initTimer();
		Screen_timer.cancel();
		screen_Timer();
		if (cardT == 0) {
			physicsno = Long.parseLong(sMsg, 16);
			cardno = tt - physicsno;
			System.out.println(cardno);
			System.out.println("99999999999999999999999999999999");
			cardT = 1;
			dbManagercard.openDB();
			if (!(Long.toString(cardno).equals(dbManagercard.query(Long
					.toString(cardno))))) {
				Log.e("MainActivity", physicsno + "\r\n" + cardno);
				/*
				 * new Thread(new Runnable() {
				 * 
				 * @Override public void run() {
				 */
				Log.e("MainActivity", physicsno + "");
				stu = dbManagerstu.query(cardno + "");
				System.out.println("stu   " + stu);

				/*
				 * uri1 = Uri.fromFile(new File(Environment
				 * .getExternalStorageDirectory() + "/baige/headportrait/" +
				 * cardno + ".jpg"));
				 */
				name = stu.getName();
				System.out.println("name   " + name);
				classname = stu.getClassname();
				if (stu.getSex() == 1) {
					sex = "男";
				} else if (stu.getSex() == 2) {
					sex = "女";
				}
				type = stu.getType();
				if (name != null) {
					Log.e("666", "准备显示图片"+stu.getHeadportraitid());
					if (!(stu.getHeadportraitid().equals("0"))) {

						uri1 = Uri.fromFile(new File(Environment
								.getExternalStorageDirectory()
								+ "/baige/headportrait/" + cardno + ".jpg"));
					}
					takePicture();
				} else if (name == null) {
					new AsyncTask<Void, Void, Void>(){

						@Override
						protected Void doInBackground(Void... params) {
							// TODO Auto-generated method stub
							RecOneCard recOneCard = new RecOneCard();
							stu = recOneCard.receiveDate(physicsno);
							return null;
						
						}
						protected void onPostExecute(Void result) {
							name = stu.getName();
							classname = stu.getClassname();
							String headportraitid = stu.getHeadportraitid(); 
							// System.out.println(name);
							// System.out.println(classname);
							System.out.println(stu.getSex()+"uuuuuuuuuuuuussssssssssssssssssssqqqqqqqqqqqqqq");
							if (stu.getSex() == 1) {
								sex = "男";
							} else if (stu.getSex() == 2) {
								sex = "女";
							}
							type = stu.getType();
							if (name != null) {
								dbManagerstu.insert(stu);
								System.out.println(name);
								System.out.println(classname);
								System.out.println("666666" + stu.getHeadportraitid());

								Log.e("stu.getHeadportraitid()",
										stu.getHeadportraitid());
								
								if (!(stu.getHeadportraitid().equals("0"))) {

									uri1 = Uri
											.fromFile(new File(Environment
													.getExternalStorageDirectory()
													+ "/baige/headportrait/"
													+ cardno
													+ ".jpg"));
								}
								takePicture();
							} else if (name == null) {
								cardT = 0;
								Message msg1 = new Message();
								msg1.what = 1;
								MainActivity.this.showHandler.sendMessage(msg1);
							}
						
							
						
						};
						
					}.execute();
					/*
					 * if (!(stu.getHeadportraitid().equals("0"))) {
					 * 
					 * uri1 = Uri.fromFile(new File(Environment
					 * .getExternalStorageDirectory() + "/baige/headportrait/" +
					 * cardno + ".jpg")); }
					 */
				}
				/*
				 * } }).start();
				 */
				// takePicture();
				// cardT = 1;
			} else if (Long.toString(cardno).equals(
					dbManagercard.query(Long.toString(cardno)))) {
				cardT = 0;
				Message msg2 = new Message();
				msg2.what = 2;
				MainActivity.this.showHandler.sendMessage(msg2);
			}
		} else if (cardT == 1) {
			Message msg3 = new Message();
			msg3.what = 3;
			MainActivity.this.showHandler.sendMessage(msg3);
		}
	}

	public static byte[] fromHex(String hexString) throws NumberFormatException {
		hexString = hexString.trim();
		String s[] = hexString.split(" ");
		byte ret[] = new byte[s.length];
		for (int i = 0; i < s.length; i++) {
			ret[i] = (byte) Integer.parseInt(s[i], 16);
		}
		return ret;
	}

	// TODO
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (uri1 != null) {
					System.out.println("uri1  " + uri1.toString());
					imageView1.setImageURI(uri1);

				} else {
					imageView1.setImageDrawable(getResources().getDrawable(
							R.drawable.kongbai2));
				}
				textname.setText("姓名：" + name);
				textcardno.setText("卡号：" + Long.toString(cardno));
				textgrade.setText("班级：" + classname);
				textsex.setText("性别：" + sex);
				uri1 = null;
			}

		}
	};

	@Override
	protected void onDestroy() {
		if(up!=null){
			up.clooseLocation();	
		}
		// unregisterReceiver(mReceiver)
		if(broad!=null){
			unregisterReceiver(broad);	
		}
		if(mScreenReceiver!=null){
			unregisterReceiver(mScreenReceiver);
		}
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		if(timeLoc!=null){
			timeLoc.cancel();
			timeLoc = null;
		}
		if(timerAd!=null){
			timerAd.cancel();
			timerAd = null;
		}
		if(Screen_timer!=null){
			Screen_timer.cancel();
			Screen_timer = null;
		}
		if(db!=null){
			if(dbWriter!=null){
				dbWriter.close();
			}
			if(dbReader!=null){
				dbReader.close();
			}
			db.close();
		}
		if(dbCompany!=null){
			dbCompany.closeDBManagerCompany();
		}
		if(dbManagerstu!=null){
			dbManagerstu.closeDB();
		}
		if(dbMac!=null){
			dbMac.closeDBMac();
		}
		if(dbManagercard!=null){
			dbManagercard.closeDB();
		}
		if(dbpicture!=null){
			dbpicture.closeDB();
		}
		// if (readThread != null)
		// readThread.interrupt();
		//
		// serialPort.close();
		// serialPort = null;
		// cardT = 0;
		// sMsg = new StringBuilder();
		//
		// dbManagercard.closeDB();
		// dbManagerstu.closeDB();
		// finish();
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("确定要退出吗？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
//							stopService(intent);
							if (readThread != null) {
								readThread.interrupt();
								readThread = null;
								
							}
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (serialPort != null) {
								serialPort.close();
								serialPort = null;
								
							}
							if (dbManagercard != null && dbManagerstu != null
									&& dbpicture != null) {

							}
							finish();
							onDestroy();
							System.exit(0);
							System.out.println("�ر�------------------");
						}
					});
			builder.setNeutralButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			builder.show();
			return true;
		}else 	if(keyCode == KeyEvent.KEYCODE_APP_SWITCH)
		{
			return true;
		}else {

			return super.onKeyDown(keyCode, event);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler showHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ShowMessage("此卡无效，请检查您的IC卡");
				textToSpeech("此卡无效，请检查您的IC卡");
				break;
			case 2:
				ShowMessage("请勿重复刷卡");
				textToSpeech("请勿重复刷卡");
				break;
			case 3:
				ShowMessage("请稍后");
				textToSpeech("请稍后");
				break;
			default:
				break;
			}
		}
	};

	public static String fromCardno(String hexString) {
		hexString = hexString.trim();
		String s[] = hexString.split(" ");
		StringBuilder builder = new StringBuilder();
		for (int i = s.length - 1; i >= 0; i--) {
			builder.append(s[i]);
		}
		return builder.toString();
	}

	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector GetFileName(String fileAbsolutePath, String form) {
		Vector vecFile = new Vector();
		File file = new File(fileAbsolutePath);
		if (file.exists() && file.isDirectory()) {
			if (file.listFiles().length > 0) {
				File[] subFile = file.listFiles();
				for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {

					if (!subFile[iFileLength].isDirectory()) {
						String filename = subFile[iFileLength].getName();

						if (filename.trim().toLowerCase().endsWith(form)) {
							vecFile.add(filename);
						}
					}
				}
			}
		}
		return vecFile;
	}
	
	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList GetFileName1() {
		ArrayList<String> vector = new ArrayList<String>();
//		Cursor c1 = dbReader
//				.query("allpaths", new String[] { "alluploadpaths" }, null,
//						null, null, null, null);
//		for (c1.moveToFirst(); !c1.isAfterLast(); c1.moveToNext()) {
//			int msgColumn = c1.getColumnIndex("alluploadpaths"); 
//			String content = c1.getString(msgColumn);
//			vector.add(content);
//		}
//		c1.close();
		File file = new File(Environment.getExternalStorageDirectory()+"/baige/twoFile/");
		if(file.exists()){
			if(file.isDirectory()){
				File files[] = file.listFiles();
				if(files.length>0){
					for(int i =0;i<files.length;i++){
						vector.add(files[i].getPath());
					}
				}
			}
		}
		return vector;
	}

	private void ShowMessage(String sMsg) {
		Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
	}

	// ---------------------------------------------------


	public String getSysNowTime() {
		Date now = new Date();
		java.text.DateFormat format = new java.text.SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		String formatTime = format.format(now);
		return formatTime;
	}

	@SuppressLint("HandlerLeak")
	Handler Hand = new Handler() {
		public void handleMessage(Message msg) {
			System.out.println("-------------------------");
			switch (msg.what) {
			case 0:
				SetLight(MainActivity.this, -1);
				break;
			case 1:
				SetLight(MainActivity.this, 1);
				break;
			default:
				break;
			}
		}
	};

	private void screen_Timer() {

		Message msg = new Message();
		msg.what = 0;
		MainActivity.this.Hand.sendMessage(msg);
		Screen_timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				// if (currentLight == 1) {
				Message msg = new Message();
				msg.what = 1;
				MainActivity.this.Hand.sendMessage(msg);
				System.out.println("baghrieaghielaqghierqgpreio");
				// }
			}
		};
		Screen_timer.schedule(timerTask, 10000);
	}

	private void InitData() {
		screen_handler = new Handler(Looper.getMainLooper());
		maxLight = GetLightness(this);
	}

	private void SetLight(Activity context, int light) {
		currentLight = light;
		WindowManager.LayoutParams layoutParams = context.getWindow()
				.getAttributes();
		layoutParams.screenBrightness = (light / 255.0F);
		// layoutParams.screenBrightness = 0.5F;
		context.getWindow().setAttributes(layoutParams);
	}

	private float GetLightness(Activity context) {
		WindowManager.LayoutParams layoutParams = context.getWindow()
				.getAttributes();
		float light = layoutParams.screenBrightness;
		return light;
	}

	
	//语音播报
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				Toast.makeText(MainActivity.this, "初始化失败,错误码：" + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {

		}

		@Override
		public void onBufferProgress(int i, int i1, int i2, String s) {
			mPercentForBuffering = i;
		}

		@Override
		public void onSpeakPaused() {

		}

		@Override
		public void onSpeakResumed() {

		}

		@Override
		public void onSpeakProgress(int i, int i1, int i2) {
			mPercentForPlaying = i;
		}

		@Override
		public void onCompleted(SpeechError speechError) {

		}

		@Override
		public void onEvent(int i, int i1, int i2, Bundle bundle) {

		}
	};
	

	private void checkNetWork(){

		Toast.makeText(MainActivity.this, "如无法正常使用，请连接网络！", Toast.LENGTH_LONG).show();
	}
	
	
	
    @Override
    protected void onStart() {
    	registerUpdate();
    	super.onStart();
    }
    //更新数据广播
    private void registerUpdate(){
    	broad = new Broad();
		IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
	    filter.addAction("com.baige.ui.service");
		registerReceiver(broad, filter);// 注册Broadcast Receive
    }
    private class Broad extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getStringExtra("miss")!=null){
				if(intent.getStringExtra("miss").equals("2")){
					openControls();
					progressDialog.dismiss();	
				}
			}
			
			if(intent.getStringExtra("reflush")!=null){
				if(intent.getStringExtra("reflush").equals("1")){
					closeControls();
					progressDialog.dismiss();
		    		progressDialog = new ProgressDialog(MainActivity.this);
		    		progressDialog.setCanceledOnTouchOutside(false);
		            progressDialog.setCancelable(true);
	                progressDialog.setTitle("正在更新数据，请不要刷卡或退出！");
	                progressDialog.show();		
				}
			}
			
			if(intent.getStringExtra("Fail")!=null){
				if(intent.getStringExtra("Fail").equals("3")){
					openControls();
					progressDialog.dismiss();
		    		progressDialog = new ProgressDialog(MainActivity.this);
		            progressDialog.setCancelable(true);
	                progressDialog.setTitle("更新失败，请与开发人员联系！");
	                progressDialog.show();		
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
		    		progressDialog = new ProgressDialog(MainActivity.this);
		    		progressDialog.setCanceledOnTouchOutside(false);
		            progressDialog.setCancelable(true);
	                progressDialog.setTitle("正在更新图片，请不要退出！");
	                progressDialog.show();	
				}
			}
			if(intent.getStringExtra("picFail")!=null){
				if(intent.getStringExtra("picFail").equals("5")){
					Toast.makeText(MainActivity.this,"更新失败，您可能没有头像，联网后重试！", Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		
	}
    
    private String getMac(){

		dbMac = new DBMacAddress();
		dbMac.creatDB();
		dbMac.creatDB_ID();
		MacEntity macEntity = new MacEntity();
		macEntity = dbMac.query();
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
				dbMac.insert(accesstoken);	
			}
		}
		return accesstoken;
    }
    
    private class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(60000);
					if (work.isNetworkAvailable(MainActivity.this) && oss == null) {
						getOSSCertificate();
					}
					dbManagercard.delete();
					if(!progressDialog.isShowing()){
						if(hasdown){
							getNoDown();
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}
    
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            	if(camera){
//            		cameraDialog.dismiss();
//            		cameraDialog = new ProgressDialog(MainActivity.this);
//            		cameraDialog.setCanceledOnTouchOutside(false);
//            		cameraDialog.setCancelable(true);
//            		cameraDialog.setTitle("                           正在准备摄像头，请不要刷卡！\n                                     锁屏时请不要刷卡！");
//            		cameraDialog.show();
            		Toast.makeText(context, "正在准备摄像头，请不要刷卡！锁屏时不要刷卡！", Toast.LENGTH_LONG).show();
            		Message msg  = new Message();
            		msg.what = 66;
            		mHandler.sendMessageDelayed(msg, 3000);
            		Message ms = new Message();
        			ms.what = 4;
        			schooleInfo.sendMessageDelayed(ms, 4000);
        			camera = false;
        		}
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mHandler.removeMessages(66);
                schooleInfo.removeMessages(4);
                if(inputStream!=null){
//                	synchronized (inputStream) {
//    					try {
//    						inputStream.wait();
//    					} catch (InterruptedException e) {
//    						e.printStackTrace();
//    					}
//    				}		
                }
            	closeControls();
    			camera = true;
    			if(myCamera!=null){
    				myCamera.cancelAutoFocus();
        			myCamera.stopPreview();
        			myCamera.setZoomChangeListener(null);
        			myCamera.setFaceDetectionListener(null);
        			myCamera.setErrorCallback(null);
        			myCamera.release();
        			myCamera = null;
        			lvSurface.setVisibility(View.GONE);	
        			cardT = 0;
    			}
    			
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
             
            }
        }
    }
    
    private void getNoDown(){
    	hasdown = false;
    	ArrayList<String> listDBhead = dbpicture.query();
    	int i=0;
		for(;i<listDBhead.size();i++){
			if(noDownload(listDBhead.get(i))){
				if(dbpicture.queryHead(listDBhead.get(i))!=null){
					Log.e("66666662222", dbpicture.queryHead(listDBhead.get(i)));
					if(!dbpicture.queryHead(listDBhead.get(i)).equals("c")){
						PictureId picId = new PictureId();
						picId.setCardNo(listDBhead.get(i));
						picId.setPic_id(dbpicture.queryHead(listDBhead.get(i)));
						new ReceiveICCard(MainActivity.this).savePic(picId, MainActivity.this);
					}	
				}
			}
		}
		if(i==listDBhead.size()||i==0){
			hasdown = true;
		}
    }
    private boolean noDownload(String carNo) {
		ArrayList<String> listFileHead = new GetFileNum().getFiles(new File(Environment
				.getExternalStorageDirectory()
				+ "/baige/headportrait/"));
		if(listFileHead.size()>0){
			for(int j =0;j<listFileHead.size();j++){
				if(carNo.equals(listFileHead.get(j))){
					return false;
				}
			}	
		}
		return true;
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return false;
    }
    
    private ArrayList<String> queryRecode(String val){
    	ArrayList<String> list = new ArrayList<String>();
    	System.out.println(val);
		String cardq = "c";
		String type =  "b";
		String time =  "d";
		String[] columns = { "type","cardno","timecode"};
		String[] selectionArgs = { val };
		Cursor c = dbReader.query("allpaths", columns, "alluploadpaths=?",
				selectionArgs, null, null, null);
		while (c.moveToNext()) {
			cardq = c.getString(c.getColumnIndex("cardno"));
			type = c.getString(c.getColumnIndex("type"));
			time = c.getString(c.getColumnIndex("timecode"));
		}
		c.close();
		list.add(String.valueOf(cardq));
		list.add(String.valueOf(type));
		list.add(String.valueOf(time));
		return list;
    }
 
    

	/*
	 * 
	 * 
	 * public boolean dispatchTouchEvent(MotionEvent ev) { if (currentLight ==
	 * 1) { startSleepTask(); } return super.dispatchTouchEvent(ev); }
	 * 
	 * public void startSleepTask() { SetLight(this, (int) maxLight);
	 * screen_handler.removeCallbacks(sleepWindowTask);
	 * screen_handler.postDelayed(sleepWindowTask, denytime); }
	 * 
	 * public void stopSleepTask() {
	 * screen_handler.removeCallbacks(sleepWindowTask); }
	 * 
	 * Runnable sleepWindowTask = new Runnable() {
	 * 
	 * @Override public void run() { SetLight(MainActivity.this, 1); } };
	 */
}
