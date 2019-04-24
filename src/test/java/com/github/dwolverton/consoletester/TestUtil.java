package com.github.dwolverton.consoletester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.function.Executable;

public class TestUtil {

	public static void assertFails(String expectedErrorMessage, Executable executable) {
		String actualErrorMsg = assertThrows(AssertionError.class, executable).getMessage();
		assertEquals(expectedErrorMessage, actualErrorMsg);
	}
}
