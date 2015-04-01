package com.github.sbugat.ec2tools.model.instance;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Main launcher test.
 * 
 * @author Sylvain Bugat
 */
public class InstanceOrderTest {

	private static final String INSTANCE_ID_1 = "instance 1";
	private static final String INSTANCE_ID_2 = "instance 2";

	@Test
	public void testInstanceOrderEqualsNull() throws Exception {
		final InstanceOrder instanceOrder = new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString());
		Assertions.assertThat(instanceOrder).isNotEqualTo(null);
	}

	@Test
	public void testInstanceOrderEqualsDifferentType() throws Exception {
		final InstanceOrder instanceOrder = new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString());
		Assertions.assertThat(instanceOrder).isNotEqualTo(INSTANCE_ID_1);
	}

	@Test
	public void testInstanceOrderEqualsDifferentInstance() throws Exception {
		final InstanceOrder instanceOrder = new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString());
		Assertions.assertThat(instanceOrder).isNotEqualTo(new InstanceOrder(INSTANCE_ID_2, OrderType.START.toString()));
	}

	@Test
	public void testInstanceOrderEqualsDifferentOrdertype() throws Exception {
		final InstanceOrder instanceOrder = new InstanceOrder(INSTANCE_ID_1, OrderType.START.toString());
		Assertions.assertThat(instanceOrder).isNotEqualTo(new InstanceOrder(INSTANCE_ID_1, OrderType.STOP.toString()));
	}
}
