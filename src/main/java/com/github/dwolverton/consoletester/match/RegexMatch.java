package com.github.dwolverton.consoletester.match;

import java.util.Optional;
import java.util.regex.Pattern;

public class RegexMatch implements Match {

	private Pattern regex;

	public RegexMatch(Pattern regex) {
		this.regex = regex;
	}

	public RegexMatch(String regex) {
		this(regex, false);
	}

	public RegexMatch(String regex, boolean ignoreCase) {
		int flags = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;
		this.regex = Pattern.compile(regex, flags);
	}

	@Override
	public Optional<MatchInfo> match(String s, int startIndex) {
		java.util.regex.Matcher m = regex.matcher(s);
		if (m.find(startIndex)) {
			return Optional.of(new MatchInfo(this, m.start(), m.end()));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public String getPart(String s, int startIndex, int index) {
		java.util.regex.Matcher m = regex.matcher(s);
		if (m.find(startIndex)) {
			return m.group(index);
		} else {
			return null;
		}
	}

	@Override
	public String getExpectedMessage() {
		return "to match: <" + regex + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (regex != null) {
			result = prime * result + regex.toString().hashCode();
			result = prime * result + regex.flags();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegexMatch other = (RegexMatch) obj;
		if ((regex == null) != (other.regex == null)) {
			return false;
		} else if (regex == null) {
			return false;
		} else if (!regex.toString().equals(other.regex.toString()))
			return false;
		if (regex.flags() != other.regex.flags())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + regex + "]";
	}

}
