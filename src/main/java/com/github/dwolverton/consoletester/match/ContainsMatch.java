package com.github.dwolverton.consoletester.match;

import java.util.regex.Pattern;

public class ContainsMatch extends RegexMatch {

	private String expected;

	public ContainsMatch(String expected) {
		this(expected, true);
	}

	public ContainsMatch(String expected, boolean ignoreCase) {
		super(Pattern.quote(expected), ignoreCase);
		this.expected = expected;
	}

	@Override
	public String getExpectedMessage() {
		return "to contain: <" + expected + ">";
	}

}
