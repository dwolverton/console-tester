package com.github.dwolverton.consoletester;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.dwolverton.consoletester.match.Found;
import com.github.dwolverton.consoletester.match.Match;
import com.github.dwolverton.consoletester.match.MatchInfo;
import com.github.dwolverton.consoletester.runner.BlockEndType;
import com.github.dwolverton.consoletester.runner.IOBlock;
import com.github.dwolverton.consoletester.runner.Runnables;
import com.github.dwolverton.consoletester.runner.Runner;

public class IOTester {
	
	private static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");
	static {
		Runner.init(); // Tap into System.in as soon as possible.
	}
	
	private Set<Match> disallowed = new HashSet<>();
	
	private IOBlock block;
	private int outputOffset;
	
	/**
	 * Start a console program to test.
	 * @param mainClass a class that has a public static void main method to run.
	 */
	public void start(Class<?> mainClass) {
		start(Runnables.fromMainClassWithTempClassLoader(mainClass));
	}
	
	/**
	 * Start a console program to test.
	 * @param main a main method to run. For example,
	 *        <code>io.start(MyProgram::main)</code>
	 */
	public void start(Consumer<String[]> main) {
		start(Runnables.fromMainMethod(main));
	}
	
	/**
	 * Start a console program to test
	 * @param codeToTest a Runnable with the code to test. This can be used with a lambda.
	 *        For example, <code>io.start(() -> System.out.println("Hello!"));</code>
	 */
	public void start(Runnable codeToTest) {
		Runner.start(codeToTest);
		nextBlock();
	}
	
	/**
	 * Start a group. A group allows multiple I/O operations to be tested for in
	 * any order. See {@link Group}.
	 * 
	 * Generally this will done with method chaining. For example,
	 * <pre>{@code
	 *   io.group()
	 *     .out("Java")
	 *     .maybePrompt("lang?", "Java")
	 *     .exec();
	 * }</pre>
	 */
	public Group group() {
		return new Group(this);
	}
	
	/**
	 * Expect to find the given match in the output.
	 * @return information about the match that was found
	 * @throws AssertionError if the match was not found in the output.
	 */
	public Found out(Match match) throws AssertionError {
		return findNextInBlock(match);
	}
	
	/**
	 * Expect to find the given match in the output.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #out(Match)} with a specific
	 *        {@link Match}.
	 * @return information about the match that was found
	 * @throws AssertionError if the match was not found in the output.
	 */
	public Found out(Object match) throws AssertionError {
		return out(Match.of(match));
	}
	
	/**
	 * Expect to find the given match in the output. The given match must all be
	 * found in the same line. This will most likely apply to an {@link Match#all} match.
	 * @return information about the match that was found
	 * @throws AssertionError if the match was not found in the output.
	 */
	public Found lineOut(Match match) throws AssertionError {
		return findNextLineInBlock(match);
	}
	
	/**
	 * Expect to find the given match in the output. The given match must all be
	 * found in the same line. This will most likely apply to an {@link Match#all} match.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #out(Match)} with a specific
	 *        {@link Match}.
	 * @return information about the match that was found
	 * @throws AssertionError if the match was not found in the output.
	 */
	public Found lineOut(Object match) throws AssertionError {
		return lineOut(Match.of(match));
	}
	
