package com.github.sbugat.ec2tools.service.aws;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
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
}
