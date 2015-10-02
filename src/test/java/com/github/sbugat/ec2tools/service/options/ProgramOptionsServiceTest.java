package com.github.sbugat.ec2tools.service.options;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class ProgramOptionsServiceTest {

	private static final String SECTION_1 = "section1";
	private static final String SECTION_2 = "section2";

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

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsListExecuteOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-le"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsListPostcheckOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-lp"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsListCheckOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-lc"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsExecutePostcheckOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-ep"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsExecuteCheckOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-ec"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsPostcheckCheckOptions() {

		programOptionsService.processProgramArgs(Arrays.array("-pc"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsExecuteWithoutSection() {

		programOptionsService.processProgramArgs(Arrays.array("-e"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsPostcheckWithoutSection() {

		programOptionsService.processProgramArgs(Arrays.array("-p"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsCheckWithoutSection() {

		programOptionsService.processProgramArgs(Arrays.array("-c"));
	}

	@Test
	public void testProcessProgramArgsListOnly() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-l"))).isEqualTo(new ProgramOptions(false, true, false, false, new ArrayList<String>()));
	}

	@Test
	public void testProcessProgramArgsExecuteSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-e", "-s", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(true, false, false, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsCompactExecuteSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-es", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(true, false, false, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsListSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-l", "-s", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(false, true, false, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsCompactListSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-ls", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(false, true, false, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsCheckSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-c", "-s", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(false, false, true, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsCompactCheckSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-cs", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(false, false, true, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsPostcheckSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-p", "-s", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(false, false, false, true, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProcessProgramArgsCompactPostcheckSections() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-ps", SECTION_1, "-s", SECTION_2))).isEqualTo(new ProgramOptions(false, false, false, true, ImmutableList.of(SECTION_1, SECTION_2)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessProgramArgsNonOptionArgument() {

		Assertions.assertThat(programOptionsService.processProgramArgs(Arrays.array("-es", SECTION_1, SECTION_2))).isEqualTo(new ProgramOptions(true, false, false, false, ImmutableList.of(SECTION_1, SECTION_2)));
	}
}
