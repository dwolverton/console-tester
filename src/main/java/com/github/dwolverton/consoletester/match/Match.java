package com.github.dwolverton.consoletester.match;

import java.util.Arrays;
import java.util.Optional;

/**
 * A Match is used to specify what to look for a program's output.
 * The static methods of this interface are factories for the various types
 * of Matches an are the preferred way to create a Match instance.
 */
public interface Match {
	
	/**
	 * Converts and object to a Match.
	 * If it is not already a Match the value is used to create an exactWholeWord Match.
	 */
	static Match of(Object value) {
		if (value instanceof Match) {
			return (Match) value;
		}
		return exactWholeWord(value);
	}
	
	/**
	 * Return a Match that looks for the exact string provided.
	 * Case is ignored, and the string could be anywhere within a word.
	 * For example <code>Match.exact("5")</code> would match "255.4" and
	 * <code>Match.exact("dance")</code> would match "attendance". If you
	 * want whole word matching, use {@link Match#exactWholeWord(Object)}
	 * @param value a string to match, or if not a string it will be converted to a string.
	 */
	static Match exact(Object value) {
		return new ContainsMatch(String.valueOf(value), true);
	}
	
	/**
	 * Return a Match that looks for the exact string provided.
	 * Case must match exactly, but the string could be anywhere within a word.
	 * For example <code>Match.exact("5")</code> would match "255.4" and
	 * <code>Match.exact("dance")</code> would match "attendance". If you
	 * want whole word matching, use {@link Match#exactWholeWord(Object)}
	 * @param value a string to match, or if not a string it will be converted to a string.
	 */
	static Match exactExactCase(Object value) {
		return new ContainsMatch(String.valueOf(value), false);
	}
	
	/**
	 * Return a Match that looks for the exact string provided.
	 * Case is ignored, and the string cannot be just part of a word.
	 * For example <code>Match.exactWholeWord("5")</code> would not match "255.4" and
	 * <code>Match.exactWholeWord("dance")</code> would not match "attendance".
	 * @param value a string to match, or if not a string it will be converted to a string.
	 */
	static Match exactWholeWord(Object value) {
		return new ContainsWholeWordMatch(String.valueOf(value), true);
	}
	
	/**
	 * Return a Match that looks for the exact string provided.
	 * Case must match exactly. The string cannot be just part of a word.
	 * For example <code>Match.exactWholeWord("5")</code> would not match "255.4" and
	 * <code>Match.exactWholeWord("dance")</code> would not match "attendance".
	 * @param value a string to match, or if not a string it will be converted to a string.
	 */
	static Match exactWholeWordExactCase(Object value) {
		return new ContainsWholeWordMatch(String.valueOf(value), false);
	}
	
	/**
	 * Return a Match that looks for the given regular expression.
	 * Case is ignored.
	 */
	static Match regex(String value) {
		return new RegexMatch(value, true);
	}
	
	/**
	 * Return a Match that looks for the given regular expression.
	 * Case must match exactly.
	 */
	static Match regexExactCase(String value) {
		return new RegexMatch(value, false);
	}
	
	/**
	 * Return a Match that looks for a line that is a exactly the value provided.
	 * The value must be the entire line from start to end. Case is ignored.
	 * @param value a string to match, or if not a string it will be converted to a string.
	 */
	static Match exactLine(Object value) {
		return new LineMatch(String.valueOf(value), true);
	}
	
	/**
	 * Return a Match that looks for a line that is a exactly the value provided.
	 * The value must be the entire line from start to end. Case must match exactly.
	 * @param value a string to match, or if not a string it will be converted to a string.
	 */
	static Match exactLineExactCase(Object value) {
		return new LineMatch(String.valueOf(value), false);
	}
	
	/**
	 * Return a Match that will match the first of the given Matches that is found.
	 */
	static Match any(Match... matches) {
		return new AnyMatch(matches);
	}
	
	/**
	 * Return a Match that will match the first of the given Matches that is found.
	 * @param matches matches do not need to be Match instances. If any are not, they
	 *        will be converted with {@link Match#of}.
	 */
	static Match any(Object... matches) {
		Match[] matches2 = Arrays.stream(matches).map(Match::of).toArray(Match[]::new);
		return new AnyMatch(matches2);
	}
	
	/**
	 * Return a Match that will match only if it finds all of the given matches.
	 */
	static Match all(Match... matches) {
		return new AllMatch(matches);
	}
	
	/**
	 * Return a Match that will match only if it finds all of the given matches.
	 * @param matches matches do not need to be Match instances. If any are not, they
	 *        will be converted with {@link Match#of}.
	 */
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
