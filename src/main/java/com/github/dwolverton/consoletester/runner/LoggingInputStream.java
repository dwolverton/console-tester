package com.github.dwolverton.consoletester.runner;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LoggingInputStream extends FilterInputStream {
	
	public LoggingInputStream(InputStream in) {
		super(in);
	}

	// In order to keep any static shared Scanners alive, we can't permit out stream to send an EOF signal.
	@Override
	public int read() throws IOException {
		int b = super.read();
		System.out.println("read: " + b);
		return b;
	}

	// In order to keep any static shared Scanners alive, we can't permit out stream to send an EOF signal.
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int i = super.read(b, off, len);
		System.out.println("read: " + i + "(off=" + off + ", len=" + len + ") " + Arrays.toString(b));
		return i;
	}
	
	
}
