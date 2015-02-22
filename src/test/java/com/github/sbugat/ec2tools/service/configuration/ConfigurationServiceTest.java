package com.github.sbugat.ec2tools.service.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.sbugat.GenericMockitoTest;
import com.github.sbugat.ec2tools.InstanceOrder;
import com.github.sbugat.ec2tools.OrderType;

/**
 * Configuration service test.
 * 
 * @author Sylvain Bugat
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest extends GenericMockitoTest {

	private static final String SECTION = "section";
	private static final String INSTANCE_ID = "instance id";

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

	@Test
	public void testLoadConfigurationException() throws ConfigurationException {

		Mockito.doThrow(new ConfigurationException()).when(hierarchicalINIConfiguration).load(ConfigurationService.CONFIGURATION_FILE_NAME);

		try {
			configurationService.loadConfiguration();
			Assert.fail();
		}
		catch (final ConfigurationException e) {
			// Expected exeption

			final InOrder inOrder = Mockito.inOrder(hierarchicalINIConfiguration);

			inOrder.verify(hierarchicalINIConfiguration).clear();
			inOrder.verify(hierarchicalINIConfiguration).load(ConfigurationService.CONFIGURATION_FILE_NAME);
		}
	}

	@Test
	public void testLoadConfiguration() throws ConfigurationException {

		Mockito.doReturn(Sets.newLinkedHashSet(SECTION)).when(hierarchicalINIConfiguration).getSections();

		final SubnodeConfiguration subnodeConfiguration = new SubnodeConfiguration(new HierarchicalConfiguration(), new HierarchicalConfiguration.Node());
		subnodeConfiguration.setProperty(INSTANCE_ID, OrderType.START.toString());
		Mockito.doReturn(subnodeConfiguration).when(hierarchicalINIConfiguration).getSection(SECTION);

		configurationService.loadConfiguration();

		final InOrder inOrder = Mockito.inOrder(hierarchicalINIConfiguration);

		inOrder.verify(hierarchicalINIConfiguration).clear();
		inOrder.verify(hierarchicalINIConfiguration).load(ConfigurationService.CONFIGURATION_FILE_NAME);
		inOrder.verify(hierarchicalINIConfiguration).getSections();
		inOrder.verify(hierarchicalINIConfiguration).getSection(SECTION);

		Assertions.assertThat(configurationService.getConfiguredSections()).containsOnly(MapEntry.entry(SECTION, Lists.newArrayList(new InstanceOrder(INSTANCE_ID, OrderType.START.toString()))));
	}
}
