package com.github.dwolverton.consoletester;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.junit.GradingTest;

@GradingTest
class StaticScannerTest {

	@Tag("TODO")
	@Test
	void test(IOTester io) {
		io.start(StaticScannerSample.class);
		io.out("What's your name?");
		io.in("Mickey");
		io.out("Hello Mickey");
	}
	
	@Tag("TODO")
	@Test
	void test2(IOTester io) {
		io.start(StaticScannerSample::main);
		io.out("What's your name?");
		io.in("Mickey");
		io.out("Hello Mickey");
	}

}
