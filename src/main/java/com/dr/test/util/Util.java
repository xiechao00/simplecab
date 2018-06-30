package com.dr.test.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static String dateToString(Date date, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		df.setLenient(false);
		df.applyPattern(pattern);
		return df.format(date);
	}
}
