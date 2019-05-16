package com.github.dwolverton.consoletester.runner;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * This stream just lets us swap out the underlying input stream.
 */
public class ProxyInputStream extends FilterInputStream {

	public ProxyInputStream(InputStream in) {
		super(in);
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

}
