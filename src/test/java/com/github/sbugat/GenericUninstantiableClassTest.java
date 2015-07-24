package com.github.sbugat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public abstract class GenericUninstantiableClassTest {

	@Test(expected = InvocationTargetException.class)
	public void testUninstantiableClassConstructor() throws Exception {
		// System.out.println(this.getClass().getName().replace("Test$", "").replace("IT$", ""));

		final String targetClassName = getClass().getName().replaceFirst("Test$", "").replaceFirst("IT$", "");
		@SuppressWarnings("rawtypes")
		final Class targetClass = Class.forName(targetClassName);

		System.out.println(targetClass.getName());
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final Constructor uninstantiableClassConstructor = targetClass.getDeclaredConstructor();
		uninstantiableClassConstructor.setAccessible(true);
		uninstantiableClassConstructor.newInstance();
	}
}
