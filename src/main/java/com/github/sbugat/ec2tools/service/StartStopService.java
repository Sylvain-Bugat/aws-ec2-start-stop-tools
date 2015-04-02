package com.github.sbugat.ec2tools.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.amazonaws.services.ec2.model.InstanceStateName;
import com.github.sbugat.ec2tools.model.instance.InstanceOrder;
import com.github.sbugat.ec2tools.model.instance.OrderType;
import com.github.sbugat.ec2tools.service.aws.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.configuration.ConfigurationService;
import com.github.sbugat.ec2tools.service.options.ProgramOptions;

public class StartStopService {

	/** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(StartStopService.class);

	@Inject
	private AmazonEC2Service amazonEC2Service;

	@Inject
	private ConfigurationService configurationService;

	public boolean processAllSections(final ProgramOptions programOptions) {

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
					error |= checkInstance(instanceOrder);
				}
				else if (programOptions.hasPostCheckOption()) {
					error |= postCheckInstance(instanceOrder);
				}
				else if (programOptions.hasExecutionOption()) {
					error |= processOrder(instanceOrder);
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
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private boolean processOrder(final InstanceOrder instanceOrder) {

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
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private boolean checkInstance(final InstanceOrder instanceOrder) {

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
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private boolean postCheckInstance(final InstanceOrder instanceOrder) {

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
