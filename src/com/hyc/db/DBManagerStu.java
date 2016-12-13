package com.hyc.db;

import java.io.File;
import java.util.ArrayList;

import com.hyc.bean.Stu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBManagerStu extends SQLiteOpenHelper {
	static final String dbName = "db";
	static final Integer dbVersion = 1;
	SQLiteDatabase dbWirter;
	SQLiteDatabase dbReader;

	public DBManagerStu(Context context) {
		super(context, dbName, null, dbVersion);
		dbWirter = getWritableDatabase();
		dbReader =getReadableDatabase();
	}

//	// TODO Auto-generated constructor stub
//
//	public SQLiteDatabase db;
//	public static int type;
//
//	public void creatDB00() {
//		File dbf = new File(getDir() + "/baige/db");
//		if (!dbf.exists()) {
//			dbf.mkdirs();
//		}
//		// db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
//		// + "stu.db", null);
//
//		// db.execSQL("DROP TABLE IF EXISTS stu");
//		db.execSQL("CREATE TABLE if not exists stu (_id INTEGER PRIMARY KEY,cardno INTEGER, type INTEGER, name VARCHAR, classname VARCHAR, sex INTEGER, headportraitid VARCHAR)");
//		System.out.println("------DBManagerStu.creatDB-----------");
//	}
//
//	public void openDB1() {
//		File dbf = new File(getDir() + "/baige/db");
//		if (!dbf.exists()) {
//			dbf.mkdirs();
//		}
//		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
//				+ "stu.db", null);
//		System.out.println("------DBManagerStu.openDB-----------");
//	}

	public void insert(Stu stu) {
		if (stu != null) {
			ContentValues values = new ContentValues();
			values.put("cardno", stu.getCardno());
			values.put("type", stu.getType());
			values.put("name", stu.getName());
			values.put("classname", stu.getClassname());
			values.put("sex", stu.getSex());
			values.put("headportraitid", stu.getHeadportraitid());
			System.out.println(stu.getHeadportraitid()+"uuuuuuddddd");
			dbWirter.insert("stu", "_id", values);
		}
	}

	public Stu query(String val) {
		Stu stu = new Stu();
		String[] columns = { "cardno", "type", "name", "classname", "sex",
				"headportraitid" };
		String[] selectionArgs = { val };
		Cursor c = dbReader.query("stu", columns, "cardno=?", selectionArgs, null,
				null, null);

		while (c.moveToNext()) {
			System.out.println("0000000");
			stu.setSex(c.getInt(c.getColumnIndex("sex")));
			stu.setType(c.getInt(c.getColumnIndex("type")));
			stu.setName(c.getString(c.getColumnIndex("name")));
			stu.setClassname(c.getString(c.getColumnIndex("classname")));
			stu.setHeadportraitid(c.getString(c
					.getColumnIndex("headportraitid")));
		}
		c.close();
		return stu;
	}
	
	public void insertDate(String date) {
		dbWirter.delete("up", null, null);
		if (date != null) {
			ContentValues values = new ContentValues();
			values.put("date", date);
			dbWirter.insert("up", null, values);
		}
	}
	
	public void insertIntent(String isintent) {
		dbWirter.delete("intent", null, null);
		if (isintent != null) {
			ContentValues values = new ContentValues();
			values.put("isintent", isintent);
			dbWirter.insert("intent", null, values);
		}
	}
	public void insertTwo(String twoUp) {
		dbWirter.delete("twoup", null, null);
		if (twoUp != null) {
			ContentValues values = new ContentValues();
			values.put("uptwo", twoUp);
			dbWirter.insert("twoup", null, values);
		}
	}
	
	public String queryIntent() {
		String isintent = null;
		Cursor c = dbReader.query("intent", null, null, null, null,
				null, null);

		while (c.moveToNext()) {
			isintent = c.getString(c.getColumnIndex("isintent"));
		}
		c.close();
		return isintent;
	}
	
	public String queryDate() {
		String date = null;
		Cursor c = dbReader.query("up", null, null, null, null,
				null, null);

		while (c.moveToNext()) {
			date = c.getString(c.getColumnIndex("date"));
		}
		c.close();
		return date;
	}
	
	public String queryTwo() {
		String two = "c";
		Cursor c = dbReader.query("twoup", null, null, null, null,
				null, null);

		while (c.moveToNext()) {
			two = c.getString(c.getColumnIndex("uptwo"));
		}
		c.close();
		return two;
	}
	

	// public Stu query(String val) {
	// Stu stu = new Stu();
	// String[] columns = { "type", "name", "classname", "sex",
	// "headportraitid" };
	// String[] selectionArgs = { val };
	// Cursor c = db.query("stu", columns, "cardno=?", selectionArgs, null,
	// null, null);
	// System.out.println(c.getCount());
	// System.out.println(c + "     ...        " + c.moveToNext());
	// while (c.moveToNext()) {
	// System.out.println("name query()11 ");
	// System.out.println("name query()11 " + c.getColumnIndex("name"));
	// stu.setType(c.getInt(c.getColumnIndex("type")));
	// stu.setName(c.getString(c.getColumnIndex("name")));
	// stu.setClassname(c.getString(c.getColumnIndex("classname")));
	// stu.setSex(c.getInt(c.getColumnIndex("sex")));
	// stu.setHeadportraitid(c.getString(c
	// .getColumnIndex("headportraitid")));
	// }
	// c.close();
	// System.out.println("name  query()22" + stu.getName());
	// return stu;
	// }

	public void closeDB() {
		dbWirter.close();
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
	
	public void delete(){
		dbWirter.delete("stu", null, null);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE if not exists stu (_id INTEGER PRIMARY KEY,cardno INTEGER, type INTEGER, name VARCHAR, classname VARCHAR, sex INTEGER, headportraitid VARCHAR)");
		db.execSQL("CREATE TABLE up(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT  DEFAULT\"\")");
		db.execSQL("CREATE TABLE intent(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "isintent TEXT  DEFAULT\"\")");
		db.execSQL("CREATE TABLE twoup(" +
                "uptwo TEXT  DEFAULT\"\")");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}