package com.hyc.db;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.hyc.bean.PictureId;

public class DBManagerPicture {

	public SQLiteDatabase db;

	public void creatDB() {
		File dbf = new File(getDir() + "/baige/db");
		if (!dbf.exists()) {
			dbf.mkdirs();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
				+ "picture.db", null);
	//	db.execSQL("DROP TABLE IF EXISTS stu");
		db.execSQL("create table if not exists picture (_id INTEGER PRIMARY KEY, pic_id VARCHAR, cardno TEXT)");
		System.out.println("------DBManagerPicture.creatDB-----------");
	}

	public void openDB() {
		File dbf = new File(getDir() + "/baige/db"); 
		if (!dbf.exists()) {
			dbf.mkdirs();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
				+ "picture.db", null);
		System.out.println("------DBManagerPicture.openDB-----------");
	}

	public int getNum() {
		int i = 0;
		Cursor cursor = db.query("picture", null, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			i++;
		}
		cursor.close();
		return i;
	}
	
	public ArrayList<String> query() {
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query("picture", null, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex("cardno")));
		}
		cursor.close();
      return list;
	}

	public String queryCarno(String carNo) {
		System.out.println("query()的参数" + carNo);
		String cardq = "c";
		String[] columns = { "cardno" };
		String[] selectionArgs = { carNo };
		Cursor c = db.query("picture", columns, "cardno=?",
				selectionArgs, null, null, null);
		while (c.moveToNext()) {
			cardq = c.getString(c.getColumnIndex("cardno"));
		}
		c.close();
		return cardq;
	}
	
	public String queryHead(String head) {
		System.out.println("query()的参数" + head);
		String cardq = "c";
		String[] columns = { "pic_id" };
		String[] selectionArgs = { head };
		Cursor c = db.query("picture", columns, "cardno=?",
				selectionArgs, null, null, null);
		while (c.moveToNext()) {
			cardq = c.getString(c.getColumnIndex("pic_id"));
		}
		c.close();
		return cardq;
	}
	
	public void update(ArrayList<String> list){
		ContentValues values = new ContentValues();
		values.put("pic_id",list.get(0));
		values.put("cardno", list.get(1));
		db.insert("picture", null, values);
	}

	public void closeDB() {
		db.close();
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

	public void delete() {
		if(db!=null){
			db.delete("picture", "cardno=?", null);	
		}
		
	}
	public void deleteAll() {
		if(db!=null){
			db.delete("picture", null, null);	
		}
		
	}
}
