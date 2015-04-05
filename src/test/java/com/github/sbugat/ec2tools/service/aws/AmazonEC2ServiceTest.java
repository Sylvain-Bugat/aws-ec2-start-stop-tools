package com.github.sbugat.ec2tools.service.aws;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.github.sbugat.GenericMockitoTest;

/**
 * Amazon EC2 simple service test.
 * 
 * @author Sylvain Bugat
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonEC2ServiceTest extends GenericMockitoTest {

	private static final String INSTANCE_ID = "instance id";

	@Mock
	private AmazonEC2Client amazonEC2Client;

	@InjectMocks
	private AmazonEC2Service amazonEC2Service;

	@Test
	public void testInitialize() {

		amazonEC2Service.initialize();

		final InOrder inOrder = Mockito.inOrder(amazonEC2Client);
		inOrder.verify(amazonEC2Client).setRegion(Region.getRegion(Regions.EU_WEST_1));
		inOrder.verify(amazonEC2Client).describeAvailabilityZones();
	}

	@Test
	public void testStartInstance() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Stopped)));

		final StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(INSTANCE_ID);
		final StartInstancesResult startInstancesResult = new StartInstancesResult().withStartingInstances(new InstanceStateChange().withCurrentState(new InstanceState().withName(InstanceStateName.Running)));

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		Mockito.doReturn(startInstancesResult).when(amazonEC2Client).startInstances(startInstancesRequest);

		amazonEC2Service.startInstance(INSTANCE_ID);

		final InOrder inOrder = Mockito.inOrder(amazonEC2Client);
		inOrder.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		inOrder.verify(amazonEC2Client).startInstances(startInstancesRequest);
	}

	@Test(expected = AmazonClientException.class)
	public void testStartInstanceAlreadyRunning() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Running)));

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);

		try {
			amazonEC2Service.startInstance(INSTANCE_ID);
		}
		finally {
			Mockito.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		}
	}

	@Test
	public void testStopInstanceStopping() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Running)));

		final StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(INSTANCE_ID);
		final StopInstancesResult stopInstancesResult = new StopInstancesResult().withStoppingInstances(new InstanceStateChange().withCurrentState(new InstanceState().withName(InstanceStateName.Stopping)));

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		Mockito.doReturn(stopInstancesResult).when(amazonEC2Client).stopInstances(stopInstancesRequest);

		amazonEC2Service.stopInstance(INSTANCE_ID);

		final InOrder inOrder = Mockito.inOrder(amazonEC2Client);
		inOrder.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		inOrder.verify(amazonEC2Client).stopInstances(stopInstancesRequest);
	}

	@Test
	public void testStopInstanceStopped() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Running)));

		final StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(INSTANCE_ID);
		final StopInstancesResult stopInstancesResult = new StopInstancesResult().withStoppingInstances(new InstanceStateChange().withCurrentState(new InstanceState().withName(InstanceStateName.Stopped)));

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		Mockito.doReturn(stopInstancesResult).when(amazonEC2Client).stopInstances(stopInstancesRequest);

		amazonEC2Service.stopInstance(INSTANCE_ID);

		final InOrder inOrder = Mockito.inOrder(amazonEC2Client);
		inOrder.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		inOrder.verify(amazonEC2Client).stopInstances(stopInstancesRequest);
	}

	@Test(expected = AmazonClientException.class)
	public void testStopInstanceAlreadyStopped() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Stopped)));
		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);

		try {
			amazonEC2Service.stopInstance(INSTANCE_ID);
		}
		finally {
			Mockito.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		}
	}

	@Test
	public void testGetInstanceStatus() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Running)));

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);

		Assertions.assertThat(amazonEC2Service.getInstanceStatus(INSTANCE_ID)).isEqualTo(InstanceStateName.Running);

		Mockito.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
	}

	@Test(expected = AmazonClientException.class)
	public void testGetInstanceStatusNull() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		try {
			amazonEC2Service.getInstanceStatus(INSTANCE_ID);
		}
		finally {
			Mockito.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		}
	}

	@Test(expected = AmazonClientException.class)
	public void testGetInstanceStatusEmpty() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult();

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);

		try {
			amazonEC2Service.getInstanceStatus(INSTANCE_ID);
		}
		finally {
			Mockito.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		}
	}

	@Test(expected = AmazonClientException.class)
	public void testGetInstanceStatusMultipleInstances() {

		final DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true).withInstanceIds(INSTANCE_ID);
		final DescribeInstanceStatusResult describeInstanceStatusResult = new DescribeInstanceStatusResult().withInstanceStatuses(new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Running)), new InstanceStatus().withInstanceState(new InstanceState().withName(InstanceStateName.Running)));

		Mockito.doReturn(describeInstanceStatusResult).when(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);

		try {
			amazonEC2Service.getInstanceStatus(INSTANCE_ID);
		}
		finally {
			Mockito.verify(amazonEC2Client).describeInstanceStatus(describeInstanceStatusRequest);
		}
	}
}
