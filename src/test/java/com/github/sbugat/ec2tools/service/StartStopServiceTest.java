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

import com.github.sbugat.GenericMockitoTest;
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
}