	/**
	 * Try to find the given match in the output. If not found, do nothing.
	 * @return information about the match that was found, if any
	 */
	public Optional<Found> maybeOut(Match match) {
		try {
			return Optional.of(findNextInBlock(match));
		} catch (AssertionError e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Try to find the given match in the output. If not found, do nothing.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #maybeOut(Match)} with a specific
	 *        {@link Match}.
	 * @return information about the match that was found, if any
	 */
	public Optional<Found> maybeOut(Object match) {
		return maybeOut(Match.of(match));
	}
	
	/**
	 * Try to find the given match in the output. If not found, do nothing.
	 * If found, the given consumer function will be run with information about the
	 * matching output passed in.
	 * @return information about the match that was found, if any
	 */
	public Optional<Found> maybeOut(Match match, Consumer<Found> then) {
		return maybeOut(match).map(found -> {
			then.accept(found);
			return found;
		});
	}
	
	/**
	 * Try to find the given match in the output. If not found, do nothing.
	 * If found, the given consumer function will be run with information about the
	 * matching output passed in.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #maybeOut(Match, Consumer)} with a specific
	 *        {@link Match}.
	 * @return information about the match that was found, if any
	 */
	public Optional<Found> maybeOut(Object match, Consumer<Found> then) {
		return maybeOut(Match.of(match), then);
	}
	
	/**
	 * Expect the running program to request user console input. Respond with the given
	 * value as a line of input.
	 * @param input this value will be converted to a string and passed as a whole line of input
	 * @throws AssertionError if the program ends or hangs before requesting user input.
	 */
	public void in(Object input) throws AssertionError {
		if (block.getEndType() != BlockEndType.INPUT) {
			fail("Expected user to be able to enter <" + input + "> but " + block.getEndType().getActualMessage() + ".");
		}
		Runner.in(input);
		nextBlock();
	}
	
	/**
	 * Expect to find the given match in the output. Then expect the running program to request
	 * user console input. Respond with the given value as a line of input.
	 * <p> This is a shortcut for running {@link #out(Object)} then {@link #in(Object)}.
	 * @param match match to search for in output.
	 * @param input this value will be converted to a string and passed as a whole line of input
	 * @return information about the match that was found
	 * @throws AssertionError if the match was not found in the output or if the program ends
	 *         or hangs before requesting user input.
	 */
	public Found prompt(Match match, Object input) throws AssertionError {
		Found found = out(match);
		in(input);
		return found;
	}
	
	/**
	 * Expect to find the given match in the output. Then expect the running program to request
	 * user console input. Respond with the given value as a line of input.
	 * <p> This is a shortcut for running {@link #out(Object)} then {@link #in(Object)}.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #prompt(Match, Object)} with a specific
	 *        {@link Match}.
	 * @param input this value will be converted to a string and passed as a whole line of input
	 * @return information about the match that was found
	 * @throws AssertionError if the match was not found in the output or if the program ends
	 *         or hangs before requesting user input.
	 */
	public Found prompt(Object match, Object input) throws AssertionError {
		return prompt(Match.of(match), input);
	}
	
	/**
	 * Try to find the given match in the output. If not found, do nothing.
	 * If found, expect the running program to request
	 * user console input. Respond with the given value as a line of input.
	 * <p> This is a shortcut for running {@link #maybeOut(Object)} then {@link #in(Object)}
	 * if the output is found.
	 * @param match match to search for in output.
	 * @param input this value will be converted to a string and passed as a whole line of input
	 * @return information about the match that was found, if any
	 * @throws AssertionError the match is found, but then the program ends or hangs before
	 *         requesting user input.
	 */
	public Optional<Found> maybePrompt(Match match, Object input) throws AssertionError {
		return maybeOut(match, m -> in(input));
	}
	
	/**
	 * Try to find the given match in the output. If not found, do nothing.
	 * If found, expect the running program to request
	 * user console input. Respond with the given value as a line of input.
	 * <p> This is a shortcut for running {@link #maybeOut(Object)} then {@link #in(Object)}
	 * if the output is found.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #maybePrompt(Match, Object)} with a specific
	 *        {@link Match}.
	 * @param input this value will be converted to a string and passed as a whole line of input
	 * @return information about the match that was found, if any
	 * @throws AssertionError the match is found, but then the program ends or hangs before
	 *         requesting user input.
	 */
	public Optional<Found> maybePrompt(Object match, Object input) throws AssertionError {
		return maybePrompt(Match.of(match), input);
	}
	
	/**
	 * Start disallowing the given match. All subsequent expected output must match
	 * before this match is encountered. This can be used to make sure the program does
	 * not output a certain thing or to set up a point in the output before which other
	 * output must occur.
	 * <p> disallowed matches can be temporary. Remove them with the {@link #allow(Match)} method.
	 * @return the match that is disallowed
	 */
	public Match disallow(Match match) {
		disallowed.add(match);
		return match;
	}
	
	/**
	 * Start disallowing the given matches. All subsequent expected output must match
	 * before these matches are encountered. This can be used to make sure the program does
	 * not output a certain thing or to set up a point in the output before which other
	 * output must occur.
	 * <p> disallowed matches can be temporary. Remove them with the {@link #allow(Match)} method.
	 * <p> This is equivalent to calling {@link #disallow(Match)} for each individual match.
	 */
	public void disallow(Match... matches) {
		disallowed.addAll(Arrays.asList(matches));
	}
	
	/**
	 * Start disallowing the given match. All subsequent expected output must match
	 * before this match is encountered. This can be used to make sure the program does
	 * not output a certain thing or to set up a point in the output before which other
	 * output must occur.
	 * <p> disallowed matches can be temporary. Remove them with the {@link #allow(Match)} method.
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #disallow(Match)} with a specific
	 *        {@link Match}.
	 * @return the match that is disallowed
	 */
	public Match disallow(Object match) {
		return disallow(Match.of(match));
	}
	
	/**
	 * Start disallowing the given matches. All subsequent expected output must match
	 * before these matches are encountered. This can be used to make sure the program does
	 * not output a certain thing or to set up a point in the output before which other
	 * output must occur.
	 * @param matches if any of these are not Match instances, those values will be matched using
	 *        exact whole word matching ignoring case. To customize this use specific
	 *        {@link Match} types.
	 * <p> disallowed matches can be temporary. Remove them with the {@link #allow(Match)} method.
	 * <p> This is equivalent to calling {@link #disallow(Match)} for each individual match.
	 */
	public void disallow(Object... matches) {
		disallowed.addAll(Arrays.stream(matches).map(Match::of).collect(Collectors.toList()));
	}
	
	/**
	 * Re-allow a match that was previous disallowed.
	 */
	public void allow(Match match) {
		disallowed.remove(match);
	}
	
	/**
	 * Re-allow a match that was previous disallowed.
	 */
	public void allow(Object match) {
		disallowed.remove(Match.of(match));
	}
	
	/**
	 * Re-allow multiple matches that were previous disallowed.
	 */
	public void allow(Match... matches) {
		disallowed.removeAll(Arrays.asList(matches));
	}
	
	/**
	 * Re-allow multiple matches that were previous disallowed.
	 */
	public void allow(Object... matches) {
		disallowed.removeAll(Arrays.stream(matches).map(Match::of).collect(Collectors.toList()));
	}
	
	/**
	 * Re-allow all matches that were previous disallowed.
	 */
	public void allowAll() {
		disallowed.clear();
	}
	
	/**
	 * Used to create a block of code during which a certain output match is disallowed.
	 * For example,
	 * <pre>{@code
	 * io.disallowing(Match.exact("blue")).run(() -> {
	 *   io.out("red");
	 *   io.out("green");
	 * });
	 * }</pre>
	 */
	public Disallowing disallowing(Match... match) {
		return new Disallowing(match);
	}
	
	/**
	 * Used to create a block of code during which a certain output match is disallowed.
	 * For example,
	 * <pre>{@code
	 * io.disallowing(Match.exact("blue")).run(() -> {
	 *   io.out("red");
	 *   io.out("green");
	 * });
	 * }</pre>
	 * @param match this value will be matched using exact whole word matching ignoring case.
	 *        To customize this use the overloaded method {@link #disallowing(Match...)} with a specific
	 *        {@link Match}.
	 */
	public Disallowing disallowing(Object... match) {
		return new Disallowing(Arrays.stream(match).map(Match::of).toArray(Match[]::new));
	}
	
	public final class Disallowing {
		private Match[] matchers;
		
		private Disallowing(Match... matchers) {
			this.matchers = matchers;
		}
		
		public void run(Runnable code) {
			disallow(matchers);
			try {
				code.run();
			} finally {
				allow(matchers);
			}
		}
		
		public void run(Consumer<IOTester> code) {
			disallow(matchers);
			try {
				code.accept(IOTester.this);
			} finally {
				allow(matchers);
			}
		}
	}
	
	/**
	 * Run to the end expecting no more input required.
	 */
	public void end() {
		Runner.terminate(false);
		if (block.getEndType() != BlockEndType.END) {
			fail("Expected end of program but " + block.getEndType().getActualMessage() + ".");
		}
	}
	
	/**
	 * Terminate the program.
	 */
	public void skipToEnd() {
		Runner.terminate(true);
	}
	
	/**
	 * @return never null
	 * @throws AssertionError if no match found
	 */
	private Found findNextInBlock(Match matcher) throws AssertionError {
		MatchInfo info = matcher.match(block.getOutput(), outputOffset).orElse(null);
		if (info == null) {
			return fail("Expected console output " + matcher.getExpectedMessage() + " but " + block.getEndType().getActualMessage() + ".");
		} else {
			if (isDisallowedBefore(info.getEnd())) {
				return fail("Expected console output " + matcher.getExpectedMessage() + " but found output " + Match.any(disallowed.stream().toArray(Match[]::new)).getExpectedMessage());
			}
			outputOffset = info.getEnd();
			return new Found(block.getOutput(), info);
		}
	}
	
	/**
	 * @return never null
	 * @throws AssertionError if no match found
	 */
	private Found findNextLineInBlock(Match matcher) throws AssertionError {
		Matcher m = LINE_SEPARATOR_PATTERN.matcher(block.getOutput());
		int lineStart = outputOffset;
		while (m.find(lineStart)) {
			String line = block.getOutput().substring(lineStart, m.start());
			MatchInfo infoInLine = matcher.match(line, 0).orElse(null);
			if (infoInLine != null) {
				if (isDisallowedBefore(infoInLine.getEnd() + lineStart)) {
					return fail("Expected console output line " + matcher.getExpectedMessage() + " but found output " + Match.any(disallowed.stream().toArray(Match[]::new)).getExpectedMessage());
				}
				
				MatchInfo info = new MatchInfo(infoInLine.getMatch(),
						infoInLine.getStart() + lineStart,
						infoInLine.getEnd() + lineStart);
				outputOffset = info.getEnd();
				return new Found(block.getOutput(), info);
			}
			lineStart = m.end();
		}
		return fail("Expected console output line " + matcher.getExpectedMessage() + " but " + block.getEndType().getActualMessage() + ".");
	}
	
	private boolean isDisallowedBefore(int position) {
		if (disallowed.isEmpty()) {
			return false;
		}
		Optional<MatchInfo> disallowedInfo = Match.any(disallowed.stream().toArray(Match[]::new)).match(block.getOutput(), outputOffset);
		return disallowedInfo.isPresent() && disallowedInfo.get().getStart() < position;
	}
	
	private void nextBlock() {
		block = Runner.nextBlock();
		outputOffset = 0;
	}
	
	public static <T> T fail(String message) throws AssertionError {
		throw new AssertionError(message);
	}

}
