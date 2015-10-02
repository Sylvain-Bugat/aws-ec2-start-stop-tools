package com.github.sbugat.ec2tools.launcher;

public class MockLauncher {

	private static Class<? extends Exception> nextExceptionClass;

	public final void main(final String args[]) throws Exception {

		if (null != nextExceptionClass) {

			final Exception exception = nextExceptionClass.newInstance();
			nextExceptionClass = null;
			throw exception;
		}
	}

	public static void setNextExceptionClass(final Class<? extends Exception> exceptionClass) {
		nextExceptionClass = exceptionClass;
	}
}
