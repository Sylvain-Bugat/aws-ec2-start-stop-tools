package com.github.sbugat.ec2tools.service.options;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Test;

public class ProgramOptionsTest {

	private final String SECTION_1 = "section 1";
	private final String SECTION_2 = "section 2";

	@Test
	public void testProgramOptionsGetterTrue() {

		final ProgramOptions programOptions = new ProgramOptions(true, true, true, true, new ArrayList<String>());
		Assertions.assertThat(programOptions.hasCheckOption()).isTrue();
		Assertions.assertThat(programOptions.hasPostCheckOption()).isTrue();
		Assertions.assertThat(programOptions.hasListOption()).isTrue();
		Assertions.assertThat(programOptions.hasExecutionOption()).isTrue();

		Assertions.assertThat(programOptions.hasNoSectionOption()).isTrue();
		Assertions.assertThat(programOptions.getSectionOptions()).isEmpty();
	}

	@Test
	public void testProgramOptionsGetterFalse() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions.hasCheckOption()).isFalse();
		Assertions.assertThat(programOptions.hasPostCheckOption()).isFalse();
		Assertions.assertThat(programOptions.hasListOption()).isFalse();
		Assertions.assertThat(programOptions.hasExecutionOption()).isFalse();

		Assertions.assertThat(programOptions.hasNoSectionOption()).isFalse();
		Assertions.assertThat(programOptions.getSectionOptions()).containsOnly(SECTION_1, SECTION_2);
	}

	@Test
	public void testProgramOptionsEqualsSameObject() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, new ArrayList<String>());
		Assertions.assertThat(programOptions).isEqualTo(programOptions);
	}

	@Test
	public void testProgramOptionsEquals() {

		final ProgramOptions programOptions = new ProgramOptions(false, true, false, true, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isEqualTo(new ProgramOptions(false, true, false, true, Lists.newArrayList(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProgramOptionsEqualsNull() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, new ArrayList<String>());
		Assertions.assertThat(programOptions).isNotEqualTo(null);
	}

	@Test
	public void testProgramOptionsEqualsDifferentType() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, new ArrayList<String>());
		Assertions.assertThat(programOptions).isNotEqualTo(SECTION_1);
	}

	@Test
	public void testProgramOptionsEqualsDifferentExecutionFlag() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isNotEqualTo(new ProgramOptions(true, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProgramOptionsEqualsDifferentListFlag() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isNotEqualTo(new ProgramOptions(false, true, false, false, Lists.newArrayList(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProgramOptionsEqualsDifferentCheckFlag() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isNotEqualTo(new ProgramOptions(false, false, true, false, Lists.newArrayList(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProgramOptionsEqualsDifferentPostcheckFlag() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isNotEqualTo(new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1, SECTION_2)));
	}

	@Test
	public void testProgramOptionsEqualsDifferentSections() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isNotEqualTo(new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_1)));
	}

	@Test
	public void testProgramOptionsEqualsDifferentSectionsOrder() {

		final ProgramOptions programOptions = new ProgramOptions(false, false, false, false, Lists.newArrayList(SECTION_1, SECTION_2));
		Assertions.assertThat(programOptions).isNotEqualTo(new ProgramOptions(false, false, false, true, Lists.newArrayList(SECTION_2, SECTION_1)));
	}
}
