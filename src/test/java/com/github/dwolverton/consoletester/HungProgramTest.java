package com.github.dwolverton.consoletester;

import static com.github.dwolverton.consoletester.TestUtil.assertFails;

import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.IOTester;
import com.github.dwolverton.consoletester.junit.GradingTest;

@GradingTest
class HungProgramTest {

	@Test
	void testEndWithInfiniteLoop(IOTester io) {
		io.start(() -> {
			System.out.println("Alpha");
			@SuppressWarnings("unused")
			int i = 0;
			while (true) {
				i++;
			}
		});
		io.out("Alpha");
		assertFails(
				"Expected end of program but the program is hung, perhaps in an infinite loop.",
				() -> {
					io.end();
				});
	}

	@Test
	void testSkipToEndWithInfiniteLoop(IOTester io) {
		io.start(() -> {
			System.out.println("Alpha");
			@SuppressWarnings("unused")
			int i = 0;
			while (true) {
				i++;
			}
		});
		io.out("Alpha");
		io.skipToEnd();
	}

}
