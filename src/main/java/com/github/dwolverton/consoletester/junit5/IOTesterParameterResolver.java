package com.github.dwolverton.consoletester.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.github.dwolverton.consoletester.IOTester;
import com.github.dwolverton.consoletester.runner.Runner;

public class IOTesterParameterResolver implements ParameterResolver, AfterEachCallback {
		
		@Override
		public Object resolveParameter(ParameterContext pc, ExtensionContext ec)
				throws ParameterResolutionException {
			IOTester io = new IOTester();
			return io;
		}

		@Override
		public boolean supportsParameter(ParameterContext pc, ExtensionContext ec)
				throws ParameterResolutionException {
			return pc.getParameter().getType().equals(IOTester.class);
		}

		@Override
		public void afterEach(ExtensionContext ec) throws Exception {
			Runner.terminate(true);
		}

	}