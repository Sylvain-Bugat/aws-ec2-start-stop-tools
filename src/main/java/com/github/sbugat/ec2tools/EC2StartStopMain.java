package com.github.sbugat.ec2tools;

import gnu.getopt.Getopt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.amazonaws.services.ec2.model.InstanceStateName;
import com.github.sbugat.ec2tools.configuration.Configuration;
import com.github.sbugat.ec2tools.service.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.ServiceModule;
import com.github.sbugat.ec2tools.service.StartStopService;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Main class of start-stop EC2 tools.
 * 
 * @author Sylvain Bugat
 * 
 */
public class EC2StartStopMain {

	/**
	 * SLF4J XLogger
	 */
	private static final XLogger log = XLoggerFactory.getXLogger(EC2StartStopMain.class);

	public static void main(final String args[]) throws ConfigurationException {

		log.entry((Object[]) args);
		log.info("Start of EC2StartStop tools");

		final Injector injector = Guice.createInjector(new ServiceModule());
		final StartStopService startStopService = injector.getInstance(StartStopService.class);
		startStopService.loadConfiguration();

		System.exit(0);

		final String usage = "Usage: " + EC2StartStopMain.class.getSimpleName() + " [-l] [-c|-p -s <section1> -s <section2> ... ]";

		final Getopt getOpt = new Getopt(EC2StartStopMain.class.getSimpleName(), args, ":lcps:");
		getOpt.setOpterr(false);

		boolean list = false;
		boolean check = false;
		boolean postCheck = false;
		final List<String> sectionsToExecute = new ArrayList<String>();

		int c = getOpt.getopt();
		while (-1 != c) {

			switch (c) {

			case 'l':
				list = true;
				break;

			case 'c':
				check = true;
				break;

			case 'p':
				postCheck = true;
				break;

			case 's':
				sectionsToExecute.add(getOpt.getOptarg());
				break;

			case '?':
			default:
				System.err.println(usage);
				System.exit(1);
			}

			c = getOpt.getopt();
		}

		if (sectionsToExecute.isEmpty()) {

			if (!list && !check && !postCheck) {
				System.err.println(usage);
				System.exit(1);
			}
			if (check || postCheck) {
				System.err.println(usage);
				System.exit(1);
			}
		}

		// Configuration loading
		final Configuration configuration;
		try {
			configuration = new Configuration();
			if (list) {
				if (sectionsToExecute.isEmpty()) {
					System.out.println(configuration);
				}
				else {
					System.out.println(configuration.toString(sectionsToExecute));
				}
			}
		}
		catch (final ConfigurationException e) {

			log.error("Error during configuration loading ", e);
			log.exit(1);
			System.exit(1);
			// Just for the final: dead code
			return;
		}

		if (!check && !postCheck && sectionsToExecute.isEmpty()) {
			log.info("End of EC2StartStop tools with just listing configuration");
			log.exit(0);
			System.exit(0);
		}

		// Amazon EC2 service initialization
		final AmazonEC2Service amazonEC2Service;
		try {
			amazonEC2Service = new AmazonEC2Service();
		}
		catch (final Exception e) {

			log.error("Error during Amazon initialization ", e);
			log.exit(1);
			System.exit(1);
			// Just for the final: dead code
			return;
		}

		// Process all arguments sections
		boolean error = false;
		for (final String section : sectionsToExecute) {

			// Get all orders of a section
			final List<InstanceOrder> orderList = configuration.getSectionOrders(section);

			if (null != orderList) {

				// Process each order
				for (final InstanceOrder instanceOrder : orderList) {

					if (check) {
						error |= checkInstance(amazonEC2Service, instanceOrder);
					}
					else if (postCheck) {
						error |= postCheckInstance(amazonEC2Service, instanceOrder);
					}
					else {
						error |= processOrder(amazonEC2Service, instanceOrder);
					}
				}
			}
			// If a section is unknown (and not empty)
			else {
				log.error("Error unknown section {}", section);
				error = true;
			}
		}

		// Return in error if a section is unknown or if an order cannot be done
		if (error) {
			log.error("End of EC2StartStop tools with errors");
			log.exit(1);
			System.exit(1);
		}

		log.info("End of EC2StartStop tools with success");
		log.exit(0);
		System.exit(0);
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
				return false;
			}
		}
		// Stoping instance order
		else if (OrderType.STOP == instanceOrder.orderType) {

			try {
				amazonEC2Service.stopInstance(instanceOrder.instanceId);
			}
			// Stoping error
			catch (final Exception e) {
				log.error("Error stoping instance {}", instanceOrder.instanceId, e);
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
	private static boolean checkInstance(final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder) {

		log.entry(amazonEC2Service, instanceOrder);
		// Starting instance order
		if (OrderType.START == instanceOrder.orderType) {

			try {
				final String status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Stopped.toString().equals(status)) {
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
				final String status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Running.toString().equals(status)) {
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
	private static boolean postCheckInstance(final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder) {

		log.entry(amazonEC2Service, instanceOrder);
		// Starting instance order
		if (OrderType.START == instanceOrder.orderType) {

			try {
				final String status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Running.toString().equals(status)) {
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
				final String status = amazonEC2Service.getInstanceStatus(instanceOrder.instanceId);
				if (InstanceStateName.Stopped.toString().equals(status)) {
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
