package com.github.dwolverton.consoletester.runner;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Runner {

	private static PrintStream originalStdOut = System.out;
	private static InputStream originalStdIn = System.in;

	private BlockingQueue<IOBlock> queue = new LinkedBlockingQueue<>();

	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private PrintStream stdOutPrintStream;
	private PipedInputStream stdInInputStream;
	private PipedOutputStream stdInOutputStream;
	private PrintStream stdInPrintStream;
	private Thread thread;
	private boolean suppressStackTrace;
	
	public void start(Runnable runnable) {
		outStream.reset();
		try {
			stdInInputStream = new PipedInputStream();
			stdInOutputStream = new PipedOutputStream(stdInInputStream);
			stdInPrintStream = new PrintStream(stdInOutputStream);

			stdOutPrintStream = new PrintStream(new TeeOutputStream(outStream, originalStdOut), true, "UTF-8");
			System.setOut(stdOutPrintStream);
			System.setIn(new TappedInputStream(stdInInputStream));

		} catch (IOException e) {
			cleanUp();
			throw new RuntimeException(e);
		}
		
		thread = new Thread(() -> {
			RuntimeException exception = null;
			try {
				runnable.run();
			} catch (RuntimeException e) {
				exception = e;
				if (!suppressStackTrace) {
					e.printStackTrace();
				}
			} finally {
				String output = flushOutput();
				BlockEndType endType = exception == null ? BlockEndType.END
						: BlockEndType.EXCEPTION;
				queue.offer(new IOBlock(output, endType));
			}
		});
		thread.start();
	}

	public IOBlock nextBlock() {
		try {
			IOBlock block = queue.poll(3, TimeUnit.SECONDS);
			if (block == null) {
				return new IOBlock(flushOutput(), BlockEndType.HANG);
			} else {
				return block;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"Unexpected interrupt of test thread. This is likely a problem with the test, not your code.");
		}
	}
	
	public void in(Object line) {
		originalStdOut.println("«" + line + "»");
		stdInPrintStream.println(line);
		stdInPrintStream.flush();
	}
	
	public void terminate(boolean suppressStackTrace) {
		if (thread == null) {
			return;
		}
		this.suppressStackTrace = suppressStackTrace;
		if (thread.isAlive()) {
			thread.interrupt();
		}
		try {
			thread.join(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			cleanUp();
			this.suppressStackTrace = false;
		}
	}

	private String flushOutput() {
		stdOutPrintStream.flush();
		String output = new String(outStream.toByteArray(),
				StandardCharsets.UTF_8);
		outStream.reset();
		return output;
	}

	private void markInput() {
		queue.offer(new IOBlock(flushOutput(), BlockEndType.INPUT));
	}

	private void cleanUp() {
		stdOutPrintStream = null;
		closeSilently(stdInInputStream);
		stdInInputStream = null;
		closeSilently(stdInOutputStream);
		stdInOutputStream = null;
		closeSilently(stdInPrintStream);
		stdInPrintStream = null;
		thread = null;
		System.setOut(originalStdOut);
		System.setIn(originalStdIn);
	}

	private static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	private class TappedInputStream extends FilterInputStream {

		public TappedInputStream(InputStream in) {
			super(in);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			markInput();
			return super.read(b, off, len);
		}

	}

}
