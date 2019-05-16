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
	
	private static final int HANG_DETECTION_SECONDS = 3;
	
	/** Cannot be instantiated. */
	private Runner() {}

	private static PrintStream originalStdOut = System.out;
	private static InputStream originalStdIn = System.in;
	
	// Before any other code has a chance to store System.in in a variable
	// such as a static Scanner, switch to a proxy that be able to tap into.
	private static ProxyInputStream stdInProxy = new ProxyInputStream(originalStdIn);
	static {
		System.setIn(stdInProxy);
	}

	private static BlockingQueue<IOBlock> queue = new LinkedBlockingQueue<>();

	private static ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private static PrintStream stdOutPrintStream;
	private static PipedInputStream stdInInputStream;
	private static PipedOutputStream stdInOutputStream;
	private static PrintStream stdInPrintStream;
	private static Thread thread;
	private static boolean suppressStackTrace;
	
	/**
	 * A dummy method just to make sure that the above static initialization runs.
	 * Java will run that code as soon as this class is used. Calling this will
	 * "use" the class, which needs to be triggered as early as possible in the test
	 * run.
	 */
	public static void init() {}
	
	public static synchronized void start(Runnable runnable) {
		if (thread != null) {
			throw new RuntimeException("IOTester Runner was already started. It must be terminated before starting again.");
		}
		outStream.reset();
		try {
			stdInInputStream = new PipedInputStream();
			stdInOutputStream = new PipedOutputStream(stdInInputStream);
			stdInPrintStream = new PrintStream(stdInOutputStream);

			stdOutPrintStream = new PrintStream(new TeeOutputStream(outStream, originalStdOut), true, "UTF-8");
			System.setOut(stdOutPrintStream);
			stdInProxy.setInputStream(new TappedInputStream(stdInInputStream));
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

	public static IOBlock nextBlock() {
		try {
			IOBlock block = queue.poll(HANG_DETECTION_SECONDS, TimeUnit.SECONDS);
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
	
	public static void in(Object line) {
		originalStdOut.println("«" + line + "»");
		stdInPrintStream.println(line);
		stdInPrintStream.flush();
	}
	
	public static synchronized void terminate(boolean suppressStackTrace) {
		if (thread == null) {
			return;
		}
		Runner.suppressStackTrace = suppressStackTrace;
		if (thread.isAlive()) {
			thread.interrupt();
		}
		try {
			thread.join(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			cleanUp();
			Runner.suppressStackTrace = false;
		}
	}

	private static String flushOutput() {
		stdOutPrintStream.flush();
		String output = new String(outStream.toByteArray(),
				StandardCharsets.UTF_8);
		outStream.reset();
		return output;
	}

	private static void markInput() {
		queue.offer(new IOBlock(flushOutput(), BlockEndType.INPUT));
	}

	private static void cleanUp() {
		queue.clear();
		stdOutPrintStream = null;
		closeSilently(stdInInputStream);
		stdInInputStream = null;
		closeSilently(stdInOutputStream);
		stdInOutputStream = null;
		closeSilently(stdInPrintStream);
		stdInPrintStream = null;
		thread = null;
		System.setOut(originalStdOut);
		stdInProxy.setInputStream(originalStdIn);
	}

	private static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	private static class TappedInputStream extends FilterInputStream {

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
