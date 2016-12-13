package com.hyc.up;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.extend.DeleteFilePic;
import com.hyc.baige7.Login;
import com.hyc.baige7.MainActivity;
import com.hyc.bean.ImgInfo;
import com.hyc.bean.InterWeb;
import com.hyc.bean.UpRecord;

public class UploadRecord {
	private JSONObject operator;
	private String typeToString;
	private String reulse;
	private JSONObject jsonobject;
	InterWeb interWeb = new InterWeb();
	private String errcode = "22";



	public UpRecord upLoadRecord(ImgInfo imginfo, Context context,
			String resourceid, String object,Long time,String path,Handler mHandler,SQLiteDatabase dbWriter) {

		operator = new JSONObject();
		try {
			operator.put("resourceid", "0");
			operator.put("resourcekey", object);
			operator.put("recordtime", time);
			operator.put("usertype", imginfo.getType());
			operator.put("iccardno", imginfo.getCardno());
			operator.put("remark", "remark");
			typeToString = operator.toString();

			System.out.println("typeToString-------" + typeToString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			URL url_post = new URL(interWeb.getURL_UploadRecord());
			System.out.println(url_post);
			// url.openConnection()����������
			HttpURLConnection urlConnection = (HttpURLConnection) url_post
					.openConnection();
			urlConnection.setRequestMethod("POST");// ��������ķ�ʽ
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setReadTimeout(5000);// ���ó�ʱ��ʱ��
			urlConnection.setConnectTimeout(5000);// �������ӳ�ʱ��ʱ��
			// ���������ͷ
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Authorization",
					interWeb.getURL_UploadRecord());
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(typeToString.getBytes().length));
			urlConnection.setDoOutput(true);
			// 4.�������д������
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
				System.out.println(reulse + "..............AAAAAAA");
				System.out.println(" ============================= ");
				System.out.println(" Contents of post request ends ");
				System.out.println(" ============================= ");
				try {
					jsonobject = new JSONObject(reulse.substring(4));
					errcode = jsonobject.getString("errcode");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (errcode.equals("0")) {
					// delete(imginfo.getFile());
					// MainActivity.upallcount++;
					// Log.v("cc","UploadRecord==>"+MainActivity.upallcount+"//");
					File file = new File(path);
					if(file.exists()){
						DeleteFilePic.delete(file);
					}
					Log.e("上传数", ""+MainActivity.shangchuan++);
				} else if(!errcode.equals("0")) {
					File dir = new File(Environment.getExternalStorageDirectory()+"/baige/twoFile");
					if (dir.exists()) {
					} else {
						dir.mkdirs();
					}
					copyFile(path, Environment.getExternalStorageDirectory()+"/baige/twoFile/"+path.substring(path.lastIndexOf("/")+1,path.length()));
					DeleteFilePic.delete(new File(path));
					ContentValues values = new ContentValues();
					values.put("type", imginfo.getType());
					values.put("cardno", imginfo.getCardno());
					values.put("alluploadpaths", path.substring(path.lastIndexOf("/")+1,path.length()));
					values.put("timecode", time);
					dbWriter.insert("allpaths", null, values);
					Login login = new Login();
					login.myFun(context);
				}
				reader.close();
				urlConnection.disconnect();
				System.out.println("1111111111111111111111111111111");
			} else {
				Log.e("UploadRecord", urlConnection.getResponseCode() + "");
				File dir = new File(Environment.getExternalStorageDirectory()+"/baige/twoFile");
				if (dir.exists()) {
				} else {
					dir.mkdirs();
				}
				copyFile(path, Environment.getExternalStorageDirectory()+"/baige/twoFile/"+path.substring(path.lastIndexOf("/")+1,path.length()));
				DeleteFilePic.delete(new File(path));
				ContentValues values = new ContentValues();
				values.put("type", imginfo.getType());
				values.put("cardno", imginfo.getCardno());
				values.put("alluploadpaths", path.substring(path.lastIndexOf("/")+1,path.length()));
				values.put("timecode", time);
				dbWriter.insert("allpaths", null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			File dir = new File(Environment.getExternalStorageDirectory()+"/baige/twoFile");
			if (dir.exists()) {
			} else {
				dir.mkdirs();
			}
			copyFile(path, Environment.getExternalStorageDirectory()+"/baige/twoFile/"+path.substring(path.lastIndexOf("/")+1,path.length()));
			DeleteFilePic.delete(new File(path));
			ContentValues values = new ContentValues();
			values.put("type", imginfo.getType());
			values.put("cardno", imginfo.getCardno());
			values.put("alluploadpaths", path.substring(path.lastIndexOf("/")+1,path.length()));
			values.put("timecode", time);
			dbWriter.insert("allpaths", null, values);
		} 
		return null;
	}
	
	public UpRecord upLoadTwoRecord(ImgInfo imginfo, Context context,
			String resourceid, String object,Long time,String path,Handler mHandler,SQLiteDatabase dbWriter) {

		operator = new JSONObject();
		try {
			operator.put("resourceid", "0");
			operator.put("resourcekey", object);
			operator.put("recordtime", time);
			operator.put("usertype", imginfo.getType());
			operator.put("iccardno", imginfo.getCardno());
			operator.put("remark", "remark");
			typeToString = operator.toString();

			System.out.println("typeToString-------" + typeToString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			URL url_post = new URL(interWeb.getURL_UploadRecord());
			System.out.println(url_post);
			// url.openConnection()����������
			HttpURLConnection urlConnection = (HttpURLConnection) url_post
					.openConnection();
			urlConnection.setRequestMethod("POST");// ��������ķ�ʽ
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setReadTimeout(5000);// ���ó�ʱ��ʱ��
			urlConnection.setConnectTimeout(5000);// �������ӳ�ʱ��ʱ��
			// ���������ͷ
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Authorization",
					interWeb.getURL_UploadRecord());
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(typeToString.getBytes().length));
			urlConnection.setDoOutput(true);
			// 4.�������д������
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
				System.out.println(reulse + "..............AAAAAAA");
				System.out.println(" ============================= ");
				System.out.println(" Contents of post request ends ");
				System.out.println(" ============================= ");
				try {
					jsonobject = new JSONObject(reulse.substring(4));
					errcode = jsonobject.getString("errcode");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (errcode.equals("0")) {

					File file = new File(path);
					if (file.exists()) {
						DeleteFilePic.delete(file);
					}
					
					dbWriter.delete("allpaths",
							"alluploadpaths=?",
							new String[] { path.substring(path.lastIndexOf("/")+1,path.length()) });
					
					Log.e("上传数",""+MainActivity.shangchuan++);
					
				} else if (errcode.equals("2")) {
					
					Login login = new Login();
					login.myFun(context);
				}
				reader.close();
				urlConnection.disconnect();
				System.out.println("1111111111111111111111111111111");
			} else {
				Log.e("UploadRecord", urlConnection.getResponseCode() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("copyFile");
			e.printStackTrace();

		}

	}

}
