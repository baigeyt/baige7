package com.hyc.bean;

public class Stu {
	private long cardno;
	private int type;
	private String name;
	private String classname;
	private int sex = 0;
	private String headportraitid;

	public String getHeadportraitid() {
		return headportraitid;
	}

	public void setHeadportraitid(String headportraitid) {
		this.headportraitid = headportraitid;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public long getCardno() {
		return cardno;
	}

	public void setCardno(long cardno) {
		this.cardno = cardno;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}
}
