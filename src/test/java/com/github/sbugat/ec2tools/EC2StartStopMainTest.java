package com.github.sbugat.ec2tools;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.sbugat.ec2tools.launcher.MainLauncher;
import com.github.sbugat.ec2tools.service.MainService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MainLauncher.class)
public class EC2StartStopMainTest {

	@Before
	public void setUp() {
		PowerMockito.mockStatic(MainLauncher.class);
	}

	@After
	public void cleanUp() throws Exception {
		PowerMockito.verifyStatic();
		MainLauncher.launcher(MainService.class, new String[0]);
	}

	@Test
	public void testEC2StartStopMain() throws Exception {

		EC2StartStopMain.main(new String[0]);
	}

	@Test(expected = IOException.class)
	public void testEC2StartStopMainException() throws Exception {

		PowerMockito.doThrow(new IOException()).when(MainLauncher.class);
		MainLauncher.launcher(Mockito.any(Class.class), Mockito.any(String[].class));
		EC2StartStopMain.main(new String[0]);
	}
}