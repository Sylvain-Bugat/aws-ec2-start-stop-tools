package com.github.sbugat.ec2tools;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.launcher.MainLauncher;
import com.github.sbugat.ec2tools.service.MainService;

/**
 * 
 * @author Sylvain Bugat
 * 
 */
public final class EC2StartStopMain {

	/** SLF4J XLogger. */
	private static final XLogger LOG = XLoggerFactory.getXLogger(EC2StartStopMain.class);

	/**
	 * Private constructor.
	 */
	private EC2StartStopMain() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Starting method of the of start-stop EC2 tools.
	 * 
	 * @param args program arguments
	 * @throws Exception thrown when the program exit with an error
	 */
	public static void main(final String[] args) throws Exception {

		LOG.entry((Object[]) args);
		LOG.info("Start of EC2StartStop tools");

		try {
			MainLauncher.launcher(MainService.class, args);
			LOG.info("End of EC2StartStop tools");
			LOG.exit();
		}
		catch (final Exception e) {
			LOG.error("Error during EC2StartStop tools execution", e);
			LOG.exit(e);
			throw e;
		}
	}
}