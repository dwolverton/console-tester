package com.github.dwolverton.consoletester.match;

/**
 * The result of a successful match in the program's output.
 */
public class Found {
	
	private String output;
	private MatchInfo matchInfo;
	
	public Found(String output, MatchInfo matchInfo) {
		super();
		this.output = output;
		this.matchInfo = matchInfo;
	}

	/**
	 * Return the string of output that matched.
	 */
	public String get() {
		return output.substring(matchInfo.getStart(), matchInfo.getEnd());
	}
	
	/**
	 * Return the entire line of output where the match was found.
	 */
	public String getLine() {
		return output.substring(
				LineUtils.findLineStart(output, matchInfo.getStart()),
				LineUtils.findLineEnd(output, matchInfo.getEnd()));
	}
	
	/**
	 * Some Matches can have parts within the match. For example regex Matches can have
	 * groups. Use this method to access the strings matching those parts.
	 * @param index usually starts at 1 for regex groups.
	 */
	public String getPart(int index) {
		return matchInfo.getMatch().getPart(output, matchInfo.getStart(), index);
	}
	
	/**
	 * Get the Match that was used to find this result. In the case of a {@link Match#any(Match...)},
	 * this will be the individual Match that was found first.
	 */
	public Match getMatch() {
		return matchInfo.getMatch();
	}

}
