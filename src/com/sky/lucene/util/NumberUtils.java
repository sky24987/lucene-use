package com.sky.lucene.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

	private static final DecimalFormat formatter = new DecimalFormat(
			"00000000.00");
	private static final Pattern pattern = Pattern
			.compile("^([0]*)([1-9][0-9]*//.[0-9]*)$");

	public static String pad(float n) {
		return formatter.format(n);
	}
	
	public static String pad(double n) {
		return formatter.format(n);
	}

	public static String revert(String n) {
		Matcher matcher = pattern.matcher(n);
		matcher.matches();
		return matcher.group(2);
	}
}