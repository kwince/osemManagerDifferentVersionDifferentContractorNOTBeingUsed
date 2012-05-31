package com.osem.validation;

import java.lang.reflect.Field;

import org.osem.annotations.Id;

import com.osem.util.ReflectionUtil;

public class NoId implements Constraint {

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
