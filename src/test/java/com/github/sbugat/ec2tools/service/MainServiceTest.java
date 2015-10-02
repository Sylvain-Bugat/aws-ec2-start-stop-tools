package com.github.sbugat.ec2tools.service;

import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.AmazonClientException;
import com.github.sbugat.GenericMockitoTest;
import com.github.sbugat.ec2tools.service.aws.AmazonEC2Service;
import com.github.sbugat.ec2tools.service.configuration.ConfigurationService;
import com.github.sbugat.ec2tools.service.options.ProgramOptions;
import com.github.sbugat.ec2tools.service.options.ProgramOptionsService;

/**
 * Main service test.
 * 
 * @author Sylvain Bugat
 */
@RunWith(MockitoJUnitRunner.class)
public class MainServiceTest extends GenericMockitoTest {

	private static final String SECTION_1 = "Section 1";

	@Mock
	private AmazonEC2Service amazonEC2Service;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private ProgramOptionsService programOptionsService;

	@Mock
	private StartStopService startStopService;

	@InjectMocks
	private MainService mainService;

	@Test(expected = IllegalArgumentException.class)
	public void testMainProcessProgramArgsIllegalArgumentException() throws Exception {

		Mockito.doThrow(new IllegalArgumentException()).when(programOptionsService).processProgramArgs(Mockito.any(String[].class));
		final String[] arguments = {};
		try {
			mainService.main(arguments);
		}
		catch (final Exception e) {
			Mockito.verify(programOptionsService).processProgramArgs(arguments);
			throw e;
		}
	}

	@Test(expected = ConfigurationException.class)
	public void testMainLoadConfigurationConfigurationException() throws Exception {

		Mockito.doThrow(new ConfigurationException()).when(configurationService).loadConfiguration();
		final String[] arguments = {};
		try {
			mainService.main(arguments);
		}
		catch (final Exception e) {
			Mockito.verify(programOptionsService).processProgramArgs(arguments);
			Mockito.verify(configurationService).loadConfiguration();
			throw e;
		}
	}

	@Test
	public void testMainPrintAllSections() throws Exception {

		Mockito.doReturn(new ProgramOptions(false, true, false, false, new ArrayList<String>())).when(programOptionsService).processProgramArgs(Mockito.any(String[].class));
		final String[] arguments = {};

		mainService.main(arguments);

		Mockito.verify(programOptionsService).processProgramArgs(arguments);
		Mockito.verify(configurationService).loadConfiguration();
	}

	@Test
	public void testMainPrintOneSections() throws Exception {

		Mockito.doReturn(new ProgramOptions(false, true, false, false, Lists.newArrayList(SECTION_1))).when(programOptionsService).processProgramArgs(Mockito.any(String[].class));
		final String[] arguments = {};

		mainService.main(arguments);

		Mockito.verify(programOptionsService).processProgramArgs(arguments);
		Mockito.verify(configurationService).loadConfiguration();
		Mockito.verify(configurationService).toString(Lists.newArrayList(SECTION_1));
	}

	@Test(expected = AmazonClientException.class)
	public void testMainInitializeAmazonClientException() throws Exception {

		Mockito.doReturn(new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1))).when(programOptionsService).processProgramArgs(Mockito.any(String[].class));
		Mockito.doThrow(new AmazonClientException(SECTION_1)).when(amazonEC2Service).initialize();
		final String[] arguments = {};
		try {
			mainService.main(arguments);
		}
		catch (final Exception e) {
			Mockito.verify(programOptionsService).processProgramArgs(arguments);
			Mockito.verify(configurationService).loadConfiguration();
			Mockito.verify(amazonEC2Service).initialize();
			throw e;
		}
	}

	@Test
	public void testMain() throws Exception {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(programOptions).when(programOptionsService).processProgramArgs(Mockito.any(String[].class));
		Mockito.doReturn(false).when(startStopService).processAllSections(programOptions);
		final String[] arguments = {};

		mainService.main(arguments);

		Mockito.verify(programOptionsService).processProgramArgs(arguments);
		Mockito.verify(configurationService).loadConfiguration();
		Mockito.verify(amazonEC2Service).initialize();
		Mockito.verify(startStopService).processAllSections(programOptions);
	}

	@Test(expected = RuntimeException.class)
	public void testMainProcessError() throws Exception {

		final ProgramOptions programOptions = new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1));
		Mockito.doReturn(programOptions).when(programOptionsService).processProgramArgs(Mockito.any(String[].class));
		Mockito.doReturn(true).when(startStopService).processAllSections(programOptions);
		final String[] arguments = {};
		try {
			mainService.main(arguments);
		}
		catch (final Exception e) {
			Mockito.verify(programOptionsService).processProgramArgs(arguments);
			Mockito.verify(configurationService).loadConfiguration();
			Mockito.verify(amazonEC2Service).initialize();
			Mockito.verify(startStopService).processAllSections(programOptions);
			throw e;
		}
	}

}
