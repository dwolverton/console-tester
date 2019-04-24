package com.github.dwolverton.consoletester;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.dwolverton.consoletester.match.Found;
import com.github.dwolverton.consoletester.match.Match;

public class Group {
	
	private IOTester io;
	private List<GroupEl> elements = new LinkedList<>();
	
	public Group(IOTester io) {
		this.io = io;
	}

	public Group out(Match match) {
		elements.add(new GroupEl(match, null, false));
		return this;
	}
	
	public Group out(Object match) {
		return out(Match.of(match));
	}
	
	public Group out(Match match, Consumer<Found> then) {
		elements.add(new GroupEl(match, then, false));
		return this;
	}
	
	public Group out(Object match, Consumer<Found> then) {
		return out(Match.of(match), then);
	}
	
	public Group maybeOut(Match match, Consumer<Found> then) {
		elements.add(new GroupEl(match, then, true));
		return this;
	}
	
	public Group maybeOut(Object match, Consumer<Found> then) {
		return maybeOut(Match.of(match), then);
	}
	
	public Group prompt(Match match, Object input) {
		elements.add(new GroupEl(match, m -> io.in(input), false));
		return this;
	}
	
	public Group prompt(Object match, Object input) {
		return prompt(Match.of(match), input);
	}
	
	public Group maybePrompt(Match match, Object input) {
		elements.add(new GroupEl(match, m -> io.in(input), true));
		return this;
	}
	
	public Group maybePrompt(Object match, Object input) {
		return maybePrompt(Match.of(match), input);
	}
	
	public void exec() {
		List<GroupEl> unmatched = new ArrayList<>(elements);
		
		Optional<Found> found = io.maybeOut(anyMatcher(unmatched));
		while (found.isPresent()) {
			Match match = found.get().getMatch();
			Optional<GroupEl> el = unmatched.stream().filter(e -> e.match == match).findFirst();
			if (el.isPresent()) {
				el.get().respond(found.get());
				unmatched.removeIf(e -> e == el.get());
			}
			found = io.maybeOut(anyMatcher(unmatched));
		}
		
		if (!unmatched.isEmpty() && unmatched.stream().anyMatch(el -> !el.optional) ) {
			// This will fail and generate the appropriate error.
			io.out(anyMatcher(unmatched));
		}
	}
	
	private static Match anyMatcher(List<GroupEl> elements) {
		return Match.any(elements.stream().map(g -> g.match).toArray(Match[]::new));
	}
	
	private static class GroupEl {
		public Match match;
		public Consumer<Found> response;
		public boolean optional;
		public GroupEl(Match match, Consumer<Found> response, boolean optional) {
			this.match = match;
			this.response = response;
			this.optional = optional;
		}
		public void respond(Found found) {
			if (response != null) {
				response.accept(found);
			}
		}
	}

}
