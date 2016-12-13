package com.hyc.db;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DBManagerCard {
	private SQLiteDatabase db;
	public static int type;

	public void creatDB() {
		File dbf = new File(getDir() + "/baige/db");
		if (!dbf.exists()) {
			dbf.mkdirs();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
				+ "card.db", null);
		db.execSQL("DROP TABLE IF EXISTS card");
		db.execSQL("CREATE TABLE card (_id INTEGER PRIMARY KEY,cardno INTEGER not null)");
		System.out.println("------DBManagerCard.creatDB-----------");
	}

	public void openDB() {
		File dbf = new File(getDir() + "/baige/db");
		if (!dbf.exists()) {
			dbf.mkdirs();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf.toString() + "/"
				+ "card.db", null);
		System.out.println("------DBManagerCard.openDB-----------");
	}

	public void insert(String card) {
		if (card != null) {
			ContentValues values = new ContentValues();
			values.put("cardno", card);
			db.insert("card", "_id", values);
		}
	}

	public String query(String val) {
		String cardq = "c";
		String[] columns = { "cardno" };
		String[] selectionArgs = { val };
		Cursor c = db.query("card", columns, "cardno=?", selectionArgs, null,
				null, null);
		while (c.moveToNext()) {
			cardq = c.getString(c.getColumnIndex("cardno"));
		}
		c.close();
		return cardq;
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
	public void delete(){
		db.delete("card", null, null);
	}
}