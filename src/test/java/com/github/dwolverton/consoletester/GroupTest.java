package com.github.dwolverton.consoletester;

import static com.github.dwolverton.consoletester.TestUtil.assertFails;
import static com.github.dwolverton.consoletester.match.Match.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.IOTester;
import com.github.dwolverton.consoletester.junit.GradingTest;

@GradingTest
class GroupTest {
	
	@Test
	void testGroupAll(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 1");
			System.out.println("Hello 2");
			System.out.println("Goodbye");
		});
		
		io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testGroupAll2(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 1");
			System.out.println("Hello 2");
			System.out.println("Goodbye");
		});
		
		io.group().out(exact("Hello 2")).out(exact("Hello 1")).exec();
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testGroupAllButNoneFound(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 3");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 1> or to contain: <Hello 2> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		});
	}
	
	@Test
	void testGroupAllButOnlyOneFound(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 1");
			System.out.println("Hello 3");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 2> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		});
	}
	
	@Test
	void testGroupAllButOnlyOneFound2(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 2");
			System.out.println("Hello 3");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 1> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		});
	}
	
	@Test
	void testGroupAllButRepeat(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 1");
			System.out.println("Hello 1");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 2> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		});
	}
	
	@Test
	void testGroupAllButRepeat2(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 2");
			System.out.println("Hello 2");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 1> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		});
	}
	
	@Test
	void testGroupAllWithOptional(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 1");
			System.out.println("Hello 2");
			System.out.println("Hello 3");
			System.out.println("Goodbye");
		});
		
		io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testGroupAllWithOptional3(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 3");
			System.out.println("Hello 2");
			System.out.println("Hello 1");
			System.out.println("Goodbye");
		});
		
		io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testGroupAllWithOptional5(IOTester io) {
		io.start(() -> {
			System.out.println("Hello 1");
			System.out.println("Hello 3");
			System.out.println("Hello 2");
			System.out.println("Goodbye");
		});
		
		io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testGroupAllMultipleOptionalsWithResponses(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.print("Enter your name: ");
			String name = scnr.nextLine();
			System.out.println("Enter your age:");
			int age = scnr.nextInt();
			scnr.nextLine();
			System.out.println("Enter your pet:");
			scnr.nextLine();
			System.out.println("Hi " + name + ", the " + age + " year old.");
			scnr.close();
		});
		
		io.group()
			.prompt("name", "David")
			.prompt("age", 40)
			.maybePrompt("pet", "Snuffy")
			.exec();
		io.out("Hi David, the 40 year old.");
		io.end();
	}
	
	@Test
	void testGroupAllMultipleOptionalsWithResponses2(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.print("Enter your name: ");
			String name = scnr.nextLine();
			System.out.println("Enter your age:");
			int age = scnr.nextInt();
			System.out.println("Enter your pet:");
			scnr.next();
			System.out.println("Hi " + name + ", the " + age + " year old.");
			scnr.close();
		});
		
		io.group()
			.prompt("age", 40)
			.prompt("name", "David")
			.maybePrompt("pet", "Snuffy")
			.exec();
		io.out("Hi David, the 40 year old.");
		io.end();
	}
	
	@Test
	void testGroupAllMultipleOptionalsWithResponses3(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Enter your age:");
			int age = scnr.nextInt();
			scnr.nextLine();
			System.out.println("Enter your pet:");
			scnr.nextLine();
			System.out.print("Enter your name: ");
			String name = scnr.nextLine();
			System.out.println("Hi " + name + ", the " + age + " year old.");
			scnr.close();
		});
		
		io.group()
		.prompt("name", "David")
		.prompt("age", 40)
		.maybePrompt("pet", "Snuffy")
		.exec();
		io.out("Hi David, the 40 year old.");
		io.end();
	}
	
	@Test
	void testGroupSkipToAll(IOTester io) {
		io.start(() -> {
			System.out.println("Foo");
			System.out.println("Bar");
			System.out.println("Hello 1");
			System.out.println("Hello 2");
			System.out.println("Goodbye");
		});
		
		io.group().out(exact("Hello 1")).out(exact("Hello 2")).exec();
		io.out("Goodbye");
		io.end();
	}
	
	@Test
	void testGroupSkipToAllButNeverFound(IOTester io) {
		io.start(() -> {
			System.out.println("Foo");
			System.out.println("Bar");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 1> or to contain: <Hello 2> or to contain: <Hello 3> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).maybePrompt(exact("Hello 3"), "X").exec();
		});
	}
	
	@Test
	void testGroupSkipToAllButHungOnInput(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Foo");
			System.out.println("Bar");
			scnr.nextLine();
			System.out.println("Goodbye");
			scnr.close();
		});
		
		assertFails("Expected console output to contain: <Hello 1> or to contain: <Hello 2> or to contain: <Hello 3> but the program is waiting for user input.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).maybePrompt(exact("Hello 3"), "X").exec();
		});
	}
	
	@Test
	void testGroupSkipToAllButOnlyOptionalFound(IOTester io) {
		io.start(() -> {
			System.out.println("Foo");
			System.out.println("Bar");
			System.out.println("Hello 3");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Hello 1> or to contain: <Hello 2> but the program ended.", () -> {
			io.group().out(exact("Hello 1")).out(exact("Hello 2")).maybeOut(exact("Hello 3"), f -> {}).exec();
		});
	}
	
	@Test
	void testGroupSkipToAllWithResponse(IOTester io) {
		io.start(() -> {
			Scanner scnr = new Scanner(System.in);
			System.out.println("Foo");
			System.out.println("Bar");
			System.out.println("Hello 1");
			String a = scnr.nextLine();
			System.out.println("Hello 2");
			String b = scnr.nextLine();
			System.out.println("Goodbye " + a + b);
			scnr.close();
		});
		
		io.group().prompt(exact("Hello 1"), "A").prompt(exact("Hello 2"), "B").exec();
		io.out("Goodbye AB");
		io.end();
	}
	
	@Test
	void testGroupWithAnyMatcher(IOTester io) {
		io.start(() -> {
			System.out.println("Hello Aaron");
			System.out.println("Hello Betty");
			System.out.println("Goodbye");
		});
		
		io.group()
			.out(any("Aaron", "Elsa"), m -> assertEquals("Aaron", m.get()))
			.out(any("Blanch", "Betty"), m -> assertEquals("Betty", m.get()))
			.exec();
		io.out("Goodbye");
	}
	
	@Test
	void testGroupWithAnyMatcher2(IOTester io) {
		io.start(() -> {
			System.out.println("Hello Aaron");
			System.out.println("Hello Betty");
			System.out.println("Goodbye");
		});
		
		io.group()
			.out(any("Blanch", "Betty"), m -> assertEquals("Betty", m.get()))
			.out(any("Aaron", "Elsa"), m -> assertEquals("Aaron", m.get()))
			.exec();
		io.out("Goodbye");
	}
	
	@Test
	void testGroupWithAnyMatcher3(IOTester io) {
		io.start(() -> {
			System.out.println("Hello Aaron");
			System.out.println("Hello Betty");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Blanch> or to contain: <Sean> or to contain: <Kevin> or to contain: <Elsa> but the program ended.", () -> {
			io.group()
				.out(any("Blanch", "Sean"))
				.out(any("Kevin", "Elsa"))
				.exec();
		});
	}
	
	@Test
	void testGroupWithAnyMatcher4(IOTester io) {
		io.start(() -> {
			System.out.println("Hello Aaron");
			System.out.println("Hello Betty");
			System.out.println("Goodbye");
		});
		
		assertFails("Expected console output to contain: <Kevin> or to contain: <Elsa> but the program ended.", () -> {
			io.group()
				.out(any("Blanch", "Betty"))
				.out(any("Kevin", "Elsa"))
				.exec();
		});
	}
	
}
