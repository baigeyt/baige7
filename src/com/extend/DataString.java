package com.extend;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取系统当前日期，时间， 星期
 * 
 */

public class DataString {

	public static String StringData() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日 \nE \taa HH:mm");
		String str = formatter.format(curDate);
		// System.out.println(str);
		return str;
	}

	public static String StringData1() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String str = formatter.format(curDate);
		// System.out.println(str);
		return str;
	}

	public static String StringData3() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String str = formatter.format(curDate);
		// System.out.println(str);
		return str;
	}

	public static String StringData2() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = formatter.format(curDate);
		// System.out.println(str);
		return str;
	}

}
