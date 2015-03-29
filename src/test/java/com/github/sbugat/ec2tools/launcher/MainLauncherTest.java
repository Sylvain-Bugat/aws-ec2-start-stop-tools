package com.github.sbugat.ec2tools.launcher;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 * Main launcher test.
 * 
 * @author Sylvain Bugat
 */
public class MainLauncherTest {

	@Test(expected = NoSuchMethodException.class)
	public void testNoMainMethodLauncher() throws Exception {
		MainLauncher.launcher(this.getClass(), null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullClassLauncher() throws Exception {
		MainLauncher.launcher(null, null);
	}

	@Test
	public void testLauncher() throws Exception {
		MainLauncher.launcher(MockLauncher.class, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionLauncher() throws Exception {
		MainLauncher.launcher(IllegalArgumentLauncher.class, null);
	}

	@Test(expected = IllegalAccessException.class)
	public void testIllegalAccessExceptionLauncher() throws Exception {
		MainLauncher.launcher(IllegalAccessLauncher.class, null);
	}

	@Test(expected = InvocationTargetException.class)
	public void testInvocationTargetExceptionLauncher() throws Exception {
		MockLauncher.setNextExceptionClass(InvocationTargetException.class);
		MainLauncher.launcher(MockLauncher.class, null);
	}

	@Test(expected = InvocationTargetException.class)
	public void testIOExceptionLauncher() throws Exception {
		MockLauncher.setNextExceptionClass(IOException.class);
		MainLauncher.launcher(MockLauncher.class, null);
	}

	@Test(expected = InvocationTargetException.class)
	public void testLauncherConstructor() throws Exception {
		final Constructor<MainLauncher> mainLauncherConstructor = MainLauncher.class.getDeclaredConstructor();
		mainLauncherConstructor.setAccessible(true);
		mainLauncherConstructor.newInstance();
	}
}
