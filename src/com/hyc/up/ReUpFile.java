package com.hyc.up;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.hyc.bean.InterWeb;

public class ReUpFile {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; 
	private static final String CHARSET = "utf-8"; 
	private static JSONObject jsonobject;
	private static String errcode = "22";
	public static String resourceid;
	private static long tt = 4294967295L;
	static InterWeb interWeb = new InterWeb();

	public static void uploadFile(File file,Context context) {
		if (file.exists()) {
			int res = 0;
			String result = null;
			String BOUNDARY = UUID.randomUUID().toString(); 
			String PREFIX = "--", LINE_END = "\r\n";
			String CONTENT_TYPE = "multipart/form-data"; 
			JSONObject operator = new JSONObject();
			@SuppressWarnings("unused")
			String typeToString;
			String Request_URL = interWeb.getURL_UploadFile();
			System.out.println(Request_URL);
			try {
				operator.put("resource", "@" + file.getName());
			} catch (JSONException e1) {

				e1.printStackTrace();
			}
			typeToString = operator.toString();
			try {
				URL url = new URL(Request_URL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(TIME_OUT);
				conn.setConnectTimeout(TIME_OUT);
				conn.setDoInput(true); 
				conn.setDoOutput(true); 
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Charset", CHARSET); 
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("Content-Type", CONTENT_TYPE
						+ ";boundary=" + BOUNDARY);
				conn.setDoOutput(true);
				if (file != null) {
	
					DataOutputStream dos = new DataOutputStream(
							conn.getOutputStream());
					StringBuffer sb = new StringBuffer();
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINE_END);
				
					sb.append("Content-Disposition:form-data; name=\""
							+ "resource" + "\"; filename=\"" + file.getName()
							+ "\"" + LINE_END);
					sb.append("Content-Type: image/jpg; charset=" + CHARSET
							+ LINE_END);
					sb.append(LINE_END);

					dos.write(sb.toString().getBytes());
					
					FileInputStream is = new FileInputStream(file);
				
					byte[] bytes = new byte[1024];
					int len = 0;
					
					while ((len = is.read(bytes)) != -1) {
					
						dos.write(bytes, 0, len);
					}
					is.close();
					dos.write(LINE_END.getBytes());
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
							.getBytes();
					dos.write(end_data);
					dos.flush();

					System.out.println("44444444444444444444444444444");
					res = conn.getResponseCode();
					Log.e(TAG, "response code:" + res);
					if (res == 200) {
						Log.e(TAG, "request success");
						InputStream input = conn.getInputStream();
						StringBuffer sb1 = new StringBuffer();
						int ss;
						while ((ss = input.read()) != -1) {
							sb1.append((char) ss);
						}
						System.out.println(sb1);
						result = sb1.toString();
						try {
							jsonobject = new JSONObject(sb1.substring(0));
							errcode = jsonobject.getString("errcode");
							resourceid = jsonobject.getString("resourceid");
							ReUpRecord reUpRecord = new ReUpRecord();
							// TODO
							String iccardid = file.getName().substring(0,file.getName().lastIndexOf("."));
							reUpRecord.upLoadRecord(iccardid, Long.toString(tt
									- Long.parseLong(iccardid)), context);
							if (errcode.equals("0")) {
								delete(file);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.e(TAG, "result : " + result);
					} else {
						Log.e(TAG, "request error");
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
		}
	}
}
