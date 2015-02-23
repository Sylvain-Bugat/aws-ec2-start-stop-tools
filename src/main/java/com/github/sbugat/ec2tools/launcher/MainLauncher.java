package com.github.sbugat.ec2tools.launcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.EC2StartStopMain;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainLauncher {

	/** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(EC2StartStopMain.class);

	private static final String MAIN_METHOD_NAME = "main";

	public static void launcher(@SuppressWarnings("rawtypes") final Class classToLaunch, final String args[]) throws Exception {

		log.entry(classToLaunch, args);

		final Injector injector = Guice.createInjector();
		@SuppressWarnings("unchecked")
		final Object classInstance = injector.getInstance(classToLaunch);
		for (final Method method : classToLaunch.getDeclaredMethods()) {

			if (MAIN_METHOD_NAME.equals(method.getName())) {

				try {
					method.invoke(classInstance, (Object) args);
					return;
				}
				catch (final IllegalAccessException e) {
					log.error("Cannot call method {} in the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					log.exit(e);
					throw e;
				}
				catch (final IllegalArgumentException e) {
					log.error("Cannot call method {} in the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					log.exit(e);
					throw e;
				}
				catch (final InvocationTargetException e) {
					log.error("Cannot call method {} in the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					log.exit(e);
					throw e;
				}
				catch (final Exception e) {
					log.error("Execution error in method {} of the class {}", MAIN_METHOD_NAME, classToLaunch, e);
					log.exit(e);
					throw e;
				}

			}
		}

		final NoSuchMethodException noSuchMethodException = new NoSuchMethodException(MAIN_METHOD_NAME);
		log.exit(noSuchMethodException);
		throw noSuchMethodException;
	}
}
