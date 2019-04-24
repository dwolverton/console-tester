package com.github.dwolverton.consoletester.match;

import java.util.Arrays;
import java.util.Optional;

public interface Match {
	
	static Match of(Object value) {
		if (value instanceof Match) {
			return (Match) value;
		}
		return exactWholeWord(value);
	}
	
	static Match exact(Object value) {
		return new ContainsMatch(String.valueOf(value), true);
	}
	
	static Match exactExactCase(Object value) {
		return new ContainsMatch(String.valueOf(value), false);
	}
	
	static Match exactWholeWord(Object value) {
		return new ContainsWholeWordMatch(String.valueOf(value), true);
	}
	
	static Match exactWholeWordExactCase(Object value) {
		return new ContainsWholeWordMatch(String.valueOf(value), false);
	}
	
	static Match regex(String value) {
		return new RegexMatch(value, true);
	}
	
	static Match regexExactCase(String value) {
		return new RegexMatch(value, false);
	}
	
	static Match exactLine(Object value) {
		return new LineMatch(String.valueOf(value), true);
	}
	
	static Match exactLineExactCase(Object value) {
		return new LineMatch(String.valueOf(value), false);
	}
	
	static Match any(Match... matches) {
		return new AnyMatch(matches);
	}
	
	static Match any(Object... matches) {
		Match[] matches2 = Arrays.stream(matches).map(Match::of).toArray(Match[]::new);
		return new AnyMatch(matches2);
	}
	
	static Match all(Match... matches) {
		return new AllMatch(matches);
	}
	
	static Match all(Object... matches) {
		Match[] matches2 = Arrays.stream(matches).map(Match::of).toArray(Match[]::new);
		return new AllMatch(matches2);
	}
	
	Optional<MatchInfo> match(String s, int startIndex);
	
	default Optional<MatchInfo> matchLast(String s, int startIndex) {
		Optional<MatchInfo> last = Optional.empty();
		Optional<MatchInfo> found = match(s, startIndex);
		while (found.isPresent()) {
			last = found;
			startIndex = found.get().getStart() + 1;
			found = match(s, startIndex);
		}
		return last;
	}
	
	String getPart(String s, int startIndex, int index);
	
	String getExpectedMessage();

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);

}
