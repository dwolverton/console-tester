package com.github.dwolverton.consoletester.match;

import java.util.regex.Pattern;

public class ContainsWholeWordMatch extends RegexMatch {

	private String expected;

	public ContainsWholeWordMatch(String expected) {
		this(expected, true);
	}

	public ContainsWholeWordMatch(String expected, boolean ignoreCase) {
		super(buildPattern(expected), ignoreCase);
		this.expected = expected;
	}

	@Override
	public String getExpectedMessage() {
		return "to contain: <" + expected + ">";
	}
	
	private static String buildPattern(String expected) {
		String pattern = Pattern.quote(expected);
		char first = expected.charAt(0);
		char last = expected.charAt(expected.length() - 1);
		if (Character.isAlphabetic(first)) {
			pattern = "(?<=^|[^A-Za-z])" + pattern;
		} else if (Character.isDigit(first)) {
			// must not be preceded by digit or minus
			pattern = "(?<=^|[^\\d\\-])" + pattern;
		}
		if (Character.isAlphabetic(last)) {
			pattern = pattern + "(?=$|[^A-Z-a-z])";
		} else if (Character.isDigit(last)) {
			pattern = pattern + "(?=$|\\D)";
		}
		return pattern;
	}

}
