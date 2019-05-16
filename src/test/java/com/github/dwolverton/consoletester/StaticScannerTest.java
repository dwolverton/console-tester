package com.github.dwolverton.consoletester;

import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.junit5.GradingTest;

@GradingTest
class StaticScannerTest {

	@Test
	void testReuseScanner(IOTester io) {
		io.start(StaticScannerSample.class);
		io.out("What's your name?");
		io.in("Mickey");
		io.out("Hello Mickey");
	}
	
	@Test
	void testReuseScanner2(IOTester io) {
		io.start(StaticScannerSample::main);
		io.out("What's your name?");
		io.in("Mickey");
		io.out("Hello Mickey");
	}
	
	@Test
	void testReuseScannerAfterEOF(IOTester io) {
		io.start(StaticScannerSample.class);
		io.out("What's your name?");
		io.skipToEnd();
		
		// This is the test that requires reloading the target class to clear
		// the static scanner.
		io.start(StaticScannerSample.class);
		io.out("What's your name?");
		io.in("Mickey");
		io.out("Hello Mickey");
	}
	
	@Test
	void testReuseScanner3(IOTester io) {
		io.start(StaticScannerSample.class);
		io.out("What's your name?");
		io.in("Mickey");
		io.out("Hello Mickey");
		io.end();
		
		io.start(StaticScannerSample.class);
		io.out("What's your name?");
		io.in("Minnie");
		io.out("Hello Minnie");
		io.end();
	}

}
