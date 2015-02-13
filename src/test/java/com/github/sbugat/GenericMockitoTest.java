package com.github.sbugat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.junit.After;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Generic test class to automatic check of all Mock marked with @Mock annotation by using verifyNoMoreInteractions on them.
 * 
 * @author Sylvain Bugat
 * 
 */
public abstract class GenericMockitoTest {

	@After
	public void verifyNoMoreInteractionsAllMocks() throws IllegalArgumentException, IllegalAccessException {

		Class<? extends GenericMockitoTest> currentClass = this.getClass();
		while (null != currentClass) {

			for (final Field field : currentClass.getDeclaredFields()) {

				final Annotation annotation = field.getAnnotation(Mock.class);

				if (null != annotation) {

					if (field.isAccessible()) {
						Mockito.verifyNoMoreInteractions(field.get(this));
					} else {
						field.setAccessible(true);
						Mockito.verifyNoMoreInteractions(field.get(this));
						field.setAccessible(false);
					}
				}
			}

			@SuppressWarnings("unchecked")
			final Class<? extends GenericMockitoTest> uncheckedCurrentClass = (Class<? extends GenericMockitoTest>) currentClass.getSuperclass();
			currentClass = uncheckedCurrentClass;
		}
	}
}
