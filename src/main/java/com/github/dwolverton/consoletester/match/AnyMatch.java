package com.github.dwolverton.consoletester.match;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dwolverton.consoletester.match.Match;

public class AnyMatch implements Match {

	private Match[] matches;

	public AnyMatch(Match[] matches) {
		this.matches = matches;
	}

	@Override
	public Optional<MatchInfo> match(String s, int startIndex) {
		MatchInfo first = null;
		for (Match m : matches) {
			Optional<MatchInfo> result = m.match(s, startIndex);
			if (result.isPresent()) {
				if (first == null
						|| result.get().getStart() < first.getStart()) {
					// create a new MatchInfo that has m as the matcher, not a submatch (if applicable)
					first = new MatchInfo(m, result.get().getStart(), result.get().getEnd());
				}
			}
		}
		return Optional.ofNullable(first);
	}

	@Override
	public Optional<MatchInfo> matchLast(String s, int startIndex) {
		Optional<MatchInfo> last = Optional.empty();
		for (Match m : matches) {
			Optional<MatchInfo> result = m.matchLast(s, startIndex);
			if (result.isPresent()) {
				if (!last.isPresent()
						|| result.get().getEnd() > last.get().getEnd()) {
					last = result;
				}
			}
		}
		return last;
	}

	@Override
	public String getPart(String s, int startIndex, int index) {
		throw new UnsupportedOperationException(
				"Can't get part of an AnyMatch");
	}

	@Override
	public String getExpectedMessage() {
		return Stream.of(matches).map(Match::getExpectedMessage)
				.collect(Collectors.joining(" or "));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(matches);
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
		AnyMatch other = (AnyMatch) obj;
		if (!Arrays.equals(matches, other.matches))
			return false;
		return true;
	}

}
