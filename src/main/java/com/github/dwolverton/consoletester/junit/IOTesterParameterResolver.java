package com.github.dwolverton.consoletester.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.github.dwolverton.consoletester.IOTester;

public class IOTesterParameterResolver implements ParameterResolver, AfterEachCallback {
		
		private static final String IO_TESTER = "IO_TESTER";

		@Override
		public Object resolveParameter(ParameterContext pc, ExtensionContext ec)
				throws ParameterResolutionException {
			IOTester io = new IOTester();
			getStore(ec).put(IO_TESTER, io);
			return io;
		}

		@Override
		public boolean supportsParameter(ParameterContext pc, ExtensionContext ec)
				throws ParameterResolutionException {
			return pc.getParameter().getType().equals(IOTester.class);
		}

		@Override
		public void afterEach(ExtensionContext ec) throws Exception {
			IOTester io = getStore(ec).remove(IO_TESTER, IOTester.class);
			if (io != null) {
				// This will clean up any loose ends.
				io.skipToEnd();
			}
		}
		
		private Store getStore(ExtensionContext context) {
	        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
	    }

	}