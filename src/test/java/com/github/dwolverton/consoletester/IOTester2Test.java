package com.github.dwolverton.consoletester;
import static com.github.dwolverton.consoletester.TestUtil.assertFails;
import static com.github.dwolverton.consoletester.match.Match.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.IOTester;
import com.github.dwolverton.consoletester.junit5.GradingTest;
import com.github.dwolverton.consoletester.match.Found;
import com.github.dwolverton.consoletester.match.Match;

@GradingTest
class IOTester2Test {

	@Test
	void testStdOut(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
		});
		
		io.out("Hello");
		io.out("Goodbye");
		io.end();
	}

	@Test
	void testWrongOut(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbyes");
		});
		
		io.out("Hello");
		assertFails("Expected console output to contain: <Goodbye> but the program ended.", () -> {
			io.out("Goodbye");
		});
		io.end();
	}
	
	@Test
	void testNotEnoughOutput(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
		});
		
		io.out("Hello");
		io.out("Goodbye");
		assertFails("Expected console output to contain: <p.s.> but the program ended.", () -> {
			io.out("p.s.");
		});
		io.end();
	}
	
	@Test
	void testOutputWrongOrder(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
		});
		
		io.out("Goodbye");
		assertFails("Expected console output to contain: <Hello> but the program ended.", () -> {
			io.out("Hello");
		});
		io.end();
	}
	
	@Test
	void testOutputBackToBack(IOTester io) {
		io.start(() -> {
			System.out.println("Everything is awesome!");
		});
		
		io.out("Everything");
		io.out("is");
		io.out("awesome");
		io.out("!");
		io.end();
	}
	
	@Test
	void testOutputBackToBack2(IOTester io) {
		io.start(() -> {
			System.out.println("Everythingisawesome!");
		});
		
		io.out(exact("Everything"));
		io.out(exact("is"));
		io.out(exact("awesome"));
		io.out(exact("!"));
		io.end();
	}
	
	@Test
	void testOutputBackToBack3(IOTester io) {
		io.start(() -> {
			System.out.println("Everythingishot!");
		});
		
		io.out(exact("Everything"));
		io.out(exact("is"));
		assertFails("Expected console output to contain: <shot> but the program ended.", () -> {
			io.out(exact("shot"));
		});
		io.end();
	}
	
	@Test
	void testEndsWithoutEndline(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.print("Goodbye");
		});
		
		io.out("Hello");
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testOutputWithInputNextLine(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("What's your name?");
		io.in("David");
		io.out("Hello David");
		io.end();
	}
	
	@Test
	void testOutputWithInputNextLine2(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("What's your name?");
		io.in("John Jacob Jingelheimer-Smidt");
		io.out("Hello John Jacob Jingelheimer-Smidt");
		io.end();
	}
	
	@Test
	void testOutputWithInputSameLine(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.print("What's your name? ");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("Welcome.");
		io.out("What's your name?");
		io.in("David");
		io.out("Hello David");
		io.end();
	}
	
	@Test
	void testOptionalBeforeInputAbsent(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("Welcome.");
		io.out("What's your name?");
		io.maybeOut(">");
		io.in("David");
		io.out("Hello David");
		io.end();
	}
	
	@Test
	void testOptionalBeforeInputPresent(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			System.out.println("> ");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("Welcome.");
		io.out("What's your name?");
		io.maybeOut(">");
		io.in("David");
		io.out("Hello David");
		io.end();
	}
	
	@Test
	void testOptionalBeforeInputPresentNoEndline(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			System.out.print("> ");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("Welcome.");
		io.out("What's your name?");
		io.maybeOut(">");
		io.in("David");
		io.out("Hello David");
		io.end();
	}
	
	@Test
	void testFailWhenHungOnInput(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.close();
		});
		io.out("Welcome.");
		assertFails("Expected console output to contain: <What's your name?> but the program is waiting for user input.", () -> {
			io.out("What's your name?");
		});
		io.in("David");
		io.out("Hello David");
		io.end();
	}
	
	@Test
	void testFailWhenExpectEndButHungOnInput(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			String name = scnr.nextLine();
			System.out.println("Hello " + name);
			scnr.nextLine(); // extra nextLine
			scnr.close();
		});
		io.out("Welcome.");
		io.out("What's your name?");
		io.in("David");
		io.out("Hello David");
		assertFails("Expected end of program but the program is waiting for user input.", () -> {
			io.end();
		});
	}
	
	@Test
	void testScannerMethods(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Welcome.");
			System.out.println("What's your name?");
			String name = scnr.next();
			name += " " + scnr.next();
			name += " " + scnr.next();
			System.out.print("What's your age? ");
			int age = scnr.nextInt();
			
			System.out.println("Hello " + name + ". You're " + age + ".");
			scnr.close();
		});
		io.out("Welcome.");
		io.out("What's your name?");
		io.in("David   Grand");
		io.in("Circus");
		io.out("What's your age?");
		io.in("492 years old.");
		io.out("Hello David Grand Circus. You're 492.");
		io.end();
	}
	
	@Test
	void testStudentCodeErrors(IOTester io) {
		io.start(() -> {
			System.out.println("Alpha");
			"Hello".charAt(100);
			System.out.println("Omega");
		});
		io.out("Alpha");
		assertFails("Expected console output to contain: <Omega> but the program terminated with an exception.", () -> {
			io.out("Omega");
		});
		assertFails("Expected end of program but the program terminated with an exception.", () -> {
			io.end();
		});
	}
	
	@Test
	void testStudentCodeErrorsAtEnd(IOTester io) {
		io.start(() -> {
			System.out.println("Alpha");
			"Hello".charAt(100);
		});
		io.out("Alpha");
		assertFails("Expected end of program but the program terminated with an exception.", () -> {
			io.end();
		});
	}
	
	@Test
	void testSkipToEnd(IOTester io) {
		io.start(() -> {
			System.out.println("Alpha");
			System.out.println("Beta");
			System.out.println("Gamma");
		});
		io.out("Alpha");
		io.skipToEnd();
	}
	
	@Test
	void testSkipToEndWithInput(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Alpha");
			System.out.println("Beta");
			String word = scnr.nextLine();
			System.out.println("Gamma " + word);
			scnr.close();
		});
		io.out("Alpha");
		io.skipToEnd();
	}
	
	@Test
	void testFound(IOTester io) {
		io.start(() -> {
			System.out.println("Welcome.");
			System.out.println("Hello David.");
		});
		Found found = io.out("Welcome.");
		assertEquals("Welcome.", found.get());
		found = io.out(Match.regex("Hello (\\w+)"));
		assertEquals("Hello David", found.get());
		assertEquals("David", found.getPart(1));
		io.end();
	}
	
	@Test
	void testOutAcrossLines(IOTester io) {
		io.start(() -> {
			System.out.println("Word One");
			System.out.println("Word Two");
		});
		
		io.out(all("One", "Two"));
		io.end();
	}
	
	@Test
	void testLineOut(IOTester io) {
		io.start(() -> {
			System.out.println("Word One");
			System.out.println("Word Two");
			System.out.println("Word Three");
		});
		
		io.out(lineWith("Word One"));
		io.out(lineWith(all("Word", "Two")));
		io.out(lineWith(all("Word", "Three")));
		io.end();
	}
	
	@Test
	void testLineOut2(IOTester io) {
		io.start(() -> {
			System.out.println("Word One");
			System.out.println("Word Two");
			System.out.println("Word Three");
		});
		
		io.out(lineWith(all("Three", "Word")));
		io.end();
	}
	
	@Test
	void testLineOut3(IOTester io) {
		io.start(() -> {
			System.out.println("Word One");
			System.out.println("Word Two");
		});
		
		assertFails("Expected console output in a single line to contain: <One> and to contain: <Two> but the program ended.", () -> {
			io.out(lineWith(all("One", "Two")));
		});
		io.end();
	}
	
	@Test
	void testDisallowedNotEncountered(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
			System.out.println("Greetings");
		});
		
		io.disallow("Goodbye");
		io.out("Hello");
		io.allow("Goodbye");
		io.out("Greetings");
		io.end();
	}
	
	@Test
	void testDisallowedEncountered(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
			System.out.println("Greetings");
		});
		
		io.disallow("Goodbye");
		io.out("Hello");
		assertFails("Expected console output to contain: <Greetings> but found output to contain: <Goodbye>", () -> {
			io.out("Greetings");
		});
	}
	
	@Test
	void testDisallowedWithInput(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			String choice;
			do {
				System.out.println("Continue?");
				choice = scnr.next();
			} while (choice.equals("yes"));
			System.out.println("Done.");
			scnr.close();
		});
		
		io.disallow("Done");
		io.out("Continue?");
		io.in("yes");
		io.out("Continue?");
		io.in("yes");
		io.prompt("Continue?", "yes");
		io.in("yes");
		io.prompt("Continue?", "no");
		io.allow("Done");
		io.out("Done.");
		io.end();
	}
	
	@Test
	void testDisallowedWithInput2(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			String choice;
			do {
				System.out.println("Continue?");
				choice = scnr.next();
			} while (choice.equals("yes"));
			System.out.println("Done.");
			scnr.close();
		});
		
		io.disallow("Done");
		io.out("Continue?");
		io.in("yes");
		io.out("Continue?");
		io.in("yes");
		io.prompt("Continue?", "yes");
		io.in("yes");
		io.prompt("Continue?", "no");
		assertFails("Expected console output to contain: <Done.> but found output to contain: <Done>", () -> {
			io.out("Done.");
		});
	}
	
	@Test
	void testDisallowing(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
			System.out.println("Greetings");
		});
		
		io.disallowing("Goodbye").run(() -> io.out("Hello"));
		io.out("Greetings");
		io.end();
	}
	
	@Test
	void testDisallowing2(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
			System.out.println("Greetings");
		});
		
		io.disallowing("Goodbye").run(i -> {
			i.out("Hello");
		});
		io.out("Greetings");
		io.end();
	}
	
	@Test
	void testDisallowing3(IOTester io) {
		io.start(() -> {
			System.out.println("Hello");
			System.out.println("Goodbye");
			System.out.println("Greetings");
		});
		
		io.disallowing("Goodbye").run(() -> {
			assertFails("Expected console output to contain: <Greetings> but found output to contain: <Goodbye>", () -> {
				io.out("Greetings");
			});
		});
		io.end();
	}
	
	@Test
	void testFoundLine(IOTester io) {
		io.start(() -> {
			System.out.println("Hello David");
			System.out.println("Goodbye");
		});
		
		assertEquals("Hello David", io.out("Hello").getLine());
		assertEquals("Goodbye", io.out("Goodbye").getLine());
		io.end();
	}
	
	@Test
	void testFoundLine2(IOTester io) {
		io.start(() -> {
			System.out.println("Hello David");
			System.out.println("You're awesome");
			System.out.println("Goodbye");
		});
		
		assertEquals("You're awesome", io.out(any("Goodbye", "awesome")).getLine());
		io.end();
	}
	
	@Test
	void testMultipleRuns(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("What's your name?");
			String name = scnr.next();
			System.out.println("Hi " + name);
			scnr.close();
		});
		
		io.out("What's your name?");
		io.in("David");
		io.out("Hi David");
		io.end();
		
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("What's your name?");
			String name = scnr.next();
			System.out.println("Hi " + name);
			scnr.close();
		});
		
		io.out("What's your name?");
		io.in("Mariah");
		io.out("Hi Mariah");
		io.end();
	}
	
	
	// maybeIn
	// disallow to end
	// group.lineOut
	
	// Test prompt()
	// too much data to stdout
	
}
