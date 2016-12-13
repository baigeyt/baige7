package com.hyc.rec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hyc.bean.Company;
import com.hyc.bean.InterWeb;
import com.hyc.db.DBManagerCompany;
import com.hyc.db.DBManagerStu;
import com.hyc.qidong.Reflesh;


public class RecSchoolInfo {
	private String ic_String=null;
	private String time_String = null;
	private String main_String = null;
	public static int logoId=0;
	public static String schoolName=null;
	public static String qq = null;
	public static String mobile=null;
	public static String email=null;
	public static String province=null;
	public static String city=null;
	public static String district=null;
	public static String address=null;
	public static String content=null;
	public static String errcode=null;
	public static int Id=0;
	public static int appType = 0;

	private final static String LOGO_PATH = Environment
			.getExternalStorageDirectory() + "/baige/LOGOFile/0.jpg";
	InterWeb interWeb = new InterWeb();
	private DBManagerCompany dbcompany = new DBManagerCompany();
	private Company com = new Company();

	public void receiveSchInfo(Handler mHandler,Boolean noData) {
		URL five_url;
		try {
			five_url = new URL(interWeb.getURL_SchoolInfo());
			HttpURLConnection five_urlConnection = (HttpURLConnection) five_url
					.openConnection();
			five_urlConnection.setRequestMethod("GET");
			five_urlConnection.setReadTimeout(5000);
			five_urlConnection.setConnectTimeout(5000);

			try{
				if (five_urlConnection.getResponseCode() == 200) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									five_urlConnection.getInputStream()));
					String line;
					System.out.println(" 11111111111111111111111111111 ");
					System.out.println(" Contents of post request ");
					System.out.println(" 11111111111111111111111111111 ");
					while ((line = reader.readLine()) != null) {
						ic_String += line;
					}
					if(ic_String==null){
						Message msgCon = new Message();
						msgCon.what =3;
		    			mHandler.sendMessage(msgCon);
					}
						JSONObject jsonNow;
						try {
							jsonNow = new JSONObject(ic_String.substring(4))
							.getJSONObject("company");
							errcode = new JSONObject(ic_String.substring(4)).getString("errcode");
							if(!errcode.equals("0")){
								Message msgCon = new Message();
								msgCon.what =3;
				    			mHandler.sendMessage(msgCon);
							}else{
								String str = jsonNow.getString("logoid");
								if(str.equals("0") || str.equals("null")){
									System.out.println(str);
								}else{
								    JSONArray jsonarray = jsonNow.getJSONArray("logoid");
								    JSONObject ject = jsonarray.getJSONObject(0);
								    logoId = ject.getInt("id");
								}
								Id = jsonNow.getInt("id");
								schoolName = jsonNow.getString("name");
								qq = jsonNow.getString("qq");
								mobile = jsonNow.getString("mobile");
								email = jsonNow.getString("email");
								province = jsonNow.getString("province");
								city = jsonNow.getString("city");
								district = jsonNow.getString("district");
								address = jsonNow.getString("address");
								content = jsonNow.getString("content");	
								appType = jsonNow.getInt("apptype");
							}
						} catch (Exception e) {
							Message msgCon = new Message();
							msgCon.what =3;
			    			mHandler.sendMessage(msgCon);
							e.printStackTrace();
						}
				
				System.out.println(logoId + schoolName + qq + mobile + email
						+ province + city + district + address + content);
				dbcompany.creatDB();
				com = dbcompany.query();
					
				ic_String = null;
				      if(schoolName != null){
				    		if ( !schoolName.equals(dbcompany.queryName())||dbcompany.queryName()==null) {
				    			//鍙慼andler鍚姩鏈嶅姟
				    			Log.e("学校信息进来了", "呵呵呵");
				    			Message msgSta = new Message();
				    			msgSta.what =1;
				    			mHandler.sendMessage(msgSta);
								dbcompany.deleteSchoolInfo();
								com.setAppType(appType);
							
							    com.setSchoolid(Id);
									
								if(schoolName!=null){
									com.setName(schoolName);	
								}
								if(qq!=null){
									com.setQq(qq);
								}
								
								if(mobile!=null){
									com.setMobile(mobile);	
								}
								if(email!=null){
									com.setEmail(email);
								}
								if(province!=null){
									com.setProvince(province);	
								}
								if(city!=null){
									com.setCity(city);	
								}
								if(district!=null){
									com.setDistrict(district);
								}
								if(address!=null){
									com.setAddress(address);	
								}
								if(content!=null){
									com.setContent(content);	
								}
								dbcompany.insert(com);
							}else{
								dbcompany.deleteSchoolInfo();
								com.setAppType(appType);
							
							    com.setSchoolid(Id);
									
								if(schoolName!=null){
									com.setName(schoolName);	
								}
								if(qq!=null){
									com.setQq(qq);
								}
								if(mobile!=null){
									com.setMobile(mobile);	
								}
								if(email!=null){
									com.setEmail(email);
								}
								if(province!=null){
									com.setProvince(province);	
								}
								if(city!=null){
									com.setCity(city);	
								}
								if(district!=null){
									com.setDistrict(district);
								}
								if(address!=null){
									com.setAddress(address);	
								}
								if(content!=null){
									com.setContent(content);	
								}
								dbcompany.insert(com);
								if(noData){
									Message msgUp = new Message();
									msgUp.what =1;
					    			mHandler.sendMessage(msgUp);
								}else{
									Message msgInt = new Message();
					    			msgInt.what =2;
					    			mHandler.sendMessage(msgInt);
								}
								//鍙慼andler璺宠浆
							}
				      }	
					
					
				}else{
					Message msgCon = new Message();
					msgCon.what =3;
	    			mHandler.sendMessage(msgCon);
				}
			}catch(Exception e){
				Message msgCon = new Message();
				msgCon.what =3;
    			mHandler.sendMessage(msgCon);
			}
		} catch (Exception e) {
			Message msgCon = new Message();
			msgCon.what =3;
			mHandler.sendMessage(msgCon);
		}
	}
	
 public void receiveSchoolInfoMain(Handler mHandler,String macAddress,Context mContext){

		URL five_url;
		try {
			five_url = new URL("http://api.360baige.cn/company/info?accesstoken="+macAddress);
			HttpURLConnection five_urlConnection = (HttpURLConnection) five_url
					.openConnection();
			five_urlConnection.setRequestMethod("GET");
			five_urlConnection.setReadTimeout(5000);
			five_urlConnection.setConnectTimeout(5000);

			System.out.println(interWeb.getURL_SchoolInfoMain());
			if (five_urlConnection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								five_urlConnection.getInputStream()));
				String line;
				System.out.println(" 11111111111111111111111111111 ");
				System.out.println(" Contents of post request ");
				System.out.println(" 11111111111111111111111111111 ");
				while ((line = reader.readLine()) != null) {
					main_String += line;
				}
					JSONObject jsonNow;
					try {
						jsonNow = new JSONObject(main_String.substring(4))
						.getJSONObject("company");
						errcode = new JSONObject(main_String.substring(4)).getString("errcode");
						if(!errcode.equals("0")){
							Message msgCon = new Message();
							msgCon.what =3;
			    			mHandler.sendMessage(msgCon);
						}else{
							String str = jsonNow.getString("logoid");
							if(str.equals("0") || str.equals("null")){
								System.out.println(str);
							}else{
							    JSONArray jsonarray = jsonNow.getJSONArray("logoid");
							    JSONObject ject = jsonarray.getJSONObject(0);
							    logoId = ject.getInt("id");
							}
							Id = jsonNow.getInt("id");
							schoolName = jsonNow.getString("name");
							qq = jsonNow.getString("qq");
							mobile = jsonNow.getString("mobile");
							email = jsonNow.getString("email");
							province = jsonNow.getString("province");
							city = jsonNow.getString("city");
							district = jsonNow.getString("district");
							address = jsonNow.getString("address");
							content = jsonNow.getString("content");	
							appType = jsonNow.getInt("apptype");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
			System.out.println(logoId + schoolName + qq + mobile + email
					+ province + city + district + address + content+appType);
			dbcompany.creatDB();
			com = dbcompany.query();
				System.out.println("娣诲姞瀛︽牎淇℃伅鍒版暟鎹簱");
				
				main_String = null;
			      if(schoolName != null){
			    	    Message msgShow = new Message();
			    	    msgShow.what =1;
		    			mHandler.sendMessage(msgShow);
			    		if ( !schoolName.equals(dbcompany.queryName())||dbcompany.queryName()==null) {
			    			dbcompany.deleteSchoolInfo();
							com.setAppType(appType);
						
						    com.setSchoolid(Id);
								
							if(schoolName!=null){
								com.setName(schoolName);	
							}
							if(qq!=null){
								com.setQq(qq);
							}
							
							if(mobile!=null){
								com.setMobile(mobile);	
							}
							if(email!=null){
								com.setEmail(email);
							}
							if(province!=null){
								com.setProvince(province);	
							}
							if(city!=null){
								com.setCity(city);	
							}
							if(district!=null){
								com.setDistrict(district);
							}
							if(address!=null){
								com.setAddress(address);	
							}
							if(content!=null){
								com.setContent(content);	
							}
							dbcompany.insert(com);
							
							Message msgRe = new Message();
			    			msgRe.what =2;
			    			mHandler.sendMessage(msgRe);
						}else{
							dbcompany.deleteSchoolInfo();
							com.setAppType(appType);
						
						    com.setSchoolid(Id);
								
							if(schoolName!=null){
								com.setName(schoolName);	
							}
							if(qq!=null){
								com.setQq(qq);
							}
							
							if(mobile!=null){
								com.setMobile(mobile);	
							}
							if(email!=null){
								com.setEmail(email);
							}
							if(province!=null){
								com.setProvince(province);	
							}
							if(city!=null){
								com.setCity(city);	
							}
							if(district!=null){
								com.setDistrict(district);
							}
							if(address!=null){
								com.setAddress(address);	
							}
							if(content!=null){
								com.setContent(content);	
							}
							dbcompany.insert(com);
							if(getDate(macAddress, mHandler, mContext)){
								
							}else{
								Message msgRe = new Message();
				    			msgRe.what =3;
				    			mHandler.sendMessage(msgRe);
							};	
						}
			      }	
			}else{
				Message msgCon = new Message();
				msgCon.what =3;
 			    mHandler.sendMessage(msgCon);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
 }
 private boolean getDate(String macAddress,Handler mHandler,Context context){
		URL timeUrl;
		String date = null;
		try {
			timeUrl = new URL("http://api.360baige.cn/equipment/geteqcurrentdate?accesstoken="+macAddress);
			HttpURLConnection time_urlConnection = (HttpURLConnection) timeUrl 
					.openConnection();
			time_urlConnection.setRequestMethod("GET");
			time_urlConnection.setReadTimeout(5000);
			time_urlConnection.setConnectTimeout(5000);
			if(time_urlConnection.getResponseCode()==200){
				BufferedReader timrReader = new BufferedReader(
						new InputStreamReader(
								time_urlConnection.getInputStream()));
				String time;
				System.out.println(" 11111111111111111111111111111 ");
				System.out.println(" Contents of post request ");
				System.out.println(" 11111111111111111111111111111 ");
				while ((time = timrReader.readLine()) != null) {
					time_String += time;
				}
				Log.e("cuurenttime", time_String);
				if(time_String!=null){
					try {
						JSONObject timeObject = new JSONObject(time_String.substring(4));
						if(timeObject.getInt("errcode")==0){
							date = timeObject.getString("currentdate");
							 String upDate = new DBManagerStu(context).queryDate();
							 if(upDate==null){
					    			new DBManagerStu(context).insertDate(date);
					    			return true;
							 }else {
								 if(!date.equals(upDate)){
								 Message msgR = new Message();
					    			msgR.what =5;
					    			mHandler.sendMessage(msgR);
					    			new DBManagerStu(context).insertDate(date);
					    			return true;
							 } 
							 }
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
 }
}
