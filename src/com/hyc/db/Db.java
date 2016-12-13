package com.hyc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {
    static  final String dbName = "uploadbaige";
    static final Integer dbVersion = 1;
    public Db(Context context) {
        super(context, dbName, null, dbVersion);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        db.execSQL("CREATE TABLE allpaths(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT  DEFAULT\"\"," +
                "cardno TEXT  DEFAULT\"\"," +
                "timecode TEXT  DEFAULT\"\"," +
                "alluploadpaths TEXT  DEFAULT\"\")");
        db.execSQL("CREATE TABLE filepaths(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uploadpaths TEXT  DEFAULT\"\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /** 
     * @param context 
     * @return 
     */

    public boolean deleteDatabase(Context context) {  
    return context.deleteDatabase("uploadbaige");  
    }  
    
}
