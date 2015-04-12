package com.github.sbugat.ec2tools.model.instance;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

import com.github.sbugat.ec2tools.service.options.ProgramOptions;

/**
 * Main launcher test.
 * 
 * @author Sylvain Bugat
 */
public class InstanceOrderTest {

	@Test
	public void testEqualsAndHashCode() {

		EqualsVerifier.forClass(ProgramOptions.class).verify();
	}
}
