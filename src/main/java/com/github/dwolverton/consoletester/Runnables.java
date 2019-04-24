package com.github.dwolverton.consoletester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class Runnables {

	public static Runnable fromMainMethod(Consumer<String[]> main) {
		return () -> main.accept(new String[0]);
	}
	
	public static Runnable fromMainClass(Class<?> mainClass) {
		try {
			Method main = mainClass.getMethod("main", String[].class);
			if (!Modifier.isPublic(main.getModifiers())) {
				IOTester.fail("main method must be public.");
			}
			if (!Modifier.isStatic(main.getModifiers())) {
				IOTester.fail("main method must be static.");
			}
			return () -> {
				try {
					main.invoke(null, (Object) new String[0]);
				} catch (InvocationTargetException e) {
					throw new RuntimeException("An runtime error occurred. See below for details.", e.getTargetException());
				} catch (IllegalAccessException | IllegalArgumentException e) {
					throw new RuntimeException("Unexpected failure calling main method", e);
				}
			};
		} catch (NoSuchMethodException | SecurityException e) {
			IOTester.fail("Unable to find main(String[] args) method in class " + mainClass.getName());
			return null; // this will never actually return because fail() throws.
		}	
	}
}
