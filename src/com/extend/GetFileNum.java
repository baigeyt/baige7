package com.extend;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class GetFileNum {


	public  ArrayList<String> getFiles(File file) {
		ArrayList<String> list =new ArrayList<String>();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					getFiles(files[i]);
				} else {
					if(isImage(files[i].getAbsolutePath())){
						list.add(files[i].getName().substring(0,files[i].getName().lastIndexOf(".")));	
					}else{
						files[i].delete();
					}
				}
			}
		}
		return list;
	}
	//获取第一个子文件的文件名
   public String getFilesName(File root){  
		File files[] = root.listFiles();  	
    if(files != null){  
        for (File f : files){  
            if(f.isDirectory()){  
            	getFilesName(f);
                System.out.println("aa"+f);
            }else{  
                System.out.println("bb"+f);  
            }  
        }  
      }
	return files[0].getName();
    }

   
   //获取当前文件夹下子文件个数
   public int getAllFilesNum(File root){  
		File files[] = root.listFiles();  	
    if(files != null){  
        for (File f : files){  
            if(f.isDirectory()){  
            	getAllFilesNum(f);
                System.out.println("11"+f);
            }else{  
                System.out.println("22"+f);  
            }  
        }  
        return files.length;
      }
	return 0;
    }
   
   //创建baige文件夹  防止数据库报错
   public void creadFile(){
	   
	   String pic_url = getDir() + "/baige/db";
		File pic_file = new File(pic_url);
		if (pic_file.exists()) {
		} else {
			pic_file.mkdirs();
		}
   }
   private File getDir() {
		// 得到SD卡根目录
		File dir = Environment.getExternalStorageDirectory();
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}
   private  boolean isImage(String imagepath) {
		//
		Bitmap bitmap = BitmapFactory.decodeFile(imagepath);

		try {
			if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (bitmap != null) {
				bitmap.recycle();
			}
			bitmap = null;
		}
	}
}
