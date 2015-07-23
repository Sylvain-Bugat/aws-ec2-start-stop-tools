package com.github.sbugat.ec2tools.launcher;

import java.lang.reflect.Method;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.EC2StartStopMain;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Main launcher using Guice dependencies injection class.
 * 
 * @author Sylvain Bugat
 * 
 */
public final class MainLauncher {

	/** SLF4J XLogger. */
	private static final XLogger LOG = XLoggerFactory.getXLogger(EC2StartStopMain.class);

	/** Method name to launch on the target class. */
	private static final String MAIN_METHOD_NAME = "main";

	/**
	 * Private constructor.
	 */
	private MainLauncher() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Launcher method which inject dependencies using Guice and call the main method of the target class.
	 * 
	 * @param classToLaunch class to instanciate and call the method
	 * @param args program arguments
	 * @throws Exception exception thrown to indicate an error in the dependencies injection or in the called main method
	 */
	public static void launcher(@SuppressWarnings("rawtypes") final Class classToLaunch, final String[] args) throws Exception {

		LOG.entry(classToLaunch, args);

		// Create the Guice dependencies injector and instantiate the target method
		final Injector injector = Guice.createInjector();
		@SuppressWarnings("unchecked")
		final Object classInstance = injector.getInstance(classToLaunch);
		for (final Method method : classToLaunch.getDeclaredMethods()) {

			if (MAIN_METHOD_NAME.equals(method.getName())) {

				try {
					// Call the main method
					method.invoke(classInstance, (Object) args);
					return;
				}
				// If the target main method cannot be called because of an invalid visibility scope
				catch (final IllegalAccessException e) {
					LOG.error("Cannot call method {} in the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					LOG.exit(e);
					throw e;
				}
				// If the called method don't accept String[] args argument
				catch (final IllegalArgumentException e) {
					LOG.error("Cannot call method {} in the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					LOG.exit(e);
					throw e;
				}
				// If an exception occurs in the called method
				catch (final Exception e) {
					LOG.error("Execution error in method {} of the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					LOG.exit(e);
					throw e;
				}
			}
		}

		// If no method main has been found in the target class
		final NoSuchMethodException noSuchMethodException = new NoSuchMethodException(MAIN_METHOD_NAME);
		LOG.exit(noSuchMethodException);
		throw noSuchMethodException;
	}
}
