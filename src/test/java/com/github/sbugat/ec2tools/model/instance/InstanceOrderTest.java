package com.github.sbugat.ec2tools.model.instance;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

/**
 * Main launcher test.
 * 
 * @author Sylvain Bugat
 */
public class InstanceOrderTest {

	@Test
	public void testEqualsAndHashCode() {

		EqualsVerifier.forClass(InstanceOrder.class).verify();
	}
}
