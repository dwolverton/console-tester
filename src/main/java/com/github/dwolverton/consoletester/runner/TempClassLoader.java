package com.github.dwolverton.consoletester.runner;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is designed to work around the problem that when running multiple test cases
 * if the code under test has static fields they will not be reset.
 * 
 * The solution here is to load the classes under test freshly with each run.
 */
public class TempClassLoader extends ClassLoader {
	
	private ClassLoader parent;
	
	public TempClassLoader(ClassLoader parent) {
		// Give the superclass a null parent so that it does not automatically
		// resolve classes that have been loaded already.
        super(null);
        this.parent = parent;
    }

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String resourceName = name.replace('.', '/') + ".class";
        try (InputStream rawInput = parent.getResourceAsStream(resourceName);
        	 BufferedInputStream input = new BufferedInputStream(rawInput)) {
        	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }
            input.close();
            rawInput.close();

            byte[] classData = buffer.toByteArray();

            return defineClass(name,
                    classData, 0, classData.length);

        } catch (SecurityException e) {
        	return parent.loadClass(name);
        } catch (IOException e) {
            throw new ClassNotFoundException("Error reading class", e);
        }
	}
	
	
}
