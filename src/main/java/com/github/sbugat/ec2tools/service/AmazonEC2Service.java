package com.github.sbugat.ec2tools.service;

import java.util.List;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceStateChange;
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

	/**
	 * SLF4J XLogger
	 */
	private static final XLogger log = XLoggerFactory.getXLogger( AmazonEC2Service.class );

	private static final String EC2_INSTANCE_STATE_STOPPED = "stopped";

	private static final String EC2_INSTANCE_STATE_RUNNING = "running";

	/**
	 * Amazon EC2 service access
	 */
	private final AmazonEC2 ec2;

	/**
	 * Amazon EC2 service initialization
	 *
	 * @throws AmazonClientException
	 */
	public AmazonEC2Service() throws AmazonClientException{

		log.entry();
		log.info( "Loading AWS configuration" );

		//Get AWS credentials in the .aws/credentials directory by default
		final AWSCredentials aWScredentials;
		try {
			aWScredentials = new ProfileCredentialsProvider().getCredentials();
		}
		catch ( final Exception e) {

			final AmazonClientException exception = new AmazonClientException( "Error loading AWS credentials check file exitence and access $HOME/.aws/credentials on Linux", e );
			log.exit( exception );
			throw exception;
		}

		//Client configuration
		final ClientConfiguration clientConfiguration = new ClientConfiguration();

		//AWS client initialization
		ec2 = new AmazonEC2Client( aWScredentials, clientConfiguration );
		ec2.setRegion( Region.getRegion( Regions.EU_WEST_1 ) );

		//Simple connection test
		log.info( "AWS configurtion load, now doing connection test..." );
		ec2.describeAvailabilityZones();
		log.info( "Connection with AWS OK" );
	}

	public void startInstance( final String instanceId ) {

		log.entry();
		log.info( "Starting instance {}", instanceId );

		final String instanteState = getInstanceStatus( instanceId );
		if( ! EC2_INSTANCE_STATE_STOPPED.equals( instanteState ) ) {

			final String message = "Instance " + instanceId + " is not stopped, current state: " + instanteState;
			log.error( message );
			final AmazonClientException exception = new AmazonClientException( message );
			log.exit( exception );
			throw exception;
		}

		final StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds( instanceId );
		final StartInstancesResult startInstancesResult = ec2.startInstances( startInstancesRequest );
		final List<InstanceStateChange> instanceStateChangeList = startInstancesResult.getStartingInstances();

		for( final InstanceStateChange instanceStateChange : instanceStateChangeList ) {

			log.info( "Instance {} has changing state: {} -> {}", instanceStateChange.getInstanceId(), instanceStateChange.getPreviousState(), instanceStateChange.getCurrentState() );
		}

		log.info( "Instance {} is starting", instanceId );
		log.exit();
	}

	public void stopInstance( final String instanceId ) {

		log.entry();
		log.info( "Stoping instance {}", instanceId );

		final String instanteState = getInstanceStatus( instanceId );
		if( ! EC2_INSTANCE_STATE_RUNNING.equals( instanteState ) ) {

			final String message = "Instance " + instanceId + " is not running, current status: " + instanteState;
			log.error( message );
			log.exit( message );
			throw new AmazonClientException( message );
		}

		final StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds( instanceId );
		final StopInstancesResult stopInstancesResult = ec2.stopInstances( stopInstancesRequest );
		final List<InstanceStateChange> instanceStateChangeList = stopInstancesResult.getStoppingInstances();

		for( final InstanceStateChange instanceStateChange : instanceStateChangeList ) {

			log.info( "Instance {} has changing state: {} -> {}", instanceStateChange.getInstanceId(), instanceStateChange.getPreviousState(), instanceStateChange.getCurrentState() );
		}

		log.info( "Instance {} is stoping", instanceId );
		log.exit();
	}

	/**
	 * Method to return a current instance status
	 *
	 * @param instanceId id of the instance
	 * @return instance status
	 */
	private String getInstanceStatus( final String instanceId ) {

		log.entry();
		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds( instanceId );
		final DescribeInstanceStatusResult describeInstanceStatusResult = ec2.describeInstanceStatus( describeInstanceStatusRequest );
		final List<InstanceStatus> instanceStatusList = describeInstanceStatusResult.getInstanceStatuses();

		//If none or more than one instances is found
		if( instanceStatusList.isEmpty() ) {

			final AmazonClientException exception = new AmazonClientException( "No instance found with id:" + instanceId );
			log.exit( exception );
			throw exception;
		}
		else if( instanceStatusList.size() > 1 ) {

			final AmazonClientException exception = new AmazonClientException( "Multiple instances found with id:" + instanceId );
			log.exit( exception );
			throw exception;
		}

		//Return the current state
		final String instanceState = instanceStatusList.get( 0 ).getInstanceState().getName();
		log.info( "Instance {} current state is {}", instanceId, instanceState );
		log.exit( instanceState );
		return instanceState;
	}

	public String toString() {
		return AmazonEC2Service.class.getSimpleName() + ':' + ec2.toString();
	}
}
