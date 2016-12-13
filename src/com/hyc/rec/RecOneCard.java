package com.hyc.rec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.hyc.baige7.Login;
import com.hyc.baige7.MainActivity;
import com.hyc.bean.InterWeb;
import com.hyc.bean.Stu;
import com.hyc.db.DBManagerStu;

public class RecOneCard {
	InterWeb interWeb = new InterWeb();
	private String ic_String;
	public static String name = null;
	public static int classid;
	public static String type = null;
	public Stu receiveDate(long physicsno) {
		Stu stu = new Stu();
		try {
			URL five_url = new URL(interWeb.getURL_ONEIC());
			Log.e("onecard", Login.accesstoken + "" + MainActivity.physicsno);
			System.out.println(interWeb.getURL_ONEIC());
			HttpURLConnection five_urlConnection = (HttpURLConnection) five_url
					.openConnection();
			five_urlConnection.setRequestMethod("GET");
			five_urlConnection.setReadTimeout(5000);
			five_urlConnection.setConnectTimeout(5000);
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
				Log.e("新卡", ic_String);
				JSONObject jsonObject1 = new JSONObject(ic_String.substring(4));
				JSONObject jsonObject = new JSONObject(ic_String.substring(4))
						.getJSONObject("data");
				if (jsonObject1.getInt("errcode") == 0) {
					stu.setType(jsonObject.getInt("type"));
					stu.setCardno(Long.parseLong(jsonObject.getString("cardno")));
					if (jsonObject.getString("type").equals("8")) {
						JSONObject childObject = jsonObject
								.getJSONObject("child");
						stu.setName(childObject.getString("name"));
						stu.setClassname(childObject.getString("classname"));
						stu.setHeadportraitid(childObject.getString("headportraitid"));
						stu.setSex(Integer.parseInt(childObject.getString("sex")));
					    
					}else{
						JSONObject teacherObject = jsonObject.getJSONObject("owner");
						String name = teacherObject.getString("name");
						String className = "老师";
						stu.setClassname(className);
						stu.setName(name);
//						stu.setHeadportraitid(owner.getString("headportraitid"));
						stu.setHeadportraitid("0");
						stu.setSex(Integer.valueOf(teacherObject.getString("sex")));		
					}
				}
				ic_String = null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return stu;
	}
}