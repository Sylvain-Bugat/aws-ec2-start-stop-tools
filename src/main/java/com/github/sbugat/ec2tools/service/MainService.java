package com.github.sbugat.ec2tools.service;

import javax.inject.Inject;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.service.aws.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.configuration.ConfigurationService;
import com.github.sbugat.ec2tools.service.options.ProgramOptions;
import com.github.sbugat.ec2tools.service.options.ProgramOptionsService;

public class MainService {

	/** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(MainService.class);

	@Inject
	private AmazonEC2Service amazonEC2Service;

	@Inject
	private ConfigurationService configurationService;

	@Inject
	private ProgramOptionsService programOptionsService;

	@Inject
	private StartStopService startStopService;

	public void main(final String programArgs[]) throws Exception {

		log.entry((Object[]) programArgs);

		// Arguments checking
		final ProgramOptions programOptions;
		try {
			programOptions = programOptionsService.processProgramArgs(programArgs);
		}
		catch (final Exception e) {

			log.error("Error during arguments checking", e);
			log.exit(e);
			throw e;
		}

		// Configuration loading
		try {
			configurationService.loadConfiguration();
		}
		catch (final ConfigurationException e) {

			log.error("Error during configuration loading", e);
			log.exit(e);
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

				log.error("Error during Amazon initialization ", e);
				log.exit(e);
				throw e;
			}

			final boolean processError = startStopService.processAllSections(programOptions);

			// Return in error if a section is unknown or if an order cannot be done
			if (processError) {

				log.error("Error during sections processing");

				final RuntimeException exception = new RuntimeException();
				log.exit(exception);
				throw exception;
			}
		}

		log.exit();
	}
}
