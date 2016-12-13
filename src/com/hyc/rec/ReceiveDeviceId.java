package com.hyc.rec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.hyc.bean.MacEntity;
import com.hyc.db.DBMacAddress;

import android.os.Message;
import android.util.Log;

public class ReceiveDeviceId {
	
	String reulse;
	
	public void mUploadData(String macAddress,DBMacAddress dbMacAddress) {
		
		JSONObject mJSONObject = new JSONObject();

		try {
			mJSONObject.put("type", 3);
			mJSONObject.put("macinfo", macAddress);
			String typeToString = mJSONObject.toString();
			URL url = new URL("http://api.360baige.cn/equipment/getinfo");
			// URL url = new URL("http://api.figool.cn/equipment/getinfo");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// 设置请求的方式
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setReadTimeout(20000);// 设置超时的时间
			conn.setConnectTimeout(20000);// 设置链接超时的时间
			// 设置请求的头
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Authorization", url.toString());
			conn.setRequestProperty("Content-Length",
					String.valueOf(typeToString.getBytes().length));
			conn.setDoOutput(true);
			// 4.向服务器写入数据
			conn.getOutputStream().write(typeToString.getBytes());

			
			if (conn.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					reulse += line;
				}
				Log.e("访问设备id返回结果", reulse);
				JSONObject jsonObject = new JSONObject(reulse.substring(4));
				JSONObject subJsonObject = jsonObject
						.getJSONObject("equipmentinfo");

				Log.e("获取到的设备id", subJsonObject.toString());

				 String devicesID = subJsonObject.getString("id");


				// 把设备ID插入本地数据库
				
				String strID = dbMacAddress.query_ID().getDevices();
				if (strID == null) {
					if (devicesID != null) {

						MacEntity macEntity = new MacEntity();
						macEntity.setDevices(devicesID);
						dbMacAddress.insert_ID(macEntity);
					}
				}

			}else{
				 Log.e("异常返回", conn.getResponseCode()+"");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
