package com.hyc.up;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.extend.DeleteFilePic;
import com.hyc.baige7.MainActivity;
import com.hyc.bean.ImgInfo;
import com.hyc.db.Db;

public class OSSSample {

	private Context context;
	// 上传地址
	String accessUrl = "oss-cn-shenzhen.aliyuncs.com";
	// AliYun OSS对象
	private OSS oss;
	// 文件唯一标识，随便你怎么写·
	// 就是buketName，这个就是OSS服务空间名
	private String buketName = "sdk-baige";
	private Handler scInfoHandler;
	private String uploadFilePath;
	private String mPath;
	private Handler mHandler;
	private ImgInfo imgInfo;
	private String object;
	private SQLiteDatabase dbWriter;
	


	public OSSSample(Context context, String uploadFilePath, OSS oss,
			Handler mHandler, ImgInfo imgInfo, String object,SQLiteDatabase dbWriter) {
		this.context = context;
		this.uploadFilePath = uploadFilePath;
		this.oss = oss;
		this.mHandler = mHandler;
		this.imgInfo = imgInfo;
		this.object = object;
		this.dbWriter = dbWriter;

	}

	public OSSSample(OSS oss, String path, String obj) {

		this.oss = oss;
		mPath = path;
		object = obj;

	}

	public OSSSample() {

	}

	public void upLoadLocation() {
		PutObjectRequest put = new PutObjectRequest(buketName, object,
				mPath);
		OSSAsyncTask task = oss.asyncPutObject(put,
				new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
					@Override
					public void onSuccess(PutObjectRequest request,
							PutObjectResult result) {
						Log.e("uploadTxt", "上传成功经纬度");
						
						
						File file=new File(mPath);						
						DeleteFilePic.delete(file);

					}

					@Override
					public void onFailure(PutObjectRequest request,
							ClientException clientExcepion,
							ServiceException serviceException) {
						Log.e("uploadTxt", "上传失败经纬度");

					}
				});

	};

	public void upload() throws ClientException, ServiceException {
		// 构造上传请求
		PutObjectRequest put = new PutObjectRequest(buketName, object,
				uploadFilePath);

		// 异步上传时可以设置进度回调
		put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
			@Override
			public void onProgress(PutObjectRequest request, long currentSize,
					long totalSize) {
				Log.d("PutObject", "currentSize: " + currentSize
						+ " totalSize: " + totalSize);
			}
		});

		OSSAsyncTask task = oss.asyncPutObject(put,
				new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
					@Override
					public void onSuccess(PutObjectRequest request,
							PutObjectResult result) {
//						mHandler.sendEmptyMessage(1);
						new UploadFile().uploadFile(imgInfo, context, object,System.currentTimeMillis() / 1000,uploadFilePath,mHandler,dbWriter);
//						File file = new File(uploadFilePath);
//						if(file.exists()){
//							DeleteFilePic.delete(file);
//						}
//						Log.e("PutObject", "UploadSuccess");
//						Log.e("上传数", ""+MainActivity.shangchuan++);
//						new AsyncTask<Void, Void, Void>() {
//
//							@Override
//							protected Void doInBackground(Void... params) {
//								if (file != null) {
//									DeleteFilePic.delete(file);
//								}
//								return null;
//							}
//
//							@Override
//							protected void onPostExecute(Void result) {
//								if (!file.exists()) {
//									Log.e("PutObject", "UploadSuccess");
//									Log.e("上传数", ""+MainActivity.shangchuan++);
//
//								}
//								super.onPostExecute(result);
//							}
//
//						}.execute();
					}

					@Override
					public void onFailure(PutObjectRequest request,
							ClientException clientExcepion,
							ServiceException serviceException) {
						new UploadFile().uploadFile(imgInfo, context, object,System.currentTimeMillis() / 1000,uploadFilePath,mHandler,dbWriter);
						// 请求异常
						if (clientExcepion != null) {
							// mHandler.removeMessages(0x123);
							// Message msg = new Message();
							// msg.what = 0x123;
							// mHandler.sendMessageDelayed(msg, 3000);
							// 本地异常如网络异常等
							clientExcepion.printStackTrace();
						}
						if (serviceException != null) {
							// 服务异常
							Log.e("ErrorCode", serviceException.getErrorCode());
							Log.e("RequestId", serviceException.getRequestId());
							Log.e("HostId", serviceException.getHostId());
							Log.e("RawMessage",
									serviceException.getRawMessage());
						}
						
						//发消息让屏保隐藏
					}
				});
	}

	public void sycupload(Long time) {
		// 构造上传请求
		PutObjectRequest put = new PutObjectRequest("sdk-baige", object,
				uploadFilePath);

		// 文件元信息的设置是可选的
		// ObjectMetadata metadata = new ObjectMetadata();
		// metadata.setContentType("application/octet-stream");// 设置content-type
		// metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath));
		// // 校验MD5
		// put.setMetadata(metadata);
		try {

			PutObjectResult putResult = oss.putObject(put);

			Log.d("PutObject", "UploadSuccess");
			
//			mHandler.sendEmptyMessage(1);
//
//			File file = new File(uploadFilePath);
//			if (file.exists()) {
//				DeleteFilePic.delete(file);
//			}
			new UploadFile().uploadTwoFile(imgInfo, context, object,time,uploadFilePath,mHandler,dbWriter);
			
//			Db db;
//			SQLiteDatabase dbWriter;
//			db = new Db(context);
//			dbWriter = db.getWritableDatabase();
//			dbWriter.delete("allpaths",
//					"alluploadpaths=?",
//					new String[] { uploadFilePath.substring(uploadFilePath.lastIndexOf("/")+1,uploadFilePath.length()) });
//			dbWriter.close();
//			db.close();
			Log.d("PutObject", "UploadSuccess");
//			Log.e("上传数",""+MainActivity.shangchuan++);
//			Intent intent = new Intent(
//					"jason.broadcast.action");
//			context.sendBroadcast(intent);
//			new AsyncTask<Void, Void, Void>() {
//
//				@Override
//				protected Void doInBackground(Void... params) {
//					Db db;
//					SQLiteDatabase dbWriter;
//					db = new Db(context);
//					dbWriter = db.getWritableDatabase();
//					dbWriter.delete("allpaths",
//							"alluploadpaths=?",
//							new String[] { uploadFilePath });
//					dbWriter.close();
//					db.close();
//					Log.d("PutObject", "UploadSuccess");
//					Log.e("上传数",""+MainActivity.shangchuan++);
//					Intent intent = new Intent(
//							"jason.broadcast.action");
//					context.sendBroadcast(intent);
//					return null;
//				}
//
//				@Override
//				protected void onPostExecute(Void result) {
//					Db db;
//					SQLiteDatabase dbWriter;
//					db = new Db(context);
//					dbWriter = db.getWritableDatabase();
//					dbWriter.delete("filepaths",
//							"uploadpaths=?",
//							new String[] { uploadFilePath });
//					dbWriter.close();
//					db.close();
//					super.onPostExecute(result);
//				}
//
//			}.execute();

		
		} catch (Exception e) {
			mHandler.sendEmptyMessage(2);
//			Db db;
//			SQLiteDatabase dbWriter;
//			db = new Db(context);
//			dbWriter = db.getWritableDatabase();
//			dbWriter.delete("filepaths", "uploadpaths=?",
//					new String[] { uploadFilePath });
//			dbWriter.close();
//			db.close();
			// 本地异常如网络异常等
		} 

	}

