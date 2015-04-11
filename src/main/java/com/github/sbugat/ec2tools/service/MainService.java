package com.github.sbugat.ec2tools.service;

import javax.inject.Inject;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.service.aws.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.configuration.ConfigurationService;
import com.github.sbugat.ec2tools.service.options.ProgramOptions;
import com.github.sbugat.ec2tools.service.options.ProgramOptionsService;

/**
 * Main service which include the called main method.
 * 
 * @author Sylvain Bugat
 * 
 */
public class MainService {

	/** SLF4J XLogger. */
	private static final XLogger LOG = XLoggerFactory.getXLogger(MainService.class);

	/** Amazon EC2 service. */
	@Inject
	private AmazonEC2Service amazonEC2Service;

	/** Configuration service. */
	@Inject
	private ConfigurationService configurationService;

	/** Options of the program service. */
	@Inject
	private ProgramOptionsService programOptionsService;

	/** Process orders to start/stop instances service. */
	@Inject
	private StartStopService startStopService;

	/**
	 * Main method to parse arguments and process orders.
	 * 
	 * @param programArgs program arguments
	 * @throws Exception if the program failed
	 */
	public void main(final String[] programArgs) throws Exception {

		LOG.entry((Object[]) programArgs);

		// Arguments checking
		final ProgramOptions programOptions;
		try {
			programOptions = programOptionsService.processProgramArgs(programArgs);
		}
		catch (final Exception e) {

			LOG.error("Error during arguments checking", e);
			LOG.exit(e);
			throw e;
		}

		// Configuration loading
		try {
			configurationService.loadConfiguration();
		}
		catch (final ConfigurationException e) {

			LOG.error("Error during configuration loading", e);
			LOG.exit(e);
			throw e;
		}

		// Configuration printing option
		if (programOptions.hasListOption()) {
			if (programOptions.hasNoSectionOption()) {
				System.out.println(configurationService);
			}
			else {
				System.out.println(configurationService.toString(programOptions.getSectionOptions()));
			}
		}

		// Section option to process
		if (!programOptions.hasNoSectionOption() && !programOptions.hasListOption()) {

			// Amazon EC2 service initialization
			try {
				amazonEC2Service.initialize();
			}
			catch (final Exception e) {

				LOG.error("Error during Amazon initialization ", e);
				LOG.exit(e);
				throw e;
			}

			final boolean processError = startStopService.processAllSections(programOptions);

			// Return in error if a section is unknown or if an order cannot be done
			if (processError) {

				LOG.error("Error during sections processing");

				final RuntimeException exception = new RuntimeException();
				LOG.exit(exception);
				throw exception;
			}
		}

		LOG.exit();
	}
}
