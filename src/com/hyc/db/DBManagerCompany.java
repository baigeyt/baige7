package com.hyc.db;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.hyc.bean.Company;

/**
 *学校或企业信息数据库 
 * */
public class DBManagerCompany {
	private SQLiteDatabase db;

	public void creatDB() {
		File dbf = new File(getDir() + "/baige/db");
		if (!dbf.exists()) {
			dbf.mkdirs();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
				+ "company.db", null);
		// db.execSQL("DROP TABLE IF EXISTS stu");
		db.execSQL("create table if not exists company (_id INTEGER PRIMARY KEY,schoolid integer, name VARCHAR not null,qq VARCHAR,mobile VARCHAR,email VARCHAR,province VARCHAR,city VARCHAR,district VARCHAR,address VARCHAR,content VARCHAR,apptype integer)");
		System.out.println("------DBManagerCompany.creatDB-----------");
	}
	
	public int querySchool(){
		int i = 0;
		Cursor cursorSchool = db.query("company", null, null, null, null, null,
 				null);
		while (cursorSchool.moveToNext()) {
 			i++;
 		}
	 	return i;
	}

	public void openDB() {
		File dbf = new File(getDir() + "/baige/db");
		if (!dbf.exists()) {
			dbf.mkdirs();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
				+ "company.db", null);
		System.out.println("------DBManagerPicture.openDB-----------");
	}

	public void insert(Company company) {
		if (company != null) {
			ContentValues values = new ContentValues();
				values.put("schoolid", company.getSchoolid());	
				if(company.getQq()!=null){
					values.put("qq", company.getQq());		
				}
			if(company.getName()!=null){
				values.put("name", company.getName());	
			}
			if(company.getMobile()!=null){
				values.put("mobile", company.getMobile());	
			}if(company.getEmail()!=null){
				values.put("email", company.getEmail());
			}if(company.getProvince()!=null){
				values.put("province", company.getProvince());
			}if(company.getCity()!=null){
				values.put("city", company.getCity());	
			}if(company.getDistrict()!=null){
				values.put("district", company.getDistrict());	
			}if(company.getAddress()!=null){
				values.put("address", company.getAddress());
			}
			if(company.getContent()!=null){
				values.put("content", company.getContent());
			}
			values.put("apptype", company.getAppType());
			db.insert("company", "_id", values);
		}
	}

	public Company query() {
		Company company = new Company();
		String[] columns = { "schoolid", "name", "qq", "mobile", "email",
				"province", "city", "district", "address", "content" ,"apptype"};
		String[] selectionArgs = { "1" };
		Cursor c = db.query("company", columns, "_id=?", selectionArgs, null, null,
				null);

		while (c.moveToNext()) {
			System.out.println("0000000");
			company.setSchoolid(c.getInt(c.getColumnIndex("schoolid")));
			company.setName(c.getString(c.getColumnIndex("name")));
			company.setQq(c.getString(c.getColumnIndex("qq")));
			company.setMobile(c.getString(c.getColumnIndex("mobile")));
			company.setEmail(c.getString(c.getColumnIndex("email")));
			company.setProvince(c.getString(c.getColumnIndex("province")));
			company.setCity(c.getString(c.getColumnIndex("city")));
			company.setDistrict(c.getString(c.getColumnIndex("district")));
			company.setAddress(c.getString(c.getColumnIndex("address")));
			company.setContent(c.getString(c.getColumnIndex("content")));
			company.setAppType(c.getInt(c.getColumnIndex("apptype")));
		}
		c.close();
		return company;
	}
	
	public Integer queryLocation() {
		int appType = 0;
		String[] columns = {"apptype"};
		String[] selectionArgs = { "1" };
		Cursor c = db.query("company", columns, "_id=?", selectionArgs, null, null,
				null);

		while (c.moveToNext()) {
			appType = c.getInt(c.getColumnIndex("apptype"));
		}
		c.close();
		return appType;
	}

	public void deleteDB() {
		db.execSQL("DROP TABLE IF EXISTS company");
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
	
	public String queryName() {
		String cardq = "c";
		String[] columns = { "name" };
		Cursor c = db.query("company", columns,null,
				null, null, null, null);
		while (c.moveToNext()) {
			cardq = c.getString(c.getColumnIndex("name"));
		}
		c.close();
		return cardq;
	}
	
	public void deleteSchoolInfo(){
		db.delete("company", null, null);
	}
	public void closeDBManagerCompany(){
		db.close();
	}
}


