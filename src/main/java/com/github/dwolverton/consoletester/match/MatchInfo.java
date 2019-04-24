package com.github.dwolverton.consoletester.match;

public class MatchInfo {

	private Match match;
	private int start;
	private int end;

	public MatchInfo(Match match, int start, int end) {
		super();
		this.match = match;
		this.start = start;
		this.end = end;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return end - start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "MatchInfo [match=" + match + ", start=" + start + ", end=" + end
				+ "]";
	}

}
