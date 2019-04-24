package com.github.dwolverton.consoletester.match;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineUtils {

	
	// From java.util.Scanner
	public static final String LINE_SEPARATOR_PATTERN_STR = "\r\n|[\n\r\u2028\u2029\u0085]";
	public static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile(LINE_SEPARATOR_PATTERN_STR);
	public static final String LINE_START_PATTERN = "(?<=^|" + LINE_SEPARATOR_PATTERN_STR + ")";
	public static final String LINE_END_PATTERN = "(?=$|" + LINE_SEPARATOR_PATTERN_STR + ")";
	
	public static int findLineStart(String block, int position) {
		int start = 0;
		Matcher m = LINE_SEPARATOR_PATTERN.matcher(block);
		while (m.find(start) && m.end() <= position) {
			start = m.end();
		}
		return start;
	}
	
	public static int findLineEnd(String block, int position) {
		Matcher m = LINE_SEPARATOR_PATTERN.matcher(block);
		if (m.find(position)) {
			return m.start();
		} else {
			return block.length();
		}
	}
	
}
