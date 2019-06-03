package com.github.dwolverton.consoletester.match;

import static com.github.dwolverton.consoletester.match.LineUtils.LINE_SEPARATOR_PATTERN;

import java.util.Optional;
import java.util.regex.Matcher;

public class LineMatch implements Match {

	private Match must;
	private Match mustNot;
	
	public LineMatch(Match must, Match mustNot) {
		if (must == null) {
			throw new IllegalArgumentException("LineMatch requires a Match, but null provided.");
		}
		this.must = must;
		this.mustNot = mustNot;
	}

	@Override
	public Optional<MatchInfo> match(String s, int startIndex) {
		Matcher m = LINE_SEPARATOR_PATTERN.matcher(s);
		int lineStart = startIndex;
		while (m.find(lineStart)) {
			String line = s.substring(lineStart, m.start());
			MatchInfo infoInLine = matchLine(line);
			if (infoInLine != null) {
				return Optional.of(offsetMatch(infoInLine, lineStart));
			}
			lineStart = m.end();
		}
		
		String line = s.substring(lineStart);
		MatchInfo infoInLine = matchLine(line);
		if (infoInLine != null) {
			return Optional.of(offsetMatch(infoInLine, lineStart));
		}
		
		return Optional.empty();
	}
	
	private MatchInfo offsetMatch(MatchInfo infoInLine, int lineStart) {
		return new MatchInfo(infoInLine.getMatch(),
				infoInLine.getStart() + lineStart,
				infoInLine.getEnd() + lineStart);
	}
	
	private MatchInfo matchLine(String line) {
		Optional<MatchInfo> mi = must.match(line, 0);
		if (mi.isPresent()) {
			if (mustNot == null || !mustNot.match(line, 0).isPresent()) {
				return mi.get();
			}
		}
		return null; // not a match
	}

	@Override
	public String getPart(String s, int startIndex, int index) {
		return must.getPart(s, startIndex, index);
	}

	@Override
	public String getExpectedMessage() {
		String msg = "in a single line " + must.getExpectedMessage();
		if (mustNot != null) {
			msg += " and not " + mustNot.getExpectedMessage();
		}
		return msg;
	}
	
	

}
