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

	/**
	 * Scan all instance field to get all ones with @Mock annotation and call verifyNoMoreInteractions on them.
	 * 
	 * @throws IllegalArgumentException reflexion exception when acceding fields
	 * @throws IllegalAccessException reflexion exception when acceding fields
	 */
	@After
	public final void verifyNoMoreInteractionsAllMocks() throws IllegalArgumentException, IllegalAccessException {

		// Get all class from the test class to GenericMockitoTest class
		Class<? extends GenericMockitoTest> currentClass = this.getClass();
		while (null != currentClass) {

			// Scan all fields of the current class
			for (final Field field : currentClass.getDeclaredFields()) {

				// Check if the current field has the @Mock annotation
				final Annotation annotation = field.getAnnotation(Mock.class);
				if (null != annotation) {

					// Get the mock object to check no more or no interactions
					final Object mockObject;
					if (field.isAccessible()) {
						mockObject = field.get(this);
					}
					// In the case of a unaccessible field (private, protected or package), set it accessible temporary
					else {
						field.setAccessible(true);
						mockObject = field.get(this);
						field.setAccessible(false);
					}

					Mockito.verifyNoMoreInteractions(mockObject);
				}
			}

			// Get the super class of the current class
			// An unchecked cast is needed because it's not possible to check generic type at runtime
			@SuppressWarnings("unchecked")
			final Class<? extends GenericMockitoTest> uncheckedCurrentClass = (Class<? extends GenericMockitoTest>) currentClass.getSuperclass();
			currentClass = uncheckedCurrentClass;
		}
	}
}
