package com.github.sbugat.ec2tools.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

/**
 * Amazon EC2 simple service, init credentials with default file $HOME/.aws/credentials
 * 
 * @author Sylvain Bugat
 */
public class AmazonEC2Service {

	/*** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(AmazonEC2Service.class);

	/** Amazon EC2 service access. */
	@Inject
	private AmazonEC2Client amazonEC2Client;

	/**
	 * Amazon EC2 service initialization.
	 * 
	 * @throws AmazonClientException
	 */
	public void initialize() throws AmazonClientException {

		log.entry();
		log.info("Initialize ECS AWS client");

		// AWS client initialization
		// TODO region customization
		amazonEC2Client.setRegion(Region.getRegion(Regions.EU_WEST_1));

		// Simple connection test
		log.info("AWS configuration loaded, now doing connection test...");
		amazonEC2Client.describeAvailabilityZones();
		log.info("Connection with AWS OK");
		log.exit();
	}

	/**
	 * Check the instance status and order to start it if it's stopped
	 * 
	 * @param instanceId Instance du start
	 */
	public void startInstance(final String instanceId) {

		log.entry();
		log.info("Starting instance {}", instanceId);

		// Check the instance state
		final InstanceStateName instanteState = getInstanceStatus(instanceId);
		if (InstanceStateName.Stopped != instanteState) {

			final String message = "Instance " + instanceId + " is not stopped, current state: " + instanteState;
			log.error(message);
			final AmazonClientException exception = new AmazonClientException(message);
			log.exit(exception);
			throw exception;
		}

		// Start instance order
		final StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instanceId);
		final StartInstancesResult startInstancesResult = amazonEC2Client.startInstances(startInstancesRequest);
		final List<InstanceStateChange> instanceStateChangeList = startInstancesResult.getStartingInstances();

		for (final InstanceStateChange instanceStateChange : instanceStateChangeList) {

			log.info("Instance {} has changing state: {} -> {}", instanceStateChange.getInstanceId(), instanceStateChange.getPreviousState(), instanceStateChange.getCurrentState());
		}

		log.info("Instance {} is starting", instanceId);
		log.exit();
	}

	/**
	 * Check the instance status and order to stop it if it's running
	 * 
	 * @param instanceId instance to stop
	 */
	public void stopInstance(final String instanceId) {

		log.entry();
		log.info("Stoping instance {}", instanceId);

		// Check the instance state
		final InstanceStateName instanteState = getInstanceStatus(instanceId);
		if (InstanceStateName.Running != instanteState) {

			final String message = "Instance " + instanceId + " is not running, current status: " + instanteState;
			log.error(message);
			log.exit(message);
			throw new AmazonClientException(message);
		}

		// Stop instance order
		final StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
		final StopInstancesResult stopInstancesResult = amazonEC2Client.stopInstances(stopInstancesRequest);
		final List<InstanceStateChange> instanceStateChangeList = stopInstancesResult.getStoppingInstances();

		for (final InstanceStateChange instanceStateChange : instanceStateChangeList) {

			log.info("Instance {} has changing state: {} -> {}", instanceStateChange.getInstanceId(), instanceStateChange.getPreviousState(), instanceStateChange.getCurrentState());
		}

		log.info("Instance {} is stoping", instanceId);
		log.exit();
	}

	/**
	 * Method to return a current instance status
	 * 
	 * @param instanceId id of the instance
	 * @return instance status
	 */
	public InstanceStateName getInstanceStatus(final String instanceId) {

		log.entry();
		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(instanceId);
		final DescribeInstanceStatusResult describeInstanceStatusResult = amazonEC2Client.describeInstanceStatus(describeInstanceStatusRequest);
		final List<InstanceStatus> instanceStatusList = describeInstanceStatusResult.getInstanceStatuses();

		// If none or more than one instances is found
		if (instanceStatusList.isEmpty()) {

			final AmazonClientException exception = new AmazonClientException("No instance found with id:" + instanceId);
			log.exit(exception);
			throw exception;
		}
		else if (instanceStatusList.size() > 1) {

			final AmazonClientException exception = new AmazonClientException("Multiple instances found with id:" + instanceId);
			log.exit(exception);
			throw exception;
		}

		// Return the current state
		final InstanceStateName instanceState = InstanceStateName.fromValue(instanceStatusList.get(0).getInstanceState().getName());
		log.info("Instance {} current state is {}", instanceId, instanceState);
		log.exit(instanceState);
		return instanceState;
	}

	@Override
	public String toString() {
		return AmazonEC2Service.class.getSimpleName() + ':' + amazonEC2Client.toString();
	}
}
