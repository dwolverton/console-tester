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
import com.github.dwolverton.consoletester.runner.Runner;

public class IOTester {
	
	private static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");
	
	private Runner runner = new Runner();
	
	private Set<Match> disallowed = new HashSet<>();
	
	private IOBlock block;
	private int outputOffset;
	
	public void start(Class<?> mainClass) {
		start(Runnables.fromMainClass(mainClass));
	}
	
	public void start(Consumer<String[]> main) {
		start(Runnables.fromMainMethod(main));
	}
	
	public void start(Runnable codeToTest) {
		runner.start(codeToTest);
		nextBlock();
	}
	
	public Group group() {
		return new Group(this);
	}
	
	public Found prompt(Match matcher, Object input) {
		Found match = out(matcher);
		in(input);
		return match;
	}
	
	public Found prompt(Object matcher, Object input) {
		return prompt(Match.of(matcher), input);
	}
	
	public Optional<Found> maybePrompt(Match matcher, Object input) {
		return maybeOut(matcher, m -> in(input));
	}
	
	public Optional<Found> maybePrompt(Object matcher, Object input) {
		return maybePrompt(Match.of(matcher), input);
	}
	
	public Found out(Match matcher) {
		return findNextInBlock(matcher);
	}
	
	public Found out(Object matcher) {
		return out(Match.of(matcher));
	}
	
	public Found lineOut(Match matcher) {
		return findNextLineInBlock(matcher);
	}
	
	public Found lineOut(Object matcher) {
		return lineOut(Match.of(matcher));
	}
	
	public Optional<Found> maybeOut(Match matcher) {
		try {
			return Optional.of(findNextInBlock(matcher));
		} catch (AssertionError e) {
			return Optional.empty();
		}
	}
	
	public Optional<Found> maybeOut(Object matcher) {
		return maybeOut(Match.of(matcher));
	}
	
	public Optional<Found> maybeOut(Match matcher, Consumer<Found> then) {
		return maybeOut(matcher).map(found -> {
			then.accept(found);
			return found;
		});
	}
	
	public Optional<Found> maybeOut(Object matcher, Consumer<Found> then) {
		return maybeOut(Match.of(matcher), then);
	}
	
	public void in(Object input) {
		if (block.getEndType() != BlockEndType.INPUT) {
			fail("Expected user to be able to enter <" + input + "> but " + block.getEndType().getActualMessage() + ".");
		}
		runner.in(input);
		nextBlock();
	}
	
	public Match disallow(Match match) {
		disallowed.add(match);
		return match;
	}
	
	public void disallow(Match... matches) {
		disallowed.addAll(Arrays.asList(matches));
	}
	
	public Match disallow(Object match) {
		return disallow(Match.of(match));
	}
	
	public void disallow(Object... matches) {
		disallowed.addAll(Arrays.stream(matches).map(Match::of).collect(Collectors.toList()));
	}
	
	public void allow(Match match) {
		disallowed.remove(match);
	}
	
	public void allow(Object match) {
		disallowed.remove(Match.of(match));
	}
	
	public void allow(Match... matches) {
		disallowed.removeAll(Arrays.asList(matches));
	}
	
	public void allow(Object... matches) {
		disallowed.removeAll(Arrays.stream(matches).map(Match::of).collect(Collectors.toList()));
	}
	
	public void allowAll() {
		disallowed.clear();
	}
	
	public Disallowing disallowing(Match... match) {
		return new Disallowing(match);
	}
	
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
		runner.terminate(false);
		if (block.getEndType() != BlockEndType.END) {
			fail("Expected end of program but " + block.getEndType().getActualMessage() + ".");
		}
	}
	
	/**
	 * Terminate the program.
	 */
	public void skipToEnd() {
		runner.terminate(true);
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
				return fail("Expected console output " + matcher.getExpectedMessage() + " but found output " + Match.any(disallowed.toArray(Match[]::new)).getExpectedMessage());
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
					return fail("Expected console output line " + matcher.getExpectedMessage() + " but found output " + Match.any(disallowed.toArray(Match[]::new)).getExpectedMessage());
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
		Optional<MatchInfo> disallowedInfo = Match.any(disallowed.toArray(Match[]::new)).match(block.getOutput(), outputOffset);
		return disallowedInfo.isPresent() && disallowedInfo.get().getStart() < position;
	}
	
	private void nextBlock() {
		block = runner.nextBlock();
		outputOffset = 0;
	}
	
	public static <T> T fail(String message) throws AssertionError {
		throw new AssertionError(message);
	}

}
