package com.github.sbugat.ec2tools;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.launcher.MainLauncher;
import com.github.sbugat.ec2tools.service.MainService;

/**
 * Main class of start-stop EC2 tools.
 * 
 * @author Sylvain Bugat
 * 
 */
public final class EC2StartStopMain {

	/** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(EC2StartStopMain.class);

	private EC2StartStopMain() {
		throw new UnsupportedOperationException();
	}

	public static void main(final String args[]) throws Exception {

		log.entry((Object[]) args);
		log.info("Start of EC2StartStop tools");

		try {
			MainLauncher.launcher(MainService.class, args);
			log.info("End of EC2StartStop tools");
			log.exit();
		}
		catch (final Exception e) {
			log.error("Error during EC2StartStop tools execution", e);
			log.exit(e);
			throw e;
		}
	}
}
