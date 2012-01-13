package com.urbanesia.utils;

import java.util.Date;

public class Log {
	protected static final String APP_NAME = "Jajan";
	
	public static void out(String m) {
		Date timestamp = new Date();
		StringBuffer sb = new StringBuffer();
		sb.append("***");
		sb.append(APP_NAME);
		sb.append(" - ");
		sb.append(timestamp);
		sb.append(": ");
		sb.append(m);
		System.out.println(sb.toString());
	}
}
