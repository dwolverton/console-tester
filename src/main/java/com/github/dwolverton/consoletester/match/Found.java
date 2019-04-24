package com.github.dwolverton.consoletester.match;

public class Found {
	
	private String output;
	private MatchInfo matchInfo;
	
	public Found(String output, MatchInfo matchInfo) {
		super();
		this.output = output;
		this.matchInfo = matchInfo;
	}

	public String get() {
		return output.substring(matchInfo.getStart(), matchInfo.getEnd());
	}
	
	public String getLine() {
		return output.substring(
				LineUtils.findLineStart(output, matchInfo.getStart()),
				LineUtils.findLineEnd(output, matchInfo.getEnd()));
	}
	
	public String getPart(int index) {
		return matchInfo.getMatch().getPart(output, matchInfo.getStart(), index);
	}
	
	public Match getMatch() {
		return matchInfo.getMatch();
	}

}
