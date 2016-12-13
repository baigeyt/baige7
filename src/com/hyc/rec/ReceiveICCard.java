package com.hyc.rec;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.extend.GetFileNum;
import com.hyc.baige7.Login;
import com.hyc.baige7.MainActivity;
import com.hyc.bean.InterWeb;
import com.hyc.bean.PictureId;
import com.hyc.bean.Stu;
import com.hyc.db.DBManagerPicture;
import com.hyc.db.DBManagerStu;

public class ReceiveICCard {
	InterWeb interWeb = new InterWeb();
	private String ic_String;
	private JSONObject jsonobject;
	private String errcode;
	private JSONArray jsonArray;
	DBManagerStu dbManager;
	private DBManagerPicture dbPicture;
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/baige/headportrait/";
	private String mFileName;
	private String picID;
	private Cursor cursor;
	private String time;
	private int n = 1;
	private int flag1 =0;
	private int flag2 =0;
	private int flag3 =0;
	private int flag4 =0;
	private int num =0;
	private ArrayList<PictureId> picBean = new ArrayList<PictureId>();
	Context mContext;
	Handler mHandler;
	
	public ReceiveICCard(Context context){
		mContext = context;
	}
	
	public void receiveDate() {
		URL five_url;
		dbManager = new DBManagerStu(mContext);
		dbPicture = new DBManagerPicture();
		dbPicture.creatDB();
		try {
			five_url = new URL(interWeb.getURL_IC());
			Log.e("oo", interWeb.getURL_IC());
			HttpURLConnection five_urlConnection = (HttpURLConnection) five_url
					.openConnection();
			five_urlConnection.setRequestMethod("GET");
			five_urlConnection.setReadTimeout(5000);
			five_urlConnection.setConnectTimeout(5000);
			
			System.out.println("ic卡相关信息列表："+interWeb.getURL_IC());
			if (five_urlConnection.getResponseCode() == 200) {
				Intent iSReflush = new Intent();
				iSReflush.setAction("com.baige.ui.service");
				iSReflush.putExtra("reflush", "1");
				new DBManagerStu(mContext).insertIntent("0");
				mContext.sendBroadcast(iSReflush);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								five_urlConnection.getInputStream()));
				
//			
//				char[] bytes=new char[1024];
//				StringBuffer sb=new StringBuffer();		
//				int len;
//				while((len=reader.read(bytes))!=-1){
//					System.out.println(len);
//					sb.append(bytes, 0, len);
//				}
//				
//				System.out.println("sb.toString()"+sb.toString());
//				System.out.println("sb.length()"+sb.length());
												
				String line;
				while ((line = reader.readLine()) != null) {
					ic_String += line;
				}
				Log.e("ic卡相关信息数据 ", ic_String);
				jsonobject = new JSONObject(ic_String.substring(4));
				errcode = jsonobject.getString("errcode");
				if(!errcode.equals("0")){
					Intent iSFail = new Intent();
					iSFail.setAction("com.baige.ui.service");
					iSFail.putExtra("Fail", "3");
					mContext.sendBroadcast(iSFail);
				}else{
					jsonArray = new JSONObject(ic_String.substring(4))
					.getJSONArray("data");
			System.out.println("jsonArray.length  "+jsonArray.length());
			System.out.println("jsonArray  "+jsonArray);
			for (int i = 0; i < jsonArray.length(); i++) {
				Intent updatePro = new Intent();
				updatePro.setAction("com.baige.ui.service");
				updatePro.putExtra("progress", i+"/"+jsonArray.length());
				mContext.sendBroadcast(updatePro);
				
				Stu stu = new Stu();
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("cardno") != "null") {
					stu.setCardno(object.getLong("cardno"));
//					dbPicture.insert(pictureId);
					if (object.getString("type").equals("8")){
						stu.setType(object.getInt("type"));
						JSONObject childObject = object
								.getJSONObject("child");
						if (childObject.getString("name") != "null") {
							stu.setName(childObject.getString("name"));
							stu.setSex(Integer.parseInt(childObject.getString("sex")));
							if (childObject.getString("classname") != "null") {
								stu.setClassname(childObject
										.getString("classname"));
								String picID = childObject.getString("headportraitid").toString(); 
								
								
								Log.e("ID666", picID);
								Log.e("ID", 00000000+"");
								if(picID.equals("0") || picID.equals("null")){
									stu.setHeadportraitid(childObject
											.getString("headportraitid"));
									dbManager.insert(stu);
									
								}else{
									stu.setHeadportraitid(childObject
											.getString("headportraitid").toString());
									Log.e("呵呵", "把"+childObject
											.getString("headportraitid").toString()+"添加进数据库"); 
									dbManager.insert(stu);
									if(dbPicture.queryCarno(String.valueOf(stu.getCardno())).equals("c")){
										ArrayList<String> list = new ArrayList<String>();
										list.add(stu.getHeadportraitid());
										list.add(String.valueOf(stu.getCardno()));
										dbPicture.update(list);
										PictureId pictureId = new PictureId();
										pictureId.setCardNo(list.get(1));
										pictureId.setPic_id(list.get(0));
										picBean.add(pictureId);
									}
//									if(dbPicture.query(String.valueOf(stu.getCardno()))==null||!dbPicture.query(String.valueOf(stu.getCardno())).equals(stu.getHeadportraitid())){
//										Log.e("日志","进来了 准备下载");
//										savePic(stu); 
//									}
								}
							}
						}
					}
					else if(object.getString("type").equals("7")){
						JSONObject owner = object.getJSONObject("owner");
						String name = owner.getString("name");
						Long cardno = object.getLong("cardno");
						String className = "老师";
						String type = object.getString("type");
						stu.setCardno(cardno);
						stu.setClassname(className);
						stu.setName(name);
//						stu.setHeadportraitid(owner.getString("headportraitid"));
						stu.setHeadportraitid("0");
						stu.setType(Integer.valueOf(type));
						stu.setSex(Integer.valueOf(owner.getString("sex")));
						dbManager.insert(stu);
												
					}
				}
				if(i==jsonArray.length()-1){
					if(picBean.size()!=0){
						Intent upHead = new Intent();
						upHead.setAction("com.baige.ui.service");
						upHead.putExtra("uphead", "4");
						mContext.sendBroadcast(upHead);
						
						for(int j =0;j<picBean.size();j++){
							Log.e("picBean", "图像的名字"+picBean.get(j));
							Intent updatePic = new Intent();
							updatePic.setAction("com.baige.ui.service");
							updatePic.putExtra("progress", j+"/"+picBean.size());
							mContext.sendBroadcast(updatePic);
							savePic(picBean.get(j),mContext);
							if(j==picBean.size()-1){
								Intent iSMiss = new Intent();
								iSMiss.setAction("com.baige.ui.service");
								iSMiss.putExtra("miss", "2");
								mContext.sendBroadcast(iSMiss);
								new DBManagerStu(mContext).insertIntent("1");
							}	
						}
					}else{
						Intent iSMiss = new Intent();
						iSMiss.setAction("com.baige.ui.service");
						iSMiss.putExtra("miss", "2");
						mContext.sendBroadcast(iSMiss);
						new DBManagerStu(mContext).insertIntent("1");
					}
				}
			}
			ic_String = null;
			
				}
				System.out.println("errcode" + errcode);

		}else{
				Intent iSFail = new Intent();
				iSFail.setAction("com.baige.ui.service");
				iSFail.putExtra("Fail", "3");
				mContext.sendBroadcast(iSFail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Intent iSFail = new Intent();
			iSFail.setAction("com.baige.ui.service");
			iSFail.putExtra("Fail", "3");
			mContext.sendBroadcast(iSFail);
		} 
	}

	public void savePic(PictureId picId,Context mContext) {
//		Log.e("传入头像id", stu.getHeadportraitid());
//		Log.e("传入卡号",stu.getCardno()+"");
		try {
			String filePath = "http://api.360baige.cn/resource/get?accesstoken="+Login.accesstoken + "&resourceid="+ picId.getPic_id();
			Log.e("ss", filePath);
			URL url = new URL(filePath);
			System.out.println("filePath " + filePath);
			System.out.println("savePic()");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			if (conn.getResponseCode() == 200) {
				InputStream inStream = conn.getInputStream();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				outStream.close();
				inStream.close();
				byte[] data = outStream.toByteArray();
				File file = new File(Environment
						.getExternalStorageDirectory()
						+ "/baige/headportrait/" + picId.getCardNo() + ".jpg");
				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(data);
				outputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Intent picFail = new Intent();
			picFail.setAction("com.baige.ui.service");
			picFail.putExtra("picFail", "5");
			mContext.sendBroadcast(picFail);
		} 
	}
	
	
}
