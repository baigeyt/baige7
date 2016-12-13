package com.hyc.baige7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.hyc.bean.AppId;
import com.hyc.bean.Company;
import com.hyc.bean.MacEntity;
import com.hyc.db.DBMacAddress;
import com.hyc.db.DBManagerBase;
import com.hyc.db.DBManagerCompany;
import com.hyc.db.DBManagerStu;
import com.hyc.network.GetDeviceID;
import com.hyc.rec.RecSchoolInfo;
import com.hyc.rec.ReceiveICCard;

public class Login {
	private String appid = "";
	private String appsecret = "";
	private JSONObject jsonobject;
	private String errcode = "22";
	private Handler schoolHandler;
	public static String accesstoken = null;
	public static String rel_String;
	ReceiveICCard receiveICCard;
	int i = 0;
	DBManagerBase base = new DBManagerBase();
	AppId id = new AppId();
	DBMacAddress db;
	DBManagerStu dbManagerStu;
	SQLiteDatabase dbReader;

	public void myFun(Context context) {
		dbManagerStu = new DBManagerStu(context);
		dbReader = dbManagerStu.getReadableDatabase();


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
			if(accesstoken!=null){
				db.insert(accesstoken);	
			}
			System.out.println("accesstoken  " + accesstoken);
		}

		System.out.println("Login 获取到的mac：  " + accesstoken);
		Cursor cursor = dbReader.query("stu", null, null, null, null, null,
				null);
		int i = 0;
		while (cursor.moveToNext()) {
			i++;
		}
		if (i==0) {
			Log.e("aaaa", "long");

			receiveICCard = new ReceiveICCard(context);
			receiveICCard.receiveDate();
		}

		/**
		 * 下面是1.0的
		 */

		// base.creatDB();
		// id = base.query();
		// appid = id.getAppid();
		// appsecret = id.getAppsecret();

		// appid = "0790f5c7954f8329d220c8cc031213dc";
		// appsecret = "4s4ln4zbzNVuIjOx5MYlnsWNawj8TKqo";
		//
		// String URL_Login =
		// "http://api.360baige.com/index/getAccessToken?appid="
		// + appid + "&appsecret=" + appsecret;
		//
		// URL five_url;
		// try {
		// five_url = new URL(URL_Login);
		// HttpURLConnection five_urlConnection = (HttpURLConnection) five_url
		// .openConnection();
		// System.out.println("five_url  " + five_url);
		// five_urlConnection.setRequestMethod("GET");
		// five_urlConnection.setReadTimeout(5000);
		// five_urlConnection.setConnectTimeout(5000);
		// five_urlConnection.setRequestProperty("Authorization", URL_Login);
		// System.out.println("myFun()返回码： "
		// + five_urlConnection.getResponseCode());
		// if (five_urlConnection.getResponseCode() == 200) {
		// BufferedReader reader = new BufferedReader(
		// new InputStreamReader(
		// five_urlConnection.getInputStream()));
		// String line;
		// while ((line = reader.readLine()) != null) {
		// rel_String += line;
		// }
		// jsonobject = new JSONObject(rel_String.substring(4));
		// errcode = jsonobject.getString("errcode");
		// accesstoken = jsonobject.getString("accesstoken");
		// rel_String = null;
		// }
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// if (errcode.equals("0")) {
		// switch (i) {
		// case 0:
		//
		// Cursor cursor = dbManagerStu.db.query("stu", null, null, null,
		// null, null, null);
		// System.out.println("cursor.isAfterLast()  "
		// + cursor.isAfterLast());
		// if (cursor.isAfterLast()) {
		// receiveICCard = new ReceiveICCard();
		// receiveICCard.receiveDate();
		// }
		//
		// break;
		// default:
		// break;
		// }
		// } else if (errcode.equals("1")) {
		// rel_String = null;
		// } else if (errcode.equals("-1")) {
		// rel_String = null;
		// }

	}

}
