package com.github.sbugat.ec2tools.service;

import gnu.getopt.Getopt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.amazonaws.services.ec2.model.InstanceStateName;
import com.github.sbugat.ec2tools.EC2StartStopMain;
import com.github.sbugat.ec2tools.InstanceOrder;
import com.github.sbugat.ec2tools.OrderType;
import com.github.sbugat.ec2tools.service.configuration.ConfigurationService;

public class MainService {

	/** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(MainService.class);

	private static final String USAGE = "Usage: [-l] [-e|-c|-p -s <section1> -s <section2> ... ]";

	@Inject
	private AmazonEC2Service amazonEC2Service;

	@Inject
	private ConfigurationService configurationService;

	public ProgramOptions processProgramArgs(final String programArgs[]) {

		// Arguments checking
		final Getopt getOpt = new Getopt(EC2StartStopMain.class.getSimpleName(), programArgs, ":lecps:");
		getOpt.setOpterr(false);

		boolean list = false;
		boolean check = false;
		boolean execute = false;
		boolean postCheck = false;
		int exclusiveOptionsCount = 0;
		final List<String> sectionsToExecute = new ArrayList<String>();

		int option = getOpt.getopt();
		while (-1 != option) {

			switch (option) {

			case 'l':
				list = true;
				exclusiveOptionsCount++;
				break;

			case 'e':
				execute = true;
				exclusiveOptionsCount++;
				break;

			case 'c':
				check = true;
				exclusiveOptionsCount++;
				break;

			case 'p':
				postCheck = true;
				exclusiveOptionsCount++;
				break;

			case 's':
				sectionsToExecute.add(getOpt.getOptarg());
				break;

			case '?':
			default:
				System.err.println(USAGE);
				final IllegalArgumentException exception = new IllegalArgumentException("Unknow option:" + option);
				log.exit(exception);
				throw exception;
			}

			option = getOpt.getopt();
		}

		// Missing a mandatory option
		if (0 == exclusiveOptionsCount) {
			System.err.println(USAGE);
			final IllegalArgumentException exception = new IllegalArgumentException("One of list, execute, check and post-check options is mandatory ");
			log.exit(exception);
			throw exception;
		}
		// Multiple exclusive options
		else if (exclusiveOptionsCount > 1) {
			System.err.println(USAGE);
			final IllegalArgumentException exception = new IllegalArgumentException("List, execute, check and post-check options are exclusive");
			log.exit(exception);
			throw exception;
		}
		// Missing section option
		else if (sectionsToExecute.isEmpty() && !list) {

			System.err.println(USAGE);
			final IllegalArgumentException exception = new IllegalArgumentException("Execute, check and post-check options need one or more section option");
			log.exit(exception);
			throw exception;
		}

		// All check are OK
		final ProgramOptions programOptions = new ProgramOptions(execute, list, check, postCheck, sectionsToExecute);
		log.exit(programOptions);
		return programOptions;
	}

	public void main(final String programArgs[]) throws Exception {

		if (0 == programArgs.length) {
			System.err.println(USAGE);
			log.exit();
			return;
		}

		// Arguments checking
		final ProgramOptions programOptions = processProgramArgs(programArgs);

		// Configuration loading
		try {
			configurationService.loadConfiguration();
		}
		catch (final ConfigurationException e) {

			log.error("Error during configuration loading ", e);
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
		if (!programOptions.hasNoSectionOption()) {

			// Amazon EC2 service initialization
			try {
				amazonEC2Service.initialize();
			}
			catch (final Exception e) {

				log.error("Error during Amazon initialization ", e);
				log.exit(e);
				throw e;
			}

			final boolean processError = processAllSections(programOptions);

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

	private boolean processAllSections(final ProgramOptions programOptions) {

		log.entry(programOptions);

		// Process all arguments sections
		boolean error = false;
		for (final String section : programOptions.getSectionOptions()) {

			error |= processSection(programOptions, section);
		}

		log.exit(error);
		return error;
	}

	private boolean processSection(final ProgramOptions programOptions, final String section) {

		log.entry(programOptions, section);
		// Get all orders of a section
		final List<InstanceOrder> orderList = configurationService.getConfiguredSections(section);

		if (null != orderList) {

			// Process each order
			boolean error = false;
			for (final InstanceOrder instanceOrder : orderList) {

				if (programOptions.hasCheckOption()) {
					error |= checkInstance(amazonEC2Service, instanceOrder);
				}
				else if (programOptions.hasPostCheckOption()) {
					error |= postCheckInstance(amazonEC2Service, instanceOrder);
				}
				else if (programOptions.hasExecutionOption()) {
					error |= processOrder(amazonEC2Service, instanceOrder);
				}
			}

			log.exit(error);
			return error;
		}
		// If a section is unknown (and not empty)
		else {
			log.error("Error unknown section {}", section);
			log.exit(true);
			return true;
		}
	}

	/**
	 * Process an order to start or stop an Amazon EC2 instance
	 * 
	 * @param amazonEC2Service service to use
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private static boolean processOrder(final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder) {

		log.entry(amazonEC2Service, instanceOrder);
		// Starting instance order
		if (OrderType.START == instanceOrder.orderType) {

			try {
				amazonEC2Service.startInstance(instanceOrder.instanceId);
			}
			// Starting error
			catch (final Exception e) {
				log.error("Error starting instance {}", instanceOrder.instanceId, e);
				log.exit(false);
				return false;
			}
		}
		// Stoping instance order
		else if (OrderType.STOP == instanceOrder.orderType) {

			try {
				amazonEC2Service.stopInstance(instanceOrder.instanceId);
			}
			// Stopping error
			catch (final Exception e) {
				log.error("Error stopping instance {}", instanceOrder.instanceId, e);
				log.exit(false);
				return false;
			}
		}

		log.exit(true);
		return true;
	}

	/**
	 * Process an order to start or stop an Amazon EC2 instance
	 * 
	 * @param amazonEC2Service service to use
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private boolean checkInstance(final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder) {

		log.entry(amazonEC2Service, instanceOrder);
		// Starting instance order
		if (OrderType.START == instanceOrder.orderType) {

			try {
				final InstanceStateName status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Stopped == status) {
					log.info("Instance {} is stopped and can be started", instanceOrder.instanceId);
				}
				else {
					log.error("Instance {} is not stopped and cannot be started", instanceOrder.instanceId, status);
					log.exit(true);
					return true;
				}
			}
			// Starting error
			catch (final Exception e) {
				log.error("Error checking status of instance {}", instanceOrder.instanceId, e);
				log.exit(true);
				return true;
			}
		}
		// Stoping instance order
		else if (OrderType.STOP == instanceOrder.orderType) {

			try {
				final InstanceStateName status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Running == status) {
					log.info("Instance {} is running and can be stopped", instanceOrder.instanceId);
				}
				else {
					log.error("Instance {} is not running and cannot be stopped", instanceOrder.instanceId, status);
					log.exit(true);
					return true;
				}
			}
			// Stoping error
			catch (final Exception e) {
				log.error("Error stoping instance {}", instanceOrder.instanceId, e);
				log.exit(true);
				return true;
			}
		}

		log.exit(false);
		return false;
	}

	/**
	 * Process an order to start or stop an Amazon EC2 instance
	 * 
	 * @param amazonEC2Service service to use
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private boolean postCheckInstance(final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder) {

		log.entry(amazonEC2Service, instanceOrder);
		// Starting instance order
		if (OrderType.START == instanceOrder.orderType) {

			try {
				final InstanceStateName status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Running == status) {
					log.info("Instance {} is running", instanceOrder.instanceId);
				}
				else {
					log.error("Instance {} is not running", instanceOrder.instanceId, status);
					log.exit(true);
					return true;
				}
			}
			// Starting error
			catch (final Exception e) {
				log.error("Error checking status of instance {}", instanceOrder.instanceId, e);
				log.exit(true);
				return true;
			}
		}
		// Stoping instance order
		else if (OrderType.STOP == instanceOrder.orderType) {

			try {
				final InstanceStateName status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Stopped == status) {
					log.info("Instance {} is stopped", instanceOrder.instanceId);
				}
				else {
					log.error("Instance {} is not stopped", instanceOrder.instanceId, status);
					log.exit(true);
					return true;
				}
			}
			// Stoping error
			catch (final Exception e) {
				log.error("Error stoping instance {}", instanceOrder.instanceId, e);
				log.exit(true);
				return true;
			}
		}

		log.exit(false);
		return false;
	}
}