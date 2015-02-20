package com.github.sbugat.ec2tools.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.services.ec2.AmazonEC2;
import com.github.sbugat.GenericMockitoTest;

/**
 * Amazon EC2 simple service test.
 * 
 * @author Sylvain Bugat
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonEC2ServiceTest extends GenericMockitoTest {

	@Mock
	private AmazonEC2 amazonEC2;

	@InjectMocks
	private AmazonEC2Service amazonEC2Service;

	@Test
	public void test() {

		// amazonEC2Service.get
		// amazonEC2Service.
		// amazonEC2Service.getInstanceStatus("");
	}
}
