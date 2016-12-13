package com.hyc.up;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.hyc.baige7.MainActivity;
import com.hyc.bean.InterWeb;
import com.hyc.bean.Stu;
import com.hyc.bean.UpRecord;
import com.hyc.db.DBManagerStu;

import android.content.Context;
import android.util.Log;

public class ReUpRecord {
	private JSONObject operator;
	private String typeToString;
	private int type;
	private Stu stu;
	private String reulse;
	private JSONObject jsonobject;
	InterWeb interWeb = new InterWeb();
	private String errcode = "22";
	private DBManagerStu dbManagerstu;
    

	public UpRecord upLoadRecord(String iccardid, String cardno,Context context) {
		dbManagerstu = new DBManagerStu(context);
		operator = new JSONObject();
        dbManagerstu.closeDB();
		stu = dbManagerstu.query(cardno + "");
		type = stu.getType();
		try {
			// TODO
	    	operator.put("resourceid", ReUpFile.resourceid);
			operator.put("recordtime", System.currentTimeMillis()/1000);			
			operator.put("usertype", type);						
			operator.put("iccardno", cardno);
			operator.put("remark", "remark");
			typeToString = operator.toString();
			MainActivity.type = 0;
			MainActivity.cardno = 0L;
			MainActivity.physicsno = 0L;
			System.out.println("typeToString-------" + typeToString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			URL url_post = new URL(interWeb.getURL_UploadRecord());
			System.out.println(url_post);
			HttpURLConnection urlConnection = (HttpURLConnection) url_post
					.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setReadTimeout(20000);
			urlConnection.setConnectTimeout(20000);
		
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Authorization",
					interWeb.getURL_ReUploadRecord());
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(typeToString.getBytes().length));
			urlConnection.setDoOutput(true);
		
			urlConnection.getOutputStream().write(typeToString.getBytes());

			System.out.println("888888888888888888888888888");
			System.out.println("1111111111111111111111111111111");

			if (urlConnection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				String line;
				System.out.println(" ============================= ");
				System.out.println(" Contents of post request ");
				System.out.println(" ============================= ");
				while ((line = reader.readLine()) != null) {
					reulse += line;
				}
				System.out.println(reulse);
				System.out.println(" ============================= ");
				System.out.println(" Contents of post request ends ");
				System.out.println(" ============================= ");
				reader.close();
				urlConnection.disconnect();
				System.out.println("1111111111111111111111111111111");
                try {
					jsonobject = new JSONObject(reulse.substring(4));
					errcode = jsonobject.getString("errcode");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(errcode.equals("0")){
					System.out.println("�ϴ��ɹ�������������������");
				}
			} else {
				Log.e("UploadRecord", urlConnection.getResponseCode() + "");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
