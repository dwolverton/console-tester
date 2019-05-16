package com.github.dwolverton.consoletester.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import com.github.dwolverton.consoletester.IOTester;

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
	
	public static Runnable fromMainClassWithTempClassLoader(Class<?> mainClass) {
		ClassLoader classLoader = new TempClassLoader(mainClass.getClassLoader());
		return fromMainClass(classLoader, mainClass.getName());
	}
	
	public static Runnable fromMainClass(String mainClassName) {
		ClassLoader classLoader = Runnables.class.getClassLoader();
		return fromMainClass(classLoader, mainClassName);
	}
	
	public static Runnable fromMainClassWithTempClassLoader(String mainClassName) {
		ClassLoader classLoader = new TempClassLoader(Runnables.class.getClassLoader());
		return fromMainClass(classLoader, mainClassName);
	}
	
	private static Runnable fromMainClass(ClassLoader classLoader, String className) {
		try {
			Class <?> tempMainClass = classLoader.loadClass(className);
			return fromMainClass(tempMainClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to load main class", e);
		}
	}
}
