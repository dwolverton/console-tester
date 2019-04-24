package com.github.dwolverton.consoletester.runner;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream that forwards to multiple other output streams.
 */
public class TeeOutputStream extends OutputStream {

	private OutputStream[] streams;

	public TeeOutputStream(OutputStream... streams) {
		this.streams = streams;
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(b);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(b, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream stream : streams) {
			stream.flush();
		}
	}

	@Override
	public void close() throws IOException {
		for (OutputStream stream : streams) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

}
