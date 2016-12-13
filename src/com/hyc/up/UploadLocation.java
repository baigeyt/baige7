package com.hyc.up;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSS;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.extend.DataString;
import com.hyc.bean.MacEntity;
import com.hyc.db.DBMacAddress;

public class UploadLocation {
	OSS mOss;
	int mSchoolId;
	Context mContext;


	private DBMacAddress db = new DBMacAddress();
	private MacEntity macEntity;

	public LatLng defaultPoint = new LatLng(39.963175, 116.400244);
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	Double lat = 0.0;
	Double lon = 0.0;
	public UploadLocation(){
		macEntity = new MacEntity();
		db.creatDB_ID();
		macEntity = db.query_ID();
	}
	

	public void upLocation(Context mContext,OSS oss,int SchoolID) {

		mOss = oss;
		mSchoolId = SchoolID;
		this.mContext = mContext;
		
		initView(mContext);

		initLocation();

		mLocationClient.start();

	}

	/**
	 * 初始化控件
	 */
	private void initView(Context context) {
		mLocationClient = new LocationClient(context.getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

	}

	/**
	 * 初始化位置
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	/**
	 * 位置的监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if ((!lat.equals(location.getLatitude()))
					|| (!lon.equals(location.getLongitude()))) {
				lat = location.getLatitude();
				lon = location.getLongitude();
				if(lat!=4.9e-324&&lon!=4.9e-324){
					new AsyncTask<Void, Void, ArrayList<String>>(){

						@Override
						protected ArrayList<String> doInBackground(Void... arg0) {
							// TODO Auto-generated method stub
							// TODO Auto-generated method stub
							File dbf = new File(getDir() + "/baige/location");
							if (!dbf.exists()) {
								dbf.mkdirs();
							}

							String devices_ID = macEntity.getDevices();
							System.out.println("++++++++");
							System.out.println("........." + devices_ID);
							String date = DataString.StringData3();
							String time = DataString.StringData2();
							// 保存数据到本地
							String txtName = time;

							Log.e("lllll  ", txtName);
							File file_name = new File(getDir() + "/baige/location/"
									+ txtName + ".txt");
							try {
								if (!file_name.exists()) {
									// 在指定的文件夹中创建文件
									file_name.createNewFile(); 
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
//							String location_json = "{t:" + time + ",j:" + lon + ",w:" + lat
//									+ "}";
	//
//							FileOutputStream outputStream;
//							try {
//								outputStream = new FileOutputStream(file_name);
//								outputStream.write(location_json.getBytes());
//								outputStream.flush();
//								outputStream.close();
//							} catch (FileNotFoundException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} 
//							catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							
							String parment = "baige2e" + "/" + devices_ID + "/"
									+ mSchoolId + "/" + date + "/" + time + "/"+lon+"/"+lat;
							
							Log.v("经纬度参数",parment);
							
							ArrayList<String> list = new ArrayList<String>();
							list.add(file_name.getAbsolutePath());
							list.add(parment);
							return list;
						}

						@Override
						protected void onPostExecute(ArrayList<String> result) {
							// TODO Auto-generated method stub
							new OSSSample(mOss, result.get(0), result.get(1))
							.upLoadLocation();
							super.onPostExecute(result);
						}
						}.execute();
								
					Log.e("位置", "纬度：" + lat + "精度：" + lon);
					Toast.makeText(mContext, "纬度：" + lat + "精度：" + lon, Toast.LENGTH_SHORT).show();	
				}
			}
		}
	}

	/**
	 * 让百度地图的生命周期和activity相同
	 */ 

	public void clooseLocation() {

		if (mLocationClient != null) {
			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(myListener);
		}
	}

	private File getDir() {
		File dir = Environment.getExternalStorageDirectory();
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}

}
