package com.github.sbugat.ec2tools;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.amazonaws.services.ec2.model.InstanceStateName;
import com.github.sbugat.ec2tools.launcher.MainLauncher;
import com.github.sbugat.ec2tools.service.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.MainService;

/**
 * Main class of start-stop EC2 tools.
 * 
 * @author Sylvain Bugat
 * 
 */
public class EC2StartStopMain {

	/** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(EC2StartStopMain.class);

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
	private static boolean postCheckInstance(final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder) {

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
