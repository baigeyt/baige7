package com.hyc.bean;

import com.hyc.baige7.Login;
import com.hyc.baige7.MainActivity;
import com.hyc.db.DBMacAddress;
import com.hyc.network.GetDeviceID;
import com.hyc.qidong.Reflesh;

public class InterWeb {
	public String MAC=getMac();
	//private String web = "http://api.360baige.com";
	private String web = "http://api.360baige.cn";
	
	private DBMacAddress dbMac;
	
	private String getMac(){
        
		
		String accesstoken = null;
		dbMac = new DBMacAddress();
		dbMac.creatDB();
		MacEntity macEntity = new MacEntity();
		macEntity = dbMac.query();
		if (macEntity.getMac() != null) {
			System.out.println("mac:  " + macEntity.getMac());
			accesstoken = macEntity.getMac();
			System.out.println("从数据库获取MAC");
		}
		if (accesstoken == null) {
			GetDeviceID getDeviceID = new GetDeviceID();
			accesstoken = getDeviceID.getMacAddress();
			System.out.println("accesstoken  " + accesstoken);
			if(accesstoken!=null){
				dbMac.insert(accesstoken);	
			}
		}
		return accesstoken;
    }

	private String URL_IC = web + "/iccard/list?accesstoken="
			+ MAC;

	private String URL_ONEIC = web + "/iccard/one?accesstoken="
			+ MAC + "&physicsno=" + MainActivity.physicsno;

	private String URL_SchoolInfo = web + "/company/info?accesstoken="
			+ MAC;
	
	private String URL_SchoolInfoMain = web + "/company/info?accesstoken="
			+ MAC;

	private String URL_UploadFile = web + "/resource/upload?accesstoken="
			+ MAC + "&type=2";

	private String URL_UploadRecord = web + "/icrecord/add?accesstoken="
			+ MAC;

	private String URL_ReUploadRecord = web + "icrecord/update?accesstoken="
			+ MAC;

	private String URL_RecResource = web + "/resource/get?accesstoken="
			+ MAC + "&resourceid=";

	public String getURL_IC() {
		return URL_IC;
	}

	public String getURL_ONEIC() {
		return URL_ONEIC;
	}

	public String getURL_UploadFile() {
		return URL_UploadFile;
	}

	public String getURL_UploadRecord() {
		return URL_UploadRecord;
	}

	public String getURL_ReUploadRecord() {
		return URL_ReUploadRecord;
	}

	public String getURL_RecResource() {
		return URL_RecResource;
	}

	public String getURL_SchoolInfo() {
		return URL_SchoolInfo;
	}
    public String getURL_SchoolInfoMain() {
		return URL_SchoolInfoMain;
	}
}
