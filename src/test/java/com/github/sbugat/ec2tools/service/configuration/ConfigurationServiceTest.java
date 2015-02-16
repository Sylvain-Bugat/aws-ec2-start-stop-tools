package com.github.sbugat.ec2tools.service.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.sbugat.GenericMockitoTest;

/**
 * Configuration service test.
 * 
 * @author Sylvain Bugat
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest extends GenericMockitoTest {

	@Mock
	private HierarchicalINIConfiguration hierarchicalINIConfiguration;

	@InjectMocks
	private ConfigurationService configurationService;

	@Test
	public void testLoadConfigurationEmpty() throws ConfigurationException {

		configurationService.loadConfiguration();

		final InOrder inOrder = Mockito.inOrder(hierarchicalINIConfiguration);

		inOrder.verify(hierarchicalINIConfiguration).clear();
		inOrder.verify(hierarchicalINIConfiguration).load(ConfigurationService.CONFIGURATION_FILE_NAME);
		inOrder.verify(hierarchicalINIConfiguration).getSections();
	}
}
