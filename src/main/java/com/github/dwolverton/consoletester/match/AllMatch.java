package com.github.dwolverton.consoletester.match;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dwolverton.consoletester.match.Match;

public class AllMatch implements Match {

	private Match[] matches;

	public AllMatch(Match[] matches) {
		this.matches = matches;
	}

	@Override
	public Optional<MatchInfo> match(String s, int startIndex) {
		int start = Integer.MAX_VALUE, end = -1;
		for (Match m : matches) {
			Optional<MatchInfo> result = m.match(s, startIndex);
			if (result.isPresent()) {
				MatchInfo i = result.get();
				if (i.getStart() < start) {
					start = i.getStart();
				}
				if (i.getEnd() > end) {
					end = i.getEnd();
				}
			} else {
				return Optional.empty();
			}
		}
		return Optional.of(new MatchInfo(this, start, end));
	}

	@Override
	public Optional<MatchInfo> matchLast(String s, int startIndex) {
		int start = Integer.MAX_VALUE, end = -1;
		for (Match m : matches) {
			Optional<MatchInfo> result = m.matchLast(s, startIndex);
			if (result.isPresent()) {
				MatchInfo i = result.get();
				if (i.getStart() < start) {
					start = i.getStart();
				}
				if (i.getEnd() > end) {
					end = i.getEnd();
				}
			}
		}
		if (end == -1) {
			return Optional.empty();
		} else {
			return Optional.of(new MatchInfo(this, start, end));
		}
	}

	@Override
	public String getPart(String s, int startIndex, int index) {
		throw new UnsupportedOperationException(
				"Can't get part of an AllMatch");
	}

	@Override
	public String getExpectedMessage() {
		return Stream.of(matches).map(Match::getExpectedMessage)
				.collect(Collectors.joining(" and "));
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
		AllMatch other = (AllMatch) obj;
		if (!Arrays.equals(matches, other.matches))
			return false;
		return true;
	}

}
