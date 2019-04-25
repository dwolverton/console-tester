package com.github.dwolverton.consoletester;

import static com.github.dwolverton.consoletester.TestUtil.assertFails;
import static com.github.dwolverton.consoletester.match.Match.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.IOTester;
import com.github.dwolverton.consoletester.junit5.GradingTest;

@GradingTest
class MatchTest {

	@Test
	void testAny(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertEquals("Alpha", io.out(any("Alpha", "Beta")).get());
		assertEquals("Beta", io.out(any("Alpha", "Beta")).get());
		io.end();
	}
	
	@Test
	void testAnyNotMatch(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertFails("Expected console output to contain: <Gamma> or to contain: <ord> but the program ended.", () -> {
			io.out(any("Gamma", "ord"));
		});
		io.end();
	}
	
	@Test
	void testAll(IOTester io) {
		io.start(() -> {
			System.out.println("Hello.");
			System.out.println("The quick brown fox jumped over the lazy dog.");
			System.out.println("Goodbye.");
		});
		assertEquals("The quick brown fox jumped", io.out(all("jumped", "the", "fox")).get());
		assertFalse(io.maybeOut("Hello").isPresent());
		io.out("dog");
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testAll2(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertEquals("Alpha\nWord Beta", io.out(all("Alpha", "Beta")).get());
		io.end();
	}
	
	@Test
	void testAllNotMatch(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha Beta");
		});
		assertFails("Expected console output to contain: <Alpha> and to contain: <Gamma> but the program ended.", () -> {
			io.out(all("Alpha", "Gamma"));
		});
		io.end();
	}
	
	@Test
	void testContains(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertEquals("Alpha", io.out(exact("Alpha")).get());
		assertEquals("Bet", io.out(exact("Bet")).get());
		assertFails("Expected console output to contain: <Bet> but the program ended.", () -> {
			io.out(exact("Bet")).get();
		});
		io.end();
	}
	
	@Test
	void testContainsIgnoreCase(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertEquals("Alpha", io.out(exact("alpha")).get());
		assertEquals("Bet", io.out(exact("bet")).get());
		assertFails("Expected console output to contain: <bet> but the program ended.", () -> {
			io.out(exact("bet")).get();
		});
		io.end();
	}
	
	@Test
	void testContainsExactCase(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertFails("Expected console output to contain: <alpha> but the program ended.", () -> {
			io.out(exactExactCase("alpha")).get();
		});
		io.end();
	}
	
	@Test
	void testWholeWord(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertEquals("Alpha", io.out(exactWholeWord("Alpha")).get());
		assertFails("Expected console output to contain: <Bet> but the program ended.", () -> {
			io.out(exactWholeWord("Bet")).get();
		});
		io.end();
	}
	
	@Test
	void testWholeWordNumber(IOTester io) {
		io.start(() -> {
			System.out.println("12");
			System.out.println("Number 12");
			System.out.println("Number 12.");
			System.out.println("Number 12lbs");
			System.out.println("Number 12 lbs");
			System.out.println("Clear");
			System.out.println("Number 122.");
			System.out.println("Number 212.");
			System.out.println("Number -12.");
			
			System.out.println("34.9");
			System.out.println("Number 34.9");
			System.out.println("Number 34.9.");
			System.out.println("Number 34.9lbs");
			System.out.println("Number 34.9 lbs");
			System.out.println("Clear");
			System.out.println("Number 34.95");
			System.out.println("Number 234.9");
			System.out.println("Number -34.9");
		});
		assertEquals("12", io.out(12).get());
		assertEquals("12", io.out(12).get());
		assertEquals("12", io.out(12).get());
		assertEquals("12", io.out(12).get());
		assertEquals("12", io.out(12).get());
		io.out("Clear");
		assertFalse(io.maybeOut(12).isPresent());
		
		assertEquals("34.9", io.out("34.9").get());
		assertEquals("34.9", io.out("34.9").get());
		assertEquals("34.9", io.out("34.9").get());
		assertEquals("34.9", io.out("34.9").get());
		assertEquals("34.9", io.out("34.9").get());
		io.out("Clear");
		assertFalse(io.maybeOut("34.9").isPresent());
		io.end();
	}
	
	@Test
	void testWholeWordIgnoreCase(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertEquals("Alpha", io.out(exactWholeWord("alpha")).get());
		assertFails("Expected console output to contain: <bet> but the program ended.", () -> {
			io.out(exactWholeWord("bet")).get();
		});
		io.end();
	}
	
	@Test
	void testWholeWordExactCase(IOTester io) {
		io.start(() -> {
			System.out.println("Word Alpha");
			System.out.println("Word Beta");
		});
		assertFails("Expected console output to contain: <alpha> but the program ended.", () -> {
			io.out(exactWholeWordExactCase("alpha")).get();
		});
		io.end();
	}

}
