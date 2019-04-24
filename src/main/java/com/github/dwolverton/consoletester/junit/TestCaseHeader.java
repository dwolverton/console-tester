package com.github.dwolverton.consoletester.junit;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestCaseHeader implements BeforeEachCallback {

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		String header = context.getRequiredTestMethod().getName();
		header = splitCamelCase(header);
		header = capitalizeFirstLetter(header);
		System.out.println();
		System.out.println(" === " + header + " ===");
	}

	private static String splitCamelCase(String s) {
		// Thanks:
		// https://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
		return s.replaceAll(
				"(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])",
				" ");
	}
	
	private static String capitalizeFirstLetter(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
