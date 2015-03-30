package com.github.sbugat.ec2tools.service.options;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProgramOptionsServiceTest {

	@InjectMocks
	private ProgramOptionsService programOptionsService;

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsUnknowOption() {

		programOptionsService.processProgramArgs(Arrays.array("-u"));
	}

	@Test(expected = NullPointerException.class)
	public void testProcessProgramArgsNullArgs() {

		programOptionsService.processProgramArgs(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsNoArgs() {

		programOptionsService.processProgramArgs(new String[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsAllExclusiveOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-lepc"));
	}
}