//	public void uploadtwo0() throws ClientException, ServiceException {
//		// 构造上传请求
//		PutObjectRequest put = new PutObjectRequest("sdk-baige", object,
//				uploadFilePath);
//
//		// 异步上传时可以设置进度回调
//		put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//			@Override
//			public void onProgress(PutObjectRequest request, long currentSize,
//					long totalSize) {
//				Log.d("PutObject", "currentSize: " + currentSize
//						+ " totalSize: " + totalSize);
//			}
//		});
//
//		OSSAsyncTask task = oss.asyncPutObject(put,
//				new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//					@Override
//					public void onSuccess(PutObjectRequest request,
//							PutObjectResult result) {
//						final File file = new File(uploadFilePath);
//						new UploadFile().uploadFile(imgInfo, context, object);
//						db = new Db(context);
//						dbWriter = db.getWritableDatabase();
//						new AsyncTask<Void, Void, Void>() {
//
//							@Override
//							protected Void doInBackground(Void... params) {
//								if (file != null) {
//									DeleteFilePic.delete(file);
//									dbWriter.delete("allpaths",
//											"alluploadpaths=?",
//											new String[] { uploadFilePath });
//								}
//								return null;
//							}
//
//							@Override
//							protected void onPostExecute(Void result) {
//								if (!file.exists()) {
//									dbWriter.delete("filepaths",
//											"uploadpaths=?",
//											new String[] { uploadFilePath });
//									dbWriter.close();
//									db.close();
//									Log.d("PutObject", "UploadSuccess");
//									Log.e("上传数", ""+MainActivity.shangchuan++);
//								}
//								super.onPostExecute(result);
//							}
//
//						}.execute();
//
//					}
//
//					@Override
//					public void onFailure(PutObjectRequest request,
//							ClientException clientExcepion,
//							ServiceException serviceException) {
//						db = new Db(context);
//						dbWriter = db.getWritableDatabase();
//						dbWriter.delete("filepaths", "uploadpaths=?",
//								new String[] { uploadFilePath });
//						dbWriter.close();
//						db.close();
//
//						// 请求异常
//						if (clientExcepion != null) {
//
//							// 本地异常如网络异常等
//							clientExcepion.printStackTrace();
//						}
//						if (serviceException != null) {
//							// 服务异常
//							Log.e("ErrorCode", serviceException.getErrorCode());
//							Log.e("RequestId", serviceException.getRequestId());
//							Log.e("HostId", serviceException.getHostId());
//							Log.e("RawMessage",
//									serviceException.getRawMessage());
//						}
//					}
//				});
//	}

	public File getDir() {
		// 得到SD卡根目录
		File dir = Environment.getExternalStorageDirectory();
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}

//	public void copyFile(String oldPath, String newPath) {
//		try {
//			int bytesum = 0;
//			int byteread = 0;
//			File oldfile = new File(oldPath);
//			if (oldfile.exists()) {
//				InputStream inStream = new FileInputStream(oldPath);
//				FileOutputStream fs = new FileOutputStream(newPath);
//				byte[] buffer = new byte[1444];
//				int length;
//				while ((byteread = inStream.read(buffer)) != -1) {
//					bytesum += byteread;
//					System.out.println(bytesum);
//					fs.write(buffer, 0, byteread);
//				}
//				inStream.close();
//			}
//		} catch (Exception e) {
//			System.out.println("copyFile");
//			e.printStackTrace();
//
//		}
//
//	}

}
