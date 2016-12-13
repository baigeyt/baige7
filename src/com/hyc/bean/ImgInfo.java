package com.hyc.bean;

import java.io.File;

public class ImgInfo {
	int type;
	long cardno;
	File file;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getCardno() {
		return cardno;
	}
	public void setCardno(long cardno) {
		this.cardno = cardno;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
