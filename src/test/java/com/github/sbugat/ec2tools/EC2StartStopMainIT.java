package com.github.sbugat.ec2tools;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class EC2StartStopMainIT {

	@Test
	public void testEC2StartStopMain() throws Exception {

		EC2StartStopMain.main(new String[] { "-l" });
	}

	@Test(expected = InvocationTargetException.class)
	public void testEC2StartStopMainException() throws Exception {

		EC2StartStopMain.main(new String[0]);
	}
}