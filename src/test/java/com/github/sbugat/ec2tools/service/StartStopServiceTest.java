package com.github.sbugat.ec2tools.service;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.github.sbugat.GenericMockitoTest;
import com.github.sbugat.ec2tools.model.instance.InstanceOrder;
import com.github.sbugat.ec2tools.model.instance.OrderType;
import com.github.sbugat.ec2tools.service.aws.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.configuration.ConfigurationService;
import com.github.sbugat.ec2tools.service.options.ProgramOptions;

/**
 * Start stop core service test.
 * 
 * @author Sylvain Bugat
 */
@RunWith(MockitoJUnitRunner.class)
public class StartStopServiceTest extends GenericMockitoTest {

	private static final String SECTION_1 = "Section 1";
	private static final String SECTION_2 = "Section 2";

	private static final String INSTANCE_ID_1 = "Instance id 1";

	@Mock
	private AmazonEC2Service amazonEC2Service;

	@Mock
	private ConfigurationService configurationService;

	@InjectMocks
	private StartStopService startStopService;

	@Test
	public void testProcessAllSectionsEmpty() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, new ArrayList<String>());

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();
	}

	@Test
	public void testProcessAllSectionsUnknownSection() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(null).when(configurationService).getConfiguredSections(SECTION_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
	}

	@Test
	public void testProcessAllSectionsEmptySection() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
	}

	@Test
	public void testProcessAllSectionsStartInstance() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).startInstance(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsStartInstanceException() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doThrow(new AmazonClientException("")).when(amazonEC2Service).startInstance(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).startInstance(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsStopInstance() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).stopInstance(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsStopInstanceException() {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doThrow(new AmazonClientException("")).when(amazonEC2Service).stopInstance(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).stopInstance(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsCheckStartOrderStoppedInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Stopped).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsCheckStartOrderRunningInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Running).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsCheckStartOrderException() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doThrow(new AmazonClientException("")).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsCheckStopOrderRunningInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Running).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsCheckStopOrderStoppedInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Stopped).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsCheckStopOrderException() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doThrow(new AmazonClientException("")).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsPostCheckStartOrderRunningInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Running).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsPostCheckStartOrderStoppedInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Stopped).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsPostCheckStartOrderException() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doThrow(new AmazonClientException("")).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsPostCheckStopOrderStoppedInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Stopped).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isFalse();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsPostCheckStopOrderRunningInstance() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doReturn(InstanceStateName.Running).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}

	@Test
	public void testProcessAllSectionsPostCheckStopOrderException() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(Lists.newArrayList(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()))).when(configurationService).getConfiguredSections(SECTION_1);
		Mockito.doThrow(new AmazonClientException("")).when(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);

		Assertions.assertThat(startStopService.processAllSections(programOptions)).isTrue();

		Mockito.verify(configurationService).getConfiguredSections(SECTION_1);
		Mockito.verify(amazonEC2Service).getInstanceStatus(INSTANCE_ID_1);
	}
}
