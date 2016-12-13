package com.hyc.up;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.hyc.bean.ImgInfo;

public class UploadFile {

	public static void uploadFile(ImgInfo imginfo, Context context,String object,Long time,String path,Handler mHandler,SQLiteDatabase dbWriter){
		
		UploadRecord uploadRecord = new UploadRecord();
		uploadRecord.upLoadRecord(imginfo, context, "0",object,time,path,mHandler,dbWriter);

	}
	
    public static void uploadTwoFile(ImgInfo imginfo, Context context,String object,Long time,String path,Handler mHandler,SQLiteDatabase dbWriter){
		
		UploadRecord uploadRecord = new UploadRecord();
		uploadRecord.upLoadTwoRecord(imginfo, context, "0",object,time,path,mHandler,dbWriter);

	}

}
