package org.kwince.contribs.osem.validation;

import java.lang.reflect.Field;

import org.kwince.contribs.osem.annotations.Id;

import org.kwince.contribs.osem.util.ReflectionUtil;


public class MultipleId implements Constraint {

	@Override
	public void check(Class<?> clazz) {
		Field[] fields = ReflectionUtil.getAnnotatedFileds(clazz, Id.class);
		if (fields.length==0) {
			try {
				throw new Exception("No Id");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
