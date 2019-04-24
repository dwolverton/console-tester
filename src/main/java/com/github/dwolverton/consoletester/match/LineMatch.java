package com.github.dwolverton.consoletester.match;

import java.util.regex.Pattern;

public class LineMatch extends RegexMatch {

	private String expected;

	public LineMatch(String expected) {
		this(expected, true);
	}

	public LineMatch(String expected, boolean ignoreCase) {
		super(LineUtils.LINE_START_PATTERN + Pattern.quote(expected) + LineUtils.LINE_END_PATTERN, ignoreCase);
		this.expected = expected;
	}

	@Override
	public String getExpectedMessage() {
		return "to contain line: <" + expected + ">";
	}

}
