package com.github.sbugat.ec2tools.service.options;

import java.util.ArrayList;

import nl.jqno.equalsverifier.EqualsVerifier;

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
	public void testEqualsAndHashCode() {

		EqualsVerifier.forClass(ProgramOptions.class).verify();
	}
}
